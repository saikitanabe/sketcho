package net.sevenscales.editor.gfx.domain;

public class Rect {
	public Rect(int x, int y, int width, int height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }
  public Rect() {
    // TODO Auto-generated constructor stub
  }
  public int x;
	public int y;
	public int width;
	public int height;
	public int r; // rounded corners radius
}
