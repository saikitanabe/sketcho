package net.sevenscales.editor.api.event;

import java.util.ArrayList;
import java.util.List;

import net.sevenscales.editor.diagram.Diagram;

import com.google.gwt.event.shared.GwtEvent;

public class DiagramElementAddedEvent extends GwtEvent<DiagramElementAddedEventHandler> {
  public static Type<DiagramElementAddedEventHandler> TYPE = new Type<DiagramElementAddedEventHandler>();
	private List<Diagram> diagrams;
	private boolean duplicate;

  @Override
	public String toString() {
		return "DiagramElementAddedEvent [diagrams=" + diagrams + "]";
	}

	public DiagramElementAddedEvent(Diagram diagram, boolean duplicate) {
  	this.diagrams = new ArrayList<Diagram>();
  	diagrams.add(diagram);
  	this.duplicate = duplicate;
	}

	public DiagramElementAddedEvent(List<Diagram> diagrams, boolean duplicate) {
  	this.diagrams = diagrams;
  	this.duplicate = duplicate;
	}

	@Override
  protected void dispatch(DiagramElementAddedEventHandler handler) {
      handler.onAdded(this);
  }

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<DiagramElementAddedEventHandler> getAssociatedType() {
		return TYPE;
	}
	
	public List<Diagram> getDiagrams() {
		return diagrams;
	}
	
	public boolean isDuplicate() {
		return duplicate;
	}
}
