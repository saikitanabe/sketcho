package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.GwtEvent;

public class SaveButtonClickedEvent extends GwtEvent<SaveButtonClickedEventHandler> {
  public static Type<SaveButtonClickedEventHandler> TYPE = new Type<SaveButtonClickedEventHandler>();

  @Override
  protected void dispatch(SaveButtonClickedEventHandler handler) {
      handler.onSelection(this);
  }

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<SaveButtonClickedEventHandler> getAssociatedType() {
		return TYPE;
	}
}
