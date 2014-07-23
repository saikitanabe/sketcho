package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.GwtEvent;

public class BackgroundMoveStartedEvent extends GwtEvent<BackgroundMoveStartedEventHandler> {
  public static Type<BackgroundMoveStartedEventHandler> TYPE = new Type<BackgroundMoveStartedEventHandler>();

  @Override
  protected void dispatch(BackgroundMoveStartedEventHandler handler) {
      handler.on(this);
  }

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<BackgroundMoveStartedEventHandler> getAssociatedType() {
		return TYPE;
	}
}
