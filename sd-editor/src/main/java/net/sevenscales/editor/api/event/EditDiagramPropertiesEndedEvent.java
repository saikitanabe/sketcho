package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.GwtEvent;

public class EditDiagramPropertiesEndedEvent extends GwtEvent<EditDiagramPropertiesEndedEventHandler> {
  public static Type<EditDiagramPropertiesEndedEventHandler> TYPE = new Type<EditDiagramPropertiesEndedEventHandler>();

  @Override
  protected void dispatch(EditDiagramPropertiesEndedEventHandler handler) {
      handler.on(this);
  }

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<EditDiagramPropertiesEndedEventHandler> getAssociatedType() {
		return TYPE;
	}
}
