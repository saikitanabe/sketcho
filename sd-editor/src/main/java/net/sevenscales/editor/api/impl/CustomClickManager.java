package net.sevenscales.editor.api.impl;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;

public class CustomClickManager implements HasClickHandlers {
	private List<ClickHandler> clickHandlers = new ArrayList<ClickHandler>();
	
	private class HandlerRegistrationImpl implements HandlerRegistration {
		private ClickHandler clickHandler;
		
		public HandlerRegistrationImpl(ClickHandler clickHandler) {
			this.clickHandler = clickHandler;
		}
		
		@Override
		public void removeHandler() {
			clickHandlers.remove(clickHandler);
		}
	}
	
	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		clickHandlers.add(handler);
		return new HandlerRegistrationImpl(handler);
	}
	
	@Override
	public void fireEvent(GwtEvent<?> event) {
		
	}
	
	protected void fireClick(Event event) {
		ClickEvent ce = new ClickEvent() {
		};
		ce.setNativeEvent(event);
		for (ClickHandler ch : clickHandlers) {
			ch.onClick(ce);
		}
		
//		NativeEvent evt = Document.get().createClickEvent(1, 0, 0, 0, 0, false,
//				false, false, false);
//		FastElementButton.this.element.dispatchEvent(evt);
		
	}


}
