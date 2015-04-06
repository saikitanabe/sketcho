package net.sevenscales.editor.diagram;

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

}