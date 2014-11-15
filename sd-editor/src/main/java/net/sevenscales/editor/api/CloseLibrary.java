package net.sevenscales.editor.api;

import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.Element;


class CloseLibrary extends SimplePanel {
	CloseLibrary() {
		setStyleName("library-close");
		handleClose(getElement());
	}

	private native void handleClose(Element e)/*-{
		$wnd.Hammer(e).on('tap', function() {
			$wnd.globalStreams.closeLibraryStram.push(true)
		})
	}-*/;
}