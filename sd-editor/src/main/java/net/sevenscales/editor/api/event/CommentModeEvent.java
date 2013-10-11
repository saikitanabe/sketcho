package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.GwtEvent;

public class CommentModeEvent extends GwtEvent<CommentModeEventHandler> {
  public static Type<CommentModeEventHandler> TYPE = new Type<CommentModeEventHandler>();
	private boolean enabled;
  public CommentModeEvent(boolean enabled) {
  	this.enabled = enabled;
	}

  @Override
  protected void dispatch(CommentModeEventHandler handler) {
      handler.on(this);
  }

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<CommentModeEventHandler> getAssociatedType() {
		return TYPE;
	}
	
	public boolean isEnabled() {
		return enabled;
	}

}
