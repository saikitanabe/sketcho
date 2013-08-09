package net.sevenscales.sketcho.client.app.utils;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class DialogUtils {
	public static void showInfoNote(String text) {
		final DialogBox d = new DialogBox();
		d.setText(text);
		HorizontalPanel buttons = new HorizontalPanel();
		Button ok = new Button("Ok");
		ok.addClickListener(new ClickListener() {
			public void onClick(com.google.gwt.user.client.ui.Widget sender) {
				d.hide();
			}
		});
		buttons.add(ok);
		d.setWidget(buttons);
		d.center();
		d.show();
	}
}
