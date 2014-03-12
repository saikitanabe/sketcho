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
  void setTranslate(double x, double y);
  void setScale(double xx, double yy);
  void setMatrix(double xx, double xy, double yx, double yy, double dx, double dy);
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

  /**
  * Possibility to define constant colors for shape inside a element.
  * E.g. Note element uses tape with constant colors, that doesn't change
  * according to theme. 
  * Needed on svg generation to know if shape is converted according to theme colors or not.
  */
  boolean isThemeSupported();
  void setSupportsTheme(boolean themeSupported);


}