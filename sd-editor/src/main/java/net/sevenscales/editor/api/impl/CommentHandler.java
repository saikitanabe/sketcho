package net.sevenscales.editor.api.impl;

import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.event.CommentModeEvent;
import net.sevenscales.editor.api.event.CommentModeEventHandler;
import net.sevenscales.editor.diagram.Diagram;

public class CommentHandler {
	ISurfaceHandler surface;
	
	public CommentHandler(ISurfaceHandler surface) {
		this.surface = surface;

		surface.getEditorContext().getEventBus().addHandler(CommentModeEvent.TYPE, new CommentModeEventHandler() {
			@Override
			public void on(CommentModeEvent event) {
				showHideCommentMode(event.isEnabled());
			}
		});
	}

	private void showHideCommentMode(boolean on) {
		for (Diagram d : surface.getDiagrams()) {
			if (d.isAnnotated()) {
				d.setVisible(on);
			}
		}
	}

}
