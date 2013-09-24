package net.sevenscales.editor.diagram.utils;

import net.sevenscales.editor.content.utils.ColorHelpers;

public class Color {
	private String textColor;
	private int r;
	private int b;
	private int g;
	private String backgroundColor;
	private int rr;
	private int bb;
	private int gg;
	private double opacity;
	private int borR;
	private int borG;
	private int borB;
	private String borderColor;
	
	@Override
	public String toString() {
		return "Color [textColor=" + textColor + ", r=" + r + ", b=" + b + ", g="
				+ g + ", backgroundColor=" + backgroundColor + ", rr=" + rr + ", bb="
				+ bb + ", gg=" + gg + ", opacity=" + opacity + ", borR=" + borR
				+ ", borG=" + borG + ", borB=" + borB + ", borderColor=" + borderColor
				+ "]";
	}

	public Color() {
	}

	public Color(String textColor, int r, int g, int b, String backgroundColor, int rr, int gg, int bb, String borderColor, int borR, int borG, int borB, double opacity) {
		this.textColor = textColor;
		this.r = r;
		this.g = g;
		this.b = b;
		
		this.backgroundColor = backgroundColor;
		this.rr = rr;
		this.gg = gg;
		this.bb = bb;
		
		this.borderColor = ColorHelpers.parserHexColor(borderColor);
		this.borR = borR;
		this.borG = borG;
		this.borB = borB;
		
		this.opacity = opacity;
	}

	public String getTextColor() {
		return textColor;
	}
	public String getTextColor4Web() {
		return "#" + textColor;
	}

	public void setTextColor(String textColor) {
		this.textColor = textColor;
	}

	public int getR() {
		return r;
	}

	public void setR(int r) {
		this.r = r;
	}

	public int getB() {
		return b;
	}

	public void setB(int b) {
		this.b = b;
	}

	public int getG() {
		return g;
	}

	public void setG(int g) {
		this.g = g;
	}

	public String getBackgroundColor() {
		return backgroundColor;
	}
	public String getBackgroundColor4Web() {
		if (backgroundColor.equals("transparent")) {
			return backgroundColor;
		}
		return "#" + backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public int getRr() {
		return rr;
	}

	public void setRr(int rr) {
		this.rr = rr;
	}

	public int getBb() {
		return bb;
	}

	public void setBb(int bb) {
		this.bb = bb;
	}

	public int getGg() {
		return gg;
	}

	public void setGg(int gg) {
		this.gg = gg;
	}

	public String getBorderColor4Web() {
		return "#" + borderColor;
	}
	public void setBorderColor(String borderColor) {
		this.borderColor = ColorHelpers.parserHexColor(borderColor);
	}
	public String getBorderColor() {
		return borderColor;
	}
	public void setBorR(int borR) {
		this.borR = borR;
	}
	public void setBorB(int borB) {
		this.borB = borB;
	}
	public void setBorG(int borG) {
		this.borG = borG;
	}
	public int getBorR() {
		return borR;
	}
	public int getBorG() {
		return borG;
	}
	public int getBorB() {
		return borB;
	}

	public void setOpacity(double opacity) {
		this.opacity = opacity;
	}

	public double getOpacity() {
		return opacity;
	}
	
}
