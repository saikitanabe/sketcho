package net.sevenscales.editor.gfx.domain;

import com.google.gwt.core.client.JavaScriptObject;


public class JsSvg extends JavaScriptObject {

	protected JsSvg() {
	}

	public final native String getSvg()/*-{
		return this.svg
  }-*/;
  
	public final native int getWidth()/*-{
		return this.width
  }-*/;
  
	public final native int getHeight()/*-{
		return this.height
	}-*/;

}
