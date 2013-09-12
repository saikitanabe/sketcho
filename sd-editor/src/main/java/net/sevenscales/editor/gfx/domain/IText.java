package net.sevenscales.editor.gfx.domain;

import com.google.gwt.core.client.JavaScriptObject;

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
	public void setText(String text);
	public String getText();
	public void setShape(int x, int y);
	public double getTextWidth();
	public double getLastSpanWidth();
	public double getTextHeight();
	public void setFontFamily(String family);
	public String getFontFamily();
	/**
	 * 
	 * @param text
	 * @param fontWeight
	 * @param firstInsert needed to position first word with same indent as when using new line
	 * @param newline
	 * @param x
	 * @param width
	 */
	void addText(String text, boolean fontWeight, boolean firstInsert, boolean newline, int x, int width);
	void addText(JavaScriptObject tokens, int x, int width);
	String getChildElements(int dx);
	void removeLastSpan();
}
