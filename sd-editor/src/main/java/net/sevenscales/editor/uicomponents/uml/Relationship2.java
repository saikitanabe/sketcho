package net.sevenscales.editor.uicomponents.uml;

import java.util.ArrayList;
import java.util.List;

import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.domain.utils.StringUtil;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.impl.Theme;
import net.sevenscales.editor.api.impl.TouchHelpers;
import net.sevenscales.editor.api.impl.Theme.ElementColorScheme;
import net.sevenscales.editor.content.ui.ContextMenuItem;
import net.sevenscales.editor.content.utils.AreaUtils;
import net.sevenscales.editor.content.utils.DiagramHelpers;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.DiagramDragHandler;
import net.sevenscales.editor.diagram.DiagramResizeHandler;
import net.sevenscales.editor.diagram.shape.Info;
import net.sevenscales.editor.diagram.shape.RelationshipShape2;
import net.sevenscales.editor.diagram.utils.ReattachHelpers;
import net.sevenscales.editor.gfx.domain.IGroup;
import net.sevenscales.editor.gfx.domain.ILine;
import net.sevenscales.editor.gfx.domain.IPolyline;
import net.sevenscales.editor.gfx.domain.IShape;
import net.sevenscales.editor.gfx.domain.IShapeFactory;
import net.sevenscales.editor.uicomponents.AbstractDiagramItem;
import net.sevenscales.editor.diagram.drag.Anchor;
import net.sevenscales.editor.diagram.drag.AnchorElement;
import net.sevenscales.editor.diagram.drag.AnchorMoveHandler;
import net.sevenscales.editor.diagram.drag.ConnectionMoveHandler;
import net.sevenscales.editor.uicomponents.AngleUtil2;
import net.sevenscales.editor.uicomponents.CircleElement;
import net.sevenscales.editor.uicomponents.Point;
import net.sevenscales.editor.uicomponents.RelationshipText2;
import net.sevenscales.editor.uicomponents.RelationshipText2.ClickTextPosition;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil;
import net.sevenscales.editor.uicomponents.helpers.ConnectionHelpers;
import net.sevenscales.editor.uicomponents.helpers.IConnectionHelpers;
import net.sevenscales.editor.uicomponents.helpers.RelationshipHandleHelpers;
import net.sevenscales.editor.uicomponents.impl.RelationshipTextUtil2;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.DiagramItemDTO;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;


public class Relationship2 extends AbstractDiagramItem implements DiagramDragHandler, DiagramResizeHandler {
	private static final SLogger logger = SLogger.createLogger(Relationship2.class);
	private IPolyline inheritance;
  private IPolyline arrow;
  private IPolyline aggregate;
  private ArrowStartPolyline arrowStartPolyline;
  private RelationshipText2 relationshipText;
//  private List<IShape> elements = new ArrayList<IShape>();

  // diamond information
  private Point dright = new Point();
  private Point dleft = new Point();
  private Point dline = new Point();
  private int dbx; // to position start text correctly
  private int dby;

  // arrow head information
  private Point right = new Point();
  private Point left = new Point();
  private static final int SELECTION_AREA_WIDTH;
  private static final int ARROW_WIDTH = 6;
  private double angle = 30;

  private Anchor endAnchor;
  private Anchor startAnchor;
  private List<Integer> points;
//  private List<Integer> prevPoints = new ArrayList<Integer>();
  private String text;
  private RelationshipTextUtil2 textUtil;
  private int[] inheritancePoints;
  private int[] arrowPoints;
  private int[] aggregatePoints;
  
  private RelationshipShape2 info;
  private RelLine relLine;
  private Diagram currentDragged;
//	protected boolean followUpChange;
	private IGroup group;
	private ClickTextPosition currentTextEditLocation;
	private boolean legacyAnchor;
  private RelationshipHandleHelpers relationshipHandleHelpers;
  private AnchorMoveHandler handler;

  private static class ArrowStartPolyline {
    private IPolyline arrowStart;
    private Relationship2 parent;

    ArrowStartPolyline(Relationship2 parent) {
      this.parent = parent;
    }

    void setShape(int startx, int starty) {
      if (parent.info != null && parent.info.isDirectedStart()) {
        createIfNull();
        // borrow arrowPoints array; it is always calculated
        // before applying as a shape to arrow
        parent.arrowPoints[0] = parent.dleft.x; parent.arrowPoints[1] = parent.dleft.y;
        parent.arrowPoints[2] = startx; parent.arrowPoints[3] = starty;
        parent.arrowPoints[4] = parent.dright.x; parent.arrowPoints[5] = parent.dright.y;
        arrowStart.setShape(parent.arrowPoints);
      }
      if (arrowStart != null) {
        arrowStart.setVisibility(parent.info.isDirectedStart());
      }
    }

    /**
    * To minimize DOM manipulation unnecessarily.
    */
    private void createIfNull() {
      if (arrowStart == null) {
        arrowStart = IShapeFactory.Util.factory(parent.editable).createPolyline(parent.group, parent.arrowPoints);
        arrowStart.setStroke(Theme.getCurrentColorScheme().getBorderColor().toHexString());
        arrowStart.setFill(255, 255, 255, 0);
        parent.shapes.add(arrowStart);
      }
    }

    void setVisibility(boolean visible) {
      if (arrowStart != null) {
        arrowStart.setVisibility(visible);
      }
    }

    void setStroke(String color) {
      if (arrowStart != null) {
        arrowStart.setStroke(color);
      }
    }

  }
	
//	private TextPosition textUnderEdit = TextPosition.TEXT_ALL;
//	private enum TextPosition {
//		TEXT_START, TEXT_MIDDLE, TEXT_END, TEXT_ALL
//	}
	
	static {
    SLogger.addFilter(Relationship2.class);

		if (TouchHelpers.isSupportsTouch()) {
			SELECTION_AREA_WIDTH = 15;
		} else {
			SELECTION_AREA_WIDTH = 6;
		}
	}
  
