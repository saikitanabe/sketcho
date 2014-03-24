package net.sevenscales.editor.content.ui.layers;

import com.google.gwt.user.client.ui.PopupPanel;

import net.sevenscales.editor.api.ISurfaceHandler;


public class LayersPopup extends PopupPanel {
	private LayersMenu layersMenu;

	public LayersPopup(ISurfaceHandler surface) {
		setStyleName("text-size-popup");
		setAutoHideEnabled(true);
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
