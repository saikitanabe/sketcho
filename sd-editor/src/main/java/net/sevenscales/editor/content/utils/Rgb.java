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

	public static Rgb toRgb(String hexColor) {
		if (hexColor.equals("transparent")) {
			return new Rgb(0xff, 0xff, 0xff);
		}
		hexColor = parserHexColor(hexColor);
		int r = Integer.valueOf(hexColor.substring(0, 2), 16);
		int g = Integer.valueOf(hexColor.substring(2, 4), 16);
		int b = Integer.valueOf(hexColor.substring(4, 6), 16);

		if (hexColor.length() == 8) {
			int a = Integer.valueOf(hexColor.substring(6, 8), 16);
			return new Rgb(r, g, b, a);
		}

		return new Rgb(r, g, b);
	}

	public static String parserHexColor(String color) {
		if (color.startsWith("#")) {
			color = color.substring(1);
		}
		return color;
	}

	public static String makeRgba(int r, int g, int b, double a) {
		return "rgba(" + r + "," + g + "," + b + "," + a + ")";
	}

	public String toRgba() {
		return Rgb.makeRgba(red, green, blue, a);
	}

	public static String makeRgb(int r, int g, int b) {
		return "rgb(" + r + "," + g + "," + b + ")";
	}

	public String toRgb() {
		return makeRgb(red, green, blue);
	}
	
	@Override
	public String toString() {
		return toRgb();
	}
}
