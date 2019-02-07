package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.GwtEvent;

public class SurfaceScaleEvent extends GwtEvent<SurfaceScaleEventHandler> {
  public static Type<SurfaceScaleEventHandler> TYPE = new Type<SurfaceScaleEventHandler>();
	private int scaleFactor;
	private boolean wheel;
	private int middleX;
	private int middleY;

  public SurfaceScaleEvent(int scaleFactor) {
  	this.scaleFactor = scaleFactor;
  	this.wheel = false;
	}

  public SurfaceScaleEvent(int scaleFactor, boolean wheel) {
  	this.scaleFactor = scaleFactor;
  	this.wheel = wheel;
  }
  
  public SurfaceScaleEvent(
    int scaleFactor,
    boolean wheel,
    int middleX,
    int middleY
  ) {
  	this.scaleFactor = scaleFactor;
    this.wheel = wheel;
    this.middleX = middleX;
    this.middleY = middleY;
  }
  
	@Override
  protected void dispatch(SurfaceScaleEventHandler handler) {
      handler.on(this);
  }

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<SurfaceScaleEventHandler> getAssociatedType() {
		return TYPE;
	}
	
	public int getScaleFactor() {
		return scaleFactor;
	}

	public boolean isWheel() {
		return wheel;
  }
  
  public int getMiddleX() {
    return middleX;
  }

  public int getMiddleY() {
    return middleY;
  }
}
