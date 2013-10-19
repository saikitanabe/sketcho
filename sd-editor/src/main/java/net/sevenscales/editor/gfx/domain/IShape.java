package net.sevenscales.editor.gfx.domain;

import com.google.gwt.core.client.JavaScriptObject;

public interface IShape extends IGraphics {
  void setStroke(String color);
	void setStroke(int red, int green, int blue, double opacity);
	void setStroke(int red, int green, int blue, double opacity, double width);
  void setStrokeWidth(double width);
  double getStrokeWidth();
  Color getStrokeColor();
//  public abstract JavaScriptObject getStroke();
  void setFill(String color);
  void setFill(int red, int green, int blue, double opacity);
  Color getFillColor();
//  public abstract JavaScriptObject getFill();
  JavaScriptObject getRawNode();
  void moveToBack();
  void moveToFront();
  void applyTransform(int dx, int dy);
  void applyTransformToShape(int dx, int dy);
  int getDX();
  int getDY();
  void setVisibility(boolean visibility);
  boolean isVisible();
  String getAttribute(String name);
  void setAttribute(String name, String value);
  void rotatedxdy(int degree, int x, int y, int dx, int dy);
  void rotate(int degrees, int x, int y);
  void rotateg(int degree);
  void skewg(int skew);
  int getRotateDegree();
  void unrotate(int degree, int x, int y);
  String getTransformMatrix();
  void resetAllTransforms();
	void remove();
	
	// HACK!
	void setSvgFixX(int dx);
	int getSvgFixX();
	void setSvgFixY(int dy);
	int getSvgFixY();
	
  void setStyle(String style);
  String getStyle();

}