package net.sevenscales.editor.diagram.shape;


public class CircleShape extends CenterShape {

	public int radius;

	public CircleShape() {
		super(0, 0);
	}

	public CircleShape(int x, int y, int radius) {
		super(x, y);
		this.radius = radius;
	}
	
	public void reset() {
	  centerX = 0;
	  centerY = 0;
	  radius = 0;
	}
	
}
