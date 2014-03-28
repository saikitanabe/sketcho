package net.sevenscales.editor.gfx.domain;

import com.google.gwt.core.client.JavaScriptObject;

public class Point {
	public int x;
	public int y;

	public Point() {
	}

	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public native JavaScriptObject getNativePoint()/*-{
		return {x: this.@net.sevenscales.editor.gfx.domain.Point::x, 
		        y: this.@net.sevenscales.editor.gfx.domain.Point::y};
	}-*/;
}
