package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.GwtEvent;

import net.sevenscales.editor.api.ot.OTOperation;

public class BoardUserEvent extends GwtEvent<BoardUserEventHandler> {
  public static Type<BoardUserEventHandler> TYPE = new Type<BoardUserEventHandler>();

  private OTOperation operation;
  private int x;
  private int y;
  private int targetx;
  private int targety;
  private String selectedCids;

  public BoardUserEvent(OTOperation operation) {
    this(operation, 0, 0, 0, 0, "");
  }

  public BoardUserEvent(OTOperation operation, int x, int y) {
    this(operation, x, y, 0, 0, "");
  }

  public BoardUserEvent(OTOperation operation, int x, int y, int targetx, int targety, String selectedCids) {
    this.operation = operation;
    this.x = x;
    this.y = y;
    this.targetx = targetx;
    this.targety = targety;
    this.selectedCids = selectedCids;
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

  public int getTargetX() {
    return targetx;
  }

  public int getTargetY() {
    return targety;
  }

  public String getSelectedCids() {
    return selectedCids;
  }
}