  class RelLine {
    private IPolyline polyline;
    private IPolyline lineBackground;
    private List<IShape> elements = new ArrayList<IShape>();

    public RelLine() {
      polyline = IShapeFactory.Util.factory(editable).createPolyline(group, points);
      polyline.setStroke(Theme.getCurrentColorScheme().getBorderColor().toHexString());
      // do not fill polyline, because it will be selectable area and hides everything under it!
//      polyline.setFill(150, 150, 150, 0.5);
      lineBackground = IShapeFactory.Util.factory(editable).createPolyline(group, points);
      lineBackground.setFill(255, 255, 255, 0);
      
      elements.add(polyline);
      elements.add(lineBackground);
//      setShape(points);
    }
    
    public List<IShape> getShapes() {
      return elements;
    }
    
    public IPolyline getSelectionArea() {
      return lineBackground;
    }
    
    public void setStyle(String style) {
      polyline.setStyle(style);
    }

    public void setShape(List<Integer> points) {
      polyline.setShape(points);
      List<Integer> selectionAreaPoints = new ArrayList<Integer>();
      int firstX = 0;
      int firstY = 0;
      
      int lineCount = points.size()/2-1;
      // piirrä toinen reuna
      for (int i = 0, line = 0; line < lineCount; i += 2, ++line) {
        int x1 = points.get(i);
        int y1 = points.get(i+1);
        int x2 = points.get(i+2);
        int y2 = points.get(i+3);
        
        double beta = AngleUtil2.beta(x1, y1, x2, y2);
        double gamma = Math.PI - Math.PI / 2 - beta;
  
        int ydiff = (int) (Math.sin(gamma) * SELECTION_AREA_WIDTH);
        int xdiff = (int) (Math.cos(gamma) * SELECTION_AREA_WIDTH);
        
//        selectionAreaPoints.add(x1 + xdiff); selectionAreaPoints.add(y1 - ydiff); 
        selectionAreaPoints.add(x1 - xdiff); selectionAreaPoints.add(y1 + ydiff); 
        selectionAreaPoints.add(x2 - xdiff); selectionAreaPoints.add(y2 + ydiff);
        if (line == 0) {
          firstX = x1 - xdiff;
          firstY = y1 + ydiff;
        }
//        selectionAreaPoints.add(6, x2 + xdiff); selectionAreaPoints.add(7, y2 - ydiff);
//        selectionAreaPoints.add(8, x1 + xdiff); selectionAreaPoints.add(9, y1 - ydiff); 
      }
      
      // piirrä toisesta reunasta loppuun
      for (int i = points.size()-1, line = lineCount; line > 0; i -= 2, --line) {
        int y1 = points.get(i);
        int x1 = points.get(i-1);
        int y2 = points.get(i-2);
        int x2 = points.get(i-3);
        
        double beta = AngleUtil2.beta(x1, y1, x2, y2);
        double gamma = Math.PI - Math.PI / 2 - beta;
  
        int ydiff = (int) (Math.sin(gamma) * SELECTION_AREA_WIDTH);
        int xdiff = (int) (Math.cos(gamma) * SELECTION_AREA_WIDTH);
        
//        selectionAreaPoints.add(x1 + xdiff); selectionAreaPoints.add(y1 - ydiff); 
        selectionAreaPoints.add(x1 - xdiff); selectionAreaPoints.add(y1 + ydiff); 
        selectionAreaPoints.add(x2 - xdiff); selectionAreaPoints.add(y2 + ydiff); 
//        selectionAreaPoints.add(x2 + xdiff); selectionAreaPoints.add(y2 - ydiff);
//        selectionAreaPoints.add(8, x1 + xdiff); selectionAreaPoints.add(9, y1 - ydiff); 
      }

      selectionAreaPoints.add(firstX); selectionAreaPoints.add(firstY);
      lineBackground.setShape(selectionAreaPoints);
    }

    public void setStroke(String color) {
      polyline.setStroke(color);
    }

		public void moveBackgroundToBack() {
			lineBackground.moveToBack();
		}

  }
  
  public Relationship2(ISurfaceHandler surface, RelationshipShape2 points, String text,
      boolean editable, IDiagramItemRO item) {
    super(editable, surface, Theme.createDefaultBackgroundColor(), Theme.createDefaultBorderColor(), Theme.createDefaultTextColor(), item);
    this.points = points.points;
    handler = new ConnectionMoveHandler();
    
    group = IShapeFactory.Util.factory(editable).createGroup(surface.getConnectionLayer());

    startAnchor = new Anchor(this);
    endAnchor = new Anchor(this);
    
    startAnchor.setTheOtherEnd(endAnchor);
    endAnchor.setTheOtherEnd(startAnchor);
    
//    anchorPoint = new Circle(surface.getSurface());
//    anchorPoint.setStrokeWidth(1);
//    anchorPoint.setVisibility(false);

    textUtil = new RelationshipTextUtil2();
    relationshipText = new RelationshipText2(group, surface, editable);

    inheritancePoints = new int[8];
    inheritance = IShapeFactory.Util.factory(editable).createPolyline(group, inheritancePoints);
    inheritance.setFill(255, 255, 255, 1);
    
    int endx = this.points.get(this.points.size()-2);
    int endy = this.points.get(this.points.size()-1);
    arrowPoints = new int[]{
          right.x, right.y,
          endx, endy, 
          left.x, left.y
        };
    arrow = IShapeFactory.Util.factory(editable).createPolyline(group, arrowPoints);
    arrow.setFill(255, 255, 255, 0);

    arrowStartPolyline = new ArrowStartPolyline(this);
    
    aggregatePoints = new int[10];
    aggregate = IShapeFactory.Util.factory(editable).createPolyline(group, aggregatePoints);
    aggregate.setFill(255, 255, 255, 1);
    
    this.relLine = new RelLine();
//    for (Shape e : relLine.getShapes()) {
//      addObserver(relLine.getSelectionArea().getRawNode(), "MouseLeftButtonDown");
//      addObserver(relLine.getSelectionArea().getRawNode(), "MouseLeftButtonUp");
//    }

    relationshipHandleHelpers = RelationshipHandleHelpers.createConnectionHelpers(surface, this);

    shapes.addAll(relLine.getShapes());
    shapes.add(arrow);
    shapes.add(aggregate);
    shapes.add(inheritance);
    shapes.addAll(relationshipText.getElements());

    // needed to speed up relationship construction
    Scheduler.get().scheduleDeferred(new ScheduledCommand() {
      @Override
      public void execute() {
        for (IShape e : shapes) {
          e.addGraphicsMouseDownHandler(Relationship2.this);
          e.addGraphicsMouseUpHandler(Relationship2.this);
          e.addGraphicsTouchStartHandler(Relationship2.this);
          e.addGraphicsTouchEndHandler(Relationship2.this);
        }
      }
    });

    setReadOnly(!editable);
    setText(text);
    
    setBorderColor(Theme.getCurrentColorScheme().getBorderColor());

    // applyAnnotationColors();
  }
  
