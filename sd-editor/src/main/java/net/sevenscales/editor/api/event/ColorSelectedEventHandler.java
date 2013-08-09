package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.EventHandler;

public interface ColorSelectedEventHandler extends EventHandler {
	void onSelection(ColorSelectedEvent event);
}
