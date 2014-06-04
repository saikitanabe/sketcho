package net.sevenscales.editor.gfx.domain;

import com.google.gwt.core.client.JavaScriptObject;

public class PointDouble {
	public double x;
	public double y;

	public PointDouble() {
	}

	public PointDouble(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public native JavaScriptObject getNativePoint()/*-{
		return {x: this.@net.sevenscales.editor.gfx.domain.PointDouble::x, 
		        y: this.@net.sevenscales.editor.gfx.domain.PointDouble::y};
	}-*/;
}
