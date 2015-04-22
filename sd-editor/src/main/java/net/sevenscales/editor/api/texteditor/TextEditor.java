package net.sevenscales.editor.api.texteditor;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style;


class TextEditor extends Composite implements ITextEditor {

	private static CodeMirrorUiBinder uiBinder = GWT.create(CodeMirrorUiBinder.class);

	interface CodeMirrorUiBinder extends UiBinder<Widget, TextEditor> {
	}

	@UiField DivElement menu;
	@UiField TextAreaElement textArea;

	private ITextEditor.TextChanged changeListener;
	private JavaScriptObject cm;

	TextEditor(ITextEditor.TextChanged changeListener) {
		this.changeListener = changeListener;
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public Composite getUi() {
		return this;
	}

	@Override
	public void setMarkdownMode(boolean markdownMode) {
	}

	private void textAreaChanged() {
		changeListener.onTextChanged();
	}

	@Override
	public void setFocus() {
	}

	@Override
	public void selectAll() {
	}

	@Override
	public void cursorEnd() {
	}

	@Override
	public void setText(String text) {
	}

	@Override
	public String getText() {
		return "";
	}

	@Override
	public void setBackgroundColor(String color) {
	}

	@Override
	public void setColor(String color) {
	}

	@Override
	public void setFontSize(String fontSize) {
	}

	@Override
	public void setLineHeight(String lineHeight) {
	}

	@Override
	public void setTextAlign(String textAlign) {
	}
}