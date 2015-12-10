package net.sevenscales.domain.js;

import com.google.gwt.core.client.JavaScriptObject;

public class JsBoardPosition extends JavaScriptObject {
	protected JsBoardPosition() {
	}

	public final native boolean isZoomed()/*-{
		return this.zoom
	}-*/;
	public final native float getZoom()/*-{
		return this.zoom
	}-*/;

	public final native boolean isPositioned()/*-{
		return this.dx && this.dy
	}-*/;
	public final native int getDx()/*-{
		return this.dx
	}-*/;
	public final native int getDy()/*-{
		return this.dy
	}-*/;

	public final native boolean isMap()/*-{
		return this.map
	}-*/;

	public final static native JsBoardPosition get()/*-{
		return $wnd.ngGetBoardPositionFactory().getCurrent()
	}-*/;

}
