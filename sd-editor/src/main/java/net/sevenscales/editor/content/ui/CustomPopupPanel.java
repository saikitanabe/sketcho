package net.sevenscales.editor.content.ui;

import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.content.utils.KeyHelpers;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;

public class CustomPopupPanel extends PopupPanel {
	private static final SLogger logger = SLogger
			.createLogger(CustomPopupPanel.class);
	private boolean selectAll;
	private TextArea textArea;
	
	public CustomPopupPanel(TextArea textArea) {
		super(false);
		this.textArea = textArea;
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
		textArea.setFocus(true);
		
		if (selectAll) {
			textArea.setSelectionRange(0, textArea.getText().length());
			logger.info("focus set and text selected");
		} else {
			textArea.setCursorPos(textArea.getText().length());
		}
		
	}

	public void selectAll(boolean selectAll) {
		this.selectAll = selectAll;
	}
	
}
