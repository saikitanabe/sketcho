package net.sevenscales.editor.diagram;


import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.Timer;

import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.IBirdsEyeView;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.ReactAPI;
import net.sevenscales.editor.api.SketchDiagramAreaHandler;
import net.sevenscales.editor.api.Tools;
import net.sevenscales.editor.api.event.BoardEmptyAreaClickedEvent;
import net.sevenscales.editor.api.event.FreehandModeChangedEvent;
import net.sevenscales.editor.api.event.ShowDiagramPropertyTextEditorEvent;
import net.sevenscales.editor.api.event.ShowDiagramPropertyTextEditorEventHandler;
import net.sevenscales.editor.content.ui.IModeManager;
import net.sevenscales.editor.diagram.drag.MouseDiagramDragHandler;
import net.sevenscales.editor.gfx.domain.MatrixPointJS;
import net.sevenscales.editor.gfx.domain.OrgEvent;


public class MouseDiagramHandlerManager implements
  MouseDiagramHandler,
  ClickDiagramHandler,
  MouseState,
  DoubleTapHandler.IDoubleTapHandler {
	private static final SLogger logger = SLogger.createLogger(MouseDiagramHandlerManager.class);

	static {
		SLogger.addFilter(MouseDiagramHandlerManager.class);
	}

	private MouseDiagramDragHandler dragHandler;
//	private MouseDiagramBendHandler bendHandler;
	private SelectionHandler selectionHandler;
	private MouseDiagramResizeHandler resizeHandler;
  private boolean editable;
  private IBackgroundMoveHandler backgroundMoveHandler;
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
	private DoubleTapHandler doubleTapHandler;
	private boolean cancelMouseDown;

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
    doubleTapHandler = new DoubleTapHandler(editable, surface, selectionHandler, this);
		resizeHandler = new MouseDiagramResizeHandler(this, surface, modeManager);
		backgroundMoveHandler = createBackgroundMoveHandler(surface, diagrams);
		lassoSelectionHandler = new LassoSelectionHandler(surface, this);
		if (ISurfaceHandler.DRAWING_AREA.equals(surface.getName())) {
			// free hand drawing is not possible on library area
			freehandDrawHandler = new FreehandDrawerHandler(surface);
		}
		surfaceClickHandler = new SurfaceClickHandler(surface);
		sketchDiagramAreaHandler = new SketchDiagramAreaHandler(surface, modeManager);
		new LinkHandler(surface);
		quickConnectionHandler = new QuickConnectionHandler(surface);

		// legacy code uses hammer for double tap and double click, but has performance problems		
		// if (!UiUtils.isIE()) {
		// 	handleDoubleTap(surface.getElement(), this);
		// } else {
		// 	IE looses all mouse events if Hammer is used for double click
		// 	so handling through jquery on, this solution will not work on
		// 	Microsoft Surface
		// 	handleMouseDoubleClick(surface.getElement(), this);
		// }

		// new code handles both separately and doesn't use hammer
		// due to performance problems
    // always handling both double tap and double click
    

		if (!surface.isLibrary()) {
			_init(this);
		}
		// addMouseDiagramHandler(sketchDiagramAreaHandler);
  }

  private IBackgroundMoveHandler createBackgroundMoveHandler(
    ISurfaceHandler surface,
    List<Diagram> diagrams
  ) {

    if (backgroundMoveHandler != null) {
      backgroundMoveHandler.unregister();
    }

    if (ReactAPI.isNav2()) {
      return new BackgroundMoveHandlerV2(diagrams, surface);
    }

    return new BackgroundMoveHandler(diagrams, surface);
  }
  
  private ShowDiagramPropertyTextEditorEventHandler showEditorHandler = 
    new ShowDiagramPropertyTextEditorEventHandler() {

    @Override
    public void on(ShowDiagramPropertyTextEditorEvent event) {
      cancelMouseDown = true;
      releaseCancelMouseDown.schedule(200);      
    }

  };

	private native void _init(MouseDiagramHandlerManager me)/*-{
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

    $wnd.globalStreams.navigationUpdatedStream.onValue(function(value) {
      me.@net.sevenscales.editor.diagram.MouseDiagramHandlerManager::updateNavigation()();
    })

	}-*/;

  private void updateNavigation() {
    backgroundMoveHandler = createBackgroundMoveHandler(surface, surface.getDiagrams());
  }

	private void editable(boolean value) {
		if (editable) {
			// possible to change editable only if originally allowed to edit
			surface.getEditorContext().setEditable(value);
		}
	}

  @Override
  public boolean onMouseDown(OrgEvent event, Diagram sender, MatrixPointJS point, int keys) {
    // if (doubleTapHandler.isDoubleTap()) {
    //   return false;
    // }

   //  logger.start("MouseDiagramHandlerManager.onMouseDown SUM");
  	// logger.start("MouseDiagramHandlerManager.onMouseDown 1");
  	// logger.debug("onMouseDown sender={}...", sender);
  	try {

      if (cancelMouseDown) {
        // Fix iPad touch double tap after mouse down
        // now double tap cancels immediate touch/mouse/pointer down
        return false;
      }

      // Debug.log("MouseDiagramHandlerManager.onMouseDown...");

	    if (!surface.getEditorContext().isEditable()) {
	      // if not editable, background should be still movable
	  		backgroundMoveHandler.onMouseDown(event, sender, point, keys);

		    if (proxyDragHandler != null) {
		    	// possibility to disable modes like hand tool when 
		    	// starting to drag shapes from library
			    proxyDragHandler.onMouseDown(event, sender, point, keys);
			  }

	  	// 	selectionHandler.onMouseDown(sender, point, keys);
				// lassoSelectionHandler.onMouseDown(sender, point, keys);
	      return false;
	    }
	    
	    // logger.debugTime();
	    // logger.start("MouseDiagramHandlerManager.onMouseDown 2");

	    if (sketchDiagramAreaHandler.onMouseDown(event, sender, point, keys)) {
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
		    freehandDrawHandler.onMouseDown(event, sender, point, keys);
	    }

	    if (freehandDrawHandler != null) {
		    boolean handled = freehandDrawHandler.handling();
		    // logger.debugTime();
		    // logger.start("MouseDiagramHandlerManager.onMouseDown 6");
		    if (handled) {
		    	return false;
		    }
		  }

			resizeHandler.onMouseDown(event, sender, point, keys);

			lassoSelectionHandler.onMouseDown(event, sender, point, keys);
	    if (lassoSelectionHandler.isLassoOn()) {
				return true;
	    }

	    // logger.debugTime();
	    // logger.start("MouseDiagramHandlerManager.onMouseDown 4");

	    quickConnectionHandler.onMouseDown(event, sender, point, keys);
	    selectionHandler.onMouseDown(event, sender, point, keys);

	    // logger.debugTime();
	    // logger.start("MouseDiagramHandlerManager.onMouseDown 5");
	    			
	    // logger.debugTime();
	    // logger.start("MouseDiagramHandlerManager.onMouseDown 7");
	    
	    handlers.fireMouseDown(event, sender, point, keys);
	    if (proxyDragHandler != null) {
		    proxyDragHandler.onMouseDown(event, sender, point, keys);
		  }

	    // logger.debugTime();
	    // logger.start("MouseDiagramHandlerManager.onMouseDown 8");

	    if (sender == null) {
				// do not send diagram events
				// diagrams register them selves straight if those are draggable
				dragHandler.onMouseDown(event, sender, point, keys);
			}
	    
	    // logger.debugTime();
	    // logger.start("MouseDiagramHandlerManager.onMouseDown 9");

	//		MatrixPointJS translatedPoint = MatrixPointJS.createScaledPoint
	//			(point.getX() - surface.getRootLayer().getTransformX(), 
	//			 point.getY() - surface.getRootLayer().getTransformY(), surface.getScaleFactor());
			backgroundMoveHandler.onMouseDown(event, sender, point, keys);
			
	    // logger.debugTime();
	    // logger.start("MouseDiagramHandlerManager.onMouseDown 10");

	    // logger.debugTime();
	    // logger.start("MouseDiagramHandlerManager.onMouseDown 11");

			surfaceClickHandler.onMouseDown(event, sender, point, keys);
  	} catch (Exception e) {
  		net.sevenscales.domain.utils.Error.reload(e);
  	}

  //   logger.debugTime();
		// logger.debugTime();
		return true;
	}

  @Override
	public void onMouseEnter(OrgEvent event, Diagram sender, MatrixPointJS point) {
    handlers.fireMouseEnter(event, sender, point);
    if (proxyDragHandler != null) {
	    proxyDragHandler.onMouseEnter(event, sender, point);
	  }
		resizeHandler.onMouseEnter(event, sender, point);
		backgroundMoveHandler.onMouseEnter(event, sender, point);
		lassoSelectionHandler.onMouseEnter(event, sender, point);
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
  
  @Override
	public void onMouseMove(OrgEvent event, Diagram sender, MatrixPointJS point) {
    if (doubleTapHandler.isDoubleTap()) {
      return;
    }

		try {
	    if (!surface.getEditorContext().isEditable()) {
	  		backgroundMoveHandler.onMouseMove(event, sender, point);
				// lassoSelectionHandler.onMouseMove(sender, point);
	      return;
	    }
	    
	    if (freehandDrawHandler != null) {
		    freehandDrawHandler.onMouseMove(event, sender, point);
				selectionHandler.onMouseMove(event, sender, point);
		    if (freehandDrawHandler.handling()) {
		    	return;
		    }
		  }

		  if (lassoSelectionHandler.isLassoOn()) {
		  	lassoSelectionHandler.onMouseMove(event, sender, point);
		  	return;
		  }
	    
	    handlers.fireMouseMove(event, sender, point);
	    if (proxyDragHandler != null) {
		    proxyDragHandler.onMouseMove(event, sender, point);
		  }

	    if (currentMouseHandler == sketchDiagramAreaHandler) {
	      sketchDiagramAreaHandler.onMouseMove(event, sender, point);
	//      dragHandler.onMouseMove(sender, point);
	//      return;
	    }

			resizeHandler.onMouseMove(event, sender, point);
			if (sender == null) {
				// do not send diagram events
				// diagrams register them selves straight if those are draggable
				dragHandler.onMouseMove(event, sender, point);
			}
			backgroundMoveHandler.onMouseMove(event, sender, point);
			surface.dispatchDiagram(point);

		} catch (Exception e) {
			net.sevenscales.domain.utils.Error.reload(e);
		}
	}

	public void onMouseUp(Diagram sender, MatrixPointJS point, int keys) {
    if (cancelMouseDown) {
      // Fix iPad after quick connection handler created shape editor is closed
      // now show editor blocks next touch/mouse/pointer up
      return;
    }

		if (doubleTapHandler.isDoubleTap()) {
			// double tap is handling this currently
			// reset the state
      // doubleTapHandler.resetState();
			return;
		}

		try {
			// logger.start("onMouseUp");
			// logger.debug("onMouseUp...");
	//		startedConnection = false;
	    if (!surface.getEditorContext().isEditable()) {
	  		backgroundMoveHandler.onMouseUp(sender, point, keys);
				// lassoSelectionHandler.onMouseUp(sender, point, keys);
				// selectionHandler.onMouseUp(sender, point, keys);
	      return;
	    }
	    
	    if (currentMouseHandler == sketchDiagramAreaHandler) {
				sketchDiagramAreaHandler.onMouseUp(sender, point, keys);
				// ST 2.11.2018: Fix endless loop when sender is null
				// This is a regression bug after insertMoveElement
				// initiated dragEnd call on onMouseUp
				// and RelationshipDragEndHandler.onNotAttached
				// calls fake mouse up to free resources
				// see below: dragHandler.onMouseUp(sender, point, keys);
				// clear already handled
				currentMouseHandler = null;
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
			net.sevenscales.editor.uicomponents.AnchorUtils.hide();
			// logger.debugTime();
	    currentMouseHandler = null;
		} catch (Exception e) {
			net.sevenscales.domain.utils.Error.reload(e);
		}
	}
	
	@Override
	public void onTouchStart(OrgEvent event, Diagram sender, MatrixPointJS point) {
		onMouseDown(event, sender, point, 0);
	}
  @Override
  public void onTouchMove(OrgEvent event, Diagram sender, MatrixPointJS point) {
		onMouseMove(event, sender, point);
  }
  @Override
  public void onTouchEnd(Diagram sender, MatrixPointJS point) {
  	if (!doubleTapHandler.isDoubleTap()) {
  		// double tap is handling touch end already
	  	// net.sevenscales.domain.utils.Debug.log("onTouchEnd", itsDoubleTap);
			onMouseUp(sender, point, 0);
  	}
  	// reset the state
    // itsDoubleTap = true;
    doubleTapHandler.resetState();
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

  public IBackgroundMoveHandler getBackgroundMoveHandler() {
    return backgroundMoveHandler;
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

	/**
	 * TODO this is not very good design that mouse manager knows what kind of events will be fired...
	 * better would be that concrete handlers register for long press and acts accordingly.
	 * This is kind of quick hack...
	 * @param x
	 * @param y
	 */
	public void fireLongPress(int x, int y) {
		if (modeManager.isConnectMode() || surface.getEditorContext().isFreehandMode() || isResizing()) {
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
  
  private Timer releaseCancelMouseDown = new Timer() {
    @Override
    public void run() {
      // Debug.log("releaseCancelMouseDown...");
      cancelMouseDown = false;
    }
  };


	public void handleDoubleTap(int x, int y, boolean shiftKey, String targetId) {
    releaseCancelMouseDown.cancel();
    backgroundMoveHandler.cancelBackgroundMove();
    lassoSelectionHandler.cancel();

    cancelMouseDown = true;
    releaseCancelMouseDown.schedule(200);

		if (surface.getEditorContext().isFreehandMode()) {
			// double click is disabled on freehand
			return;
		}

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

		if (!shiftKey) {
			surface.getEditorContext().getEventBus().fireEvent(new FreehandModeChangedEvent(false));		
		}
		
		Set<Diagram> selected = selectionHandler.getSelectedItems();

		if (selected.size() == 1 && targetId.equals(ISurfaceHandler.DRAWING_AREA) && quickConnectionHandler.handleSurfaceDoubleTap(x, y)) {
			// it is double tap on surface not on a shape, so check if shoudl create quick connection
			// target is always the original root of the event target
			return;
		}

    if (selected.size() == 1) {
			Diagram s = selected.iterator().next().getOwnerComponent();
	    MatrixPointJS point = MatrixPointJS.createScaledPoint(x, y, surface.getScaleFactor());
			surface.getEditorContext().getEventBus().fireEvent(new ShowDiagramPropertyTextEditorEvent(s, point));
		} else if (selected.size() == 0) {
			if (!quickConnectionHandler.handleDoubleTap(x, y)) {
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
