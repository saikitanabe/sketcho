package net.sevenscales.editor.api;

import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;

import net.sevenscales.editor.api.impl.UnAttachedSurface;
import net.sevenscales.editor.content.UiModelContentHandler;
import net.sevenscales.editor.gfx.svg.converter.SvgConverter;
import net.sevenscales.editor.gfx.svg.converter.SvgData;
import net.sevenscales.domain.api.IDiagramContent;
import net.sevenscales.domain.api.IDiagramItem;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.JSONContentParser;
import net.sevenscales.domain.utils.SLogger;


public class SvgHandler {
	private static SLogger logger = SLogger.createLogger(SvgHandler.class);
	static {
		SLogger.addFilter(SvgHandler.class);
	}

	private String svg;

	public SvgHandler(JavaScriptObject json) {
		EditorContext editorContext = new EditorContext();
    UiModelContentHandler modelHandler = new UiModelContentHandler(editorContext);
    JSONObject obj = new JSONObject(json);
    ISurfaceHandler surface = new UnAttachedSurface(editorContext);
    if (obj.isObject() != null) {
	    JSONContentParser parser = new JSONContentParser(obj);
	    IDiagramContent content = parser.toDTO();
	    modelHandler.addContentItems(content, surface);
	    SvgConverter converter = new SvgConverter(false);
			SvgData data = converter.convertToSvg(content, surface, false);
			this.svg = data.svg;
    }
	}

	public String getSvg() {
		return svg;
	}
}