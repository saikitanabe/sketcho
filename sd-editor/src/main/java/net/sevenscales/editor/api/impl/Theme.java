package net.sevenscales.editor.api.impl;

import java.util.HashMap;
import java.util.Map;

import net.sevenscales.editor.gfx.domain.Color;

public class Theme {
  private static final String THEME_PREFIX = "theme-";
	private static Theme instance;
	
  private ThemeName currentThemeName;
	private ElementColorScheme currentColorScheme;

  public enum ThemeName {
    WHITE("white", "#fff"), BLACK("black", "#272822"), SEPIA("sepia", "#FBF0D9"), PAPER("paper", "#FAFAFA");
    
    private String name;
    private String boardBackgroundColor;
    private ThemeName(String name, String boardBackgroundColor) {
      this.name = name;
      this.boardBackgroundColor = boardBackgroundColor;
    }
    
    public String getName() {
      return name;
    }

    public String getBoardBackgroundColor() {
      return boardBackgroundColor;
    }
    
    public static ThemeName getEnum(String operation) {
      if (operation == null) {
        throw new IllegalArgumentException();
      }
      
      for (ThemeName v : values()) {
        if (operation.equalsIgnoreCase(v.name)) return v;
      }
      throw new IllegalArgumentException();
    }
  }

  // maps background color to default colors: text, border and background
  // by default background opacity is 0 and text and border are the same
  public static class ElementColorScheme {
    private Color textColor;
    private Color borderColor;
    private Color backgroundColor;
    public ElementColorScheme(Color textColor, Color borderColor,
        Color backgroundColor) {
      super();
      this.textColor = textColor;
      this.borderColor = borderColor;
      this.backgroundColor = backgroundColor;
    }
    public Color getTextColor() {
      return textColor;
    }
    public Color getBorderColor() {
      return borderColor;
    }
    public Color getBackgroundColor() {
      return backgroundColor;
    }
    
    @Override
    public boolean equals(Object obj) {
      if (obj instanceof ElementColorScheme) {
        ElementColorScheme o = (ElementColorScheme) obj;
        boolean result = true;
        if (!textColor.equals(o.textColor)) {
          result = false; 
        }
        if (!borderColor.equals(o.borderColor)) {
          result = false;
        }
        if (!backgroundColor.equals(o.backgroundColor)) {
          result = false;
        }
        return result;
      }
      return false;
    }
  }
  private Map<String,ElementColorScheme> defaultColorMap;

  public static Theme instance() {
  	if (instance == null) {
  		instance = new Theme();
  	}
  	return instance;
  }
  
  private Theme() {
    defaultColorMap = new HashMap<String, ElementColorScheme>();
    defaultColorMap.put(ThemeName.WHITE.name, new ElementColorScheme(createDefaultTextColorOnWhite(),
                                                                     createDefaultBorderColorOnWhite(),
                                                                     createDefaultBackgroundColor()));
    defaultColorMap.put(ThemeName.BLACK.name, new ElementColorScheme(createDefaultTextColorOnBlack(),
                                                                     createDefaultTextColorOnBlack(),
                                                                     createDefaultBackgroundColor()));
    defaultColorMap.put(ThemeName.PAPER.name, new ElementColorScheme(createDefaultTextColorOnWhite(),
                                                                     createDefaultBorderColorOnWhite(),
                                                                     createDefaultBackgroundColor()));
    defaultColorMap.put(ThemeName.SEPIA.name, new ElementColorScheme(createDefaultTextColorOnSepia(), 
                                                                     createDefaultBorderColorOnSepia(), 
                                                                     createDefaultBackgroundColor()));

    currentThemeName = ThemeName.PAPER;
    currentColorScheme = defaultColorMap.get(currentThemeName.name);
	}
  
  private static Color createDefaultTextColorOnSepia() {
    return new Color(0x5F, 0x4B, 0x32, 1);
  }

  private static Color createDefaultBorderColorOnSepia() {
    return new Color(0x5F, 0x4B, 0x32, 1);
  }

  private static Color createDefaultTextColorOnBlack() {
    return new Color(0xF8, 0xF8, 0xF2, 1);
  }

  private static Color createDefaultTextColorOnWhite() {
    return new Color(0x33, 0x33, 0x33, 1);
  }

  private static Color createDefaultBorderColorOnWhite() {
    return new Color(0x33, 0x33, 0x33, 1);
  }

	public static Color createDefaultBorderColor() {
		return new Color(instance().currentColorScheme.getBorderColor());
	}
	
	public static Color createDefaultBackgroundColor() {
		return new Color(0xcc, 0xcc, 0xff, 0);
	}
	
	public static Color createDefaultTextColor() {
		return new Color(instance().currentColorScheme.getTextColor());
	}
	
	public static net.sevenscales.editor.diagram.utils.Color defaultColor() {
		Color background = createDefaultBackgroundColor();
		Color border = createDefaultBorderColor();
		Color text = createDefaultTextColor();
		return new net.sevenscales.editor.diagram.utils.Color(text.toHexString(), text.red, text.green, text.blue,
				background.toHexString(), background.red, background.green, background.blue,
				border.toHexString(), border.red, border.green, border.blue, 0);
	}

	public static void setColorScheme(String colorName) {
		instance()._setColorScheme(colorName);
	}

	private void _setColorScheme(String colorName) {
    currentThemeName = ThemeName.getEnum(colorName);
    currentColorScheme = defaultColorMap.get(currentThemeName.name);
	}

	public static ThemeName getCurrentThemeName() {
		return instance().currentThemeName;
	}
	
	public static ElementColorScheme getCurrentColorScheme() {
		return instance().currentColorScheme;
	}

  public static ElementColorScheme getColorScheme(ThemeName themeName) {
    return instance().defaultColorMap.get(themeName.name);
  }
  
  public static String themeCssClass() {
    return THEME_PREFIX + getCurrentThemeName().name;
  }

}
