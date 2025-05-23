package net.sevenscales.editor.api;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.i18n.client.NumberFormat;

import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.texteditor.ITextEditor;
import net.sevenscales.editor.content.utils.KeyHelpers;

public class CustomPopupCodeMirror extends PopupPanel {
	private static final SLogger logger = SLogger
			.createLogger(CustomPopupCodeMirror.class);
	private boolean selectAll;
	private ITextEditor codeMirror;
	
	public CustomPopupCodeMirror() {
		super(false);
	}

	void setCodeMirror(ITextEditor codeMirror) {
		this.codeMirror = codeMirror;
	}

	@Override
	protected void onPreviewNativeEvent(NativePreviewEvent event) {
		super.onPreviewNativeEvent(event);
		switch (event.getTypeInt()) {
		case Event.ONKEYDOWN:
      if (KeyHelpers.isSave(event)) {
      	// Close editing and prevent default web page saving by the Browser
      	// Default saving prevents content saving on rest service and error will occur.
      	// ctrl/cmd + s
      	hide();
        event.cancel();
      }
			// if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE) {
			// 	hide();
			// }
			break;
		}
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		focusEditor();
	}

	private void focusEditor() {
		codeMirror.setFocus();
		
		if (selectAll) {
			codeMirror.selectAll();
			// textArea.setSelectionRange(0, textArea.getText().length());
			logger.info("focus set and text selected");
		} else {
			codeMirror.cursorEnd();
			// textArea.setCursorPos(textArea.getText().length());
		}
		
	}

	public void selectAll(boolean selectAll) {
		this.selectAll = selectAll;
	}

  void setScaling(double scaleFactor) {
    getElement().getStyle().setProperty("transformOrigin", "0 0");
    NumberFormat fmt = NumberFormat.getDecimalFormat();
    String formatted = fmt.format(scaleFactor);
    getElement().
      getStyle().
      setProperty(
        "transform",
        "scale(" + formatted + ")"
      );
  }

	void setContentWidth(int width) {
		setContentWidth(width + "px", getElement());
	}
	private native void setContentWidth(String width, JavaScriptObject popup)/*-{
		var popupContent = $wnd.$(popup).find(".popupContent")
		popupContent.css("width", width)
	}-*/;

}
