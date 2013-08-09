package net.sevenscales.editor.api.impl;

import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.SurfaceHandler;
import net.sevenscales.editor.api.impl.TouchDragAndDrop.ITouchToMouseHandler;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;

public class DragAndDropHandler implements MouseMoveHandler, ITouchToMouseHandler {
	private static final SLogger logger = SLogger.createLogger(DragAndDropHandler.class);
	
	private SurfaceHandler surface;
	private SurfaceHandler toolFrame;
	
	private Boolean onentersurface = false;

	private boolean libraryHandling;

	public DragAndDropHandler(SurfaceHandler surface, SurfaceHandler toolFrame) {
		this.surface = surface;
		this.toolFrame = toolFrame;
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		handleOnMouseMove(event);
	}

	private void handleOnMouseMove(MouseMoveEvent event) {
		handleToolbarMouseMove(event, toolFrame);
		handleSurfaceMouseMove(event, surface);
	}

	private void handleSurfaceMouseMove(MouseMoveEvent event, SurfaceHandler sh) {
		boolean onsurface = !isOverToolbar(event);
		
		if (!onentersurface && onsurface) {
			onentersurface = true;
			sh.fireMouseOnEnter(event);
		}
		
		if (onsurface) {
			boolean toolbar = false;
			sh.fireMouseMove(event, toolbar);
		} else {
			onentersurface = false;
			sh.fireMouseOnLeave(event);
		}
	}

	private void handleToolbarMouseMove(MouseMoveEvent event, SurfaceHandler sh) {
		if (isOverToolbar(event)) {
			boolean toolbar = true;
			sh.fireMouseMove(event, toolbar);
		}
	}
	
	private boolean isOverToolbar(MouseEvent event) {
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
