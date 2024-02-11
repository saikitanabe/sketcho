package net.sevenscales.editor.uicomponents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.Event;

import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.ShapeProperty;
import net.sevenscales.domain.api.IDiagramItem;
import net.sevenscales.domain.js.JsShape;
import net.sevenscales.domain.utils.DiagramItemHelpers;
import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.ActionType;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.SurfaceUtil;
import net.sevenscales.editor.api.Tools;
import net.sevenscales.editor.api.impl.Theme;
import net.sevenscales.editor.api.impl.Theme.ElementColorScheme;
import net.sevenscales.editor.api.impl.Theme.ThemeName;
import net.sevenscales.editor.content.ui.ColorSelections;
import net.sevenscales.editor.content.ui.ContextMenuItem;
import net.sevenscales.editor.content.ui.UMLDiagramType;
import net.sevenscales.editor.content.utils.AreaUtils;
import net.sevenscales.editor.content.utils.ColorHelpers;
import net.sevenscales.editor.content.utils.ContainerAttachHelpers;
import net.sevenscales.editor.content.utils.DiagramItemFactory;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.DiagramProxy;
import net.sevenscales.editor.diagram.DiagramSelectionHandler;
import net.sevenscales.editor.diagram.DragState;
import net.sevenscales.editor.diagram.KeyEventListener;
import net.sevenscales.editor.diagram.MouseDiagramHandler;
import net.sevenscales.editor.diagram.MouseDiagramListenerCollection;
import net.sevenscales.editor.diagram.SelectionHandlerCollection;
import net.sevenscales.editor.diagram.drag.Anchor;
import net.sevenscales.editor.diagram.drag.AnchorElement;
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
import net.sevenscales.editor.gfx.domain.IRectangle;
import net.sevenscales.editor.gfx.domain.MatrixPointJS;
import net.sevenscales.editor.gfx.domain.OrgEvent;
import net.sevenscales.editor.gfx.domain.Point;
import net.sevenscales.editor.gfx.domain.SupportsRectangleShape;
import net.sevenscales.editor.gfx.domain.Promise;
import net.sevenscales.editor.gfx.domain.ElementSize;
import net.sevenscales.editor.uicomponents.AnchorUtils.AnchorProperties;
import net.sevenscales.editor.uicomponents.helpers.ConnectionHelpers;
import net.sevenscales.editor.uicomponents.helpers.IConnectionHelpers;
import net.sevenscales.editor.uicomponents.helpers.ResizeHelpers;
import net.sevenscales.editor.uicomponents.uml.ShapeCache;

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
  public static final Color DEFAULT_SELECTION_COLOR = new Color(0x1D, 0x00, 0xFF, 1);
  public static final Color HIGHLIGHT_COLOR = new Color(0x6A, 0xCA, 0x00, 1);
  // public static final Color HIGHLIGHT_COLOR_TO_ANCHOR = new Color(0x7A, 0xBE, 0x37, 1);
  public static final double STROKE_WIDTH = 2.0;

  private int dispachSequence;
  protected MouseDiagramListenerCollection  mouseListeners;
  private boolean mouseDown = false;
  private DiagramProxy ownerComponent;
  private DragState dragState;
  protected int diffFromMouseDownX;
  protected int diffFromMouseDownY;
  private IDiagramItem data;
  protected boolean editable;
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
	private boolean highlightOn;
  private Point coords = new Point();
  private int transformX;
  private int transformY;
  protected int factorX = 1;
  protected int factorY = 1;
  private boolean svgExport;
  private boolean forceTextRendering;

  private Integer rotateDegree;
  private LinkElement linkElement;
	
  public static final String EVENT_DOUBLE_CLICK = "ondblclick";

  public AbstractDiagramItem(boolean editable, ISurfaceHandler surface, IDiagramItemRO item) {
  	this(editable, surface, Theme.createDefaultBackgroundColor(), 
  													Theme.createDefaultBorderColor(), 
  													Theme.createDefaultTextColor(), item);
  }
  public AbstractDiagramItem(boolean editable, ISurfaceHandler surface, Color backgroundColor, Color borderColor, Color textColor, IDiagramItemRO item) {
    // Always a valid state from the very beginning!
    this.data = item.copy();

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
    
    connectionHelpers = createConnectionHelpers();
    
    if (anchorPoint == null && ISurfaceHandler.DRAWING_AREA.equals(surface.getName())) {
      // "singleton" common for all diagram elements, there can be visible only one at a time
      // surface.getSurface()
      anchorPoint = IShapeFactory.Util.factory(editable).createCircle(surface.getInteractionLayer());
      anchorPoint.setStrokeWidth(2);
      anchorPoint.setVisibility(false);
    }
    
    this.borderColor = borderColor;
    if (borderColor == null) {
    	// legacy border color is calculated
    	this.borderColor = ColorHelpers.createBorderColor(backgroundColor);
    }
  }

  private void shouldAutoAnnotate() {
    if (Tools.isCommentMode()) {
      data.annotate();
    }
  }

  public void setAnchorPointShape(int ax, int ay) {
    if (anchorPoint != null) {
      anchorPoint.setShape(ax, ay, 7);
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
    setShapeCssClass();
    applyLink();
    // ST 21.11.2018: Fix do not override shape class
    // IGroup group = getGroup();
    // if (group != null) {
    //   // net.sevenscales.domain.utils.Debug.log("add shapebase...");
    //   group.setAttribute("class", "shapebase");
    // }


    rotate(getDiagramItem().getRotateDegrees(), false);
  }

  protected void applyLink() {

    // FIX link not available on image element sometimes:
    // make sure that shape css is set that is used to
    // position anchor icon.
    setShapeCssClass();    

    String link = getLink();

    if (link == null || link == "") {
      if (linkElement != null) {
        // case: link removed
        linkElement.remove();
        linkElement = null;
      }
      return;
    }

    // IGroup group = getSubgroup();
    // if (group == null) {
    //   group = getGroup();
    // }
    IGroup group = getGroup();

    if (group != null && linkElement == null) {
      linkElement = _createLink(group.getElement());
    }

    linkElement.setLink(link, getRelativeLeft(), getRelativeTop());

    // if (!this.surface.isExporting()) {
    //   // enabled only on export
    //   return;
    // }

    // IGroup g = getSubgroup();
    // if (g == null) {
    //   g = getGroup();
    // }
    // if (g != null) {
    //   // TODO tooltip of the link
    //   String linkUrl = getLink();
    //   if (linkUrl != null && !"".equals(linkUrl)) {
    //     JsShape linkicon = ShapeCache.findIcon("link");
    //     if (linkicon != null) {
    //       SafeUri url = UriUtils.fromString(linkUrl);
    //       String urlString = url.asString(); //.replaceAll("&", "&amp;");
    //       // _applyLink(g.getContainer(), "#linkshape", (int)(getWidth() / 2 - linkicon.getWidth() / 2), getHeight(), urlString);
    //       _applyLink(g.getContainer(), "#linkshape", (int)(getWidth() - 3), -25, urlString);
    //     }
    //   } else {
    //     _deleteLink(g.getContainer());
    //   }
    // }
  }

  private native void _deleteLink(JavaScriptObject group)/*-{
    if (group.alink && group.alink.parentNode == group.rawNode) {
      group.rawNode.removeChild(group.alink);
      group.alink = null
    }
  }-*/;

  private native void _applyLink(JavaScriptObject group, String linkid, int x, int y, String link)/*-{

    function _createElementNS(ns, nodeType){
      // summary:
      //    Internal helper to deal with creating elements that
      //    are namespaced.  Mainly to get SVG markup output
      //    working on IE.
      if($doc.createElementNS){
        return $doc.createElementNS(ns,nodeType)
      }else{
        return $doc.createElement(nodeType)
      }
    }

    function _setAttributeNS(node, ns, attr, value){
     if(node.setAttributeNS){
       return node.setAttributeNS(ns, attr, value)
     }else{
       return node.setAttribute(attr, value)
     }
    }

    var svgns = "http://www.w3.org/2000/svg"
    var xlinkns = 'http://www.w3.org/1999/xlink'

    function createOrFindLink() {
      function create() {
        var alink = _createElementNS(svgns, 'a')
        var use = _createElementNS(svgns, 'use')
        _setAttributeNS(use, xlinkns, "xlink:href", linkid)
        alink.appendChild(use)
        alink.use = use
        group.rawNode.appendChild(alink)
        group.alink = alink
        return alink
      }

      if (group.alink) {
        return group.alink
      }

      return create()
    }

    var alink = createOrFindLink()

    alink.use.setAttribute('x', x)
    alink.use.setAttribute('y', y)
    alink.setAttribute('title', link)

    // having a HTML layer to handle link clicks on touch devices
    // this is no longer needed, because mystically it doesn't
    // work sometimes on touch...
    // if (typeof $wnd.__addLinkListener__ === 'function') {
    //   $wnd.__addLinkListener__(alink)
    // }

    // TODO tooltip doesn't work on SvgHandler (preview handler)
    // bootstrap is not available
    // $wnd.$(alink).tooltip()

    // if (!@net.sevenscales.editor.diagram.utils.UiUtils::isFirefox()()) {
      // webkit browsers handles properly  a href in svg
      _setAttributeNS(alink, xlinkns, "xlink:href", link)

    if (typeof $wnd.linkOutside === 'function' &&
        $wnd.linkOutside(link)) {
      _setAttributeNS(alink, null, "target", "_blank")
      _setAttributeNS(alink, null, "rel", "noopener noreferrer")
    }
    // } else {
      // Firefox opens same page when pressing cmd+click on different tab
      // NOTE doesn't work, still opens also same page on different tab
      // function openLink(e) {
      //   try {
      //     $wnd.open(this.thelink, "", "")
      //   } catch (e) {
      //     console.log(e)
      //   }
      // }
      // alink.use.removeEventListener('click', openLink)
      // alink.use.thelink = link

      // alink.use.addEventListener('click', openLink, false)
    // }

  }-*/;

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
    try {
      int x = event.getElementOffsetX(surface.getElement());
      int y = event.getElementOffsetY(surface.getElement());
      MatrixPointJS point = MatrixPointJS.createScaledPoint(x, y, surface.getScaleFactor());
      mouseListeners.fireMouseDown(new OrgEvent(event), this, point, keys);
      mouseDown = true;
    } catch (Exception e) {
      net.sevenscales.domain.utils.Error.reload(e);
    }
  }
  
  @Override
  public void onMouseUp(GraphicsEvent event, int keys) {
    try {
      int x = SurfaceUtil.eventGetElementOffsetX(surface.getElement(), event);
      int y = SurfaceUtil.eventGetElementOffsetY(surface.getElement(), event);
      MatrixPointJS point = MatrixPointJS.createScaledPoint(x, y, surface.getScaleFactor());
      mouseListeners.fireMouseUp(this, point, keys);
      
      mouseDown = false;
    } catch (Exception e) {
      net.sevenscales.domain.utils.Error.reload(e);
    }
  }
  
  public void onMouseMove(GraphicsEvent event) {
    try {
      int x = SurfaceUtil.eventGetElementOffsetX(surface.getElement(), event);
      int y = SurfaceUtil.eventGetElementOffsetY(surface.getElement(), event);
      MatrixPointJS point = MatrixPointJS.createScaledPoint(x, y, surface.getScaleFactor());
      mouseListeners.fireMouseMove(new OrgEvent(event), this, point);
    } catch (Exception e) {
      net.sevenscales.domain.utils.Error.reload(e);
    }
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
    mouseListeners.fireMouseEnter(new OrgEvent(event), this, point);
    
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
    mouseListeners.fireTouchMove(new OrgEvent(event), this, point);
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
    mouseListeners.fireTouchStart(new OrgEvent(event), this, point);
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
  
  public void accept(ISurfaceHandler surface) {
    surface.addKeyEventHandler(this);
  }

  public void release() {
    TextElementFormatUtil tf = getTextFormatter();
    if (tf != null) {
      tf.release();
    }

    // fix memory leak of a TextForeignObject
    surface.removeKeyEventHandler(this);
  }

  public void remove() {
    surface.getSelectionHandler().remove(this, true);
  }
  
	public void removeFromParent() {
    removeFromParentWithoutConnections();
    removeConnections();
	}

  public void removeFromParentWithoutConnections() {
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

  private void removeConnections() {
    for (AnchorElement ae : getAnchors()) {
      if (ae.getRelationship() != null && ae.getRelationship().noText()) {
        surface.getSelectionHandler().addToBeRemovedCycle(ae.getRelationship());
      }
    }
  }

  public boolean isRemoved() {
    return removed;
  }

  public boolean changeRemoveToModify() {
    return false;
  }
  
  public void select() {
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
    if (!getDiagramItem().isGroup()) {
      toggleConnectionHelpers();
    }
		// } else {
		// 	connectionHelpers.show(this);
		// }
    
    if (resizeHelpers != null && !getDiagramItem().isGroup()) {
    	resizeHelpers.show(this);
    }
  }

  public void showResizeHandles() {
    if (resizeHelpers != null) {
      resizeHelpers.show(this);
    }
  }

  public void hideResizeHandles() {
    if (resizeHelpers != null) {
      resizeHelpers.hideGlobalElement();
    }
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

    Color color = null;
    if (surface.getBoardUserHandler() != null) {
      color = surface.getBoardUserHandler().getColor(this);
    }
    
    restoreHighlighColor(color);

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
    // return onAttachArea(anchor, x, y, getLeft(), getTop(), getWidth(), getHeight());
    return ContainerAttachHelpers.onAttachAreaRotated(
      this,
      anchor,
      x,
      y,
      surface.getInteractionLayer()
    );
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
  	AnchorUtils.anchorPoint(x, y, tempAnchorProperties , getLeft(), getTop(), getWidth(), getHeight(), getDiagramItem().getRotateDegrees());
  	AnchorElement result = makeAnchorElementFromTemp(anchor);
    result.setFixedPoint(false);
    return result;
  }
  
  /**
   * Returns null if cannot find fixed anchor points.
   */
  protected AnchorElement makeFixedTempAnchorProperties(Anchor anchor, int x, int y) {
    Integer[] fixedAnchorPoints = getFixedAnchorPoints();
    if (fixedAnchorPoints != null) {
      AnchorUtils.anchorPoint(x, y, getLeft(), getTop(), getWidth(), getHeight(), tempAnchorProperties, fixedAnchorPoints);
      AnchorElement result = makeAnchorElementFromTemp(anchor);
      result.setFixedPoint(true);
      return result;
    }
    return null;
  }
  
  protected AnchorElement makeAnchorElementFromTemp(Anchor anchor) {
    AnchorElement result = getAnchorElement(anchor);
    result.setAx(tempAnchorProperties.x);
    result.setAy(tempAnchorProperties.y);
    result.setRelativeX(tempAnchorProperties.relativeValueX);
    result.setRelativeY(tempAnchorProperties.relativeValueY);
    result.setCardinalDirection(tempAnchorProperties.cardinalDirection);

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

    boolean fixedOrRelative = setFixedOrRelativeAnchor(x, y, anchor);
    result.setRelativeX(tempAnchorProperties.relativeValueX);
    result.setRelativeY(tempAnchorProperties.relativeValueY);
    result.setFixedPoint(fixedOrRelative);
    logger.debug("tempAnchorProperties.cardinalDirection {}", tempAnchorProperties.cardinalDirection);
    result.setCardinalDirection(tempAnchorProperties.cardinalDirection);
    result.attach();
    anchor.applyAnchorElement(result);
    // anchor.getDiagram().attachedRelationship(result);
    // surface.getMouseDiagramManager().getDragHandler().attach(anchor.getRelationship(), result);

    return result;
  }

  /**
   * To be overriden if relative anchor has some exotic calculation like ellipse.
   * @param x
   * @param y
   * @return false if relative point; true if fixed point
   */
  protected boolean setFixedOrRelativeAnchor(int x, int y, Anchor anchor) {
    Integer[] fixedAnchorPoints = getFixedAnchorPoints();
    if (fixedAnchorPoints == null) {
    	// do not recalculate relationship position, but use x, y, that's why not using anchorPoint method
   	 	AnchorUtils.relativeValue(tempAnchorProperties, x, y, getLeft(), getTop(), getWidth(), getHeight());
      return false;
    } else {
      AnchorUtils.anchorPoint(x, y, getLeft(), getTop(), getWidth(), getHeight(), tempAnchorProperties, fixedAnchorPoints);
      return true;
    }
	}

	protected Integer[] getFixedAnchorPoints() {
		return null;
	}
	public void removeAnchor(Anchor anchor) {
    anchorMap.remove(anchor);
  }
  public void clearAnchorMap() {
    anchorMap.clear();
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

  @Override
  public boolean isSketchiness() {
    return false;
  }
  
  public Diagram showEditorForDiagram(int screenX, int screenY) {
    return this;
  }

  public String getText() {
    return "";
  }
  @Override
  public String getText(int x, int y) {
  	return getText();
  }
  
  @Override
  public final void setText(String text) {
    applyFontDecorations();
    doSetText(text);
  }

  @Override
  public final void setText(String text, int x, int y) {
    applyFontDecorations();
    doSetText(text, x, y);
  }

  protected void doSetText(String text) {
  }

  protected void doSetText(String text, int x, int y) {
    doSetText(text);
  }

  private void applyFontDecorations() {
    TextElementFormatUtil textFormatter = getTextFormatter();
    if (textFormatter != null && getDiagramItem().getFontSize() != null) {
      textFormatter.setFontSize(getDiagramItem().getFontSize());
    }
  }
  
  public void toSvgStart() {
    this.svgExport = true;
  }

  public void toSvgEnd() {
    this.svgExport = false;
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
    // NOTE if specialization is needed, override this functionality
    getDiagramItem().addShapeProperty(ShapeProperty.DISABLE_SHAPE_AUTO_RESIZE);
  }
  
  public boolean resize(Point diff) {
    return false;
  }

  public void resizeEnd() {
    rotate(getDiagramItem().getRotateDegrees(), false);
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
    return getTransformX(getGroup());
	}
	protected int getTransformX(IGroup group) {
  	if (group != null) {
  		return group.getTransformX();
  	}
		return 0;
	}

	@Override
	public int getTransformY() {
    return getTransformY(getGroup());
	}
	protected int getTransformY(IGroup group) {
  	if (group != null) {
  		return group.getTransformY();
  	}
		return 0;
	}

  public void setDiagramItem(IDiagramItem data) {
    this.data = data;
    IGroup group = getGroup();
    if (group != null) {
      group.setAttribute("id", "s"+data.getClientId());
    }
  }
  
  public IDiagramItem getDiagramItem() {
    return this.data;
  }
    
  public void setLink(String link) {
    data.setLink(link);
    applyLink();
    notifyLinkAdded(link);
  }

  private native LinkElement _createLink(
    JavaScriptObject el
  )/*-{
    return $wnd.createElementLink(el)
  }-*/;

  private native void notifyLinkAdded(String url)/*-{
    $wnd.globalStreams.shapeLinkStream.push(url)
  }-*/;
  
  public String getLink() {
    if (data != null) {
      return data.getFirstLink();
    }
    return null;
  }

  public boolean hasLink() {
    String link = getLink();
    // need to have at least something, event this is too little.
    return link != null && link.length() > 3;
  }

  public void setGroupId(String groupId) {
    data.setGroup(groupId);
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
    if (connectionHelpers != null) {
      connectionHelpers.setVisibility(false);
    }
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
  @Override
  public boolean onMouseDown(OrgEvent event, Diagram sender, MatrixPointJS point, int keys) {
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

  @Override
  public void onMouseEnter(OrgEvent event, Diagram sender, MatrixPointJS point) {
  }
  public void onMouseLeave(Diagram sender, MatrixPointJS point) {
  }
  @Override
  public void onMouseMove(OrgEvent event, Diagram sender, MatrixPointJS point) {
  }
  public void onMouseUp(Diagram sender, MatrixPointJS point, int keys) {
  }
  @Override
  public void onTouchMove(OrgEvent event, Diagram sender, MatrixPointJS point) {
  }
  @Override
  public void onTouchStart(OrgEvent event, Diagram sender, MatrixPointJS point) {
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
  public boolean hasDefaultColors() {
    return false;
  }

  @Override
  public void restoreDefaultColors() {
  }
  
  // @Override
  // public void setBackgroundColor(int red, int green, int blue, double opacity) {
  // 	backgroundColor.red = red;
  // 	backgroundColor.green = green;
  // 	backgroundColor.blue = blue;
  // 	backgroundColor.opacity = opacity;

  //   setShapeCssClass();
  // }
  
  @Override
  public void setBackgroundColor(Color color) {
    // setBackgroundColor(color.red, color.green, color.blue, color.opacity);

    backgroundColor.red = color.red;
    backgroundColor.green = color.green;
    backgroundColor.blue = color.blue;
    backgroundColor.opacity = color.opacity;
    backgroundColor.gradient = color.gradient;

    setShapeCssClass();
  }

  protected void setShapeCssClass() {
    // if (!this.surface.isExporting()) {
    //   // enabled only on export
    //   return;
    // }
    
    Color bg = getBackgroundColorAsColor();
    IGroup g = getGroup();
    if (g != null && bg.opacity != 0 && ColorHelpers.isRgbBlack(bg.red, bg.green, bg.blue)) {
      g.setAttribute("class", "dark-shape shapebase");
    } else {
      g.setAttribute("class", "shapebase");
    }
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
  public Color getTextColor() {
    return textColor;
  }

  @Override
  public void setFontSize(Integer fontSize) {
    if (fontSize != null && fontSize > 0) {
      TextElementFormatUtil formatter = getTextFormatter();
      if (formatter.isSupportFontSize()) {
        getDiagramItem().setFontSize(fontSize);
        if (formatter != null) {
          formatter.setFontSize(fontSize);
          formatter.reapplyText();
        }
      }
    }
  }

  @Override
  public void rotate(
    Integer degrees,
    boolean save
  ) {

    // hide resize helpers when rotating
    if (resizeHelpers != null) {
      resizeHelpers.hide(this);
    }

    // 0 clears rotation
    int rdeg = degrees != null ? degrees : 0;

    // FIX: rotate doesn't work correctly if using resized
    // shape and then rotate for AWS shapes.
    // Rotation gets skewed if matrix and rotation are applied
    // on a same group. Now there is a new rotate group for a shape.
    IGroup group = getRotategroup();
    if (group != null) {
      // this works with sub group
      // group.rotate(degrees, getWidth() / 2, getHeight() / 2);
      group.rotate(
        rdeg, 
        getLeft() - getTransformX() + getWidth() / 2, 
        getTop() - getTransformY() + getHeight() / 2
      );
    }

    IRectangle rect = getBackground();
    if (rect != null) {
      rect.rotate2(
        rdeg, 
        getLeft() - getTransformX() + getWidth() / 2, 
        getTop() - getTransformY() + getHeight() / 2
      );
    }

    IGroup textGroup = getTextGroup();
    if (textGroup != null) {
      textGroup.rotate(rdeg, getWidth() / 2, getHeight() / 2);
    }

    // notify relationships that the diagram is rotated
    dispatchRotate(save, data.getRotateDegrees(), rdeg);

    // store current runtime rotation, this is not yet
    // send to the server.
    this.rotateDegree = rdeg;

    if (save) {
      // save rotation to the data model, to be
      // sent to the server.
      // If saving every time old rotation degree is lost
      // and cannot restore old rotation point position.
      this.data.setRotateDegrees(rdeg);

      // apply e.g. relationship closest path when rotate ends
      // the best would be if there would be no save attribute
      // and there would be rotate-end event.
      AnchorElement.dragEndAnchors(this);
    }
  }

  private void dispatchRotate(
    boolean save,
    Integer oldRotate,
    Integer newRotate
  ) {
    for (AnchorElement a : anchorMap.values()) {
      a.dispatchRotate(save, oldRotate, newRotate, surface);
    }
  }

  @Override
  public Integer getRotate() {
    return this.rotateDegree;
  }

  @Override
  public void setTextAlign(ShapeProperty textAlign) {
    getDiagramItem().setTextAlign(textAlign);
    TextElementFormatUtil formatter = getTextFormatter();
    if (formatter != null) {
      formatter.reapplyText();
    }
  }

  @Override
  public Integer getFontSize() {
    return getDiagramItem().getFontSize();
  }

  @Override
  public void setLineWeight(Integer lineWeight) {
    getDiagramItem().setLineWeight(lineWeight);
  }

  @Override
  public Integer getLineWeight() {
    return getDiagramItem().getLineWeight();
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
    IGroup group = getGroup();
    if (group != null) {
      group.moveToBack();
    }
  }

  @Override
	public void moveToFront() {
    IGroup group = getGroup();
    if (group != null) {
      group.moveToFront();
    }
	}
	
	public final int getLeft() {
		return getRelativeLeft() + getTransformX();
	}
  public int getLeftWithText() {
    return getLeft();
  }
	public final int getTop() {
		return getRelativeTop() + getTransformY();
	}

	public int getWidth() {
		return 0;
	}
  public int getWidthWithText() {
    return getWidth();
  }
	public int getHeight() {
		return 0;
	}
  /**
  * Default implementation is same as height. Most of the elements
  * has text inside of it.
  */
  public int getHeightWithText() {
    return getHeight();
  }

  @Override
  public int getSvgHeightWithText() {
    return getHeightWithText();
  }
	
	@Override
	public int getCenterX() {
		return getLeft() + getWidth() / 2;
	}
	@Override
	public int getCenterY() {
		return getTop() + getHeight() / 2;
	}

  @Override 
  public void setHeight(int height) {
  }

  @Override 
  public void setWidth(int width) {
  }


  // public void setHeightAccordingToText() {
  // }

  @Override
  public void updateTimestamp(Long createdAt, Long updatedAt) {
    IDiagramItem di = getDiagramItem();
    di.setCreatedAt(createdAt);
    di.setUpdatedAt(updatedAt);
  }

  @Override
  public void copyFrom(IDiagramItemRO diagramItem) {
    IDiagramItem current = getDiagramItem();

    // sequence item has wrong format!! => convert first space to comma
    String newshape = diagramItem.getShape();
    boolean shapeChanged = false;
    if (newshape != null && !newshape.equals(current.getShape())) {
      shapeChanged = true;
      int [] shape = DiagramItemHelpers.parseShape(newshape);

      String ns = DiagramItemHelpers.formatShape(shape);

      // fix rotate moves relationships to a wrong place
      current.setShape(ns);
      setShape(shape);
    }

    duplicateFrom(diagramItem, shapeChanged);
  }

  @Override
  public void merge(IDiagramItemRO diagramItem) {
    IDiagramItemRO current = getDiagramItem();
    IDiagramItemRO copy = current.copy();
    copy.merge(diagramItem);
    copyFrom(copy);


    // IDiagramItemRO current = getDiagramItem();

    // // sequence item has wrong format!! => convert first space to comma
    // String newshape = diagramItem.getShape();
    // boolean shapeChanged = false;
    // if (newshape != null && !newshape.equals(current.getShape())) {
    //   shapeChanged = true;
    //   int [] shape = DiagramItemHelpers.parseShape(newshape);
    //   setShape(shape);
    // }

    // IDiagramItemRO copy = current.copy();
    // copy.merge(diagramItem);

    // duplicateFrom(copy, shapeChanged);
  }

  @Override
  public void duplicateFrom(IDiagramItemRO diagramItem) {
    duplicateFrom(diagramItem, true);
  }

	private void duplicateFrom(IDiagramItemRO diagramItem, boolean shapeChanged) {
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
      // setBackgroundColor(bgColor.red, bgColor.green, bgColor.blue, bgColor.opacity);
      setBackgroundColor(bgColor);
    }

    logger.debugTime();
    logger.start("copyFrom 7");
		
		if (connectionHelpers != null && connectionHelpers.isShownFor(this)) {
			connectionHelpers.show(this);
		}

    logger.debugTime();
    logger.start("copyFrom 9");

    boolean newResolved = diagramItem.isResolved();
    if (newResolved != current.isResolved()) {
      // item is shown if it is not resolved
      setVisible(!newResolved);
    }

    // Integer newDord = diagramItem.getDisplayOrder();
    // if ((newDord == null && current.getDisplayOrder() != null) || (newDord != null && !newDord.equals(current.getDisplayOrder()))) {
    //   setDisplayOrder(newDord.intValue());
    // }

    Integer props = diagramItem.getShapeProperties();
    if (props == null) {
      // keep old behaviour of getShapeProperties
      props = 0;
    }

    Integer cprops = current.getShapeProperties();
    if (cprops == null) {
      // keep old behaviour of getShapeProperties
      cprops = 0;
    }

    if (cprops == null && props != null || cprops != null && !cprops.equals(props)) {
      setShapeProperties(props);
    }

    Integer lineWeight = diagramItem.getLineWeight();
    Integer clineWeight = current.getLineWeight();
    if (clineWeight == null && lineWeight != null || clineWeight != null && !clineWeight.equals(lineWeight)) {
      setLineWeight(lineWeight);
    }

    String oldLink = current.getFirstLink();
    String newLink = diagramItem.getFirstLink();
    if (newLink != null && !newLink.equals(oldLink)) {
      setLink(newLink);
    }

    // just copy all fields
    getDiagramItem().copyFrom(diagramItem);

    setFontSize(getDiagramItem().getFontSize());
    rotate(getDiagramItem().getRotateDegrees(), false);

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

  protected void setShapeProperties(Integer shapeProperties) {
    Integer old = getDiagramItem().getShapeProperties();
    getDiagramItem().setShapeProperties(shapeProperties);

    if (isTextAlignChanged(old, shapeProperties)) {
      TextElementFormatUtil formatter = getTextFormatter();
      if (formatter != null) {
        formatter.reapplyText();
      }
    }
  }

  private boolean isTextAlignChanged(Integer oldValue, Integer newValue) {
    boolean oldLeftNewDiffers = 
      ShapeProperty.isTextAlignLeft(oldValue)
        && (ShapeProperty.isTextAlignCenter(newValue) 
          || ShapeProperty.isTextAlignRight(newValue));

    boolean oldCenterNewDiffers =
      ShapeProperty.isTextAlignCenter(oldValue)
        && (ShapeProperty.isTextAlignLeft(newValue) 
          || ShapeProperty.isTextAlignRight(newValue));

    boolean oldRightNewDiffers = 
      ShapeProperty.isTextAlignRight(oldValue)
        && (ShapeProperty.isTextAlignLeft(newValue)
          || ShapeProperty.isTextAlignCenter(newValue));

    return oldLeftNewDiffers || oldCenterNewDiffers || oldRightNewDiffers;
  }

  @Override
  public boolean isTextAlignLeft() {
    return ShapeProperty.isTextAlignLeft(getDiagramItem().getShapeProperties());
  }
  @Override
  public boolean isTextAlignCenter() {
    return ShapeProperty.isTextAlignCenter(getDiagramItem().getShapeProperties());
  }
  @Override
  public boolean isTextAlignRight() {
    return ShapeProperty.isTextAlignRight(getDiagramItem().getShapeProperties());
  }

  // private void setDisplayOrder(int displayOrder) {
  //   surface.applyDisplayOrder(this, displayOrder);
  // }

	public final void setShape(int[] shape) {
    doSetShape(shape);
    resetTransform();

    TextElementFormatUtil textFormatter = getTextFormatter();
    if (textFormatter != null) {
      textFormatter.setTextShape();
    }

    applyLink();
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
	public void restoreHighlighColor(Color color) {
    if (selected && color == null) {
      setHighlightColor(DEFAULT_SELECTION_COLOR);
    } else if (color != null) {
      setHighlightColor(color);
    } else {
      setBorderColor(getBorderColor());
    }
	}

  @Override
  public void setHighlightBackgroundBorder(Color color) {
  }
  @Override
  public void clearHighlightBackgroundBorder() {
  }

  @Override
	public void setHighlightColor(Color color) {
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
    setHighlightColor(color);
    applyThemeBorderColor();
    applyAnnotationColors();
    TextElementFormatUtil textUtil = getTextFormatter();
    if (textUtil != null) {
      textUtil.applyBorderColor(color);
    }
  }

	public void registerOnAttachArea(OnAttachAreaListener listener) {
    onAttachAreaListener = listener;
	}
	
	public void setSizeChangedHandlerByText(SizeChangedHandler handler) {
		this.sizeChangedHandler = handler;
	}
	public void fireSizeChanged() {
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

  @Override
  public void editingEnded(boolean modified) {
    showText();

    // this is not necessary if setShape applies rotate again
    // see GenericElement setShape rotating after textGroup.setTransform(left, top);
    // rotate(getDiagramItem().getRotateDegrees(), false);
  }

  @Override
  public boolean isSequenceElement() {
    return false;
  }

	public String getTextAreaBackgroundColor() {
		if (backgroundColor.opacity == 0) {
			// transparent
			return "transparent";
		}
		return "#" + getBackgroundColor();
	}
	
	// @Override
	// public int getTextAreaLeft() {
	// 	return getLeft() + 4;
	// }
	
	// @Override
	// public int getTextAreaTop() {
	// 	return getTop() + 11;
	// }
	
	// @Override
	// public Promise getTextAreaSize() {
  //   TextElementFormatUtil formatter = getTextFormatter();
  //   if (formatter != null) {
  //     return formatter.getTextSize();
  //   }

  //   int width = getWidth() - 5;
  //   int height = getHeight() - 13;
  //   return Promise.resolve(ElementSize.create(width, height));
	// }

  @Override
  public Promise getTextSize() {
    TextElementFormatUtil formatter = getTextFormatter();
    if (formatter != null) {
      return formatter.getTextSize();
    }

    return Promise.resolve(ElementSize.create(0, 0));
  }
	
	// @Override
	// public int getTextAreaHeight() {
	// 	return getHeight() - 13;
	// }
	
	@Override
	public String getTextAreaAlign() {
    if (ShapeProperty.isTextAlignCenter(getDiagramItem().getShapeProperties())) {
      return "center";
    } else if (ShapeProperty.isTextAlignRight(getDiagramItem().getShapeProperties())) {
      return "right";
    }
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
	
	protected void dispatch(AnchorElement a, int left, int top, int width, int height, int dispachSequence) {
		Integer[] fixedPoints = getFixedAnchorPoints();
		if (a.isFixedPoint() && fixedPoints != null) {
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
		} else {
      AnchorUtils.setRelativePosition(a, left, top, width, height);
    }
		a.dispatch(dispachSequence);
	}

  protected boolean fixedIncludesTransformation() {
    return false;
  }
	
	public Color getBorderColor() {
		return borderColor;
	}
	
  public void setHighlight(boolean highlight) {
  	this.highlightOn = highlight;
    Color color = borderColor;
    if (highlight) {
      color = HIGHLIGHT_COLOR;
    }
    
    if (anchorPoint != null) {
      anchorPoint.setVisibility(highlight);
      anchorPoint.setStroke(color);
      // anchorPoint.setFill(color);
    }

    if (!highlight && isAnnotation()) {
      applyAnnotationColors();
    } else {
      // 26.4.2018 ST: potentially disturbing to highlight
      // whole shape when making connection pre highlight
      // setHighlightColor(color);
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
  	return ContextMenuItem.NO_MENU.getValue() | 
           ContextMenuItem.DUPLICATE.getValue() |
           ContextMenuItem.COLOR_MENU.getValue() |
           ContextMenuItem.URL_LINK.getValue() | 
           ContextMenuItem.LAYERS.getValue() |
           ContextMenuItem.DELETE.getValue();
  }

  @Override
  public boolean supportsMenu(ContextMenuItem menuItem) {
   return (supportedMenuItems() & menuItem.getValue()) == menuItem.getValue();
  }

  @Override
  public boolean supportsModifyToCenter() {
    return true;
  }

  @Override
  public boolean equals(Object obj) {
  	if (obj == this) return true;
    if (obj instanceof AbstractDiagramItem) {
      AbstractDiagramItem a = (AbstractDiagramItem) obj;
      IDiagramItem di = a.getDiagramItem();
      IDiagramItem medi = getDiagramItem();
      if (di != null && medi != null && 
          di.getClientId() != null && di.getClientId().equals(medi.getClientId())) {
        return true;
      }
    }
    return false;
  }

	public void fillInfo(Info info) {
    info.setLeft(getLeft());
    info.setTop(getTop());
    info.setWidth(getWidth());
    info.setHeight(getHeight());

	  // check if all colors equals with current scheme colors
	  // if they do then save color information based on white paper theme
    ElementColorScheme paper = getRefrenceColorScheme();
	  // if (usesSchemeDefaultColors(getCurrentColorScheme())) {
   //    setColorInfo(info, getDefaultBackgroundColor(paper), getDefaultBorderColor(paper), getDefaultTextColor(paper));
	  // } else {
	  //   Color textColor = this.textColor; 
	  //   if (isTextColorAccordingToBackgroundColor()) {
	  //     // if text color is according to background color
	  //     // convert text color for saving/sending to paper color
	  //     // e.g. actor and server elements behave like this, but text element could change
	  //     // text color
	  //     // textColor = getDefaultTextColor(paper);
	  //   }
	  //   setColorInfo(info, backgroundColor, borderColor, textColor);
	  // }

    ElementColorScheme current = getCurrentColorScheme();
    setColorInfo(info, 
                 getBackgroundOrDefaultColor(current, paper),
                 getBorderOrDefaultColor(current, paper), 
                 getTextOrDefaultColor(current, paper));
	}

  private Color getTextOrDefaultColor(ElementColorScheme currentScheme, ElementColorScheme referenceScheme) {
    if (usesSchemeDefaultTextColor(currentScheme)) {
      return getDefaultTextColor(referenceScheme);
    }
    return textColor;
  }
  private Color getBorderOrDefaultColor(ElementColorScheme currentScheme, ElementColorScheme referenceScheme) {
    if (usesSchemeDefaultBorderColor(currentScheme)) {
      return getDefaultBorderColor(referenceScheme);
    }
    return borderColor;
  }
  private Color getBackgroundOrDefaultColor(ElementColorScheme currentScheme, ElementColorScheme referenceScheme) {
    if (usesSchemeDefaultBackgroundColor(currentScheme)) {
      return getDefaultBackgroundColor(referenceScheme);
    }
    return backgroundColor;
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
  @Override
  public boolean usesSchemeDefaultColors(ElementColorScheme colorScheme) {
    if (!this.getDefaultTextColor(colorScheme).equals(this.getTextColor())) {
      return false;
    }
    
    if (!this.getDefaultBorderColor(colorScheme).equals(this.getBorderColor())) {
      return false;
    }
    if (!(this.getDefaultBackgroundColor(colorScheme).equals(this.getBackgroundColorAsColor()))) {
      return false;
    }
    return true;
  }

  @Override
  public boolean usesSchemeDefaultTextColor(ElementColorScheme colorScheme) {
    // check text default color only background opacity is transparent
    // othwerwise if there is a background fill, then text color is set based on background color
    // or set as custom
    if (backgroundColor.opacity == 0 && this.getDefaultTextColor(colorScheme).equals(this.getTextColor())) {
      return true;
    }
    return false; 
  }
  @Override
  public boolean usesSchemeDefaultBorderColor(ElementColorScheme colorScheme) {
    if (this.getDefaultBorderColor(colorScheme).equals(this.getBorderColor())) {
      return true;
    }
    return false;
  }
  @Override
  public boolean usesSchemeDefaultBackgroundColor(ElementColorScheme colorScheme) {
    if (this.getDefaultBackgroundColor(colorScheme).equals(this.getBackgroundColorAsColor())) {
      return true;
    }
    return false;  
  }


	// @Override
	// public Promise getMeasurementAreaSize() {
	// 	return getTextAreaSize();
	// }

  protected void toggleConnectionHelpers() {
    if (connectionHelpers != null) {
      connectionHelpers.toggle(this);
    }
  }
	
	@Override
	public void hideConnectionHelpers() {
    if (connectionHelpers != null) {
      connectionHelpers.hideForce();
    }
	}
	public void applyHelpersShape() {
		if (resizeHelpers != null) {
			resizeHelpers.setShape();
		}
    if (connectionHelpers != null) {
      connectionHelpers.setShape(getLeft(), getTop(), getWidth(), getHeight(), getDiagramItem().getRotateDegrees());
    }

    applyLink();
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
    // if (backgroundColor.opacity == 0) {
      return colorScheme.getTextColor();
    // }
    // return textColor;
	}
	
	@Override
	public boolean isTextElementBackgroundTransparent() {
	  return false;
	}
	
	@Override
	public boolean isTextColorAccordingToBackgroundColor() {
	  return false;
	}

  @Override
  public boolean isMarkdownEditor() {
    boolean result = false;
    TextElementFormatUtil formatter = getTextFormatter();
    if (formatter != null) {
      result = formatter.isMarkdownEditor();
    }
    return result;
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

  @Override 
  public void applyThemeBorderColor() {
    if (Theme.isThemeBorderColor(borderColor)) {
      Color color = ColorHelpers.borderColorByBackground(
        backgroundColor.red, backgroundColor.green, backgroundColor.blue
      );
      setHighlightColor(color);
    }
  }

  public void applyAnnotationColors() {
    if (getDiagramItem() != null && getDiagramItem().isAnnotation() && supportsAnnotationColors()) {
      setHighlightColor(Theme.getCommentThreadColorScheme().getBackgroundColor());
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

  protected void fixCardinality(AnchorUtils.AnchorProperties aprops, Anchor anchor) {
    if (anchor.getAnchorElement() != null) {
      anchor.getAnchorElement().setCardinalDirection(aprops.cardinalDirection);
    }
  }

  public IGroup getGroup() {
    return null;
  }

  public IGroup getRotategroup() {
    return null;
  }

  public IGroup getSubgroup() {
    return null;
  }

	public IGroup getTextGroup() {
		return null;
	}

  public IRectangle getBackground() {
    return null;
  }

  /**
	 * @return the forceTextRendering
	 */
  @Override
	public boolean isForceTextRendering() {
		return forceTextRendering;
	}
	/**
	 * @param forceTextRendering the forceTextRendering to set
	 */
  @Override
	public void setForceTextRendering(boolean forceTextRendering) {
		this.forceTextRendering = forceTextRendering;
	}

}
