package net.sevenscales.domain.js;

import com.google.gwt.core.client.JavaScriptObject;

public class ImageInfo extends JavaScriptObject {
  protected ImageInfo() {
  }

  public final native String getHash()/*-{
    return this.hash;
  }-*/;

  public final native String getFilename()/*-{
  	return this.filename;
  }-*/;
  public final native String getUrl()/*-{
    return this.url;
  }-*/;
  public final native String getThumbnailUrl()/*-{
    return this.thumbUrl;
  }-*/;
  public final native int getWidth()/*-{
    return this.width;    
  }-*/;
  public final native int getHeight()/*-{
    return this.height;
  }-*/;
}
