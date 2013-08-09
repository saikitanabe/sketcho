package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.EventHandler;

public interface DiagramsLoadedEventHandler extends EventHandler {
	void on(DiagramsLoadedEvent event);
}
