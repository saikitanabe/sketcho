package net.sevenscales.editor.uicomponents.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.event.EditDiagramPropertiesEndedEvent;
import net.sevenscales.editor.api.event.EditDiagramPropertiesEndedEventHandler;
import net.sevenscales.editor.api.event.EditDiagramPropertiesStartedEvent;
import net.sevenscales.editor.api.event.EditDiagramPropertiesStartedEventHandler;
import net.sevenscales.editor.api.event.FreehandModeChangedEvent;
import net.sevenscales.editor.api.event.FreehandModeChangedEventHandler;
import net.sevenscales.editor.api.event.UndoEvent;
import net.sevenscales.editor.api.event.UndoEventHandler;
import net.sevenscales.editor.api.impl.TouchHelpers;
import net.sevenscales.editor.content.ui.IModeManager;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.DiagramDragHandler;
import net.sevenscales.editor.diagram.DiagramResizeHandler;
import net.sevenscales.editor.diagram.utils.UiUtils;
import net.sevenscales.editor.gfx.base.GraphicsEvent;
import net.sevenscales.editor.gfx.base.GraphicsMouseDownHandler;
import net.sevenscales.editor.gfx.base.GraphicsMouseEnterHandler;
import net.sevenscales.editor.gfx.base.GraphicsMouseLeaveHandler;
import net.sevenscales.editor.gfx.base.GraphicsMouseMoveHandler;
import net.sevenscales.editor.gfx.base.GraphicsMouseUpHandler;
import net.sevenscales.editor.gfx.base.GraphicsTouchEndHandler;
import net.sevenscales.editor.gfx.base.GraphicsTouchMoveHandler;
import net.sevenscales.editor.gfx.base.GraphicsTouchStartHandler;
import net.sevenscales.editor.gfx.domain.ICircle;
import net.sevenscales.editor.gfx.domain.IGroup;
import net.sevenscales.editor.gfx.domain.IShapeFactory;
import net.sevenscales.editor.uicomponents.AbstractDiagramItem;
import net.sevenscales.editor.gfx.domain.Point;
import net.sevenscales.editor.gfx.domain.Color;


