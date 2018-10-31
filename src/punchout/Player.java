package punchout;

import java.awt.Graphics;

/*
 * Player
 * The main player entity for the enemy to fight
 */
public class Player extends Boxer{

	// how much health the player has
	private int health;

	// the amount of time between changing the player's sprite
	private int animDuration = 200;
	
	private boolean punchIsUppercut = false;
	
	// the time it takes the player to run through the punching animation
	private int punchDuration = 350;
	
	// the time it takes to run through the uppercut animation
	private int uppercutDuration = 450;
	
	// whether the punch is right handed
	private boolean punchIsRightHanded = false;
	
	// the last time the player punched
	private long lastPunchTime = 0;
	
	// the time the player must wait between punches
	private int timeBetweenPunches = 50;
	
	// whether the player has damaged the enemy yet
	private boolean hasCalledEnemyDamage = false;
	
	// whether the player is dodging to the left
	// Note: If the isDodging is true but dodgingLeft is not, the player is dodging right
	private boolean dodgingLeft;
	
	// the time it takes to finish the dodge animation
	private int dodgeDuration = 400;
	
	// whether or not the player will be able to dodge a punch
	// Note that even if isDodging is true, there is a small
	// delay before the player can dodge so that spamming dodge
	// is not a valid strategy
	private boolean canEvadePunch = false;
	
	// the time it takes the player to recover from flinching
	private int flinchDuration = 200;
	
	// whether it is still playing the animation of the player falling off the screen
	private boolean isPlayingDownAnim = false;
	
	// the time it takes to play the player down animation
	private int downAnimDuration = 1000;
	
	// the times space has been hit
	private int timesSpaceHit = 0;
	
	// the times space needs to be hit in order to recover
	private int spaceHitsToRecover = 30;
	
	// the number of times the player has fallen down
	private int timesDown = 0;
	
	// whether or not the player has signalled it has lost
	private boolean hasSignalledLoss = false;
	
	/*
	 *  Constructor
	 *  Input:
	 *  	game: The Game which the player belongs to
	 *  Purpose: Sets up the player entity
	 */
	public Player(Game game) {
		
		// Initializes the Entity class
		super(2, game);
		
		// loads its sprites
		String[] spriteNames = {
				
				// idle animation
				"standOne",
				"standTwo",
				
				// punching animation
				"punchOne",
				"punchTwo",
				"punchThree",
				
				// dodging animation
				"dodge",
				
				// flinching animation
				"hurtOne",
				"hurtTwo",
				
				// uppercut
				"upperOne",
				"upperTwo",
				"upperThree",
				"upperFour",
				
				// celbration
				"winOne",
				"winTwo"
				
		}; // spriteNames
		
		for (int i = 0; i < spriteNames.length; i++){
			
			super.addSprite("images/player/" + spriteNames[i] + ".png");
			
		}// for
		
		// sets x and y to predetermined locations
		
		// centers in the middle of the screen horizontally
		x = getParent().getWidth() / 2;
		// centers 7/8ths down the screen vertically
		y = (int)(getParent().getHeight()  * (7 / 8.0));
		
		// sets imgX and imgY back to default value
		imgX = x;
		imgY = y;
		
		// sets health to full
		health = 100;
		
	}// Constructor
	
	// to string method
	public String toString(){
		return "Player object";
		
	} // toString
	
