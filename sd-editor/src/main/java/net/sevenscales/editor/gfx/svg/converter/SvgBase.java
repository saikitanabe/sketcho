package net.sevenscales.editor.gfx.svg.converter;

import java.util.Map;

import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.api.impl.Theme;
import net.sevenscales.editor.api.impl.Theme.ThemeName;
import net.sevenscales.domain.utils.StringUtil;


class SvgBase {

	protected static final String STROKE_TEMPLATE = "%stroke%";
	protected static final String FILL_TEMPLATE = "%fill%";
	protected static final String TEXT_COLOR_TEMPLATE = "%textcolor%";

	private static void applyDefaultColors(Map<String,String> params, Diagram diagram) {
    // params.put("%fill%", fill);
    // params.put("%fill-opacity%", String.valueOf(rect.getFillColor().getOpacity()));
    if (params.get(STROKE_TEMPLATE) != null) {
	    params.put(STROKE_TEMPLATE, "rgb(" + Theme.getColorScheme(ThemeName.PAPER).getBorderColor().toRgb() + ")");
    }
		String fill = params.get(FILL_TEMPLATE);
    if (fill != null && !"none".equals(fill)) {
	    params.put(FILL_TEMPLATE, "rbg(" + Theme.getColorScheme(ThemeName.PAPER).getBorderColor().toRgb() + ")");
    }
	}

	public static String parse(String template, Map<String,String> params, Diagram diagram) {
		boolean usersDefaultColors = diagram.usesSchemeDefaultColors(Theme.getCurrentColorScheme());
		if (usersDefaultColors) {
			applyDefaultColors(params, diagram);
		}

    if (diagram.isTextElementBackgroundTransparent() || (usersDefaultColors && params.get(TEXT_COLOR_TEMPLATE) != null)) {
    	// if text background color is transparent then apply paper text color
    	params.put(TEXT_COLOR_TEMPLATE, "#" + Theme.getColorScheme(ThemeName.PAPER).getTextColor().toHexString());
    }

		return StringUtil.parse(template, params);
	}

	protected static String rgb(String rgbValue) {
		return "rgb(" + rgbValue + ")";
	}

}