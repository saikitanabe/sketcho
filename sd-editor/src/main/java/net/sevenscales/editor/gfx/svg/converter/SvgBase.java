package net.sevenscales.editor.gfx.svg.converter;

import java.util.Map;

import net.sevenscales.editor.gfx.domain.IShape;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.api.impl.Theme;
import net.sevenscales.editor.api.impl.Theme.ThemeName;
import net.sevenscales.domain.utils.StringUtil;


class SvgBase {

	protected static final String STROKE_TEMPLATE = "%stroke%";
	protected static final String FILL_TEMPLATE = "%fill%";
	protected static final String TEXT_COLOR_TEMPLATE = "%textcolor%";

	/**
	* Relationships use background color as transparent color to hide line.
	*/
	private static boolean isThemeBackgroundColor(String color) {
		if (color != null && color.equals(Theme.getCurrentThemeName().getBoardBackgroundColor())) {
			return true;
		}
		return false;
	}

	private static void applyDefaultColors(Map<String,String> params, Diagram diagram) {
    // params.put("%fill%", fill);
    // params.put("%fill-opacity%", String.valueOf(rect.getFillColor().getOpacity()));
    if (params.get(STROKE_TEMPLATE) != null) {
	    // params.put(STROKE_TEMPLATE, "rgb(" + Theme.getColorScheme(ThemeName.PAPER).getBorderColor().toRgb() + ")");
	    // batik doesn't support functions in style
	    params.put(STROKE_TEMPLATE, "#" + Theme.getColorScheme(ThemeName.PAPER).getBorderColor().toHexString());
    }
    applyDefaultFillColors(params);
	}

	private static void applyDefaultFillColors(Map<String, String> params) {
		String fill = params.get(FILL_TEMPLATE);
		boolean themeBgcolor = isThemeBackgroundColor(fill);

		if (fill != null && fill.equals("#"+ Theme.getCurrentColorScheme().getBorderColor().toHexString())) {
			params.put(FILL_TEMPLATE, "#" + Theme.getColorScheme(ThemeName.PAPER).getBorderColor().toHexString());
		} else if (isThemeBackgroundColor(fill)) {
			params.put(FILL_TEMPLATE, ThemeName.PAPER.getBoardBackgroundColor());
		}

    // if (fill != null && !"none".equals(fill) || themeBgcolor) {
	   //  // params.put(FILL_TEMPLATE, "rbg(" + Theme.getColorScheme(ThemeName.PAPER).getBorderColor().toRgb() + ")");
	   //  // Batik doesn't support functions!
	   //  if (themeBgcolor) {
		  //   params.put(FILL_TEMPLATE, ThemeName.PAPER.getBoardBackgroundColor());
	   //  } else if () {
		  //   params.put(FILL_TEMPLATE, "#" + Theme.getColorScheme(ThemeName.PAPER).getBorderColor().toHexString());
	   //  }
    // }
	}

	private static void checkToApplyColor(IShape shape, String template, Map<String,String> params, Diagram diagram) {
		if (!shape.isThemeSupported()) {
			// this shape doesn't support theme colors and is constant
			return;
		}

		boolean usersDefaultColors = diagram.usesSchemeDefaultColors(Theme.getCurrentColorScheme());
		if (usersDefaultColors) {
			applyDefaultColors(params, diagram);
		} else {
			applyDefaultFillColors(params);
		}

    if (diagram.isTextElementBackgroundTransparent() || (usersDefaultColors && params.get(TEXT_COLOR_TEMPLATE) != null)) {
    	// if text background color is transparent then apply paper text color
    	params.put(TEXT_COLOR_TEMPLATE, "#" + Theme.getColorScheme(ThemeName.PAPER).getTextColor().toHexString());
    }
	}

	public static String parse(IShape shape, String template, Map<String,String> params, Diagram diagram) {
		checkToApplyColor(shape, template, params, diagram);
		return StringUtil.parse(template, params);
	}

	protected static String rgb(String rgbValue) {
		return "rgb(" + rgbValue + ")";
	}

	protected static String hex(String value) {
		return "#" + value;
	}

}