	/*
	 * see Boxer.onDown
	 */
	public void onDown(long since){
		
		// the farthest down positino the playter can go
		int downPosition = (int)(parent.getHeight() - y + h);
		
		// whether or not the player is playing the animation
		if (isPlayingDownAnim){
			
			// checks if it is done falling
			if (since > downAnimDuration){
				
				// sets to recovering
				isPlayingDownAnim = false;
				imgX = x;
				
			} // if
				
			// moves the player down a bit
			
			// the percentage of time that has gone by
			double percentage = (since / (double)downAnimDuration);
			
			// moves the player downs
			imgY = y + (int)(downPosition * percentage);
			
			// sets sprite to hurt
			currentSprite = 7;
			
		}else { // if
			
			// checks if the plyaer is still alive
			if (timesDown < 3){
				
				// moves up according to how many times space has been hit
				imgY = y + (int)(downPosition - downPosition * (timesSpaceHit / (double)spaceHitsToRecover));
				
				// sets sprite to standing
				currentSprite = 0;
				
				// checks if it has recovered
				if (timesSpaceHit >= spaceHitsToRecover){

					// changes stage back to normal
					changeStage(STATUS_NORMAL);
					
					// regenerates health
					health = 100 - (20 * timesDown);
					
					// resets times space was hit
					timesSpaceHit = 0;
					
				} // if
				
			}else if (!hasSignalledLoss){ // if
				
				// tells the parent it has lost
				parent.onLoss();
				hasSignalledLoss = true;
				
			} // else if
			
		} // else
		
	} // onDown
	
	/*
	 * see Boxer.onFlinch
	 */
	public void onFlinch(long since){

		// checks if the player is done flinching
		if (since >= flinchDuration){
			
			// resets the variables to default
			changeStage(STATUS_NORMAL);
			isMirrored = false;
			imgY = y;
			currentSprite = 0;
			
		}else { // if
			
			// gets the percentage through the animation
			double percentage = since / (double)flinchDuration;

			// the maximum distance the image will be drawn from the origin x
			int maxY = 40;

			// checks if it is less than halfway done
			if (since < flinchDuration / 2.0){
				
				// sets the sprite to the first sprite
				currentSprite = 6;
				
			}else { // if

				// sets the sprite to the second
				currentSprite = 7;
				
			} // else
			
			// moves the player over
			imgY = (int)(y + ((percentage) * maxY));
			
		}// else
		
	} //onFlinch
	
	/*
	 * see Boxer.onPunch
	 */
	public void onPunch(long since){
		
		// gets the percentage into the animation the player is
		// between 0.0 and 1.0
		double percentage;
		
		if (punchIsUppercut){
			percentage = since / (double)uppercutDuration;
			
		}else { // if
			percentage = since / (double)punchDuration;

		} // else

		// chooses which stage the animation is at
		// Note: The each stage runs different code shows different sprites
		int stage = (int) Math.ceil(percentage * 5);
		
		// the maximum to move up the image
		// the image should move up until stage 3, when it delivers the punch
		// then it falls back to its original position
		int maximum;
		
		if (punchIsUppercut){
			maximum = 200;
			
		}else { // if
			maximum = 80;
			
		} // else
		
		// checks if punch is connecting right now
		if (stage == 3){
			
			// deal damage to enemy
			if (!hasCalledEnemyDamage){
				
				// calls damage
				parent.onPlayerPunch(punchIsUppercut, punchIsRightHanded);
				hasCalledEnemyDamage = true;
			} // if
		
		// checks if the player is done punching
		}else if (percentage >= 1.0){
			
			// sets variables back to default value
			changeStage(STATUS_NORMAL);
			currentSprite = 0;
			isMirrored = false;
			imgY = y;
			
			// recors the time it was last punching
			lastPunchTime = System.currentTimeMillis();
			
		}else { // else if
			
			// checks if the animation is over halfway done
			if (percentage > 0.5){
				
				// now, currentSprite decreases  as the stage increases
				// Note: Adds 2 at the end to skip over the idle sprites
				currentSprite = (5 - stage) + 2;
				
				// makes the player fall back to its original position
				// Note: When percentage is 1.0, imgY will be reset to y
				imgY = (int) (y - maximum * (1.0 - percentage));
				
			}else { //if
				
				// sets currentSprite to increase with stage
				// Adds 2 to skip over the idle sprites
				currentSprite = stage + 2;
				
				// makes the player jump into the air
				imgY = (int) (y - maximum * (percentage));
				
			} // else
			
			if (punchIsUppercut){

				
				// makes sprite the correct uppercut sprite
				currentSprite = 8 + (int)Math.floor(percentage * 4);
				
			} // if
		}// else
	} // onPunch
	
