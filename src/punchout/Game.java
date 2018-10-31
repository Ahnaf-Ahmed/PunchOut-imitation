package punchout;

// imports things i need
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

// creates the main game class
public class Game extends Canvas {

	private BufferStrategy strategy; // take advantage of accelerated graphics
	private boolean waitingForKeyPress = true; // true if game held up until
												// a key is pressed

	// whether the game is in the main menu or in game
	private boolean inMenu = true;
	private int currentDifficulty = 1;
	private int menuSelectedOption = 0;
	private boolean isShowingControls = false;

	// the sprite for the menu screen
	private Sprite menuScreen;
	private Sprite controlScreen;

	// the dimensions of the screen
	private int W;
	private int H;

	// whether or not the user is pressed shift
	private boolean shiftPressed = false;

	// whether the game is running
	private boolean gameRunning = true;

	// the background
	private Sprite background;

	// the glove for the menu
	private Sprite glove;

	// whether or not the player has won
	private boolean hasWon = false;

	// the time the fight ended
	private long fightEndTime = 0;

	// the time to show the player celebrating
	private int fightEndDuration = 3000;

	// whether the fight has ended
	private boolean fightEnded = false;

	// the instance of the player class
	private Player player;

	// the instance of the enemy class
	private Enemy enemy;

	// the referee
	private Referee referee;

	// the menu music
	private Clip menuMusic;

	// the in-game music
	private Clip gameMusic;

	// the winning scene music
	private Clip winMusic;
	
	// the punch sound
	private Clip punchSound;

