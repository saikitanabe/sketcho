package net.sevenscales.editor.api.dojo;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import net.sevenscales.appFrame.impl.EventRegistry;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.utils.Debug;
import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.BoardDimensions;
import net.sevenscales.editor.api.LoadEventListenerCollection;
import net.sevenscales.editor.api.Properties;
import net.sevenscales.editor.api.EditorContext;
import net.sevenscales.editor.api.SurfaceLoadedEventListener;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.event.DiagramElementAddedEvent;
import net.sevenscales.editor.api.event.PotentialOnChangedEvent;
import net.sevenscales.editor.api.impl.SurfaceDiagramSearch;
import net.sevenscales.editor.api.impl.TouchHelpers;
import net.sevenscales.editor.api.Tools;
import net.sevenscales.editor.api.SurfaceDefs;
import net.sevenscales.editor.api.ot.BoardDocumentHelpers;
import net.sevenscales.editor.api.ot.OTBuffer;
import net.sevenscales.editor.api.ot.OperationTransaction;
import net.sevenscales.editor.api.IBirdsEyeView;
import net.sevenscales.editor.content.ui.IModeManager;
import net.sevenscales.editor.content.utils.ScaleHelpers;
import net.sevenscales.editor.content.utils.DiagramHelpers;
import net.sevenscales.editor.content.utils.DiagramDisplaySorter;
import net.sevenscales.editor.diagram.ClickDiagramHandler;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.DiagramDragHandler;
import net.sevenscales.editor.diagram.DiagramResizeHandler;
import net.sevenscales.editor.diagram.DiagramSearch;
import net.sevenscales.editor.diagram.DiagramSelectionHandler;
import net.sevenscales.editor.diagram.KeyEventHandler;
import net.sevenscales.editor.diagram.KeyEventListener;
import net.sevenscales.editor.diagram.MouseDiagramHandler;
import net.sevenscales.editor.diagram.MouseDiagramHandlerManager;
import net.sevenscales.editor.diagram.SelectionHandler;
import net.sevenscales.editor.diagram.utils.DiagramDisplayOrderList;
import net.sevenscales.editor.diagram.ProxyDragHandler;
import net.sevenscales.editor.gfx.base.GraphicsEvent;
import net.sevenscales.editor.gfx.base.GraphicsMouseDownHandler;
import net.sevenscales.editor.gfx.base.GraphicsMouseEnterHandler;
import net.sevenscales.editor.gfx.base.GraphicsMouseLeaveHandler;
import net.sevenscales.editor.gfx.base.GraphicsMouseMoveHandler;
import net.sevenscales.editor.gfx.base.GraphicsMouseUpHandler;
import net.sevenscales.editor.gfx.domain.IGraphics;
import net.sevenscales.editor.gfx.domain.IGroup;
import net.sevenscales.editor.gfx.domain.IKeyEventHandler;
import net.sevenscales.editor.gfx.domain.ILoadObserver;
import net.sevenscales.editor.gfx.domain.IShape;
import net.sevenscales.editor.gfx.domain.IShapeFactory;
import net.sevenscales.editor.gfx.domain.ISurface;
import net.sevenscales.editor.gfx.domain.MatrixPointJS;
import net.sevenscales.editor.diagram.drag.Anchor;
import net.sevenscales.editor.diagram.drag.AnchorElement;
import net.sevenscales.editor.uicomponents.CircleElement;
import net.sevenscales.domain.js.JsShape;
import net.sevenscales.domain.js.JsDimension;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.HasTouchEndHandlers;
import com.google.gwt.event.dom.client.HasTouchMoveHandlers;
import com.google.gwt.event.dom.client.HasTouchStartHandlers;
import com.google.gwt.event.dom.client.HasMouseWheelHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.Window;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;


