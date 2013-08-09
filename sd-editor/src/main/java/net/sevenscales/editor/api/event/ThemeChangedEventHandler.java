package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.EventHandler;

public interface ThemeChangedEventHandler extends EventHandler {
	void on(ThemeChangedEvent event);
}