  @Override
  protected IConnectionHelpers createConnectionHelpers() {
  	return ConnectionHelpers.createEmptyConnectionHelpers();
  }
  
//  private void initHandles() {
//    for (int i = 0; i < points.size(); i+=2) {
//      CircleElement h = createHandle(points.get(i), points.get(i+1), HANDLE_RADIUS, SELECTION_RADIUS);
//      handles.add(h);
//      
//      // add bend points
//      if (i+3 < points.size()) {
//        int x = (points.get(i) + points.get(i+2)) / 2;
//        int y = (points.get(i+1) + points.get(i+3)) / 2;
//        CircleElement hm = createHandle(x, y, 5, SELECTION_RADIUS);
//        bendHandles.add(hm);
//      }
//    }
//	}
  
//	private CircleElement createHandle(int x, int y, int radius, int selectionRadius) {
//    return createHandle(x, y, radius, selectionRadius, "#000000");
//  }
//
//  private CircleElement createHandle(int x, int y, int radius, int selectionRadius, String color) {
//    CircleElement h = new CircleElement(surface.getInteractionLayer(), surface, this, x, y, radius, selectionRadius, getEditable());
//    h.setStroke(color);
//    surface.add(h, true);
//    h.addMouseDiagramHandler(this);
//    h.setVisible(false);
//    return h;
//  }

  // @Override
  public void accept(ISurfaceHandler surface) {
    // NOTE: there is a special handling on MouseDiagramDragHandler for CircleElement follow
//    surface.addDragHandler(this);
    surface.makeDraggable(this);
    surface.makeBendable(this);
    surface.addResizeHandler(this);
  }
  
  @Override
  public void saveLastTransform(int dx, int dy) {
  	resetTransform();
  	for (int i = 0; i < points.size(); i += 2) {
  		points.set(i, points.get(i) + dx);
  		points.set(i + 1, points.get(i + 1) + dy);
  	}
  	
	  startAnchor.setDiff(dx, dy);
	  endAnchor.setDiff(dx, dy);
	  
  	doSetShape();
  }
  
//  public void saveLastTransform(int dx, int dy) {
////    System.out.println("saveLastTransform:"+points);
//
//    // pick one of the elements
////    int dx = SilverUtils.getTransformX(elements.get(0).getRawNode());
////    int dy = SilverUtils.getTransformY(elements.get(0).getRawNode());
//    
//    for (IShape s : shapes) {
//      s.applyTransform(dx, dy);
//      SilverUtils.resetRenderTransform(s.getRawNode());
//    }
//    for (int i = 0; i < points.size(); i+=2) {
//      points.set(i, points.get(i)+dx);
//      points.set(i+1, points.get(i+1)+dy);
//    }
//    
//    
//    // need to reset handles transformation in here
//    // or those will be moving twice
//    for (CircleElement ce : handles) {
//      ce.resetTransform();
//    }
//    for (CircleElement ce : bendHandles) {
//      ce.resetTransform();
//    }
//    
//    // move anchors as well to correct place
//    startAnchor.setDiff(dx, dy);
//    endAnchor.setDiff(dx, dy);
//
////    System.out.println("saveLastTransform:"+" dx:"+dx+"dy:"+dy+points);
//
//    doSetShape();
//    surface.scaleDiagram(this);
////    super.saveLastTransform();
//  }
  
//  @Override
//  public void applyTransform(MatrixPointJS point) {
//  	group.applyTransform(point.getDX(), point.getDY());

//	  for (CircleElement ce : handles) {
//	  	ce.applyTransform(point);
//		}
//		for (CircleElement ce : bendHandles) {
//		  ce.applyTransform(point);
//		}
//  }

  @Override
  public void applyTransform(int dx, int dy) {
  	group.applyTransform(dx, dy);
  	
  	hideAllHandles();
  	
//    super.applyTransform(dx, dy);
//    for (CircleElement ce : handles) {
//      ce.setShape(ce.getLocationX()+dx, ce.getLocationY()+dy, ce.getRadius());
//    }
//    for (CircleElement ce : bendHandles) {
//      ce.setShape(ce.getLocationX()+dx, ce.getLocationY()+dy, ce.getRadius());
//    }
//    for (CircleElement ce : handles) {
//      ce.applyTransform(dx, dy);
//    }
//    for (CircleElement ce : bendHandles) {
//      ce.applyTransform(dx, dy);
//    }
  }

  public Point getDiffFromMouseDownLocation() {
    // not draggable
    return null;
  }

  public void select() {
    super.select();
    relationshipHandleHelpers.showConditionally(this, true);
    // setBorderColor(DEFAULT_SELECTION_COLOR);
  }

  public void unselect() {
    super.unselect();
    relationshipHandleHelpers.hide(this);
    // applyAnnotationColors();

    // setBorderColor(Theme.getCurrentColorScheme().getBorderColor().toHexString());
    
    // allow e.g. connection helpers to higher than line background
//    relLine.moveBackgroundToBack();
  }
  
