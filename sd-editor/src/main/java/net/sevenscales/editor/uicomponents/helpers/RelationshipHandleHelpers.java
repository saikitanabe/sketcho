package net.sevenscales.editor.uicomponents.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.event.RelationshipNotAttachedEvent;
import net.sevenscales.editor.api.impl.TouchHelpers;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.DiagramDragHandler;
import net.sevenscales.editor.diagram.DiagramProxy;
import net.sevenscales.editor.diagram.MouseDiagramHandler;
import net.sevenscales.editor.diagram.shape.CircleShape;
import net.sevenscales.editor.diagram.utils.GridUtils;
import net.sevenscales.editor.gfx.domain.MatrixPointJS;
import net.sevenscales.editor.uicomponents.AbstractDiagramItem;
import net.sevenscales.editor.uicomponents.Anchor;
import net.sevenscales.editor.uicomponents.AnchorElement;
import net.sevenscales.editor.uicomponents.CircleElement;
import net.sevenscales.editor.uicomponents.uml.Relationship2;

public class RelationshipHandleHelpers implements MouseDiagramHandler, DiagramProxy, DiagramDragHandler, IGlobalElement {
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

    initDefaults();
  }

  public static RelationshipHandleHelpers createConnectionHelpers(ISurfaceHandler surface, Relationship2 parentRelationship) {
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

  private void setHandlePosition(CircleElement h, Integer x, Integer y) {
    CircleShape cs = (CircleShape) h.getInfo();
    cs.centerX = x;
    cs.centerY = y;
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
      CircleElement h = createHandle(points.get(i), points.get(i+1), HANDLE_RADIUS, SELECTION_RADIUS);
      handles.add(h);
      
      // add bend points
      if (i+3 < points.size()) {
        int x = (points.get(i) + points.get(i+2)) / 2;
        int y = (points.get(i+1) + points.get(i+3)) / 2;
        CircleElement hm = createHandle(x, y, 5, SELECTION_RADIUS);
        bendHandles.add(hm);
      }
    }
  }
  
  private CircleElement createHandle(int x, int y, int radius, int selectionRadius) {
    return createHandle(x, y, radius, selectionRadius, AbstractDiagramItem.DEFAULT_SELECTION_COLOR);
  }

  private CircleElement createHandle(int x, int y, int radius, int selectionRadius, String color) {
    boolean editable = true;
    CircleElement h = new CircleElement(surface.getInteractionLayer(), surface, this, x, y, radius, selectionRadius, editable);
    h.setStroke(color);
    surface.add(h, true);
    // pass events to parent relationship
    h.addMouseDiagramHandler(this);
    h.setVisible(false);
    return h;
  }

  public void dragStart(Diagram sender) {
    splitRelationshipShapeIfBendPointDragged(sender);
  }

  private void splitRelationshipShapeIfBendPointDragged(Diagram sender) {
    int parentRelHandlesCount = parentHandlesCount();

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
        break;
      }
    }
  }

  public void onDrag(Diagram sender, int dx, int dy) {
    moveHandlesIfParentIsDragged(sender, dx, dy);
    moveParentAccordingToHandle(sender, dx, dy);
    preHighlightTargetAnchor(sender);
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
    if (sender == parentRelationship) {
      // need to translate according to transform
      // otherwise point is not in correct place
      diffx = parentRelationship.getTransformX(); // SilverUtils.getTransformX(shapes.get(0).getRawNode());
      diffy = parentRelationship.getTransformY(); // SilverUtils.getTransformY(shapes.get(0).getRawNode());
    }

    // pre highlight if selection is on attach area
    if (handles.size() > 0 && handles.get(0) == sender || sender == parentRelationship) { // whole relationship is moved
      int x = points.get(0) + diffx;
      int y = points.get(1) + diffy;
      highlightOnHover(x, y, parentRelationship.getStartAnchor()); // startAnchor
    } 
    if (handles.size() > parentRelHandlesCount - 1 && handles.get(parentRelHandlesCount - 1) == sender || sender == parentRelationship) {
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
      anchor.setAnchorElement(anchorElement);
    } else {
      if (anchor.getAnchorElement() != null) {
        anchor.getAnchorElement().highlight(false);
      }
      anchor.clear();
    }
  }

  public void dragEnd(Diagram sender) {
//    if (parentRelationship == null) {
//      return;
//    }

    logger.debug("RelationshipHandleHelpers.dragEnd...");

    // drag source cannot be start or end when "eating" points!
    // int sourceIndex = handles.indexOf(sender);
    // boolean okToEatPoints = !(sourceIndex == 0 || sourceIndex == handles.size()-1); 
    
    int parentRelHandlesCount = parentHandlesCount();
    // target drop might be removing points
//     for (int i = handles.size()-1; okToEatPoints && i >= 0; --i) {
//       CircleElement h = handles.get(i);
//       if (i < parentRelHandlesCount && h == sender) {
//         // get target drop if any
//         CircleElement target = null;
//         int targetIndex = -1;
//         for (int x = handles.size()-1; x >= 0 ; --x) {
//           CircleElement tmp = handles.get(x);
//           int x0 = tmp.getLocationX();
//           int y0 = tmp.getLocationY();
//           double distance = Math.sqrt(Math.pow(h.getLocationX() - x0, 2) + Math.pow(h.getLocationY()-y0, 2));
//           if (h != tmp && distance <= tmp.getRadius()) {
//             target = tmp;
//             targetIndex = x;
//             break;
//           }
//         }
        
//         if (target != null) {
//           // remove all points in between target and itself
//           for (int z = targetIndex-1; z >= i && (z * 2 +1) < points.size(); --z) {
//             // remove points and handles
//             points.remove(z*2+1);
//             points.remove(z*2);
//             CircleElement remove = handles.get(z);
//             handles.remove(remove);
//             surface.remove(remove);
            
//             // remove bend handle also from middle
// //            CircleElement bebefore = bendHandles.get(z-1);
//             CircleElement beafter = bendHandles.get(z);
//             bendHandles.remove(beafter);
// //            bendHandles.remove(bebefore);
//             surface.remove(beafter);
// //            surface.remove(bebefore);
//           }
          
//           // quit, nothing more to do
//           break;
//         }
//       }
//     }
    
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
        
        // add new bend handles before and after
        int x = (points.get(i*2) + points.get(i*2+2)) / 2;
        int y = (points.get(i*2+1) + points.get(i*2+3)) / 2;
        CircleElement hm = createHandle(x, y, 5, SELECTION_RADIUS);
        bendHandles.add(index, hm);

        x = (points.get(i*2+2) + points.get(i*2+4)) / 2;
        y = (points.get(i*2+3) + points.get(i*2+5)) / 2;
        hm = createHandle(x, y, 5, SELECTION_RADIUS);
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
  }

  public void doSetShape(Diagram currentDragged) {
    // logger.debug("RelationshipHandleHelpers.doSetShape...");
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
  public void onMouseUp(Diagram sender, MatrixPointJS point) {
//    if (parentRelationship == null) {
//      return;
//    }

    parentRelationship.onMouseUp(sender, point);
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
      h.removeFromParent();
    }
    
    for (CircleElement b : bendHandles) {
      b.removeFromParent();
    }
    
    parentRelationship = null;
    instances.clear();
  }

}
