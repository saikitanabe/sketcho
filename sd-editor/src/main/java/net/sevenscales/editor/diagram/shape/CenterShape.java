package net.sevenscales.editor.diagram.shape;

public class CenterShape extends Info {
	public int centerX;
	public int centerY;

	public CenterShape(int cx, int cy) {
		this.centerX = cx;
		this.centerY = cy;
	}

  @Override
  public Info move(int moveX, int moveY) {
  	centerX += moveX;
  	centerY += moveY;
    return this;
  }

  public String toString() {
  	return centerX + "," + centerY;
  }
}