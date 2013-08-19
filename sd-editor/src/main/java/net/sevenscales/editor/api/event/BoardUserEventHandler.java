package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.EventHandler;

public interface BoardUserEventHandler extends EventHandler {
	void on(BoardUserEvent event);
}
