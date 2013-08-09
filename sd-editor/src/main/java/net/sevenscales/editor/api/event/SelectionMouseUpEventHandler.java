package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.EventHandler;

public interface SelectionMouseUpEventHandler extends EventHandler {
	void onSelection(SelectionMouseUpEvent event);
}
