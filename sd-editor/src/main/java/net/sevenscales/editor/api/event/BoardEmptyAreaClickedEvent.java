package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.GwtEvent;

public class BoardEmptyAreaClickedEvent extends GwtEvent<BoardEmptyAreaClickEventHandler> {
  public static Type<BoardEmptyAreaClickEventHandler> TYPE = new Type<BoardEmptyAreaClickEventHandler>();
	private int x;
	private int y;

	public BoardEmptyAreaClickedEvent(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
  protected void dispatch(BoardEmptyAreaClickEventHandler handler) {
      handler.on(this);
  }

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<BoardEmptyAreaClickEventHandler> getAssociatedType() {
		return TYPE;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
}
