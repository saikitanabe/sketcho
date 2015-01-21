package net.sevenscales.editor.uicomponents;

import java.util.ArrayList;
import java.util.List;

import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.IGroup;
import net.sevenscales.editor.gfx.domain.IShape;
import net.sevenscales.editor.gfx.domain.IShapeFactory;
import net.sevenscales.editor.gfx.domain.IText;
import net.sevenscales.editor.gfx.domain.MatrixPointJS;
import net.sevenscales.editor.gfx.domain.PointDouble;
import net.sevenscales.editor.gfx.domain.IRelationship;
import net.sevenscales.editor.uicomponents.impl.RelationshipTextUtil2;
import net.sevenscales.editor.diagram.utils.BezierHelpers;


public class RelationshipText2 {
	private static final SLogger logger = SLogger.createLogger(RelationshipText2.class);
	
  private IText endElement;
//  private TextParser textParser;
  private String end;
  private String start;
  private String label;
  private IText labelElement;
  private IText startElement;
//  private Circle temp;
  private List<IShape> elements;
  int posx = 0;
  int posy = 0;
	private ISurfaceHandler surface;
  private final static double angle = 30;

  public enum ClickTextPosition {
  	START, MIDDLE, END, ALL
  }

  public RelationshipText2(IGroup group, ISurfaceHandler surface, boolean editable) {
  	this.surface = surface;
    labelElement = IShapeFactory.Util.factory(editable).createText(group);
    startElement = IShapeFactory.Util.factory(editable).createText(group);
    endElement = IShapeFactory.Util.factory(editable).createText(group);
    
//    temp = new Circle(surface);
//    textParser = new TextParser();
    elements = new ArrayList<IShape>();
    elements.add(endElement);
    elements.add(labelElement);
    elements.add(startElement);
//    elements.add(temp);
  }
  
  private int pointDistance(int x, int y, int fromX, int fromY) {
  	int dx = Math.abs(x - fromX);
  	int dy = Math.abs(y - fromY);
  	return (int) Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
  }

  public ClickTextPosition findClickPosition(int x, int y, List<Integer> points) {
  	// diminish root layer transform
		x -= surface.getRootLayer().getTransformX();
		y -= surface.getRootLayer().getTransformY();

		// unscale text element coordinates; could be done other way around => scale screen x and screen y and root layer tranforms
		MatrixPointJS startPoint = MatrixPointJS.createUnscaledPoint(startElement.getX(), startElement.getY(), surface.getScaleFactor());
  	int startDist = pointDistance(x, y, startPoint.getX(), startPoint.getY());
  	
		MatrixPointJS endPoint = MatrixPointJS.createUnscaledPoint(endElement.getX(), endElement.getY(), surface.getScaleFactor());
  	int endDist = pointDistance(x, y, endPoint.getX(), endPoint.getY());
  	
		MatrixPointJS middlePoint = MatrixPointJS.createUnscaledPoint(labelElement.getX(), labelElement.getY(), surface.getScaleFactor());
  	int middleDist = pointDistance(x, y, middlePoint.getX() + (int) labelElement.getTextWidth()/2, middlePoint.getY());
  	
//  	int zeroDist = pointDistance(x, y, 0, 0);
  	
  	logger.debug("point({}, {})", x, y);
  	logger.debug("startPoint({}, {})", startElement.getX(), startElement.getY());
  	logger.debug("middlePoint({}, {})", labelElement.getX(), labelElement.getY());
  	logger.debug("endPoint({}, {})", endElement.getX(), endElement.getY());
  	
  	int minDist = Math.min(startDist, endDist);
  	minDist = Math.min(minDist, middleDist);
//  	minDist = Math.min(minDist, zeroDist); // if there is no position then it is connection
  	if (minDist == startDist) {
  		return ClickTextPosition.START;
  	} else if (minDist == endDist) {
  		return ClickTextPosition.END;
  	} else if (minDist == middleDist) {
    	return ClickTextPosition.MIDDLE;
  	}
  	
  	return ClickTextPosition.ALL;
  }

