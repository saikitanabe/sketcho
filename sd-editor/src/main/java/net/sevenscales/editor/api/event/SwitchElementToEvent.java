package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.GwtEvent;

import net.sevenscales.domain.ElementType;

public class SwitchElementToEvent extends GwtEvent<SwitchElementToEventHandler> {
  public static Type<SwitchElementToEventHandler> TYPE = new Type<SwitchElementToEventHandler>();

  private ElementType elementType;

  public SwitchElementToEvent(ElementType elementType) {
  	this.elementType = elementType;
	}

	@Override
  protected void dispatch(SwitchElementToEventHandler handler) {
      handler.on(this);
  }

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<SwitchElementToEventHandler> getAssociatedType() {
		return TYPE;
	}
	
	public ElementType getElementType() {
		return elementType;
	}
	
}
