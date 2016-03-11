package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.GwtEvent;

public class SurfaceScaleEvent extends GwtEvent<SurfaceScaleEventHandler> {
  public static Type<SurfaceScaleEventHandler> TYPE = new Type<SurfaceScaleEventHandler>();
	private int scaleFactor;
	private boolean wheel;

  public SurfaceScaleEvent(int scaleFactor) {
  	this.scaleFactor = scaleFactor;
  	this.wheel = false;
	}

  public SurfaceScaleEvent(int scaleFactor, boolean wheel) {
  	this.scaleFactor = scaleFactor;
  	this.wheel = wheel;
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
}
