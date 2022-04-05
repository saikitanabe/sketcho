package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.GwtEvent;

import net.sevenscales.editor.diagram.Diagram;

public class EditDiagramPropertiesStartedEvent extends GwtEvent<EditDiagramPropertiesStartedEventHandler> {
  public static Type<EditDiagramPropertiesStartedEventHandler> TYPE = new Type<EditDiagramPropertiesStartedEventHandler>();
  private Diagram diagram;

  public EditDiagramPropertiesStartedEvent(Diagram diagram) {
    this.diagram = diagram;
  }

  public Diagram getDiagram() {
    return this.diagram;
  }

  @Override
  protected void dispatch(EditDiagramPropertiesStartedEventHandler handler) {
      handler.on(this);
  }

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<EditDiagramPropertiesStartedEventHandler> getAssociatedType() {
		return TYPE;
	}
}
