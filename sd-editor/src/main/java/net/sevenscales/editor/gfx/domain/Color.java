package net.sevenscales.editor.gfx.domain;

import com.google.gwt.core.client.JsArrayInteger;
import net.sevenscales.editor.content.utils.Rgb;

public class Color {
  public int red;
  public int green;
  public int blue;
  public double opacity;
  public String gradient;
  
  public Color(int red, int green, int blue, double opacity) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.opacity = opacity;
  }
  
  public Color(String gradient) {
    this.gradient = gradient;
	}

	public Color() {
	}

	public Color(Color color) {
	  copy(color);
	}

	public Color(String[] backgroundColor) {
		red = Integer.valueOf(backgroundColor[0]); 
		green = Integer.valueOf(backgroundColor[1]); 
		blue = Integer.valueOf(backgroundColor[2]); 
		opacity = Double.valueOf(backgroundColor[3]);
	}

  public static Color hexToColor(String color) {
    if (color.startsWith("url")) {
      return new Color(color);
    }

		Rgb rgb = Rgb.toRgb(color);
		Color result = new Color(rgb.red, rgb.green, rgb.blue, rgb.a);
		return result;
  }

	public Color toLighterLess() {
		JsArrayInteger rgb = tsRgbToLighter(red, green, blue, 5);
		return copyRgbColor(rgb);
	}

	public Color toLighter() {
		JsArrayInteger rgb = tsRgbToLighter(red, green, blue, 20);
		return copyRgbColor(rgb);
	}

	private Color copyRgbColor(JsArrayInteger rgb) {
		Color result = create();
		result.red = rgb.get(0);
		result.green = rgb.get(1);
		result.blue = rgb.get(2);

		return result;
	}

	public Color toDarker() {
		JsArrayInteger rgb = tsRgbToDarker(red, green, blue);

		Color result = create();
		result.red = rgb.get(0);
		result.green = rgb.get(1);
		result.blue = rgb.get(2);

		return result;
	}

	private native JsArrayInteger tsRgbToLighter(int red, int green, int blue, int lighter)/*-{
		return $wnd.tsRgbToLighter(red, green, blue, lighter);
	}-*/;
	private native JsArrayInteger tsRgbToDarker(int red, int green, int blue)/*-{
		return $wnd.tsRgbToDarker(red, green, blue);
	}-*/;
	
	public void copy(Color color) {
	  red = color.red;
    green = color.green;
    blue = color.blue;
    opacity = color.opacity;
    gradient = color.gradient;
	}
	
	public Color create() {
	  return new Color(this);
	}

	@Override
  public String toString() {
    return "Color [blue=" + blue + ", green=" + green + ", opacity=" + opacity
        + ", red=" + red + "]";
  }

  public String toRgb() {
    return red + "," + green + "," + blue;
  }

  public String toRgbWithOpacity() {
    return red+","+green+","+blue+","+opacity;
  }

  public String toRgbCss() {
  	return net.sevenscales.editor.content.utils.Rgb.makeRgb(red, green, blue);
  }

  public String toRgbaCss() {
  	return net.sevenscales.editor.content.utils.Rgb.makeRgba(red, green, blue, opacity);
  }

	public String toHexString() {
		String red = Integer.toHexString(this.red);
		String green = Integer.toHexString(this.green);
		String blue = Integer.toHexString(this.blue);
		return pad(red) + pad(green) + pad(blue);
	}

	public String toHexStringWithHash() {
		return "#" + toHexString();
	}

	private String pad(String hex) {
		String result = "0" + hex;
		return result.substring(result.length() - 2, result.length());
	}
	
	public double getOpacity() {
		return opacity;
	}
	public void setOpacity(double opacity) {
		this.opacity = opacity;
  }
  
  public boolean isGradient() {
    return gradient != null && gradient.length() > 0;
  }
	
	@Override
	public boolean equals(Object obj) {
	  if (obj instanceof Color) {
	    Color c = (Color) obj;
	    if (c.red != red) {
	      return false;
	    }
	    if (c.green != green) {
	      return false;
	    }
	    if (c.blue != blue) {
	      return false;
	    }
	    if (c.opacity != opacity) {
	      return false;
      }
      if (c.gradient == null && gradient != null) {
        return false;
      }
      if (c.gradient != null && gradient == null) {
        return false;
      }
      if (c.gradient != null && !c.gradient.equals(gradient)) {
        return false;
      }

	    return true;
	  }
	  return false;
	}

}
