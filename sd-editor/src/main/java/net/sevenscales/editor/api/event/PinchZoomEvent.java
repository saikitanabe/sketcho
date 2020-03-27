package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.GwtEvent;

public class PinchZoomEvent extends GwtEvent<PinchZoomEventHandler> {
  public static Type<PinchZoomEventHandler> TYPE = new Type<PinchZoomEventHandler>();

  private boolean started;
  
  public PinchZoomEvent(boolean started) {
    this.started = started;
  }

  public boolean getStarted() {
    return started;
  }

	@Override
	protected void dispatch(PinchZoomEventHandler handler) {
		handler.on(this);
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<PinchZoomEventHandler> getAssociatedType() {
		return TYPE;
	}
}
