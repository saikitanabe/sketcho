package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.EventHandler;

public interface CancelButtonClickedEventHandler extends EventHandler {
	void onSelection(CancelButtonClickedEvent event);
}
