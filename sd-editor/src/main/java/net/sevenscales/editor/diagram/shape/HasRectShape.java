package net.sevenscales.editor.diagram.shape;

public class HasRectShape extends Info {
	public RectShape rectShape = new RectShape();
	
	public HasRectShape() {
	}
	
	public HasRectShape(String[] rectShape) {
		this.rectShape = new RectShape(rectShape);
	}

	public HasRectShape(int left, int top, int width, int height) {
		rectShape.left = left;
		rectShape.top = top;
		rectShape.width = width;
		rectShape.height = height;
	}

	@Override
	public Info move(int moveX, int moveY) {
		rectShape.move(moveX, moveY);
		return this;
	}

	@Override
	public void setLeft(int left) {
		rectShape.setLeft(left);
	}
	@Override
	public int getLeft() {
		return rectShape.getLeft();
	}

	@Override
	public void setTop(int top) {
		rectShape.setTop(top);
	}
	@Override
	public int getTop() {
		return rectShape.getTop();
	}

	@Override
	public void setWidth(int width) {
		rectShape.setWidth(width);
	}
	@Override
	public int getWidth() {
		return rectShape.getWidth();
	}

	@Override
	public void setHeight(int height) {
		rectShape.setHeight(height);
	}
	@Override
	public int getHeight() {
		return rectShape.getHeight();
	}

}
