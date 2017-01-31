package net.sevenscales.editor.content.ui;

import java.util.List;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.json.client.*;

import net.sevenscales.editor.content.ui.UIKeyHelpers;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.EditorContext;
import net.sevenscales.domain.utils.SLogger;


/**
* DO NOT USE any longer, using computer clipboard that browser allows to access.
* See SketchboardClipboard
* @deprecated
*/
public class Clipboard {
	private static final SLogger logger = SLogger.createLogger(Clipboard.class);

	private EditorContext editorContext;

	public Clipboard(EditorContext editorContext) {
		this.editorContext = editorContext;

		Event.addNativePreviewHandler(new NativePreviewHandler() {
		  @Override
		  public void onPreviewNativeEvent(NativePreviewEvent event) {
		  	copyOrPaste(event);
			}
		});

	}

	private void copyOrPaste(NativePreviewEvent event) {
    NativeEvent ne = event.getNativeEvent();
    if (event.getTypeInt() == Event.ONKEYDOWN && UIKeyHelpers.cntrlOrCmdKey(ne) && !editorContext.isTrue(EditorProperty.PROPERTY_EDITOR_IS_OPEN)) {
      if (ne.getKeyCode() == 'C' && UIKeyHelpers.allMenusAreClosed()) {
	      logger.debug("copy...");
	      _copy();
	      preventDefault(event);
	    } else if (ne.getKeyCode() == 'V' && UIKeyHelpers.allMenusAreClosed()) {
	      logger.debug("paste...");
	      _paste();
	      preventDefault(event);
	    }
	  }
	}

	private void preventDefault(NativePreviewEvent event) {
    event.getNativeEvent().preventDefault();
	} 

	private native void _copy()/*-{
		$wnd.copyToClipboard();
	}-*/;

	private native void _paste()/*-{
		$wnd.pasteFromClipboard();
	}-*/;


}
