package net.sevenscales.editor.api;

import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.dom.client.Style.Visibility;

import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.impl.UnAttachedSurface;
import net.sevenscales.editor.content.UiModelContentHandler;
import net.sevenscales.editor.gfx.svg.converter.SvgConverter;
import net.sevenscales.editor.gfx.svg.converter.SvgData;
import net.sevenscales.domain.api.IDiagramContent;
import net.sevenscales.domain.api.IDiagramItem;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.JSONContentParser;
import net.sevenscales.editor.gfx.domain.ILoadObserver;
import net.sevenscales.domain.utils.SLogger;


public class SvgHandler {
	private static SLogger logger = SLogger.createLogger(SvgHandler.class);
	static {
		SLogger.addFilter(SvgHandler.class);
	}

	private String svg;
	private JavaScriptObject json;
	private JavaScriptObject handler;
	private UnAttachedSurface surface;
	private EditorContext editorContext;

	public SvgHandler(JavaScriptObject json, JavaScriptObject handler) {
		this.json = json;
		this.handler = handler;
		this.editorContext = new EditorContext();
    this.surface = new UnAttachedSurface(editorContext, new ILoadObserver() {
			public void loaded() {
				handleLoaded();
			}
		});

		// Firefor cannot render manipulate dom if not attached
		// chrome could do without adding to DOM
		surface.getElement().getStyle().setVisibility(Visibility.HIDDEN);
		RootPanel.get().add(surface);
	}

	private void handleLoaded() {
		logger.debug("handleLoaded...");

    JSONObject obj = new JSONObject(json);
    if (obj.isObject() != null) {
	    JSONContentParser parser = new JSONContentParser(obj);
	    IDiagramContent content = parser.toDTO();
	    UiModelContentHandler modelHandler = new UiModelContentHandler(editorContext);
	    modelHandler.addContentItems(content, surface);
	    SvgConverter converter = new SvgConverter(false);
			SvgData data = converter.convertToSvg(content, surface, false);
			this.svg = data.svg;
			nativeReady(handler, data.svg);
    }
  }

	public String getSvg() {
		return svg;
	}

	private native void nativeReady(JavaScriptObject handler, String svg)/*-{
		handler(svg)
	}-*/;
}