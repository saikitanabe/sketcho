package net.sevenscales.editor.api.impl;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Event;

import net.sevenscales.domain.utils.Debug;
import net.sevenscales.editor.api.event.hammer.Hammer2;
import net.sevenscales.editor.api.event.hammer.Hammer2TapEventHandler;

public class FastElementButton extends CustomClickManager implements 
  Hammer2TapEventHandler {
	// private Element element;
	protected int startX;
	protected int startY;
	// private boolean touchMoved;
	// private boolean touchHandled;
	// private boolean clickHandled;
	
	public FastElementButton(Element element) {
		// this.element = element;

    new Hammer2(element).on("tap", this);
  }
  
  public void onHammerTap(Event event) {
    Debug.log("FastElementButton.onHammerTap Pointer click...");
    fireClick(event);
  }

  // private void supportMouseAndTouchEvents() {
	// 	DOM.sinkEvents((com.google.gwt.user.client.Element) element.cast(),
	// 			Event.ONCLICK | Event.TOUCHEVENTS);
	// 	DOM.setEventListener((com.google.gwt.user.client.Element) element.cast(),
	// 			new EventListener() {
	// 				@Override
	// 				public void onBrowserEvent(Event event) {
	// 					switch (DOM.eventGetType(event)) {
	// 					case Event.ONCLICK:
	// 						onClick(event);
	// 						break;
	// 					case Event.ONTOUCHSTART:
	// 						touchStart(event);
	// 						break;
	// 					case Event.ONTOUCHCANCEL:
	// 						touchCancel(event);
	// 						break;
	// 					case Event.ONTOUCHEND:
	// 						touchEnd(event);
	// 						break;
	// 					case Event.ONTOUCHMOVE:
	// 						touchMove(event);
	// 						break;
	// 				}
	// 			}
	// 		});
  // }
	
	// private void onClick(Event event) {
	// 	event.stopPropagation();
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

	// 	event.preventDefault();
	// 	fireClick(event);

	// }
	
	// private void touchStart(Event event) {
	// 	touchHandled = false;
	// 	touchMoved = false;
	// 	startX = event.getClientX();
	// 	startY = event.getClientY();
	// }
	
	// private void touchCancel(Event event) {
		
	// }
	
// 	private void touchEnd(Event event) {
		
// 		if (!touchMoved) {
// //			event.stopPropagation();
// //			event.preventDefault();
// 			touchHandled = true;
// 			fireClick(event);
// 		}
// 	}

	// private void touchMove(Event event) {
	// 	if (event.getTouches().length() != 1) {
	// 		return;
	// 	}
		
	// 	Touch touch = event.getTouches().get(0);
	// 	int deltaX = Math.abs(startX - touch.getClientX());
	// 	int deltaY = Math.abs(startY - touch.getClientY());

	// 	if (deltaX > 5 || deltaY > 5) {
	// 		touchMoved = true;
	// 	}
	// }

}
