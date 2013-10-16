package net.sevenscales.editor.gfx.base;

import java.util.HashSet;
import java.util.Set;

import net.sevenscales.editor.gfx.domain.IEventHandler;
import net.sevenscales.editor.gfx.domain.IGraphics;

import com.google.gwt.user.client.Event;

public class EventHandlerCollection {
	private Set<Handler> eventHandlers = new HashSet<Handler>();
	
	class Handler {
		public IEventHandler eventHandler;
		public String eventType;
	}

	public void add(IEventHandler eventHandler, String eventType) {
		Handler h = new Handler();
		h.eventHandler = eventHandler;
		h.eventType = eventType;
		eventHandlers.add(h);
	}

	public void fireOnMouseLeave(IGraphics shape, Event event) {
		for (Handler h : eventHandlers) {
			if (IGraphics.ON_MOUSE_LEAVE.equals(h.eventType))
				h.eventHandler.onMouseLeave(shape, event);
		}
	}

	public void fireOnMouseEnter(IGraphics shape, Event event) {
		for (Handler h : eventHandlers) {
			if (IGraphics.ON_MOUSE_ENTER.equals(h.eventType))
				h.eventHandler.onMouseEnter(shape, event);
		}
	}
	
	public void fireOnMouseMove(IGraphics shape, Event event) {
		for (Handler h : eventHandlers) {
			if (IGraphics.ON_MOUSE_MOVE.equals(h.eventType))
				h.eventHandler.onMouseMove(shape, event);
		}
	}

	public void fireOnDoubleClick(IGraphics shape, Event event) {
		for (Handler h : eventHandlers) {
			if (IGraphics.ON_DOUBLE_CLICK.equals(h.eventType))
				h.eventHandler.onDoubleClick(shape, event);
		}
	}

	public void fireOnMouseDown(IGraphics shape, Event event, int keys) {
		for (Handler h : eventHandlers) {
			if (IGraphics.ON_MOUSE_DOWN.equals(h.eventType))
				h.eventHandler.onMouseDown(shape, event, keys);
		}
	}
	
	public void fireOnMouseUp(IGraphics shape, Event event) {
		for (Handler h : eventHandlers) {
			if (IGraphics.ON_MOUSE_UP.equals(h.eventType))
				h.eventHandler.onMouseUp(shape, event);
		}
	}

	public void fireOnTouchMove(GraphicsBase shape, GraphicsEvent event) {
		for (Handler h : eventHandlers) {
			if (IGraphics.ON_TOUCH_MOVE.equals(h.eventType))
				h.eventHandler.onTouchMove(shape, event);
		}
	}

	public void fireOnTouchStart(GraphicsBase shape, GraphicsEvent event) {
		for (Handler h : eventHandlers) {
			if (IGraphics.ON_TOUCH_START.equals(h.eventType))
				h.eventHandler.onTouchStart(shape, event);
		}
	}

	public void fireOnTouchEnd(GraphicsBase shape, GraphicsEvent event) {
		for (Handler h : eventHandlers) {
			if (IGraphics.ON_TOUCH_END.equals(h.eventType))
				h.eventHandler.onTouchEnd(shape, event);
		}
	}


}