  @Override
  public AnchorElement onAttachArea(Anchor anchor, int x, int y) {
    return null;
//    if (anchor.getDiagram() == this) {
//      return null;
//    }
//    // y = kx + b
//    // (y-y0) = k(x -x0)
//    // google: pisteen etäisyys suorasta
//    int lineCount = points.size()/2-1;
//    // piirrä toinen reuna
//    for (int i = 0, line = 0; line < lineCount; i += 2, ++line) {
//      int x1 = points.get(i);
//      int y1 = points.get(i+1);
//      int x2 = points.get(i+2);
//      int y2 = points.get(i+3);
//      int a = y2-y1;
//      int b = x2-x1;
//      double k = (a)/(b);
//      
//      double distance = Math.abs( (y-y1) - k*(x-x1) ) / Math.sqrt( Math.pow(a, 2)+Math.pow(b, 2) );
//      
//      if (distance < 5) {
//        AnchorElement result = anchorMap.get(anchor);
//
//        double kalpha = AngleUtil2.slope(x1, y1, x2, y2);
//        double beta = Math.PI - kalpha;
//        double alpha = Math.PI - Math.PI/2 - beta;
//        int dx = (int) (distance * Math.sin(alpha));
//        int dy = (int) (distance * Math.cos(alpha));
//        
//        if (result == null) {
//          result = new AnchorElement(anchor, this);//TODO x, y of nearest anchor point);
//          anchorMap.put(anchor, result);
//        }
//        
//        int ax = x - dx;
//        int ay = y - dy;
//        this.anchorPoint.setShape(ax, ay, 6);
//        this.anchorPoint.moveToFront();
//
//        result.setAx(ax);
//        result.setAy(ay);
////        result.setRelativeX(alpha);
//
//        return result; 
//      }
//    }
//    return null;
  }
  
  @Override
  public boolean onArea(int left, int top, int right, int bottom) {
  	return AreaUtils.onArea(points, left, top, right, bottom);
  }

  public void removeFromParent() {
    super.removeFromParent();

    relationshipHandleHelpers.hide(this);

    detachConnections();    

    startAnchor.clear();
    endAnchor.clear();
    
    // surface.getMouseDiagramManager().getDragHandler().detach(this);
  }

  private void detachAnchor(Anchor anchor) {
    if (anchor.getAnchorElement() != null) {
      anchor.getAnchorElement().detach();
    }
  }

	public void dragStart(Diagram sender) {
    currentDragged = sender;
    
    // relationshipHandleHelpers gets dragStart event direcly
    // registering to mouse drag handler, that's why commented out
    //  relationshipHandleHelpers.dragStart(sender);
  }

  public void onDrag(Diagram sender, int dx, int dy) {
    // relationshipHandleHelpers onDrag gets called first by
    // mouse drag handler and then drag handler calls CircleElement
    // owner that is e.g. relationship (could be seq as well).
    // then relationship gets redrawn. Not perfect logic, but works for 
    // now.
//    boolean redraw = relationshipHandleHelpers.onDrag(sender, dx, dy);

//    if (redraw) {
    doSetShape();
//    } 
      // it might be anchor that has changed
      // dynamically change line if anchor changes position
//      doAnchorDragChanges(sender, dx, dy);
  }
  
  /**
   * NOTE this actually not called at the moment... rel handl helpers calls doSetShape on dragEnd for now.
   */
  public void dragEnd(Diagram sender) {
    currentDragged = null;
//    relationshipHandleHelpers.dragEnd(sender);
    
//    if (isSelected()) {
    	// reattachIfPartOfSelection();
    	// if I and start or end dieagram is part of the selection, reattach 
//    	anchor(false);
//    }
//    } else if (startAnchor.getDiagram() != null && startAnchor.getDiagram().equals(sender)) {
//      reattachAnchor(startAnchor);
//    }

//    } else if (endAnchor.getDiagram() != null && endAnchor.getDiagram().equals(sender)) {
//      reattachAnchor(endAnchor);
//    }
//    attachAnchor(0, 1, startAnchor, true);
//    anchor(true);
    
//    System.out.println(this+" "+sender+"dragEnd:"+points);
    doSetShape();
    
//    if (followUpChange) {
//    	surface.getEditorContext().getEventBus().fireEvent(new PotentialOnChangedEvent(this));
//    }
//    followUpChange = false;
  }
  
//  private void reattachAnchor(Anchor anchor) {
//    anchor.getAnchorElement().reattach();
//  }

  @Override
  public Diagram duplicate(boolean partOfMultiple) {
    return duplicate(surface, points.get(0)+20, points.get(1)+20);
  }

  @Override
  public Diagram duplicate(ISurfaceHandler surface, int x, int y) {
//    RelationshipShape newshape = new RelationshipShape(
//        shape.startPoint.x + 20, shape.startPoint.y + 20, shape.endPoint.x + 20, shape.endPoint.y + 20, shape.caps);

    List<Integer> ps = new ArrayList<Integer>();
    
    int basex = points.get(0);
    int basey = points.get(1);
    int diffx = x - basex;
    int diffy = y - basey;
    int isx = 0;
    for (int val : points) {
      if (isx++ % 2 == 0) {
        ps.add(val+diffx);
      } else {
        ps.add(val+diffy);
      }
    }
    Relationship2 result = new Relationship2(surface, new RelationshipShape2(ps), getText(), getEditable(), new DiagramItemDTO());
    return result;
  }

  // ///////////////////////////////////////////////////////////////////////////
  public boolean findAndAttachAnchor(int posX, int posY, Anchor anchor, boolean highlight) {
    AnchorElement anchorElement = surface.getAttachElement(
        anchor, points.get(posX), points.get(posY));
    return attachAnchor(anchorElement, posX, posY, anchor, highlight);
  }
  
