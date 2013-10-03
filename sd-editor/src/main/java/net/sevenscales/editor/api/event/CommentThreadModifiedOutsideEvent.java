package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.GwtEvent;

import net.sevenscales.editor.diagram.Diagram;

public class CommentThreadModifiedOutsideEvent extends GwtEvent<CommentThreadModifiedOutsideEventHandler> {
  public static Type<CommentThreadModifiedOutsideEventHandler> TYPE = new Type<CommentThreadModifiedOutsideEventHandler>();
  private Diagram commentThreadElement;

  public CommentThreadModifiedOutsideEvent(Diagram commentThreadElement) {
  	this.commentThreadElement = commentThreadElement;
  }

  public Diagram getCommentThreadElement() {
  	return commentThreadElement;
  }

  @Override
  protected void dispatch(CommentThreadModifiedOutsideEventHandler handler) {
    handler.on(this);
  }

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<CommentThreadModifiedOutsideEventHandler> getAssociatedType() {
		return TYPE;
	}
}
