package net.sevenscales.editor.api.event;

import net.sevenscales.editor.diagram.Diagram;

import com.google.gwt.event.shared.GwtEvent;

public class SelectionMouseUpEvent extends GwtEvent<SelectionMouseUpEventHandler> {
  public static Type<SelectionMouseUpEventHandler> TYPE = new Type<SelectionMouseUpEventHandler>();
	private Diagram[] diagrams;
	private Diagram lastSelected;
  
  public SelectionMouseUpEvent(Diagram diagram) {
  	this.diagrams = new Diagram[1];
  	this.diagrams[0] = diagram;
  	this.lastSelected = diagram.getOwnerComponent();
	}
  
  public SelectionMouseUpEvent(Diagram[] diagrams, Diagram lastSelected) {
  	this.diagrams = diagrams;
  	// assert(lastSelected != null);
  	this.lastSelected = lastSelected.getOwnerComponent();
  }

  @Override
  protected void dispatch(SelectionMouseUpEventHandler handler) {
      handler.onSelection(this);
  }

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<SelectionMouseUpEventHandler> getAssociatedType() {
		return TYPE;
	}
	
	public boolean isOnlyOne() {
		return diagrams.length == 1;
	}
	
	public Diagram getFirst() {
		if (diagrams.length > 0) {
			return diagrams[0];
		}
		return null;
	}
	
	public Diagram[] getDiagrams() {
		return diagrams;
	}
	
	public Diagram getLastSelected() {
		return lastSelected;
	}
}
