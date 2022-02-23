package net.sevenscales.editor.gfx.domain;

import com.google.gwt.core.client.JavaScriptObject;

public class ElementSize extends JavaScriptObject {

  protected ElementSize() {
  }

  public native static final ElementSize create(int width, int height)/*-{
    return {
      width: width,
      height: height
    };
  }-*/;

  public final native double getWidth()/*-{
    return this.width
  }-*/;

  public final native double getHeight()/*-{
    return this.height
  }-*/;

}