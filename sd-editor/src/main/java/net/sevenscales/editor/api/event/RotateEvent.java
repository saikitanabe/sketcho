package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.GwtEvent;

public class RotateEvent extends GwtEvent<RotateEventHandler> {
  public static Type<RotateEventHandler> TYPE = new Type<RotateEventHandler>();

  private int rotateDeg;

  public RotateEvent(int rotateDeg) {
  	this.rotateDeg = rotateDeg;
  }

  public int getRotateDeg() {
  	return rotateDeg;
  }

  @Override
  protected void dispatch(RotateEventHandler handler) {
  	handler.on(this);
  }

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<RotateEventHandler> getAssociatedType() {
		return TYPE;
	}
}
