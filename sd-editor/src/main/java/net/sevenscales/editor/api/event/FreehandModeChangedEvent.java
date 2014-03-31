package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.GwtEvent;

public class FreehandModeChangedEvent extends GwtEvent<FreehandModeChangedEventHandler> {
  public static Type<FreehandModeChangedEventHandler> TYPE = new Type<FreehandModeChangedEventHandler>();
	private boolean enabled;
	private FreehandModeType modeType = FreehandModeType.FREEHAND_FREE;
	private boolean modeTypeChanged = false;

	public enum FreehandModeType {
		FREEHAND_FREE, FREEHAND_LINES;
	}
  
  public FreehandModeChangedEvent(boolean enabled) {
  	this.enabled = enabled;
	}

	public FreehandModeChangedEvent(boolean enabled, FreehandModeType modeType) {
		this.enabled = enabled;
		this.modeType = modeType;
		this.modeTypeChanged = true;
	}

	public FreehandModeChangedEvent(FreehandModeType modeType) {
		this.enabled = true;
		this.modeType = modeType;
		this.modeTypeChanged = true;
	}

  @Override
  protected void dispatch(FreehandModeChangedEventHandler handler) {
      handler.on(this);
  }

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<FreehandModeChangedEventHandler> getAssociatedType() {
		return TYPE;
	}
	
	public boolean isEnabled() {
		return enabled;
	}

	public FreehandModeType getModeType() {
		return modeType;
	}

	public boolean isModeTypeChanged() {
		return modeTypeChanged;
	}
}
