package net.sevenscales.editor.diagram.shape;



public class RectShape extends Info {

	public int left;
	public int top;
	public int width;
	public int height;
	
	public RectShape() {
	}

	public RectShape(String[] rectShape) {
		// assert(rectShape.length == 4);
		left = Integer.valueOf(rectShape[0]);
		top = Integer.valueOf(rectShape[1]);
		width = Integer.valueOf(rectShape[2]);
		height = Integer.valueOf(rectShape[3]);
	}

	public RectShape(int x, int y, int width, int height) {
		this.left = x;
		this.top = y;
		this.width = width;
		this.height = height;
	}
	
	public void reset() {
	  left = 0;
	  top = 0;
	  width = 0;
	  height = 0;
	}
	
	@Override
	public void setLeft(int left) {
		this.left = left;
	}
	@Override
	public int getLeft() {
		return left;
	}
	
	@Override
	public void setTop(int top) {
		this.top = top;
	}
	@Override
	public int getTop() {
		return top;
	}
	
	@Override
	public void setWidth(int width) {
		this.width = width;
	}
	@Override
	public int getWidth() {
		return height;
	}
	
	@Override
	public void setHeight(int height) {
		this.height = height;
	}
	@Override
	public int getHeight() {
		return width;
	}

}