	/*
	 * see Boxer.onDodge
	 */
	public void onDodge(long since){
		
		// gets the percentage (between 0.0 and 1.0)
		double percentage = (since / (double)dodgeDuration);
		
		// gets the stage
		int stage = (int)Math.floor(percentage * 5.0);
		/*
		 * STAGES:
		 * 0: Moving halfway away and up
		 * 1: Moving the other half away and down
		 * 2: Not moving
		 * 3: Moving halfway towards and up
		 * 4: Moving the other half towards and down
		 * 5: done
		 * 
		 */
		
		// the maximum distance to move the image
		int maxY = 30;
		int maxX = 80;
		
		// sets the sprite to the dodge sprite
		currentSprite = 5;
		
		// gets the percentage through each stage\
		// Note: Due to the % 0.2, the percentage will reset once the next stage is started
		double stagePercentage = (percentage % 0.2) / 0.2;
		
		// whether or not the player can evade the punch
		canEvadePunch = true;
		
		// runs the animation for each stage
		switch (stage){
		
			case 0:
				
				// moves up and away
				imgY = (int) (y - (stagePercentage) * maxY);
				imgX = (int) (x - (stagePercentage) * (maxX / 2));
				break;
			
			case 1:
				
				// moves down and away
				imgY = (int)(y - ((stagePercentage) * (1 - maxY)));
				imgX = (int)(x - (stagePercentage) * (maxX / 2) - (maxX / 2));
				break;
				
			case 2:
				
				// The player needs to just stay, so no movement is needed
				break;
				
			case 3:
				
				// moves up and towards
				imgY = (int) (y - (stagePercentage) * maxY);
				imgX = (int)((x - maxX) + (stagePercentage) * (maxX / 2));
				break;
				
			case 4:
				
				// moves down and towards
				imgY = (int)(y - ((stagePercentage) * (1 - maxY)));
				imgX = (int)((x - maxX / 2) + (stagePercentage) * (maxX / 2));
				break;
				
		} // switch
		
		// checks if the player is done dodging
		
		if (percentage > 1){
			
			// resets all the variables used
			changeStage(STATUS_NORMAL);
			isMirrored = false;
			imgY = y;
			imgX = x;
		}// if
	}  // onDodge
	
	/*
	 *  see Boxer.onIdle
	 */
	public void onIdle(long since){

		// checks that the player is running the idle animation
		if (currentSprite != 0 && currentSprite != 1){
			currentSprite = 0;
			
		} // if
		
		// sets imgX and imgY to default
		imgX = x;
		imgY = y;
		
		// player cannot evade punch right now
		canEvadePunch = false;
		
		// checks if it needs to change its sprite
		if (since > animDuration){
			
			// sets the last sprite change time to current time
			changeStage(STATUS_NORMAL);
			
			// changes the sprite
			if (currentSprite == 0){
				
				// the player stands for 700 millliseconds
				currentSprite = 1;
				animDuration = 700;
				imgY = y;
				
			}else{ // if
				
				// the player hops for 200 milliseconds
				currentSprite = 0;
				animDuration = 200;
				imgY = y - 5;
				
			} // else
			
		}// if
		
	} // onIdle

	/*
	 * see Boxer.onCelebrate
	 */
	public void onCelebrate(long since){
		
		// the most the player will go up
		int maxY = 50;
		
		if (since < 600){
			
			// the player walks up to the center of the ring
			imgY = (int) (y - (since / (double)600) * maxY);
			
			// changes sprite to running every 200 milliseconds
			currentSprite = Math.round(since / 200) % 2;
			
		}else { // if
			
			// changes sprite to winning every 200 milliseconds
			currentSprite = 12 + (Math.round(since / 200) % 2);
			
		} // else
		
	} // onCelebrate
	
	/*
	 * see Boxer.onIntro
	 */
	public void onIntro(long since){
		
		// changes sprite between idles sprites
		currentSprite = (int) (Math.round(since / 600.0) % 2);
		
	} // onIntro
	
