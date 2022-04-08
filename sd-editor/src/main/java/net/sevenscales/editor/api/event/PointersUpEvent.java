package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.GwtEvent;

public class PointersUpEvent extends GwtEvent<PointersUpEventHandler> {
  public static Type<PointersUpEventHandler> TYPE = new Type<PointersUpEventHandler>();

  @Override
  protected void dispatch(PointersUpEventHandler handler) {
    handler.on(this);
  }

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<PointersUpEventHandler> getAssociatedType() {
		return TYPE;
	}
}
