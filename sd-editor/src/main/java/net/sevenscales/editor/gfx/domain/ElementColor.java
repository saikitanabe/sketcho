package net.sevenscales.editor.gfx.domain;

public class ElementColor {
	public Color textColor;
	public Color borderColor;
	public Color backgroundColor;
	
	public ElementColor() {
	}

	public ElementColor(Color textColor, Color borderColor, Color backgroundColor) {
		this.textColor = textColor;
		this.borderColor = borderColor;
		this.backgroundColor = backgroundColor;
	}

	public void setTextColor(Color textColor)	{
		this.textColor = textColor;
	}
	public Color getTextColor() {
		return textColor;
	}

	public void setBorderColor(Color borderColor)	{
		this.borderColor = borderColor;
	}
	public Color getBorderColor() {
		return borderColor;
	}

	public void setBackgroundColor(Color backgroundColor)	{
		this.backgroundColor = backgroundColor;
	}
	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void copy(ElementColor elementColor) {
		elementColor.textColor.copy(elementColor.textColor);
		elementColor.borderColor.copy(elementColor.borderColor);
		elementColor.backgroundColor.copy(elementColor.backgroundColor);
	}

}
