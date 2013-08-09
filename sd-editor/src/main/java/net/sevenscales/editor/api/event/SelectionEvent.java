package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.GwtEvent;

public class SelectionEvent extends GwtEvent<SelectionEventHandler> {
  public static Type<SelectionEventHandler> TYPE = new Type<SelectionEventHandler>();

  @Override
  protected void dispatch(SelectionEventHandler handler) {
      handler.onSelection(this);
  }

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<SelectionEventHandler> getAssociatedType() {
		return TYPE;
	}
}
