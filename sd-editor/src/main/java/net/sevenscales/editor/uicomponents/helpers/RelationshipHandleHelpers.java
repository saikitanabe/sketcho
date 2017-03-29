package net.sevenscales.editor.uicomponents.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.event.RelationshipNotAttachedEvent;
import net.sevenscales.editor.api.event.UndoEvent;
import net.sevenscales.editor.api.event.UndoEventHandler;
import net.sevenscales.editor.api.impl.TouchHelpers;
import net.sevenscales.editor.content.ui.ContextMenuItem;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.DiagramDragHandler;
import net.sevenscales.editor.diagram.DiagramProxy;
import net.sevenscales.editor.diagram.MouseDiagramHandler;
import net.sevenscales.editor.diagram.drag.Anchor;
import net.sevenscales.editor.diagram.drag.AnchorElement;
import net.sevenscales.editor.diagram.shape.CircleShape;
import net.sevenscales.editor.diagram.utils.BezierHelpers;
import net.sevenscales.editor.diagram.utils.GridUtils;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.MatrixPointJS;
import net.sevenscales.editor.gfx.domain.Point;
import net.sevenscales.editor.gfx.domain.PointDouble;
import net.sevenscales.editor.gfx.domain.SegmentPoint;
import net.sevenscales.editor.uicomponents.AbstractDiagramItem;
import net.sevenscales.editor.uicomponents.CircleElement;
import net.sevenscales.editor.uicomponents.uml.Relationship2;

