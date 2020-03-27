package net.sevenscales.editor.api.impl;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.RootPanel;

import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.ToolFrame;
import net.sevenscales.editor.api.event.pointer.PointerDownEvent;
import net.sevenscales.editor.api.event.pointer.PointerDownHandler;
import net.sevenscales.editor.api.event.pointer.PointerEventsSupport;
import net.sevenscales.editor.api.event.pointer.PointerMoveEvent;
import net.sevenscales.editor.api.event.pointer.PointerUpEvent;
import net.sevenscales.editor.api.event.pointer.PointerUpHandler;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.MouseDiagramHandler;
import net.sevenscales.editor.gfx.base.GraphicsEvent;
import net.sevenscales.editor.gfx.domain.IGraphics;
import net.sevenscales.editor.gfx.domain.MatrixPointJS;
import net.sevenscales.editor.gfx.domain.OrgEvent;

public class ModelingPanelEventHandler implements
	MouseDiagramHandler,
	MouseDownHandler,
	MouseUpHandler,
	PointerDownHandler,
	PointerUpHandler {
	private ISurfaceHandler surface;
	private ToolFrame toolFrame;
	private DragAndDropHandler dragAndDropHandler;
	private TouchDragAndDrop touchManager;

	public ModelingPanelEventHandler(ISurfaceHandler surface, ToolFrame toolFrame) {
		this.surface = surface;
		this.toolFrame = toolFrame;

		dragAndDropHandler = new DragAndDropHandler(surface, toolFrame.getToolbar());
		touchManager = new TouchDragAndDrop(
			dragAndDropHandler,
			toolFrame.getToolbar().getHasTouchStartHandlers()
		);

		if (PointerEventsSupport.isSupported()) {
			RootPanel.get().addDomHandler(dragAndDropHandler, PointerDownEvent.getType());
			RootPanel.get().addDomHandler(dragAndDropHandler, PointerUpEvent.getType());
			RootPanel.get().addDomHandler(dragAndDropHandler, PointerMoveEvent.getType());
	
			RootPanel.get().addDomHandler(this, PointerDownEvent.getType());
			RootPanel.get().addDomHandler(this, PointerUpEvent.getType());
		} else {
      new SurfaceEventWrapper(
        surface,
        dragAndDropHandler
      );

			// add dom handler on the root panel, then drag and drop works safely
			RootPanel.get().addDomHandler(dragAndDropHandler, MouseDownEvent.getType());
			RootPanel.get().addDomHandler(dragAndDropHandler, MouseUpEvent.getType());
			RootPanel.get().addDomHandler(dragAndDropHandler, MouseMoveEvent.getType());
	
			RootPanel.get().addDomHandler(this, MouseDownEvent.getType());
			RootPanel.get().addDomHandler(this, MouseUpEvent.getType());
		}
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
    mouseDown((event));
  }
  
  private void mouseDown(MouseEvent event) {
		if (!(event.getNativeEvent().getButton() == Event.BUTTON_LEFT) || !Element.is(event.getNativeEvent().getEventTarget())) {
			// handle only left button events
			return;
		}

		com.google.gwt.user.client.Event e = Event.as(event.getNativeEvent());
		int keys = e.getShiftKey() ? IGraphics.SHIFT : 0;
		keys |= e.getAltKey() ? IGraphics.ALT : 0;
		if (surface.getElement().isOrHasChild(Element.as(event.getNativeEvent().getEventTarget()))) {
      surface.onMouseDown((GraphicsEvent) e, keys);
      // Pointer down event is not fired
      // on Hammer2.ts if default is prevented.
			// event.getNativeEvent().preventDefault();
		} else if (toolFrame.getToolbar().getElement().isOrHasChild(Element.as(event.getNativeEvent().getEventTarget()))) {
			toolFrame.getToolbar().onMouseDown((GraphicsEvent) e, keys);
		}
  }

	@Override
	public void onMouseUp(MouseUpEvent event) {
    mouseUp(event);
  }
  
  private void mouseUp(MouseEvent event) {
		if (!(event.getNativeEvent().getButton() == Event.BUTTON_LEFT) || !Element.is(event.getNativeEvent().getEventTarget())) {
			// handle only left button events
			return;
		}

		com.google.gwt.user.client.Event e = Event.as(event.getNativeEvent());
		int keys = e.getShiftKey() ? IGraphics.SHIFT : 0;
		if (surface.getElement().isOrHasChild(Element.as(event.getNativeEvent().getEventTarget()))) {
			surface.onMouseUp((GraphicsEvent) e, keys);
		} else if (toolFrame.getToolbar().getElement().isOrHasChild(Element.as(event.getNativeEvent().getEventTarget()))) {
			toolFrame.getToolbar().onMouseUp((GraphicsEvent) e, keys);
		}
  }

  @Override
	public void onPointerUp(PointerUpEvent event) {
    // Debug.log("onPointerUp...");
    mouseUp(event);
	}

	@Override
	public void onPointerDown(PointerDownEvent event) {
    // Debug.log("onPointerDown...");
    
    mouseDown(event);
	}


	@Override
	public boolean onMouseDown(OrgEvent event, Diagram sender, MatrixPointJS point, int keys) {
		toolFrame.hideToolbar();
		return false;
	}

	@Override
	public void onMouseUp(Diagram sender, MatrixPointJS point, int keys) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseMove(OrgEvent event, Diagram sender, MatrixPointJS point) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseLeave(Diagram sender, MatrixPointJS point) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseEnter(OrgEvent event, Diagram sender, MatrixPointJS point) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTouchStart(OrgEvent event, Diagram sender, MatrixPointJS point) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTouchMove(OrgEvent event, Diagram sender, MatrixPointJS point) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTouchEnd(Diagram sender, MatrixPointJS point) {
		// TODO Auto-generated method stub
		
	}
}
