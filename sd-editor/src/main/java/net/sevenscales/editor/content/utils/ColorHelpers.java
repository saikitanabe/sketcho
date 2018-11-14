package net.sevenscales.editor.content.utils;

import net.sevenscales.editor.gfx.domain.Color;

import com.google.gwt.core.client.JavaScriptObject;

public class ColorHelpers {
	
	public static String asHexColor(int red, int green, int blue) {
		String r = Integer.toHexString(0x100 | red).substring(1).toUpperCase();
		String g = Integer.toHexString(0x100 | green).substring(1).toUpperCase();
		String b = Integer.toHexString(0x100 | blue).substring(1).toUpperCase();
		return r + g + b;
	}

	public static Color createBorderColor(Color color) {
		return borderColorByBackground(color.red, color.green, color.blue);
	}
	
	public static Color createBorderColor(String color) {
		if (color.equals("transparent")) {
			return new Color(0x55, 0x55, 0x55, 1);
		}
		Rgb rgb = Rgb.toRgb(color);
		return borderColorByBackground(rgb.red, rgb.green, rgb.blue);
	}
	
	public static String createOppositeColor(String color) {
		Rgb rgb = Rgb.toRgb(color);
		String result = "#444444";
		if (!"transparent".equals(color) && isRgbWhite(rgb.toString())) {
			result = "#dddddd";
		}
		return result;
	}

	public static boolean isHexBlack(String hexColor) {
		return !isHexWhite(hexColor);
	}

	public static boolean isRgbBlack(String rgb) {
		return !isRgbWhite(rgb);
	}

	public static boolean isRgbBlack(int r, int g, int b) {
		return !isRgbWhite(r, g, b);
	}

	public static boolean isHexWhite(String hexColor) {
		Rgb rgb = Rgb.toRgb(hexColor);
		return isRgbWhite(rgb.red, rgb.green, rgb.blue);
	}
	
	public static native boolean isRgbWhite(String rgb)/*-{
		rgb = rgb.match(/^rgb\((\d+),\s*(\d+),\s*(\d+)\)$/);
		return (((parseInt(rgb[1]) + parseInt(rgb[2]) + parseInt(rgb[3])) / 3) > 128) ? true : false;
	}-*/;

	public static native boolean isRgbWhite(int r, int g, int b)/*-{
		return (((r + g + b) / 3) > 128) ? true : false;
	}-*/;

	public static Color borderColorByBackground(int red, int green, int blue) {
		JavaScriptObject hsv = rgbToHsv(red, green, blue);
		JavaScriptObject rgb = hsv2rgb(getIntValue(hsv, 0), getIntValue(hsv, 1),
				getIntValue(hsv, 2) - 10);

		return new Color(getIntValue(rgb, 0), getIntValue(rgb, 1), getIntValue(rgb, 2), 1);
	}

	private static native JavaScriptObject rgbToHsv(int r, int g, int b)/*-{
		function rgbToHsl(r, g, b) {
			r /= 255, g /= 255, b /= 255;
			var max = Math.max(r, g, b), min = Math.min(r, g, b);
			var h, s, l = (max + min) / 2;

			if (max == min) {
				h = s = 0; // achromatic
			} else {
				var d = max - min;
				s = l > 0.5 ? d / (2 - max - min) : d / (max + min);
				switch (max) {
				case r:
					h = (g - b) / d + (g < b ? 6 : 0);
					break;
				case g:
					h = (b - r) / d + 2;
					break;
				case b:
					h = (r - g) / d + 4;
					break;
				}
				h /= 6;
			}

			return [ Math.floor(h * 360), Math.floor(s * 100), Math.floor(l * 100) ];
		}

		function rgbToHsv(r, g, b) {
			var min = Math.min(r, g, b), max = Math.max(r, g, b), delta = max - min, h, s, v = max;

			v = Math.floor(max / 255 * 100);
			if (max != 0)
				s = Math.floor(delta / max * 100);
			else {
				// black
				return [ 0, 0, 0 ];
			}

			if (r == max)
				h = (g - b) / delta; // between yellow & magenta
			else if (g == max)
				h = 2 + (b - r) / delta; // between cyan & yellow
			else
				h = 4 + (r - g) / delta; // between magenta & cyan

			h = Math.floor(h * 60); // degrees
			if (h < 0)
				h += 360;

			return [ h, s, v ];
		}

		return rgbToHsv(r, g, b);
	}-*/;

	public native static JavaScriptObject hsv2rgb(int h, int s, int v)/*-{
		function HSV2RGB(HSV, RGB) {
			var h = HSV.h / 360;
			var s = HSV.s / 100;
			var v = HSV.v / 100;
			if (s == 0) {
				RGB.r = v * 255;
				RGB.g = v * 255;
				RGB.b = v * 255;
			} else {
				var_h = h * 6;
				var_i = Math.floor(var_h);
				var_1 = v * (1 - s);
				var_2 = v * (1 - s * (var_h - var_i));
				var_3 = v * (1 - s * (1 - (var_h - var_i)));

				if (var_i == 0) {
					var_r = v;
					var_g = var_3;
					var_b = var_1
				} else if (var_i == 1) {
					var_r = var_2;
					var_g = v;
					var_b = var_1
				} else if (var_i == 2) {
					var_r = var_1;
					var_g = v;
					var_b = var_3
				} else if (var_i == 3) {
					var_r = var_1;
					var_g = var_2;
					var_b = v
				} else if (var_i == 4) {
					var_r = var_3;
					var_g = var_1;
					var_b = v
				} else {
					var_r = v;
					var_g = var_1;
					var_b = var_2
				}
				;

				RGB.r = var_r * 255;
				RGB.g = var_g * 255;
				RGB.b = var_b * 255;
			}
		}
		var hsv = {
			h : h,
			s : s,
			v : v
		};
		var rgb = {};
		HSV2RGB(hsv, rgb);
		return [ rgb.r, rgb.g, rgb.b ];
	}-*/;

	private static native int getIntValue(JavaScriptObject values, int index) /*-{
		if (values.length > index && !isNaN(values[index])) {
			return parseInt(values[index]);
		}
		return 0;
	}-*/;

}
