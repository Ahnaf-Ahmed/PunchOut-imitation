package punchout;

/* 
 * Enemy.java
 * April 11, 2016
 * The main enemy class for the player to fight
 */

public class Enemy extends Boxer {

	// its health
	private int health;
	private int maxHealth;
	
	// whether it is blocking lower or higher
	// if it is blocking the same place the player attacks, it will take less damage
	private boolean isBlockingLower = true;
	
	// whether it is blocking
	private boolean isBlocking = false;
	
	// was it hit by an uppercut
	// needed to determine the correct animation to play
	private boolean wasHitByUppercut = false;
	
	
	// the last time the enemy threw a punch
	private long lastPunchTime = 0;
	
	// the time it takes to play the punch animation
	private long punchDuration = 1000;
	
	// whether the punch thrown is right or left
	private boolean punchIsRight = false;
	
	// whether is has already caused the player to take damage
	// used so that it does not hurt the player every frame
	private boolean hasDealtDamage = false;
	
	// whether the enemy is stunned
	protected boolean isStunned = true;
	
	// whether the enemy is throwing an uppercut or a normal punch
	private boolean isThrowingUppercut = false;
	
	// the time it takes to switch between frames on the idle animation
	private int animDuration = 200;
	
	// whether or not the enemy has called the ref
	private boolean hasCalledRef = false;
	
	// whether it is possible to stun the enemy at this moment
	private boolean isVunerableToStun = false;
	
	// the time the enemy was stunned
	private long stunTime = 0;
	
	// the time it takes for the enemy to recover from being stunned
	private int stunDuration = 2000;
	
	private int difficulty;
	// whether the enemy has told the Game class that is has 
	// finished its intro and that the fight can start
	private boolean hasSignalledFinishedIntro = false;
	
	private int flinchDuration = 260;

	/*
	 * Constructor
	 * Input: 
	 * 		g: the Game the enemy lives in
	 * 		difficulty: the difficulty level
	 * 			Should be between 1 and 3
	 */
	public Enemy(Game g, int difficulty) {
		
		// initializes the Boxer class
		super(2, g);
		
		// sets health to difficulty
		health = (100 + 50*(difficulty-1));
		maxHealth = health;
		
		this.difficulty = difficulty;
		
		punchDuration -= 200 * difficulty;
		flinchDuration -= 20 * difficulty;
		// records parent
		parent = g;
		
		// sets x and y
		x = parent.getWidth() / 2;
		y = parent.getHeight() * 3 / 4;
		
		// sets imgX and imgY to default
		// see Entity for imgX and imgY explanation
		imgX = x;
		imgY = y;

		// loads all of the needed sprites
		String[] sprites = {
			"blockingJabOne",
			"blockingJabTwo",
			"blockingUpperOne",
			"blockingUpperTwo",
			"blockJab",
			"blockUppercut",
			"hitJab",
			"hitUppercut",
			"jabOne",
			"upperOne",
			"upperTwo",
			"upperThree",
			"fall",
			"down",
			"stunned",
			"getUpOne",
			"getUpTwo",
			"introOne",
			"introTwo",
			"introThree"
		};
		
		// gets the difficulty as a string representation for the image directory
		// because higher difficulty enemies have different sprites
		String dir = "";
		switch (difficulty){
		
			case 1:
				dir = "one";
				break;
				
			case 2:
				dir = "two";
				break;
				
			case 3:
				dir = "three";
				break;
				
			default:
				// sends an error message
				System.err.println("Invalid difficulty: " + difficulty);
				System.exit(0);
				
		} // switch
		
		// cycles through and loads all the sprites
		for (int i = 0; i < sprites.length; i++){
			
			// adds a sprite
			// see Entity.addSprite
			super.addSprite("images/enemies/" + dir + "/" + sprites[i] + ".png");
			
		}// for
		
		// prevents the enemy from immediately 
		lastPunchTime = System.currentTimeMillis();

	}// EnemyEntity

	/*
	 * getHealth
	 * Input: none
	 * 
	 * Purpose: returns the enemy's health
	 */
	public int getHealth() {

		// returns the enemy's health
		return health;

	}// getHealth
	
