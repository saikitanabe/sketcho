package net.sevenscales.editor.api.event;

import net.sevenscales.editor.diagram.utils.Color;

import com.google.gwt.event.shared.GwtEvent;

public class ColorSelectedEvent extends GwtEvent<ColorSelectedEventHandler> {
	public enum ColorTarget {
		BORDER, BACKGROUND, TEXT
	}

  public static Type<ColorSelectedEventHandler> TYPE = new Type<ColorSelectedEventHandler>();
	private Color color;
	private ColorTarget colorTarget;

	public ColorSelectedEvent(Color color, ColorTarget colorTarget) {
		this.color = color;
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
	
	public Color getColor() {
		return color;
	}
	
	public ColorTarget getColorTarget() {
		return colorTarget;
	}
	
}
