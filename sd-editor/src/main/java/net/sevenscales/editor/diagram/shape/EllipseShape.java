package net.sevenscales.editor.diagram.shape;


public class EllipseShape extends CenterShape {

	public int rx;
	public int ry;

	public EllipseShape(String[] shape) {
		super(Integer.valueOf(shape[0]), Integer.valueOf(shape[1]));
    rx = Integer.valueOf(shape[2]);
    ry = Integer.valueOf(shape[3]);
  }

	public EllipseShape(int cx, int cy, int rx, int ry) {
		super(cx, cy);
		this.rx = rx;
		this.ry = ry;
	}
	
	@Override
	public int getLeft() {
		return centerX - rx;
	}
	
	@Override
	public int getTop() {
		return centerY - ry;
	}

}
