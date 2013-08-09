package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.EventHandler;

public interface RelationshipNotAttachedEventHandler extends EventHandler {
	void onNotAttached(RelationshipNotAttachedEvent event);
}
