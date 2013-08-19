package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.GwtEvent;

import net.sevenscales.editor.api.ot.OTOperation;

public class BoardUserEvent extends GwtEvent<BoardUserEventHandler> {
  public static Type<BoardUserEventHandler> TYPE = new Type<BoardUserEventHandler>();

  private OTOperation operation;
  private int x;
  private int y;

  public BoardUserEvent(OTOperation operation) {
  	this.operation = operation;
  }

  public BoardUserEvent(OTOperation operation, int x, int y) {
  	this.operation = operation;
  	this.x = x;
  	this.y = y;
  }

  @Override
  protected void dispatch(BoardUserEventHandler handler) {
      handler.on(this);
  }

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<BoardUserEventHandler> getAssociatedType() {
		return TYPE;
	}

	public OTOperation getOperation() {
		return operation;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}
