package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.EventHandler;

public interface CommentThreadDeletedEventHandler extends EventHandler {
	void on(CommentThreadDeletedEvent event);
}
