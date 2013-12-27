package net.sevenscales.editor.content.ui.textsize;

import com.google.gwt.user.client.ui.PopupPanel;

public class TextSizePopup extends PopupPanel implements TextSizeHandler {
	private TextSizeHandler handler;
	private TextSizeEditor editor;

	public TextSizePopup(TextSizeHandler handler) {
		this.handler = handler;
		editor = new TextSizeEditor(this);

		setStyleName("text-size-popup");
		setAutoHideEnabled(true);
		// addAutoHidePartner(addlink);
		// editLinkPopup.addAutoHidePartner(openlink);
		setWidget(editor);
	}

	public void setCurrentSize(int currentSize) {
		editor.setCurrentSize(currentSize);
	}

	public void show(final int left, final int top) {
		setPopupPositionAndShow(new PopupPanel.PositionCallback() {
			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
				setPopupPosition(left, top);
			}
		});
	}

	public void fontSizeChanged(int fontSize) {
		handler.fontSizeChanged(fontSize);
		hide();
	}
}
