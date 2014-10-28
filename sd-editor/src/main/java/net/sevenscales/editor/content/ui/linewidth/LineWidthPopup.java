package net.sevenscales.editor.content.ui.linewidth;

import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.dom.client.Element;

import net.sevenscales.editor.api.ISurfaceHandler;


public class LineWidthPopup extends PopupPanel {
	private LineWidthMenu lineWidthMenu;

	public LineWidthPopup(ISurfaceHandler surface, Element launcher) {
		setStyleName("text-size-popup");
		setAutoHideEnabled(true);
		addAutoHidePartner(launcher);
		lineWidthMenu = new LineWidthMenu(surface, this);
		setWidget(lineWidthMenu);
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
