package net.sevenscales.editor.api.texteditor;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import net.sevenscales.editor.api.impl.Theme;
import net.sevenscales.editor.content.utils.ColorHelpers;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style;


class CodeMirror extends Composite implements ITextEditor {

	private static CodeMirrorUiBinder uiBinder = GWT.create(CodeMirrorUiBinder.class);

	interface CodeMirrorUiBinder extends UiBinder<Widget, CodeMirror> {
	}

	@UiField DivElement menu;
	@UiField TextAreaElement textArea;

	private ITextEditor.TextChanged changeListener;
	private JavaScriptObject cm;

	CodeMirror(ITextEditor.TextChanged changeListener) {
		this.changeListener = changeListener;
		initWidget(uiBinder.createAndBindUi(this));
		textArea.setId("markdown");

		cm = init(textArea, this);
	}

	@Override
	public Composite getUi() {
		return this;
	}

	public void setMarkdownMode(boolean markdownMode) {
		String mode = "";
		Style.Display menuDisplay = Style.Display.NONE;
		if (markdownMode) {
			mode = "markdown";
			menuDisplay = Style.Display.INLINE_BLOCK;
		}

		menu.getStyle().setDisplay(menuDisplay);
		setMarkdownMode(cm, mode);
	}

	private native void setMarkdownMode(JavaScriptObject cm, String mode)/*-{
		cm.setOption("mode", mode);
	}-*/;

	private native JavaScriptObject init(Element textarea, CodeMirror me)/*-{
		var codeMirror = $wnd.$(textarea).markdownify()

		codeMirror.on('change', function() {
			me.@net.sevenscales.editor.api.texteditor.CodeMirror::textAreaChanged()()
		})

		return codeMirror
	}-*/;

	private void textAreaChanged() {
		changeListener.onTextChanged();
	}

	@Override
	public void setFocus() {
		setFocus(cm);
	}
	private native void setFocus(JavaScriptObject editor)/*-{
		editor.refresh()
		editor.focus()
	}-*/;

	@Override
	public void selectAll() {
		selectAll(cm);
	}
	private native void selectAll(JavaScriptObject cm)/*-{
		// cm.setSelection($wnd.CodeMirror.Pos(cm.firstLine(), 0), $wnd.CodeMirror.Pos(cm.lastLine()))
		cm.execCommand("selectAll")
	}-*/;

	@Override
	public void cursorEnd() {
		cursorEnd(cm);
	}
	private native void cursorEnd(JavaScriptObject cm)/*-{
		cm.setCursor(cm.lineCount(), 0)
	}-*/;

	@Override
	public void setText(String text) {
		setText(text, cm);
	}
	private native void setText(String text, JavaScriptObject editor)/*-{
		editor.getDoc().setValue(text);
		editor.refresh();
	}-*/;

	@Override
	public String getText() {
		return getText(cm);
	}
	private native String getText(JavaScriptObject editor)/*-{
		return editor.getValue()
	}-*/;

	@Override
	public void setBackgroundColor(String color) {
		setBackgroundColor(color, cm);
	}
	private native void setBackgroundColor(String color, JavaScriptObject cm)/*-{
		$wnd.$(cm.getTextArea().parentNode).css("backgroundColor", color)
	}-*/;

	public void setCursorColorByBgColor(String bgcolor) {
		if ("transparent".equals(bgcolor) && Theme.isBlackTheme()) {
			setCursorStyleClass("cm-cursor-white", "cm-cursor-white", cm);
			return;
		}

		// best would be to apply text color for the cursor but
		// it would require modifying css class on run time
		if (!ColorHelpers.isHexBlack(bgcolor)) {
			setCursorStyleClass("cm-cursor-white", "cm-cursor-white", cm);
		} else {
			setCursorStyleClass("cm-cursor-white", "", cm);
		}
	}

	@Override
	public void setColor(String color) {
		setColor(color, cm);
	}
	private native void setColor(String color, JavaScriptObject cm)/*-{
		$wnd.$(cm.getTextArea().parentNode).find(".CodeMirror").css("color", color)
	}-*/;

	private native void setCursorStyleClass(String removeClass, String newStyleClass, JavaScriptObject cm)/*-{
		var base = $wnd.$(cm.getTextArea().parentNode).find(".CodeMirror")
		base.removeClass(removeClass)
		base.addClass(newStyleClass)
	}-*/;

	@Override
	public void setFontSize(String fontSize) {
		setFontSize(fontSize, cm);
	}
	private native void setFontSize(String fontSize, JavaScriptObject cm)/*-{
		$wnd.$(cm.getTextArea().parentNode).find(".CodeMirror").css("fontSize", fontSize)
	}-*/;

	@Override
	public void setLineHeight(String lineHeight) {
		setLineHeight(lineHeight, cm);
	}
	private native void setLineHeight(String lineHeight, JavaScriptObject cm)/*-{
		$wnd.$(cm.getTextArea().parentNode).find(".CodeMirror").css("lineHeight", lineHeight)
	}-*/;

	@Override
	public void setTextAlign(String textAlign) {
		setTextAlign(textAlign, cm);
	}
	private native void setTextAlign(String textAlign, JavaScriptObject cm)/*-{
		$wnd.$(cm.getTextArea().parentNode).find(".CodeMirror").css("textAlign", textAlign)
	}-*/;


	@Override
	public void setWidth(int width) {
	}
	@Override
	public void setHeight(int height) {
	}
	@Override
	public boolean isCodeMirror() {
		return true;
	}

}