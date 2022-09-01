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
		this.a = a;
	}

  public static Rgb parse(String color) throws Exception {
    Rgb result = null;
		if (color.startsWith("#")) {
			// in hex format IE8 at least
			result = Rgb.toRgba(color);
		} else {
      // assume rgb() format
      int r = red(color);
      int g = green(color);
      int b = blue(color);

      result = new Rgb(r, g, b);
    }

    return result;
  }

	public static Rgb toRgb(String hexColor) {
		if (hexColor.equals("transparent")) {
			return new Rgb(0xff, 0xff, 0xff);
		}
		hexColor = parserHexColor(hexColor);

		int r = 0;
		int g = 0;
		int b = 0;

    if (hexColor.length() == 3) {
      // short hand hex color code
      // #fc9, same as #ffcc99

      String rr = hexColor.substring(0, 1);
      r = Integer.valueOf(rr + rr, 16);

      String gg = hexColor.substring(1, 2);
      g = Integer.valueOf(gg + gg, 16);

      String bb = hexColor.substring(2, 3);
      b = Integer.valueOf(bb + bb, 16);
    } else {
      r = Integer.valueOf(hexColor.substring(0, 2), 16);
      g = Integer.valueOf(hexColor.substring(2, 4), 16);
      b = Integer.valueOf(hexColor.substring(4, 6), 16);
    }

		if (hexColor.length() == 8) {
      double a = hexToAlpha(hexColor.substring(6, 8));
			return new Rgb(r, g, b, a);
		}

		return new Rgb(r, g, b);
	}

  private static native double alphaToHex(double value)/*-{
    return $wnd.percentToHex(value * 100)
  }-*/;
  private static native double hexToAlpha(String hexAlpha)/*-{
    return $wnd.hexToAlpha(hexAlpha) / 100
  }-*/;

  public String toHex() {
    String result = "#" + 
      pad(Integer.toHexString(this.red)) +
      pad(Integer.toHexString(this.green)) +
      pad(Integer.toHexString(this.blue)) +
      alphaToHex(this.a);

    return result.toUpperCase();
  }

	private String pad(String hex) {
		String result = "0" + hex;
		return result.substring(result.length() - 2, result.length());
	}  

	public static Rgb toRgba(String hexColor) throws Exception {
    // if (hexColor.matches("^#{0,1}[0-9A-Fa-f]{0,8}$")) {
      return Rgb.toRgb(hexColor);
    // }

    // throw new Exception("invalid hex color: " + hexColor);
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

	private static native int red(String rgb)/*-{
		rgb = rgb.match(/^rgb\((\d+),\s*(\d+),\s*(\d+)\)$/);
		return parseInt(rgb[1]);
	}-*/;
	private static native int green(String rgb)/*-{
		rgb = rgb.match(/^rgb\((\d+),\s*(\d+),\s*(\d+)\)$/);
		return parseInt(rgb[2]);
	}-*/;
	private static native int blue(String rgb)/*-{
		rgb = rgb.match(/^rgb\((\d+),\s*(\d+),\s*(\d+)\)$/);
		return parseInt(rgb[3]);
	}-*/;

}
