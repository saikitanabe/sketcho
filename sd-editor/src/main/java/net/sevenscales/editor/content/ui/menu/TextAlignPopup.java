package net.sevenscales.editor.content.ui.menu;

import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.dom.client.Element;

import net.sevenscales.editor.api.ISurfaceHandler;


public class TextAlignPopup extends PopupPanel {
	private TextAlignMenu textAlignMenu;

	public TextAlignPopup(ISurfaceHandler surface, Element launcher) {
		setStyleName("text-size-popup");
		setAutoHideEnabled(true);
		addAutoHidePartner(launcher);
		textAlignMenu = new TextAlignMenu(surface, this);
		setWidget(textAlignMenu);
		
		handleStreams(this);
	}

	private native void handleStreams(TextAlignPopup me)/*-{
    $wnd.cancelStream.onValue(function(v) {
      me.@net.sevenscales.editor.content.ui.menu.TextAlignPopup::onEsc()();
    })
	}-*/;

	private void onEsc() {
		if (isShowing()) {
			hide();
		}
	}

	public void show(final int left, final int top) {
		setPopupPositionAndShow(new PopupPanel.PositionCallback() {
			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
				setPopupPosition(left, top);
			}
		});
	}
}
