package net.sevenscales.editor.content.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;

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

  interface Template extends SafeHtmlTemplates {
    @Template("<a href=\"{0}\" target=\"_blank\" class=\"white-text\">{0}</a>")
    SafeHtml a(String url);
  }

	@UiField Element popoverTitle;
	@UiField AnchorElement apply;
	@UiField InputElement urlField;
	private ApplyCallback applyCallback;
	private static final Template TEMPLATE;

	static {
		TEMPLATE = GWT.create(Template.class);
	}

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

		Event.sinkEvents(urlField, Event.ONKEYPRESS | Event.ONKEYUP);
    Event.setEventListener(urlField, new EventListener() {
      @Override
      public void onBrowserEvent(Event event) {
      	logger.debug("event: " + event);
      }
    });
	}

	public void setLink(String link) {
		if (link != null && !"".equals(link)) {
			urlField.setValue(link);
			popoverTitle.setInnerSafeHtml(TEMPLATE.a(link));
		} else {
			popoverTitle.setInnerSafeHtml(SafeHtmlUtils.fromSafeConstant("Edit Link"));
		}
	}

	private void stopEvent(ClickEvent event) {
		event.stopPropagation();
		event.preventDefault();
	}

	private void apply() {
		applyCallback.applied(urlField.getValue());
		urlField.setValue("");
	}

}