	/*
	 * getMaxHealth
	 * Input: none
	 * 
	 * Purpose: returns the enemy's total health
	 */
	public int getMaxHealth() {

		// returns the enemy's total health
		return maxHealth;

	}// getMaxHealth
	
	/*
	 * toString
	 * Input: none,
	 * Purpose: returns a string representation of the enemy
	 */
	public String toString(){
		
		return "Enemy Object";
		
	}// toString
	
	/*
	 * tryToGetUp
	 * input: None
	 * Purpose: When the enemy is down, it has a change to get back up 
	 * 			each time the Referee counts.
	 * 			Determines if it should get up or stay down
	 * 
	 */
	public void tryToGetUp(){
		
		// checks if it should get back up
		if (Math.random() > 0.89 - (0.29 * difficulty)){
			// 0.89^10 = 31% chance to not get up
			
			// sets stage to recover
			changeStage(STATUS_RECOVER);
			
			// recovers some health, but not all
			health = 100 - 20 * timesDown;
			
			// tells the ref to stop counting
			parent.stopCount();
			
		}// if
		
	}// tryToGetUp

	/*
	 * onDown
	 * 	Input:
	 * 		since: the time since the enemy was knocked down
	 * 	Purpose:
	 * 		Plays the falling down animation, then tells the parent to start counting
	 */
	public void onDown(long since){
		
		// checks if it should be lying down or standing up
		if (since > 1200){
			
			// changes sprite to lying down
			currentSprite = 13;
			
			// checks that it has not told the ref to start already
			if (!hasCalledRef){
				
				// checks it has not already been knocked down 3 times already
				if (timesDown < 3){
					
					// tells the parent to start counting
					parent.startCount();
					
				}else { // if
					
					// tells the parent it has lost
					parent.callTKO();
					
				}// else
				
				// records that it has already called the ref so that it does not call twice
				hasCalledRef = true;
				
			}// if
			
		}else { // if
			
			// changes sprite to falling down
			currentSprite = 12;
			
			// changes imgY to moev the image up
			imgY = (int)(y - (100 * (since / 1200.0)));
			
		}// else
		
	}// onDown
	
	/*
	 * onFlinch
	 * Input:
	 * 		since: the time since the enemy was hit
	 * 
	 * Purpose: 
	 * 		Plays the flinching animation when hit
	 */
	public void onFlinch(long since){
		
		// checks if it successfully blocked the punch
		if (isBlocking){
			
			// changes sprite depending where it blocked
			if (isBlockingLower){
				
				// changes sprite to blocking lower
				currentSprite = 4;
			}else { // if
				
				// changes sprite to blocking higher
				currentSprite = 5;
				
			} // else
			
			// checks if it is done flinching
			if (since > flinchDuration / 2){
				
				// changes back to normal
				changeStage(STATUS_NORMAL);
				
				// sets blocking to false
				// it would be set to true in takePunch
				isBlocking = false;
				
			}// if
			
		}else { // if
			
			// checks if it was hit by an uppercut
			if (wasHitByUppercut){
				
				// changes sprite to hit by uppercut
				currentSprite = 7;
				
			}else { // if
				
				// changes sprite to hit by jab
				currentSprite = 6;
				
			}// else
		}// else
		
		// checks if it is done flinching
		if (since > flinchDuration){
			
			// changes back to normal
			changeStage(STATUS_NORMAL);
			
			// sets blocking to false
			// it would be set to true in takePunch
			isBlocking = false;
			
		}// if
		
		
	}// onFlinch
	
