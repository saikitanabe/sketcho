package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.EventHandler;

public interface LibrarySelectionEventHandler extends EventHandler {
	void onSelection(LibrarySelectionEvent event);
}
