package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.EventHandler;

public interface UndoEventHandler extends EventHandler {
	void on(UndoEvent event);
}
