package net.sevenscales.editor.uicomponents.uml;

import java.util.ArrayList;
import java.util.List;

import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.domain.utils.StringUtil;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.impl.Theme;
import net.sevenscales.editor.api.impl.TouchHelpers;
import net.sevenscales.editor.api.impl.Theme.ElementColorScheme;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.content.ui.ContextMenuItem;
import net.sevenscales.editor.content.utils.AreaUtils;
import net.sevenscales.editor.content.utils.DiagramHelpers;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.DiagramDragHandler;
import net.sevenscales.editor.diagram.DiagramResizeHandler;
import net.sevenscales.editor.diagram.shape.Info;
import net.sevenscales.editor.diagram.shape.RelationshipShape2;
import net.sevenscales.editor.diagram.shape.TextShape;
import net.sevenscales.editor.diagram.utils.ReattachHelpers;
import net.sevenscales.editor.diagram.utils.PathFitter;
import net.sevenscales.editor.diagram.utils.BezierHelpers;
import net.sevenscales.editor.gfx.domain.IGroup;
import net.sevenscales.editor.gfx.domain.ILine;
import net.sevenscales.editor.gfx.domain.IPolyline;
import net.sevenscales.editor.gfx.domain.IPath;
import net.sevenscales.editor.gfx.domain.IShape;
import net.sevenscales.editor.gfx.domain.IShapeFactory;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.uicomponents.AbstractDiagramItem;
import net.sevenscales.editor.diagram.drag.Anchor;
import net.sevenscales.editor.diagram.drag.AnchorElement;
import net.sevenscales.editor.diagram.drag.AnchorMoveHandler;
import net.sevenscales.editor.diagram.drag.ConnectionMoveHandler;
import net.sevenscales.editor.uicomponents.AngleUtil2;
import net.sevenscales.editor.uicomponents.AnchorUtils;
import net.sevenscales.editor.uicomponents.CardinalDirection;
import net.sevenscales.editor.uicomponents.CircleElement;
import net.sevenscales.editor.gfx.domain.Point;
import net.sevenscales.editor.gfx.domain.PointDouble;
import net.sevenscales.editor.uicomponents.RelationshipText2;
import net.sevenscales.editor.uicomponents.RelationshipText2.ClickTextPosition;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil.AbstractHasTextElement;
import net.sevenscales.editor.uicomponents.helpers.ConnectionHelpers;
import net.sevenscales.editor.uicomponents.helpers.IConnectionHelpers;
import net.sevenscales.editor.uicomponents.helpers.RelationshipHandleHelpers;
import net.sevenscales.editor.uicomponents.impl.RelationshipTextUtil2;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.domain.ShapeProperty;
import net.sevenscales.editor.api.event.PotentialOnChangedEvent;
import net.sevenscales.editor.gfx.domain.IRelationship;
import net.sevenscales.editor.gfx.domain.IParentElement;
import net.sevenscales.editor.gfx.domain.IChildElement;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;


