package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.GwtEvent;

import net.sevenscales.editor.diagram.Diagram;

public class CommentDeletedEvent extends GwtEvent<CommentDeletedEventHandler> {
  public static Type<CommentDeletedEventHandler> TYPE = new Type<CommentDeletedEventHandler>();
  private Diagram commentElement;

  public CommentDeletedEvent(Diagram commentElement) {
  	this.commentElement = commentElement;
  }

  public Diagram getCommentElement() {
  	return commentElement;
  }

  @Override
  protected void dispatch(CommentDeletedEventHandler handler) {
    handler.on(this);
  }

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<CommentDeletedEventHandler> getAssociatedType() {
		return TYPE;
	}
}
