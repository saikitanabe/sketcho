package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.GwtEvent;

public class StartSelectToolEvent extends GwtEvent<StartSelectToolEventHandler> {
  public static Type<StartSelectToolEventHandler> TYPE = new Type<StartSelectToolEventHandler>();

  @Override
  protected void dispatch(StartSelectToolEventHandler handler) {
      handler.on(this);
  }

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<StartSelectToolEventHandler> getAssociatedType() {
		return TYPE;
	}
}