  public boolean attachAnchor(AnchorElement anchorElement, int posX, int posY, Anchor anchor, boolean highlight) {
  	boolean result = false;
    if (anchorElement != null) {
      // set adjusted point
//      int ax = GridUtils.align(anchorElement.getAx());
//      int ay = GridUtils.align(anchorElement.getAy());
      int ax = anchorElement.getAx();
      int ay = anchorElement.getAy();
//      System.out.println(posX + ","+posY+" "+"points.get(posX)"+points.get(posX)+" attachAnchor:" + ax+","+ay);
      points.set(posX, ax);
      points.set(posY, ay);
      anchor.applyAnchorElement(anchorElement);
      anchor.setDiagram(anchorElement.getSource(), false);
      anchorElement.attach();
      // anchor.getDiagram().attachedRelationship(anchorElement);
      // surface.getMouseDiagramManager().getDragHandler().attach(this, anchorElement);

      anchorElement.highlight(false);
      doSetShape();
      result = true;
    } else {
      anchor.clear();
    }

    return result;
//    Diagram d = surface.getAttachElement(x, y);
//    if (d != null) {
//      // System.out.println("attached: " + anchor + "->" + d);
//      // over some diagram attach area
//      anchor.setDiagram(d, highlight);
//      anchor.setPoint(x, y);
//    } else {
//      // deattach
//      // System.out.println("deattached: " + anchor + "->" +
//      // anchor.getDiagram());
//      anchor.setDiagram(null);
//      anchor.setPoint(null);
//    }
  }
  
  public void detachConnections() {
    detachAnchor(startAnchor);
    detachAnchor(endAnchor);
  	// surface.getMouseDiagramManager().getDragHandler().detach(this);
  }

  private void calculateArrowHead(double angle, int width,
      int x1, int y1, int x2, int y2) {
    angle = angle * Math.PI / 180;
    double beta = AngleUtil2.beta(x1, y1, x2, y2);
    double height = width / Math.tan(angle);

    // calculate base
    double betaadj = Math.sin(beta) * height;
    double betanext = Math.cos(beta) * height;

    // System.out.println("beta:" + Math.toDegrees(beta) + " betaadj:" + betaadj
    // + " betanext:" + betanext);

    int bx = (int) (points.get(points.size()-2) + betanext);
    int by = (int) (points.get(points.size()-1) + betaadj);

    double gamma = Math.PI - Math.PI / 2 - beta;

    int ydiff = (int) (Math.sin(gamma) * width);
    int xdiff = (int) (Math.cos(gamma) * width);

    // left side
    left.x = bx - xdiff;
    left.y = by + ydiff;

    // right side
    right.x = bx + xdiff;
    right.y = by - ydiff;
  }

  private void calculateDiamond(double angle, int width,
      int x1, int y1, int x2, int y2) {
    angle = angle * Math.PI / 180;
    double beta = AngleUtil2.beta(x1, y1, x2, y2) + Math.PI;
    double height = width / Math.tan(angle);

    // calculate base
    double betaadj = Math.sin(beta) * height;
    double betanext = Math.cos(beta) * height;
    
    dbx = (int) (x1 + betanext);
    dby = (int) (y1 + betaadj);

    double betaadj2 = Math.sin(beta) * height * 2;
    double betanext2 = Math.cos(beta) * height * 2;

    // x2, y2
    dline.x = (int) (x1 + betanext2);
    dline.y = (int) (y1 + betaadj2);

    double gamma = Math.PI - Math.PI / 2 - beta;
    int ydiff = (int) (Math.sin(gamma) * width);
    int xdiff = (int) (Math.cos(gamma) * width);

    // left side
    dleft.x = dbx - xdiff;
    dleft.y = dby + ydiff;

    // right side
    dright.x = dbx + xdiff;
    dright.y = dby - ydiff;
  }

  public void setHighlight(boolean highlight) {
//    String color = "black";
//    if (highlight) {
//      color = "red";
//    }
//    anchorPoint.setVisibility(highlight);
//    anchorPoint.setStroke(color);
  }

  public String getText() {
    return text;
  }

//  @Override
//  public void startTextEdit(int x, int y) {
//  	super.startTextEdit(x, y);
//  	textUnderEdit = TextPosition.TEXT_MIDDLE;
//  }
//  
//  @Override
//  public void endTextEdit() {
//  	super.endTextEdit();
//  }

  public String getTextLabel() {
    return relationshipText.getLabelElement().getText();
  }

  public String getTextStart() {
    return relationshipText.getStartElement().getText();
  }

  public String getTextEnd() {
    return relationshipText.getEndElement().getText();
  }

  public boolean noText() {
    return "".equals(getTextLabel()) && "".equals(getTextStart()) && "".equals(getTextEnd());
  }

  @Override
  public String getText(int x, int y) {
  	currentTextEditLocation = relationshipText.findClickPosition(x, y, points);
  	switch (currentTextEditLocation) {
  	case START:
  		return textUtil.parseLeftText();
  	case END:
  		return textUtil.parseRightText();
  	case MIDDLE:
  		return textUtil.parseLabel();
  	}
  	return text;
  }
  
  @Override
  public void doSetText(String text, int x, int y) {
  	currentTextEditLocation = relationshipText.findClickPosition(x, y, points);
  	switch (currentTextEditLocation) {
  	case START:
  		setText(textUtil.parseLabel() + "\n" + text + textUtil.parseConnection() + textUtil.parseRightText());
  		break;
  	case END:
  		setText(textUtil.parseLabel() + "\n" + textUtil.parseLeftText() + textUtil.parseConnection() + text);
  		break;
  	case MIDDLE:
  		setText(text + "\n" + textUtil.parseArrowLine());
  		break;
  	case ALL:
  		setText(text);
//  		setText(textUtil.parseLabel() + "\n" + textUtil.parseLeftText() + text + textUtil.parseRightText());
  		break;
  	}
  }

