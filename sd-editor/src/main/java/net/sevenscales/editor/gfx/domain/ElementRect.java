package net.sevenscales.editor.gfx.domain;

import com.google.gwt.core.client.JavaScriptObject;

public class ElementRect extends JavaScriptObject {

  protected ElementRect() {
  }

  public native static final ElementRect create(
    int left,
    int top,
    int width, 
    int height
  )/*-{
    return {
      left: left,
      top: top,
      width: width,
      height: height
    };
  }-*/;

  public final native double getLeft()/*-{
    return this.left
  }-*/;
  public final native double getTop()/*-{
    return this.top
  }-*/;
  public final native double getWidth()/*-{
    return this.width
  }-*/;

  public final native double getHeight()/*-{
    return this.height
  }-*/;

}