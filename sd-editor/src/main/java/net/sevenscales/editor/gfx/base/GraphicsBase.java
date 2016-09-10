package net.sevenscales.editor.gfx.base;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;

import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.gfx.base.GraphicsEventBase.Type;
import net.sevenscales.editor.gfx.domain.IGraphics;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Event;

public abstract class GraphicsBase implements IGraphics {
	private static final SLogger logger = SLogger.createLogger(GraphicsBase.class); 

	protected EventHandlerCollection eventHandlerCollection = new EventHandlerCollection();
	protected JavaScriptObject rawNode;
	/** 
	 * NOTE: cannot change ArrayList to HashSet for now, since order matters! Now e.g.
	 * EllipseElement would be before ConnectionHelpers and then touch start events with 
	 * quick relationships will not work. Ellipse element will fire event first... Should
	 * keep the order or make implementation without order dependency. 
	 * 
	 * CURRENT implementation is FRAGILE for changes!
	 */
	protected Map<Type<?>, ArrayList<? extends GraphicsEventHandler>> map = new HashMap<Type<?>, ArrayList<? extends GraphicsEventHandler>>();

	public GraphicsBase() {
		super();
	}

	public <H extends GraphicsEventHandler> void addGraphicsHandler(H handler,
			GraphicsEventBase.Type<H> type) {
		ArrayList<H> handlers = (ArrayList<H>) map.get(type);
		if (handlers == null) {
			handlers = new ArrayList<H>();
			map.put(type, handlers);
		}
		handlers.add(handler);
	}

	protected void onMouseLeave(GraphicsEvent event) {
		eventHandlerCollection.fireOnMouseLeave(this, event);
		ArrayList<GraphicsMouseLeaveHandler> handlers = (ArrayList<GraphicsMouseLeaveHandler>) map
				.get(GraphicsMouseLeaveEvent.getType());
		if (handlers != null) {
			for (GraphicsMouseLeaveHandler h : handlers) {
				h.onMouseLeave(event);
			}
		}
	}

	protected void onMouseEnter(GraphicsEvent event) {
		eventHandlerCollection.fireOnMouseEnter(this, event);
		ArrayList<GraphicsMouseEnterHandler> handlers = (ArrayList<GraphicsMouseEnterHandler>) map
				.get(GraphicsMouseOverEvent.getType());
		if (handlers != null) {
			for (GraphicsMouseEnterHandler h : handlers) {
				h.onMouseEnter(event);
			}
		}
	}

	protected void onMouseMove(GraphicsEvent event) {
		eventHandlerCollection.fireOnMouseMove(this, event);
		ArrayList<GraphicsMouseMoveHandler> handlers = (ArrayList<GraphicsMouseMoveHandler>) map
				.get(GraphicsMouseMoveEvent.getType());
		if (handlers != null) {
			for (GraphicsMouseMoveHandler h : handlers) {
				h.onMouseMove(event);
			}
		}
	}

	protected void onMouseDown(GraphicsEvent event, int keys) {
		if (event.getButton() != Event.BUTTON_LEFT) {
			// handle only left button events
			return;
		}

		eventHandlerCollection.fireOnMouseDown(this, event, keys);
		ArrayList<GraphicsMouseDownHandler> handlers = (ArrayList<GraphicsMouseDownHandler>) map
				.get(GraphicsMouseDownEvent.getType());
		if (handlers != null) {
			for (GraphicsMouseDownHandler h : handlers) {
				h.onMouseDown(event, keys);
			}
		}
	}

	protected void onMouseUp(GraphicsEvent event, int keys) {
		eventHandlerCollection.fireOnMouseUp(this, event, keys);
		ArrayList<GraphicsMouseUpHandler> handlers = (ArrayList<GraphicsMouseUpHandler>) map
				.get(GraphicsMouseUpEvent.getType());
		if (handlers != null) {
			for (GraphicsMouseUpHandler h : handlers) {
				h.onMouseUp(event, keys);
			}
		}
	}

	protected void onTouchMove(GraphicsEvent event) {
		eventHandlerCollection.fireOnTouchMove(this, event);
		ArrayList<GraphicsTouchMoveHandler> handlers = (ArrayList<GraphicsTouchMoveHandler>) map
				.get(GraphicsTouchMoveEvent.getType());
		if (handlers != null) {
			for (GraphicsTouchMoveHandler h : handlers) {
				h.onTouchMove(event);
			}
		}
	}

	protected void onTouchStart(GraphicsEvent event) {
		eventHandlerCollection.fireOnTouchStart(this, event);
		ArrayList<GraphicsTouchStartHandler> handlers = (ArrayList<GraphicsTouchStartHandler>) map
				.get(GraphicsTouchStartEvent.getType());
		if (handlers != null) {
			for (GraphicsTouchStartHandler h : handlers) {
				h.onTouchStart(event);
			}
		}
	}

