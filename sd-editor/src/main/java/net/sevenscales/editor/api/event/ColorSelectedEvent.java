package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.GwtEvent;
import net.sevenscales.editor.gfx.domain.ElementColor;

public class ColorSelectedEvent extends GwtEvent<ColorSelectedEventHandler> {
	public enum ColorTarget {
		BORDER, BACKGROUND, TEXT, ALL
	}

  public static Type<ColorSelectedEventHandler> TYPE = new Type<ColorSelectedEventHandler>();
	private ElementColor elementColor;
	private ColorTarget colorTarget;

	public ColorSelectedEvent(ElementColor elementColor, ColorTarget colorTarget) {
		this.elementColor = elementColor;
		this.colorTarget = colorTarget;
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
	
}
