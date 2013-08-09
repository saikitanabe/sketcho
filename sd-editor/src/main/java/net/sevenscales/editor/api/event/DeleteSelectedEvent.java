package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.GwtEvent;

public class DeleteSelectedEvent extends GwtEvent<DeleteSelectedEventHandler> {
  public static Type<DeleteSelectedEventHandler> TYPE = new Type<DeleteSelectedEventHandler>();

  @Override
  protected void dispatch(DeleteSelectedEventHandler handler) {
      handler.onSelection(this);
  }

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<DeleteSelectedEventHandler> getAssociatedType() {
		return TYPE;
	}
}