public class RelationshipHandleHelpers implements MouseDiagramHandler, DiagramProxy, DiagramDragHandler, IGlobalElement, CircleElement.DeleteHandler
 {
  private static final SLogger logger = SLogger.createLogger(RelationshipHandleHelpers.class);
  
  private static Map<ISurfaceHandler, RelationshipHandleHelpers> instances;
  
  private static final int HANDLE_RADIUS = 6;
  private static final int SELECTION_RADIUS;
  
  private List<CircleElement> handles = new ArrayList<CircleElement>();
  private List<CircleElement> bendHandles = new ArrayList<CircleElement>();
  private List<Integer> points = new ArrayList<Integer>();
  private ISurfaceHandler surface;

  private Relationship2 parentRelationship;

  static {
    instances = new HashMap<ISurfaceHandler, RelationshipHandleHelpers>();
    
    if (TouchHelpers.isSupportsTouch()) {
      SELECTION_RADIUS = 25;
    } else {
      // on desktop version have a smaller selection area
      SELECTION_RADIUS = 6;
    }
  }

  private RelationshipHandleHelpers(ISurfaceHandler surface, Relationship2 parentRelationship) {
    this.surface = surface;
    // set initial relationship; since parentRelationship should not be null
    // changed on runtime
    this.parentRelationship = parentRelationship;
    
    // to follow if parent relationship is dragged => update handles position
    surface.addDragHandler(this);

    surface.getEditorContext().getEventBus().addHandler(UndoEvent.TYPE, new UndoEventHandler() {
      public void on(UndoEvent event) {
        forceHide();
      }
    });

    initDefaults();

    // fix: sometimes handle helpers are shown after initialization
    forceHide();
  }

  public static RelationshipHandleHelpers createConnectionHelpers(ISurfaceHandler surface, Relationship2 
    parentRelationship) {
    
    if (!surface.getEditorContext().isEditable()) {
      return null;
    }

    RelationshipHandleHelpers result = instances.get(surface);
    if (result == null) {
      if (ISurfaceHandler.DRAWING_AREA.equals(surface.getName())) {
        result = new RelationshipHandleHelpers(surface, parentRelationship);
        instances.put(surface, result);
      } else {
        // currently library doesn't have relationships
      }
    }
    result.setParentRelationship(parentRelationship);
    return result;
  }
  
  public static RelationshipHandleHelpers getIfAny(ISurfaceHandler surface) {
    return instances.get(surface);
  }
  
  private void setParentRelationship(Relationship2 parentRelationship) {
    this.parentRelationship = parentRelationship;
    setPoints();
  }
  
  public void showConditionally(Relationship2 parentRelationship, boolean show) {
    if (surface.getSelectionHandler().isMultiMode()) {
      // do not show handles if multiple items are selected; this is just for performance
      // to speed up lasso selection
      return;
    }
    
    logger.debug("RelationshipHandleHelpers.showConditionally...");
    this.parentRelationship = parentRelationship;
    setPoints();
    
    logger.debug("showConditionally parentHandlesCount {}...", parentHandlesCount());
    
    // TODO should return read only points!! e.g. something inherited from list
    // and throws if tried to modified, e.g. remove, add, insert, or it hides those method, but maybe that is not possible...
    fillHandlePoolByPoints(points);
    applyPositionByParent(show);
    
    logger.debug("show... done");
  }

  private void setPoints() {
    this.points = parentRelationship.getPoints();
  }
  
  private void applyPositionByParent(boolean show) {
    if (parentRelationship.getDiagramItem().isGroup()) {
      return;
    }

    int parentRelHandlesCount = parentHandlesCount();
    int handlesSize = handles.size();
    logger.debug("  parentHandlesCount {}, handlesSize {}", parentRelHandlesCount, handlesSize);
    for (int i = 0; i < handlesSize; ++i) {
      CircleElement h = handles.get(i);
      if (i < parentRelHandlesCount) {
        int x = points.get(i*2);
        int y = points.get(i*2+1);
        x += parentRelationship.getTransformX();
        y += parentRelationship.getTransformY();
        setHandlePosition(h, x, y);
        h.setVisible(show);
      } else {
        // hide rest from the pool
        h.setVisible(false);
      }
    }
    
    if (parentRelationship.isCurved()) {
      curvedBendPoints(parentRelHandlesCount, show);
    } else {
      straightBendPoints(parentRelHandlesCount, show);
    }
  }

  private void straightBendPoints(int parentRelHandlesCount, boolean show) {
    for (int i = 0; i < bendHandles.size(); ++i) {
      CircleElement bh = bendHandles.get(i);
      if (i < parentRelHandlesCount - 1) {
        setHandlePosition(bh, (points.get(i*2) + points.get(i*2+2)) / 2 + parentRelationship.getTransformX(), 
                              (points.get(i*2+1) + points.get(i*2+3)) / 2 + parentRelationship.getTransformY());
        bh.setVisible(show);
      } else {
        // hide rest from the pool
        bh.setVisible(false);
      }
    }
  }

  private void curvedBendPoints(int parentRelHandlesCount, boolean show) {
    for (int i = 0; i < bendHandles.size(); ++i) {
      CircleElement bh = bendHandles.get(i);
      if (i < parentRelHandlesCount - 1) {
        PointDouble point = BezierHelpers.bezierMiddlePoint(i, parentRelationship.getSegments());
        if (point != null) {
          setHandlePosition(bh, point.x + parentRelationship.getTransformX(), 
                                point.y + parentRelationship.getTransformY());
          bh.setVisible(show);
        }
      } else {
        // hide rest from the pool
        bh.setVisible(false);
      }
    }
  }

  private void setHandlePosition(CircleElement h, double x, double y) {
    CircleShape cs = (CircleShape) h.getInfo();
    cs.centerX = (int) x;
    cs.centerY = (int) y;
    h.setShape(cs);
  }

  private int parentHandlesCount() {
    return points.size() / 2;
  }

  public void hide(Relationship2 candidate) {
    logger.debug("RelationshipHandleHelpers.hide...");
    if (candidate == parentRelationship) {
      forceHide();
    }
  }
  
  public void forceHide() {
    logger.debug("forceHide...");
    for (CircleElement ce : handles) {
      ce.setVisible(false);
    }
    for (CircleElement bh : bendHandles) {
      bh.setVisible(false);
    }
  }
  
  private void initDefaults() {
    // add two default points; start and end, no particular position
    // position needs to be set on demand
    points.add(0); points.add(0);
    points.add(0); points.add(0);
    fillHandlePoolByPoints(points);
  }
  
  private void fillHandlePoolByPoints(List<Integer> points) {
    if (handles.size() >= points.size() / 2) {
      return;
    }
    for (int i = 0; i < points.size(); i+=2) {
      CircleElement h = createHandle(points.get(i), points.get(i+1), HANDLE_RADIUS, SELECTION_RADIUS, true);
      handles.add(h);
      
      // add bend points
      if (i+3 < points.size()) {
        int x = (points.get(i) + points.get(i+2)) / 2;
        int y = (points.get(i+1) + points.get(i+3)) / 2;
        CircleElement hm = createHandle(x, y, 5, SELECTION_RADIUS, false);
        bendHandles.add(hm);
      }
    }
  }
  
  private CircleElement createHandle(int x, int y, int radius, int selectionRadius, boolean setDeleteHandler) {
    return createHandle(x, y, radius, selectionRadius, AbstractDiagramItem.DEFAULT_SELECTION_COLOR, setDeleteHandler);
  }

  private CircleElement createHandle(int x, int y, int radius, int selectionRadius, Color color, boolean setDeleteHandler) {
    boolean editable = true;
    CircleElement h = new CircleElement(surface.getInteractionLayer(), surface, this, x, y, radius, selectionRadius, editable, new DiagramItemDTO());
    h.setStroke(color);
    surface.add(h, true);
    // pass events to parent relationship
    h.addMouseDiagramHandler(this);
    h.setVisible(false);
    if (setDeleteHandler) {
      h.setDeleteHandler(this);
    }
    return h;
  }

  public void dragStart(Diagram sender) {
    storeChildrenRelativeDistance(sender);
    splitRelationshipShapeIfBendPointDragged(sender);
    resetClosestPathIfEndPointDragged(sender);
  }

  private void storeChildrenRelativeDistance(Diagram sender) {
    // if (parentRelated(sender)) {
    //   parentRelationship.storeChildrenRelativeDistance();
    // }
  }

  // private void moveChildrenRelatively(Diagram sender) {
  //   if (parentRelated(sender)) {
  //     parentRelationship.moveChildren();
  //   }
  // }

  private boolean parentRelated(Diagram sender) {
    return parentRelationship != null && 
          (isHandle(sender) ||
           sender == parentRelationship.getStartAnchor().getDiagram() ||
           sender == parentRelationship.getEndAnchor().getDiagram());
  }

  private boolean isHandle(Diagram sender) {
    for (CircleElement ce : handles) {
      if (ce == sender) {
        return true;
      }
    }
    for (CircleElement h : bendHandles) {
      if (h == sender) {
        return true;
      }
    }
    return false;
  }

  private void resetClosestPathIfEndPointDragged(Diagram sender) {
    int parentRelHandlesCount = parentHandlesCount();
    for (int i = 0; i < handles.size(); ++i) {
      CircleElement h = handles.get(i);
      if (i < parentRelHandlesCount && sender == h) {
        parentRelationship.resetClosestPath();
      }    
    }
  }

  private void splitRelationshipShapeIfBendPointDragged(Diagram sender) {
    int parentRelHandlesCount = parentHandlesCount();
    boolean morePoints = false;

    for (int i = 0; i < bendHandles.size(); ++i) {
      CircleElement h = bendHandles.get(i);
      if (i < parentRelHandlesCount - 1 && h == sender) {
        logger.debug("splitRelationshipShapeIfBendPointDragged {}...", sender);
        // it might not be needed to align, because
        // bend handle doesn't need to attach anywhere
        int x = h.getLocationX();
        int y = h.getLocationY();
        x = GridUtils.align(x);
        y = GridUtils.align(y);
        h.setShape(x, y, h.getRadius());
        points.add((i+1)*2, x);
        points.add((i+1)*2+1, y);
        morePoints = true;
        break;
      }
    }

    if (morePoints) {
      parentRelationship.resetChildren();
      parentRelationship.resetClosestPath();
    }
  }

  public void onDrag(Diagram sender, int dx, int dy) {
    moveHandlesIfParentIsDragged(sender, dx, dy);
    moveParentAccordingToHandle(sender, dx, dy);
    preHighlightTargetAnchor(sender);
    // moveChildrenRelatively(sender);
  }

  private void moveHandlesIfParentIsDragged(Diagram sender, int dx, int dy) {
    if (sender == parentRelationship) {
      logger.debug("moveHandlesIfParentIsDragged...");
      applyPositionByParent(true);
    }
  }

  private void preHighlightTargetAnchor(Diagram sender) {
    int parentRelHandlesCount = parentHandlesCount();
    
    int diffx = 0;
    int diffy = 0;
    boolean wholeRelationShipIsDragged = sender == parentRelationship;
    if (wholeRelationShipIsDragged) {
      // need to translate according to transform
      // otherwise point is not in correct place
      diffx = parentRelationship.getTransformX(); // SilverUtils.getTransformX(shapes.get(0).getRawNode());
      diffy = parentRelationship.getTransformY(); // SilverUtils.getTransformY(shapes.get(0).getRawNode());
    }

    // pre highlight if selection is on attach area
    if (handles.size() > 0 && handles.get(0) == sender || wholeRelationShipIsDragged) { // whole relationship is moved
      int x = points.get(0) + diffx;
      int y = points.get(1) + diffy;
      highlightOnHover(x, y, parentRelationship.getStartAnchor()); // startAnchor
    } 
    if (handles.size() > parentRelHandlesCount - 1 && handles.get(parentRelHandlesCount - 1) == sender || wholeRelationShipIsDragged) {
      int x = points.get(points.size()-2) + diffx;
      int y = points.get(points.size()-1) + diffy;
      highlightOnHover(x, y, parentRelationship.getEndAnchor());
    }
  }

  private void moveParentAccordingToHandle(Diagram sender, int dx, int dy) {
    int parentRelHandlesCount = parentHandlesCount();
    for (int i = 0; i < handles.size(); ++i) {
      CircleElement h = handles.get(i);
      if (i < parentRelHandlesCount && sender == h) {
//        int x = h.getLocationX();
//        int y = h.getLocationY();
        int x = points.get(i*2);
        int y = points.get(i*2+1);
        x = GridUtils.align(x) + dx; // h.getTransformX();
        y = GridUtils.align(y) + dy; // h.getTransformY();

        points.set(i*2, x);
        points.set(i*2+1, y);
//        redraw = true;
      }
    }
    
    for (int i = 0; i < bendHandles.size(); ++i) {
      CircleElement h = bendHandles.get(i);
      if (i < parentRelHandlesCount -1 && h == sender) {
        int x = points.get((i+1)*2);
        int y = points.get((i+1)*2+1);
        points.set((i+1)*2, x + dx);
        points.set((i+1)*2+1, y + dy);
//        redraw = true;
      }
    }
  }

  private void highlightOnHover(int x, int y, Anchor anchor) {
//    AnchorElement prev = anchor.getAnchorElement();
    AnchorElement anchorElement = surface.getAttachElement(anchor, x, y);
//    if (anchorElement != null && (prev != null && prev.getSource() != anchorElement.getSource()) ) {
    if (anchorElement != null) {
      anchorElement.highlight(true);
      anchor.applyAnchorElement(anchorElement);
    } else {
      if (anchor.getAnchorElement() != null) {
        anchor.getAnchorElement().highlight(false);
      }
      anchor.clear();
    }
  }

  public void dragEnd(Diagram sender) {
   if (parentRelationship != sender && parentRelationship != sender.getOwnerComponent()) {
      // if either of these are parentRelationship then skip
     return;
   }

    logger.debug("RelationshipHandleHelpers.dragEnd...");
    
    int parentRelHandlesCount = parentHandlesCount();
    for (int i = 0; i < bendHandles.size(); ++i) {
      CircleElement h = bendHandles.get(i);
      if (i < parentRelHandlesCount - 1 && h == sender && (i * 2 + 5) < points.size()) {
        // remove from bend handles and add to handles, because
        // now handle is a new point
        int index = bendHandles.indexOf(h);
        bendHandles.remove(h);

        // change radius size
        CircleShape cs = (CircleShape) h.getInfo();
        cs.centerX = points.get(i*2);
        cs.centerY = points.get(i*2+1);
        cs.radius = HANDLE_RADIUS;
        h.setShape(cs);     

        // this doesn't work for some reason
//        h.setRadius(10);
        handles.add(index+1, h);
        h.setDeleteHandler(this);
        
        // add new bend handles before and after
        int x = (points.get(i*2) + points.get(i*2+2)) / 2;
        int y = (points.get(i*2+1) + points.get(i*2+3)) / 2;
        CircleElement hm = createHandle(x, y, 5, SELECTION_RADIUS, false);
        bendHandles.add(index, hm);

        x = (points.get(i*2+2) + points.get(i*2+4)) / 2;
        y = (points.get(i*2+3) + points.get(i*2+5)) / 2;
        hm = createHandle(x, y, 5, SELECTION_RADIUS, false);
        bendHandles.add(index+1, hm);
        break;
      }
    }
    
    // can only attach when anchor itself is moved
    // reattach if anchor target is moved
    // in ellipse case point is otherwise lost
    
    CircleElement endHandle = getEndHandle();
    if (endHandle != null && endHandle.equals(sender) || sender.equals(this)) {
      if (!parentRelationship.findAndAttachAnchor(points.size()-2, points.size()-1, parentRelationship.getEndAnchor(), true) && endHandle.equals(sender)) {
        // not attached; fire event relationship not attached
        surface.getEditorContext().getEventBus().fireEvent(
            new RelationshipNotAttachedEvent(points.get(points.size()-2), points.get(points.size()-1), parentRelationship, parentRelationship.getEndAnchor()));
      }
    }
//    } else if (endAnchor.getDiagram() != null && endAnchor.getDiagram().equals(sender)) {
//      reattachAnchor(endAnchor);
//    }
    CircleElement startHandle = getStartHandle(); 
    if (startHandle != null && startHandle.equals(sender) || sender.equals(this)) {
      if (!parentRelationship.findAndAttachAnchor(0, 1, parentRelationship.getStartAnchor(), true) && startHandle.equals(sender)) {
        // not attached; fire event relationship not attached
        surface.getEditorContext().getEventBus().fireEvent(
            new RelationshipNotAttachedEvent(points.get(0), points.get(1), parentRelationship, parentRelationship.getStartAnchor()));
      }
    }
    
    forceHide();
    parentRelationship.doSetShape();
    // moveChildrenRelatively(sender);
  }

  public void doSetShape(Diagram currentDragged) {
  }

  public CircleElement getEndHandle() {
    if (parentHandlesCount() - 1 < handles.size()) {
      return handles.get(parentHandlesCount() - 1);
    }
    return null;
  }
  
  public CircleElement getStartHandle() {
    if (handles.size() > 0) {
      return handles.get(0);
    }
    return null;
  }

  @Override
  public boolean onMouseDown(Diagram sender, MatrixPointJS point, int keys) {
//    if (parentRelationship == null) {
//      return false;
//    }

    parentRelationship.onMouseDown(sender, point, keys);
    return false;
  }

  @Override
  public void onMouseUp(Diagram sender, MatrixPointJS point, int keys) {
//    if (parentRelationship == null) {
//      return;
//    }

    parentRelationship.onMouseUp(sender, point, keys);
  }

  @Override
  public void onMouseMove(Diagram sender, MatrixPointJS point) {
//    if (parentRelationship == null) {
//      return;
//    }

    parentRelationship.onMouseMove(sender, point);
  }

  @Override
  public void onMouseLeave(Diagram sender, MatrixPointJS point) {
//    if (parentRelationship == null) {
//      return;
//    }

    parentRelationship.onMouseLeave(sender, point);
  }

  @Override
  public void onMouseEnter(Diagram sender, MatrixPointJS point) {
//    if (parentRelationship == null) {
//      return;
//    }

    parentRelationship.onMouseEnter(sender, point);
  }

  @Override
  public void onTouchStart(Diagram sender, MatrixPointJS point) {
//    if (parentRelationship == null) {
//      return;
//    }

    parentRelationship.onTouchStart(sender, point);
  }

  @Override
  public void onTouchMove(Diagram sender, MatrixPointJS point) {
//    if (parentRelationship == null) {
//      return;
//    }

    parentRelationship.onTouchMove(sender, point);
  }

  @Override
  public void onTouchEnd(Diagram sender, MatrixPointJS point) {
//    if (parentRelationship == null) {
//      return;
//    }

    parentRelationship.onTouchEnd(sender, point);
  }

  @Override
  public Diagram getDiagram() {
    return parentRelationship;
  }

  @Override
  public boolean isSelected() {
    return parentRelationship.isSelected();
  }

  @Override
  public AbstractDiagramItem getParent() {
    return parentRelationship;
  }

  @Override
  public void hideGlobalElement() {
    forceHide();
  }

  /**
   * Removes this global handler from memory.
   */
  @Override
  public void release() {
    for (CircleElement h : handles) {
      h.removeFromParentForce();
    }
    
    for (CircleElement b : bendHandles) {
      b.removeFromParentForce();
    }
    
    parentRelationship = null;
    instances.clear();
  }

  @Override
  public void remove(CircleElement ce) {
    // handles.remove(ce);
    // ce.removeFromParent();
    int index = handles.indexOf(ce);
    ce.setVisible(false);
    parentRelationship.removePoint(index);
    parentRelationship.doSetShape();
  }

  @Override
  public int supportedMenuItems(CircleElement ce) {
    if (ce != null && !ce.equals(getStartHandle()) && !ce.equals(getEndHandle())) {
      return ContextMenuItem.DELETE.getValue();
    } else {
      return ContextMenuItem.NO_MENU.getValue();
    }
  }

  private static boolean isClosest(int x, int y, double px, double py, Point diff) {
    boolean result = false;
    int dx = Math.abs(x - (int) px);
    int dy = Math.abs(y - (int) py);
    if ( (dx + dy) < (diff.x + diff.y) ) {
      diff.x = dx;
      diff.y = dy;
      result = true;
    }
    return result;
  }

  public static SegmentPoint findClosestSegmentPointIndex(int x, int y, Relationship2 relationship) {
    SegmentPoint result = new SegmentPoint();
    Point diff = new Point(Integer.MAX_VALUE / 2, Integer.MAX_VALUE / 2);
    for (int i = 0; i < relationship.getSegments().length(); ++i) {
      BezierHelpers.Segment seg = relationship.getSegments().get(i);
      if (isClosest(x, y, seg.getPoint1().getX(), seg.getPoint1().getY(), diff)) {
        result.segmentIndex = i;
        result.inSegmentIndex = 0;
      } 

      PointDouble point = BezierHelpers.bezierMiddlePoint(seg);
      if (isClosest(x, y, point.x, point.y, diff)) {
        result.segmentIndex = i;
        result.inSegmentIndex = 1;
      }

      if (isClosest(x, y, seg.getPoint2().getX(), seg.getPoint2().getY(), diff)) {
        result.segmentIndex = i;
        result.inSegmentIndex = 2;
      } 

      // if (relationship.isCurved()) {
      //   PointDouble point = bezierMiddlePoint(i, relationship);
      //   int ddx = Math.abs(x - point.x);
      //   int ddy = Math.abs(y - point.y);
      //   if ( (dx + dy) < (ddx + ddy) ) {
      //     dx = ddx;
      //     dy = ddy;
      //     result = index + 1;
      //   }
      // } else {
      //   int x = middleX(points, parentRelationship); 
      //   int y = middleY(points, parentRelationship);
      //   int ddx = Math.abs(x - point.x);
      //   int ddy = Math.abs(y - point.y);
      //   if ( (dx + dy) < (ddx + ddy) ) {
      //     dx = ddx;
      //     dy = ddy;
      //     result = index + 1;
      //   }
      // }
    }
    // int parentRelHandlesCount = parentHandlesCount();
    // int handlesSize = handles.size();
    // int result = -1;
    // int index = 0;
    // int dx = Integer.MAX_VALUE / 2;
    // int dy = Integer.MAX_VALUE / 2;
    // for (int i = 0; i < parentRelHandlesCount && i < handlesSize; ++i) {
    //   CircleElement h = handles.get(i);
    //   int ddx = Math.abs(x - h.getLeft());
    //   int ddy = Math.abs(y - h.getTop());
    //   if ( (ddx + ddy) < (dx + dy) ) {
    //     dx = ddx;
    //     dy = ddy;
    //     result = index;
    //   }
    //   index += 2;
    // }

    // int bindex = 1;
    // for (int i = 0; i < parentRelHandlesCount - 1 && i < bendHandles.size(); ++i) {
    //   CircleElement h = bendHandles.get(i);
    //   int ddx = Math.abs(x - h.getLeft());
    //   int ddy = Math.abs(y - h.getTop());
    //   if ( (ddx + ddy) < (dx + dy) ) {
    //     dx = ddx;
    //     dy = ddy;
    //     result = bindex;
    //   }
    //   bindex += 2;
    // }

    return result;
  }

}
