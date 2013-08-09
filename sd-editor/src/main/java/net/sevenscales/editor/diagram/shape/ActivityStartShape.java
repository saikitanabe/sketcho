package net.sevenscales.editor.diagram.shape;


public class ActivityStartShape extends Info {

	public int centerX;
	public int centerY;
	public int radius;

	public ActivityStartShape() {
	}

	public ActivityStartShape(int x, int y, int radius) {
		this.centerX = x;
		this.centerY = y;
		this.radius = radius;
	}
	
	public void reset() {
	  centerX = 0;
	  centerY = 0;
	  radius = 0;
	}
	
	@Override
	public int getLeft() {
		return centerX - radius;
	}
	
	@Override
	public int getTop() {
		return centerY - radius;
	}
	
}
