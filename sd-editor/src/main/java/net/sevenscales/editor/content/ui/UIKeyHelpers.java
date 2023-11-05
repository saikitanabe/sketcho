package net.sevenscales.editor.content.ui;

import com.google.gwt.dom.client.NativeEvent;

import net.sevenscales.editor.api.EditorContext;
import net.sevenscales.editor.api.EditorProperty;

public class UIKeyHelpers {

  public static boolean isEditorClosed(EditorContext editorContext) {
    return !editorContext.isTrue(EditorProperty.PROPERTY_EDITOR_IS_OPEN);
  }

	public static native boolean allMenusAreClosed()/*-{
	  var menuOpenFunc = $wnd.isMenuOpen;
	  if (typeof menuOpenFunc == "function") return !menuOpenFunc();
	  return true; 
	}-*/; 

  public static native boolean isInputFocus()/*-{
    return typeof $wnd.isInputFocus === 'function' && $wnd.isInputFocus()
  }-*/;
	
	public static boolean noMetaKeys(NativeEvent ne) {
	  return !ne.getCtrlKey() && !ne.getMetaKey() && !ne.getShiftKey();
	}

	public static boolean justShift(NativeEvent ne) {
		return !ne.getCtrlKey() && !ne.getMetaKey() && ne.getShiftKey();
	}

	public static boolean justAlt(NativeEvent ne) {
		return !ne.getCtrlKey() && !ne.getMetaKey() && ne.getShiftKey() && ne.getAltKey();
	}

	public static boolean cntrlOrCmdKey(NativeEvent ne) {
		if (!ne.getCtrlKey() && ne.getMetaKey() && !ne.getShiftKey()) {
			return true;
		}

		if (ne.getCtrlKey() && !ne.getMetaKey() && !ne.getShiftKey()) {
			return true;
		}

		return false;
	}

}