  public void doSetText(String text) {
  	text = text.replaceAll("\\\\n", "\n");
  	text = text.replaceAll("\\\\r", ""); // windows line breaks removed
  	this.text = text;
  	
    textUtil.setText(this.text);
    RelationshipShape2 rs = (RelationshipShape2) textUtil.parseShape();	
    
//    for (int i = 0; i < lines.size(); ++i) {
    if (rs.isDependancy()) {
      relLine.setStyle(ILine.DASH);
    } else {
      relLine.setStyle(ILine.SOLID);
    }
//    }

    setShape(rs);
    relationshipText.setText(textUtil, points);
    relationshipText.applyTextColor(textColor);
    
    // notify property text area
    fireSizeChanged();
    
    // reapply border color to see dashed and solid line changes
    setBorderColor(getBorderColorAsColor());
    // applyAnnotationColors();
  }

  public void setType(String type) {
    String text = getText();
    String what = getRelationshipShape().type.getValue();
    String to = type;
    logger.debug("what => to : {} => {}", what, to);
    if (!"".equals(text)) {
      text = text.replace(what, to);
      logger.debug("replaced text {}", text);
    } else {
      // fallback to just set the value
      text = new String(to);
    }
    setText(text);
  }

  public RelationshipShape2 getRelationshipShape() {
    textUtil.setText(text);
    return (RelationshipShape2) textUtil.parseShape();
  }

  public void resizeStart(Diagram sender) {
    dragStart(sender);
  }

  public void onResize(Diagram sender, Point diff) {
    /*
     * // NOTE: too complex at the moment... what is the logic how to make
     * changes // if e.g. exceeding component boundaries, should it stay in the
     * beginning or should it // try to keep same position all the time
     * (relatively) or lets just user do the adjustment... // in case of
     * ClassElement changes if (resizeInfo.area == ResizeInfo.West) { diff.y =
     * 0; } else if (resizeInfo.area == ResizeInfo.East) { diff.x = 0; diff.y =
     * 0; } else if (resizeInfo.area == ResizeInfo.North || resizeInfo.area ==
     * ResizeInfo.South) { // x is not resized diff.x = 0; }
     * 
     * if (startAnchor.getDiagram() == sender) { // if anchor dragged => move
     * according to those Point tmp = new Point(currentshape.x1 + diff.x,
     * currentshape.y1 + diff.y); doSetShape(tmp, endPoint); } else if
     * (endAnchor.getDiagram() == sender) { // if anchor dragged => move
     * according to those Point tmp = new Point(currentshape.x12 + diff.x,
     * currentshape.y2 + diff.y); doSetShape(startPoint, tmp); }^
     */
  }

  public void resizeEnd(Diagram sender) {
    // Attach/deattach anchor
//    anchor(true);
  }

  public Info getInfo() {
    // TODO: remove separate points member variable
    info.points = points;
    fillInfo(info);
    return info;
  }

  public void setShape(Info shape) {
    RelationshipShape2 l = (RelationshipShape2) shape;
    this.info = l;
    doSetShape();
  }
  
  public void doSetShape() {
  	// TODO: optimization has been removed
  	// could compare text content to previous
  	// needs a new member variable prevText
//    if (prevPoints.equals(points)) {
      // points has not been changed so don't do anything
//      return;
//    }
//    prevPoints.clear();
//    prevPoints.addAll(points);
//    System.out.println("doSetShape:"+points+" "+anchorMap);
    relLine.setShape(points);
  	if (TouchHelpers.isSupportsTouch() && surface.getMouseDiagramManager().getDragHandler().isDragging()) {
  		// performance improvement needed on touch devices; there is not enough
  		// processing power to calculate arrow head shapes on every touch move.
      inheritance.setVisibility(false);
      arrow.setVisibility(false);
      aggregate.setVisibility(false);
  		return;
  	}
    
    int size = points.size();
    calculateArrowHead(angle, ARROW_WIDTH, points.get(size-4), points.get(size-3),
           points.get(size-2), points.get(size-1));

    calculateDiamond(angle, ARROW_WIDTH, points.get(0), points.get(1),
         points.get(2), points.get(3));
    
    relationshipText.setShape(points);

//    String color = startSelection.getVisibility() ? "blue" : "black";
//  line.setShape(start.x, start.y, end.x, end.y);
//  line.setStroke(color);

    int endx = points.get(size-2);
    int endy = points.get(size-1);
    int startx = points.get(0);
    int starty = points.get(1);
    if (info.isInheritance()) {
      inheritancePoints[0] = endx; inheritancePoints[1] = endy;
      inheritancePoints[2] = left.x; inheritancePoints[3] = left.y;
      inheritancePoints[4] = right.x; inheritancePoints[5] = right.y;
      inheritancePoints[6] = endx; inheritancePoints[7] = endy;
      inheritance.setShape(inheritancePoints);
    }
    inheritance.setVisibility(info.isInheritance());
//    inheritance.setStroke("black");
    inheritance.moveToFront();

    if (info.isDirected()) {
      arrowPoints[0] = right.x; arrowPoints[1] = right.y;
      arrowPoints[2] = endx; arrowPoints[3] = endy;
      arrowPoints[4] = left.x; arrowPoints[5] = left.y;
      arrow.setShape(arrowPoints);
    }
    arrow.setVisibility(info.isDirected());

    if (info.isAggregate()) {
      aggregatePoints[0] = startx; aggregatePoints[1] = starty;
      aggregatePoints[2] = dleft.x; aggregatePoints[3] = dleft.y;
      aggregatePoints[4] = dline.x; aggregatePoints[5] = dline.y;
      aggregatePoints[6] = dright.x; aggregatePoints[7] = dright.y;
      aggregatePoints[8] = startx; aggregatePoints[9] = starty;
      fillAggregate();
      aggregate.setShape(aggregatePoints);
    }
    aggregate.setVisibility(info.isAggregate());
//    aggregate.setStroke("black");
    aggregate.moveToFront();

    arrowStartPolyline.setShape(startx, starty);

    relationshipHandleHelpers.doSetShape(currentDragged);
  }

