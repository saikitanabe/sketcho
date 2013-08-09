package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.GwtEvent;

public class EditDiagramPropertiesStartedEvent extends GwtEvent<EditDiagramPropertiesStartedEventHandler> {
  public static Type<EditDiagramPropertiesStartedEventHandler> TYPE = new Type<EditDiagramPropertiesStartedEventHandler>();

  @Override
  protected void dispatch(EditDiagramPropertiesStartedEventHandler handler) {
      handler.on(this);
  }

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<EditDiagramPropertiesStartedEventHandler> getAssociatedType() {
		return TYPE;
	}
}
