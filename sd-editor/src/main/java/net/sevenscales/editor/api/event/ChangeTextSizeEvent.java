package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.GwtEvent;

public class ChangeTextSizeEvent extends GwtEvent<ChangeTextSizeEventHandler> {
  public static Type<ChangeTextSizeEventHandler> TYPE = new Type<ChangeTextSizeEventHandler>();

  private int textSize;

  public ChangeTextSizeEvent(int textSize) {
  	this.textSize = textSize;
  }

  public int getTextSize() {
  	return textSize;
  }

  @Override
  protected void dispatch(ChangeTextSizeEventHandler handler) {
  	handler.on(this);
  }

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<ChangeTextSizeEventHandler> getAssociatedType() {
		return TYPE;
	}
}