  private void fillAggregate() {
    if (info != null && info.isAggregate() && info.isFilled()) {
      aggregate.setFill(Theme.getCurrentColorScheme().getBorderColor().red,
                        Theme.getCurrentColorScheme().getBorderColor().green,
                        Theme.getCurrentColorScheme().getBorderColor().blue,
                        Theme.getCurrentColorScheme().getBorderColor().opacity);
    } else {
      // TODO set background color and on print set white bg color
      aggregate.setFill(255, 255, 255, 1);
    }
  }

  // ///////////////////////////////////////
  public void anchor(boolean highlight) {
    // Attach/deattach anchor
    anchorEnd(highlight);
    anchorStart(highlight);

    relationshipHandleHelpers.showConditionally(this, true);
  }

  public void anchorEnd(boolean highlight) {
    findAndAttachAnchor(points.size()-2, points.size()-1, endAnchor, highlight);
  }

  public void anchorStart(boolean highlight) {
    findAndAttachAnchor(0, 1, startAnchor, highlight);
  }

  
//  @Override
//  public void applyTransform(int dx, int dy) {
//    for (int i = 0; i < points.size(); i+=2) {
//      points.set(i, points.get(i)+dx);
//      points.set(i+1, points.get(i+1)+dy);
//    }
//    doSetShape();
//  }

  @Override
  public void setReadOnly(boolean value) {
    relLine.lineBackground.setVisibility(!value);
  }
  public Anchor getStartAnchor() {
    return startAnchor;
  }
  public Anchor getEndAnchor() {
		return endAnchor;
	}

  public int getStartPosX() {
    return 0;
  }
  public int getStartPosY() {
    return 1;
  }
  public CircleElement getEndHandler() {
    return relationshipHandleHelpers.getEndHandle();
  }
  
  public String getDefaultRelationship() {
    // doesn't yet support 
    return null;
  }
  
  public int getStartX() {
  	return points.get(0);
  }
  
  public int getStartY() {
  	return points.get(1);
  }
  
  public int getEndX() {
    return points.get(points.size()-2);
  }
  
  public int getEndY() {
    return points.get(points.size()-1);
  }
	public void reverse() {
		logger.debug("reversing...");
		List<Integer> reversedPoints = new ArrayList<Integer>();
		for (int i = points.size() - 1; i >= 0; i -= 2) {
			reversedPoints.add(points.get(i - 1));
			reversedPoints.add(points.get(i));
		}
		points = reversedPoints;
		doSetShape();
		
//		Diagram startDiagram = resetAndGetDiagram(startAnchor);
//		Diagram endDiagram = resetAndGetDiagram(endAnchor);
		
		swapStartAndEndAnchors(startAnchor, endAnchor);
		swapCustomData();
		
		ReattachHelpers rh = new ReattachHelpers();
		rh.processDiagram(this);
		rh.processDiagram(startAnchor.getDiagram());
		rh.processDiagram(endAnchor.getDiagram());
		rh.reattachRelationships();
		
		// allowed since concerns only one relationship and one client at at time does this
		// could be fine tuned to just swap targets
//		anchor(false);
		// swap start and end anchor
//		ReattachHelpers.anchorPositionToDiagram(this, getStartX(), getStartY(), startAnchor, endDiagram);
//		ReattachHelpers.anchorPositionToDiagram(this, getEndX(), getEndY(), endAnchor, startDiagram);
		
		logger.debug("reversing... done");
	}
	
	private void swapStartAndEndAnchors(Anchor startAnchor, Anchor endAnchor) {
		Anchor tmp = new Anchor(endAnchor);
		endAnchor.copyFrom(startAnchor);
		startAnchor.copyFrom(tmp);
	}

	// private Diagram resetAndGetDiagram(Anchor anchor) {
	// 	Diagram result = null;
	// 	if (anchor.getAnchorElement() != null) {
	// 		anchor.getAnchorElement().remove();
	// 		result = anchor.getDiagram();
	// 		anchor.setDiagram(null);
	// 	}
	// 	return result;
	// }

	private void swapCustomData() {
		String cd = getDiagramItem().getCustomData();
		if (cd != null && cd.indexOf(":") > 0) {
			String[] cds = cd.split(":");
			getDiagramItem().setCustomData(cds[1] + ":" + cds[0]);
		}
	}
	
	@Override
	protected void doSetShape(int[] shape) {
		points.clear();
		for (Integer i : shape) {
			points.add(i);
		}
		
		// TODO update AnchorElements as well...
		
//		removeHandles();
    // TODO are these needed? now constructing on selection
    // not when created...
//		handles.clear();
//		bendHandles.clear();
//		
//		initHandles();

		doSetShape();
	}
	
	public List<Integer> getPoints() {
		return points;
	}
	
	@Override
	public String getBorderColor() {
		return Theme.getCurrentColorScheme().getBorderColor().toHexString();
	}

	@Override
	public void restoreHighlighColor() {
		setBorderColor(getBorderColorAsColor());
    // applyAnnotationColors();
	}
  
  @Override
  public void setHighlightColor(String color) {
  	relLine.setStroke(color);
    arrow.setStroke(color);
    arrowStartPolyline.setStroke(color);
    inheritance.setStroke(color);
    aggregate.setStroke(color);
  }

  @Override
  public void setVisible(boolean visible) {
    super.setVisible(visible);
    arrow.setVisibility(visible && info.isDirected());
    arrowStartPolyline.setVisibility(visible && info.isDirectedStart());
    inheritance.setVisibility(visible && info.isInheritance());
    aggregate.setVisibility(visible && info.isAggregate());
  }
  
  @Override
  public void setBackgroundColor(int red, int green, int blue, double opacity) {
  	// relationship doesn't have bgcolor at the moment
  }
  
	@Override
	public IGroup getGroup() {
		return group;
	}
	
  @Override
  protected int doGetLeft() {
  	return Math.min(points.get(0), points.get(points.size()-2));
  }
  
