package net.sevenscales.editor.content.ui.lineweight;

import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.dom.client.Element;

import net.sevenscales.editor.api.ISurfaceHandler;


public class LineWeightPopup extends PopupPanel {
	private LineWeightMenu lineWeightMenu;

	public LineWeightPopup(ISurfaceHandler surface, Element launcher) {
		setAutoHideEnabled(true);
		addAutoHidePartner(launcher);
		lineWeightMenu = new LineWeightMenu(surface, this);
		setWidget(lineWeightMenu);
		
		handleStreams(this);
	}

	private native void handleStreams(LineWeightPopup me)/*-{
    $wnd.cancelStream.onValue(function(v) {
      me.@net.sevenscales.editor.content.ui.lineweight.LineWeightPopup::onEsc()();
    })
	}-*/;

	private void onEsc() {
		if (isShowing()) {
			hide();
		}
	}

	// public void show(final int left, final int top, final boolean reduceHeight) {
	public void show(Element element, final int parentHeight, final boolean reduceHeight, boolean black) {
		if (black) {
			setStyleName("text-size-popup");
		} else {
			setStyleName("popup-white");
		}
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
				} else {
					t += parentHeight;
				}
				setPopupPosition(left, t);
			}
		});
	}
}
