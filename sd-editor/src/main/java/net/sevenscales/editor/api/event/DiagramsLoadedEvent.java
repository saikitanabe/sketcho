package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.GwtEvent;

public class DiagramsLoadedEvent extends GwtEvent<DiagramsLoadedEventHandler> {
  public static Type<DiagramsLoadedEventHandler> TYPE = new Type<DiagramsLoadedEventHandler>();

  @Override
  protected void dispatch(DiagramsLoadedEventHandler handler) {
      handler.on(this);
  }

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<DiagramsLoadedEventHandler> getAssociatedType() {
		return TYPE;
	}
}
