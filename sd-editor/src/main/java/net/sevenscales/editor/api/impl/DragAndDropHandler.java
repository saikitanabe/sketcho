package net.sevenscales.editor.api.impl;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;

import net.sevenscales.domain.utils.Debug;
import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.event.pointer.PointerDownEvent;
import net.sevenscales.editor.api.event.pointer.PointerDownHandler;
import net.sevenscales.editor.api.event.pointer.PointerMoveEvent;
import net.sevenscales.editor.api.event.pointer.PointerMoveHandler;
import net.sevenscales.editor.api.event.pointer.PointerUpEvent;
import net.sevenscales.editor.api.event.pointer.PointerUpHandler;

public class DragAndDropHandler implements 
  MouseDownHandler,
  MouseUpHandler,
  MouseMoveHandler,
  PointerDownHandler,
  PointerUpHandler,
  PointerMoveHandler,
  ITouchToMouseHandler {
	private static final SLogger logger = SLogger.createLogger(DragAndDropHandler.class);
	
	private ISurfaceHandler surface;
	private ISurfaceHandler toolFrame;
	
	private Boolean onentersurface = false;

	private boolean libraryHandling;
	private boolean overToolBar;

	public DragAndDropHandler(ISurfaceHandler surface, ISurfaceHandler toolFrame) {
		this.surface = surface;
		this.toolFrame = toolFrame;
	}

	private void proxyCreatedShape() {
		overToolBar = false;
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		overToolBar = _isOverToolbar(event);
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		overToolBar = false;
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		handleOnMouseMove(event);
  }
  
	@Override
	public void onPointerMove(PointerMoveEvent event) {
    Debug.log(("pointermove root"));
    handleOnMouseMove(TouchHelpers.createMouseMoveEvent(event));
	}

	@Override
	public void onPointerUp(PointerUpEvent event) {
    overToolBar = false;
	}

	@Override
	public void onPointerDown(PointerDownEvent event) {
    overToolBar = _isOverToolbar(event);
	}

	private void handleOnMouseMove(MouseMoveEvent event) {
		handleToolbarMouseMove(event, toolFrame);
		handleSurfaceMouseMove(event, surface);
	}

	private void handleSurfaceMouseMove(MouseMoveEvent event, ISurfaceHandler sh) {
		boolean onsurface = !isOverToolbar(event);
		
		if (!onentersurface && onsurface) {
			onentersurface = true;
			sh.fireMouseOnEnter(event);
		}
		
		if (onsurface) {
      Debug.Profile p1 = Debug.startProfile("pointermove 1");
			boolean toolbar = false;
      sh.fireMouseMove(event, toolbar);
      p1.stopAndLog();
		} else {
			onentersurface = false;
			sh.fireMouseOnLeave(event);
		}
	}

	private void handleToolbarMouseMove(MouseMoveEvent event, ISurfaceHandler sh) {
		if (isOverToolbar(event)) {
			boolean toolbar = true;
			sh.fireMouseMove(event, toolbar);
		}
	}

	private boolean isOverToolbar(MouseEvent event) {
		if (overToolBar) {
			overToolBar = _isOverToolbar(event);
		}
		return overToolBar;
	}
	
	private boolean _isOverToolbar(MouseEvent event) {
		Element over = elementFromPoint(event.getClientX(), event.getClientY());
		if (over != null && toolFrame.getElement().isOrHasChild(over)) {
			return true;
		}
		
		return false;
	}

	private native Element elementFromPoint(int x, int y)/*-{
		return $doc.elementFromPoint(x, y);
	}-*/;

	@Override
	public void onTouchToMouseMove(MouseMoveEvent event) {
		handleOnMouseMove(event);
	}
	
	@Override
	public void onTouchToMouseUp(MouseUpEvent event) {
		libraryHandling = false;
		if (isOverToolbar(event)) {
			toolFrame.fireMouseUp(event);
		} else {
			surface.fireMouseUp(event);
		}
	}
	
	@Override
	public void onTouchToMouseDown(MouseDownEvent event) {
		overToolBar = _isOverToolbar(event);

		if (isOverToolbar(event)) {
			onentersurface = false;
			libraryHandling = true;
			toolFrame.fireMouseDown(event);
		} else {
//			surface.fireMouseDown(event);
		}
	}
	
	public boolean isLibraryHandling() {
		return libraryHandling;
	}

}