	/*
	 * Construct our game and set it running.
	 */
	public Game() {

		// create a frame to contain game
		JFrame container = new JFrame("Punch-Out");

		// get hold the content of the frame
		JPanel panel = (JPanel) container.getContentPane();

		// set up the resolution of the game
		W = 800;
		H = 600;

		// sets the dimensions
		panel.setPreferredSize(new Dimension(W, H));
		panel.setLayout(null);

		// set up canvas size (this) and add to frame
		setBounds(0, 0, 800, 600);
		panel.add(this);

		// Tell AWT not to bother repainting canvas since that will
		// be done using graphics acceleration
		setIgnoreRepaint(true);

		// make the window visible
		container.pack();
		container.setResizable(false);
		container.setVisible(true);

		// if user closes window, shutdown game and jre
		container.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			} // windowClosing
		});

		// add key listener to this canvas
		addKeyListener(new KeyInputHandler());

		// request focus so key events are handled by this canvas
		requestFocus();

		// gets the background
		background = (SpriteStore.get()).getSprite("images/background.png");
		controlScreen = (SpriteStore.get()).getSprite("images/controls.png");

		// gets the glove
		glove = (SpriteStore.get()).getSprite("images/glove.png");
		glove.setHeight(40);

		// gets the menu
		menuScreen = (SpriteStore.get()).getSprite("images/menu.png");

		// create buffer strategy to take advantage of accelerated graphics
		createBufferStrategy(2);
		strategy = getBufferStrategy();

		// loads music
		menuMusic = (MusicManager.get()).loadSound("music/menu.wav");
		gameMusic = (MusicManager.get()).loadSound("music/game.wav");
		winMusic = (MusicManager.get()).loadSound("music/win.wav");
		punchSound = (MusicManager.get()).loadSound("music/punch.wav");

		// plays music
		(MusicManager.get()).playMusic(menuMusic);

		// initialize entities
		initEntities();

		// start the game
		gameLoop();

	} // constructor

	/*
	 * initEntities input: none output: none purpose: Initialize the starting
	 * state of the player and enemy.
	 */
	private void initEntities() {

		// creates a new instance of the player class
		player = new Player(this);

		// creates a new instance of the enemy class
		enemy = new Enemy(this, currentDifficulty);

		// creates a new instance of the referee class
		referee = new Referee(this);

	} // initEntities

	/*
	 * callTKO Makes the ref walk out onto the screen and tell the player they
	 * have won
	 */
	public void callTKO() {
		// makes the referee call a Total Knock-Out
		referee.onWin();

	} // calTKO

	/*
	 * onWin Input: none
	 * 
	 * Purpose: shows the winning screen
	 */
	public void onWin() {

		// makes the player win
		player.win();

		// records that the fight has ended and the player has won
		hasWon = true;
		fightEnded = true;
		fightEndTime = System.currentTimeMillis();

	} // onWin

	/*
	 * onLoss Input: None
	 * 
	 * Purpose: shows the losing screen
	 */
	public void onLoss() {

		// plays the enemy win animation
		enemy.win();

		// makes the ref call the match for the player
		referee.onLoss();

		// records that the match is over and the player lost
		hasWon = false;
		fightEnded = true;
		fightEndTime = System.currentTimeMillis();

	} // onLoss

	/*
	 * startFightAnimation Input: None
	 * 
	 * Purpose: starts the fight animation
	 */
	public void startFightAnimation() {

		// makes the ref start the fight
		referee.startFight();

	}// startFightAnimation

	/*
	 * startFight Input: none,
	 * 
	 * Purpose: starts the actual fight
	 */
	public void startFight() {

		// makes the player able to fight
		player.startFight();

		// makes the enemy able to fight
		enemy.startFight();

	} // startFight

	/*
	 * enemyTryToGetUp Input: None,
	 * 
	 * Purpose: each time the ref counts, the enemy has a chance to get up
	 * through this method
	 */
	public void enemyTryToGetUp() {

		// tells the enemy to try and get up
		enemy.tryToGetUp();

	} // enemyTryToGetUp
	/*
	 * gameLoop input: none output: none purpose: Main game loop. Runs
	 * throughout game play. Responsible for the following activities: -
	 * calculates speed of the game loop to update moves - moves the game
	 * entities - draws the screen contents (entities, text) - updates game
	 * events - checks input
	 */

	public void gameLoop() {

		// get the last loop time
		long lastLoopTime = System.currentTimeMillis();

		// keep loop running until game ends
		while (gameRunning) {

			// gets delta time
			long thisTime = System.currentTimeMillis();
			long delta = thisTime - lastLoopTime;
			lastLoopTime = thisTime;

			// get graphics context for the accelerated surface
			Graphics2D g = (Graphics2D) strategy.getDrawGraphics();

			// checks if it is in the menu
			if (inMenu) {

				// checks if it is showing the controls
				if (isShowingControls) {

					// draws the control screen
					controlScreen.draw(g, 0, 0, W, H);

					// checks if it is showing the win screen
				} else { // else if

					// draws the menu
					menuScreen.draw(g, 0, 0, W, H);

					// sets the font
					g.setColor(Color.white);
					Font font = new Font("Arial", Font.CENTER_BASELINE, 20);
					g.setFont(font);

					// draws start
					g.drawString("START", (800 - g.getFontMetrics().stringWidth("START")) / 2, 400);

					// draws controls
					g.drawString("CONTROLS", (800 - g.getFontMetrics().stringWidth("CONTROLS")) / 2, 440);

					// draws the two gloves on either side of the selected
					// option
					glove.draw(g, (W - 400) / 2, 400 + (40 * menuSelectedOption) - (glove.getW() / 2), glove.getW(),
							glove.getH());
					glove.draw(g, (W + 400) / 2, 400 + (40 * menuSelectedOption) - (glove.getW() / 2), -glove.getW(),
							glove.getH());

				} // else

			} else { 

				// draws the backgroundW
				background.draw(g, 0, 0, W, H);

				// runs the enemy's code
				enemy.update();
				enemy.render(g);
				g.setColor(Color.RED);
				g.fillRect(450, 40, ((int) (((double) enemy.getHealth() / enemy.getMaxHealth()) * 150)), 16);
				// reminder to future Ahnaf: positions above found by trial and error

				// runs the player's code
				player.update();
				player.render(g);
				g.setColor(Color.GREEN);
				g.fillRect(275, 40, ((int) (((double) player.getHealth() / 100) * 150)), 16);// position found by trial and error

				// runs the ref's code
				referee.update();
				referee.render(g);

				// checks if the fight has ended
				if (fightEnded) {

					// checks if the player is done celebrating
					if (System.currentTimeMillis() - fightEndTime > fightEndDuration) {

						// checks if the player won the match
						if (hasWon) {

							// checks if it was the final enemy
							if (currentDifficulty == 3) {

								// sets to menu
								inMenu = true;

								// resets difficulty
								currentDifficulty = 1;

							} else {

								// increases the difficulty
								currentDifficulty++;

								// starts the next match
								startGame();

							} // else

						} else { // if

							// sets the difficulty back to one
							currentDifficulty = 1;

							// returns to menu
							inMenu = true;

						} // else
					} // if
				} // if
			} // if

			// clear graphics and flip buffer
			g.dispose();
			strategy.show();

			// pause
			try {
				Thread.sleep(10);
			} catch (Exception e) {

			} // catch
		} // while
	} // gameLoop

	/*
	 * startCount input: None Purpose: Tells the ref to start counting when the
	 * enemy is down
	 */
	public void startCount() {

		// tells the ref to start counting
		referee.startCount();

	} // startCount

	/*
	 * stopCount input: None Purpose: Tells the ref to stop counting
	 */
	public void stopCount() {

		// tells the ref
		referee.stopCount();

	} // stopCount

	/*
	 * onPlayerPunch input: isUpper: Whether the punch is an uppercut isRight:
	 * Whether the punch is right or left handed output: None Purpose: tells the
	 * enemy to take damage
	 */
	public void onPlayerPunch(boolean isUpper, boolean isRight) {

		(MusicManager.getClip()).playOnce(punchSound);
		// does more damage if the punch is uppercut
		if (isUpper) {

			enemy.takePunch(10, isUpper, isRight);

		} else { // if

			enemy.takePunch(5, isUpper, isRight);

		} // else

	} // onPlayerPunch

	/*
	 * onEnemyPunch Input: isRight: Whether the punch is right or left damage:
	 * The damage done by the punch Output: None Purpose: Tells the player to
	 * take damage
	 */
	public void onEnemyPunch(boolean isRight, int damage) {

		// tells the player to take damage
		player.takePunch(isRight, damage);

	}// onEnemyPunch

	/*
	 * getPlayerIsDown Input: None Output: Boolean - whether the player is down
	 * or not Purpose: Used by the enemy so that he stops punching when the
	 * player has fallen
	 */

	public boolean getPlayerIsDown() {

		// calls the player's function
		return player.getIsDown();

	} // getPlayerIsDown

	/*
	 * startGame input: none output: none purpose: start a fresh game, clear old
	 * data
	 */
	private void startGame() {

		// initializes the entities
		initEntities();

		// sets to out of menu
		inMenu = false;
		isShowingControls = false;

		// resets so that the player has not won yet and the fight is not over
		hasWon = false;
		fightEnded = false;

		// sets up music
		(MusicManager.get()).stopMusic();
		(MusicManager.get()).playMusic(gameMusic);

	} // startGame

	/*
	 * inner class KeyInputHandler handles keyboard input from the user
	 */

	private class KeyInputHandler extends KeyAdapter {

		/*
		 * The following methods are required for any class that extends the
		 * abstract class KeyAdapter. They handle keyPressed, keyReleased and
		 * keyTyped events.
		 */
		public void keyPressed(KeyEvent e) {

			// checks if it is not in the menu
			if (!inMenu) {

				// records whether shift is pressed
				if (e.getKeyCode() == KeyEvent.VK_SHIFT) {

					// player uppercuts when shift is pressed
					shiftPressed = true;

				} // if

				// respond to punch left
				if (e.getKeyCode() == KeyEvent.VK_LEFT) {

					// checks whether to punch or uppercut
					if (!shiftPressed) {

						// punches left
						player.throwPunch(false, false);

					} else { // if

						// uppercuts left
						player.throwPunch(false, true);

					} // if
				} // if

				// respond to punch right
				if (e.getKeyCode() == KeyEvent.VK_RIGHT) {

					// checks whether to punch or uppercut
					if (!shiftPressed) {

						// punches right
						player.throwPunch(true, false);

					} else { // if

						// uppercuts right
						player.throwPunch(true, true);

					} // else

				} // if

				// checks for space for recovering
				if (e.getKeyCode() == KeyEvent.VK_SPACE) {

					// tells the player
					player.onSpace();
				} // if

				// checks for dodging
				if (e.getKeyCode() == KeyEvent.VK_A) {

					// dodges left
					player.dodge(false);
				} // if

				if (e.getKeyCode() == KeyEvent.VK_D) {

					// dodges right
					player.dodge(true);

				} // if

			} else { // if

				// checks if it is showing the controls
				if (isShowingControls) {

					// returns back to the menu
					if (e.getKeyCode() == KeyEvent.VK_SPACE) {

						isShowingControls = false;

					} // if

				} else { // if

					// changes the option up
					if (e.getKeyCode() == KeyEvent.VK_UP) {

						// makes sure the selected option is not below 0
						if (menuSelectedOption > 0) {
							menuSelectedOption--;

						} // if

					} // if

					// changes the option down
					if (e.getKeyCode() == KeyEvent.VK_DOWN) {

						// makes sure the option does not go above 1
						if (menuSelectedOption < 1) {
							menuSelectedOption++;

						} // if

					} // if

					// checks if an option is being selected
					if (e.getKeyCode() == KeyEvent.VK_SPACE) {

						// does different stuff depending on the currently
						// selected option
						switch (menuSelectedOption) {

						case 0:

							// starts the game
							startGame();
							break;

						case 1:

							// show the controls
							isShowingControls = true;

						} // switch
					} // if
				} // if
			} // if

		} // keyPressed

		public void keyReleased(KeyEvent e) {

			// checks if shift was released
			if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
				shiftPressed = false;

			} // if

		} // keyReleased

		public void keyTyped(KeyEvent e) {

			// if escape is pressed, end game
			if (e.getKeyChar() == 27) {
				System.exit(0);
			} // if escape pressed

		} // keyTyped

	} // class KeyInputHandler

	// get methods

	public int getW() {
		return W;

	} // geW

	public int getH() {
		return H;

	} // getH

	/**
	 * Main Program
	 */
	public static void main(String[] args) {

		// instantiate this object
		new Game();

	} // main

} // Game
