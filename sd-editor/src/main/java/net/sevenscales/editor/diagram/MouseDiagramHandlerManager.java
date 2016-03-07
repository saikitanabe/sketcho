package net.sevenscales.editor.diagram;


import java.util.List;
import java.util.Set;

import com.google.gwt.dom.client.Element;

import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.SketchDiagramAreaHandler;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.event.BoardEmptyAreaClickedEvent;
import net.sevenscales.editor.api.event.ShowDiagramPropertyTextEditorEvent;
import net.sevenscales.editor.api.event.FreehandModeChangedEvent;
import net.sevenscales.editor.diagram.drag.MouseDiagramDragHandler;
import net.sevenscales.editor.diagram.utils.UiUtils;
import net.sevenscales.editor.content.ui.IModeManager;
import net.sevenscales.editor.gfx.domain.MatrixPointJS;
import net.sevenscales.editor.api.IBirdsEyeView;
import net.sevenscales.editor.api.Tools;


public class MouseDiagramHandlerManager implements MouseDiagramHandler, ClickDiagramHandler, MouseState {
	private static final SLogger logger = SLogger.createLogger(MouseDiagramHandlerManager.class);

	static {
		SLogger.addFilter(MouseDiagramHandlerManager.class);
	}

	private MouseDiagramDragHandler dragHandler;
//	private MouseDiagramBendHandler bendHandler;
	private SelectionHandler selectionHandler;
	private MouseDiagramResizeHandler resizeHandler;
  private boolean editable;
  private BackgroundMoveHandler backgroundMoveHandler;
  private MouseDiagramHandlerCollection handlers = new MouseDiagramHandlerCollection();
  private ProxyDragHandler proxyDragHandler;
	private SketchDiagramAreaHandler sketchDiagramAreaHandler;
//private Diagram mouseDownSender;
//private boolean mouseDown;
  private IModeManager modeManager;
	private LassoSelectionHandler lassoSelectionHandler;
	private FreehandDrawerHandler freehandDrawHandler;
	private SurfaceClickHandler surfaceClickHandler;
	private QuickConnectionHandler quickConnectionHandler;
	private ISurfaceHandler surface;
	// current handler gets events after mouse down if registered
	private MouseDiagramHandler currentMouseHandler;
	private IBirdsEyeView birdsEyeView;
//	private MouseDiagramHandler connectionModeMouseHandler;

	public MouseDiagramHandlerManager(ISurfaceHandler surface, List<Diagram> diagrams, boolean editable, 
	    IModeManager modeManager, IBirdsEyeView birdsEyeView) {
		this.surface = surface;
	  this.editable = editable;
	  this.modeManager = modeManager;
	  this.birdsEyeView = birdsEyeView;

//    bendHandler = new MouseDiagramBendHandler(this);
    dragHandler = new MouseDiagramDragHandler(surface, this, new ISelectionHandler() {
      public void moveSelected(int x, int y) {
        selectionHandler.moveSelected(x, y);
      }
      public Set<Diagram> getSelectedItems() {
        return selectionHandler.getSelectedItems();
      }
      @Override
      public void unselectAll() {
      	selectionHandler.unselectAll();
      }
      @Override
      public void movedMaybeGroup(int dx, int dy) {
				selectionHandler.movedMaybeGroup(dx, dy);
      }
    });
		selectionHandler = new SelectionHandler(surface, diagrams, dragHandler.getDragHandlers(), this);
		resizeHandler = new MouseDiagramResizeHandler(this, surface, modeManager);
		backgroundMoveHandler = new BackgroundMoveHandler(diagrams, surface);
		lassoSelectionHandler = new LassoSelectionHandler(surface, this);
		if (ISurfaceHandler.DRAWING_AREA.equals(surface.getName())) {
			// free hand drawing is not possible on library area
			freehandDrawHandler = new FreehandDrawerHandler(surface);
		}
		surfaceClickHandler = new SurfaceClickHandler(surface);
		sketchDiagramAreaHandler = new SketchDiagramAreaHandler(surface, modeManager);
		new LinkHandler(surface);
		quickConnectionHandler = new QuickConnectionHandler(surface);
		
		if (!UiUtils.isIE()) {
			handleDoubleTap(surface.getElement(), this);
		} else {
			// IE looses all mouse events if Hammer is used for double click
			// so handling through jquery on, this solution will not work on
			// Microsoft Surface
			handleMouseDoubleClick(surface.getElement(), this);
		}

		if (!surface.isLibrary()) {
			handleOnline(this);
		}
		// addMouseDiagramHandler(sketchDiagramAreaHandler);
	}

