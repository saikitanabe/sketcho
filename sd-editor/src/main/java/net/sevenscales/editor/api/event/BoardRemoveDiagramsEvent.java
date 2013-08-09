package net.sevenscales.editor.api.event;

import java.util.Set;

import net.sevenscales.editor.diagram.Diagram;

import com.google.gwt.event.shared.GwtEvent;

public class BoardRemoveDiagramsEvent extends GwtEvent<BoardRemoveDiagramsEventHandler> {
  public static Type<BoardRemoveDiagramsEventHandler> TYPE = new Type<BoardRemoveDiagramsEventHandler>();
	private Set<Diagram> removed;

  public BoardRemoveDiagramsEvent(Set<Diagram> removed) {
  	this.removed = removed;
	}

	@Override
  protected void dispatch(BoardRemoveDiagramsEventHandler handler) {
      handler.on(this);
  }

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<BoardRemoveDiagramsEventHandler> getAssociatedType() {
		return TYPE;
	}
	
	public Set<Diagram> getRemoved() {
		return removed;
	}
}
