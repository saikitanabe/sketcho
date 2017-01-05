package net.sevenscales.editor.api.impl;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;

public class FastElementButton extends CustomClickManager {
	private Element element;
	protected int startX;
	protected int startY;
	private boolean touchMoved;
	private boolean touchHandled;
	private boolean clickHandled;
	
	public FastElementButton(Element element) {
		this.element = element;
		
		DOM.sinkEvents((com.google.gwt.user.client.Element) element.cast(),
				Event.ONCLICK | Event.TOUCHEVENTS);
		DOM.setEventListener((com.google.gwt.user.client.Element) element.cast(),
				new EventListener() {
					@Override
					public void onBrowserEvent(Event event) {
						switch (DOM.eventGetType(event)) {
						case Event.ONCLICK:
							onClick(event);
							break;
						case Event.ONTOUCHSTART:
							touchStart(event);
							break;
						case Event.ONTOUCHCANCEL:
							touchCancel(event);
							break;
						case Event.ONTOUCHEND:
							touchEnd(event);
							break;
						case Event.ONTOUCHMOVE:
							touchMove(event);
							break;
					}
				}
			});
	}
	
	private void onClick(Event event) {
		event.stopPropagation();
		// if (touchHandled) {
		// 	touchHandled = false;
		// 	clickHandled = true;
		// } else {
		// 	if (clickHandled) {
		// 		event.preventDefault();
		// 	} else {
		// 		clickHandled = false;
		// 		fireClick(event);
		// 	}
		// }

		event.preventDefault();
		fireClick(event);

	}
	
	private void touchStart(Event event) {
		touchHandled = false;
		touchMoved = false;
		startX = event.getClientX();
		startY = event.getClientY();
	}
	
	private void touchCancel(Event event) {
		
	}
	
	private void touchEnd(Event event) {
		
		if (!touchMoved) {
//			event.stopPropagation();
//			event.preventDefault();
			touchHandled = true;
			fireClick(event);
		}
	}

	private void touchMove(Event event) {
		if (event.getTouches().length() != 1) {
			return;
		}
		
		Touch touch = event.getTouches().get(0);
		int deltaX = Math.abs(startX - touch.getClientX());
		int deltaY = Math.abs(startY - touch.getClientY());

		if (deltaX > 5 || deltaY > 5) {
			touchMoved = true;
		}
	}

}
