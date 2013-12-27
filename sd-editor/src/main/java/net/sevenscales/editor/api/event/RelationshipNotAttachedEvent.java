package net.sevenscales.editor.api.event;

import net.sevenscales.editor.diagram.drag.Anchor;
import net.sevenscales.editor.uicomponents.uml.Relationship2;

import com.google.gwt.event.shared.GwtEvent;

public class RelationshipNotAttachedEvent extends GwtEvent<RelationshipNotAttachedEventHandler> {
  public static Type<RelationshipNotAttachedEventHandler> TYPE = new Type<RelationshipNotAttachedEventHandler>();
	private Relationship2 relationship;
	private Anchor anchor;
	private int x;
	private int y;

  public RelationshipNotAttachedEvent(int x, int y, Relationship2 relationship2,
			Anchor anchor) {
  	this.x = x;
  	this.y = y;
  	this.relationship = relationship2;
  	this.anchor = anchor;
	}

	@Override
  protected void dispatch(RelationshipNotAttachedEventHandler handler) {
      handler.onNotAttached(this);
  }

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<RelationshipNotAttachedEventHandler> getAssociatedType() {
		return TYPE;
	}
	
	public Relationship2 getRelationship() {
		return relationship;
	}
	
	public Anchor getAnchor() {
		return anchor;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
}
