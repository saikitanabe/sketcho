package net.sevenscales.editor.uicomponents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.api.IDiagramItem;
import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.ActionType;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.Tools;
import net.sevenscales.editor.api.SurfaceUtil;
import net.sevenscales.editor.api.impl.Theme;
import net.sevenscales.editor.api.impl.Theme.ElementColorScheme;
import net.sevenscales.editor.api.impl.Theme.ThemeName;
import net.sevenscales.editor.api.impl.TouchHelpers;
import net.sevenscales.editor.content.ui.ColorSelections;
import net.sevenscales.editor.content.ui.ContextMenuItem;
import net.sevenscales.editor.content.ui.UMLDiagramSelections.UMLDiagramType;
import net.sevenscales.editor.content.utils.ColorHelpers;
import net.sevenscales.editor.content.utils.Rgb;
import net.sevenscales.editor.content.utils.DiagramItemFactory;
import net.sevenscales.editor.content.utils.AreaUtils;
import net.sevenscales.editor.diagram.ClickDiagramHandler;
import net.sevenscales.editor.diagram.ClickDiagramHandlerCollection;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.DiagramProxy;
import net.sevenscales.editor.diagram.DiagramSelectionHandler;
import net.sevenscales.editor.diagram.DragState;
import net.sevenscales.editor.diagram.KeyEventListener;
import net.sevenscales.editor.diagram.MouseDiagramHandler;
import net.sevenscales.editor.diagram.MouseDiagramListenerCollection;
import net.sevenscales.editor.diagram.SelectionHandlerCollection;
import net.sevenscales.editor.diagram.shape.Info;
import net.sevenscales.editor.gfx.base.GraphicsEvent;
import net.sevenscales.editor.gfx.base.GraphicsMouseDownHandler;
import net.sevenscales.editor.gfx.base.GraphicsMouseEnterHandler;
import net.sevenscales.editor.gfx.base.GraphicsMouseLeaveHandler;
import net.sevenscales.editor.gfx.base.GraphicsMouseMoveHandler;
import net.sevenscales.editor.gfx.base.GraphicsMouseUpHandler;
import net.sevenscales.editor.gfx.base.GraphicsTouchEndHandler;
import net.sevenscales.editor.gfx.base.GraphicsTouchMoveHandler;
import net.sevenscales.editor.gfx.base.GraphicsTouchStartHandler;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.ICircle;
import net.sevenscales.editor.gfx.domain.IGroup;
import net.sevenscales.editor.gfx.domain.IShape;
import net.sevenscales.editor.gfx.domain.IShapeFactory;
import net.sevenscales.editor.gfx.domain.MatrixPointJS;
import net.sevenscales.editor.gfx.domain.SupportsRectangleShape;
import net.sevenscales.editor.uicomponents.AnchorUtils.AnchorProperties;
import net.sevenscales.editor.uicomponents.helpers.ConnectionHelpers;
import net.sevenscales.editor.uicomponents.helpers.IConnectionHelpers;
import net.sevenscales.editor.uicomponents.helpers.ResizeHelpers;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;


