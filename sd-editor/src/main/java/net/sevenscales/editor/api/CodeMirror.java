package net.sevenscales.editor.api;

import com.google.gwt.core.client.JavaScriptObject;

class CodeMirror {

	private JavaScriptObject editor;

	CodeMirror(JavaScriptObject editor) {
		this.editor = editor;
	}

	void setFocus() {
		setFocus(editor);
	}
	private native void setFocus(JavaScriptObject editor)/*-{
		// $wnd._.defer(function() {
		// 	editor.focus()
		// })
		editor.refresh()
		editor.focus()
	}-*/;

	void selectAll() {
		selectAll(editor);
	}
	private native void selectAll(JavaScriptObject cm)/*-{
		cm.setSelection($wnd.CodeMirror.Pos(cm.firstLine(), 0), $wnd.CodeMirror.Pos(cm.lastLine()))
	}-*/;

	void cursorEnd() {
		cursorEnd(editor);
	}
	private native void cursorEnd(JavaScriptObject cm)/*-{
		cm.setCursor(cm.lineCount(), 0)
	}-*/;

	void setText(String text) {
		setText(text, editor);
	}
	private native void setText(String text, JavaScriptObject editor)/*-{
		editor.getDoc().setValue(text);
		editor.refresh();
	}-*/;

	String getText() {
		return getText(editor);
	}
	private native String getText(JavaScriptObject editor)/*-{
		return editor.getValue()
	}-*/;

	void setBackgroundColor(String color) {
		setBackgroundColor(color, editor);
	}
	private native void setBackgroundColor(String color, JavaScriptObject cm)/*-{
		$wnd.$(cm.getTextArea().parentNode).css("backgroundColor", color)
	}-*/;

	void setColor(String color) {
		setColor(color, editor);
	}
	private native void setColor(String color, JavaScriptObject cm)/*-{
		$wnd.$(cm.getTextArea().parentNode).find(".CodeMirror").css("color", color)
	}-*/;

	void setFontSize(String fontSize) {
		setFontSize(fontSize, editor);
	}
	private native void setFontSize(String fontSize, JavaScriptObject cm)/*-{
		$wnd.$(cm.getTextArea().parentNode).find(".CodeMirror").css("fontSize", fontSize)
	}-*/;

	void setLineHeight(String lineHeight) {
		setLineHeight(lineHeight, editor);
	}
	private native void setLineHeight(String lineHeight, JavaScriptObject cm)/*-{
		$wnd.$(cm.getTextArea().parentNode).find(".CodeMirror").css("lineHeight", lineHeight)
	}-*/;

	void setTextAlign(String textAlign) {
		setTextAlign(textAlign, editor);
	}
	private native void setTextAlign(String textAlign, JavaScriptObject cm)/*-{
		$wnd.$(cm.getTextArea().parentNode).find(".CodeMirror").css("textAlign", textAlign)
	}-*/;
}