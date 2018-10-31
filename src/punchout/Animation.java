package punchout;

import java.util.ArrayList;

public class Animation {

	private int[] indexes;
	
	private int index;
	
	public Animation(int[] i){
		
		indexes = i;
		
	}
	
	public void nextSprite(){
		
		index++;
		if (index >= indexes.length){
			index = 0;
		}
		
	}
	
	public int getCurrentIndex(){
		return indexes[index];
	}

}
