package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.GwtEvent;

public class CancelButtonClickedEvent extends GwtEvent<CancelButtonClickedEventHandler> {
  public static Type<CancelButtonClickedEventHandler> TYPE = new Type<CancelButtonClickedEventHandler>();

  @Override
  protected void dispatch(CancelButtonClickedEventHandler handler) {
      handler.onSelection(this);
  }

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<CancelButtonClickedEventHandler> getAssociatedType() {
		return TYPE;
	}
}
