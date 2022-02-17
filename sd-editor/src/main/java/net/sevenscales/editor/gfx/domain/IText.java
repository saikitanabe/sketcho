package net.sevenscales.editor.gfx.domain;

import com.google.gwt.core.client.JavaScriptObject;
import net.sevenscales.editor.gfx.domain.Color;

public interface IText extends IShape {
  public static final String ALIGN_LEFT = "start";
  public static final String ALIGN_CENTER = "middle";
  public static final String ALIGN_RIGHT = "end";
  
  public static final String WEIGHT_BOLD = "bold";
  public static final String WEIGHT_NORMAL = "normal";
	public static final String SANS = "arial,helvetica,sans-serif";
  
	public int getX();
	public int getY();
	void setFontSize(String fontSize);
	String getFontSize();
  public String getFontWeight();
	public void setFontWeight(String weight);
	public void setAlignment(String alignment);
  public String getAlignment();
  void setTextTspanAlignCenter();
  void setTextTspanAlignRight();
	public void setText(String text);
	public String getText();
	void updateTspanX(int x);
	public void setShape(int x, int y);
	public double getTextWidth();
	public double getLastSpanWidth();
	public double getTextHeight();
	public void setFontFamily(String family);
	public String getFontFamily();
	void addText(JavaScriptObject tokens, int x, int width, boolean textAlignCenter, boolean textAlignRight);
	String getChildElements(int dx);
  void setProperties(
    int marginLeft,
    int marginTop,
    int marginBottom,
    int width
  );
  // Safari needs width and height for foreignobject
  // to show it on page
  void setShapeSize(int widht, int height);
  void setHorizontal(boolean horizontal);
  void setColor(Color color);
  void setBorderColor(Color color);
  void setShapeProperties(int properties);
}
