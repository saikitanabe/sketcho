package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.EventHandler;

public interface EditorClosedEventHandler extends EventHandler {
	void onSelection(EditorClosedEvent event);
}
