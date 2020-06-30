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

	public static native void notifySaveStatusChanged()/*-{
    $wnd.globalStreams.boardSaveStatusChangedStream.push()
  }-*/;
  
  public static native void notifySelected(JsArrayString items)/*-{
    if (typeof $wnd.globalStreams.selectionStream !== 'undefined') {
      $wnd.globalStreams.selectionStream.push({
        operation: 'select',
        items: items,
      })
    }
  }-*/;

}