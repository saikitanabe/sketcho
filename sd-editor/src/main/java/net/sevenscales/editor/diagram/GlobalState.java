package net.sevenscales.editor.diagram;

import com.google.gwt.core.client.JsArrayString;

public class GlobalState {

	public static native void slideCreated()/*-{
		$wnd.globalStreams.slideCreatedStream.push()
	}-*/;
	
  public static native boolean isAddSlideMode()/*-{
    return $wnd.globalState.addSlideMode
  }-*/;

	public static native void disableAddSlideMode()/*-{
		if ($wnd.globalState.addSlideMode) {
			$wnd.globalStreams.addSlideStream.push()
		}
	}-*/;

  // 0: disabled
  // 1: link selection
  // 2: shape selection
  public static native int getNoteSelectionMode()/*-{
    if (typeof $wnd.getNoteSelectionMode === 'function') {
      return $wnd.getNoteSelectionMode();
    }
    
    return 0;
  }-*/;

  public static boolean isSelectionModeOn() {
    return GlobalState.isAddSlideMode() || GlobalState.getNoteSelectionMode() > 0;
  }

	public static native void notifySaveStatusChanged()/*-{
    $wnd.globalStreams.boardSaveStatusChangedStream.push()
  }-*/;
  
  public static native void notifySelected(JsArrayString items, String selectType)/*-{
    if (typeof $wnd.globalStreams.selectionStream !== 'undefined') {
      $wnd.globalStreams.selectionStream.push({
        operation: selectType,
        items: items,
      })
    }
  }-*/;

}