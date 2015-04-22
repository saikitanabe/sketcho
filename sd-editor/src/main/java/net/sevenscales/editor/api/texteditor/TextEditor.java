package net.sevenscales.editor.api.texteditor;

import com.google.gwt.user.client.ui.TextArea;
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
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyCodes;


class TextEditor extends Composite implements ITextEditor {

	private static CodeMirrorUiBinder uiBinder = GWT.create(CodeMirrorUiBinder.class);

	interface CodeMirrorUiBinder extends UiBinder<Widget, TextEditor> {
	}

	private static final String TAB_AS_SPACES = "    ";

	@UiField DivElement menu;
	// @UiField TextAreaElement textAreaElement;
	@UiField TextArea textArea;

	private ITextEditor.TextChanged changeListener;
	private JavaScriptObject cm;
	// private TextArea textArea;

	TextEditor(ITextEditor.TextChanged changeListener) {
		this.changeListener = changeListener;
		initWidget(uiBinder.createAndBindUi(this));

		onTextAreaChange(textArea.getElement(), this);

		textArea.addKeyDownHandler(new KeyDownHandler() {
		  @Override
		  public void onKeyDown(KeyDownEvent event) {
		    if (event.getNativeKeyCode() == KeyCodes.KEY_TAB) {
		      event.preventDefault();
		      event.stopPropagation();
					if(event.getSource() instanceof TextArea) {
		        TextArea ta = (TextArea) event.getSource();
		        int index = ta.getCursorPos();
		        String text = ta.getText();
		        ta.setText(text.substring(0, index) 
		                   + TAB_AS_SPACES + text.substring(index));
		        ta.setCursorPos(index + TAB_AS_SPACES.length());
		      }		      
		    }
		  }
		});		
	}

	private native void onTextAreaChange(Element e, TextEditor me)/*-{
		$wnd.$(e).bind('input propertychange', function(){
		  // alert($wnd.$(this).val());
		  me.@net.sevenscales.editor.api.texteditor.TextEditor::textAreaChanged()();
		});		
	}-*/;

	private void textAreaChanged() {
		changeListener.onTextChanged();
	}	

	@Override
	public Composite getUi() {
		return this;
	}

	@Override
	public void setMarkdownMode(boolean markdownMode) {
	}

	@Override
	public void setFocus() {
		textArea.setFocus(true);
	}

	@Override
	public void selectAll() {
		textArea.selectAll();
	}

	@Override
	public void cursorEnd() {
		textArea.setCursorPos(textArea.getText().length());
	}

	@Override
	public void setText(String text) {
		textArea.setText(text);
	}

	@Override
	public String getText() {
		return textArea.getText();
	}

	@Override
	public void setBackgroundColor(String color) {
		textArea.getElement().getStyle().setBackgroundColor(color);
	}

	@Override
	public void setColor(String color) {
		textArea.getElement().getStyle().setColor(color);
	}

	@Override
	public void setFontSize(String fontSize) {
		textArea.getElement().getStyle().setProperty("fontSize", fontSize);
	}

	@Override
	public void setLineHeight(String lineHeight) {
		textArea.getElement().getStyle().setProperty("lineHeight", lineHeight);
	}

	@Override
	public void setTextAlign(String textAlign) {
		textArea.getElement().getStyle().setProperty("textAlign", textAlign);
	}

	@Override
	public void setWidth(int width) {
		textArea.getElement().getStyle().setWidth(width, Style.Unit.PX);
	}

	@Override
	public void setHeight(int height) {
		textArea.getElement().getStyle().setHeight(height, Style.Unit.PX);
	}
}