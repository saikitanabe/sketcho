package net.sevenscales.editor.api.impl;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.ElementColor;

public class Theme {
  // ST 16.11.2018: if border color has been saved in this color
  // it is just a storage format border color and real color
  // should be calculated based on theme and background color.
  public static final Color THEME_BORDER_COLOR_STORAGE = new Color(0x1, 0x1, 0x1, 0);

  private static final String THEME_PREFIX = "theme-";
	private static Theme instance;
	
  private ThemeName currentThemeName;
	private ElementColorScheme currentColorScheme;
  private ElementColorScheme commentColorScheme;
  private ElementColorScheme commentThreadColorScheme;

  public enum ThemeName {
    WHITE("white", new Color(0xff, 0xff, 0xff, 1)), 
    BLACK("black", new Color(0x27, 0x28, 0x22, 1)),
    SEPIA("sepia", new Color(0xFB, 0xF0, 0xD9, 1)), 
    PAPER("paper", new Color(0xFA, 0xFA, 0xFA, 1)), 
    GRID("grid", new Color(0xEC, 0xE8, 0xE6, 1));
    
    private String name;
    private Color boardBackgroundColor;
    private ThemeName(String name, Color boardBackgroundColor) {
      this.name = name;
      this.boardBackgroundColor = boardBackgroundColor;
    }
    
    public String getName() {
      return name;
    }

    public Color getBoardBackgroundColor() {
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
    private ThemeName themeName;

    public ElementColorScheme(Color textColor, Color borderColor, Color backgroundColor, ThemeName themeName) {
      super();
      this.textColor = textColor;
      this.borderColor = borderColor;
      this.backgroundColor = backgroundColor;
      this.themeName = themeName;
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
    public Color getBoardBackgroundColor() {
      return themeName.getBoardBackgroundColor();
    }
    public JSONObject toJson() {
      JSONObject result = new JSONObject();
      result.put("background_color", new JSONString(backgroundColor.toHexStringWithHash()));
      result.put("border_color", new JSONString(borderColor.toHexStringWithHash()));
      result.put("text_color", new JSONString(textColor.toHexStringWithHash()));
      return result;
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
                                                                     createDefaultBackgroundColor(),
                                                                     ThemeName.WHITE));
    defaultColorMap.put(ThemeName.BLACK.name, new ElementColorScheme(createDefaultTextColorOnBlack(),
                                                                     createDefaultTextColorOnBlack(),
                                                                     createDefaultBackgroundColor(),
                                                                     ThemeName.BLACK));
    defaultColorMap.put(ThemeName.PAPER.name, new ElementColorScheme(createDefaultTextColorOnWhite(),
                                                                     createDefaultBorderColorOnWhite(),
                                                                     createDefaultBackgroundColor(),
                                                                     ThemeName.PAPER));
    defaultColorMap.put(ThemeName.SEPIA.name, new ElementColorScheme(createDefaultTextColorOnSepia(), 
                                                                     createDefaultBorderColorOnSepia(), 
                                                                     createDefaultBackgroundColor(),
                                                                     ThemeName.SEPIA));
    defaultColorMap.put(ThemeName.GRID.name, new ElementColorScheme(createDefaultTextColorOnWhite(),
                                                                     createDefaultBorderColorOnWhite(),
                                                                     createDefaultBackgroundColor(),
                                                                     ThemeName.GRID));

    // comments don't have specific background!
    commentColorScheme = new ElementColorScheme(createDefaultCommentTextColor(),
                                                createDefaultCommentBorderColor(),
                                                createDefaultCommentBackgroundColor(),
                                                ThemeName.PAPER);
    commentThreadColorScheme = new ElementColorScheme(createDefaultCommentThreadTextColor(),
                                                      createDefaultCommentThreadBorderColor(),
                                                      createDefaultCommentThreadBackgroundColor(),
                                                      ThemeName.PAPER);

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
    return new Color(0xE8, 0xE8, 0xE9, 1);
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

  private static Color createDefaultCommentTextColor() {
    return new Color(0x44, 0x44, 0x44, 1);
  }
  private static Color createDefaultCommentBorderColor() {
    return new Color(255,102,102,0);
  }
  private static Color createDefaultCommentBackgroundColor() {
    return new Color(255,102,102,0);
  }

  private static Color createDefaultCommentThreadTextColor() {
    return new Color(0x44, 0x44, 0x44, 1);
  }
  private static Color createDefaultCommentThreadBorderColor() {
    return new Color(255,102,102,0);
  }
  private static Color createDefaultCommentThreadBackgroundColor() {
    return new Color(255,102,102,0.85);
    // return new Color(0xF0, 0xF0, 0xF0, 1.0);
    // return new Color(0x60, 0xC3, 0x32, 1.0);
    // return new Color(0xF0, 0x60, 0x4C, 1.0);
  }
	
	public static ElementColor defaultColor() {
		Color background = createDefaultBackgroundColor();
		Color border = createDefaultBorderColor();
		Color text = createDefaultTextColor();
		return new ElementColor(text, border, background);
	}

  public static ElementColor defaultCommentColor() {
    Color background = createDefaultCommentBackgroundColor();
    Color border = createDefaultCommentBorderColor();
    Color text = createDefaultCommentTextColor();
    return new ElementColor(text, border, background);
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
	public static JavaScriptObject getCurrentColorSchemeAsJson() {
		return instance().currentColorScheme.toJson().getJavaScriptObject();
	}

  public static ElementColorScheme getCommentColorScheme() {
    return instance().commentColorScheme;
  }

  public static ElementColorScheme getCommentThreadColorScheme() {
    return instance().commentThreadColorScheme;
  }

  public static ElementColorScheme getColorScheme(ThemeName themeName) {
    return instance().defaultColorMap.get(themeName.name);
  }
  
  public static String themeCssClass() {
    return THEME_PREFIX + getCurrentThemeName().name;
  }

  public static boolean isBlackTheme() {
    return Theme.ThemeName.BLACK.equals(Theme.getCurrentThemeName());
  }

  public static boolean isThemeBorderColor(Color color) {
    return THEME_BORDER_COLOR_STORAGE.equals(color);
  }

}
