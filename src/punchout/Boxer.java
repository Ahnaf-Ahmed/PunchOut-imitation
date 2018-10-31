package punchout;

public abstract class Boxer extends Entity{
	
	// the current status
	protected int stage = 0;
	
	// the different statuses
	protected final int STATUS_NORMAL = 0;
	protected final int STATUS_PUNCHING = 1;
	protected final int STATUS_FLINCHING = 2;
	protected final int STATUS_DOWN = 3;
	protected final int STATUS_CELEBRATING = 4;
	protected final int STATUS_INTRO = 5;
	protected final int STATUS_DODGING = 6;
	protected final int STATUS_RECOVER = 7;
	
	// the last time a status change was made
	protected long lastStatusChangeTime = 0;
	
	// the times it has been down
	// a boxer can only be down three times before losing
	protected int timesDown = 0;
	
	/*
	 * Constructor
	 * Input:
	 * 		imgMulti: the multiplier to resize the images by
	 * 		See entity for more information
	 * 		game: The Game in which the boxer exists
	 * 
	 * Purpose: Mainly initializes the entity class, and sets the status to intro by default
	 */
	public Boxer(int imgMulti, Game game){
		
		// Initializes the Entity class
		super(0, 0, imgMulti, game);
		
		// sets to intro
		changeStage(STATUS_INTRO);
		
	}// constructor
	
	/*
	 * startFight
	 * Input: None,
	 * 
	 * Purpose: Called by the referee instance, changes the boxer from intro to normal
	 */
	public void startFight(){
		
		// changes the status
		changeStage(STATUS_NORMAL);
		
	}// startFight
	
	/*
	 * update
	 * 
	 * Input: none
	 * 
	 * Purpose: The main use of the boxer class, it chooses which function to run
	 * depending on the stage. For example, if the player's status is set 
	 * to punching, it will run the code in the onPunch method, which will
	 * be set in the Player or Enemy class
	 */
	public void update (){
		
		// gets the time since the last status change
		long since = System.currentTimeMillis() - lastStatusChangeTime;
		
		// chooses a method based on the current status
		switch (stage){
		
			// runs the idle code
			case STATUS_NORMAL:
				onIdle(since);
				break;
				
			// runs the punching code
			case STATUS_PUNCHING:
				onPunch(since);
				break;
				
			// runs the flinching code
			case STATUS_FLINCHING:
				onFlinch(since);
				break;
				
			// runs the down code
			case STATUS_DOWN:
				onDown(since);
				break;
				
			// runs the celebration code
			case STATUS_CELEBRATING:
				onCelebrate(since);
				break;
				
			// runs the intro code
			case STATUS_INTRO:
				onIntro(since);
				break;
				
			// runs the dodging code
			case STATUS_DODGING:
				onDodge(since);
				break;
				
			// runs the recover code
			case STATUS_RECOVER:
				onRecover(since);
				break;
				
			// sends and error message
			default:
				System.err.println("Invalid stage value " + stage + " for " + toString());
				System.exit(0);
				
		}// switch
		
	}// update

	/*
	 * Methods that MUST be declared in the subclass
	 * These are the methods that will be called depending on the 
	 * current state.
	 */
	public abstract void onIdle(long since);
	public abstract void onPunch(long since);
	public abstract void onFlinch(long since);
	public abstract void onDown(long since);
	public abstract void onCelebrate(long since);
	public abstract void onIntro(long since);
	public abstract void onDodge(long since);
	public abstract void onRecover(long since);

	/*
	 * changeStage
	 * Input:
	 * 		newStage: the stage to change to
	 * 			Note: usually a given stage from above (Ex: changeStage(STATUS_IDLE))
	 * 
	 * Purpose:
	 * 		Changes the stage and records the time the last stage change occured at
	 */
	protected void changeStage (int newStage){
		
		stage = newStage;
		lastStatusChangeTime = System.currentTimeMillis();
		
	}// changeStage
	
	/*
	 * win
	 * Input: None,
	 * 
	 * Purpose: Called when the boxer has won the fight
	 * 		Note: since it is called by the Game class, it cannot use changeStage
	 * 		because changeStage is protected
	 */
	public void win(){		
		changeStage(STATUS_CELEBRATING);
		
	}// win
	
}// Boxer
