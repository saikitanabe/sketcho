package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.GwtEvent;

public class SurfaceMouseUpNoHandlingYetEvent extends GwtEvent<SurfaceMouseUpNoHandlingYetEventHandler> {
  public static Type<SurfaceMouseUpNoHandlingYetEventHandler> TYPE = new Type<SurfaceMouseUpNoHandlingYetEventHandler>();
	private int x;
	private int y;

  public SurfaceMouseUpNoHandlingYetEvent(int x, int y) {
  	this.x = x;
  	this.y = y;
	}

	@Override
  protected void dispatch(SurfaceMouseUpNoHandlingYetEventHandler handler) {
      handler.on(this);
  }

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<SurfaceMouseUpNoHandlingYetEventHandler> getAssociatedType() {
		return TYPE;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
}
