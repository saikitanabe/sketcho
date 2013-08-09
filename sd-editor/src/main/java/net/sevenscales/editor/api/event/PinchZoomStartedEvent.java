package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.GwtEvent;

public class PinchZoomStartedEvent extends GwtEvent<PinchZoomStartedEventHandler> {
	public static Type<PinchZoomStartedEventHandler> TYPE = new Type<PinchZoomStartedEventHandler>();

	@Override
	protected void dispatch(PinchZoomStartedEventHandler handler) {
		handler.on(this);
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<PinchZoomStartedEventHandler> getAssociatedType() {
		return TYPE;
	}
}
