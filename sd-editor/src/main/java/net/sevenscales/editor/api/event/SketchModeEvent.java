package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.GwtEvent;

public class SketchModeEvent extends GwtEvent<SketchModeEventHandler> {
  public static Type<SketchModeEventHandler> TYPE = new Type<SketchModeEventHandler>();
	private boolean enabled;
  public SketchModeEvent(boolean enabled) {
  	this.enabled = enabled;
	}

  @Override
  protected void dispatch(SketchModeEventHandler handler) {
      handler.on(this);
  }

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<SketchModeEventHandler> getAssociatedType() {
		return TYPE;
	}
	
	public boolean isEnabled() {
		return enabled;
	}

}