	/*
	 * onPunch
	 * 	Input:
	 * 		since: the time since the punch was thrown
	 * 
	 * 	Purpose:
	 * 		Runs the punching animation, calls the enemy damage when the punch connects
	 */
	public void onPunch(long since){
		
		// checks if it is jabbing
		if (!isThrowingUppercut){
			
			// changes sprite to jab
			currentSprite = 8;
			
		}else { // if
			
			// rounds the time since to 200 millisecons
			int rounded = (int)Math.round(Math.floor(since / (double)(punchDuration / 4.0)));
			
			// changes the sprite depending on how far into the animation is it
			// note that the last frame is shown twice al long as the others
			if (rounded < 2){
				
				// changes the sprite to winding up or connecting
				currentSprite = rounded + 9;
				
			}else { // if
				
				// changes the sprite to swinging through
				currentSprite = 11;
				
				// the player will be able to stun the enemy if he hits him now
				isVunerableToStun = true;
				
			} // else
			
			// checks if the punch connected right now
			if (rounded == 2 && !hasDealtDamage){
				
				// causes the player to take damage
				parent.onEnemyPunch(punchIsRight, 30);
				
				// prevents the punch from connecting twice
				hasDealtDamage = true;
				
			} // if
			
		} // else
		
		// checks if it is done punching
		if (since > punchDuration){
			
			// resets back to normal
			lastPunchTime = System.currentTimeMillis();
			
			changeStage(STATUS_NORMAL);
			
			// these variables will be set when the enemy decides to punch again
			isVunerableToStun = false;
			isThrowingUppercut = false;
			
		} // if
		
	} // onPunch
	
	/*
	 * onIdle
	 * 	Input: 
	 * 		since: the time in milliseconds since the enemy was not idle
	 * 	
	 * 	Purpose:
	 * 		Runs the idle animation and checks where it should block, whether is should punch
	 */
	public void onIdle(long since){
		
		if (currentSprite != 0 && currentSprite != 1){
			currentSprite = 0;
		}
		
		// checks if it is stunned
		if (isStunned){
			
			// changes its sprite to stunned
			currentSprite = 14;
			
			// checks if it has recovered from the stun
			if (System.currentTimeMillis() - stunTime >= stunDuration){
				
				// changes to not stunned
				isStunned = false;
				
			}// if
			
		}else { // if
			
			// checks if it should changes the animation
			if (since > animDuration){
				
				// resets since by changign status to normal again
				changeStage(STATUS_NORMAL);
			
				// changes the sprite
				if (currentSprite == 0){
					
					// sets the duration to less because he is hopping
					currentSprite = 1;
					animDuration = 300;
					
					
				}else { // if
					
					// sets the sprite to standing
					currentSprite = 0;
					animDuration = 600;
					
				} // else
	
				
				// check if it should change blocking direction
				if (Math.random() > 0.5){
					
					// adds two to currentSprite so that the enemy is blocking uppercuts
					currentSprite += 2;
					
					// records where it is blocking
					isBlockingLower = false;
					
				}else { // if
					
					// records where it is blocking
					isBlockingLower = true;
					
				} // else
				
				// checks if it should throw a punch
				if (System.currentTimeMillis() - lastPunchTime > 1000 + (Math.random() * 600) - 300 && !parent.getPlayerIsDown()){
					
					// checks if it should jab or uppercut
					if (Math.random() < 0.9){
						
						// uppercuts
						throwPunch(true);
						
					}else { // if
						
						//jabs
						throwPunch(false);
						
					} // else
				} // if
			} // if
		} // if
		
		// checks if it is dead
		if (health <= 0){
			
			// records it has fallen down
			timesDown++;
			
			// changes state to fallen down
			changeStage(STATUS_DOWN);
			
		} // if
	} // onIdle
	
	/*
	 * onDodge
	 * Input:
	 * 		since: The time since the enemy started dodging
	 * 
	 * Purpose:
	 * 		It is a filler method, will never be called
	 */
	public void onDodge(long since){	
	} // onDodge
	