	protected void onTouchEnd(GraphicsEvent event) {
		eventHandlerCollection.fireOnTouchEnd(this, event);
		ArrayList<GraphicsTouchEndHandler> handlers = (ArrayList<GraphicsTouchEndHandler>) map
				.get(GraphicsTouchEndEvent.getType());
		if (handlers != null) {
			for (GraphicsTouchEndHandler h : handlers) {
				h.onTouchEnd(event);
			}
		}
	}

	protected void onDoubleClick(GraphicsEvent event) {
		eventHandlerCollection.fireOnDoubleClick(this, event);
		ArrayList<GraphicsDoubleClickHandler> handlers = (ArrayList<GraphicsDoubleClickHandler>) map
				.get(GraphicsDoubleClickEvent.getType());
		if (handlers != null) {
			for (GraphicsDoubleClickHandler h : handlers) {
				h.onDoubleClick(event, 0);
			}
		}
	}

	public void addGraphicsDoubleClickHandler(GraphicsDoubleClickHandler handler) {
		if (map.get(GraphicsDoubleClickEvent.getType()) == null) {
			connectMouse(GraphicsDoubleClickEvent.getType().getEventName());
		}
		addGraphicsHandler(handler, GraphicsDoubleClickEvent.getType());
	}

	public void addGraphicsMouseDownHandler(GraphicsMouseDownHandler handler) {
		if (map.get(GraphicsMouseDownEvent.getType()) == null) {
			connectMouse(GraphicsMouseDownEvent.getType().getEventName());
		}
		addGraphicsHandler(handler, GraphicsMouseDownEvent.getType());
	}

	public void addGraphicsMouseUpHandler(GraphicsMouseUpHandler handler) {
		if (map.get(GraphicsMouseUpEvent.getType()) == null) {
			connectMouse(GraphicsMouseUpEvent.getType().getEventName());
		}
		addGraphicsHandler(handler, GraphicsMouseUpEvent.getType());
	}

	public void addGraphicsMouseEnterHandler(GraphicsMouseEnterHandler handler) {
		if (map.get(GraphicsMouseOverEvent.getType()) == null) {
			connectMouse(GraphicsMouseOverEvent.getType().getEventName());
		}
		addGraphicsHandler(handler, GraphicsMouseOverEvent.getType());
	}

	public void addGraphicsMouseLeaveHandler(GraphicsMouseLeaveHandler handler) {
		if (map.get(GraphicsMouseLeaveEvent.getType()) == null) {
			connectMouse(GraphicsMouseLeaveEvent.getType().getEventName());
		}
		addGraphicsHandler(handler, GraphicsMouseLeaveEvent.getType());
	}

	public void addGraphicsMouseMoveHandler(GraphicsMouseMoveHandler handler) {
		if (map.get(GraphicsMouseMoveEvent.getType()) == null) {
			connectMouse(GraphicsMouseMoveEvent.getType().getEventName());
		}
		addGraphicsHandler(handler, GraphicsMouseMoveEvent.getType());
	};

	public void addGraphicsTouchMoveHandler(GraphicsTouchMoveHandler handler) {
		if (map.get(GraphicsTouchMoveEvent.getType()) == null) {
			connectMouse(GraphicsTouchMoveEvent.getType().getEventName());
		}
		addGraphicsHandler(handler, GraphicsTouchMoveEvent.getType());
	};

	public void addGraphicsTouchStartHandler(GraphicsTouchStartHandler handler) {
		if (map.get(GraphicsTouchStartEvent.getType()) == null) {
			connectMouse(GraphicsTouchStartEvent.getType().getEventName());
		}
		addGraphicsHandler(handler, GraphicsTouchStartEvent.getType());
	};

	public void addGraphicsTouchEndHandler(GraphicsTouchEndHandler handler) {
		if (map.get(GraphicsTouchEndEvent.getType()) == null) {
			connectMouse(GraphicsTouchEndEvent.getType().getEventName());
		}
		addGraphicsHandler(handler, GraphicsTouchEndEvent.getType());
	}

	// public void addGraphicsKeyDownhandler(GraphicsKeyDownHandler handler) {
	// if (map.get(GraphicsMouseMoveEvent.getType()) == null) {
	// connectKey(GraphicsMouseMoveEvent.getType().getEventName());
	// }
	// addGraphicsHandler(handler, GraphicsMouseMoveEvent.getType());
	// }

	protected abstract void connectMouse(String eventType);

}