  @Override
  protected int doGetTop() {
  	int result = Integer.MAX_VALUE;
  	for (int i = 1; i < points.size(); i += 2) {
  		result = Math.min(result, points.get(i));
  	}
  	return result;
  }

  @Override
  public int getWidth() {
    return DiagramHelpers.getWidth(points);
  }

  @Override
  public int getHeight() {
    return DiagramHelpers.getHeight(points);
  }
  
	@Override
	public int getTextAreaLeft() {
		switch (currentTextEditLocation) {
		case START:
			return relationshipText.getStartElement().getX() - getTextAreaWidth() / 2;
		case END:
			return relationshipText.getEndElement().getX() - getTextAreaWidth() / 2;
		case MIDDLE:
			return relationshipText.getLabelElement().getX();
		}
		return 0;
	}
	
	@Override
	public int getTextAreaTop() {
		switch (currentTextEditLocation) {
		case START:
			return relationshipText.getStartElement().getY() - TextElementFormatUtil.ROW_HEIGHT + 5;
		case END:
			return relationshipText.getEndElement().getY() - TextElementFormatUtil.ROW_HEIGHT + 5;
		case MIDDLE:
			return relationshipText.getLabelElement().getY() - TextElementFormatUtil.ROW_HEIGHT + 5;
		}
		return 0;
	}
	
	@Override
	public int getTextAreaWidth() {
		String[] lines = text.split("\\n");
		int widest = 0;
		for (String line : lines) {
			if (line.length() > widest) {
				widest = line.length();
			}
		}
		return widest * 5 + 30; // some magic char length + some margin
	}
	
	@Override
	public int getTextAreaHeight() {
		return TextElementFormatUtil.ROW_HEIGHT;
	}
  
	public String getTextAreaBackgroundColor() {
		return "transparent"; // other wise looks little bit funny with rect background
	}
	
	@Override
	public String getBackgroundColor() {
		return "transparent";
	}
	
	@Override
	public String getTextAreaAlign() {
		return "center";
	}
	
	@Override
	public void showText() {
		if (currentTextEditLocation != null) {
			switch (currentTextEditLocation) {
			case START:
				relationshipText.getStartElement().setVisibility(true);
				break;
			case END:
				relationshipText.getEndElement().setVisibility(true);
				break;
			case MIDDLE:
				relationshipText.getLabelElement().setVisibility(true);
				break;
			}
		}
	}
	
	@Override
	public void hideText() {
		switch (currentTextEditLocation) {
		case START:
			relationshipText.getStartElement().setVisibility(false);
			break;
		case END:
			relationshipText.getEndElement().setVisibility(false);
			break;
		case MIDDLE:
			relationshipText.getLabelElement().setVisibility(false);
			break;
		}
		
		relationshipHandleHelpers.forceHide();
	}
	
  @Override
  public boolean supportsTextEditing() {
  	return true;
  }
  
  @Override
  public int supportedMenuItems() {
  	return ContextMenuItem.REVERSE_CONNECTION_MENU.getValue();
  }
  
  @Override
  public String getCustomData() {
  	return StringUtil.stringOrEmpty(getStartClientId()) + ":" 
  					+ StringUtil.stringOrEmpty(getEndClientId());
  }

  public void applyCustomData() {
    parseCustomData(getDiagramItem().getCustomData());
  }

  /**
  * This is not absolutely necessary and could be removed, since
  * runtime model for end client ids are anchors that are set using anchor
  * when dragging relationship as a whole, e.g. in case of seq diagram connection
  * reposition.
  */
  public void applyAnchor(Anchor anchor) {
    // if (anchor == startAnchor) {
    //   setAnchorClientId()
    //   startAnchor.setClientId(anchor.getAnchorElement().getSource().getDiagramItem().getClientId());
    // } else if (anchor == endAnchor) {
    //   endAnchor.setClientId(anchor.getAnchorElement().getSource().getDiagramItem().getClientId());
    // }

    anchor.setClientId(anchor.getAnchorElement().getSource().getDiagramItem().getClientId());

    // apply anchor cliend id handles to model
    getDiagramItem().setCustomData(getCustomData());
  }
  
  @Override
  public void parseCustomData(String customData) {
  	String[] se = new String[]{};
  	if (customData != null) {
  		se = customData.split(":");	
  	}
  	
  	if ("".equals(customData) || se.length == 0) {
  		// legacy implementation calculates anchor always when loaded => flag it to be calculated
  		// later
      if (getDiagramItem().getVersion() <= 3) {
        // data format after that is no longer a legacy
        legacyAnchor = true;
      }
  	} else if (se.length == 1 && customData.startsWith(":")) {
      getEndAnchor().setClientId(se[0]);
    } else if (se.length == 1 && customData.endsWith(":")) {
      getStartAnchor().setClientId(se[0]);
    } else if (se.length == 2) {
  		getStartAnchor().setClientId(se[0]);
  		getEndAnchor().setClientId(se[1]);
  	}
  }
  
	public String getStartClientId() {
		return getStartAnchor().getClientId();
	}
	public String getEndClientId() {
		return getEndAnchor().getClientId();
	}
	
	public boolean isLegacyAnchor() {
		return legacyAnchor;
	}

	public void hideAllHandles() {
		relationshipHandleHelpers.forceHide();
	}

  public void calculateHandles() {
    relationshipHandleHelpers.showConditionally(this, false);
  }
  
  @Override
  protected void applyTextColor() {
    relationshipText.applyTextColor(textColor);
  }
  
  @Override
  public boolean isTextElementBackgroundTransparent() {
    return true;
  }
  
  @Override
  public boolean usesSchemeDefaultColors(ElementColorScheme colorScheme) {
    // At the moment it is not possible to change any colors for relationship
    // though this needs to be changed. And now new color is actually stored for
    // relationship on theme change
    return true;
  }

  public AnchorMoveHandler getAnchorMoveHandler() {
    return handler;
  }

}
