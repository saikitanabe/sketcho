package net.sevenscales.editor.uicomponents.uml;


import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.impl.Theme;
import net.sevenscales.editor.content.ui.ContextMenuItem;
import net.sevenscales.editor.content.ui.UMLDiagramSelections.UMLDiagramType;
import net.sevenscales.editor.content.utils.AreaUtils;
import net.sevenscales.editor.content.utils.ContainerAttachHelpers;
import net.sevenscales.editor.content.utils.DiagramHelpers;
import net.sevenscales.editor.content.utils.IntegerHelpers;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.shape.FreehandShape;
import net.sevenscales.editor.diagram.shape.Info;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.IGroup;
import net.sevenscales.editor.gfx.domain.ILine;
import net.sevenscales.editor.gfx.domain.IPath;
import net.sevenscales.editor.gfx.domain.IRectangle;
import net.sevenscales.editor.gfx.domain.IShapeFactory;
import net.sevenscales.editor.gfx.domain.SupportsRectangleShape;
import net.sevenscales.editor.silver.SilverUtils;
import net.sevenscales.editor.uicomponents.AbstractDiagramItem;
import net.sevenscales.editor.uicomponents.Anchor;
import net.sevenscales.editor.uicomponents.AnchorElement;
import net.sevenscales.editor.uicomponents.Point;
import net.sevenscales.domain.utils.SLogger;

public class FreehandElement extends AbstractDiagramItem implements SupportsRectangleShape {
	private static SLogger logger = SLogger.createLogger(FreehandElement.class);
	public static int FREEHAND_STROKE_WIDTH = 2;
	public static int ACTIVITY_START_RADIUS = 10;
//	private Rectangle rectSurface;
	private FreehandShape shape;
	private Point coords = new Point();
	private IRectangle boundary;
//	private boolean onResizeArea;
  private IPath path;
//  private IPolyline path;
  private IGroup group;
  
  private static final String BOUNDARY_COLOR = "#aaaaaa";

  private IPath.PathTransformer pathTransformer = new IPath.PathTransformer() {
  	public String getShapeStr(int dx, int dy) {
  		return calcShape(shape.points, dx, dy);
  	}
  };
  
	public FreehandElement(ISurfaceHandler surface, FreehandShape newShape, Color backgroundColor, Color borderColor, Color textColor, boolean editable) {
		super(editable, surface, backgroundColor, borderColor, textColor);
		this.shape = newShape;
		
		group = IShapeFactory.Util.factory(editable).createGroup(surface.getElementLayer());
    group.setAttribute("cursor", "default");

    path = IShapeFactory.Util.factory(editable).createPath(group, pathTransformer);
    path.setStroke(borderWebColor);
    path.setStrokeWidth(FREEHAND_STROKE_WIDTH);
    path.setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, backgroundColor.opacity);
    
    boundary = IShapeFactory.Util.factory(editable).createRectangle(group);
    boundary.setStroke("transparent");
    boundary.setFill(0, 0, 0, 0); // transparent
    boundary.setStyle(ILine.DASH);
		
		addEvents(boundary);
		
		addMouseDiagramHandler(this);
		
    shapes.add(boundary);
    shapes.add(path);
    
    setReadOnly(!editable);
    setShape(shape.points);
    
    super.constructorDone();
	}
	
	@Override
	protected int doGetLeft() {
		return boundary.getX();
	}
	@Override

	protected int doGetTop() {
		return boundary.getY();
	}
	@Override
	public int getWidth() {
		return boundary.getWidth();
	}
	@Override
	public int getHeight() {
		return boundary.getHeight();
	}
	
	@Override
	public void setShape(int left, int top, int width, int height) {
//    boundary.setShape(left + width / 2, top + height / 2, width / 2);
//    connectionHelpers.setShape(getLeft(), getTop(), getWidth(), getHeight());
	}
	
