package net.sevenscales.editor.api.event;

import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.gfx.domain.MatrixPointJS;

import com.google.gwt.event.shared.GwtEvent;

public class ShowDiagramPropertyTextEditorEvent extends GwtEvent<ShowDiagramPropertyTextEditorEventHandler> {
  public static Type<ShowDiagramPropertyTextEditorEventHandler> TYPE = new Type<ShowDiagramPropertyTextEditorEventHandler>();
	private Diagram diagram;
	private MatrixPointJS point;
	private boolean justCreated;
  
  public ShowDiagramPropertyTextEditorEvent(Diagram diagram) {
  	this(diagram, false);
	}
  
  public ShowDiagramPropertyTextEditorEvent(Diagram diagram, boolean justCreated) {
  	this.diagram = diagram;
  	this.justCreated = justCreated;
	}

  public ShowDiagramPropertyTextEditorEvent(Diagram diagram, MatrixPointJS point) {
  	this.diagram = diagram;
  	this.point = point;
	}

	@Override
  protected void dispatch(ShowDiagramPropertyTextEditorEventHandler handler) {
      handler.on(this);
  }

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<ShowDiagramPropertyTextEditorEventHandler> getAssociatedType() {
		return TYPE;
	}
	
	public Diagram getDiagram() {
		return diagram;
	}

	public MatrixPointJS getPoint() {
		return point;
	}
	
	public boolean isJustCreated() {
		return justCreated;
	}
	
	public ShowDiagramPropertyTextEditorEvent setJustCreated(boolean justCreated) {
		this.justCreated = justCreated;
		return this;
	}
}