class SurfaceHandler extends SimplePanel implements 
              ILoadObserver, 
              IKeyEventHandler,
              GraphicsMouseDownHandler,
              GraphicsMouseUpHandler,
              GraphicsMouseMoveHandler,
              GraphicsMouseLeaveHandler,
              GraphicsMouseEnterHandler, 
              HasTouchStartHandlers,
              HasTouchMoveHandlers,
              HasTouchEndHandlers,
              HasMouseWheelHandlers,
              ISurfaceHandler {
  private static final SLogger logger = SLogger.createLogger(SurfaceHandler.class);
    
  private ISurface surface;
  private DiagramDisplayOrderList diagrams;
  protected MouseDiagramHandlerManager mouseDiagramManager;
  private KeyEventHandler keyEventHandler;
  private LoadEventListenerCollection loadEventListenerCollection;
  // private ClickDiagramHandlerCollection clickListenerCollection;
  private boolean editable;
  private boolean deleteSupported;
  private EventRegistry eventRegistry = new EventRegistry();
  private boolean dragEnabled = true;
  private boolean proxyOnDrag;
  private Properties properties;
  private EditorContext editorContext;
  private IModeManager modeManager;
  private boolean disabedOnAreaCheck;
  private double scaleFactor = 1.0f;
  private IGroup connectionLayer3;
  private IGroup interactionLayer4;
  private IGroup elementLayer2;
  private IGroup containerLayer1;
  private IGroup rootLayer0;
  private Integer width;
  private Integer height;
  private int currentClientX;
  private int currentClientY;
  private int currentClientMouseMoveX;
  private int currentClientMouseMoveY;
  private boolean proxyDragAdd;
  private String svgClassName;
  
  // configuration parameter that e.g. library enables for background movement
  private boolean verticalDragOnly;
  private String name = "";
  protected boolean cancelSurfaceClickEvent;
  private OTBuffer otBuffer;
  private OperationTransaction operationTransaction;
  private IBirdsEyeView birdsEyeView;

  // >>>>>>>>> Debugging
  // private net.sevenscales.editor.gfx.domain.ICircle tempCircle;
  // private net.sevenscales.editor.gfx.domain.ICircle tempCircle2;
  // private net.sevenscales.editor.gfx.domain.IRectangle tempRect;
  // <<<<<<<<< Debugging

  public void init(int width, int height, boolean editable, IModeManager modeManager, boolean deleteSupported, 
      EditorContext editorContext, OTBuffer otBuffer, OperationTransaction operationTransaction, IBirdsEyeView birdsEyeView) {
    this.editorContext = editorContext;
    this.modeManager = modeManager;
    this.editable = editable;
    this.deleteSupported = deleteSupported;
    this.otBuffer = otBuffer;
    this.operationTransaction = operationTransaction;
    this.birdsEyeView = birdsEyeView;
    logger.debug("init {}...", name);

    addStyleName("sd-SurfaceHandler");
//    ensureDebugId("surfaceFocusPanel");
//    this.panel = new SimplePanel();
//    panel.ensureDebugId("innerdivInsideFocusPanel");
    
//    panel.setPixelSize(width, height);
    setPixelSize(width, height);

    diagrams = new DiagramDisplayOrderList();

    loadEventListenerCollection = new LoadEventListenerCollection();

    // these are not needed if surface is not editable but to clarify code
    // these are allocated as well and only mouse and key events are not
    // observed
    mouseDiagramManager = new MouseDiagramHandlerManager(this, diagrams, editable, modeManager, birdsEyeView);

    keyEventHandler = new KeyEventHandler(editable);
    
    keyEventHandler.add(mouseDiagramManager.getSelectionHandler());
//    keyEventHandler.add(mouseDiagramManager.getBendHandler());
    
    if (editable) {

//      addStyleName("diagram-panel");
    }

    Event.addNativePreviewHandler(new NativePreviewHandler() {
      @Override
      public void onPreviewNativeEvent(NativePreviewEvent event) {
        NativeEvent ne = event.getNativeEvent();
        switch (event.getTypeInt()) {
          case Event.ONMOUSEMOVE: {
            // store mouse move position separately,
            // not to break any currentclientx and y position logic
            currentClientMouseMoveX = ne.getClientX();
            currentClientMouseMoveY = ne.getClientY();
            break;
          }
          case Event.ONMOUSEDOWN:
          case Event.ONMOUSEUP: {
            // store mouse up event location before it happens
            // on surface, then possible to pass this location
            // together with OTs.
            // mouse up location through surface is the location of mouse down
            // at least when dragging an element, most probably element prevents
            // getting mouse up location
            currentClientX = ne.getClientX();
            currentClientY = ne.getClientY();
            break;
          }
        }
      }
    });

    
    // FocusPanel support (key events handled through browser)
//    getElement().setTabIndex(0);
//    addDomHandler(new KeyDownHandler() {
//      @Override
//      public void onKeyDown(KeyDownEvent event) {
//        System.out.println("surf down");
//      }
//    }, KeyDownEvent.getType());
    
//    addFocusHandler(new FocusHandler() {
//      @Override
//      public void onFocus(FocusEvent event) {
//        System.out.println("Focus");
//      }
//    });
//    addKeyDownHandler(new KeyDownHandler() {
//      public void onKeyDown(KeyDownEvent event) {
//        keyEventHandler.onKeyDown(event);
//        DOM.eventPreventDefault(Event.as(event.getNativeEvent()));
//      }
//    });
//    addKeyPressHandler(new KeyPressHandler() {
//      public void onKeyPress(KeyPressEvent event) {
//        System.out.println("juug");
//        keyEventHandler.onKeyPress(event);
//      }
//    });
//    addKeyUpHandler(new KeyUpHandler() {
//      public void onKeyUp(KeyUpEvent event) {
//        keyEventHandler.onKeyUp(event);
//      }
//    });
    
//    addDomHandler(new MouseDownHandler() {
//      @Override
//      public void onMouseDown(MouseDownEvent event) {
//        if (properties != null) {
//          properties.setFocus(true);
//        }
//        Document.get().getElementById("surfaceFocusPanel").focus();
//      }
//    }, MouseDownEvent.getType());
    
//    add(panel);
//    setWidget(panel);
    logger.debug("init {}... done", name);
  }

  @Override
  public IBirdsEyeView getBirdsEyeView() {
    return birdsEyeView;
  }

  @Override
  public void setId(String id) {
    getElement().setId(id);
  }
  
//  public void addDoubleClickHandler(DoubleClickHandler handler) {
//    // TODO: hack
//    this.handler = handler;
//  }
  
  @Override
  protected void onLoad() {
    logger.debug("onLoad {}...", name);
    if (surface == null) {
      surface = IShapeFactory.Util.factory(editable).createSurface();
  // new Surface();
//      surface.init(this.panel);
      surface.init(this, this);
      surface.setAttribute("id", name);
      surface.setAttribute("class", svgClassName);
      mouseDiagramManager.reset();
      surface.load();
      logger.debug("onLoad {}... done", name);
    }
  }

  public void hide() {
    getRootLayer().setVisible(false);
  }

  public void show() {
    getRootLayer().setVisible(true);
  }

  public void makeDraggable(Diagram diagram) {
    if (dragEnabled) {
      mouseDiagramManager.makeDraggable(diagram);
    }
  }
  
  public void makeBendable(Diagram diagram) {
    mouseDiagramManager.makeBendable(diagram);
  }

  public void addDragHandler(DiagramDragHandler handler) {
    mouseDiagramManager.addDragHandler(handler);
  }

  public void addSelectionListener(DiagramSelectionHandler handler) {
    mouseDiagramManager.addSelectionListener(handler);
  }

  public void addResizeHandler(DiagramResizeHandler resizeHandler) {
    mouseDiagramManager.addResizeHandler(resizeHandler);
  }

  @Override
  public HandlerRegistration addMouseWheelHandler(MouseWheelHandler handler) {
    return addDomHandler(handler, MouseWheelEvent.getType());
  }
  
  public void add(Diagram diagram, boolean ownerComponent) {
    add(diagram, ownerComponent, false);
  }
  
  public void add(Diagram diagram, boolean ownerComponent, boolean duplicate) {
    List<Diagram> diagrams = new ArrayList<Diagram>();
    diagrams.add(diagram);
    add(diagrams, ownerComponent, duplicate);
  }
  
  public void add(List<Diagram> toAddDiagrams, boolean ownerComponent, boolean duplicate) {
    if (ownerComponent) {
      // add logically only owner components
      // rest of added items are part of owner components (composite).
      diagrams.addAll(toAddDiagrams);
    }
    
    // currently all owner diagram items are selectable and double clickable
    for (Diagram diagram : toAddDiagrams) {
      // TODO set annotation on for the element if comment mode is on
//      if (!diagrams.contains(diagram.getOwnerComponent())) {
//        diagrams.add(diagram.getOwnerComponent());
//      }
      diagram.addMouseDiagramHandler(mouseDiagramManager);
      diagram.accept(this);
    }
    
    if (editorContext.isTrue(EditorProperty.ON_CHANGE_ENABLED)) {
      editorContext.getEventBus().fireEvent(new DiagramElementAddedEvent(toAddDiagrams, duplicate));
      trigger("shape-added");
    }
  }

  private native void trigger(String event)/*-{
    $wnd.$($doc).trigger(event);
  }-*/;

  public void addAsSelected(Diagram diagram, boolean ownerComponent, boolean duplicate) {
    List<Diagram> diagrams = new ArrayList<Diagram>();
    diagrams.add(diagram);
    addAsSelected(diagrams, ownerComponent, duplicate);
  }
  
  public void addAsSelected(Diagram diagram, boolean ownerComponent) {
    addAsSelected(diagram, ownerComponent, false);
  }
  
  public void addAsSelected(List<Diagram> diagrams, boolean ownerComponent, boolean duplicate) {
//    for (Diagram diagram : diagrams) {
    add(diagrams, ownerComponent, duplicate);
//    }
    mouseDiagramManager.select(diagrams);
  }

  public int getCurrentClientX() {
    return currentClientX;
  }

  public int getCurrentClientY() {
    return currentClientY;
  }

  public int getCurrentClientMouseMoveX() {
    return currentClientMouseMoveX;
  }

  public int getCurrentClientMouseMoveY() {
    return currentClientMouseMoveY;
  }
  
  public void removeAll() {
    for (Diagram diagram : diagrams) {
      remove(diagram);
    }
//    diagrams.clear();
  }
  
  public void remove(Diagram diagram) {
    diagrams.remove(diagram);
    mouseDiagramManager.diagramRemoved(diagram);
    IGroup group = diagram.getGroup(); 
    if (group != null) {
      _removeShape(group.getContainer());
    } else {
      for (IShape e :  diagram.getElements()) {
        remove(surface.getContainer(), e.getRawNode());
      }
    }
  }
  
  private native void _removeShape(JavaScriptObject shape)/*-{
    // removes shape from its parent
    shape.removeShape();
  }-*/;
  
  public void remove(JavaScriptObject element) {
    remove(surface.getContainer(), element);
  }
  
  public AnchorElement getAttachElement(Anchor anchor, int x, int y) {
    // logger.start("getAttachElement");
    AnchorElement result = null;
    // need to loop all diagrams through to correctly unhighlight
    // items on overlapping border case
    // NOTE! this could be performance problem
//    for (Diagram d : diagrams) {
    for (int i = diagrams.size() - 1; i >= 0; --i) {
      Diagram d = diagrams.get(i);
      if (Tools.filterDiagramByCurrentTool(d) && d.isVisible()) {
        // if element is not visible do not show anchor point
        // e.g. comment thread is only hidden if resolved.

  //      if (result == null) {
        AnchorElement candidate = d.onAttachArea(anchor, x, y);
        if (candidate != null && result == null) {
          result = candidate;
        } else {
          d.setHighlight(false);
        }
      }
    }

    // logger.debugTime();
    return result;
//      AnchorElement result = d.onAttachArea(anchor, x, y);
//      if (result != null) {
//        return result;
//      }
//    }
//    return null;
  }
  
  public void clear() {
    // TODO use native clear and clear array of items
//    for (Diagram diagram : diagrams) {
//      remove(diagram);
//    }
    clear(surface.getContainer());
    diagrams.clear();
  }
  private native void clear(JavaScriptObject surface)/*-{
    surface.clear();
//    surface.canvas.children.clear();
  }-*/;

  private native void remove(JavaScriptObject surface, JavaScriptObject diagram)/*-{
    surface.remove(diagram);
  }-*/;

////////////////////////////////////////////////////////////
//  private void handleMouseDown(int x, int y) {
//    mouseDiagramManager.onMouseDown(null, x, y);
//  }

//  private void handleMouseUp(int x, int y) {
//    mouseDiagramManager.onMouseUp(null, x, y);
//  }
//
//  private void handleMouseMove(int x, int y) {
//    mouseDiagramManager.onMouseMove(null, x, y);
//  }
  
  public void handleKeyDown(int keyCode, int platformCode, boolean shift, boolean ctrl) {
//    System.out.println(DOM.eventGetCurrentEvent() + "keyCode:" + keyCode + "platCode;" + platformCode + " shift:" + shift + " ctrl:" + ctrl);
//    Debug.print("d" + keyCode);    
    keyEventHandler.onKeyEventDown(keyCode, shift, ctrl);
//      triggerEvent(getElement(), platformCode, "onkeydown", false, "UIEvents");
  }

  public void handleKeyUp(int keyCode, boolean shift, boolean ctrl) {
//    System.out.println("up:" + keyCode + " shift:" + shift + " ctrl:" + ctrl);
//  Debug.print("u" + keyCode);    
    keyEventHandler.onKeyEventUp(keyCode, shift, ctrl);
  }

  private void handleCharUp(int ch) {
    System.out.println(Character.valueOf( (char) ch));
//   System.out.println("keyCode:" + keyCode + " shift:" + shift + " ctrl:" + ctrl);
  }

  public ISurface getSurface() {
    return surface;
  }
  
  public IGroup getElementLayer() {
    return elementLayer2;
  }
  
  public IGroup getConnectionLayer() {
    return connectionLayer3;
  }
  
  public IGroup getContainerLayer() {
    return containerLayer1;
  }
  
  public IGroup getRootLayer() {
    return rootLayer0;
  }
  
  public IGroup getInteractionLayer() {
    return interactionLayer4;
  }

  public void setTransform(int tx, int ty) {
    getRootLayer().setTransform(tx, ty);
    _notifyTrasform(rootLayer0.getContainer());
  }

  public void setTransform(double tx, double ty) {
    getRootLayer().setTransform(tx, ty);
    _notifyTrasform(rootLayer0.getContainer());
  }

  private native void _notifyTrasform(JavaScriptObject group)/*-{
    if (typeof $wnd.globalStreams !== 'undefined') {
      $wnd.globalStreams.backgroundMoveStream.push({
        type: 'move',
        matrix: group.getTransform()
      })
    }
  }-*/;

  public void addLoadEventListener(SurfaceLoadedEventListener listener) {
    if (!loadEventListenerCollection.contains(listener)) {
      loadEventListenerCollection.add(listener);

//      if (surface != null && surface.isLoaded()) {
//        listener.onLoaded();
//      }
    } 
    
    // check is it possible to call right away 
    if (surface != null) {
      listener.onLoaded();
    }
  }

  public List<Diagram> getDiagrams() {
    return diagrams;
  }
  
  public List<Diagram> getVisualItems() {
    List<Diagram> result = new ArrayList<Diagram>();
    for (Diagram d : getDiagrams()) {
      if (!(d instanceof CircleElement)) {
        result.add(d);
      }
    }
    return result;
  }

  public JsDimension getDimension(JsArrayString shapeIds) {
    DiagramSearch search = createDiagramSearch();
    List<Diagram> selected = new ArrayList<Diagram>();
    for (int i = 0; i < shapeIds.length(); ++i) {
      String shapeId = shapeIds.get(i);
      Diagram d = search.findByClientId(shapeId);
      if (d != null) {
        selected.add(d);
      }
    }

    if (selected.size() > 0) {
      // could be deleted already
      BoardDimensions.resolveDimensions(selected);

      int left = BoardDimensions.getLeftmost();
      int top = BoardDimensions.getTopmost();
      int width = BoardDimensions.getWidth();
      int height = BoardDimensions.getHeight();

      return JsDimension.create(left, top, width, height);
    }
    return null;
  }

  public void addKeyEventHandler(KeyEventListener keyEventHandler) {
    this.keyEventHandler.add(keyEventHandler);
  }
  
  public void reset() {
    for (int i = diagrams.size() - 1; i >= 0; --i) {
      remove(diagrams.get(i));
    }
    
    mouseDiagramManager.reset();
    diagrams.clear();
  }

  public int count() {
    return count(surface.getContainer());
  }
  
  private native int count(JavaScriptObject surface)/*-{
    return surface.canvas.children.count;
  }-*/;
  
  public int scaleClientX(int clientX) {
     return ScaleHelpers.scaleValue(clientX, getScaleFactor()) - getRootLayer().getTransformX();
  }

  public int scaleClientY(int clientY) {
     return ScaleHelpers.scaleValue(clientY, getScaleFactor()) - getRootLayer().getTransformY();
  }

  public void onDoubleClick(IGraphics graphics, Event event) {
    // TODO Auto-generated method stub
    
  }
  
  public void onMouseDown(GraphicsEvent event, int keys) {
//    int x = event.getElementOffsetX(getElement()) + rootLayer.getTransformX();
//    int y = event.getElementOffsetY(getElement()) + rootLayer.getTransformY();
    int x = event.getElementOffsetX(getElement());
    int y = event.getElementOffsetY(getElement());
    
//    MatrixPointJS point = MatrixPointJS.createScaledPoint(x, y, scaleFactor);
    
    if (!getEditorContext().isTrue(EditorProperty.SKETCHO_BOARD_MODE)) {
      // HACK! on library need to add absolute position...
      // only enabled on Sketcho Confluence
      x = verticalDragOnly ? x + getAbsoluteLeft() : x;
      y = verticalDragOnly ? y + getAbsoluteTop() : y;
    }
    
    MatrixPointJS point = MatrixPointJS.createScaledPoint(x, y, getScaleFactor());
//    int x = event.getElementOffsetX(getElement());
//    int y = event.getElementOffsetY(getElement());
    mouseDiagramManager.onMouseDown(null, point, keys);
    
//    if (properties != null) {
//      properties.setFocus(true);
//    }
  }
  
  public void onMouseEnter(GraphicsEvent event) {
//    int x = event.getElementOffsetX(getElement()) + rootLayer.getTransformX();
//    int y = event.getElementOffsetY(getElement()) + rootLayer.getTransformY();
//    MatrixPointJS point = MatrixPointJS.createScaledPoint(x, y, scaleFactor);
//    mouseDiagramManager.onMouseEnter(null, point);
  }
  
  public void onMouseMove(GraphicsEvent event) {
//    System.out.println("event.getElementOffsetX(getElement()):" + event.getElementOffsetX(getElement()) + 
//        " rootLayer.getTransformX: " + rootLayer.getTransformX());
//    int x = event.getElementOffsetX(getElement()) + rootLayer.getTransformX();
//    int y = event.getElementOffsetY(getElement()) + rootLayer.getTransformY();
//    MatrixPointJS point = MatrixPointJS.createScaledPoint(x, y, scaleFactor);
//    mouseDiagramManager.onMouseMove(null, point);
  }
  
  public void onMouseLeave(GraphicsEvent event) {
//    int x = event.getElementOffsetX(getElement()) + rootLayer.getTransformX();
//    int y = event.getElementOffsetY(getElement()) + rootLayer.getTransformY();
//    MatrixPointJS point = MatrixPointJS.createScaledPoint(x, y, scaleFactor);
//    mouseDiagramManager.onMouseLeave(null, point);
  }
  
  @Override
  public void onMouseUp(GraphicsEvent event, int keys) {
//    int x = event.getElementOffsetX(getElement()) - rootLayer.getTransformX();
//    int y = event.getElementOffsetY(getElement()) - rootLayer.getTransformY();
//    MatrixPointJS point = MatrixPointJS.createScaledPoint(x, y, scaleFactor);
    int x = event.getElementOffsetX(getElement());
    int y = event.getElementOffsetY(getElement());
    MatrixPointJS point = MatrixPointJS.createScaledPoint(x, y, getScaleFactor());
    mouseDiagramManager.onMouseUp(null, point, keys);
    
    // set focus to get key events
//    Document.get().getElementById("surfaceFocusPanel").focus();
  }
  
  // HACK: ModelingPanel needs to pass up event on safari and firefox
  // if mouse down event is initiated in different silverlight surface
  public void onMouseUp(MouseUpEvent event) {
//    int x = event.getElementOffsetX(getElement());
//    int y = event.getElementOffsetY(getElement());
//    mouseDiagramManager.onMouseUp(null, null);
  }
  
//  public void fireClickEvent(ClickEvent event) {
//    if (getSelectionHandler().getSelectedItems().size() == 0 && !cancelSurfaceClickEvent) {
//      // empty board area was clicked
//      editorContext.getEventBus().fireEvent(new BoardEmptyAreaClickedEvent(event));
//    } // else item was clicked
//    
//    cancelSurfaceClickEvent = false;
//  }
  
  public void fireLongPress(int x, int y) {
    mouseDiagramManager.fireLongPress(x, y);
  }

  public void loaded() {
    logger.debug("loaded {}...", name);
    // layers in order
    rootLayer0 = IShapeFactory.Util.factory(editable).createGroup(surface);
    containerLayer1 = IShapeFactory.Util.factory(editable).createGroup(rootLayer0);
    elementLayer2 = IShapeFactory.Util.factory(editable).createGroup(rootLayer0);
    connectionLayer3 = IShapeFactory.Util.factory(editable).createGroup(rootLayer0);
    interactionLayer4 = IShapeFactory.Util.factory(editable).createGroup(rootLayer0);

    // >>>>>>>> Debug 
    // tempCircle = IShapeFactory.Util.factory(editable).createCircle(rootLayer0);
    // tempCircle.setShape(0, 0, 10);
    // tempCircle.setStroke(218, 57, 57, 1);
    // tempCircle.setFill(218, 57, 57, 1);

    // tempCircle2 = IShapeFactory.Util.factory(editable).createCircle(rootLayer0);
    // tempCircle2.setShape(0, 0, 10);
    // tempCircle2.setStroke(100, 57, 57, 1);
    // tempCircle2.setFill(100, 57, 57, 1);

    // tempRect = IShapeFactory.Util.factory(editable).createRectangle(rootLayer0);
    // tempRect.setStroke(218, 57, 57, 1);
    // tempRect.setStrokeWidth(4);
    // rectify(rootLayer0.getContainer(), tempRect, tempCircle, this);
    // <<<<<<<< Debug END


//    surface.addGraphicsMouseDownHandler(this);
//    surface.addGraphicsMouseUpHandler(this);
//    surface.addGraphicsMouseMoveHandler(this);
//    surface.addGraphicsMouseLeaveHandler(this);
//    surface.addGraphicsMouseEnterHandler(this);
    
//    surface.addGraphicsKeyDownHandler(new )
    
//    surface.addGraphicsDoubleClickHandler(new GraphicsDoubleClickHandler() {
//      public void onDoubleClick(Event event, int keys) {
//        System.out.println("DOUBLE CLICK");
//      }
//    });
//    surface.addGraphicsDoubleClickHandler(new GraphicsDoubleClickHandler() {
//      public void onDoubleClick(Event event, int keys) {
//        System.out.println("DOUBLE CLICK");
//      }
//    });
    
    // THIS is needed on silverlight implementation
    // when keyevents are coming directly from surface
//    surface.setKeyEventHandler(this);
    
    // this fixes bug not to have border
    setSize(getOffsetWidth(), getOffsetHeight());
    
    diagrams.clear();
    loadEventListenerCollection.fireLoadedEvent();
    Debug.log("SurfaceHandler::loaded (callback)");
    logger.debug("loaded {}... done", name);
  }

  @Override
  public void setVisible(boolean visible) {
    super.setVisible(visible);
//     if (surface != null && visible && isAttached())
//        onLoad();
  }

  private native void rectify(JavaScriptObject element, net.sevenscales.editor.gfx.domain.IRectangle rect, net.sevenscales.editor.gfx.domain.ICircle circle, SurfaceHandler me)/*-{
      // element.rawNode.setAttribute("fill", "brown")
      // element.rawNode.setAttribute("fill", "url(#img1)")


      var timer = null

      function rectify() {
        var t = element.getTransform()
        var width = @com.google.gwt.user.client.Window::getClientWidth()()
        var height = @com.google.gwt.user.client.Window::getClientHeight()()
        var value = me.@net.sevenscales.editor.api.dojo.SurfaceHandler::getScaleFactor()()

        timer = timer || setTimeout(function() {
          if (t != null) {
            var left = t.dx / value
            var top = t.dy / value
            rect.@net.sevenscales.editor.gfx.domain.IRectangle::setShape(IIIII)((-left), (-top), width/value, height/value, 0)
            circle.@net.sevenscales.editor.gfx.domain.ICircle::setShape(III)(-left + width / value / 2, -top + height / value / 2, 10)
            circle.@net.sevenscales.editor.gfx.domain.ICircle::setFill(IIID)(100, 100, 57, 1);
          }
          timer = null
          rectify()
        }, 500)
      }

      rectify()

  }-*/;
  
  public void setSize(Integer width, Integer height) {
    // for convinience; not needed to parse from string
    this.width = width;
    this.height = height;
    setPixelSize(width, height);
    if (surface != null) {
      surface.setSize(width, height);
    }
  }

  public void addMouseDiagramHandler(MouseDiagramHandler mouseDiagramHandler) {
    mouseDiagramManager.addMouseDiagramHandler(mouseDiagramHandler);
  }

  @Override
  public void addProxyDragHandler(ProxyDragHandler proxyDragHandler) {
    mouseDiagramManager.addProxyDragHandler(proxyDragHandler);
  }
  
  public SelectionHandler getSelectionHandler() {
    return mouseDiagramManager.getSelectionHandler();
  }
  
  public void setBackground(String color) {
    surface.setBackground(color);
  }
  
//  public int getOffsetX(Event event) {
//    return SurfaceUtil.eventGetElementOffsetX(getElement(), event);
//  }
//  
//  public int getOffsetY(Event event) {
//    return SurfaceUtil.eventGetElementOffsetY(getElement(), event);
//  }

  public EventRegistry getEventRegistry() {
    return eventRegistry;
  }
  
  public MouseDiagramHandlerManager getMouseDiagramManager() {
    return mouseDiagramManager;
  }

  public void suspendRedraw() {
    surface.suspendRedraw();
    rootLayer0.setVisible(false);
  }

  public void unsuspendRedrawAll() {
    surface.unsuspendRedrawAll();
    rootLayer0.setVisible(true);
  }

  public void setDragEnabled(boolean enableDragging) {
    this.dragEnabled = enableDragging;
  }
  public boolean isDragEnabled() {
    return dragEnabled;
  }

  public void setProxyOnDrag(boolean proxyOnDrag) {
    this.proxyOnDrag = proxyOnDrag;
  }
  public boolean isProxyOnDrag() {
    return proxyOnDrag;
  }

  public void addAsDragging(Diagram diagram, boolean ownerComponent, MatrixPointJS point, int keys) {
    proxyDragAdd = true;
    add(diagram, ownerComponent);
    mouseDiagramManager.select(diagram);
    mouseDiagramManager.getDragHandler().onMouseDown(diagram, point, keys);
    proxyDragAdd = false;
//    mouseDiagramManager.onMouseMove(null, x-10, y);
  }

  public boolean isProxyDragAdding() {
    return proxyDragAdd;
  }
  
  public void fireMouseDown(MouseDownEvent event) {
  }

  public void fireMouseOnEnter(MouseMoveEvent event) {
  }

  public void fireMouseMove(MouseMoveEvent event, boolean toolbar) {
  }
  
  public void fireMouseUp(MouseUpEvent event) {
    
  }

  public void setPropertiesTextArea(Properties properties) {
    this.properties = properties;
  }

  public boolean isDeleteSupported() {
    return deleteSupported;
  }
  
  public EditorContext getEditorContext() {
    return editorContext;
  }
  
  public IModeManager getModeManager() {
    return modeManager;
  }
  
  public void fireMouseOnLeave(MouseMoveEvent event) {
    
  }

  @Override
  public void moveToBack() {
    surface.moveToBack(); 
  }

  @Override
  public void moveSelectedToBack() {
    int dorder = 0;
    Diagram top = firstOnElementsLayer();
    if (top != null) {
      Integer topDisplayOrder = top.getDiagramItem().getDisplayOrder();
      dorder = (topDisplayOrder != null ? topDisplayOrder : 0) - 1;
    }

    List<Diagram> selectedInOrder = DiagramHelpers.diagramsInDisplayOrder(getSelectionHandler().getSelectedItems());
    for (Diagram d : selectedInOrder) {
      d.moveToBack();
      d.getDiagramItem().setDisplayOrder(dorder);
    }

    sort();
    notifySelectedChanged();
  }

  private Diagram firstOnElementsLayer() {
    for (Diagram d : diagrams) {
      if (d.getGroup().getLayer() == getElementLayer()) {
        return d;
      }
    }
    return null;
  }

  private Diagram lastOnElementsLayer() {
    int i = diagrams.size();
    while (--i >= 0) {
      Diagram d = diagrams.get(i);
      if (d.getGroup().getLayer() == getElementLayer()) {
        return d;
      }
    }
    return null;
  }

  private void sort() {
    Collections.sort(diagrams, DiagramDisplaySorter.createDiagramComparator());
  }

  @Override
  public void moveSelectedToFront() {
    int dorder = 0;
    Diagram last = lastOnElementsLayer();
    if (last != null) {
      Integer topDisplayOrder = last.getDiagramItem().getDisplayOrder();
      dorder = (topDisplayOrder != null ? topDisplayOrder : 0) + 1;
    }

    List<Diagram> selectedInOrder = DiagramHelpers.diagramsInDisplayOrder(getSelectionHandler().getSelectedItems());
    for (Diagram d : selectedInOrder) {
      d.moveToFront();
      d.getDiagramItem().setDisplayOrder(dorder);
    }

    sort();
    notifySelectedChanged();
  }

  @Override
  public void moveSelectedToBackward() {
    sortSelectedWithDirection(-1);
    applyDisplayOrderForSelection();
    notifySelectedChanged();
  }

  @Override
  public void moveSelectedToForward() {
    sortSelectedWithDirection(1);
    applyDisplayOrderForSelection();
    notifySelectedChanged();
  }

  private void sortSelectedWithDirection(int dir) {
    List<Diagram> selectedInOrder = DiagramHelpers.diagramsInDisplayOrder(getSelectionHandler().getSelectedItems());
    for (Diagram d : selectedInOrder) {
      Integer cord = d.getDiagramItem().getDisplayOrder();
      int dord = cord != null ? cord.intValue() + dir : dir;
      // applyDisplayOrder(d, dord);
      d.getDiagramItem().setDisplayOrder(dord);
    }

    sort();
  }

  private void applyDisplayOrderForSelection() {
    List<Diagram> elementLayer = new DiagramDisplayOrderList();
    for (Diagram d : diagrams) {
      if (d.getGroup().getLayer() == getElementLayer()) {
        elementLayer.add(d);
      }
    }
    int i = elementLayer.size();
    while (--i >= 0) {
      if (i - 1 >= 0) {
        Diagram d = elementLayer.get(i - 1);
        d.getGroup().insertBefore(elementLayer.get(i).getGroup());
      }
    }
  }

  @Override
  public void applyDisplayOrders(List<? extends IDiagramItemRO> items) {
    applyDisplayOrderForSelection();
  }

  private boolean onSameLayer(Diagram d1, Diagram d2) {
    IGroup g1 = d1.getGroup();
    IGroup g2 = d2.getGroup();
    if (g1 != null && g2 != null && g1.getLayer() != null && g1.getLayer() == g2.getLayer()) {
      // comparing layer reference addresses
      return true;
    }
    return false;
  }

  private void insertBefore(Diagram diagram1, Diagram diagram2) {
    diagram1.getGroup().insertBefore(diagram2.getGroup());
  }

  private void notifySelectedChanged() {
    boolean atLeastOneChanged = getSelectionHandler().getSelectedItems().size() > 0;
    if (atLeastOneChanged) {
      // notify and save changes
      getEditorContext().getEventBus().fireEvent(new PotentialOnChangedEvent(getSelectionHandler().getSelectedItems()));
    }
  }

  public boolean isEmpty() {
    return diagrams.size() == 0;
  }

//  @Override
//  public void onDoubleClick(DoubleClickEvent event) {
//    mouseDiagramManager.onDoubleClick(event);
//  }
  
  public void scale() {
    scale(scaleFactor, false);
  }
  
  public void invertScale() {
    if (scaleFactor != 1.0) {
      invertScale(rootLayer0.getContainer(), scaleFactor);
    }

    this.scaleFactor = 1.0;
  }
  
  @Override
  public void scale(double value, boolean wheel) {
    if (wheel) {
      // wheel zoom zooms to mouse point
      scaleAtPoint(value, currentClientMouseMoveX, currentClientMouseMoveY);
    } else {
      // NOTE
      // - e.g. touch device should calculate center point between fingers
      // - clicking slider or using short cuts zooms to center
      scaleAtCenter(
       rootLayer0.getContainer(),
       scaleFactor,
       value,
       com.google.gwt.user.client.Window.getClientWidth(),
       com.google.gwt.user.client.Window.getClientHeight()
      );
    }

    scaleBackground(value);
    this.scaleFactor = value;

    if (!isLibrary()) {
      _notifyScale(value);
    }
  }

  private void scaleAtPoint(double value, double px, double py) {
    if (value != 1 && value == getScaleFactor()) {
      return;
    }

    // first make calculations with previous (current) scale
    double prevScale = getScaleFactor();

    // NOTE: transition is always with scale(1)
    double tx0 = rootLayer0.getTransformDoubleX();
    double ty0 = rootLayer0.getTransformDoubleY();

    // calculate mouse position in prev zoom coordinate system
    double mousePosX = px / prevScale - tx0 / prevScale;
    double mousePosY = py / prevScale - ty0 / prevScale;

    // net.sevenscales.domain.utils.Debug.logString("scaleAtPoint...", 
    //   "scale", value,
    //   "mx", px,
    //   "my", py,
    //   "tx0", tx0,
    //   "ty0", ty0,
    //   "prevScale", prevScale,
    //   "mousePosX", mousePosX,
    //   "mousePosY", mousePosY);

    // net.sevenscales.domain.utils.Debug.debugBox(
    //   px,
    //   py);

    // tempCircle.setShape((int) mousePosX, (int) mousePosY, 10);

    // these are the steps that _scaleAt does at once using matrix multiply
    // rootLayer0.resetAllTransforms();
    // rootLayer0.translate(mousePosX, mousePosY);
    // rootLayer0.scale(value, value);
    // rootLayer0.translate(-mousePosX, -mousePosY);

    rootLayer0.scaleAt(value, mousePosX, mousePosY);

    // NOTE: transition is always with scale(1)
    double tx = rootLayer0.getTransformDoubleX();
    double ty = rootLayer0.getTransformDoubleY();

    // what is the mouse point with new zoom value
    double mousePosX2 = px / value - tx / value;
    double mousePosY2 = py / value - ty / value;

    // tempCircle2.setShape((int) mousePosX2, (int) mousePosY2, 5);

    // mouse point diff is on scale 1 => scale with current zoom value
    double diffx = (mousePosX2 - mousePosX) * value;
    double diffy = (mousePosY2 - mousePosY) * value;

    // to notify transform as well
    this.setTransform(tx + diffx, ty + diffy);
  }

  private native void _notifyScale(double value)/*-{
    $wnd.globalStreams.boardScaledStream.push(value);
  }-*/;

  private native void scaleBackground(double value)/*-{
    // 399
    var size = 141.73 * value
    var sizeValue = size + "px " + size + "px"
    $wnd.$('#sketchboard-editor').css("background-size", sizeValue)
  }-*/;
  
  public void invertScaleDiagram(Diagram diagram, int x, int y) {
    // TODO need to reposition to keep diagram in same place
    // invert also point
    invertScaleDiagram(diagram, x, y, scaleFactor);
//    scaleDiagram(diagram, 1 + 1 - scaleFactor, x, y);
  }
  
  private void invertScaleDiagram(Diagram diagram, int x, int y, double value) {
    IGroup group = diagram.getGroup();
    if (group != null) {
      // supports group
      invertScaleDiagram(group.getContainer(), x, y, value);
    }
//    } else {
//      for (IShape e :  diagram.getElements()) {
//        invertScale(e.getRawNode(), value);
//      }
//    }
  }
  
  public void scaleDiagram(Diagram diagram) {
    scaleDiagram(diagram, scaleFactor, 0, 0);
  }
  
  private void scaleDiagram(Diagram diagram, double value, int x, int y) {
    IGroup group = diagram.getGroup();
    if (group != null) {
      // supports group
      scaleDiagram(group.getContainer(), value, x, y);
    }
  }

  private native void scaleAtCenter(JavaScriptObject element, double prevScaleFactor, double value, int width, int height/*, net.sevenscales.editor.gfx.domain.ICircle circle, net.sevenscales.editor.gfx.domain.IRectangle rect*/)/*-{
    var m3 = new $wnd.dojox.gfx.Matrix2D(value)
    var t = element.getTransform()
    if (t != null) {
      // calculate visible area center point that is visible on screen
      // calculation is done with current scale factor (previous)
      var left = t.dx / prevScaleFactor
      var top = t.dy / prevScaleFactor
      var cx = -left + width / 2 / prevScaleFactor
      var cy = -top + height / 2 / prevScaleFactor

      // >>>>>>> debug center point
   //    rect.@net.sevenscales.editor.gfx.domain.IRectangle::setShape(IIIII)((-left), (-top), width / prevScaleFactor, height / prevScaleFactor, 0)
   //    circle.@net.sevenscales.editor.gfx.domain.ICircle::setFill(IIID)(218, 57, 57, 1)
      // circle.@net.sevenscales.editor.gfx.domain.ICircle::setShape(III)(cx, cy, 10)
      // <<<<<<< debug center point

      // scale at center point to zoom it
      element.setTransform($wnd.dojox.gfx.matrix.scaleAt(value, value, cx, cy))

      $wnd.globalStreams.scaleAtStream.push()

      // // translate to center point to be visible area center again
      t = element.getTransform()
      t.dx = t.dx - cx + width / 2
      t.dy = t.dy - cy + height / 2


      element.setTransform(t)
      $wnd.globalStreams.surfaceTransformStream.push()
    } else {
      element.setTransform($wnd.dojox.gfx.matrix.scale({ x: m3.xx, y: m3.yy}))
    }
  }-*/;

  private native void scaleDiagram(JavaScriptObject element, double value, int x, int y)/*-{
    var m3 = new $wnd.dojox.gfx.Matrix2D(value);
    var m = $wnd.dojox.gfx.matrix;
    var s = m.scale({ x: m3.xx, y: m3.yy});
    s.dx = 0;
    s.dy = 0;
    element.applyTransform(s);
  }-*/;
  
  public static native JavaScriptObject invertScale(JavaScriptObject element, double value)/*-{
    var m3 = new $wnd.dojox.gfx.Matrix2D(value);
    var m = $wnd.dojox.gfx.matrix;
    element.applyTransform(m.invert(m.scale({ x: m3.xx, y: m3.yy})));
  }-*/;
  
  public static native JavaScriptObject invertScaleDiagram(JavaScriptObject element, int x, int y, double value)/*-{
    var m3 = new $wnd.dojox.gfx.Matrix2D(value);
    var m = $wnd.dojox.gfx.matrix;
    var s = m.invert(m.scale({ x: m3.xx, y: m3.yy}));
//    var v = m.normalize([s]);
    s.dx = -x;
    s.dy = -y;
    element.applyTransform(s);
  }-*/;

  public void dispatchDiagram(MatrixPointJS point) {
    if (!disabedOnAreaCheck && rootLayer0 != null) {
      int dx = ScaleHelpers.scaleValue(rootLayer0.getTransformX(), scaleFactor);
      int dy = ScaleHelpers.scaleValue(rootLayer0.getTransformY(), scaleFactor);

      boolean none = true;
      for (Diagram d : diagrams) {
        boolean tmp = d.onArea(point.getX() - dx, point.getY() - dy);
        if (tmp) {
          none = false;
        }
      }
    }
  }

  public void setDisableOnArea(boolean value) {
    disabedOnAreaCheck = value;
  }
  
  public double getScaleFactor() {
    return scaleFactor;
  }
  
  public Integer getWidth() {
    return width;
  }
  
  public Integer getHeight() {
    return height;
  }

  public void setVerticalDragOnly(boolean verticalDragOnly) {
    this.verticalDragOnly = verticalDragOnly;
  }
  
  public boolean isVerticalDrag() {
    return verticalDragOnly;
  }

  public void setName(String name) {
    this.name = name;
  }
  public String getName() {
    return name;
  }

  public boolean isLibrary() {
    return !ISurfaceHandler.DRAWING_AREA.equals(getName());
  }
  
  /**
   * Creates a snapshot diagram search. Do not keep the instance, caches items to memory.
   * @return
   */
  public DiagramSearch createDiagramSearch() {
    return new SurfaceDiagramSearch(diagrams);
  }

  @Override
  public HandlerRegistration addTouchStartHandler(TouchStartHandler handler) {
    return addDomHandler(handler, TouchStartEvent.getType());
  }

  @Override
  public HandlerRegistration addTouchEndHandler(TouchEndHandler handler) {
    return addDomHandler(handler, TouchEndEvent.getType());
  }

  @Override
  public HandlerRegistration addTouchMoveHandler(TouchMoveHandler handler) {
    return addDomHandler(handler, TouchMoveEvent.getType());
  }

  public Widget getWidget() {
    return this;
  }

  public HasTouchStartHandlers getHasTouchStartHandlers() {
    return this;
  }

  @Override
  public OTBuffer getOTBuffer() {
    return otBuffer;
  }

  @Override
  public void beginTransaction() {
    operationTransaction.beginTransaction();
  }
  @Override
  public void commitTransaction() {
    operationTransaction.commitTransaction();
  }

  @Override
  public void setSvgClassName(String classname) {
    // String current = surface.getAttribute("class");
    // current = current.length() > 0 ? " " : "";
    svgClassName = classname;
  }

  public void addToDefs(JsArray<JsShape> icons) {
    SurfaceDefs.addToDefs(surface.getContainer(), icons);
  }

}
