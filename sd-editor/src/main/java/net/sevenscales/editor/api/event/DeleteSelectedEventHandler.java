package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.EventHandler;

public interface DeleteSelectedEventHandler extends EventHandler {
	void onSelection(DeleteSelectedEvent event);
}
