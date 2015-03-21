package net.sevenscales.editor.diagram;

class GlobalState {
	
  public static native boolean isAddSlideMode()/*-{
    return $wnd.globalState.addSlideMode
  }-*/;

	public static native void disableAddSlideMode()/*-{
		if ($wnd.globalState.addSlideMode) {
			$wnd.globalStreams.addSlideStream.push()
		}
	}-*/;
}