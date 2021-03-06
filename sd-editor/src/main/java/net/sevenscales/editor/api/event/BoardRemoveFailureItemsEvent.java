package net.sevenscales.editor.api.event;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.shared.GwtEvent;

import net.sevenscales.domain.IDiagramItemRO;

public class BoardRemoveFailureItemsEvent extends GwtEvent<BoardRemoveFailureItemsEventHandler> {
  public static Type<BoardRemoveFailureItemsEventHandler> TYPE = new Type<BoardRemoveFailureItemsEventHandler>();
	private List<IDiagramItemRO> removed;

	public BoardRemoveFailureItemsEvent(IDiagramItemRO removed) {
		this.removed = new ArrayList<IDiagramItemRO>();
		this.removed.add(removed);
	}

  public BoardRemoveFailureItemsEvent(List<IDiagramItemRO> removed) {
  	this.removed = removed;
	}

	@Override
  protected void dispatch(BoardRemoveFailureItemsEventHandler handler) {
      handler.on(this);
  }

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<BoardRemoveFailureItemsEventHandler> getAssociatedType() {
		return TYPE;
	}
	
	public List<IDiagramItemRO> getRemoved() {
		return removed;
	}
}