public class Relationship2 extends AbstractDiagramItem implements DiagramDragHandler, DiagramResizeHandler,
  IRelationship, IParentElement {
	private static final SLogger logger = SLogger.createLogger(Relationship2.class);

  private static final Color legacyBorderColor = new Color(0x51, 0x51, 0x51, 1);

  // Debug curve control point and arrow angle debugging
  // private net.sevenscales.editor.gfx.domain.ICircle tempCircle;
  // private net.sevenscales.editor.gfx.domain.ICircle tempC1;
  // private net.sevenscales.editor.gfx.domain.ICircle tempC2;

	private IPolyline inheritance;
  private IPolyline arrow;
  private IPolyline aggregate;
  private ArrowStartPolyline arrowStartPolyline;
  private RelationshipText2 relationshipText;
//  private List<IShape> elements = new ArrayList<IShape>();

  // diamond information
  private PointDouble dright = new PointDouble();
  private PointDouble dleft = new PointDouble();
  private PointDouble dline = new PointDouble();
  private double dbx; // to position start text correctly
  private double dby;

  // arrow head information
  private PointDouble right = new PointDouble();
  private PointDouble left = new PointDouble();
  private static final int SELECTION_AREA_WIDTH;
  private static final int ARROW_WIDTH = 6;
  private double angle = 30;

  private Anchor endAnchor;
  private Anchor startAnchor;
  private List<Integer> points;
//  private List<Integer> prevPoints = new ArrayList<Integer>();
  private String text;
  private RelationshipTextUtil2 textUtil;
  private double[] inheritancePoints;
  private double[] arrowPoints;
  private double[] aggregatePoints;
  
  private RelationshipShape2 info;
  private RelLine relLine;
  private Diagram currentDragged;
//	protected boolean followUpChange;
	private IGroup group;
	private ClickTextPosition currentTextEditLocation;
	private boolean legacyAnchor;
  private RelationshipHandleHelpers relationshipHandleHelpers;
  private AnchorMoveHandler handler;

  private List<IChildElement> children;

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

    void createIfNullAndDirectedStart() {
      if (parent.info != null && parent.info.isDirectedStart()) {
        createIfNull();
      }
    }

    void setStroke(String color) {
      createIfNullAndDirectedStart();
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
    private IPath path;
    private IPath lineBackground;
    private List<IShape> elements = new ArrayList<IShape>();
    private JsArray<BezierHelpers.Segment> segments;

    public RelLine() {
      path = IShapeFactory.Util.factory(editable).createPath(group, null);
      path.setStroke(Theme.getCurrentColorScheme().getBorderColor().toHexString());
      // do not fill polyline, because it will be selectable area and hides everything under it!
//      polyline.setFill(150, 150, 150, 0.5);
      lineBackground = IShapeFactory.Util.factory(editable).createPath(group, null);
      lineBackground.setStroke(51, 51, 51, 0);
      lineBackground.setStrokeWidth(10);
      
      elements.add(path);
      elements.add(lineBackground);
//      setShape(points);
    }
    
    public List<IShape> getShapes() {
      return elements;
    }
    
    public void setStrokeStyle(String style) {
      path.setStrokeStyle(style);
    }

    public void setShape(List<Integer> points) {
      BezierHelpers.Segment result = null;
      String shape = "";
      if (points.size() == 4 || !info.isCurved()) {
        shape = calcTwoPointsCurvePath(points);
      } else {
        segments = BezierHelpers.segments(points);
        shape = BezierHelpers.smooth(segments);
      }

      // logger.debug("RelLine.setShape {}", shape);

      path.setShape(shape);
      lineBackground.setShape(shape);
    }

    public BezierHelpers.Segment getLastSegment() {
      return BezierHelpers.lastSegment(segments);
    }

    public BezierHelpers.Segment getFirstSegment() {
      return BezierHelpers.firstSegment(segments);
    }

    private String calcTwoPointsCurvePath(List<Integer> points) {
      String result = "M";
      for (int i = 0; i < points.size(); i += 2) {
        if (i > 0) {
          result += " ";
        }
        int x = points.get(i);
        int y = points.get(i + 1);
        result += x + "," + y;

        // M100,200 C100,300 200,300          200,400
        if (info.isCurved()) {
          result += calcCurve(points, i, x, y);
        }
      }
      return result;
    }
    private String calcCurve(List<Integer> points, int i, int prevx, int prevy) {
      String result = "";
      if (i + 3 < points.size()) {
        // C100,300 200,300
        int endx = points.get(i + 2);
        int endy = points.get(i + 3);

        ControlPoint c = createCurve(prevx, prevy, endx, endy);
        BezierHelpers.Segment seg = BezierHelpers.createSegment(prevx, prevy, c.c1x, c.c1y, c.c2x, c.c2y, endx, endy);
        segments = BezierHelpers.createSegments(seg);
        result = " C" + c.c1x + "," + c.c1y + " " + c.c2x + "," + c.c2y + " ";
      }
      return result;
    }

    private String calcMultiPointsCurvePath(List<Integer> points) {
      return BezierHelpers.smooth(points);
    }

    public void setStroke(String color) {
      path.setStroke(color);
    }

		public void moveBackgroundToBack() {
			lineBackground.moveToBack();
		}

  }

  private static class ControlPoint {
    public double c1x;
    public double c1y;
    public double c2x;
    public double c2y;

    public ControlPoint() {

    }

    public ControlPoint(double c1x, double c1y, double c2x, double c2y) {
      this.c1x = c1x;
      this.c1y = c1y;
      this.c2x = c2x;
      this.c2y = c2y;
    }
  }

  private ControlPoint createCurve(double prevx, double prevy, double endx, double endy) {
    ControlPoint result = new ControlPoint();
    double mx = (prevx + endx) / 2;
    double my = (prevy + endy) / 2;
    CardinalDirection cardinal1 = getCardinal(getStartX(), getStartY(), getStartAnchor());
    CardinalDirection cardinal2 = getCardinal(getEndX(), getEndY(), getEndAnchor());

    Diagram d1 = getStartAnchor().getDiagram();
    Diagram d2 = getEndAnchor().getDiagram();
    if (d1 != null && d2 != null && handleSequenceDiagram(getStartAnchor(), getEndAnchor(), result, prevx, prevy, mx, my, endx, endy)) {

    } else if (d1 != null && d2 != null && cardinal1 != null && cardinal2 != null && 
        isConnectedDifferentSideEdges(getStartAnchor(), getEndAnchor(), cardinal1, cardinal2,
                                      result, prevx, prevy, mx, my, endx, endy)) {
    } else if (d1 != null && d2 != null && cardinal1 != null && cardinal2 != null && 
               isConnectSameSideEdges(getStartAnchor(), getEndAnchor(), cardinal1, cardinal2,
                                      result, prevx, prevy, mx, my, endx, endy)) {
    } else if (d1 != null && d2 != null && cardinal1 != null && cardinal2 != null && 
               isConnectedToSameNorthOrSouthEdges(getStartAnchor(), getEndAnchor(), cardinal1, cardinal2,
                                                  result, prevx, prevy, mx, my, endx, endy)) {
    } else if (d1 != null && d2 != null && cardinal1 != null && cardinal2 != null && 
               isNorthOrSouthEdgeConnectedToWestOrEast(getStartAnchor(), getEndAnchor(), cardinal1, cardinal2,
                                                  result, prevx, prevy, mx, my, endx, endy)) {
    } else {
      result.c1x = prevx;
      result.c1y = my;
      result.c2x = endx;
      result.c2y = my;
    }
    return result;
  }

  private boolean handleSequenceDiagram(Anchor a1, Anchor a2, ControlPoint curve, double prevx, double prevy, double mx, double my, double endx, double endy) {
    boolean result = false;
    Diagram d1 = a1.getDiagram();
    Diagram d2 = a2.getDiagram();
    if (d1 != null && d2 != null && d1 == d2 && d1.isSequenceElement()) {
      // self reference
      curve.c1x = prevx + 80;
      curve.c1y = prevy;
      curve.c2x = endx + 80;
      curve.c2y = endy;
      result = true;
    } else if (d1 != null && d2 != null && d1.isSequenceElement() && d2.isSequenceElement()) {
      curve.c1x = mx;
      curve.c1y = prevy;
      curve.c2x = mx;
      curve.c2y = endy;
      result = true;
    }
    return result;
  }

  private boolean isClosestSideEdgesConnected(Anchor a1, Anchor a2, CardinalDirection cd1, CardinalDirection cd2) {
    boolean result = false;
    if (cd1 != null && cd2 != null) {
      Diagram d1 = a1.getDiagram();
      Diagram d2 = a2.getDiagram();
      if (cd1.equals(CardinalDirection.WEST) && cd2.equals(CardinalDirection.EAST)) {
        int westPos = d1.getLeft();
        int eastPos = d2.getLeft() + d2.getWidth();
        result = eastPos < westPos;
      } else if (cd1.equals(CardinalDirection.EAST) && cd2.equals(CardinalDirection.WEST)) {
        int eastPos = d1.getLeft() + d1.getWidth();
        int westPos = d2.getLeft();
        result = westPos > eastPos;
      }
    }
    return result;
  }

  private boolean isClosestTopBottomEdgesConnected(Anchor a1, Anchor a2, CardinalDirection cd1, CardinalDirection cd2) {
    boolean result = false;
    if (cd1 != null && cd2 != null) {
      Diagram d1 = a1.getDiagram();
      Diagram d2 = a2.getDiagram();
      if (cd1.equals(CardinalDirection.NORTH) && cd2.equals(CardinalDirection.SOUTH)) {
        int northPos = d1.getTop();
        int southPos = d2.getTop() + d2.getHeight();
        result = northPos > southPos;
      } else if (cd1.equals(CardinalDirection.SOUTH) && cd2.equals(CardinalDirection.NORTH)) {
        int northPos = d1.getTop();
        int southPos = d2.getTop() + d1.getHeight();
        result = northPos > southPos;
      }
    }
    return result;
  }


  private boolean isLeftToRight(Anchor a1, Anchor a2, CardinalDirection cd1, CardinalDirection cd2) {
    Diagram d1 = a1. getDiagram();
    Diagram d2 = a2.getDiagram();
    boolean result = false;
    if (cd1.equals(CardinalDirection.WEST) && cd2.equals(CardinalDirection.EAST)) {
      int westPos = d1.getLeft();
      int eastPos = d2.getLeft() + d2.getWidth();
      result = westPos < eastPos;
    }
    return result;
  }

  private boolean isConnectedDifferentSideEdges(Anchor a1, Anchor a2, CardinalDirection cd1, CardinalDirection cd2,
    ControlPoint curve, double prevx, double prevy, double mx, double my, double endx, double endy) {
    boolean result = false;
    if (isClosestSideEdgesConnected(a1, a2, cd1, cd2)) {
      curve.c1x = mx;
      curve.c1y = prevy;
      curve.c2x = mx;
      curve.c2y = endy;
      result = true;
    } else if (cd1 != null && cd2 != null && eastToWest(cd1, cd2)) {
      if (isLeftToRight(getStartAnchor(), getEndAnchor(), cd1, cd2)) {
        curve.c1x = prevx - 80;
        curve.c1y = my;
        curve.c2x = endx + 80;
        curve.c2y = my;
      } else {
        curve.c1x = prevx + 80;
        curve.c1y = my;
        curve.c2x = endx - 80;
        curve.c2y = my;
      }
      result = true;
    }

    return result;
  }

  private boolean eastToWest(CardinalDirection cd1, CardinalDirection cd2) {
    return (cd1.equals(CardinalDirection.EAST) && cd2.equals(CardinalDirection.WEST)) ||
           (cd2.equals(CardinalDirection.EAST) && cd1.equals(CardinalDirection.WEST));
  }

  private int distanceWithFactorial(Diagram d1, Diagram d2) {
    if (d1 != null && d2 != null) {
      final int defaultDistance = 70;
      int d = Math.abs((d1.getLeft() + d1.getWidth()) - (d2.getLeft() + d2.getWidth()));
      float distFactor = 0.3f;
      return (int) defaultDistance + (int) (distFactor * d);
    }
    return 0;
  }

  private int distanceWithFactorial(double distance) {
    final int defaultDistance = 70;
    float distFactor = 0.3f;
    return (int) defaultDistance + (int) (distFactor * Math.abs(distance));
  }

  private boolean isConnectSameSideEdges(Anchor a1, Anchor a2, CardinalDirection cd1, CardinalDirection cd2,
    ControlPoint curve, double prevx, double prevy, double mx, double my, double endx, double endy) {
    boolean result = false;
    // 80 could be distance related with some factor, the bigger distance => bigger curve
    if (cd1.equals(CardinalDirection.EAST) && cd2.equals(CardinalDirection.EAST)) {
      int distance = distanceWithFactorial(a1.getDiagram(), a2.getDiagram());
      curve.c1x = prevx + distance;
      curve.c1y = prevy;
      curve.c2x = endx + distance;
      curve.c2y = endy;
      result = true;
    } else if (cd1.equals(CardinalDirection.WEST) && cd2.equals(CardinalDirection.WEST)) {
      int distance = distanceWithFactorial(a1.getDiagram(), a2.getDiagram());
      curve.c1x = prevx - distance;
      curve.c1y = prevy;
      curve.c2x = endx - distance;
      curve.c2y = endy;
      result = true;
    }
    return result;
  }

  private boolean isConnectedToSameNorthOrSouthEdges(Anchor a1, Anchor a2, CardinalDirection cd1, CardinalDirection cd2,
    ControlPoint curve, double prevx, double prevy, double mx, double my, double endx, double endy) {
    boolean result = false;
    boolean endLeftSide = endx < prevx;
    boolean endAbove = endy < prevy;

    // 80 could be distance related with some factor, the bigger distance => bigger curve
    if (cd1.equals(CardinalDirection.NORTH) && cd2.equals(CardinalDirection.NORTH)) {
      // int distance = distanceWithFactorial(a1.getDiagram(), a2.getDiagram());
      int distance = 80;
      curve.c1x = prevx;
      curve.c1y = prevy - distance;
      curve.c2x = endx;
      curve.c2y = endy - distance;
      result = true;
    } else if (cd1.equals(CardinalDirection.SOUTH) && cd2.equals(CardinalDirection.SOUTH)) {
      // int distance = distanceWithFactorial(a1.getDiagram(), a2.getDiagram());
      int distance = 80;
      curve.c1x = prevx;
      curve.c1y = prevy + distance;
      curve.c2x = endx;
      curve.c2y = endy + distance;
      result = true;
    } else if (cd1.equals(CardinalDirection.NORTH) && cd2.equals(CardinalDirection.SOUTH)) {
      boolean closestEdgesConnected = isClosestTopBottomEdgesConnected(a1, a2, cd1, cd2);
      curve.c1x = prevx;
      curve.c1y = closestEdgesConnected ? my : prevy - 80;
      curve.c2x = endx;
      curve.c2y = closestEdgesConnected ? my : endy + 80;
      result = true;
    } else if (endAbove && cd1.equals(CardinalDirection.SOUTH) && cd2.equals(CardinalDirection.NORTH)) {
      // boolean closestEdgesConnected = isClosestEdgesConnected(a1, a2, cd1, cd2);
      curve.c1x = prevx;
      curve.c1y = prevy + 80;
      curve.c2x = endx;
      curve.c2y = endy - 80;
      result = true;
    }
    return result;
  }

  private boolean isNorthOrSouthEdgeConnectedToWestOrEast(Anchor a1, Anchor a2, CardinalDirection cd1, CardinalDirection cd2,
    ControlPoint curve, double prevx, double prevy, double mx, double my, double endx, double endy) {
    boolean result = false;
    boolean endLeftSide = endx < prevx;
    boolean endAbove = endy < prevy;

    // 80 could be distance related with some factor, the bigger distance => bigger curve
    if (cd1.equals(CardinalDirection.SOUTH) && cd2.equals(CardinalDirection.WEST)) {
      curve.c1x = endLeftSide ? mx : prevx;
      curve.c1y = endAbove ? prevy + 80 :  my;
      curve.c2x = endLeftSide ? endx - 80 : mx;
      curve.c2y = endLeftSide ? my : endy;
      result = true;
    } else if (cd1.equals(CardinalDirection.WEST) && cd2.equals(CardinalDirection.SOUTH)) {
      curve.c1x = prevx - 80;
      curve.c1y = endAbove ? (endLeftSide ? prevy : my) : my;
      curve.c2x = endAbove ? endx : mx;
      curve.c2y = endy + 80;
      result = true;
    } else if (cd1.equals(CardinalDirection.EAST) && cd2.equals(CardinalDirection.SOUTH)) {
      int distance = distanceWithFactorial(prevx - endx);
      int distancey = distanceWithFactorial(prevy - endy);
      curve.c1x = endLeftSide ? prevx + distance : mx;
      curve.c1y = endLeftSide ? my : prevy;
      curve.c2x = endx;
      curve.c2y = endAbove ? my : endy + distancey;
      result = true;
    } else if (cd1.equals(CardinalDirection.SOUTH) && cd2.equals(CardinalDirection.EAST)) {
      int distance = distanceWithFactorial(prevx - endx);
      int distancey = distanceWithFactorial(prevy - endy);
      curve.c1x = prevx;
      curve.c1y = endAbove ? prevy + distancey : my;
      curve.c2x = endLeftSide ? mx : endx + distance;
      curve.c2y = endLeftSide ? endy : my;
      result = true;
    } else if (cd1.equals(CardinalDirection.SOUTH) && cd2.equals(CardinalDirection.SOUTH)) {
      // int distance = distanceWithFactorial(a1.getDiagram(), a2.getDiagram());
      int distance = 80;
      curve.c1x = prevx;
      curve.c1y = prevy + distance;
      curve.c2x = endx;
      curve.c2y = endy + distance;
      result = true;
    } else if (endLeftSide && cd1.equals(CardinalDirection.NORTH) && cd2.equals(CardinalDirection.WEST)) {
      int distance = distanceWithFactorial(prevx - endx);
      curve.c1x = prevx;
      curve.c1y = endAbove ? my : prevy - 80;
      curve.c2x = endx - distance;
      curve.c2y = endy;
      result = true;
    } else if (cd1.equals(CardinalDirection.NORTH) && cd2.equals(CardinalDirection.WEST)) {
      curve.c1x = prevx;
      curve.c1y = endAbove ? my : prevy - 80;
      curve.c2x = mx;
      curve.c2y = endy;
      result = true;
    } else if (endLeftSide && cd1.equals(CardinalDirection.WEST) && cd2.equals(CardinalDirection.NORTH)) {
      curve.c1x = mx;
      curve.c1y = prevy;
      curve.c2x = endx;
      curve.c2y = endAbove ? endy - 80 : my;
      result = true;
    } else if (cd1.equals(CardinalDirection.WEST) && cd2.equals(CardinalDirection.NORTH)) {
      int distance = distanceWithFactorial(prevx - endx);
      curve.c1x = prevx - distance;
      curve.c1y = prevy;
      curve.c2x = endx;
      curve.c2y = endAbove ? endy - 80 : my;
      result = true;
    } else if (endAbove && cd1.equals(CardinalDirection.NORTH) && cd2.equals(CardinalDirection.EAST)) {
      curve.c1x = prevx;
      curve.c1y = my;
      curve.c2x = endLeftSide ? mx : endx + 80;
      curve.c2y = endLeftSide ? endy : my;
      result = true;
    } else if (cd1.equals(CardinalDirection.NORTH) && cd2.equals(CardinalDirection.EAST)) {
      int distance = distanceWithFactorial(prevy - endy);
      int distancex = distanceWithFactorial(prevx - endx);
      curve.c1x = prevx;
      curve.c1y = prevy - distance;
      curve.c2x = endLeftSide ? mx : endx + distancex;
      curve.c2y = endy;
      result = true;
    } else if (endAbove && cd1.equals(CardinalDirection.EAST) && cd2.equals(CardinalDirection.NORTH)) {
      int distance = distanceWithFactorial(prevy - endy);
      int distancex = distanceWithFactorial(prevx - endx);
      curve.c1x = endLeftSide ? prevx + distancex : mx;
      curve.c1y = prevy;
      curve.c2x = endx;
      curve.c2y = endy - distance;
      result = true;
    } else if (cd1.equals(CardinalDirection.EAST) && cd2.equals(CardinalDirection.NORTH)) {
      curve.c1x = endLeftSide ? prevx + 80 : mx;
      curve.c1y = endLeftSide ? my : prevy;
      curve.c2x = endx;
      curve.c2y = my;
      result = true;
    }
    return result;
  }

  private boolean eastToTop(CardinalDirection cd1, CardinalDirection cd2) {
    return (cd1.equals(CardinalDirection.EAST) && cd2.equals(CardinalDirection.NORTH)) ||
           (cd2.equals(CardinalDirection.EAST) && cd1.equals(CardinalDirection.NORTH));
  }

  private boolean bottomToSide(CardinalDirection cd1, CardinalDirection cd2) {
    return (cd1.equals(CardinalDirection.EAST) && cd2.equals(CardinalDirection.SOUTH)) ||
           (cd2.equals(CardinalDirection.EAST) && cd1.equals(CardinalDirection.SOUTH));
  }

  private boolean sideToBottomOrTop(CardinalDirection cd1, CardinalDirection cd2) {
    return (cd1.equals(CardinalDirection.EAST) || cd1.equals(CardinalDirection.WEST)) &&
           (cd2.equals(CardinalDirection.NORTH) || cd2.equals(CardinalDirection.SOUTH)) ||
           (cd2.equals(CardinalDirection.EAST) || cd2.equals(CardinalDirection.WEST)) &&
           (cd1.equals(CardinalDirection.NORTH) || cd1.equals(CardinalDirection.SOUTH));
  }

  private boolean bottomToBottom(CardinalDirection cd1, CardinalDirection cd2) {
    return cd1.equals(CardinalDirection.SOUTH) && cd2.equals(CardinalDirection.SOUTH);
  }

  private CardinalDirection getCardinal(int x, int y, Anchor anchor) {
    AnchorElement ae = anchor.getAnchorElement();
    if (ae != null) {
      if (surface.getEditorContext().isTrue(EditorProperty.ON_OT_OPERATION)) {
        updateCardinalDirection(x, y, ae);
      }
      return ae.getCardinalDirection();
    }
    return null;
  }

  public Relationship2(ISurfaceHandler surface, RelationshipShape2 points, String text, Color backgroundColor, Color borderColor, Color textColor, boolean editable, IDiagramItemRO item) {
    super(editable, 
          surface, 
          backgroundColor,
          borderColor,
          textColor,
          // Relationship2.fixLegacyBackgroundColor(backgroundColor, item), 
          // Relationship2.fixLegacyBorderColor(borderColor, item), 
          // Relationship2.fixLegacyTextColor(textColor, item), 
          item);
    this.points = points.points;
    handler = new ConnectionMoveHandler();
    children = new ArrayList<IChildElement>();
    group = IShapeFactory.Util.factory(editable).createGroup(surface.getConnectionLayer());

    // DEBUG curve visualization START
    // tempCircle = IShapeFactory.Util.factory(editable).createCircle(group);
    // tempC1 = IShapeFactory.Util.factory(editable).createCircle(group);
    // tempC2 = IShapeFactory.Util.factory(editable).createCircle(group);
    // DEBUG curve visualization END

    startAnchor = new Anchor(this);
    endAnchor = new Anchor(this);
    
    startAnchor.setTheOtherEnd(endAnchor);
    endAnchor.setTheOtherEnd(startAnchor);
    
//    anchorPoint = new Circle(surface.getSurface());
//    anchorPoint.setStrokeWidth(1);
//    anchorPoint.setVisibility(false);

    textUtil = new RelationshipTextUtil2();
    relationshipText = new RelationshipText2(group, surface, editable);

    inheritancePoints = new double[8];
    inheritance = IShapeFactory.Util.factory(editable).createPolyline(group, inheritancePoints);
    // inheritance.setFill(255, 255, 255, 1);
    // inheritance.setFill(Theme.getCurrentThemeName().getBoardBackgroundColor());
    
    int endx = this.points.get(this.points.size()-2);
    int endy = this.points.get(this.points.size()-1);
    arrowPoints = new double[]{
          right.x, right.y,
          endx, endy, 
          left.x, left.y
        };
    arrow = IShapeFactory.Util.factory(editable).createPolyline(group, arrowPoints);
    arrow.setFill(255, 255, 255, 0);

    arrowStartPolyline = new ArrowStartPolyline(this);
    
    aggregatePoints = new double[10];
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
    
    // setBorderColor(Theme.getCurrentColorScheme().getBorderColor());

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
	  
    saveLastTransform();
  	doSetShape();
  }

  @Override
  public void snapshotTransformations() {
    super.snapshotTransformations();
    for (IChildElement child : children) {
      child.snapshotTransformations();
    }
  }

  @Override
  public void setTransform(int dx, int dy) {
    super.setTransform(dx, dy);
    setChildrenTransform(dx, dy);
  }

  private void setChildrenTransform(int dx, int dy) {
    for (IChildElement child : children) {
      // relationship resets group transformation, so need to get child own cumulative
      // transformation
      child.setTransform(dx + child.getSnaphsotTransformX(), dy + child.getSnaphsotTransformY());
    }
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

  public void removePoint(int index) {
    if (index > 0 && index < points.size() - 2 && (index * 2 + 1) < points.size()) {
      // do not allow to remove first or last point
      // NOTE order is important or would remove wrong y (actually x) if reversed
      points.remove(index * 2 + 1);
      points.remove(index * 2);
    }
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
    Relationship2 result = new Relationship2(surface, new RelationshipShape2(ps), getText(), new Color(backgroundColor), new Color(borderColor), new Color(textColor), getEditable(), new DiagramItemDTO());
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
      double x1, double y1, double x2, double y2) {
    angle = angle * Math.PI / 180;
    double beta = AngleUtil2.beta(x1, y1, x2, y2);
    double height = width / Math.tan(angle);

    // calculate base
    double betaadj = Math.sin(beta) * height;
    double betanext = Math.cos(beta) * height;

    // System.out.println("beta:" + Math.toDegrees(beta) + " betaadj:" + betaadj
    // + " betanext:" + betanext);

    double bx = (points.get(points.size()-2) + betanext);
    double by = (points.get(points.size()-1) + betaadj);

    double gamma = Math.PI - Math.PI / 2 - beta;

    double ydiff = (Math.sin(gamma) * width);
    double xdiff = (Math.cos(gamma) * width);

    // left side
    left.x = bx - xdiff;
    left.y = by + ydiff;

    // right side
    right.x = bx + xdiff;
    right.y = by - ydiff;
  }

  private void calculateDiamond(double angle, int width,
      double x1, double y1, double x2, double y2) {
    angle = angle * Math.PI / 180;
    double beta = AngleUtil2.beta(x1, y1, x2, y2) + Math.PI;
    double height = width / Math.tan(angle);

    // calculate base
    double betaadj = Math.sin(beta) * height;
    double betanext = Math.cos(beta) * height;
    
    dbx = (x1 + betanext);
    dby = (y1 + betaadj);

    double betaadj2 = Math.sin(beta) * height * 2;
    double betanext2 = Math.cos(beta) * height * 2;

    // x2, y2
    dline.x = (x1 + betanext2);
    dline.y = (y1 + betaadj2);

    double gamma = Math.PI - Math.PI / 2 - beta;
    double ydiff = (Math.sin(gamma) * width);
    double xdiff = (Math.cos(gamma) * width);

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

  public Diagram showEditorForDiagram(int screenX, int screenY) {
    // creates always a new child text element
    return createChildLabel(screenX, screenY);
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
      // if (labelChildText != null) {
      //   return labelChildText.getText();
      // } else {
      //   return "";
      // }
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
      // setText(textUtil.parseArrowLine());
      // createChildLabel(text);
  		break;
  	case ALL:
  		setText(text);
//  		setText(textUtil.parseLabel() + "\n" + textUtil.parseLeftText() + text + textUtil.parseRightText());
  		break;
  	}
  }

  private ChildTextElement createChildLabel(int screenX, int screenY) {
    TextShape ts = new TextShape(screenX, screenY, 100, 30);
    DiagramItemDTO tdto = new DiagramItemDTO();
    tdto.setParentId(getDiagramItem().getClientId());
    ChildTextElement result = new ChildTextElement(surface, 
                                          ts, 
                                          Theme.getCurrentColorScheme().getBackgroundColor(), 
                                          Theme.getCurrentColorScheme().getBorderColor(), 
                                          Theme.getCurrentColorScheme().getTextColor(), 
                                          "",
                                          editable, 
                                          tdto, 
                                          this);
    surface.addAsSelected(result, true);
    return result;
  }

  public boolean isCurved() {
    Integer props = getDiagramItem().getShapeProperties();
    if (props != null && ShapeProperty.isCurvedArrow(props)) {
      return true;
    }
    return false;
  }

  public JsArray<BezierHelpers.Segment> getSegments() {
    return relLine.segments;
  }

  public void doSetText(String text) {
  	text = text.replaceAll("\\\\n", "\n");
  	text = text.replaceAll("\\\\r", ""); // windows line breaks removed
  	this.text = text;
  	
    textUtil.setText(this.text);
    RelationshipShape2 rs = (RelationshipShape2) textUtil.parseShape(isCurved());
    
//    for (int i = 0; i < lines.size(); ++i) {
    if (rs.isDependancy()) {
      relLine.setStrokeStyle(ILine.DASH);
    } else {
      relLine.setStrokeStyle(ILine.SOLID);
    }
//    }

    setShape(rs);
    relationshipText.setText(textUtil, this);
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
    return (RelationshipShape2) textUtil.parseShape(isCurved());
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

  public void redraw() {
    doSetShape();
  }

  public void doSetShape() {
    if (surface.getEditorContext().isTrue(EditorProperty.HOLD_ARROW_DRAWING)) {
      // optimization since (curve) arrow end elements are not calculated properly
      return;
    }
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
    double x1 = points.get(size - 4);
    double y1 = points.get(size - 3);
    double x2 = points.get(size - 2);
    double y2 = points.get(size - 1);
    if (info.isCurved()) {
      double t = 0.05;
      double nextx = 0;
      double nexty = 0;
      double bx = 0;
      double by = 0;
      double prevx = 0;
      double prevy = 0;
      double cp1x = 0;
      double cp1y = 0;
      double cp2x = 0;
      double cp2y = 0;
      BezierHelpers.Segment lastSegment = relLine.getLastSegment();
      if (lastSegment != null) {
        nextx = lastSegment.getPoint2().getX();
        cp2x = lastSegment.getControlPoint2().getX();
        cp1x = lastSegment.getControlPoint1().getX();
        prevx = lastSegment.getPoint1().getX();

        nexty = lastSegment.getPoint2().getY(); 
        cp2y = lastSegment.getControlPoint2().getY();
        cp1y = lastSegment.getControlPoint1().getY();
        prevy = lastSegment.getPoint1().getY();
      }

      bx = BezierHelpers.bezierInterpolation(t, nextx, cp2x, cp1x, prevx);
      by = BezierHelpers.bezierInterpolation(t, nexty, cp2y, cp1y, prevy);

      // Debug curve visualization START
      // tempCircle.setShape(bx, by, 5);
      // tempCircle.setStroke(218, 57, 57, 1);

      // tempC1.setShape(cp.c1x, cp.c1y, 5);
      // tempC1.setStroke(51, 57, 57, 1);
      // tempC1.setFill(51, 57, 57, 1);
      // tempC2.setShape(cp.c2x, cp.c2y, 5);
      // tempC2.setStroke(150, 150, 150, 1);
      // tempC2.setFill(150, 150, 150, 1);
      // Debug curve visualization END

      calculateArrowHead(angle, ARROW_WIDTH, bx, by, x2, y2);
    } else {
      calculateArrowHead(angle, ARROW_WIDTH, x1, y1, x2, y2);
    }

    conditionallyCalculateDiamond();

    relationshipText.setShape(this);
    // moveChildrenRelatively();

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
      aggregate.setShape(aggregatePoints);
    }
    aggregate.setVisibility(info.isAggregate());
//    aggregate.setStroke("black");
    aggregate.moveToFront();

    arrowStartPolyline.setShape(startx, starty);

    relationshipHandleHelpers.doSetShape(currentDragged);
  }

  private void conditionallyCalculateDiamond() {
    if (info.isAggregate() || info.isDirectedStart()) {
      if (isCurved()) {
        calculateCurvedDiamond();
      } else {
        calculateDiamond(angle, ARROW_WIDTH, points.get(0), points.get(1), points.get(2), points.get(3));
      }
    }
  }

  private void calculateCurvedDiamond() {
    BezierHelpers.Segment segment = relLine.getFirstSegment();
    if (segment != null) {
      double nextx = segment.getPoint1().getX();
      double cp2x = segment.getControlPoint1().getX();
      double cp1x = segment.getControlPoint2().getX();
      double prevx = segment.getPoint2().getX();

      double nexty = segment.getPoint1().getY(); 
      double cp2y = segment.getControlPoint1().getY();
      double cp1y = segment.getControlPoint2().getY();
      double prevy = segment.getPoint2().getY();

      double t = 0.05;
      double bx = BezierHelpers.bezierInterpolation(t, nextx, cp2x, cp1x, prevx);
      double by = BezierHelpers.bezierInterpolation(t, nexty, cp2y, cp1y, prevy);

      calculateDiamond(angle, ARROW_WIDTH, points.get(0), points.get(1), bx, by);
    }
  }

  private void fillInheritance(String color) {
    if (info != null && info.isInheritance() && info.isFilled()) {
      // if solid then use board color
      inheritance.setFill("#" + color);
    } else {
      // no fill => hide line under the shape
      inheritance.setFill(Theme.getCurrentThemeName().getBoardBackgroundColor());
    }
  }

  private void fillAggregate(String color) {
    if (info != null && info.isAggregate() && info.isFilled()) {
      // if solid then use board color
      aggregate.setFill("#" + color);
    } else {
      // no fill => hide line under the shape
      aggregate.setFill(Theme.getCurrentThemeName().getBoardBackgroundColor());
    }
  }

  // @Override
  // public void toSvgStart() {
  //   fillInheritance(true);
  // }
  // @Override
  // public void toSvgEnd() {
  //   fillInheritance(false);
  // }


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
    swapStartAndEndAnchors(startAnchor, endAnchor);
    swapCustomData();
		doSetShape();
		
//		Diagram startDiagram = resetAndGetDiagram(startAnchor);
//		Diagram endDiagram = resetAndGetDiagram(endAnchor);
		
		
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

  public void curve() {
    info.asCurve();
    getDiagramItem().setShapeProperties(ShapeProperty.CURVED_ARROW.getValue());
    reset();
  }

  public void straight() {
    info.asStraight();
    getDiagramItem().setShapeProperties(0);
    reset();
  }

  private void reset() {
    doSetShape();
    relationshipHandleHelpers.showConditionally(this, true);
    surface.getEditorContext().getEventBus().fireEvent(new PotentialOnChangedEvent(this));
  }
	
	private void swapStartAndEndAnchors(Anchor startAnchor, Anchor endAnchor) {
    // remove from diagram anchor map or old anchor => anchor element mapping
    //  will hount and used
    startAnchor.clearParentAnchorMap();
    endAnchor.clearParentAnchorMap();
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
    fillInheritance(color);
    aggregate.setStroke(color);
    fillAggregate(color);
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
  public int getRelativeLeft() {
    if (isCurved()) {
      return getRelativeLeftCurvedLine();
    } else {
      return getRelativeLeftStraightLine();
    }
  }

  @Override
  public int getRelativeTop() {
    if (isCurved()) {
      return getRelativeTopCurvedLine();
    } else {
      return getRelativeTopStraightLine();
    }
  }

  private int getRelativeLeftCurvedLine() {
    int result = Integer.MAX_VALUE;
    for (int i = 0; i < relLine.segments.length(); ++i) {
      BezierHelpers.Segment seg = relLine.segments.get(i);
      result = Math.min(result, (int) seg.getPoint1().getX());
      result = Math.min(result, (int) seg.getPoint2().getX());
      result = Math.min(result, (int) seg.getControlPoint1().getX());
      result = Math.min(result, (int) seg.getControlPoint2().getX());
    }
    return result;
  }

  private int getRelativeTopCurvedLine() {
    int result = Integer.MAX_VALUE;
    for (int i = 0; i < relLine.segments.length(); ++i) {
      BezierHelpers.Segment seg = relLine.segments.get(i);
      result = Math.min(result, (int) seg.getPoint1().getY());
      result = Math.min(result, (int) seg.getPoint2().getY());
      result = Math.min(result, (int) seg.getControlPoint1().getY());
      result = Math.min(result, (int) seg.getControlPoint2().getY());
    }
    return result;
  }

  private int getRelativeLeftStraightLine() {
    int result = Integer.MAX_VALUE;
    for (int i = 0; i < points.size(); i += 2) {
      result = Math.min(result, points.get(i));
    }
    return result;
  }

  private int getRelativeTopStraightLine() {
    int result = Integer.MAX_VALUE;
    for (int i = 1; i < points.size(); i += 2) {
      result = Math.min(result, points.get(i));
    }
    return result;
  }

  @Override
  public int getWidth() {
    if (isCurved()) {
      return getWidthCurved();
    } else {
      return DiagramHelpers.getWidth(points);
    }
  }

  private int getWidthCurved() {
    int result = Integer.MIN_VALUE;
    int left = Integer.MAX_VALUE;
    for (int i = 0; i < relLine.segments.length(); ++i) {
      BezierHelpers.Segment seg = relLine.segments.get(i);
      result = Math.max(result, (int) seg.getPoint1().getX());
      result = Math.max(result, (int) seg.getPoint2().getX());
      result = Math.max(result, (int) seg.getControlPoint1().getX());
      result = Math.max(result, (int) seg.getControlPoint2().getX());
      left = Math.min(left, (int) seg.getPoint1().getX());
      left = Math.min(left, (int) seg.getPoint2().getX());
      left = Math.min(left, (int) seg.getControlPoint1().getX());
      left = Math.min(left, (int) seg.getControlPoint2().getX());
    }
    return result - left;
  }

  @Override
  public int getHeight() {
    if (isCurved()) {
      return getHeightCurved();
    } else {
      return DiagramHelpers.getHeight(points);
    }
  }

  private int getHeightCurved() {
    int result = Integer.MIN_VALUE;
    int top = Integer.MAX_VALUE;
    for (int i = 0; i < relLine.segments.length(); ++i) {
      BezierHelpers.Segment seg = relLine.segments.get(i);
      result = Math.max(result, (int) seg.getPoint1().getY());
      result = Math.max(result, (int) seg.getPoint2().getY());
      result = Math.max(result, (int) seg.getControlPoint1().getY());
      result = Math.max(result, (int) seg.getControlPoint2().getY());
      top = Math.min(top, (int) seg.getPoint1().getY());
      top = Math.min(top, (int) seg.getPoint2().getY());
      top = Math.min(top, (int) seg.getControlPoint1().getY());
      top = Math.min(top, (int) seg.getControlPoint2().getY());
    }
    return result - top;
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
  	return super.supportedMenuItems() | ContextMenuItem.REVERSE_CONNECTION_MENU.getValue();
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
  public boolean isTextColorAccordingToBackgroundColor() {
    return true;
  }

  public AnchorMoveHandler getAnchorMoveHandler() {
    return handler;
  }

  @Override
  protected void setShapeProperties(Integer shapeProperties) {
    super.setShapeProperties(shapeProperties);

    if (isCurved()) {
      info.asCurve();
    }
    redraw();
  }

  public void updateCardinalDirection(int x, int y, AnchorElement ae) {
    if (ae != null && ae.getSource() != null) {
      Diagram d = ae.getSource();
      ae.setCardinalDirection(AnchorUtils.findCardinalDirection(x, y, d.getLeft(), d.getTop(), d.getWidth(), d.getHeight()));
    }
  }

  @Override
  public void addChild(IChildElement child) {
    if (!children.contains(child)) {
      children.add(child);
    }
  }

  @Override
  public Diagram asDiagram() {
    return this;
  }

  @Override
  public List<IChildElement> getChildren() {
    return children;
  }

  public void storeChildrenRelativeDistance() {
    int left = getLeft();
    int top = getTop();
    int width = getWidth();
    int height = getHeight();
    for (IChildElement child : children) {
      setChildRelativeDistance(child, left, top, width, height);
    }
  }

  public void moveChildrenRelatively() {
    int left2 = getLeft();
    int top2 = getTop();
    int width2 = getWidth();
    int height2 = getHeight();
    for (IChildElement child : children) {
      double dleft2 = child.getRelativeDistanceLeft() * width2;
      double dtop2 = child.getRelativeDistanceTop() * height2;
      double childLeft2 = left2 - dleft2;
      double childTop2 = top2 - dtop2;
      child.setPosition(childLeft2, childTop2);
      // setChildRelativeDistance(child, left2, top2, width2, height2);
    }
  }

  private void setChildRelativeDistance(IChildElement child, int left, int top, int width, int height) {
    double dleft = left - child.getLeft();
    double dtop = top - child.getTop();
    double rleft = dleft / width;
    double rtop = dtop / height;
    logger.debug("rleft " + rleft + " rtop " + rtop);
    child.saveRelativeDistance(rleft, rtop);
  }

}
