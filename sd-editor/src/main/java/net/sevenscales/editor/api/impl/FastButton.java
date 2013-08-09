package net.sevenscales.editor.api.impl;

import java.util.Date;

import net.sevenscales.domain.utils.SLogger;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiChild;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * Implementation of Google FastButton {@link http
 * ://code.google.com/mobile/articles/fast_buttons.html}
 * 
 * 
 * 
 */
public class FastButton extends Composite implements HasClickHandlers {
	private static final SLogger logger = SLogger.createLogger(FastButton.class);
	
	private boolean touchHandled = false;
	private boolean clickHandled = false;
	private boolean touchMoved = false;
	private int startY;
	private int startX;
	private int timeStart;

	public FastButton() {
	}

	public FastButton(Widget child) {
		init(child);
	}

	private void init(Widget child) {
		// TODO - messages
//		assert (child instanceof HasAllTouchHandlers) : "";
//		assert (child instanceof HasClickHandlers) : "";
		initWidget(child);
		sinkEvents(Event.TOUCHEVENTS | Event.ONCLICK);
	}

	@Override
	public Widget getWidget() {
		return super.getWidget();
	}
	
	@UiChild
	public void addChild(Widget child) {
		init(child);
	}

	@Override
	public void onBrowserEvent(Event event) {
		timeStart = getUnixTimeStamp();
		switch (DOM.eventGetType(event)) {
		case Event.ONTOUCHSTART: {
			onTouchStart(event);
			break;
		}
		case Event.ONTOUCHEND: {
			onTouchEnd(event);
			break;
		}
		case Event.ONTOUCHMOVE: {
			onTouchMove(event);
			break;
		}
		case Event.ONCLICK: {
			onClick(event);
			return;
		}
		}

		super.onBrowserEvent(event);
	}

	/**
	 * 
	 * @param event
	 */
	private void onClick(Event event) {

//		int timeEnd = getUnixTimeStamp();
		if (touchHandled) {
			event.stopPropagation();
			event.preventDefault();
			// Window.alert("click via touch: "+ this.toString() + "..."
			// +timeStart+"---"+timeEnd);
			touchHandled = false;
			clickHandled = true;
			super.onBrowserEvent(event);
		} else {
			if (clickHandled) {
				event.preventDefault();
				event.stopPropagation();
			} else {
//				clickHandled = true;
				// Window.alert("click nativo: "+ this.toString()+ "..."
				// +(timeStart-timeEnd)+"==="+timeStart+"---"+timeEnd);
				logger.debug2("onClick touchHandled={}...", touchHandled);
				super.onBrowserEvent(event);
			}
		}
	}

	/**
	 * 
	 * @param event
	 */
	private void onTouchEnd(Event event) {
		if (!touchMoved) {
			event.stopPropagation();
			event.preventDefault();
			touchHandled = true;
			fireClick(event);
		}
	}

	/**
	 * 
	 * @param event
	 */
	private void onTouchMove(Event event) {
		if (!touchMoved) {
			Touch touch = event.getTouches().get(0);
			int deltaX = Math.abs(startX - touch.getClientX());
			int deltaY = Math.abs(startY - touch.getClientY());

			if (deltaX > 5 || deltaY > 5) {
				touchMoved = true;
			}
		}
	}

	/**
	 * 
	 * @param event
	 */
	private void onTouchStart(Event event) {
		Touch touch = event.getTouches().get(0);
		this.startX = touch.getClientX();
		this.startY = touch.getClientY();
		touchMoved = false;
	}

	/**
	 * @param executor
	 * @return
	 */
	private void fireClick(Event event) {
		logger.debug2("fireClick...");
		
		event.stopPropagation();
		event.preventDefault();
		
		NativeEvent evt = Document.get().createClickEvent(1, startX, startY, 
				startX, startY, false,
				false, false, false);
		getElement().dispatchEvent(evt);
		
//		ClickEvent ce = new ClickEvent() {};
//		ce.setNativeEvent(event);
//		getWidget().fireEvent(ce);
	}

	private int getUnixTimeStamp() {
		Date date = new Date();
		int iTimeStamp = (int) (date.getTime() * .001);
		return iTimeStamp;
	}
	
	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return getWidget().addDomHandler(handler, ClickEvent.getType());
	}
}