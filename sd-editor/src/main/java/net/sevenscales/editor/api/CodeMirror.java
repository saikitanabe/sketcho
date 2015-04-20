package net.sevenscales.editor.api;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;


class CodeMirror extends Composite {

	private static CodeMirrorUiBinder uiBinder = GWT.create(CodeMirrorUiBinder.class);

	interface CodeMirrorUiBinder extends UiBinder<Widget, CodeMirror> {
	}

	interface TextChanged {
		void onTextChanged();
	}

	@UiField TextAreaElement textArea;
	private TextChanged changeListener;
	private JavaScriptObject cm;

	CodeMirror(TextChanged changeListener) {
		this.changeListener = changeListener;
		initWidget(uiBinder.createAndBindUi(this));
		textArea.setId("markdown");

		cm = init(textArea, this);
	}

	private native JavaScriptObject init(Element textarea, CodeMirror me)/*-{
		var codeMirror = $wnd.$(textarea).markdownify()

		codeMirror.on('change', function() {
			me.@net.sevenscales.editor.api.CodeMirror::textAreaChanged()()
		})

		return codeMirror
	}-*/;

	private void textAreaChanged() {
		changeListener.onTextChanged();
	}

	void setFocus() {
		setFocus(cm);
	}
	private native void setFocus(JavaScriptObject editor)/*-{
		editor.refresh()
		editor.focus()
	}-*/;

	void selectAll() {
		selectAll(cm);
	}
	private native void selectAll(JavaScriptObject cm)/*-{
		// cm.setSelection($wnd.CodeMirror.Pos(cm.firstLine(), 0), $wnd.CodeMirror.Pos(cm.lastLine()))
		cm.execCommand("selectAll")
	}-*/;

	void cursorEnd() {
		cursorEnd(cm);
	}
	private native void cursorEnd(JavaScriptObject cm)/*-{
		cm.setCursor(cm.lineCount(), 0)
	}-*/;

	void setText(String text) {
		setText(text, cm);
	}
	private native void setText(String text, JavaScriptObject editor)/*-{
		editor.getDoc().setValue(text);
		editor.refresh();
	}-*/;

	String getText() {
		return getText(cm);
	}
	private native String getText(JavaScriptObject editor)/*-{
		return editor.getValue()
	}-*/;

	void setBackgroundColor(String color) {
		setBackgroundColor(color, cm);
	}
	private native void setBackgroundColor(String color, JavaScriptObject cm)/*-{
		$wnd.$(cm.getTextArea().parentNode).css("backgroundColor", color)
	}-*/;

	void setColor(String color) {
		setColor(color, cm);
	}
	private native void setColor(String color, JavaScriptObject cm)/*-{
		$wnd.$(cm.getTextArea().parentNode).find(".CodeMirror").css("color", color)
	}-*/;

	void setFontSize(String fontSize) {
		setFontSize(fontSize, cm);
	}
	private native void setFontSize(String fontSize, JavaScriptObject cm)/*-{
		$wnd.$(cm.getTextArea().parentNode).find(".CodeMirror").css("fontSize", fontSize)
	}-*/;

	void setLineHeight(String lineHeight) {
		setLineHeight(lineHeight, cm);
	}
	private native void setLineHeight(String lineHeight, JavaScriptObject cm)/*-{
		$wnd.$(cm.getTextArea().parentNode).find(".CodeMirror").css("lineHeight", lineHeight)
	}-*/;

	void setTextAlign(String textAlign) {
		setTextAlign(textAlign, cm);
	}
	private native void setTextAlign(String textAlign, JavaScriptObject cm)/*-{
		$wnd.$(cm.getTextArea().parentNode).find(".CodeMirror").css("textAlign", textAlign)
	}-*/;
}