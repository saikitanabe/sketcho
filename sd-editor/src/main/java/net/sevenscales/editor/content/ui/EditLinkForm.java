package net.sevenscales.editor.content.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.dom.client.InputElement;

import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.EditorContext;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.impl.FastElementButton;


public class EditLinkForm extends Composite {
	private static final SLogger logger = SLogger.createLogger(EditLinkForm.class);

	private static EditLinkFormUiBinder uiBinder = GWT
			.create(EditLinkFormUiBinder.class);

	interface EditLinkFormUiBinder extends UiBinder<Widget, EditLinkForm> {
	}

	public interface ApplyCallback {
		void applied(String url);
	}

	@UiField AnchorElement apply;
	@UiField InputElement urlField;
	private ApplyCallback applyCallback;


	public EditLinkForm(ApplyCallback applyCallback) {
		this.applyCallback = applyCallback;
		initWidget(uiBinder.createAndBindUi(this));

		new FastElementButton(apply).addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				stopEvent(event);
				apply();
			}
		});
	}

	private void stopEvent(ClickEvent event) {
		event.stopPropagation();
		event.preventDefault();
	}

	private void apply() {
		applyCallback.applied(urlField.getValue());
	}

}
