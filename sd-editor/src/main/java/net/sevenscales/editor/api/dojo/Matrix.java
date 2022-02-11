package net.sevenscales.editor.api.dojo;

import com.google.gwt.core.client.JavaScriptObject;

public class Matrix extends JavaScriptObject {
  protected Matrix() {
  }

  public static final native Matrix create()/*-{
    return {xx: 1, xy: 0, yx: 0, yy: 1, dx: 0, dy: 0}
  }-*/;

  public final native double getXX()/*-{
    return this.xx
  }-*/;
  public final native double getXY()/*-{
    return this.xy
  }-*/;
  public final native double getYX()/*-{
    return this.yx
  }-*/;
  public final native double getYY()/*-{
    return this.yy
  }-*/;
  public final native double getDX()/*-{
    return this.dx
  }-*/;
  public final native double getDY()/*-{
    return this.dy
  }-*/;

  public final native int getXXInt()/*-{
    return parseInt(this.xx, 10)
  }-*/;
  public final native int getXYInt()/*-{
    return parseInt(this.xy, 10)
  }-*/;
  public final native int getYXInt()/*-{
    return parseInt(this.yx, 10)
  }-*/;
  public final native int getYYInt()/*-{
    return parseInt(this.yy, 10)
  }-*/;
  public final native int getDXInt()/*-{
    return parseInt(this.dx, 10)
  }-*/;
  public final native int getDYInt()/*-{
    return parseInt(this.dy, 10)
  }-*/;

  public final native void setDX(double dx)/*-{
    this.dx = dx
  }-*/;
  public final native void setDY(double dy)/*-{
    this.dy = dy
  }-*/;
}
