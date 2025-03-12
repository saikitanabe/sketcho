package net.sevenscales.editor.api.texteditor;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import net.sevenscales.editor.diagram.utils.UiUtils;

public interface ITextEditor {

	public interface TextChanged {
		void onTextChanged();
	}

	static class Factory {
		public static ITextEditor createEditor(TextChanged changeListener) {
			if (UiUtils.isMobile()) {
				return new TextEditor(changeListener);
			}
			// return new CodeMirror(changeListener);
      return new CodeMirror6(changeListener);
		}
	}

	Widget getUi();
	void setMarkdownMode(boolean markdownMode);
	void setFocus();
	void selectAll();
	void cursorEnd();
	String getText();
  void setText(String text);
  void addClass(String styleClass);
  void removeClass(String styleClass);
	void setBackgroundColor(String color);
	String getBackgroundColor();
	void setColor(String color);
	void setCursorColorByBgColor(String bgcolor);
	void setFontSize(String fontSize);
	void setLineHeight(String lineHeight);
	void setTextAlign(String textAlign);
	void setWidth(int width, String unit);
	void setHeight(int height);
	void setHeight(String height);
  void refresh();
	boolean isCodeMirror();
}