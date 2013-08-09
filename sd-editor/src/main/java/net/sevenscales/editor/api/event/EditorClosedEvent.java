package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.GwtEvent;

public class EditorClosedEvent extends GwtEvent<EditorClosedEventHandler> {
  public static Type<EditorClosedEventHandler> TYPE = new Type<EditorClosedEventHandler>();

  @Override
  protected void dispatch(EditorClosedEventHandler handler) {
      handler.onSelection(this);
  }

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<EditorClosedEventHandler> getAssociatedType() {
		return TYPE;
	}
}
