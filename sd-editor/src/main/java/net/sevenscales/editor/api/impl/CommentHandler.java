package net.sevenscales.editor.api.impl;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;

import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.event.CommentModeEvent;
import net.sevenscales.editor.api.event.CommentModeEventHandler;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.content.ui.UIKeyHelpers;
import net.sevenscales.domain.utils.SLogger;


public class CommentHandler {
	private static final SLogger logger = SLogger.createLogger(CommentHandler.class);

	private ISurfaceHandler surface;
	private boolean commentKeyDown;
	
	public CommentHandler(ISurfaceHandler surface) {
		this.surface = surface;

		surface.getEditorContext().getEventBus().addHandler(CommentModeEvent.TYPE, new CommentModeEventHandler() {
			@Override
			public void on(CommentModeEvent event) {
				showHideCommentMode(event.isEnabled());
			}
		});

    Event.addNativePreviewHandler(new NativePreviewHandler() {
      @Override
      public void onPreviewNativeEvent(NativePreviewEvent event) {
      	handleOnPreviewNativeEvent(event);
      }
    });
	}

	private void handleOnPreviewNativeEvent(NativePreviewEvent event) {
    NativeEvent ne = event.getNativeEvent();
    if (!commentKeyDown && event.getTypeInt() == Event.ONKEYDOWN && UIKeyHelpers.noMetaKeys(ne) && UIKeyHelpers.isEditorClosed(surface.getEditorContext())) {
      if (ne.getKeyCode() == 'C' && UIKeyHelpers.allMenusAreClosed()) {
        commentKeyDown = true;
        fireToggleCommentMode();
      }
    }

    if (commentKeyDown && event.getTypeInt() == Event.ONKEYUP && UIKeyHelpers.isEditorClosed(surface.getEditorContext())) {
      if (ne.getKeyCode() == 'C' && UIKeyHelpers.allMenusAreClosed()) {
        commentKeyDown = false;
      }
    }
	}

	private void fireToggleCommentMode() {
    boolean toggleValue = !surface.getEditorContext().isTrue(EditorProperty.COMMENT_MODE);
    logger.debug("comment mode toggleValue {}", toggleValue);
    surface.getEditorContext().set(EditorProperty.COMMENT_MODE, toggleValue);
    surface.getEditorContext().getEventBus().fireEvent(new CommentModeEvent(toggleValue));
  }

	private void showHideCommentMode(boolean on) {
		for (Diagram d : surface.getDiagrams()) {
			if (d.isAnnotation()) {
				d.setVisible(on);
			}
		}
	}

}
