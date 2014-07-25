package net.sevenscales.editor.api.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.sevenscales.editor.api.ActionType;
import net.sevenscales.editor.content.utils.DiagramHelpers;
import net.sevenscales.editor.diagram.Diagram;

import com.google.gwt.event.shared.GwtEvent;

public class PotentialOnChangedEvent extends GwtEvent<PotentialOnChangedEventHandler> {
  public static Type<PotentialOnChangedEventHandler> TYPE = new Type<PotentialOnChangedEventHandler>();
	private List<Diagram> diagrams;
  
  public PotentialOnChangedEvent(Diagram diagram) {
  	diagrams = new ArrayList<Diagram>();
  	diagrams.add(diagram);
	}

  public PotentialOnChangedEvent(List<Diagram> diagrams) {
  	this.diagrams = diagrams;
	}
  
  public PotentialOnChangedEvent(Set<? extends Diagram> diagrams) {
  	this.diagrams = DiagramHelpers.filterOwnerDiagramsAsListOrderByType(diagrams, ActionType.NONE);
	}

	@Override
  protected void dispatch(PotentialOnChangedEventHandler handler) {
      handler.on(this);
  }

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<PotentialOnChangedEventHandler> getAssociatedType() {
		return TYPE;
	}
	
	public List<Diagram> getDiagrams() {
		return diagrams;
	}
}
