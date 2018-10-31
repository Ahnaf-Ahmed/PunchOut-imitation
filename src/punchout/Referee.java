package punchout;

import java.awt.Graphics;

/*
 * Referee
 * The referee entity who mostly is in for his animations
 */
public class Referee extends Entity {

	// if the ref is walking out
	private boolean isWalkingOut = false;

	// if the ref is walking in
	private boolean isWalkingIn = false;

	// the time the ref started walking out or in
	private long startWalkTime = 0;

	// the time it takes to walk out or in
	private int walkDuration = 1000;

	// the final X coordniate when walking out
	private int finalWalkX = 300;

	// whether or not the ref is counting
	private boolean isCounting = false;

	// the current number the ref counted when waiting for the enemy to get up
	private int currentCount = 0;

	// the last number the ref counted
	private int lastCount = 0;

	// whether or not the ref is calling a TKO
	private boolean isTKO = false;

	// whether or not the ref has told the Game the player has won
	private boolean hasSignalledWin = false;

	// whether or not the ref is starting the fight
	private boolean isStartingFight = false;

	// the time the ref says "Fight"
	private int startFightDuration = 600;

	// whether the ref is resuming the fight or not
	private boolean isResumingFight = false;

	// the time the ref resumed the fight
	private long resumeTime = 0;

	// whether or not the player lost
	private boolean playerLost = false;

	/*
	 * Constructor Input: g: The Game the ref exists in
	 * 
	 * Creates a ref entity for animations
	 */
	public Referee(Game g) {

		// initializes the Entity
		super(0, 0, 2, g);

		// sets x and y
		x = parent.getWidth();
		y = (int) (parent.getHeight() * 3 / 4.0);

		// sets imgX and imgY
		imgX = x;
		imgY = y;

		// adds 100 to imgX to push the ref offscreen
		imgX += 100;

		// laods the sprites
		String[] sprites = { "walkOne", "walkTwo", "stand", "count", "armUp", "fight" };

		for (int i = 0; i < sprites.length; i++) {
			super.addSprite("images/referee/" + sprites[i] + ".png");

		} // for

		// sets the currentSprite to standing
		currentSprite = 2;

	} // Constructor

	/*
	 * update runs through the refs animation and plays the current one
	 */
	public void update() {

		// gets the time since the ref started walking
		long since = (System.currentTimeMillis() - startWalkTime);

		// checks if it is walking out
		if (isWalkingOut) {

			// checks if it is done walking
			if (since > walkDuration) {

				// gets how far into the animation it is
				double percentage = (double) (since / (double) walkDuration);

				// sets sprite to stand as default
				currentSprite = 2;

				// checks if it is counting
				if (isCounting) {

					// sets the sprite to count every 400 milliseconds
					if (Math.round(since / 400) % 2 == 0) {

						currentSprite = 3;
					} // if

					// gets the number the ref has counted to
					currentCount = (int) ((Math.floor((since - walkDuration) + 400) / 800));

					// checks if the ref has reached a new number
					if (currentCount != lastCount) {

						// checks if the ref has finished counting
						if (currentCount == 10) {

							// stops counting
							isCounting = false;
							isTKO = true;
							hasSignalledWin = false;

						} else { // if

							// self explanitory
							parent.enemyTryToGetUp();

							// records the last counted number
							lastCount = currentCount;

						} // else

					} // if

					// if the enemy has lost
				} else if (isTKO) {

					// checks if it has already told the parent the player won
					if (!hasSignalledWin) {

						// tells the parent
						parent.onWin();
						hasSignalledWin = true;

					} // if

					// runs the animation, changing sprite every 200
					// milliseconds
					if (since % 400 < 200) {
						currentSprite = 4;

					} else { // if
						currentSprite = 2;
					} // else

					// checks if the ref is starting the fight
				} else if (isStartingFight) {

					// checks if it has started the fight yet
					if (since > walkDuration + startFightDuration) {

						// calls the fight
						parent.startFight();
						isStartingFight = false;

						// walks back
						startWalkIn();
					} // if

					// sets sprite to saying "Fight"
					currentSprite = 5;

					// checks if the ref is resuming the fight
				} else if (isResumingFight) {

					// sets sprite to saying "Fight"
					currentSprite = 5;

					// checks if it is done starting the fight
					if (System.currentTimeMillis() - resumeTime > 1200) {

						// walks back in
						startWalkIn();

					} // if

					// checks if the ref call the fight for the enemy
				} else if (playerLost) {

					// alternates between standing and raising his arm every 400
					// milliseconds
					if (Math.round(since / 400.0) % 2 == 0) {
						currentSprite = 3;

					} else { // if
						currentSprite = 4;

					} // else

				} // else if

			} else { // if

				// walks out
				// moves the image over depending on how far he has gone
				imgX = (int) (x - (since / (double) walkDuration) * finalWalkX);

				// changes sprite between walking one and two every 100
				// milliseconds
				if (Math.round(since / 100) % 2 == 0) {
					currentSprite = 0;

				} else { // if
					currentSprite = 1;

				} // else

			} // else

			// checks if it is walking back off the screen
		} else if (isWalkingIn) {

			// moves the image over
			imgX = (int) (x - finalWalkX + (since / (double) walkDuration) * finalWalkX);

			// checks if it is done walking
			if (since >= walkDuration) {

				isWalkingOut = false;

			} // if

			// alternates the sprite between walking one and two
			if (Math.round(since / 100) % 2 == 0) {
				currentSprite = 0;

			} else { // if
				currentSprite = 1;

			} // else

		} // else if

	} // update