	/*
	 * Empty since player does not need this method
	 * See Boxer.onRecover
	 */
	public void onRecover(long since){
		
	} // onRecover
	
	/*
	 * onSpace
	 * Input: None
	 * Output: None
	 * Purpose: adds one to space count
	 */
	public void onSpace(){
		
		// checks if the space count should count
		if (stage == STATUS_DOWN && !isPlayingDownAnim){
			
			// adds one to space count
			timesSpaceHit++;
			
		} // if
		
	} // onSpace
	
	/*
	 * throwPunch
	 * isRightHanded: whether the punch is right handed
	 * 		Note: is isRightHanded is false, the punch is left handed
	 * isUppercut: whether the punch is an uppercut or not
	 * 		Note: No current implementation of uppercuts
	 * 
	 */
	public void throwPunch(boolean isRightHanded, boolean isUppercut){
		
		// checks that the player is not already dodging or punching
		if (stage == STATUS_NORMAL && System.currentTimeMillis() - lastPunchTime > timeBetweenPunches){
			
			// checks whether the punch is rightHanded or lefthanded
			if (isRightHanded){
				
				// sets up for right handed punch
				isMirrored = true;
				
			}else { //if
				
				// sets up for left handed punch
				isMirrored = false;
			}// else
			
			// checks if the punch is an uppercut
			if (isUppercut){
				
				// currently does nothing
				punchIsUppercut = true;
				
			}else {
				punchIsUppercut = false;
			}
			
			punchIsRightHanded = isRightHanded;
			
			// sets up to punch
			changeStage(STATUS_PUNCHING);
			hasCalledEnemyDamage = false;
			
		} // if
		
	} // throwPunch
	
	/*
	 * takePunch
	 * Called when the player has a punch thrown at them
	 * checks if the player can dodge it or not
	 * isRightHanded: same as throwPunch
	 * hitDamage: The damage the punch will deal if it connects
	 */
	public void takePunch (boolean isRightHanded, int hitDamage){
		
		if (stage != STATUS_DOWN){
			// whether the dodge direction is correct
			boolean dodgeDirectionIsCorrect = false;
			
			// checks which side the punch came from and if the player is dodging to the right direction
			// Note: Uses the player's right and left
			if (isRightHanded){
				
				isMirrored = true;
				
				// checks if it cannot dodge the punch
				if (!dodgingLeft){
					
					dodgeDirectionIsCorrect = true;
					
				} // if
				
				
			}else { // if
				
				isMirrored = false;
				
				if (dodgingLeft){
					
					dodgeDirectionIsCorrect = true;
					
				}// if
				
			} // else

			// checks if it cannot dodge the handed punch
			if (!(canEvadePunch && stage == STATUS_DODGING && dodgeDirectionIsCorrect)){
			
				// hurts self
				health -= hitDamage;
				
				// sets up the flinching animation
				changeStage(STATUS_FLINCHING);
				
				imgX = x;
				imgY = y;
				
				// checks if the player is out of health
				if (health <= 0){
					
					changeStage(STATUS_DOWN);
					timesDown++;
					isPlayingDownAnim = true;
					
				}
				
			} // if
			
		}
		
	} // takePunch
	
	/*
	 * dodge
	 * Causes the player to dodge to the left or right
	 * isRight: whether the player dodges from the right or left
	 */
	public void dodge(boolean isRight){
		
		// checks if it is not already dodging or punching
		if (stage == STATUS_NORMAL){
			
			// sets up to dodge
			changeStage(STATUS_DODGING);
			dodgingLeft = !isRight;
			
			// checks if it needs to mirror the sprite
			// Note: imgX will automatically be mirrored by Entity
			if (isRight){
				
				isMirrored = true;
				
			}// if
			
		}// if
	}// dodge
	
	// get and set methods
	
	public boolean getIsDown(){
		return (stage == STATUS_DOWN);
		
	} // getIsDown
	
	public int getHealth(){
		return health;
	}

} // Player class
