package net.sevenscales.editor.gfx.domain;

public class Color {
  public int red;
  public int green;
  public int blue;
  public double opacity;
  
  public Color(int red, int green, int blue, double opacity) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.opacity = opacity;
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
	
	public void copy(Color color) {
	  red = color.red;
    green = color.green;
    blue = color.blue;
    opacity = color.opacity;
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
	    return true;
	  }
	  return false;
	}

}