  public void setText(RelationshipTextUtil2 textUtil, IRelationship parent) {
    start = textUtil.parseLeftText();
    end = textUtil.parseRightText();
    label = textUtil.parseLabel();
    
    // BUG FIX: trim check is because plain space character makes chrome halt
    if (label.trim().length() > 0) {
      labelElement.setText(label);
    }
    // alignment doesn't work well for now in seq diagrams
//    labelElement.setAttribute("dominant-baseline", "central");
    
    if (start.trim().length() > 0) {
      startElement.setText(start);
    }
    startElement.setAlignment(IText.ALIGN_CENTER);
    startElement.setAttribute("dominant-baseline", "central");
    
    if (end.trim().length() > 0) {
      endElement.setText(end);
    }
    endElement.setAlignment(IText.ALIGN_CENTER);
    endElement.setAttribute("dominant-baseline", "central");
    
    setShape(parent);

//    textItems = textParser.parse(text);
//    if (textItems.get(MULTIPLICITY_1) != null) {
//      SilverUtils.setText(this.multiplicity1Element, textItems.get(MULTIPLICITY_1 ));
//    }
  }
  
  private void calculateLocation(int width, int height,
      int x1, int y1, int x2, int y2, int fromx, int fromy, double extraAngle) {
    double beta = AngleUtil2.beta(x1, y1, x2, y2) + extraAngle;

    // calculate base
    double betaadj = Math.sin(beta) * height;
    double betanext = Math.cos(beta) * height;

    int bx = (int) (fromx + betanext);
    int by = (int) (fromy + betaadj);

    double gamma = Math.PI - Math.PI / 2 - beta;

    int ydiff = (int) (Math.sin(gamma) * width);
    int xdiff = (int) (Math.cos(gamma) * width);

//    double textWidth = endElement.getTextWidth();
    // left side
    posx = bx - xdiff;
    posy = by + ydiff;

    // right side
//    right.x = bx + xdiff;
//    right.y = by - ydiff;
  }


  public void resetRenderTransform() {
//    SilverUtils.resetRenderTransform(startElement);
//    SilverUtils.resetRenderTransform(labelElement);
//    SilverUtils.resetRenderTransform(endElement);
  }

  public void setShape(IRelationship parent) {
    int startLine = 0;
    int endLine = parent.getPoints().size() - 4;
    int lineCount = parent.getPoints().size()/2-1;
    int middleLine = lineCount/2*2;
    double actualHeight = 12;
    
    {
      // start
    	int sx = parent.getPoints().get(startLine);
    	int sy = parent.getPoints().get(startLine+1); 
    	int ex = parent.getPoints().get(startLine+2);
    	int ey = parent.getPoints().get(startLine+3);
      int width = 14;
      int height = 15;
    	calculateLocation(width, height, sx, sy, ex, ey, sx, sy, Math.PI/2);
      startElement.setShape(posx, posy);
    }

    {
      // end
      int width = 15;
      int height = 16;
    	calculateLocation(width, height, parent.getPoints().get(endLine), parent.getPoints().get(endLine+1), parent.getPoints().get(endLine+2), parent.getPoints().get(endLine+3), parent.getPoints().get(endLine+2), parent.getPoints().get(endLine+3), 0);
      endElement.setShape(posx, posy);
    }

    {
    // middle
    double actualWidth = labelElement.getTextWidth();

    int x = 0;
    int y = 0;
    if (parent.isCurved() && parent.getPoints().size() == 4) {
      // applies only when only two points; otherwise can calculate normally
      PointDouble point = BezierHelpers.bezierMiddlePoint(0, parent.getSegments());
      if (point != null) {
        x = (int) point.x - (int) (actualWidth / 2);
        y = (int) point.y;
      }
    } else if (lineCount % 2 == 0) {
      x = parent.getPoints().get(middleLine);
      y = parent.getPoints().get(middleLine+1) - (int) actualHeight;
    } else {
      x = (parent.getPoints().get(middleLine) + parent.getPoints().get(middleLine+2) - (int)actualWidth) / 2;
      int align = parent.getPoints().get(middleLine+1) >= parent.getPoints().get(middleLine+3) ? - (int) actualHeight : 0;
//      y = (parent.getPoints().get(middleLine+1) + parent.getPoints().get(middleLine+3)) / 2 + align;
      y = (parent.getPoints().get(middleLine+1) + parent.getPoints().get(middleLine+3)) / 2 + align/2;
    }
    
    labelElement.setShape(x, y);
    }
  }
  
  public List<IShape> getElements() {
		return elements;
	}
  
  public IText getStartElement() {
		return startElement;
	}
  public IText getEndElement() {
		return endElement;
	}
  public IText getLabelElement() {
		return labelElement;
	}

  public void applyTextColor(Color textColor) {
    for (IShape s : elements) {
      s.setFill(textColor.red, textColor.green, textColor.blue, textColor.opacity);
    }
  }

}
