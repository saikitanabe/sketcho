package net.sevenscales.editor.api.event;

import net.sevenscales.editor.content.RelationShipType;

import com.google.gwt.event.shared.GwtEvent;

public class RelationshipTypeSelectedEvent extends GwtEvent<RelationshipTypeSelectedEventHandler> {
  public static Type<RelationshipTypeSelectedEventHandler> TYPE = new Type<RelationshipTypeSelectedEventHandler>();
	private RelationShipType relationshipType;
  
  public RelationshipTypeSelectedEvent(RelationShipType relationshipType) {
  	this.relationshipType = relationshipType;
	}

  @Override
  protected void dispatch(RelationshipTypeSelectedEventHandler handler) {
      handler.onSelection(this);
  }

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<RelationshipTypeSelectedEventHandler> getAssociatedType() {
		return TYPE;
	}
	
	public RelationShipType getRelationshipType() {
		return relationshipType;
	}
}
