package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.EventHandler;

public interface FreehandModeChangedEventHandler extends EventHandler {
	void on(FreehandModeChangedEvent event);
}
