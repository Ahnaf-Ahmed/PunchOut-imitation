/* Sprite.java
 * March 23, 2006
 * Store no state information, this allows the image to be stored only
 * once, but to be used in many different places.  For example, one
 * copy of alien.gif can be used over and over.
 */

package punchout;

import java.awt.Graphics;
import java.awt.Image;

public class Sprite {

	private int w = 0;
	private int h = 0;

	public Image image; // the image to be drawn for this sprite

	// constructor
	public Sprite(Image i) {
		image = i;
	} // constructor

	// return width of image in pixels
	public int getWidth() {
		return image.getWidth(null);
	} // getWidth

	// return height of image in pixels
	public int getHeight() {
		return image.getHeight(null);
	} // getHeight

	// returns the set width and height
	public int getW() {
		return w;
	}

	public int getH() {
		return h;
	}

	// allows the user to set height and width
	// they will be used if w and h are null in the draw method
	public void setWidth(int newW) {
		w = newW;
		h = (int) ((newW / (double) getWidth()) * getHeight());
	}

	public void setHeight(int newH) {
		h = newH;
		w = (int) ((newH / (double) getHeight()) * getWidth());
	}

	// draw the sprite in the graphics object provided at location (x,y)
	public void draw(Graphics g, int x, int y, int imgW, int imgH) {
		if (imgW != 0 && imgH != 0) {
			g.drawImage(image, x, y, imgW, imgH, null);

		} else {
			g.drawImage(image, x, y, -w, h, null);
		}
	} // draw

} // Sprite