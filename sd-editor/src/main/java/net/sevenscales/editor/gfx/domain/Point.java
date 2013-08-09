package net.sevenscales.editor.gfx.domain;

import com.google.gwt.core.client.JavaScriptObject;

public class Point {
	public int x;
	public int y;

	public native JavaScriptObject getNativePoint()/*-{
		return {};
//		return {this.@net.st.shareddesign.editor.dojo.Point::x, 
//		        this.@net.st.shareddesign.editor.dojo.Point::y};
	}-*/;

}
