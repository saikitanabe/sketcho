package net.sevenscales.editor.uicomponents;

import com.google.gwt.core.client.JavaScriptObject;

public class LinkElement extends JavaScriptObject {
  protected LinkElement() {
  }

  public final native void setLink(
    String url
  )/*-{
    this.setLink(url)
  }-*/;
}