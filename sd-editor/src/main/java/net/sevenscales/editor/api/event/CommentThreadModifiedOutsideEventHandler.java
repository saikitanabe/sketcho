package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.EventHandler;

public interface CommentThreadModifiedOutsideEventHandler extends EventHandler {
	void on(CommentThreadModifiedOutsideEvent event);
}