	private native void handleOnline(MouseDiagramHandlerManager me)/*-{
		if (typeof $wnd.globalStreams !== 'undefined') {
			$wnd.globalStreams.webStorageSupportedStream.onValue(function(value) {
				me.@net.sevenscales.editor.diagram.MouseDiagramHandlerManager::editable(Z)(value);
			})

			$wnd.globalStreams.onlineStream.onValue(function(value) {
				if (!value && !$wnd.webStorage.browserSupportsLocalStorage) {
					me.@net.sevenscales.editor.diagram.MouseDiagramHandlerManager::editable(Z)(false);
				} else if (value) {
					me.@net.sevenscales.editor.diagram.MouseDiagramHandlerManager::editable(Z)(true);
				}
			})
		}

		$wnd.globalStreams.spaceKeyStream.onValue(function(value) {
			me.@net.sevenscales.editor.diagram.MouseDiagramHandlerManager::spaceKey()();
		})
	}-*/;

	private void editable(boolean value) {
		if (editable) {
			// possible to change editable only if originally allowed to edit
			surface.getEditorContext().setEditable(value);
		}
	}

  public boolean onMouseDown(Diagram sender, MatrixPointJS point, int keys) {
   //  logger.start("MouseDiagramHandlerManager.onMouseDown SUM");
  	// logger.start("MouseDiagramHandlerManager.onMouseDown 1");
  	// logger.debug("onMouseDown sender={}...", sender);
  	try {
	    if (Tools.isHandTool() || !surface.getEditorContext().isEditable()) {
	      // if not editable, background should be still movable
	  		backgroundMoveHandler.onMouseDown(sender, point, keys);

		    if (proxyDragHandler != null) {
		    	// possibility to disable modes like hand tool when 
		    	// starting to drag shapes from library
			    proxyDragHandler.onMouseDown(sender, point, keys);
			  }

	  	// 	selectionHandler.onMouseDown(sender, point, keys);
				// lassoSelectionHandler.onMouseDown(sender, point, keys);
	      return false;
	    }
	    
	    // logger.debugTime();
	    // logger.start("MouseDiagramHandlerManager.onMouseDown 2");

	    if (sketchDiagramAreaHandler.onMouseDown(sender, point, keys)) {
	      currentMouseHandler = sketchDiagramAreaHandler;
	//      return false;
	    }

	    // logger.debugTime();
	    // logger.start("MouseDiagramHandlerManager.onMouseDown 3");

	//  	startedConnection = modeManager.isConnectMode();
	//    if (modeManager.isConnectMode()) {
	//    	// priority handler, to make connection helpers more user friendly
	//    	connectionModeMouseHandler.onMouseDown(sender, point, keys);
	//    	startedConnection = true;
	//    	return;
	//    }

	    // send only one down click; either diagram or canvas
	//    this.mouseDown = sender != null ? true : false;
	    
	    // exclusive handlers
	    if (freehandDrawHandler != null) {
		    freehandDrawHandler.onMouseDown(sender, point, keys);
	    }

	    if (freehandDrawHandler != null) {
		    boolean handled = freehandDrawHandler.handling();
		    // logger.debugTime();
		    // logger.start("MouseDiagramHandlerManager.onMouseDown 6");
		    if (handled) {
		    	return false;
		    }
		  }

			resizeHandler.onMouseDown(sender, point, keys);

			lassoSelectionHandler.onMouseDown(sender, point, keys);
	    if (lassoSelectionHandler.isLassoOn()) {
				return true;
	    }

	    // logger.debugTime();
	    // logger.start("MouseDiagramHandlerManager.onMouseDown 4");

	    quickConnectionHandler.onMouseDown(sender, point, keys);
	    selectionHandler.onMouseDown(sender, point, keys);

	    // logger.debugTime();
	    // logger.start("MouseDiagramHandlerManager.onMouseDown 5");
	    			
	    // logger.debugTime();
	    // logger.start("MouseDiagramHandlerManager.onMouseDown 7");
	    
	    handlers.fireMouseDown(sender, point, keys);
	    if (proxyDragHandler != null) {
		    proxyDragHandler.onMouseDown(sender, point, keys);
		  }

	    // logger.debugTime();
	    // logger.start("MouseDiagramHandlerManager.onMouseDown 8");

	    if (sender == null) {
				// do not send diagram events
				// diagrams register them selves straight if those are draggable
				dragHandler.onMouseDown(sender, point, keys);
			}
	    
	    // logger.debugTime();
	    // logger.start("MouseDiagramHandlerManager.onMouseDown 9");

	//		MatrixPointJS translatedPoint = MatrixPointJS.createScaledPoint
	//			(point.getX() - surface.getRootLayer().getTransformX(), 
	//			 point.getY() - surface.getRootLayer().getTransformY(), surface.getScaleFactor());
			backgroundMoveHandler.onMouseDown(sender, point, keys);
			
	    // logger.debugTime();
	    // logger.start("MouseDiagramHandlerManager.onMouseDown 10");

	    // logger.debugTime();
	    // logger.start("MouseDiagramHandlerManager.onMouseDown 11");

			surfaceClickHandler.onMouseDown(sender, point, keys);
  	} catch (Exception e) {
			com.google.gwt.user.client.Window.Location.reload();
  	}

  //   logger.debugTime();
		// logger.debugTime();
		return true;
	}

