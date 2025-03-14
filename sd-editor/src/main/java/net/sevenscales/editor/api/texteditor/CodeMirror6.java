package net.sevenscales.editor.api.texteditor;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.dom.client.Document;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Element;

public class CodeMirror6 extends Widget implements ITextEditor {
  private JavaScriptObject codeMirror;
  private ITextEditor.TextChanged changeListener;

  public CodeMirror6(ITextEditor.TextChanged changeListener) {
    this.changeListener = changeListener;
    // Create a div element to host the React component
    setElement(Document.get().createDivElement());
    // Call a native method to initialize the React component on the div
    this.codeMirror = createCodeMirror6(getElement());
  }

  private native JavaScriptObject createCodeMirror6(Element element)/*-{
    var that = this;
    var callback = function() {
      that.@net.sevenscales.editor.api.texteditor.CodeMirror6::textAreaChanged()();
    }
    var result = $wnd.createCodeMirror6(element, callback)
    return result
  }-*/;

	private void textAreaChanged() {
		changeListener.onTextChanged();
	}

  public Widget getUi() {
    return this;
  }

  public void setMarkdownMode(boolean markdownMode) {

  }
	public void setFocus() {
    _setFocus(codeMirror);
  }
	private native void _setFocus(JavaScriptObject codeMirror)/*-{
    codeMirror.setFocus()
  }-*/;

	public void selectAll() {
    _selectAll(codeMirror);
  }
	private native void _selectAll(JavaScriptObject codeMirror)/*-{
    codeMirror.selectAll()
  }-*/;

	public void cursorEnd() {

  }
	public String getText() {
    return _getText(this.codeMirror);
  }
  private native String _getText(JavaScriptObject codeMirror)/*-{
    return codeMirror.getText();
  }-*/;

  public void setText(String text) {
    _setText(this.codeMirror, text);
  }
  private native void _setText(JavaScriptObject codeMirror, String text)/*-{
    codeMirror.setText(text);
  }-*/;

  public void addClass(String styleClass) {

  }
  public void removeClass(String styleClass) {

  }
	public void setBackgroundColor(String color) {
    _setBackgroundColor(codeMirror, color);
  }
	private native void _setBackgroundColor(JavaScriptObject codeMirror, String color)/*-{
    codeMirror.setBackgroundColor(color)
  }-*/;

	public String getBackgroundColor() {
    return "";
  }
	public void setColor(String color) {
    _setColor(codeMirror, color);
  }
	private native void _setColor(JavaScriptObject codeMirror, String color)/*-{
    codeMirror.setColor(color)
  }-*/;
	public void setCursorColorByBgColor(String bgcolor) {

  }
	public void setFontSize(String fontSize) {

  }
	public void setLineHeight(String lineHeight) {

  }
	public void setTextAlign(String textAlign) {

  }
	public void setWidth(int width, String unit) {

  }
	public void setHeight(int height) {
    _setHeight(codeMirror, height);
  }
	private native void _setHeight(JavaScriptObject codeMirror, int height)/*-{
    codeMirror.setHeight(height)
  }-*/;

  public void refresh() {
    _refresh(this.codeMirror);
  }

  private native void _refresh(JavaScriptObject codeMirror)/*-{
    codeMirror.refresh()
  }-*/;

	public boolean isCodeMirror() {
    return true;
  }

}