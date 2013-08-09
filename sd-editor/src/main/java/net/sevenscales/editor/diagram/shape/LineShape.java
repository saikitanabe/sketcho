package net.sevenscales.editor.diagram.shape;


public class LineShape extends Info {

	public int x1;
	public int y1;
	public int x2;
	public int y2;

	public LineShape() {
	}

	public LineShape(int x1, int y1, int x2, int y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}
	
	public void reset() {
	  x1 = 0;
	  y1 = 0;
	  x2 = 0;
	  y2 = 0;
	}
	
}
