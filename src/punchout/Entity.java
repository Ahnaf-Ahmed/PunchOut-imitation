package punchout;

//import SpriteStore;

import java.awt.*;
import java.util.ArrayList;

public class Entity {
	
	// anchor location on the screen
	// used to change imgX and imgY
	protected int x;
	protected int y;
	
	// the width and height to draw the image
	protected int w;
	protected int h;
	
	// where to draw the image on the screen
	// by default set to x and y
	protected int imgX;
	protected int imgY;
	
	// the amount to scale the image by
	// for example, 2 would double the size of hte image
	// makes sure the image stays proportional
	protected int imgMulti;
	
	// the game the entity belongs to
	protected Game parent;
	
	// the current sprite to draw
	protected int currentSprite = 0;
	
	// whether or not to mirror the sprite
	protected boolean isMirrored = false;
	
	// the different sprites
	protected ArrayList sprites;
	
	/*
	 * Constructor
	 * Input:
	 * 		newX: The x location
	 * 		newY: The y location
	 * 		multi: The imgMulti value
	 * 		game: The game the entity belongs to
	 */
	public Entity (int newX, int newY, int multi, Game game) {

		// sets the location
		x = newX;
		y = newY;
		   
		// sets image location to location by default
		imgX = x;
		imgY = y;
		   
		// set parent
		parent = game;
		   
		// initializes the sprites set
		sprites = new ArrayList();
		   
		// records the imgMulti
		imgMulti = multi;

	} // Entity
	
	/*
	 * addSprite
	 * Input: 
	 * 		spriteName: The name (or URL) of the sprite
	 */
	public void addSprite(String spriteName){
		
		// gets the sprite from spriteStore
		Sprite sprite = (SpriteStore.get()).getSprite(spriteName);
		
		// adds it to sprites
		sprites.add(sprite);
		
	} // addSprite
	
	/*
	 * render
	 * Input:
	 * 		g: The Graphics object with which to draw the sprite
	 */
	public void render (Graphics g) {
	      
		// gets the current sprite
		Sprite sprite = (Sprite)sprites.get(currentSprite);
		
		// scales the dimensions with the image so that the image is not distorted
		w = (int)(sprite.getWidth() * imgMulti);
		h = (int)(sprite.getHeight() * imgMulti);
		
		// the actual location to draw the image
		// because when mirrored, the x should move
		// right to not mess up the location
		int actualImgX = imgX;
		
		// mirrors it if needed
		if (isMirrored){
			
			w *= -1;
			actualImgX = (x - imgX) + x;
		} // if
		
		// sprite is centered by the bottom and center
		(sprite).draw(g,(int)(actualImgX - w / 2), (int)(imgY - h), w, h);
		
	} // render
	
	// get and set methods
	public int getW(){
		return w;
	}// getW
	
	public int getH(){
		return h;
	} // getH
	
	// Note that setX and setY also set the imgX and imgY values
	public void setX(int newX){
		x = newX;
		imgX = x;
	} // setX
	
	public void setY(int newY){
		y = newY;
		imgY = y;
	} // setY
	
	public Game getParent(){
		return parent;
	} //getPArent

} // Entity