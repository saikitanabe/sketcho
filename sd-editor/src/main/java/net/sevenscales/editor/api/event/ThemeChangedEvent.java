package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.GwtEvent;

public class ThemeChangedEvent extends GwtEvent<ThemeChangedEventHandler> {
  public static Type<ThemeChangedEventHandler> TYPE = new Type<ThemeChangedEventHandler>();

  @Override
  protected void dispatch(ThemeChangedEventHandler handler) {
    handler.on(this);
  }

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<ThemeChangedEventHandler> getAssociatedType() {
		return TYPE;
	}
}
