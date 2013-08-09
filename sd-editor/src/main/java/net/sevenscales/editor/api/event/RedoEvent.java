package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.GwtEvent;

public class RedoEvent extends GwtEvent<RedoEventHandler> {
  public static Type<RedoEventHandler> TYPE = new Type<RedoEventHandler>();

  @Override
  protected void dispatch(RedoEventHandler handler) {
      handler.on(this);
  }

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<RedoEventHandler> getAssociatedType() {
		return TYPE;
	}
}