	public void onMouseEnter(Diagram sender, MatrixPointJS point) {
    handlers.fireMouseEnter(sender, point);
    if (proxyDragHandler != null) {
	    proxyDragHandler.onMouseEnter(sender, point);
	  }
		resizeHandler.onMouseEnter(sender, point);
		backgroundMoveHandler.onMouseEnter(sender, point);
		lassoSelectionHandler.onMouseEnter(sender, point);
	}

	public void onMouseLeave(Diagram sender, MatrixPointJS point) {
    handlers.fireMouseLeave(sender, point);
    if (proxyDragHandler != null) {
	    proxyDragHandler.onMouseLeave(sender, point);
	  }

		resizeHandler.onMouseLeave(sender, point);
		backgroundMoveHandler.onMouseLeave(sender, point);
		lassoSelectionHandler.onMouseLeave(sender, point);
	}
	
	public void onMouseMove(Diagram sender, MatrixPointJS point) {
		try {
	    if (Tools.isHandTool() || !surface.getEditorContext().isEditable()) {
	  		backgroundMoveHandler.onMouseMove(sender, point);
				// lassoSelectionHandler.onMouseMove(sender, point);
	      return;
	    }
	    
	    if (freehandDrawHandler != null) {
		    freehandDrawHandler.onMouseMove(sender, point);
				selectionHandler.onMouseMove(sender, point);
		    if (freehandDrawHandler.handling()) {
		    	return;
		    }
		  }

		  if (lassoSelectionHandler.isLassoOn()) {
		  	lassoSelectionHandler.onMouseMove(sender, point);
		  	return;
		  }
	    
	    handlers.fireMouseMove(sender, point);
	    if (proxyDragHandler != null) {
		    proxyDragHandler.onMouseMove(sender, point);
		  }

	    if (currentMouseHandler == sketchDiagramAreaHandler) {
	      sketchDiagramAreaHandler.onMouseMove(sender, point);
	//      dragHandler.onMouseMove(sender, point);
	//      return;
	    }

			resizeHandler.onMouseMove(sender, point);
			if (sender == null) {
				// do not send diagram events
				// diagrams register them selves straight if those are draggable
				dragHandler.onMouseMove(sender, point);
			}
			backgroundMoveHandler.onMouseMove(sender, point);
			surface.dispatchDiagram(point);

		} catch (Exception e) {
			com.google.gwt.user.client.Window.Location.reload();
		}
	}

