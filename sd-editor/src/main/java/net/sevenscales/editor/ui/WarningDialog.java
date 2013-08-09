package net.sevenscales.editor.ui;

import net.sevenscales.editor.ui.WarningDialog.WarningHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class WarningDialog extends Composite {

	private static WarningDialogUiBinder uiBinder = GWT
			.create(WarningDialogUiBinder.class);

	interface WarningDialogUiBinder extends UiBinder<Widget, WarningDialog> {
	}
	
	public interface WarningHandler {
		void doIt();
		void cancel();
	}

	private DialogBox warning;
	@UiField VerticalPanel content;
	private WarningHandler handler;

	public WarningDialog(WarningHandler handler) {
		this.handler = handler;
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	@UiHandler("doit")
	public void onDoIt(ClickEvent event) {
		warning.hide();
		handler.doIt();
	}

	@UiHandler("cancel")
	public void onCancel(ClickEvent event) {
		warning.hide();
		handler.cancel();
	}

	public void show(String text) {
		this.warning = new DialogBox(false);
		warning.addStyleName("warning-dialog");
		warning.setGlassEnabled(true);
		warning.setAnimationEnabled(true);
		
		content.add(new HTML(SafeHtmlUtils.fromString(text)));
		warning.setWidget(this);
		
		warning.center();
		warning.show();
	}

}
