package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.GwtEvent;
import net.sevenscales.editor.gfx.domain.ElementColor;

public class ColorSelectedEvent extends GwtEvent<ColorSelectedEventHandler> {
	public enum ColorTarget {
		BORDER, BACKGROUND, TEXT, ALL
	}

	public enum ColorSetType {
		NORMAL, RESTORE_COLORS, TRANSPARENT
	}

  public static Type<ColorSelectedEventHandler> TYPE = new Type<ColorSelectedEventHandler>();
	private ElementColor elementColor;
	private ColorTarget colorTarget;
	private ColorSetType colorSetType;

	public ColorSelectedEvent(ElementColor elementColor, ColorTarget colorTarget, ColorSetType colorSetType) {
		this.elementColor = elementColor;
		this.colorTarget = colorTarget;
		this.colorSetType = colorSetType;
	}

	@Override
  protected void dispatch(ColorSelectedEventHandler handler) {
      handler.onSelection(this);
  }

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<ColorSelectedEventHandler> getAssociatedType() {
		return TYPE;
	}
	
	public ElementColor getElementColor() {
		return elementColor;
	}
	
	public ColorTarget getColorTarget() {
		return colorTarget;
	}

	public ColorSetType getColorSetType() {
		return colorSetType;
	}
	
}