	public void onMouseUp(Diagram sender, MatrixPointJS point, int keys) {
		try {
			// logger.start("onMouseUp");
			// logger.debug("onMouseUp...");
	//		startedConnection = false;
	    if (Tools.isHandTool() || !surface.getEditorContext().isEditable()) {
	  		backgroundMoveHandler.onMouseUp(sender, point, keys);
				// lassoSelectionHandler.onMouseUp(sender, point, keys);
				// selectionHandler.onMouseUp(sender, point, keys);
	      return;
	    }
	    
	    if (currentMouseHandler == sketchDiagramAreaHandler) {
	      sketchDiagramAreaHandler.onMouseUp(sender, point, keys);
	    }
	    
	    if (freehandDrawHandler != null) {
		    freehandDrawHandler.onMouseUp(sender, point, keys);
				selectionHandler.onMouseUp(sender, point, keys);
		    if (freehandDrawHandler.handling()) {
		    	return;
		    }
		  }
	    
	//    	mouseDown = false;

	//    connectionModeMouseHandler.onMouseUp(sender, point, keys);
			resizeHandler.onMouseUp(sender, point, keys);
			if (sender == null) {
				// do not send diagram events
				// diagrams register them selves straight if those are draggable
				dragHandler.onMouseUp(sender, point, keys);
			}
	    handlers.fireMouseUp(sender, point, keys);
      if (proxyDragHandler != null) {
		    proxyDragHandler.onMouseUp(sender, point, keys);
		  }

	//		bendHandler.onMouseUp(sender, x, y, keys);
			backgroundMoveHandler.onMouseUp(sender, point, keys);
			lassoSelectionHandler.onMouseUp(sender, point, keys);
			surfaceClickHandler.onMouseUp(sender, point, keys);
			quickConnectionHandler.onMouseUp(sender, point, keys);
			// logger.debugTime();
	    currentMouseHandler = null;
		} catch (Exception e) {
			com.google.gwt.user.client.Window.Location.reload();
		}
	}
	
	@Override
	public void onTouchStart(Diagram sender, MatrixPointJS point) {
		onMouseDown(sender, point, 0);
	}
  @Override
  public void onTouchMove(Diagram sender, MatrixPointJS point) {
		onMouseMove(sender, point);
  }
  @Override
  public void onTouchEnd(Diagram sender, MatrixPointJS point) {
		onMouseUp(sender, point, 0);
  }

	public void makeDraggable(Diagram diagram) {
	  if (surface.getEditorContext().isEditable()) {
	    diagram.addMouseDiagramHandler(dragHandler);
	    diagram.setDragState(dragHandler);
    }
	}
	
  public void makeBendable(Diagram diagram) {
//    if (editable) {
//      diagram.addMouseDiagramHandler(bendHandler);
//    }
  }

	public void addDragHandler(DiagramDragHandler handler) {
		dragHandler.addDragHandler(handler);
	}

	public void addSelectionListener(DiagramSelectionHandler handler) {
    selectionHandler.addDiagramSelectionHandler(handler);
	}

	public void addResizeHandler(DiagramResizeHandler handler) {
    resizeHandler.addResizeHandler(handler);
	}
	
	public SelectionHandler getSelectionHandler() {
		return selectionHandler;
	}
	
//	public MouseDiagramBendHandler getBendHandler() {
//		return bendHandler;
//	}

  public void reset() {
    dragHandler.reset();
    // HACK: cannot reset mouse handlers, since editor is not fully constructed!!
    // and mouse handlers have registered them selves earlier
    // editor opening should create handlers again as well
//    handlers.clear();
    selectionHandler.reset();
    resizeHandler.reset();
  }

  public void addMouseDiagramHandler(MouseDiagramHandler mouseDiagramHandler) {
    handlers.addMouseDiagramHandler(mouseDiagramHandler);
  }

	public void addProxyDragHandler(ProxyDragHandler proxyDragHandler) {
		this.proxyDragHandler = proxyDragHandler;
	}

  public void diagramRemoved(Diagram diagram) {
//    selectionHandler.diagramRemoved(diagram);
  }

  public void select(Diagram diagram) {
    selectionHandler.select(diagram);
  }
  
	public void select(List<Diagram> diagrams) {
    selectionHandler.select(diagrams);
	}

  public MouseDiagramDragHandler getDragHandler() {
    return dragHandler;
  }

//	@Override
//	public void onDoubleClick(DoubleClickEvent event) {
//		surfaceClickHandler.onDoubleClick(event);
//	}

	@Override
	public void onClick(Diagram sender, int x, int y, int keys) {
	}

	@Override
	public void onDoubleClick(Diagram sender, MatrixPointJS point) {
		// resizeHandler.onDoubleClick(sender, point);
		// surfaceClickHandler.onDoubleClick(sender, point);
	}

	@Override
	public boolean isResizing() {
		return resizeHandler.isResizing();
	}


	@Override
	public boolean isDragging() {
		return dragHandler.isDragging();
	}

