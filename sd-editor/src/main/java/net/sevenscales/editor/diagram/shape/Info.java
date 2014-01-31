package net.sevenscales.editor.diagram.shape;


public abstract class Info {
	private String textColor = "";
	private String backgroundColor = "";
	private String borderColor = "";

	public void setLeft(int left) {
	}
	public int getLeft() {
		return 0;
	}

	public void setTop(int top) {
	}
	public int getTop() {
		return 0;
	}

	public void setWidth(int width) {
	}
	public int getWidth() {
		return 0;
	}

	public void setHeight(int height) {
	}
	public int getHeight() {
		return 0;
	}

	public abstract Info move(int moveX, int moveY);
	
	public final void setTextColor(String textColor) {
		this.textColor = textColor;
	}
	public final String getTextColor() {
		return textColor;
	}
	public final void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	public final String getBackgroundColor() {
		return backgroundColor;
	}
	public final void setBorderColor(String borderColor) {
		this.borderColor = borderColor;
	}
	public final String getBorderColor() {
		return borderColor;
	}
}
