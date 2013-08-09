package net.sevenscales.editor.api.impl;

import java.util.Date;

import net.sevenscales.domain.utils.SLogger;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.HasTouchStartHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.RootPanel;

public class TouchDragAndDrop implements TouchStartHandler, TouchMoveHandler, TouchEndHandler {
	private static final SLogger logger = SLogger.createLogger(TouchDragAndDrop.class);
	private int startX;
	private int startY;
	private boolean touchMoved;
	private boolean touchHandled;
	private ITouchToMouseHandler touchHandler;
  private FocusPanel capturingWidget;
	private HandlerRegistration touchStartHandlerRegistration;
	private TouchContext context;
	
	public interface ITouchToMouseHandler {
		void onTouchToMouseMove(MouseMoveEvent event);
		void onTouchToMouseUp(MouseUpEvent e);
		void onTouchToMouseDown(MouseDownEvent e);
	}
	
	public TouchDragAndDrop(ITouchToMouseHandler touchHandler, HasTouchStartHandlers dragHandle) {
//		RootPanel.get().addDomHandler(this, TouchStartEvent.getType());
//		RootPanel.get().addDomHandler(this, TouchMoveEvent.getType());
//		RootPanel.get().addDomHandler(this, TouchEndEvent.getType());
		this.touchHandler = touchHandler;
		context = new TouchContext();
		
//		if (dragHandle instanceof HasTouchStartHandlers) {
//      touchStartHandlerRegistration = ((HasTouchStartHandlers) dragHandle).addTouchStartHandler(TouchManager.this);
//    }
		dragHandle.addTouchStartHandler(TouchDragAndDrop.this);
		initCapturingWidget();
	}
	
	private void initCapturingWidget() {
		logger.debug("initCapturingWidget...");
    capturingWidget = new FocusPanel();
//    capturingWidget.addMouseMoveHandler(this);
//    capturingWidget.addMouseUpHandler(this);
    capturingWidget.addTouchMoveHandler(this);
    capturingWidget.addTouchEndHandler(this);
//    capturingWidget.addTouchCancelHandler(this);
    Style style = capturingWidget.getElement().getStyle();
    // workaround for IE8 opacity http://code.google.com/p/google-web-toolkit/issues/detail?id=5538
    style.setProperty("filter", "alpha(opacity=0)");
    style.setOpacity(0);
//    style.setZIndex(10000);
    style.setMargin(0, Style.Unit.PX);
    style.setBorderStyle(BorderStyle.NONE);
    style.setBackgroundColor("blue");
		logger.debug("initCapturingWidget... done");
  }
	
	private void startCapturing() {
		if (capturingWidget.isAttached()) {
			logger.debug("startCapturing already started...");
			return;
		}
		
		logger.debug("startCapturing...");
    capturingWidget.setPixelSize(1, 1);
    RootPanel.get().add(capturingWidget, 0, 0);

    DOM.setCapture(capturingWidget.getElement());
    
    capturingWidget.setPixelSize(RootPanel.get().getOffsetWidth(),
        RootPanel.get().getOffsetHeight());
		logger.debug("startCapturing... done");
  }
	
	private void endCapturing() {
    DOM.releaseCapture(capturingWidget.getElement()); 
    capturingWidget.removeFromParent(); 
		logger.debug("endCapturing... done");
	}

	@Override
	public void onTouchStart(TouchStartEvent event) {
		logger.debug("onTouchStart...");
//		event.preventDefault();
		startCapturing();
		Touch touch = event.getTouches().get(0);
    startX = touch.getClientX();
    startY = touch.getClientY();               
    touchMoved = false;
    fireMouseDown(event);
	}

	@Override
	public void onTouchEnd(TouchEndEvent event) {
		logger.debug("onTouchEnd...");
		endCapturing();
		fireMouseUp(event);

		if (!touchMoved) {
      touchHandled = true;
      fireClick();
    }
	}

	@Override
	public void onTouchMove(TouchMoveEvent event) {
		logger.debug("onTouchEnd...");
//		if (!touchMoved) {
//        Touch touch = event.getTouches().get(0);
//        int deltaX = Math.abs(startX - touch.getClientX()); 
//        int deltaY = Math.abs(startY - touch.getClientY());
//
//        if (deltaX > 5 || deltaY > 5) {
//        	touchMoved = true;
//        }
        fireMouseMove(event);
//    }
	}
	
	private void fireMouseDown(TouchStartEvent event) {
		Touch touch = TouchHelpers.firstTouch(event);
		if (touch != null) {
			TouchHelpers.fillContext(touch, context);
			MouseDownEvent e = TouchHelpers.createMouseDownEvent(touch);
		  touchHandler.onTouchToMouseDown(e);
		}
	}
	
	private void fireMouseMove(TouchMoveEvent event) {
		Touch touch = TouchHelpers.firstTouch(event);
		if (touch != null) {
			TouchHelpers.fillContext(touch, context);
		  MouseMoveEvent e = TouchHelpers.createMouseMoveEvent(touch);
		  touchHandler.onTouchToMouseMove(e);
		}
	}
	
	private void fireMouseUp(TouchEndEvent event) {
		logger.debug("fireMouseUp...");
		if (event.getTouches().length() != 0) {
			// no multiple touches, end is always zero anyway
			return;
		}
		
		MouseUpEvent e = TouchHelpers.createMouseUpEvent(context);
	  touchHandler.onTouchToMouseUp(e);
	}
	
	private void fireClick() {
//    NativeEvent evt = Document.get().createClickEvent(1, 0, 0, 0, 0, false,
//                    false, false, false);
//    Element e;
//    e.dispatchEvent(evt);
//    getElement().dispatchEvent(evt);
  }
	
	private int getUnixTimeStamp() {
    Date date = new Date();
    int iTimeStamp = (int) (date.getTime() * .001);
    return iTimeStamp;
	}
	
}
