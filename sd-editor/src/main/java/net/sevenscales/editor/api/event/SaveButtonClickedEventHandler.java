package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.EventHandler;

public interface SaveButtonClickedEventHandler extends EventHandler {
	void onSelection(SaveButtonClickedEvent event);
}
