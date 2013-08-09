package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.GwtEvent;

public class UnselectAllEvent extends GwtEvent<UnselecteAllEventHandler> {
  public static Type<UnselecteAllEventHandler> TYPE = new Type<UnselecteAllEventHandler>();

  @Override
  protected void dispatch(UnselecteAllEventHandler handler) {
      handler.onUnselectAll(this);
  }

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<UnselecteAllEventHandler> getAssociatedType() {
		return TYPE;
	}
}
