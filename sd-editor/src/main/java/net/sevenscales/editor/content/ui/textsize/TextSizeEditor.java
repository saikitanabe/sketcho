package net.sevenscales.editor.content.ui.textsize;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.event.dom.client.ClickEvent;

import net.sevenscales.domain.utils.SLogger;


class TextSizeEditor extends Composite {
	private static SLogger logger = SLogger.createLogger(TextSizeEditor.class);

	interface TextSizeEditorUiBinder extends UiBinder<Widget, TextSizeEditor> {
	}
	private static TextSizeEditorUiBinder uiBinder = GWT.create(TextSizeEditorUiBinder.class);

	@UiField Label currentSize;
	@UiField VerticalPanel sizes;

	private TextSizeHandler handler;

	TextSizeEditor(TextSizeHandler handler) {
		this.handler = handler;
		initWidget(uiBinder.createAndBindUi(this));
	}

	private void handle(Object source) {
		if (source instanceof Label) {
			Label l = (Label) source;
			try {
				int size = Integer.parseInt(l.getText());
				handler.textSizeChanged(size);
			} catch (NumberFormatException e) {
				logger.error("Check value", e);
			}
		}
	}

	@UiHandler("size1")
	public void onSize1(ClickEvent event) {
		handle(event.getSource());
	}
	@UiHandler("size2")
	public void onSize2(ClickEvent event) {
		handle(event.getSource());
	}
	@UiHandler("size3")
	public void onSize3(ClickEvent event) {
		handle(event.getSource());
	}
	@UiHandler("size4")
	public void onSize4(ClickEvent event) {
		handle(event.getSource());
	}
	@UiHandler("size5")
	public void onSize5(ClickEvent event) {
		handle(event.getSource());
	}
	@UiHandler("size6")
	public void onSize6(ClickEvent event) {
		handle(event.getSource());
	}
	@UiHandler("size7")
	public void onSize7(ClickEvent event) {
		handle(event.getSource());
	}
	@UiHandler("size8")
	public void onSize8(ClickEvent event) {
		handle(event.getSource());
	}
	@UiHandler("size9")
	public void onSize9(ClickEvent event) {
		handle(event.getSource());
	}
	@UiHandler("size10")
	public void onSize10(ClickEvent event) {
		handle(event.getSource());
	}
	@UiHandler("size11")
	public void onSize11(ClickEvent event) {
		handle(event.getSource());
	}
	@UiHandler("size12")
	public void onSize12(ClickEvent event) {
		handle(event.getSource());
	}
	@UiHandler("size13")
	public void onSize13(ClickEvent event) {
		handle(event.getSource());
	}
	@UiHandler("size14")
	public void onSize14(ClickEvent event) {
		handle(event.getSource());
	}

}