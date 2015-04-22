package net.sevenscales.editor.api.texteditor;

import com.google.gwt.user.client.ui.Composite;


public interface ITextEditor {

	public interface TextChanged {
		void onTextChanged();
	}

	static class Factory {
		public static ITextEditor createEditor(TextChanged changeListener, boolean codeMirror) {
			if (codeMirror) {
				return new CodeMirror(changeListener);
			}
			return new TextEditor(changeListener);
		}
	}

	void setMarkdownMode(boolean markdownMode);
	Composite getUi();
	void setFocus();
	void selectAll();
	void cursorEnd();
	String getText();
	void setText(String text);
	void setBackgroundColor(String color);
	void setColor(String color);
	void setFontSize(String fontSize);
	void setLineHeight(String lineHeight);
	void setTextAlign(String textAlign);
}