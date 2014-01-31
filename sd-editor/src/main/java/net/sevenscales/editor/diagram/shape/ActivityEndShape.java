package net.sevenscales.editor.diagram.shape;


public class ActivityEndShape extends CenterShape {

	public int radius;

	public ActivityEndShape(int x, int y, int radius) {
		super(x, y);
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
