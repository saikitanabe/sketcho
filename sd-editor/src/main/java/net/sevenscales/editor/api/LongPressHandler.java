package net.sevenscales.editor.api;

import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;

import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.event.pointer.PointerDownEvent;
import net.sevenscales.editor.api.event.pointer.PointerDownHandler;
import net.sevenscales.editor.api.event.pointer.PointerEventsSupport;
import net.sevenscales.editor.api.event.pointer.PointerMoveEvent;
import net.sevenscales.editor.api.event.pointer.PointerMoveHandler;
import net.sevenscales.editor.api.event.pointer.PointerUpEvent;
import net.sevenscales.editor.api.event.pointer.PointerUpHandler;
import net.sevenscales.editor.api.impl.TouchHelpers;

public class LongPressHandler implements MouseDownHandler, 
																				 MouseMoveHandler, 
                                         MouseUpHandler,
                                         PointerDownHandler, 
																				 PointerMoveHandler, 
																				 PointerUpHandler,
																				 TouchStartHandler,
																				 TouchMoveHandler,
																				 TouchEndHandler {
	private static final SLogger logger = SLogger.createLogger(LongPressHandler.class);
	private static final int THRESHOLD;
	private static final int TIME_OUT = 700;
	private final LongPressTimer timer = new LongPressTimer();
	
	static {
		if (TouchHelpers.isSupportsTouch()) {
			THRESHOLD = 7;
		} else {
			// mouse pointer needs smaller threshold
			THRESHOLD = 5;
		}
	}
			
	private class LongPressTimer extends Timer {
    private long startTime = System.currentTimeMillis();
		private boolean isScheduled;

    @Override
    public void cancel() {
    	isScheduled = false;
      super.cancel();
    }

    public void scheduleRepeating(int periodMillis) {
    	cancel();
    	super.scheduleRepeating(periodMillis);
    	isScheduled = true;
    }
    
    @Override
    public void run() {
    	if (isScheduled && System.currentTimeMillis() >= (startTime + TIME_OUT)) {
    		fireLongPress();
    		cancel();
    	}
    }
  };
  
	private ISurfaceHandler surface;
	private int startX;
	private int startY;
	
	public LongPressHandler(ISurfaceHandler surface) {
    this.surface = surface;
    
    if (PointerEventsSupport.isSupported()) {
      surface.addDomHandler(this, PointerDownEvent.getType());
      surface.addDomHandler(this, PointerUpEvent.getType());
      surface.addDomHandler(this, PointerMoveEvent.getType());
    } else {
      surface.addDomHandler(this, MouseDownEvent.getType());
      surface.addDomHandler(this, MouseUpEvent.getType());
      surface.addDomHandler(this, MouseMoveEvent.getType());
  
      surface.addDomHandler(this, TouchStartEvent.getType());
      surface.addDomHandler(this, TouchMoveEvent.getType());
      surface.addDomHandler(this, TouchEndEvent.getType());
    }
	}
	
	@Override
	public void onMouseDown(MouseDownEvent event) {
    mouseDown(event);
  }
  
  private void mouseDown(MouseEvent event) {
		if (event.getNativeEvent().getButton() == Event.BUTTON_LEFT && !surface.getEditorContext().isTrue(EditorProperty.START_SELECTION_TOOL)) {
			startX = event.getClientX();
			startY = event.getClientY();
			starScheduler();
		}
  }
	
	private void starScheduler() {
		if (!timer.isScheduled) {
			timer.scheduleRepeating(TIME_OUT + 5);
		} else {
			timer.cancel();
		}
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		mouseMove(event);
  }
  
  private void mouseMove(MouseEvent event) {
    cancelScheduler(event.getClientX(), event.getClientY());
  }
	
	private void cancelScheduler(int x, int y) {
		int deltaX = Math.abs(startX - x);
		int deltaY = Math.abs(startY - y);

		if (deltaX > THRESHOLD || deltaY > THRESHOLD) {
			cancel();
		}
	}
		
	public void cancel() {
		if (timer.isScheduled) {
			timer.cancel();
		}
		timer.isScheduled = false;
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
    mouseUp();
  }
  
  private void mouseUp() {
    timer.cancel();
  }

	private void fireLongPress() {
		logger.info("fireLongPress xy({}, {})...", startX, startY);
		surface.fireLongPress(startX, startY);
		cancel();
	}

	@Override
	public void onTouchStart(TouchStartEvent event) {
		event.preventDefault();
		if (event.getTouches().length() != 1) {
			cancel();
			return;
		}
		Touch touch = event.getTouches().get(0);
		startX = touch.getClientX();
		startY = touch.getClientY();
		starScheduler();
	}

	@Override
	public void onTouchMove(TouchMoveEvent event) {
		Touch touch = TouchHelpers.firstTouch(event);
		if (touch != null) {
			cancelScheduler(touch.getClientX(), touch.getClientY());
		} else {
			cancel();
		}
	}

	@Override
	public void onTouchEnd(TouchEndEvent event) {
		cancel();
	}

	@Override
	public void onPointerUp(PointerUpEvent event) {
		mouseUp();
	}

	@Override
	public void onPointerMove(PointerMoveEvent event) {
		mouseMove(event);
	}

	@Override
	public void onPointerDown(PointerDownEvent event) {
		mouseDown(event);
	}

}
