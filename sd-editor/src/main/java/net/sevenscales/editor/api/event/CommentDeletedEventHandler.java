package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.EventHandler;

public interface CommentDeletedEventHandler extends EventHandler {
	void on(CommentDeletedEvent event);
}
