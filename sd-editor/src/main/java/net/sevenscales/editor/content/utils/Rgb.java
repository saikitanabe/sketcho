package net.sevenscales.editor.content.utils;

public class Rgb {
	public int red;
	public int green;
	public int blue;
	public double a;

	public Rgb(int r, int g, int b) {
		red = r;
		green = g;
		blue = b;
		a = 1;
	}

	public Rgb(int r, int g, int b, double a) {
		red = r;
		green = g;
		blue = b;
		a = a;
	}

	public static String makeRgba(int r, int g, int b, double a) {
		return "rgba(" + r + "," + g + "," + b + "," + a + ")";
	}

	public String toRgba() {
		return Rgb.makeRgba(red, green, blue, a);
	}

	public String toRgb() {
		return "rgb(" + red + "," + green + "," + blue + ")";
	}
	
	@Override
	public String toString() {
		return toRgb();
	}
}
