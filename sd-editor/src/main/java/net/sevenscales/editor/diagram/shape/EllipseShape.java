package net.sevenscales.editor.diagram.shape;


public class EllipseShape extends Info {

	public int cx;
	public int cy;
	public int rx;
	public int ry;

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
