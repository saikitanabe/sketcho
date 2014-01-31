package net.sevenscales.editor.diagram.shape;


public class EllipseShape extends Info {

	public int cx;
	public int cy;
	public int rx;
	public int ry;

	public EllipseShape(String[] shape) {
		this(shape, 0, 0);
	}
	public EllipseShape(String[] shape, int moveX, int moveY) {
    cx = Integer.valueOf(shape[0]) + moveX;
    cy = Integer.valueOf(shape[1]) + moveY;
    rx = Integer.valueOf(shape[2]);
    ry = Integer.valueOf(shape[3]);
  }


	public EllipseShape(int cx, int cy, int rx, int ry) {
		this.cx = cx;
		this.cy = cy;
		this.rx = rx;
		this.ry = ry;
	}
	
	@Override
	public int getLeft() {
		return cx - rx;
	}
	
	@Override
	public int getTop() {
		return cy - ry;
	}

}
