package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.GwtEvent;

import net.sevenscales.editor.diagram.Diagram;

public class CommentThreadDeletedEvent extends GwtEvent<CommentThreadDeletedEventHandler> {
  public static Type<CommentThreadDeletedEventHandler> TYPE = new Type<CommentThreadDeletedEventHandler>();
  private Diagram commentThreadElement;

  public CommentThreadDeletedEvent(Diagram commentThreadElement) {
  	this.commentThreadElement = commentThreadElement;
  }

  public Diagram getCommentThreadElement() {
  	return commentThreadElement;
  }

  @Override
  protected void dispatch(CommentThreadDeletedEventHandler handler) {
    handler.on(this);
  }

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<CommentThreadDeletedEventHandler> getAssociatedType() {
		return TYPE;
	}
}
