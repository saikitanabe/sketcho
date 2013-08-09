package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.EventHandler;

public interface RedoEventHandler extends EventHandler {
	void on(RedoEvent event);
}
