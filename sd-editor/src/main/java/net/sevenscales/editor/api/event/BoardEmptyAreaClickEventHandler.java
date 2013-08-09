package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.EventHandler;

public interface BoardEmptyAreaClickEventHandler extends EventHandler {
	void on(BoardEmptyAreaClickedEvent event);
}