	@Override	
	public boolean isMovingBackground() {
		return backgroundMoveHandler.backgroundMoveIsOn();
	}

	@Override
	public boolean isLassoOn() {
		return lassoSelectionHandler.isLassoOn();
	}

	@Override	
	public boolean isLassoing() {
		return lassoSelectionHandler.isLassoing();
	}

	private native void handleDoubleTap(Element e, MouseDiagramHandlerManager me)/*-{
		$wnd.Hammer(e, {preventDefault: true}).on('doubletap', function(event) {
			// console.log('handleDoubleTap', event)
			if (event.gesture.center.clientX && event.gesture.center.clientY) {
				me.@net.sevenscales.editor.diagram.MouseDiagramHandlerManager::doubleTap(IIZLjava/lang/String;)(event.gesture.center.clientX, event.gesture.center.clientY, event.gesture.srcEvent.shiftKey, event.target.id);
			}
		})
	}-*/;

	private native void handleMouseDoubleClick(Element e, MouseDiagramHandlerManager me)/*-{
		$wnd.$(e).on('dblclick', function(e) {
			me.@net.sevenscales.editor.diagram.MouseDiagramHandlerManager::doubleTap(IIZLjava/lang/String;)(e.clientX, e.clientY, false, "");
		})
	}-*/;

	private void doubleTap(int x, int y, boolean shiftKey, String targetId) {
		logger.debug("doubleTap...");
		// cannot check connect mode, or will not show property editor
		handleDoubleTap(x, y, shiftKey, targetId);
	}

	/**
	 * TODO this is not very good design that mouse manager knows what kind of events will be fired...
	 * better would be that concrete handlers register for long press and acts accordingly.
	 * This is kind of quick hack...
	 * @param x
	 * @param y
	 */
	public void fireLongPress(int x, int y) {
		if (modeManager.isConnectMode() || surface.getEditorContext().isFreehandMode() || Tools.isHandTool()) {
			// do not handle long press when connection mode is on. 
			// User is probably trying to draw connection. 
			return;
		}

		startLassoSelection();
		// handleDoubleTap(x, y, false, "");
	}

	private native void startLassoSelection()/*-{
		$wnd.globalStreams.contextMenuStream.push({type:'select'})
	}-*/;

	private void handleDoubleTap(int x, int y, boolean shiftKey, String targetId) {
		if (birdsEyeView != null && birdsEyeView.isBirdsEyeViewOn()) {
			birdsEyeView.off();
			return;
		}

		if (isResizing() || !surface.getEditorContext().isEditable()) {
			// prevent handling if on resize area
			// double click or long press disabled if not editable
			return;
		}
		resizeHandler.onLongPress(x, y);

		Tools.setHandTool(false);

		if (!shiftKey) {
			surface.getEditorContext().getEventBus().fireEvent(new FreehandModeChangedEvent(false));		
		}
		
		Set<Diagram> selected = selectionHandler.getSelectedItems();

		if (selected.size() == 1 && targetId.equals(ISurfaceHandler.DRAWING_AREA) && quickConnectionHandler.handleSurfaceDoubleTap()) {
			// it is double tap on surface not on a shape, so check if shoudl create quick connection
			// target is always the original root of the event target
			return;
		}

		if (selected.size() == 1) {
			Diagram s = selected.iterator().next().getOwnerComponent();
	    MatrixPointJS point = MatrixPointJS.createScaledPoint(x, y, surface.getScaleFactor());
			surface.getEditorContext().getEventBus().fireEvent(new ShowDiagramPropertyTextEditorEvent(s, point));
		} else if (selected.size() == 0) {
			if (!quickConnectionHandler.handleDoubleTap()) {
				surface.getEditorContext().getEventBus().fireEvent(new BoardEmptyAreaClickedEvent(x, y));
			}
		}
	}

	private void spaceKey() {
		Set<Diagram> selected = selectionHandler.getSelectedItems();
		if (selected.size() == 0) {
			int x = surface.getCurrentClientMouseMoveX();
			int y = surface.getCurrentClientMouseMoveY();
			surface.getEditorContext().getEventBus().fireEvent(new BoardEmptyAreaClickedEvent(x, y));
		}
	}
	
//	public void setConnectionModeMouseHandler(MouseDiagramHandler handler) {
//		connectionModeMouseHandler = handler;
//	}

}
