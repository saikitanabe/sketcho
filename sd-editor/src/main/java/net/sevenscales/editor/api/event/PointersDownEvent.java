package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.GwtEvent;

public class PointersDownEvent extends GwtEvent<PointersDownEventHandler> {
  public static Type<PointersDownEventHandler> TYPE = new Type<PointersDownEventHandler>();

  private int count;

  public PointersDownEvent(
    int count
  ) {
    this.count = count;
  }

  public int getPointersDownCount() {
    return this.count;
  }

  @Override
  protected void dispatch(PointersDownEventHandler handler) {
    handler.on(this);
  }

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<PointersDownEventHandler> getAssociatedType() {
		return TYPE;
	}
}
