package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.GwtEvent;

public class UndoEvent extends GwtEvent<UndoEventHandler> {
  public static Type<UndoEventHandler> TYPE = new Type<UndoEventHandler>();

  @Override
  protected void dispatch(UndoEventHandler handler) {
      handler.on(this);
  }

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<UndoEventHandler> getAssociatedType() {
		return TYPE;
	}
}