//	public void saveLastTransform() {
//	  // get transformation
//    int dx = SilverUtils.getTransformX(group.getContainer());
//    int dy = SilverUtils.getTransformY(group.getContainer());
//	    
//	  // reset transformations
//    SilverUtils.resetRenderTransform(group.getContainer());
//	    
//    // apply transformations to shapes
//    for (IShape s : shapes) {
//      s.applyTransform(dx, dy);
//    }
//	}

	public Point getDiffFromMouseDownLocation() {
		return new Point(diffFromMouseDownX, diffFromMouseDownY);
	}
	
	public void accept(ISurfaceHandler surface) {
	  super.accept(surface);
		surface.makeDraggable(this);
	}

	public void removeFromParent() {
		surface.remove(this);
    surface.remove(group.getContainer());
	}
	
	@Override
	public AnchorElement onAttachArea(Anchor anchor, int x, int y) {
  	return ContainerAttachHelpers.onAttachArea(this, anchor, x, y);
	}
  
  @Override
	public Diagram duplicate(boolean partOfMultiple) {
		return duplicate(surface, partOfMultiple);
	}
	
  @Override
	public Diagram duplicate(ISurfaceHandler surface, boolean partOfMultiple) {
		Point p = getCoords();
		return duplicate(surface, p.x + 20, p.y + 20);
	}
	
  @Override
  public Diagram duplicate(ISurfaceHandler surface, int x, int y) {
    FreehandShape newShape = new FreehandShape(DiagramHelpers.map(shape.points, 10, 10));
    Diagram result = new FreehandElement(surface, newShape, new Color(backgroundColor), new Color(borderColor), new Color(textColor), editable);
//    Diagram result = createDiagram(surface, newShape, getEditable());
    return result;
  }
	
//  protected Diagram createDiagram(ISurfaceHandler surface, ActivityStartShape newShape,
//      boolean editable) {
////    return new FreehandElement(surface, newShape, editable);
//  	return null;
//  }
	
//////////////////////////////////////////////////////////////////////
	
//	public boolean onResizeArea(int x, int y) {
//		return onResizeArea;
//	}

