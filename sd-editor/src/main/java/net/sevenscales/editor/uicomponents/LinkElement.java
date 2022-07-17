package net.sevenscales.editor.uicomponents;

import com.google.gwt.core.client.JavaScriptObject;

public class LinkElement extends JavaScriptObject {
  protected LinkElement() {
  }

  public final native void setLink(
    String url,
    int left,
    int top
  )/*-{
    this.setLink(url, left, top)
  }-*/;

  public final native void remove()/*-{
    this.remove()
  }-*/;

}