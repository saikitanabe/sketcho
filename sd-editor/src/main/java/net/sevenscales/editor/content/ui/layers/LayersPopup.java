package net.sevenscales.editor.content.ui.layers;

import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.dom.client.Element;

import net.sevenscales.editor.api.ISurfaceHandler;


public class LayersPopup extends PopupPanel {
	private LayersMenu layersMenu;

	public LayersPopup(ISurfaceHandler surface, Element launcher) {
		setStyleName("text-size-popup");
		setAutoHideEnabled(true);
		addAutoHidePartner(launcher);
		layersMenu = new LayersMenu(surface, this);
		setWidget(layersMenu);
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
