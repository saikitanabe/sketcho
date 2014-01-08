package net.sevenscales.editor.gfx.svg.converter;

import java.util.Map;

import net.sevenscales.editor.api.impl.Theme;
import net.sevenscales.editor.api.impl.Theme.ThemeName;
import net.sevenscales.domain.utils.StringUtil;


class SvgBase {

	protected static final String STROKE_TEMPLATE = "%stroke%";
	protected static final String FILL_TEMPLATE = "%fill%";
	protected static final String TEXT_COLOR_TEMPLATE = "%textcolor%";

	private static void applyDefaultColors(Map<String,String> params) {
    // params.put("%fill%", fill);
    // params.put("%fill-opacity%", String.valueOf(rect.getFillColor().getOpacity()));
    if (params.get(STROKE_TEMPLATE) != null) {
	    params.put(STROKE_TEMPLATE, "rgb(" + Theme.getColorScheme(ThemeName.PAPER).getBorderColor().toRgb() + ")");
    }

    if (params.get(FILL_TEMPLATE) != null) {
	    params.put(FILL_TEMPLATE, "rbg(" + Theme.getColorScheme(ThemeName.PAPER).getBorderColor().toRgb() + ")");
    }
    
    if (params.get(TEXT_COLOR_TEMPLATE) != null) {
    	params.put(TEXT_COLOR_TEMPLATE, "#" + Theme.getColorScheme(ThemeName.PAPER).getTextColor().toHexString());
    }
	}

	public static String parse(String template, Map<String,String> params, boolean usesSchemeDefaultColors) {
		if (usesSchemeDefaultColors) {
			applyDefaultColors(params);
		}
		return StringUtil.parse(template, params);
	}

	protected static String rgb(String rgbValue) {
		return "rgb(" + rgbValue + ")";
	}

}