package net.sevenscales.editor.api.impl;

import java.util.Date;

import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.ISurfaceHandler;

import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.TouchCancelEvent;
import com.google.gwt.event.dom.client.TouchCancelHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;

public class SurfaceEventWrapper implements TouchStartHandler, TouchMoveHandler, TouchEndHandler, TouchCancelHandler {
	private static final SLogger logger = SLogger.createLogger(SurfaceEventWrapper.class);
	private ISurfaceHandler surface;
	private DragAndDropHandler dragAndDropHandler;
	private TouchContext context;
	// >>>>>>>>>>> SOLU 14.11.2014
	// private boolean mouseDown;
	// <<<<<<<<<<< SOLU 14.11.2014	

	public SurfaceEventWrapper(ISurfaceHandler surface, DragAndDropHandler dragAndDropHandler) {
		this.surface = surface;
		this.dragAndDropHandler = dragAndDropHandler;
		context = new TouchContext();

		surface.addDomHandler(this, TouchStartEvent.getType());
		surface.addDomHandler(this, TouchMoveEvent.getType());
		surface.addDomHandler(this, TouchEndEvent.getType());
		surface.addDomHandler(this, TouchCancelEvent.getType());
	}
	
	@Override
	public void onTouchStart(TouchStartEvent event) {
		// >>>>>>>>>>> SOLU
		// mouseDown = event.getTouches().length() > 1;
		// if (event.getTouches().length() == 2) {
		// 	surface.getSelectionHandler().unselectAll();	
		// }

		// if (mouseDown) {
	 //    fireMouseDown(event);
		// }
		// >>>>>>>>>>> SOLU		
    fireMouseDown(event);
	}

	private void fireMouseDown(TouchStartEvent event) {
		Touch touch = TouchHelpers.firstTouch(event);
		if (touch != null && !dragAndDropHandler.isLibraryHandling()) {
			TouchHelpers.fillContext(touch, context);
			MouseDownEvent e = TouchHelpers.createMouseDownEvent(touch);
			surface.fireMouseDown(e);
		}
	}

	@Override
	public void onTouchEnd(TouchEndEvent event) {
		// >>>>>>>>>>> SOLU
		// if (mouseDown) {
		// 	// if (event.getTouches().length() != 0) {
		// 	// 	// no multiple touches, end is always zero anyway
		// 	// 	return;
		// 	// }
			
		// 	MouseUpEvent e = TouchHelpers.createMouseUpEvent(context);
		// 	surface.fireMouseUp(e);
		// }
		// >>>>>>>>>>> SOLU

		if (event.getTouches().length() != 0) {
	    // no multiple touches, end is always zero anyway
	    return;
	  }

		MouseUpEvent e = TouchHelpers.createMouseUpEvent(context);
		surface.fireMouseUp(e);
	}

	@Override
	public void onTouchMove(TouchMoveEvent event) {
		// >>>>>>>>>>> SOLU
		// if (mouseDown) {
	 //    fireMouseMove(event);
		// }
		// Touch touch = TouchHelpers.firstTouch(event);
		// if (touch != null) {
		// 	showMyMouse(touch.getClientX(), touch.getClientY());
		// 	externalTouchPadMove(touch.getClientX(), touch.getClientY());
		// }
		// <<<<<<<<<<< SOLU		
    fireMouseMove(event);

    // could handle touch pad move events, but should send events
    // only after a 100 milli seconds
		// Touch touch = TouchHelpers.firstTouch(event);
		// if (touch != null) {
		// 	externalTouchPadMove(touch.getClientX(), touch.getClientY());
		// }
	}

	// >>>>>>>>>>>> SOLU
	// private native void externalTouchPadMove(double x, double y)/*-{
	// 	$wnd.globalStreams.touchPadMoveStream.push(x, y)
	// }-*/;
	// <<<<<<<<<<<< SOLU

	private void fireMouseMove(TouchMoveEvent event) {
		Touch touch = TouchHelpers.firstTouch(event);
		if (touch != null) {
			TouchHelpers.fillContext(touch, context);
			MouseMoveEvent e = TouchHelpers.createMouseMoveEvent(touch);
			surface.fireMouseMove(e, false);
		}
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

	@Override
	public void onTouchCancel(TouchCancelEvent event) {
		logger.debug("onTouchCancel");
	}
	
}
