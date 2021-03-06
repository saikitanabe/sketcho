package net.sevenscales.editor.api;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.ui.RootPanel;

import net.sevenscales.domain.JSONContentParser;
import net.sevenscales.domain.api.IDiagramContent;
import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.impl.UnAttachedSurface;
import net.sevenscales.editor.api.BoardDimensions;
import net.sevenscales.editor.content.UiModelContentHandler;
import net.sevenscales.editor.diagram.utils.UiUtils;
import net.sevenscales.editor.gfx.domain.ILoadObserver;
import net.sevenscales.editor.gfx.domain.JsSvg;
import net.sevenscales.editor.gfx.svg.converter.SvgConverter;
import net.sevenscales.editor.gfx.svg.converter.SvgData;
import net.sevenscales.editor.uicomponents.uml.ShapeCache;

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
		logger.debug("SvgHandler json: {}", json);
		this.json = json;
		this.handler = handler;
		this.editorContext = new EditorContext();
		Tools.create(editorContext, false);
		if (UiUtils.isIE()) {
			// ie doesn't support vector-effect
			editorContext.set(EditorProperty.CONFLUENCE_MODE, true);
		}

		// if (SvgHandler.surface == null) {
		surface = new UnAttachedSurface(editorContext, new ILoadObserver() {
			public void loaded() {
				handleLoaded();
			}
		});

		// Firefor cannot render manipulate dom if not attached
		// chrome could do without adding to DOM
		// surface.getElement().getStyle().setDisplay(Style.Display.NONE);
		RootPanel.get().add(surface);
		// } else {
		// surface.clearAndInit();
		// }
	}

	private void handleLoaded() {
		logger.debug("handleLoaded...");

		SurfaceDefs.addToDefs(surface.getSurface().getContainer(), ShapeCache.icons());

		Tools.setExportMode(true);

		JSONObject obj = new JSONObject(json);
		if (obj.isObject() != null) {
			JSONContentParser parser = new JSONContentParser(obj);
			IDiagramContent content = parser.toDTO();
			ShapeCache.updateShapes(content.getLibrary());
			UiModelContentHandler modelHandler = new UiModelContentHandler(editorContext);
			modelHandler.addContentItems(content, surface);
			SvgConverter converter = new SvgConverter(false);
			int width = 0;
			if (content.getWidth() != null) {
				width = content.getWidth();
			}
			int height = 0;
			if (content.getHeight() != null) {
				height = content.getHeight();
			}

			// use chrome headless environment or legacy phantomjs environment
			if (!SvgHandler.isChromeHeadlessEnv()) {
				// ST 20.11.2017: Legacy SVG custom conversion
				SvgData data = converter.convertToSvg(width, height, surface, false, true);
				this.svg = data.svg;
				JsSvg jsSvg = JsSvg.create(data.svg);
				nativeReadyLegacy(handler, data.svg);
				// ST 20.11.2017: END Legacy SVG custom conversion
			} else {

				// ST 12.11.2017: NEW DOM based svg extraction
				SvgData data = new SvgData();
				JsSvg jsSvg = surface.getSvg();
				if (jsSvg != null) {
					// jsSvg could be null, e.g. now on normal SurfaceHandler
					data.svg = jsSvg.getSvg();
					this.svg = data.svg;
				}
				nativeReady(handler, jsSvg);
				// ST 12.11.2017: END NEW DOM based svg extraction
			}

			Tools.setExportMode(false);
		}

		// synchronous from RootPanel.get().add, so break out
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				surface.removeFromParent();
			}
		});

		// RootPanel.get().remove(surface);
	}

	public static final native boolean isChromeHeadlessEnv()/*-{
		return typeof $wnd.__svgNodeToString__ === 'function'
	}-*/;

	public String getSvg() {
		return svg;
	}

	private native void nativeReadyLegacy(JavaScriptObject handler, String svg)/*-{
																																							handler(svg)
																																							}-*/;

	private native void nativeReady(JavaScriptObject handler, JsSvg svg)/*-{
																																			handler(svg)
																																			}-*/;

}