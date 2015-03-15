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
		if (color != null && color.equals(Theme.getCurrentThemeName().getBoardBackgroundColor().toHexStringWithHash())) {
			return true;
		}
		return false;
	}

	private static void applyDefaultBackgroundColors(Map<String, String> params) {
		String fill = params.get(FILL_TEMPLATE);
		if (fill != null && fill.equals("#"+ Theme.getCurrentColorScheme().getBorderColor().toHexString())) {
			// if fill is scheme border color then switch to paper
			params.put(FILL_TEMPLATE, "#" + Theme.getColorScheme(ThemeName.PAPER).getBorderColor().toHexString());
		} else if (isThemeBackgroundColor(fill)) {
			// if fill is theme background then use paper background
			// used in arrows to hide line, e.g. in inheritance
			params.put(FILL_TEMPLATE, ThemeName.WHITE.getBoardBackgroundColor().toHexStringWithHash());
		}
	}

	private static void applyDefaultBorderColors(Map<String,String> params) {
    // params.put("%fill%", fill);
    // params.put("%fill-opacity%", String.valueOf(rect.getFillColor().getOpacity()));
    if (params.get(STROKE_TEMPLATE) != null && !"none".equals(params.get(STROKE_TEMPLATE))) {
	    // params.put(STROKE_TEMPLATE, "rgb(" + Theme.getColorScheme(ThemeName.PAPER).getBorderColor().toRgb() + ")");
	    // batik doesn't support functions in style
	    params.put(STROKE_TEMPLATE, "#" + Theme.getColorScheme(ThemeName.PAPER).getBorderColor().toHexString());
    }
	}

	private static void checkToApplyColor(IShape shape, String template, Map<String,String> params, Diagram diagram) {
		if (!shape.isThemeSupported()) {
			// this shape doesn't support theme colors and is constant
			return;
		}

		boolean usesSchemeDefaultTextColor = diagram.usesSchemeDefaultTextColor(Theme.getCurrentColorScheme());
    if (usesSchemeDefaultTextColor && params.get(TEXT_COLOR_TEMPLATE)!= null) {
    	// if text background color is transparent then apply paper text color
    	params.put(TEXT_COLOR_TEMPLATE, "#" + Theme.getColorScheme(ThemeName.PAPER).getTextColor().toHexString());
    }

		if (diagram.usesSchemeDefaultBorderColor(Theme.getCurrentColorScheme()) && !diagram.isAnnotation()) {
			applyDefaultBorderColors(params);	
		}

		if (diagram.usesSchemeDefaultBackgroundColor(Theme.getCurrentColorScheme())) {
			applyDefaultBackgroundColors(params);
		}

		if (shape.isFillAsBorderColor() && diagram.usesSchemeDefaultBorderColor(Theme.getCurrentColorScheme())) {
			params.put(FILL_TEMPLATE, Theme.getColorScheme(ThemeName.PAPER).getBorderColor().toHexStringWithHash());
		} else if (shape.isFillAsBoardBackgroundColor()) {
			params.put(FILL_TEMPLATE, ThemeName.PAPER.getBoardBackgroundColor().toHexStringWithHash());
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