public abstract class AbstractDiagramItem implements Diagram, DiagramProxy, 
                                          MouseDiagramHandler, KeyEventListener, 
                                          GraphicsMouseDownHandler,
                                          GraphicsMouseUpHandler,
                                          GraphicsMouseMoveHandler,
                                          GraphicsMouseLeaveHandler,
                                          GraphicsMouseEnterHandler,
                                          GraphicsTouchStartHandler,
                                          GraphicsTouchMoveHandler,
                                          GraphicsTouchEndHandler {
	private final static SLogger logger = SLogger.createLogger(AbstractDiagramItem.class);
	
  static {
    SLogger.addFilter(AbstractDiagramItem.class);
  }

	public static double DEFAULT_FILL_OPACITY = 0;
//	public static Color createDefaultBackgroundColor() { return new Color(0x66, 0x99, 0xff, 0); }
//	public static Color createDefaultBorderColor() { return null; } // create legacy color that is null
//	public static final String DEFAULT_BORDER_COLOR = "#cccccc";
  public static final String DEFAULT_SELECTION_COLOR = "#1D00FF";
  public static final String HIGHLIGHT_COLOR = "#FF0000";
  public static final double STROKE_WIDTH = 2.0;

  private long dispachSequence;
  protected MouseDiagramListenerCollection  mouseListeners;
  private ClickDiagramHandlerCollection clickListeners;
  private boolean mouseDown = false;
  private DiagramProxy ownerComponent;
  private DoubleClickState state;
  private DragState dragState;
  protected int diffFromMouseDownX;
  protected int diffFromMouseDownY;
  private IDiagramItem data;
  protected boolean editable;
  private String link;
  private boolean ctrlDown;
  private boolean verticalMovement;
  private boolean visible;
  protected ISurfaceHandler surface;
  private Map<Anchor,AnchorElement> anchorMap = new HashMap<Anchor,AnchorElement>();
  // to save memory
  protected AnchorProperties tempAnchorProperties = new AnchorProperties();
  private static ICircle anchorPoint;
  private boolean selected;
  /**
  * Need to have removed stated, since element can be removed
  * but still circle element refers to parent element that is already
  * removed, it is faster to query based on state than request from
  * surface handler; though this is duplicate information that needs
  * to be maintained.
  */
  private boolean removed;
  private SelectionHandlerCollection selectionHandlers = new SelectionHandlerCollection();
	protected Color backgroundColor;
	protected Color borderColor;
  protected Color borderColorSwitch;
	protected Color textColor;
  protected IConnectionHelpers connectionHelpers;
	protected ResizeHelpers resizeHelpers;
	protected OnAttachAreaListener onAttachAreaListener;
  protected List<IShape> shapes = new ArrayList<IShape>();
	private SizeChangedHandler sizeChangedHandler;
	protected String borderWebColor;
	private boolean highlightOn;
  private Point coords = new Point();
  private int transformX;
  private int transformY;
  protected int factorX = 1;
  protected int factorY = 1;
	
  public static final String EVENT_DOUBLE_CLICK = "ondblclick";

  public AbstractDiagramItem(boolean editable, ISurfaceHandler surface) {
  	this(editable, surface, Theme.createDefaultBackgroundColor(), 
  													Theme.createDefaultBorderColor(), 
  													Theme.createDefaultTextColor());
  }
  public AbstractDiagramItem(boolean editable, ISurfaceHandler surface, Color backgroundColor, Color borderColor, Color textColor) {
  	this.data = new DiagramItemDTO(); // set default item
    this.editable = editable;
    this.surface = surface;
    this.visible = true; // default value

    // NOTE automatic annotations cannot be enabled at this moment
    // this is too confusing for the user! Instead elements can be
    // marked as annotated if needed from context menu
    // shouldAutoAnnotate();
        
    this.backgroundColor = backgroundColor;
    this.borderColor = borderColor;
    this.borderColorSwitch = borderColor.create();
    this.textColor = textColor;
    mouseListeners = new MouseDiagramListenerCollection();
    clickListeners = new ClickDiagramHandlerCollection();
    state = new DoubleClickState();
    
    connectionHelpers = createConnectionHelpers();
    
    if (anchorPoint == null && ISurfaceHandler.DRAWING_AREA.equals(surface.getName())) {
      // "singleton" common for all diagram elements, there can be visible only one at a time
      // surface.getSurface()
      anchorPoint = IShapeFactory.Util.factory(editable).createCircle(surface.getInteractionLayer());
      anchorPoint.setStrokeWidth(1);
      anchorPoint.setVisibility(false);
    }
    
    this.borderColor = borderColor;
    if (borderColor != null) {
    	this.borderWebColor = "#" + borderColor.toHexString();
    } else {
    	this.borderColor = new Color();
    	// legacy border color is calculated
    	this.borderWebColor = ColorHelpers.createBorderColor(backgroundColor);
			Rgb borderRgb = ColorHelpers.toRgb(borderWebColor);
			this.borderColor.red = borderRgb.red;
			this.borderColor.green = borderRgb.green;
			this.borderColor.blue = borderRgb.blue;
    }
  }

  private void shouldAutoAnnotate() {
    if (Tools.isCommentMode()) {
      data.annotate();
    }
  }

  public void setAnchorPointShape(int ax, int ay) {
    if (anchorPoint != null) {
      anchorPoint.setShape(ax, ay, 6);
      anchorPoint.moveToFront();
    }
  }

  public final Point getCoords() {
    coords.x = getLeft();
    coords.y = getTop();
    return coords;
  }
  
  /**
   * Subclasses to override if not supported. Connection helpers provides 
   * static empty connection handlers. E.g. relationships don't support
   * connection helpers at the moment, e.g. negative height problem quick connecting elements. 
   * @return
   */
	protected IConnectionHelpers createConnectionHelpers() {
		return ConnectionHelpers.createConnectionHelpers(surface, surface.getModeManager());
	}

	protected void constructorDone() {
  }
	
	@Override
	public Diagram getDiagram() {
	  return this;
	}
  
	protected void addEvents(IShape boundary) {
  	if (surface.getEditorContext().isEditable()) {
			boundary.addGraphicsMouseDownHandler(this);
	    boundary.addGraphicsMouseUpHandler(this);
	    boundary.addGraphicsMouseMoveHandler(this);
	    boundary.addGraphicsMouseEnterHandler(this);
	    boundary.addGraphicsMouseLeaveHandler(this);
	    
	    boundary.addGraphicsTouchStartHandler(this);
	    boundary.addGraphicsTouchMoveHandler(this);
	    boundary.addGraphicsTouchEndHandler(this);
  	}
  }
  
  public boolean getEditable() {
    return editable;
  }

  public void onMouseDown(GraphicsEvent event, int keys) {
    int x = event.getElementOffsetX(surface.getElement());
    int y = event.getElementOffsetY(surface.getElement());
    MatrixPointJS point = MatrixPointJS.createScaledPoint(x, y, surface.getScaleFactor());
//    System.out.println("down click:" + this+"x:"+x+"y:"+y);
    mouseListeners.fireMouseDown(this, point, keys);
    mouseDown = true;
    
    if (((AbstractDiagramItem)getOwnerComponent()).state.downClick() && editable) {
      clickListeners.fireDoubleClick(getOwnerComponent(), point);
    }
  }
  
  protected void handleEventDoubleClick(Event event) {
    int x = 0;
    int y = 0;
//    int x = SurfaceUtil.eventGetOffsetX(surface.getElement(), event);
//    int y = SurfaceUtil.eventGetOffsetY(surface.getElement(), event);
//    System.out.println("double click");
//    clickListeners.fireDoubleClick(this, x, y);
  }

  public void onMouseUp(GraphicsEvent event) {
    int x = SurfaceUtil.eventGetElementOffsetX(surface.getElement(), event);
    int y = SurfaceUtil.eventGetElementOffsetY(surface.getElement(), event);
    MatrixPointJS point = MatrixPointJS.createScaledPoint(x, y, surface.getScaleFactor());
    mouseListeners.fireMouseUp(this, point);
    
    if (mouseDown) {
      clickListeners.fireClick(this, x, y, 0);
    }
    mouseDown = false;
  }
  
  public void onMouseMove(GraphicsEvent event) {
    int x = SurfaceUtil.eventGetElementOffsetX(surface.getElement(), event);
    int y = SurfaceUtil.eventGetElementOffsetY(surface.getElement(), event);
    MatrixPointJS point = MatrixPointJS.createScaledPoint(x, y, surface.getScaleFactor());
    mouseListeners.fireMouseMove(this, point);
  }

  public void onMouseLeave(GraphicsEvent event) {
    int x = SurfaceUtil.eventGetElementOffsetX(surface.getElement(), event);
    int y = SurfaceUtil.eventGetElementOffsetY(surface.getElement(), event);
    MatrixPointJS point = MatrixPointJS.createScaledPoint(x, y, surface.getScaleFactor());
    mouseListeners.fireMouseLeave(this, point);
    
//    connectionHelpers.hide(this);
  }

  public void onMouseEnter(GraphicsEvent event) {
    int x = SurfaceUtil.eventGetElementOffsetX(surface.getElement(), event);
    int y = SurfaceUtil.eventGetElementOffsetY(surface.getElement(), event);
    MatrixPointJS point = MatrixPointJS.createScaledPoint(x, y, surface.getScaleFactor());
    mouseListeners.fireMouseEnter(this, point);
    
    // if (!connectionHelpers.isShownFor(this)) {
    // 	connectionHelpers.show(this);
    // }
  }
  
  public void onTouchMove(GraphicsEvent event) {
  	if (event.getTouches() != null && event.getTouches().length() != 1) {
  		// do not handle multiple touch events for diagram items
  		// Unfortunately doesn't work at the moment and requires perhaps invisible focus panel impl
  		return;
  	}
  	
    int x = SurfaceUtil.eventGetElementOffsetX(surface.getElement(), event);
    int y = SurfaceUtil.eventGetElementOffsetY(surface.getElement(), event);
    MatrixPointJS point = MatrixPointJS.createScaledPoint(x, y, surface.getScaleFactor());
    mouseListeners.fireTouchMove(this, point);
  }
  
  @Override
  public void onTouchStart(GraphicsEvent event) {
  	if (event.getTouches() != null && event.getTouches().length() != 1) {
  		// do not handle multiple touch events for diagram items
  		// Unfortunately doesn't work at the moment and requires perhaps invisible focus panel impl
  		return;
  	}
    int x = SurfaceUtil.eventGetElementOffsetX(surface.getElement(), event);
    int y = SurfaceUtil.eventGetElementOffsetY(surface.getElement(), event);
    MatrixPointJS point = MatrixPointJS.createScaledPoint(x, y, surface.getScaleFactor());
    mouseListeners.fireTouchStart(this, point);
  }
  
  @Override
  public void onTouchEnd(GraphicsEvent event) {
    int x = SurfaceUtil.eventGetElementOffsetX(surface.getElement(), event);
    int y = SurfaceUtil.eventGetElementOffsetY(surface.getElement(), event);
    MatrixPointJS point = MatrixPointJS.createScaledPoint(x, y, surface.getScaleFactor());
    mouseListeners.fireTouchEnd(this, point);
  }

  public void addMouseDiagramHandler(MouseDiagramHandler listener) {
    mouseListeners.add(listener);
  }

  public void removeMouseDiagramHandler(MouseDiagramHandler listener) {
    mouseListeners.remove(listener);
  }
  
  public void registerClickHandler(ClickDiagramHandler listener) {
    clickListeners.addClickHandler(listener);
  }
  
  public void unregisterClickHandler(ClickDiagramHandler listener) {
    clickListeners.removeClickHandler(listener);
  }
  
  public void accept(ISurfaceHandler surface) {
    surface.addKeyEventHandler(this);
  }

  public void remove() {
    surface.getSelectionHandler().remove(this);
  }
  
	public void removeFromParent() {
    if (resizeHelpers != null) {
      resizeHelpers.hide(this);
    }
    if (connectionHelpers != null) {
      connectionHelpers.hide(this);
      if (getChildElements() != null) {
        for (Diagram d : getChildElements()) {
          if (d instanceof AbstractDiagramItem) {
            connectionHelpers.hide((AbstractDiagramItem)d);
          }
        }
      }
    }
		surface.remove(this);
    removed = true;

    TextElementFormatUtil textFormatter = getTextFormatter();
    if (textFormatter != null) {
      textFormatter.remove();
    }
	}

  public boolean isRemoved() {
    return removed;
  }
  
  public void select() {
  	logger.debug("select {}", connectionHelpers.isShown());
    selected = true;
    if (anchorPoint != null) {
      anchorPoint.setVisibility(false);
    }
    // List<Diagram> diagrams = new ArrayList<Diagram>();
    // diagrams.add(this);
    // selectionHandlers.fireSelection(diagrams);
    setHighlightColor(DEFAULT_SELECTION_COLOR);
 
    // if (TouchHelpers.isSupportsTouch()) {
    	// usability for touch devices, hide in case connection helpers are on a way
    toggleConnectionHelpers();
		// } else {
		// 	connectionHelpers.show(this);
		// }
    
    if (resizeHelpers != null) {
    	resizeHelpers.show(this);
    }
  }

  public void showResizeHandles() {
    resizeHelpers.show(this);
  }

  public void hideResizeHandles() {
    resizeHelpers.hideGlobalElement();
  }
  
  public int getResizeIndentX() {
  	return 2;
  }
  
  public int getResizeIndentY() {
  	return 2;
  }
  
  public boolean isSelected() {
    return selected;
  }
  public void unselect() {
    // logger.start("AbstractDiagramItem.unselect 1");
    this.selected = false;
    if (anchorPoint != null) {
      anchorPoint.setVisibility(false);
    }
    
    // logger.debugTime();
    // logger.start("AbstractDiagramItem.unselect 2");
    
    selectionHandlers.fireUnselect(this);
    
    // logger.debugTime();
    // logger.start("AbstractDiagramItem.unselect 3");
    
    restoreHighlighColor();

    // logger.debugTime();
    // logger.start("AbstractDiagramItem.unselect 4");

    hideConnectionHelpers();
    
    // logger.debugTime();
    // logger.start("AbstractDiagramItem.unselect 5");

    if (resizeHelpers != null) {
    	resizeHelpers.hide(this);
    }
    
    // logger.debugTime();
  }
  
  public void addDiagramSelectionHandler(DiagramSelectionHandler selectionHandler) {
    selectionHandlers.add(selectionHandler);
  }
  
  public AnchorElement onAttachArea(Anchor anchor, int x, int y) {
    return onAttachArea(anchor, x, y, getLeft(), getTop(), getWidth(), getHeight());
  }
  
  protected boolean onDynamicAttachArea(Anchor anchor, int x, int y) {
  	return AnchorUtils.onAttachArea(x, y, getLeft(), getTop(), getWidth(), getHeight());
  }
  
  public AnchorElement onAttachArea(Anchor anchor, int x, int y, int left, int top, int width, int height) {
  	// TODO need to fix this to use coordinates and not hover property
  	// current implementation just breaks people connections.
//  	ICircle connectionHandle = connectionHelpers.onMouseOverConnectionInitiator();
//    if (AnchorUtils.onAttachArea(x, y, left, top, width, height) || connectionHandle != null) {
  	AnchorElement result = null;
    if (AnchorUtils.onAttachArea(x, y, left, top, width, height)) {
      result = makeAnchorElementFromFixedOrDynamicTempAnchorProperties(anchor, x, y);

//      if (connectionHandle != null) {
//      	// translate to connection handle center
//      	tempAnchorProperties.x = connectionHandle.getX();
//      	tempAnchorProperties.y = connectionHandle.getY();
//      }

      return result;
    }
    return null;
  }
  
  protected AnchorElement makeAnchorElementFromFixedOrDynamicTempAnchorProperties(Anchor anchor, int x, int y) {
    if (makeFixedTempAnchorProperties(anchor, x, y) == null) {
    	makeDynamicTempAnchorProperties(anchor, x, y);
    }
    return makeAnchorElementFromTemp(anchor);
	}
  
  protected AnchorElement makeDynamicTempAnchorProperties(Anchor anchor, int x, int y) {
  	AnchorUtils.anchorPoint(x, y, tempAnchorProperties , getLeft(), getTop(), getWidth(), getHeight());
  	return makeAnchorElementFromTemp(anchor);
  }
  
  /**
   * Returns null if cannot find fixed anchor points.
   */
  protected AnchorElement makeFixedTempAnchorProperties(Anchor anchor, int x, int y) {
    Integer[] fixedAnchorPoints = getFixedAnchorPoints();
    if (fixedAnchorPoints != null) {
      AnchorUtils.anchorPoint(x, y, tempAnchorProperties, fixedAnchorPoints);
      return makeAnchorElementFromTemp(anchor);
    }
    return null;
  }
  
  protected AnchorElement makeAnchorElementFromTemp(Anchor anchor) {
    AnchorElement result = getAnchorElement(anchor);
    result.setAx(tempAnchorProperties.x);
    result.setAy(tempAnchorProperties.y);
    result.setRelativeX(tempAnchorProperties.relativeValueX);
    result.setRelativeY(tempAnchorProperties.relativeValueY);

    setAnchorPointShape(tempAnchorProperties.x, tempAnchorProperties.y);
    return result;
  }
  
	public AnchorElement getAnchorElement(Anchor anchor) {
    AnchorElement result = anchorMap.get(anchor);
    if (result == null) {
      result = new AnchorElement(anchor, this);
      anchorMap.put(anchor, result);
    }
    return result;
  }
  
  @Override
  public AnchorElement anchorWith(Anchor anchor, int x, int y) {
	  AnchorElement result = getAnchorElement(anchor);
	  result.setAx(x);
	  result.setAy(y);
	  tempAnchorProperties.x = x;
	  tempAnchorProperties.y = y;
	 
	  setRelativeAnchor(x, y);
	  result.setRelativeX(tempAnchorProperties.relativeValueX);
	  result.setRelativeY(tempAnchorProperties.relativeValueY);
	 
    surface.getMouseDiagramManager().getDragHandler().attach(anchor.getRelationship(), result);
    return result;
  }
  
  /**
   * To be overriden if relative anchor has some exotic calculation like ellipse.
   * @param x
   * @param y
   */
  protected void setRelativeAnchor(int x, int y) {
    Integer[] fixedAnchorPoints = getFixedAnchorPoints();
    if (fixedAnchorPoints == null) {
    	// do not recalculate relationship position, but use x, y, that's why not using anchorPoint method
   	 	AnchorUtils.relativeValue(tempAnchorProperties, getLeft(), getTop(), getWidth(), getHeight());
    } else {
      AnchorUtils.anchorPoint(x, y, tempAnchorProperties, fixedAnchorPoints);
    }
	}
	protected Integer[] getFixedAnchorPoints() {
		return null;
	}
	public void removeAnchor(Anchor anchor) {
    anchorMap.remove(anchor);
  }
  public Collection<AnchorElement> getAnchors() {
    return anchorMap.values();
  }
  
  public ISurfaceHandler getSurfaceHandler() {
    return surface;
  }

  public Diagram getOwnerComponent() {
    return getOwnerComponent(ActionType.NONE);
  }
  
  public Diagram getOwnerComponent(ActionType actionType) {
  	if (ownerComponent == null) {
  		return this;
  	}
    return ownerComponent.getDiagram();
  }
  
  public void setOwnerComponent(DiagramProxy ownerComponent) {
    this.ownerComponent = ownerComponent;
  }
  
  public Point getOnDragCoords() {
    return null;
  }
  
  public boolean isAutoResize() {
    return true;
  }
  public void setAutoResize(boolean autoresize) {
  }
  
  public String getText() {
    return "";
  }
  @Override
  public String getText(int x, int y) {
  	return getText();
  }
  
  public void setText(String text) {
  }
  @Override
  public void setText(String text, int x, int y) {
  	setText(text);
  }
  
  public void toSvgStart() {

  }

  public void toSvgEnd() {

  }

  public List<IShape> getElements() {
    return shapes;
  }

  @Override
  public List<List<IShape>> getTextElements() {
    TextElementFormatUtil textFormatter = getTextFormatter();
    if (textFormatter != null) {
    	return textFormatter.getLines();
    }
    return null;
  }
  
	public boolean onResizeArea(int x, int y) {
    if (resizeHelpers != null) {
      return resizeHelpers.isOnResizeArea();
    }
    // doesn't support resize
    return false;
  }
  
  public JavaScriptObject getResizeElement() {
    return null;
  }
  
  public void resizeStart() {
    
  }
  
  public boolean resize(Point diff) {
    return false;
  }

  public void resizeEnd() {
  }

  @Override
  public Diagram duplicate() {
    return duplicate(false);
  }

  @Override
  public Diagram duplicate(boolean partOfMultiple) {
    return duplicate(surface, partOfMultiple);
  }

  @Override
  public Diagram duplicate(ISurfaceHandler surface, boolean partOfMultiple) {
    return null;
  }

  @Override
  public Diagram duplicate(ISurfaceHandler surface, int x, int y) {
    return null;
  }

  public void setDragState(DragState dragState) {
    this.dragState = dragState;
  }
  
//  @Override
//  public void applyTransform(MatrixPointJS point) {
//  	applyTransform(point.getDX(), point.getDY());
//  }
  
  public void applyTransform(int dx, int dy) {
  	dx = verticalMovement ? 0 : dx;
  	IGroup group = getGroup();
  	if (group != null) {
  		group.applyTransform(dx, dy);
  	}
  }
  
  public void setTransform(int dx, int dy) {
  	dx = verticalMovement ? 0 : dx;
  	IGroup group = getGroup();
  	if (group != null) {
  		group.setTransform(dx, dy);
  	}
  }
  
	@Override
	public void saveLastTransform(int dx, int dy) {
    dx = verticalMovement ? 0 : dx;
    // resetTransform();

    // apply transformations to shapes
//    int dx = getGroup().getTransformX();
//    int dy = getGroup().getTransformY();

    // IGroup group = getGroup();
    // group.resetTransform();
    // group.setTransform(dx, dy);

    // for (IShape s : shapes) {
    //    s.applyTransformToShape(dx, dy);
    // }

	  // TextElementFormatUtil textFormatter = getTextFormatter();
	  // if (textFormatter != null) {
	  // 	textFormatter.applyTransformToShape(dx, dy);
	  // }
    
//    getGroup().applyTransformToShape(dx, dy);
	}

	public void saveLastTransform() {
	}

  @Override
  public final void resetTransform() {
    IGroup group = getGroup();
    if (group != null) {
      group.resetTransform();
    }
  }
	
	@Override
	public int getTransformX() {
  	IGroup group = getGroup();
  	if (group != null) {
  		return group.getTransformX();
  	}
		return 0;
	}

	@Override
	public int getTransformY() {
  	IGroup group = getGroup();
  	if (group != null) {
  		return group.getTransformY();
  	}
		return 0;
	}

  public void setDiagramItem(IDiagramItem data) {
    this.data = data;
  }
  
  public IDiagramItem getDiagramItem() {
    return this.data;
  }
    
  public void setLink(String link) {
    this.link = link;
  }
  
  public String getLink() {
    return link;
  }

  public void setVisible(boolean visible) {
    if (this.visible != visible) {
      this.visible = visible; 
      IGroup group = getGroup();
      if (group != null) {
        group.setVisible(visible);
      }

      if (getChildElements() != null) {
        for (Diagram child : getChildElements()) {
          child.setVisible(visible);
        }
      }

      TextElementFormatUtil textFormatter = getTextFormatter();
      if (textFormatter != null) {
        textFormatter.setVisible(visible);
      }
    }
  }
  
  public boolean isVisible() {
    return visible;
  }
  
  public boolean okToBend() {
    return false;
  }
  
  public void bendStart(int x, int y) {
    
  }
  public void bend(int dx, int dy) {
    // TODO Auto-generated method stub
    
  }
  
  public void setReadOnly(boolean value) {
    if (anchorPoint != null &&  anchorPoint.isVisible()) {
      anchorPoint.setVisibility(!value);
    }
    // connection helpers should be always visible when setting read only state
    connectionHelpers.setVisibility(false);
  }
  
/////////////////////////////////

  //  @Override
  public boolean onKeyEventDown(int keyCode, boolean shift, boolean ctrl) {
    if (keyCode == BrowserStyle.SI_KEY_CTRL) {
      // boolean ctrl is only given if some other key is pressed at the same time
      this.ctrlDown = true;
    }
    return false;
  }

//  @Override
  public boolean onKeyEventUp(int keyCode, boolean shift, boolean ctrl) {
    if (keyCode == BrowserStyle.SI_KEY_CTRL) {
      // boolean ctrl is only given if some other key is pressed at the same time 
      this.ctrlDown = false;
    }
    return false;
  } 
  
  public void onKeyDown(Event event) {
  // TODO Auto-generated method stub
  
  }
  
  public void onKeyUp(Event event) {
  // TODO Auto-generated method stub
  
  }
  public void onKeyPress(Event event) {   
  }
  
  public void onKeyDown(KeyDownEvent event) {
    // TODO Auto-generated method stub
  }
  public void onKeyPress(KeyPressEvent event) {
    // TODO Auto-generated method stub
  }
  public void onKeyUp(KeyUpEvent event) {
    // TODO Auto-generated method stub
  }
  
/////////////////////////////////

// From MouseDiagramListener
  public boolean onMouseDown(Diagram sender, MatrixPointJS point, int keys) {
//    if ((ctrlDown && getLink() != null) || (!editable && getLink() != null)) {
//      // on read only state mouse click is enough
//      String token = History.getToken();
//      Location current = new Location(token);
//      
//      Long projectId = RequestUtils.parseLong(RequestId.PROJECT_ID, current.getRequests());
//      Map<Object, Object> requests = new HashMap<Object, Object>();
//      requests.put(RequestId.CONTROLLER, RequestValue.PAGE_CONTROLLER);
//      requests.put(RequestId.PAGE_ID, getLink());
//      requests.put(RequestId.PROJECT_ID, projectId);
//      
//      RequestUtils.activate(requests);
//    }
    return false;
  }

  public void onMouseEnter(Diagram sender, MatrixPointJS point) {
  }
  public void onMouseLeave(Diagram sender, MatrixPointJS point) {
  }
  public void onMouseMove(Diagram sender, MatrixPointJS point) {
  }
  public void onMouseUp(Diagram sender, MatrixPointJS point) {
  }
  @Override
  public void onTouchMove(Diagram sender, MatrixPointJS point) {
  }
  @Override
  public void onTouchStart(Diagram sender, MatrixPointJS point) {
  }
  @Override
  public void onTouchEnd(Diagram sender, MatrixPointJS point) {
  }
  
  public void setVerticalMovement(boolean verticalMovement) {
    this.verticalMovement = verticalMovement;
  }
  
  @Override
  public UMLDiagramType getDiagramType() {
  	return UMLDiagramType.NONE;
  }
  
  @Override
  public boolean canSetBackgroundColor() {
  	return true;
  }
  
  @Override
  public void setBackgroundColor(int red, int green, int blue, double opacity) {
//  	if (opacity > 0) {
	  	backgroundColor.red = red;
	  	backgroundColor.green = green;
	  	backgroundColor.blue = blue;
//  	}
  	backgroundColor.opacity = opacity;
  }
  
  @Override
  public void setBackgroundColor(Color color) {
    setBackgroundColor(color.red, color.green, color.blue, color.opacity);
  }
  
  @Override
  public String getBackgroundColor() {
  	return ColorSelections.rgb2hex(backgroundColor.red, backgroundColor.green, backgroundColor.blue);
  }
  @Override
  public String getBackgroundColorRgba() {
  	return "rgba(" + backgroundColor.red + "," + backgroundColor.green + "," + backgroundColor.blue + "," + backgroundColor.opacity + ")";
  }
  
  @Override
  public Color getBackgroundColorAsColor() {
    return backgroundColor;
  }

  protected Color getBackgroundDrawingColor() {
    if (ThemeName.BLACK.equals(Theme.getCurrentThemeName()) && !usesSchemeDefaultColors(Theme.getCurrentColorScheme())) {
      borderColorSwitch.copy(borderColor);
      borderColorSwitch.opacity = backgroundColor.opacity;
      return borderColorSwitch;
    }
    return backgroundColor;
  }

  
  @Override
  public void setTextColor(int red, int green, int blue) {
  	textColor.red = red;
  	textColor.green = green;
  	textColor.blue = blue;
  	applyTextColor();
  }
  
  protected void applyTextColor() {
    TextElementFormatUtil textFormatter = getTextFormatter();
    if (textFormatter != null) {
      textFormatter.applyTextColor();
    }
  }
  
  @Override
  public void setTextColor(Color color) {
    textColor.copy(color);
    applyTextColor();
  }
  
  @Override
  public String getTextColor() {
  	return ColorSelections.rgb2hex(textColor.red, textColor.green, textColor.blue);
  }
  
  @Override
  public Color getTextColorAsColor() {
    return textColor;
  }
  
  @Override
  public boolean onArea(int left, int top, int right, int bottom) {    
    int x = getLeft();
    int y = getTop();
    int bx = x + getWidth();
    int by = y + getHeight();
    
    return AreaUtils.onArea(x, y, bx, by, left, top, right, bottom);
  }
  
  @Override
  public boolean onArea(int x, int y) {
  	if (onAttachAreaListener == null) {
  		return false;
  	}
  	
  	if (this instanceof SupportsRectangleShape) {
  		if (AnchorUtils.onAttachArea(x, y, getLeft(), getTop(), getWidth(), getHeight())) {
  			onAttachAreaListener.onAttachArea(this, x, y);
  			return true;
  		} else {
  			onAttachAreaListener.notOnArea(this);
  		}
  	}
  	return false;
  }
  
  @Override
  public void moveToBack() {
  }
	public void moveToFront() {
	}
	
	public final int getLeft() {
		return doGetLeft() + getTransformX();
	}
	public final int getTop() {
		return doGetTop() + getTransformY();
	}
  protected abstract int doGetLeft();
  protected abstract int doGetTop();

	public int getWidth() {
		return 0;
	}
	public int getHeight() {
		return 0;
	}
	
	@Override
	public int getCenterX() {
		return getLeft() + getWidth() / 2;
	}
	@Override
	public int getCenterY() {
		return getTop() + getHeight() / 2;
	}
	public void setHeight(int height) {
	}

	@Override
	public void copyFrom(IDiagramItemRO diagramItem) {
    logger.start("copyFrom SUM");
    logger.start("copyFrom 1");
    IDiagramItemRO current = getDiagramItem();
    // getDiagramItem().copyFrom(diagramItem);

    logger.debugTime();
    logger.start("copyFrom 2");
//		setDiagramText(diagramItem);
		
//		IDiagramItem me = DiagramItemFactory.createOrUpdate(this, false);
//		if (!me.getShape().equals(diagramItem.getShape())) {
			// cannot change shape at the same time
			// it will fail
			// sequence item has wrong format!! => convert first space to comma
    String newshape = diagramItem.getShape();
    boolean shapeChanged = false;
    if (newshape != null && !newshape.equals(current.getShape())) {
      shapeChanged = true;
      String shapestr = newshape.replaceFirst("\\s", ",");
      String[] shapeString = shapestr.split(",");
      int[] shape = new int[shapeString.length];
      int i = 0;
      for (String val : shapeString) {
        shape[i] = Integer.valueOf(val);
        i = i + 1;
      }
      setShape(shape);
    }

    logger.debugTime();
    logger.start("copyFrom 3");

    String newCustomData = diagramItem.getCustomData();
    if (newCustomData != null && !newCustomData.equals(current.getCustomData())) {
      parseCustomData(newCustomData);
    }
			
    logger.debugTime();
    logger.start("copyFrom 4");

    String newText = diagramItem.getText();
    if (shapeChanged || newText != null && !newText.equals(current.getText())) {
      setDiagramText(diagramItem);
    }

    logger.debugTime();
    logger.start("copyFrom 5");

//		}
		
    String newTextColor = diagramItem.getTextColor();
    if (newTextColor != null && !newTextColor.equals(current.getTextColor())) {
      Color textColor = DiagramItemFactory.parseTextColor(diagramItem);
      setTextColor(textColor.red, textColor.green, textColor.blue);
    }

    logger.debugTime();
    logger.start("copyFrom 6");

    String newBackgroundColor = diagramItem.getBackgroundColor();
    if (newBackgroundColor != null && !newBackgroundColor.equals(current.getBackgroundColor())) {
  		Color borderColor = DiagramItemFactory.parseBorderColor(diagramItem);
  		if (borderColor != null) {
  			// legacy impl didn't have border color
  			setBorderColor(borderColor);
  		}

      Color bgColor = DiagramItemFactory.parseBackgroundColor(diagramItem);
      setBackgroundColor(bgColor.red, bgColor.green, bgColor.blue, bgColor.opacity);
    }

    logger.debugTime();
    logger.start("copyFrom 7");
		
		if (connectionHelpers.isShownFor(this)) {
			connectionHelpers.show(this);
		}

    logger.debugTime();
    logger.start("copyFrom 9");

    boolean newResolved = diagramItem.isResolved();
    if (newResolved != current.isResolved()) {
      // item is shown if it is not resolved
      setVisible(!newResolved);
    }

    // just all fields
    getDiagramItem().copyFrom(diagramItem);

    logger.debugTime();
    logger.debugTime();
	}

	private void setDiagramText(IDiagramItemRO diagramItem) {
		// if (!diagramItem.getText().equals(getText())) {
  	surface.getEditorContext().set(EditorProperty.ON_SURFACE_LOAD, true);
		setText(diagramItem.getText());
  	surface.getEditorContext().set(EditorProperty.ON_SURFACE_LOAD, false);
		// }
	}

	public final void setShape(int[] shape) {
    doSetShape(shape);
    resetTransform();

    TextElementFormatUtil textFormatter = getTextFormatter();
    if (textFormatter != null) {
      textFormatter.setTextShape();
    }

	}

  protected abstract void doSetShape(int[] shape);
	
	@Override
	public int[] getShape() {
		return null;
	}
	
	@Override
	public String getCustomData() {
		return "";
	}
	
	@Override
	public void parseCustomData(String customData) {
	}
	
	@Override
	public void restoreHighlighColor() {
    if (selected) {
      setHighlightColor(DEFAULT_SELECTION_COLOR);
    } else {
      setBorderColor(getBorderColorAsColor());
    }
	}

  @Override
	public void setHighlightColor(String color) {
	}

  // @Override
  // public void setBorderColor(String color) {
  // 	this.borderWebColor = color;
  // 	Rgb bc = ColorHelpers.toRgb(color);
  // 	borderColor.red = bc.red;
  // 	borderColor.green = bc.green;
  // 	borderColor.blue = bc.blue;
  // 	borderColor.opacity = 1;
  // 	setHighlightColor(color);
  //   applyAnnotationColors();
  // }
  
  @Override
  public void setBorderColor(Color color) {
    borderColor.copy(color);
    this.borderWebColor = color.toHexString();
    setHighlightColor(borderWebColor);
    applyAnnotationColors();
  }

	public void registerOnAttachArea(OnAttachAreaListener listener) {
    onAttachAreaListener = listener;
	}
	
	public void setSizeChangedHandlerByText(SizeChangedHandler handler) {
		this.sizeChangedHandler = handler;
	}
	protected void fireSizeChanged() {
		if (sizeChangedHandler != null) {
			sizeChangedHandler.onSizeChanged(this, getWidth(), getHeight());
		}
	}
	
	@Override
	public void hideText() {
		TextElementFormatUtil textFormatter = getTextFormatter();
		if (textFormatter != null) {
			textFormatter.hide();
		}
	}

	@Override
	public void showText() {
		TextElementFormatUtil textFormatter = getTextFormatter();
		if (textFormatter != null) {
			textFormatter.show();
		}
	}

	public String getTextAreaBackgroundColor() {
		if (backgroundColor.opacity == 0) {
			// transparent
			return "transparent";
		}
		return "#" + getBackgroundColor();
	}
	
	@Override
	public int getTextAreaLeft() {
		return getLeft() + 4;
	}
	
	@Override
	public int getTextAreaTop() {
		return getTop() + 11;
	}
	
	@Override
	public int getTextAreaWidth() {
		return getWidth() - 5;
	}
	
	@Override
	public int getTextAreaHeight() {
		return getHeight() - 13;
	}
	
	@Override
	public String getTextAreaAlign() {
		return "left";
	}
	
	@Override
	public boolean supportsOnlyTextareaDynamicHeight() {
		return false;
	}
	
	protected TextElementFormatUtil getTextFormatter() {
		return null;
	}

  protected void dispatchAndRecalculateAnchorPositions() {
    dispatchAndRecalculateAnchorPositions(getLeft(), getTop(), getWidth(), getHeight());
  }
	
	protected void dispatchAndRecalculateAnchorPositions(int left, int top, int width, int height) {
  	++this.dispachSequence;
  	for (AnchorElement a : anchorMap.values()) {
  		dispatch(a, left, top, width, height, dispachSequence);
  	}
	}
	
	protected void dispatch(AnchorElement a, int left, int top, int width, int height, long dispachSequence) {
		Integer[] fixedPoints = getFixedAnchorPoints();
		if (fixedPoints == null) {
			AnchorUtils.setRelativePosition(a, left, top, width, height);
		} else {
			// use fixed point index
		  double ax = a.getRelativeFactorX();
		  double ay = a.getRelativeFactorY();
		  if ( (ax >= 0 && ax < fixedPoints.length) && (ay >= 0 && ay < fixedPoints.length) ) {
        int x = fixedPoints[(int) ax];
        int y = fixedPoints[(int) ay];

        if (!fixedIncludesTransformation()) {
          // this is HACK! Activity Choice Element doesn't have transformations in shape :)
          // and Sequence Element has transformatinos in shape, YEAH :)
          x += getTransformX();
          y += getTransformY();
        }
  			a.setAx(x);
  			a.setAy(y);
		  }
		}
		a.dispatch(dispachSequence);
	}

  protected boolean fixedIncludesTransformation() {
    return false;
  }
	
	public String getBorderColor() {
		return borderWebColor;
	}
	
	@Override
	public Color getBorderColorAsColor() {
	  return borderColor;
	}
	
	/**
	 * Switches drawing color to look better on black.
	 * @return
	 */
	protected Color getBorderDrawingColor() {
   if (ThemeName.BLACK.equals(Theme.getCurrentThemeName()) && !usesSchemeDefaultColors(Theme.getCurrentColorScheme())) {
     borderColorSwitch.copy(backgroundColor);
     borderColorSwitch.opacity = borderColor.opacity;
     return borderColorSwitch;
   }
   return borderColor;
	}
	
  public void setHighlight(boolean highlight) {
  	this.highlightOn = highlight;
    String color = borderWebColor;
    if (highlight) {
      color = HIGHLIGHT_COLOR;
    }
    
    if (anchorPoint != null) {
      anchorPoint.setVisibility(highlight);
      anchorPoint.setStroke(color);
    }

    if (!highlight && isAnnotation()) {
      applyAnnotationColors();
    } else {
      setHighlightColor(color);
    }
  }
  
  public boolean isHighlightOn() {
		return highlightOn;
	}
  
  @Override
  public boolean supportsTextEditing() {
  	return false;
  }

  public boolean supportsAlignHighlight() {
    return true;
  }
  
//  public Map<Anchor, AnchorElement> getAnchorMap() {
//		return anchorMap;
//	}
  
  public AnchorProperties getTempAnchorProperties() {
		return tempAnchorProperties;
	}

  @Override
  public int supportedMenuItems() {
  	return ContextMenuItem.NO_CUSTOM.getValue() | ContextMenuItem.COLOR_MENU.getValue();
  }

  @Override
  public boolean equals(Object obj) {
  	return obj == this;
  }

	public void fillInfo(Info info) {
    info.setLeft(getLeft());
    info.setTop(getTop());
    info.setWidth(getWidth());
    info.setHeight(getHeight());

	  // check if all colors equals with current scheme colors
	  // if they do then save color information based on white paper theme
    ElementColorScheme paper = getRefrenceColorScheme();
	  if (usesSchemeDefaultColors(getCurrentColorScheme())) {
      setColorInfo(info, getDefaultBackgroundColor(paper), getDefaultBorderColor(paper), getDefaultTextColor(paper));
	  } else {
	    Color textColor = this.textColor; 
	    if (isTextColorAccordingToBackgroundColor()) {
	      // if text color is according to background color
	      // convert text color for saving/sending to paper color
	      // e.g. actor and server elements behave like this, but text element could change
	      // text color
	      textColor = getDefaultTextColor(paper);
	    }
	    setColorInfo(info, backgroundColor, borderColor, textColor);
	  }
	}

  protected ElementColorScheme getRefrenceColorScheme() {
    return Theme.getColorScheme(ThemeName.PAPER);
  }

  protected ElementColorScheme getCurrentColorScheme() {
    return Theme.getCurrentColorScheme();
  }
	
	private void setColorInfo(Info info, Color backgroundColor, Color borderColor, Color textColor) {
    info.setBackgroundColor(backgroundColor.toRgbWithOpacity());
    if (borderColor != null) {
      info.setBorderColor(borderColor.toRgbWithOpacity());
    }
    info.setTextColor(textColor.toRgbWithOpacity());
	}
	
  /**
   * Checks if diagram uses default color specified for the element type.
   * Each diagram element type can specify it's own default colors.
   * @param colorScheme 
   */
  public boolean usesSchemeDefaultColors(ElementColorScheme colorScheme) {
    if (!this.getDefaultTextColor(colorScheme).equals(this.getTextColorAsColor())) {
      return false;
    }
    
    if (!this.getDefaultBorderColor(colorScheme).equals(this.getBorderColorAsColor())) {
      return false;
    }
    if (!(this.getDefaultBackgroundColor(colorScheme).equals(this.getBackgroundColorAsColor()))) {
      return false;
    }
    return true;
  }


	@Override
	public int getMeasurementAreaWidth() {
		return getTextAreaWidth();
	}

  protected void toggleConnectionHelpers() {
    connectionHelpers.toggle(this);
  }
	
	@Override
	public void hideConnectionHelpers() {
		connectionHelpers.hideForce();
	}
	public void applyHelpersShape() {
		if (resizeHelpers != null) {
			resizeHelpers.setShape();
		}
    connectionHelpers.setShape(getLeft(), getTop(), getWidth(), getHeight());
	}

  // too much is processed and most probably gets executed
  // also on production!!!	
	// @Override
	// public String toString() {
	// 	return "{" + super.toString() + getDiagramItem() + "}";
	// }
	
	@Override
	public Color getDefaultBackgroundColor(ElementColorScheme colorScheme) {
	  return colorScheme.getBackgroundColor();
	}
	@Override
	public Color getDefaultBorderColor(ElementColorScheme colorScheme) {
    return colorScheme.getBorderColor();
	}
	@Override
	public Color getDefaultTextColor(ElementColorScheme colorScheme) {
    return colorScheme.getTextColor();
	}
	
	@Override
	public boolean isTextElementBackgroundTransparent() {
	  return false;
	}
	
	@Override
	public boolean isTextColorAccordingToBackgroundColor() {
	  return false;
	}

  public void snapshotTransformations() {
    transformX = getTransformX();
    transformY = getTransformY();
  }

  public int getSnaphsotTransformX() {
    return transformX;
  }

  public int getSnaphsotTransformY() {
    return transformY;
  }

  public List<? extends Diagram> getChildElements() {
    return null;
  }

  public void attachedRelationship(AnchorElement anchorElement) {
  }

  @Override
  public boolean isAnnotation() {
    return getDiagramItem().isAnnotation();
  }

  @Override
  public boolean isResolved() {
    return getDiagramItem().isResolved();
  }

  public void annotate() {
    getDiagramItem().annotate();
    applyAnnotationColors();
  }

  public void applyAnnotationColors() {
    if (getDiagramItem() != null && getDiagramItem().isAnnotation() && supportsAnnotationColors()) {
      setHighlightColor(Theme.getCommentThreadColorScheme().getBackgroundColor().toHexString());
    }
  }

  protected boolean supportsAnnotationColors() {
    return true;
  }

  public void unannotate() {
    getDiagramItem().unannotate();
    setHighlightColor(getBorderColor());
  }

  public void setDuplicateMultiplySize(int factorX, int factorY) {
    this.factorX = factorX;
    this.factorY = factorY;
  }

}
