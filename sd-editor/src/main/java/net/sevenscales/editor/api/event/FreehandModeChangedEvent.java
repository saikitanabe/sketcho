package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.GwtEvent;

public class FreehandModeChangedEvent extends GwtEvent<FreehandModeChangedEventHandler> {
  public static Type<FreehandModeChangedEventHandler> TYPE = new Type<FreehandModeChangedEventHandler>();
	private boolean enabled;
	private FreehandModeType modeType = FreehandModeType.FREEHAND_SMOOTH;
	private boolean modeTypeChanged = false;

	public enum FreehandModeType {
		FREEHAND_MORE(2, "More"), FREEHAND_SMOOTH(4, "Smooth"), FREEHAND_LESS(6, "Less");

		private int value;
		private String modeName;
		
		private FreehandModeType(int value, String modeName) {
			this.value = value;
			this.modeName = modeName;
		}

		public int value() {
			return value;
		}

		@Override
		public String toString() {
			return modeName;
		}

	}
  
  public FreehandModeChangedEvent(boolean enabled) {
  	this.enabled = enabled;
	}

	public FreehandModeChangedEvent(boolean enabled, FreehandModeType modeType) {
		this.enabled = enabled;
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
