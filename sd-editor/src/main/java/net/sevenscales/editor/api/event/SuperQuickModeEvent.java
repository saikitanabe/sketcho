package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.GwtEvent;

public class SuperQuickModeEvent extends GwtEvent<SuperQuickModeEventHandler> {
  public static Type<SuperQuickModeEventHandler> TYPE = new Type<SuperQuickModeEventHandler>();
	private boolean enabled;
  public SuperQuickModeEvent(boolean enabled) {
  	this.enabled = enabled;
	}

  @Override
  protected void dispatch(SuperQuickModeEventHandler handler) {
      handler.on(this);
  }

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<SuperQuickModeEventHandler> getAssociatedType() {
		return TYPE;
	}
	
	public boolean isEnabled() {
		return enabled;
	}

}
