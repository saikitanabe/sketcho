package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.GwtEvent;

import net.sevenscales.editor.diagram.Diagram;

public class CommentSelectedEvent extends GwtEvent<CommentSelectedEventHandler> {
  public static Type<CommentSelectedEventHandler> TYPE = new Type<CommentSelectedEventHandler>();
  private Diagram commentElement;

  public CommentSelectedEvent(Diagram commentElement) {
  	this.commentElement = commentElement;
  }

  public Diagram getCommentElement() {
  	return commentElement;
  }

  @Override
  protected void dispatch(CommentSelectedEventHandler handler) {
    handler.on(this);
  }

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<CommentSelectedEventHandler> getAssociatedType() {
		return TYPE;
	}
}
