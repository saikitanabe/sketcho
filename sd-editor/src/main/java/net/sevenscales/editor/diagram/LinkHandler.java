package net.sevenscales.editor.diagram;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.safehtml.shared.SafeUri;

import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.content.ui.UIKeyHelpers;
import net.sevenscales.domain.utils.SLogger;

class LinkHandler {
	private static final SLogger logger = SLogger.createLogger(LinkHandler.class);

	static {
		logger.addFilter(LinkHandler.class);
	}

	private ISurfaceHandler surface;

	public LinkHandler(ISurfaceHandler surface) {
		this.surface = surface;

    Event.addNativePreviewHandler(new NativePreviewHandler() {
      @Override
      public void onPreviewNativeEvent(NativePreviewEvent event) {
        handleOpenLink(event);
      }
    });
	}

	private void handleOpenLink(NativePreviewEvent event) {
    NativeEvent ne = event.getNativeEvent();
    if (event.getTypeInt() == Event.ONCLICK && 
    		// cmd (Mac) or ctrl (Windows) key. Note that ctrl key works on Windows, but Mac ctrl key opens context menu
    		// that is good behaviour and it is possible to use right click menu still.
    		(ne.getMetaKey() || ne.getCtrlKey()) &&  
    		ne.getShiftKey() && 
    		UIKeyHelpers.isEditorClosed(surface.getEditorContext())) {
    	ne.preventDefault();
    	openLink();
    }
	}

	private void openLink() {
		Diagram selected = surface.getSelectionHandler().getOnlyOneSelected();
		if (selected != null && selected.hasLink()) {
			logger.debug("openLink");
			SafeUri uri = UriUtils.fromString(selected.getLink());
			Window.open(uri.asString(), "_blank", "");
		}
	}
}