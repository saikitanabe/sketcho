package net.sevenscales.editor.diagram.shape;


public class CircleShape extends Info {

	public int centerX;
	public int centerY;
	public int radius;

	public CircleShape() {
	}

	public CircleShape(int x, int y, int radius) {
		this.centerX = x;
		this.centerY = y;
		this.radius = radius;
	}
	
	public void reset() {
	  centerX = 0;
	  centerY = 0;
	  radius = 0;
	}
	
}