	// startWalkOut
	// makes the ref start walking out
	public void startWalkOut() {

		// sts up to walk out
		reset();
		isWalkingOut = true;
		isWalkingIn = false;
		startWalkTime = System.currentTimeMillis();

	} // startWalkOut

	// startWalkIn
	// makes the ref start walking back in
	public void startWalkIn() {

		// sets up to walk back in
		reset();
		isWalkingIn = true;
		isWalkingOut = false;
		startWalkTime = System.currentTimeMillis();

	} // startWalkIn

	// startCount
	// makes the ref start counting
	public void startCount() {

		// makes the ref walk out
		startWalkOut();

		// sets up to start counting
		isCounting = true;
		currentCount = 0;

	} // startCount

	// stopCount
	// makes the ref stop counting when the enemy got back up
	public void stopCount() {

		// stops counting
		isCounting = false;

		// signals to say "Fight!"
		isResumingFight = true;
		resumeTime = System.currentTimeMillis();

	} // stopCount

	// startFight
	// makes the ref start the fight at the very beginning
	public void startFight() {

		// makes the ref walk out
		startWalkOut();

		// makes the ref stay "Fight" when he gets back out
		isStartingFight = true;

	} // startFight

	// onWin
	// makes the ref call the fight for the player
	public void onWin() {

		// makes the ref walk out
		startWalkOut();

		// makes the ref call the match for the player
		isTKO = true;

	} // onWin

	// onLoss
	// Makes the ref call the match for the enemy
	public void onLoss() {

		// makes the ref walk out
		startWalkOut();

		// makes the ref call the match for the enemy
		playerLost = true;

	} // onLoss

	// reset
	// resets most variables so that I don't have to reset them each time the
	// ref's state changes
	public void reset() {

		// sets ref back to default state
		isWalkingOut = false;
		isWalkingIn = false;
		playerLost = false;
		isTKO = false;
		isCounting = false;
		isResumingFight = false;

	} // reset

	// render
	// draws the sprite on the canvas
	public void render(Graphics g) {

		// gets the sprite
		Sprite sprite = (Sprite) sprites.get(currentSprite);

		// scales the dimensions with the image so that the image is not
		// distorted
		w = (int) (sprite.getWidth() * imgMulti);
		h = (int) (sprite.getHeight() * imgMulti);

		// the actual location to draw the image
		// because when mirrored, the x should move
		int actualImgX = imgX;

		// mirrors it if needed
		if (isMirrored) {

			w *= -1;

			// mvoes the image back over
			actualImgX = (x - imgX) + x;

		} // if

		// sprite is centered by the bottom and center
		// THIS IS THE MAJOR DIFFERENCE BETWEEN Referee.render AND Entity.render
		// The ref's image is drawn relative to the bottom right
		(sprite).draw(g, (int) (actualImgX - w), (int) (imgY - h), w, h);

	} // render
} // Referee
