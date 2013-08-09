package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.GwtEvent;

public class SurfaceScaleEvent extends GwtEvent<SurfaceScaleEventHandler> {
  public static Type<SurfaceScaleEventHandler> TYPE = new Type<SurfaceScaleEventHandler>();
	private int scaleFactor;

  public SurfaceScaleEvent(int scaleFactor) {
  	this.scaleFactor = scaleFactor;
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
}
