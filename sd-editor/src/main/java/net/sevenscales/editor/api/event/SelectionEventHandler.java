package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.EventHandler;

public interface SelectionEventHandler extends EventHandler {
	void onSelection(SelectionEvent event);
}