	/*
	 * onIntro
	 * 	Input:
	 * 		since: the time since the intro began
	 * 	Purpose:
	 * 		plays the intro animation
	 */
	public void onIntro (long since){
		
		// the time to wait before moving
		int introDelay = 500;
		
		// the time it takes to move
		int introDuration = 5000;
		
		// the starting coordinates
		int maxY = 200;
		int maxX = 250;
		
		// checks if it should still be waiting
		if (since < introDelay){
			
			// changes sprite to stand
			currentSprite = 0;
			imgY = y - maxY;
			imgX = x + maxX;
			
		// checks if it should be moving
		}else if (since < introDelay + introDuration){
			
			// checks how far into moving to the center it is
			double percentage = (since - introDelay) / (double)introDuration;
			
			// moves
			imgY = (int) (y - maxY + percentage * maxY);
			imgX = (int) (x + maxX - percentage * maxX);
			
			// changes sprite
			currentSprite = (int) (17 + (Math.round(since / 200.0) % 3));
			
		}else{ // else if
			
			// sets sprite to standing
			currentSprite = (int) ((Math.round(since / 400.0) % 2));
			
			// checks if it should tell the ref to come out
			if (!hasSignalledFinishedIntro && since > introDelay + introDuration + 500){
				
				// calls the ref out
				parent.startFightAnimation();
				hasSignalledFinishedIntro = true;
				lastPunchTime = System.currentTimeMillis();
			} // if
		} // else
		
	} // onIntro
	
	// starts to fight
	public void startFight(){
		
		// see Boxer.java
		super.startFight();
		lastPunchTime = System.currentTimeMillis();
	} // startFight
	
	// plays the celebration animation
	public void onCelebrate(long since){
		
		// changes the sprite every 200 milliseconds
		currentSprite = (int) (17 + (Math.round(since / 200.0) % 3));
		
	} // onCelebrate
	
	// plays the recover animation
	public void onRecover(long since){
			
		// shows different sprites depending on how long it has been 
		if (since < 600){
			currentSprite = 15;
			
		}else if (since < 1200){
			currentSprite = 16;
			
		}else if (since < 1800){
			currentSprite = 0;
			
		}else {
			
			// moves back down the stage
			imgY = (int) (y - 100 + ((since - 1800) / 1200.0) * 100);
			
			// changes sprite
			currentSprite = (int)Math.floor((since - 1800) / 100) % 2;
			
			// checks if it is done
			if (since > 3000){
				
				// sets back to normal
				changeStage(STATUS_NORMAL);
				imgY = y;
				imgX = x;
				hasCalledRef = false;
				lastPunchTime = System.currentTimeMillis();
				
			} // if
			
		} // else
	} // onRecover


	// runs through enemy's punching action
	public void throwPunch(boolean isUppercut) {
		
		// changes stage to punching
		changeStage(STATUS_PUNCHING);
		
		// if punch is left or right side
		boolean isRight = false; 

		// randomly punch left or right
		if ((int) (Math.random() * 2) == 1) {
			
			// sets punch to player's right
			isRight = true;
			isMirrored = true;
			
		} // if
		
		// checks if it is uppercut
		if (!isUppercut){
			
			// deals damage noe
			// uppercuts will deal damage partway through the animation
			parent.onEnemyPunch(isRight, 20);
			
		}else { // if
			
			// sets up to play the uppercut animation
			isThrowingUppercut = true;
			hasDealtDamage = false;
			punchIsRight = isRight;
			
		} // else
	}// throwPunch
	
	// takes damage from a punch
	public void takePunch(int damage, boolean isUpper, boolean isRight){
		
		// checks if it is able to take the punch
		if (stage != STATUS_DOWN && stage != STATUS_RECOVER){
			
			// checks if the punch will stun it
			if (isVunerableToStun && !isStunned){
				
				// is stunned
				isStunned = true;
				stunTime = System.currentTimeMillis();
				isVunerableToStun = false;
				
			} // if
			
			// checks if it blocked the punch
			if ((isBlockingLower && !isUpper || !isBlockingLower && isUpper) && !isStunned){
				
				// sets up blocking animation
				isBlocking = true;
				wasHitByUppercut = isUpper;
				
				// takes only half damage
				health -= damage / 2;
				
			}else { // if
				
				// takes damage
				health -= damage;
				isBlocking = false;
				
			} // else
			
			// mirrors the flinching animation if needed
			isMirrored = !isRight;
			
		// changes stage to flinching
		changeStage(STATUS_FLINCHING);
			
		} // if
	} // takePunch

}// Enemy
