package net.sevenscales.editor.gfx.domain;

import com.google.gwt.core.client.JavaScriptObject;


public class JsSvgContainer extends JavaScriptObject {

	protected JsSvgContainer() {
	}

	public final native int getSvgNode()/*-{
		return this.rawNode
	}-*/;

	public final native String getSvg()/*-{
		var svg = this.rawNode;

		// return $wnd.svgNodeToString(this.rawNode)

		if (typeof $wnd.__svgNodeToString__ === 'function') {
			return $wnd.__svgNodeToString__(svg);
		}
		throw 'ERROR: __svgNodeToString__ not defined';
		return null;
	}-*/;

}