//	public JavaScriptObject getResizeElement() {
//		return rectSurface.getRawNode();
//	}
	
	public void resizeStart() {
	}

	public boolean resize(Point diff) {
//		return resize(boundary.getX(), boundary.getY(), boundary.getRadius() + diff.x, boundary.getRadius() + diff.y);
		return false;
	}

	protected boolean resize(int left, int top, int width, int height) {
//	   if (width >= minimumWidth && height >= minimumHeight) {
//  	   left = GridUtils.align(left);
//       top = GridUtils.align(top);
//       width = GridUtils.align(width);
//       height = GridUtils.align(height);
//       
//       setShape(left, top, width);
//  		return true;
//	   }
	   return false;
	}

	public void resizeEnd() {
	}

	/**
	* Copy points since shape.points is the model of this runtime instance.
	* Shape is copied e.g. to OT or to export svg.
	*/
	public Info getInfo() {
		// copy points from model
		int[] points = new int[shape.points.length];
		for (int i = 0; i < shape.points.length; i += 2) {
			points[i] = shape.points[i] + getTransformX();
			points[i + 1] = shape.points[i + 1] + getTransformY();
		}
		// fill other stuff
		FreehandShape result = new FreehandShape(points);
    super.fillInfo(result);
		return result;
	}

	public void setShape(Info shape) {
		// TODO Auto-generated method stub
		
	}

	public void setReadOnly(boolean value) {
	  super.setReadOnly(value);
	  boundary.setVisibility(!value);
	}
	
  public String getDefaultRelationship() {
    return "->";
  }
  
  @Override
  public UMLDiagramType getDiagramType() {
  	return UMLDiagramType.FREE_HAND;
  }
  
  @Override
	protected void doSetShape(int[] shape) {
//    setShape(shape[0], shape[1], shape[2]);
  	this.shape.points = shape;
  	path.setShape(calcShape(shape, 0, 0));
  	int left = DiagramHelpers.getLeftCoordinate(shape);
  	int top = DiagramHelpers.getTopCoordinate(shape);
  	int width = DiagramHelpers.getWidth(shape);
  	int height = DiagramHelpers.getHeight(shape);
  	boundary.setShape(
  			left, 
  			top, 
  			width, 
  			height, 4);
  	
    connectionHelpers.setShape(left, top, width, height);

//  	boundary.setShape(getLeft(), getTop(), getWidth(), getHeight(), 4);
	}

	private int mid(int val1, int val2) {
		return (int) ((val1 + val2) * 0.5);
	}

	private String moveBezier(int startx, int starty, int controlx, int controly, int endx, int endy) {
		return "M" + startx + "," + starty + "S" + controlx + "," + controly + " " + endx + "," + endy;
	}

	private String bezierCont(int controlx, int controly, int endx, int endy) {
		return "Q" + controlx + "," + controly + " " + endx + "," + endy + " ";
	}

	private String calcShape2(int[] shape) {
		String result = "";
    String prefix = "";

		for (int i = 0; i < shape.length; i += 2) {
		 if (i == 0) {
       prefix = "M";
		 } else if (i == 2) {
       prefix = "Q";
		 } else {
       prefix = "";
		 }
		 result += prefix + shape[i] + ","+ shape[i + 1] + " ";
		}
		return result;
	}
  
	private String calcShape(int[] shape, int dx, int dy) {
		String result = "";
		String prefix = "";

		int prev1x = 0 < shape.length ? shape[0] : 0;
		int prev1y = 1 < shape.length ? shape[1] : 0;
		int prev2x = prev1x;
		int prev2y = prev1y;
		int currx = prev1x;
		int curry = prev1y;

		boolean first = true;
		int firstx = 0;
		int firsty = 0;

		for (int i = 0; i < shape.length; i += 2) {
			prev2x = prev1x;
			prev2y = prev1y;

			currx = shape[i];
			curry = shape[i + 1];
			// if (first) {
			// 	firstx = currx;
			// 	firsty = curry;
			// }

			prev1x = i - 2 >= 0 ? shape[i - 2] : currx;
			prev1y = i - 1 >= 0 ? shape[i - 1] : curry;

			prev1x += dx;
			prev2x += dx;
			currx += dx;
			prev1y += dy;
			prev2y += dy;
			curry += dy;

			int mid1x = mid(prev1x, prev2x);
			int mid1y = mid(prev1y, prev2y);

			int mid2x = mid(currx, prev1x);
			int mid2y = mid(curry, prev1y);

			if (first) {
				result += moveBezier(mid1x, mid1y, prev1x, prev1y, mid2x, mid2y);
				first = false;
			} else {
				result += bezierCont(prev1x, prev1y, mid2x, mid2y);
			}
		}
		// result += firstx + "," + firsty;
		// logger.debug("calcShape {}", result);
		return result;
	}
	
	// @Override
	// public void select() {
	// 	super.select();
	// 	boundary.setStroke(BOUNDARY_COLOR);
	// }
	
	// @Override
	// public void unselect() {
	// 	super.unselect();
	// 	boundary.setStroke("transparent");
	// }
  
  public void setHighlightColor(String color) {
  	if (!isHighlightOn()) {
  		// do not change path color if this is highlight border color change 
  		path.setStroke(color);
  	}
  }
  
  @Override
  public void setHighlight(boolean highlight) {
  	String color = HIGHLIGHT_COLOR;
		if (!highlight) {
			color = "transparent";
		}
  	super.setHighlight(highlight);
  	boundary.setStroke(color);
  }
  
  @Override
  public void setBackgroundColor(int red, int green, int blue, double opacity) {
  	super.setBackgroundColor(red, green, blue, opacity);
//  	path.setStroke(backgroundColor.red, backgroundColor.green, backgroundColor.blue, 1);
    path.setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, backgroundColor.opacity);
//  	path.setStrokeWidth(2);
  }
  	
	@Override
	public IGroup getGroup() {
		return group;
	}
	
  @Override
  public boolean supportsTextEditing() {
  	return false;
  }
  
  @Override
  public int supportedMenuItems() {
  	return ContextMenuItem.FREEHAND_MENU.getValue() | ContextMenuItem.COLOR_MENU.getValue();
  }


}
