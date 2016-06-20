package net.sevenscales.editor.content.ui.lineweight;

import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.dom.client.Element;

import net.sevenscales.editor.api.ISurfaceHandler;


public class LineWeightPopup extends PopupPanel {
	private LineWeightMenu lineWeightMenu;

	public LineWeightPopup(ISurfaceHandler surface, Element launcher) {
		setStyleName("text-size-popup");
		setAutoHideEnabled(true);
		addAutoHidePartner(launcher);
		lineWeightMenu = new LineWeightMenu(surface, this);
		setWidget(lineWeightMenu);
	}

	// public void show(final int left, final int top, final boolean reduceHeight) {
	public void show(Element element, final boolean reduceHeight) {
		// remove before adding, so array doesn't grow too much
		removeAutoHidePartner(element);
		addAutoHidePartner(element);
		final int left = element.getAbsoluteLeft();
		final int top = element.getAbsoluteTop();

		setPopupPositionAndShow(new PopupPanel.PositionCallback() {
			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
				int t = top;
				if (reduceHeight) {
					t -= offsetHeight;
				}
				setPopupPosition(left, t);
			}
		});
	}
}