public class ConnectionHelpers implements GraphicsMouseUpHandler, GraphicsMouseMoveHandler, 
																					GraphicsTouchMoveHandler, GraphicsTouchEndHandler, 
																					GraphicsMouseEnterHandler, GraphicsMouseLeaveHandler,
																					IConnectionHelpers {
	private static final SLogger logger = SLogger.createLogger(ConnectionHelpers.class);
	
	private static final int RADIUS_VISIBLE = 5;
	private static final int RADIUS;
	private static final int TO_CENTER = 6;
	private static final int BOTTOM_INDEX = 3;
	
	private IModeManager modeManager;
	private AbstractDiagramItem parent;
  private List<ConnectionHandle> connectionHandles = new ArrayList<ConnectionHandle>();
  private List<ConnectionHandle> extraConnectionHandles = new ArrayList<ConnectionHandle>();
  private ICircle currentMouseOverHandle;
	private ISurfaceHandler surface;
	private boolean pointInHandle = false;
	private boolean shown;
	private IGroup group;
	private boolean propertyEditorShown;
	private boolean someElementIsDragged;
	private boolean freehandModeOn;

	private static final Color ON_COLOR = new Color(0x77, 0x77, 0x77, 1);
	private boolean resizeOn;
	
	private static Map<ISurfaceHandler, IConnectionHelpers> instances;
	
	static {
		instances = new HashMap<ISurfaceHandler, IConnectionHelpers>();
		if (TouchHelpers.isSupportsTouch()) {
			RADIUS = 25;
		} else {
			// mouse pointer needs smaller attach area
			RADIUS = 7;
		}
	}
	
	private ConnectionHelpers(ISurfaceHandler surface, IModeManager modeManager) {
		this.surface = surface;
		this.modeManager = modeManager;
		group = IShapeFactory.Util.factory(true).createGroup(surface.getInteractionLayer());
		init();
		
		surface.getMouseDiagramManager().addDragHandler(new DiagramDragHandler() {
			@Override
			public void onDrag(Diagram sender, int dx, int dy) {
//				if (sender != null && sender.equals(parent)) {
//					setShape(parent.getLeft() + parent.getTransformX(), parent.getTop() + parent.getTransformY(), parent.getWidth(), parent.getHeight());
//				}
			}
			
			@Override
			public boolean isSelected() {
				return false;
			}
			
			@Override
			public void dragStart(Diagram sender) {
				someElementIsDragged = true;
				setVisibility(false);
			}
			
			@Override
			public void dragEnd(Diagram sender) {
				someElementIsDragged = false;
				// TODO doesn't place connection helpers always to correct place, better to hide...				
				if (sender != null && sender.equals(parent)) {
					setShape(parent.getLeft(), parent.getTop(), parent.getWidth(), parent.getHeight());
					setVisibility(true);
				}
			}
		});
		
		surface.getEditorContext().getEventBus().addHandler(EditDiagramPropertiesStartedEvent.TYPE, new EditDiagramPropertiesStartedEventHandler() {
			@Override
			public void on(EditDiagramPropertiesStartedEvent event) {
				propertyEditorShown = true;
			}
		});
		
		surface.getEditorContext().getEventBus().addHandler(EditDiagramPropertiesEndedEvent.TYPE, new EditDiagramPropertiesEndedEventHandler() {
			@Override
			public void on(EditDiagramPropertiesEndedEvent event) {
				propertyEditorShown = false;
			}
		});

		surface.getEditorContext().getEventBus().addHandler(FreehandModeChangedEvent.TYPE, new FreehandModeChangedEventHandler() {
			public void on(FreehandModeChangedEvent event) {
				freehandModeOn = event.isEnabled();
				if (freehandModeOn) {
					hide();
				}
			}
		});

    surface.getEditorContext().getEventBus().addHandler(UndoEvent.TYPE, new UndoEventHandler() {
      public void on(UndoEvent event) {
        hideForce();
      }
    });

		surface.addResizeHandler(new DiagramResizeHandler() {
			@Override
			public void resizeStart(Diagram sender) {
				hide();
				resizeOn = true;
			}
			
			@Override
			public void resizeEnd(Diagram sender) {
				resizeOn = false;
			}
			
			@Override
			public void onResize(Diagram sender, Point diff) {
			}
		});
	}

	private static IConnectionHelpers emptyConnectionHelpers = new IConnectionHelpers() {
		private List<ConnectionHandle> empty = new ArrayList<ConnectionHandle>();
		@Override
		public void show(AbstractDiagramItem parent) {
		}
		
		@Override
		public void toggle(AbstractDiagramItem parent) {
		}
		
		@Override
		public void setVisibility(boolean visibility) {
		}
		
		@Override
		public boolean isShownFor(AbstractDiagramItem diagram) {
			return false;
		}
		
		@Override
		public boolean isShown() {
			return false;
		}
		
		@Override
		public void hide(AbstractDiagramItem diagram) {
		}

		@Override
		public void setShape(int left, int top, int width, int height) {
		}

		@Override
		public void removeExtraConnectionHandles() {
		}

		@Override
		public List<ConnectionHandle> getExtraConnectionHandles() {
			return empty;
		}

		@Override
		public void addExtraConnectionHandle(AbstractDiagramItem parent, int x, int y, int radius) {
		}

		@Override
		public void hideForce() {
		}

    @Override
    public AbstractDiagramItem getParent() {
      return null;
    }

    @Override
    public void hideGlobalElement() {
    }

    @Override
    public void release() {
    }
	};

	
	public static IConnectionHelpers createConnectionHelpers(ISurfaceHandler surface, IModeManager modeManager) {
		IConnectionHelpers result = instances.get(surface);
		if (result == null) {
			if (ISurfaceHandler.DRAWING_AREA.equals(surface.getName())) {
				// logger.debug("createConnectionHelpers created...");
				result = new ConnectionHelpers(surface, modeManager);
			} else {
				// dummy implementation is the default for library
				result = createEmptyConnectionHelpers(); 
			}
			instances.put(surface, result);
		}
		return result;
	}
	
  public static IConnectionHelpers getIfAny(ISurfaceHandler surface) {
    return instances.get(surface);
  }
		
	public static IConnectionHelpers createEmptyConnectionHelpers() {
		return emptyConnectionHelpers;
	}
	
	private void init() {
	  connectionHandles.add(createConnectionHandle());
	  connectionHandles.add(createConnectionHandle());
	  connectionHandles.add(createConnectionHandle());
	  connectionHandles.add(createConnectionHandle());
	}
	
	public void show(AbstractDiagramItem parent) {
		show(parent, parent.getLeft(), parent.getTop(), parent.getWidth(), parent.getHeight());
	}
	public void show(AbstractDiagramItem parent, int left, int top, int width, int height) {
		if (propertyEditorShown || resizeOn || freehandModeOn || someElementIsDragged) {
			// do not show connection helpers if property editor is shown
			// do not show if resize is on going
			logger.debug("show propertyEditorShown {} resizeOn {} freehandModeOn {} someElementIsDragged {}", propertyEditorShown, resizeOn, freehandModeOn, someElementIsDragged);
			return;
		}
		
		this.parent = parent;
		setShape(left, top, width, height);
		logger.debug("ConnectionHelpers.show...");
		
		this.shown = true;
		for (ConnectionHandle c : connectionHandles) {
		  c.visibleHandle.setVisibility(true);
		  c.visibleHandle.setStroke(ON_COLOR);
		  c.connectionHandle.setVisibility(true);
		}
		
		if (parent instanceof IExtraConnectionHandler) {
			((IExtraConnectionHandler) parent).showExtraConnectionHandles();
			if (((IExtraConnectionHandler) parent).disableBottom()) {
				setConnectionHandleVisibility(connectionHandles.get(BOTTOM_INDEX), false);
			}
		} else {
			removeExtraConnectionHandles();
		  // hideExtraConnectionHandles();
		}
	}
	
	private void setConnectionHandleVisibility(ConnectionHandle connectionHandle, boolean visibility) {
		connectionHandle.visibleHandle.setVisibility(visibility);
		connectionHandle.connectionHandle.setVisibility(visibility);
	}

	public void hide(AbstractDiagramItem candidate) {
		ElementHelpers.hide(this, candidate);
	}
	
	@Override
	public void hideForce() {
		hide();
	}
	
	private void hide() {
		logger.debug("ConnectionHelpers.hide {}...", parent);
		this.shown = false;
		currentMouseOverHandle = null;
		
		for (ConnectionHandle c : connectionHandles) {
		  c.visibleHandle.setVisibility(false);
		  unhighlight(c.visibleHandle);
			
		  c.connectionHandle.setVisibility(false);
		}
		
		hideExtraConnectionHandles();
		
		parent = null;
	}
	
	private void hideExtraConnectionHandles() {
   for (ConnectionHandle c : extraConnectionHandles) {
      c.visibleHandle.setVisibility(false);
      unhighlight(c.visibleHandle);
      
      c.connectionHandle.setVisibility(false);
    }
	}

	public void addExtraConnectionHandle(AbstractDiagramItem parent, int cx, int cy, int radius) {
		this.parent = parent;
		ConnectionHandle handle = createConnectionHandle();
		handle.visibleHandle.setShape(cx, cy, RADIUS_VISIBLE);
		handle.visibleHandle.setVisibility(true);
		handle.connectionHandle.setShape(cx, cy, radius);
		extraConnectionHandles.add(handle);
//		shapes.add(handle);
	}
	
	public void removeExtraConnectionHandles() {
	  removeHandles(extraConnectionHandles);
	}
	
	public void removeHandles(List<ConnectionHandle> handles) {
    for (ConnectionHandle h : handles) {
      h.visibleHandle.remove();
      h.connectionHandle.remove();
    }
    handles.clear();
	}
	
	public List<ConnectionHandle> getExtraConnectionHandles() {
		return extraConnectionHandles;
	}

	private ConnectionHandle createConnectionHandle() {
		ICircle visibleHandle = createVisibleHandle();
		return createCombinedConnectionHandle(visibleHandle);
	}

	private ConnectionHandle createCombinedConnectionHandle(ICircle visibleHandle) {
	  ICircle connectionHandle = IShapeFactory.Util.factory(true).createCircle(group);
	  connectionHandle.setStroke(new Color(0, 0, 0, 0));
//	  connectionHandle.setFill(200, 200, 200, 0.6);
	  connectionHandle.setFill(new Color(0, 0, 0, 0));
		final ConnectionHandle result = new ConnectionHandle(visibleHandle, connectionHandle);
		
		highlightConnectionHandleOnMouseEvents(result);
	  
	  connectionHandle.addGraphicsTouchStartHandler(new GraphicsTouchStartHandler() {
			@Override
			public void onTouchStart(GraphicsEvent event) {
				setCurrentHandle(result);
			}
		});
	  connectionHandle.addGraphicsTouchEndHandler(new GraphicsTouchEndHandler() {
			@Override
			public void onTouchEnd(GraphicsEvent event) {
				releaseCurrentHandle();
			}
		});
	  
	  // Handle will act as a parent, so events are routed as those are coming from parent.
	  connectionHandle.addGraphicsMouseDownHandler(new GraphicsMouseDownHandler() {
			@Override
			public void onMouseDown(GraphicsEvent event, int keys) {
				if (parent != null) {
					setCurrentHandle(result);
					parent.onMouseDown(event, keys);
				}
			}
		});
	  connectionHandle.addGraphicsMouseUpHandler(this);
	  connectionHandle.addGraphicsMouseMoveHandler(this);
	  
	  connectionHandle.addGraphicsTouchStartHandler(new GraphicsTouchStartHandler() {
			@Override
			public void onTouchStart(GraphicsEvent event) {
				if (parent != null) {
					setCurrentHandle(result);
					parent.onTouchStart(event);
				}
			}
		});
	  connectionHandle.addGraphicsTouchMoveHandler(this);
	  connectionHandle.addGraphicsTouchEndHandler(this);
	  
	  if (!UiUtils.isOpera()) {
		  connectionHandle.addGraphicsMouseEnterHandler(this);
		  connectionHandle.addGraphicsMouseLeaveHandler(this);
	  }

//	  connectionHandle.moveToFront();
//	  connectionHandle.setVisibility(false);

//	  Scheduler.get().scheduleDeferred(new ScheduledCommand() {
//			@Override
//			public void execute() {
//			  connectionHandle.setVisibility(false);
//			  connectionHandle.moveToFront();
//			}
//		});

		return result;
	}

	private void highlightConnectionHandleOnMouseEvents(final ConnectionHandle connectionHandle) {
		// logger.debug("highlightConnectionHandleOnMouseEvents...");
		connectionHandle.connectionHandle.addGraphicsMouseEnterHandler(new GraphicsMouseEnterHandler() {
			@Override
			public void onMouseEnter(GraphicsEvent event) {
				highlight(connectionHandle.visibleHandle);
			}
		});
	  
	  connectionHandle.connectionHandle.addGraphicsMouseLeaveHandler(new GraphicsMouseLeaveHandler() {
			@Override
			public void onMouseLeave(GraphicsEvent event) {
				unhighlight();
			}
		});
	}

	private ICircle createVisibleHandle() {
	  final ICircle result = IShapeFactory.Util.factory(true).createCircle(group);
	  result.setShape(0, 0, RADIUS_VISIBLE);
	  result.setStrokeWidth(2f);
		unhighlight(result);
		result.setVisibility(false);

	  result.addGraphicsMouseEnterHandler(new GraphicsMouseEnterHandler() {
			@Override
			public void onMouseEnter(GraphicsEvent event) {
				result.setVisibility(true);
				result.setFill(ON_COLOR);
				result.setStroke(ON_COLOR);
			}
		});
	  
	  result.addGraphicsMouseLeaveHandler(new GraphicsMouseLeaveHandler() {
			@Override
			public void onMouseLeave(GraphicsEvent event) {
				unhighlight(result);
			}
		});
		return result;
	}

	private void unhighlight(ICircle result) {
		result.setFill(255, 255, 255, 0.6);
		result.setStroke(34, 34, 34, 0.4);
	}

	public void setVisibility(boolean visible) {
		if (visible && resizeOn) {
			// do not show if resize is on going
			return;
		}
		
		shown = visible;
    for (ConnectionHandle c : connectionHandles) {
    	c.visibleHandle.setVisibility(visible);
    }
    for (ConnectionHandle s : extraConnectionHandles) {
    	s.visibleHandle.setVisibility(visible);
    }
	}
	
	private void setCurrentHandle(ConnectionHandle connectionHandle) {
		highlight(connectionHandle.visibleHandle);

		ConnectionHelpers.this.modeManager.setConnectionMode(true);
		ConnectionHelpers.this.modeManager.setForceConnectionPoint(connectionHandle.visibleHandle.getX() + parent.getTransformX(), connectionHandle.visibleHandle.getY() + parent.getTransformY());
	}

	private void highlight(ICircle visibleHandle) {
		// logger.debug("highlight...");
		currentMouseOverHandle = visibleHandle;
		currentMouseOverHandle.setFill(ON_COLOR);
		currentMouseOverHandle.setStroke(ON_COLOR);
	}

	private void releaseCurrentHandle() {
		// logger.debug("UNhighlight...");
		unhighlight();
	  
		ConnectionHelpers.this.modeManager.setConnectionMode(false);
		ConnectionHelpers.this.modeManager.clearConnectionPoint();
	}

	private void unhighlight() {
		if (currentMouseOverHandle != null) {
			unhighlight(currentMouseOverHandle);
			currentMouseOverHandle = null;
		}
	}

	public void setShape(int left, int top, int width, int height) {
		setLeft(connectionHandles.get(0), left, top, width, height);
		setTop(connectionHandles.get(1), left, top, width, height);
		setRight(connectionHandles.get(2), left, top, width, height);
		setBottom(connectionHandles.get(BOTTOM_INDEX), left, top, width, height);
	}

	private void setLeft(ConnectionHandle connectionHandle, int left, int top, int widht, int height) {
		int centerY = top + height / 2;
		connectionHandle.visibleHandle.setShape(left, centerY, RADIUS_VISIBLE);
		connectionHandle.connectionHandle.setShape(left - RADIUS + TO_CENTER, centerY, RADIUS);
//		connectionHandle.connectionHandle.setShape(left - RADIUS - AWAY_FROM_CENTER, top + RADIUS, RADIUS* 2, height - RADIUS * 2, 0);
	}

	private void setTop(ConnectionHandle connectionHandle, int left, int top, int width, int height) {
		int centerX = left + width / 2;
		connectionHandle.visibleHandle.setShape(centerX, top, RADIUS_VISIBLE);
		connectionHandle.connectionHandle.setShape(centerX, top - RADIUS + TO_CENTER, RADIUS);
//		connectionHandle.connectionHandle.setShape(left + RADIUS, top - RADIUS - AWAY_FROM_CENTER, width - RADIUS * 2, RADIUS * 2, 0);
	}
	
	private void setRight(ConnectionHandle connectionHandle, int left, int top, int width, int height) {
		int centerY = top + height / 2;
		connectionHandle.visibleHandle.setShape(left + width, centerY, RADIUS_VISIBLE);
		connectionHandle.connectionHandle.setShape(left + width + RADIUS - TO_CENTER, centerY, RADIUS);
//		connectionHandle.connectionHandle.setShape(left + width - RADIUS + AWAY_FROM_CENTER, top + RADIUS, RADIUS * 2, height - RADIUS * 2, 0);
	}
	
	private void setBottom(ConnectionHandle connectionHandle, int left, int top, int width, int height) {
		int centerX = left + width / 2;
		int centerY = top + height;
		connectionHandle.visibleHandle.setShape(centerX, centerY, RADIUS_VISIBLE);
		connectionHandle.connectionHandle.setShape(centerX, centerY + RADIUS - TO_CENTER, RADIUS);
//		connectionHandle.connectionHandle.setShape(left + RADIUS, top + height - RADIUS + AWAY_FROM_CENTER, width - RADIUS * 2, RADIUS * 2, 0);
	}

	public ICircle onMouseOverConnectionInitiator() {
		return currentMouseOverHandle;
	}

	public boolean isShown() {
		return shown;
	}
	
	public boolean isShownFor(AbstractDiagramItem diagram) {
		if (shown && diagram.equals(parent)) {
			return true;
		}
		return false;
	}

	public void toggle(AbstractDiagramItem parent) {
		toggle(parent, parent.getLeft(), parent.getTop(), parent.getWidth(), parent.getHeight());
	}
	
	public void toggle(AbstractDiagramItem parent, int left, int top, int width, int height) {
    if (shown) {
    	hideForce();
    } else {
      show(parent, left, top, width, height);
    }
	}

	@Override
	public void onMouseLeave(GraphicsEvent event) {
		if (parent != null) {
			parent.onMouseLeave(event);
		}
	}

	@Override
	public void onMouseEnter(GraphicsEvent event) {
		if (parent != null) {
			parent.onMouseEnter(event);
		}
	}

	@Override
	public void onTouchEnd(GraphicsEvent event) {
		releaseCurrentHandle();
		if (parent != null) {
			parent.onTouchEnd(event);
		}
	}

	@Override
	public void onTouchMove(GraphicsEvent event) {
		if (parent != null) {
			parent.onTouchMove(event);
		}
	}

	@Override
	public void onMouseMove(GraphicsEvent event) {
		if (parent != null) {
			parent.onMouseMove(event);
		}
	}

	@Override
	public void onMouseUp(GraphicsEvent event, int keys) {
		releaseCurrentHandle();
		if (parent != null) {
			parent.onMouseUp(event, keys);
		}
	}

	@Override
	public AbstractDiagramItem getParent() {
		return parent;
	}

	@Override
	public void hideGlobalElement() {
		hide();
	}

  @Override
  public void release() {
    removeHandles(extraConnectionHandles);
    removeHandles(connectionHandles);

    group.remove();
    group = null;
    instances.clear();
  }

}
