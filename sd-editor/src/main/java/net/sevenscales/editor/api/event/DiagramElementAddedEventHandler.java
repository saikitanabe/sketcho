package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.EventHandler;

public interface DiagramElementAddedEventHandler extends EventHandler {
	void onAdded(DiagramElementAddedEvent event);
}
