package net.sevenscales.editor.uicomponents.uml;


import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.impl.Theme;
import net.sevenscales.editor.content.ui.ContextMenuItem;
import net.sevenscales.editor.content.ui.UMLDiagramType;
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
import net.sevenscales.editor.diagram.drag.Anchor;
import net.sevenscales.editor.diagram.drag.AnchorElement;
import net.sevenscales.editor.gfx.domain.Point;
import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.DiagramItemDTO;

public class FreehandElement extends AbstractDiagramItem {
	private static SLogger logger = SLogger.createLogger(FreehandElement.class);
	public static int FREEHAND_STROKE_WIDTH = 2;
	public static int ACTIVITY_START_RADIUS = 10;
	public static int FREEHAND_TOUCH_WIDTH = 20;

	private FreehandShape shape;
	private Point coords = new Point();
  private IPath path;
  private IPath backgroundPath;
  private IGroup group;
  private int left;
  private int top;
  private int width;
  private int height;

  private boolean tosvg;
  
  private static final String BOUNDARY_COLOR = "#aaaaaa";

  private IPath.PathTransformer pathTransformer = new IPath.PathTransformer() {
  	public String getShapeStr(int dx, int dy) {
  		return calcShape(shape.points, dx, dy);
  	}
  };
  
	public FreehandElement(ISurfaceHandler surface, FreehandShape newShape, Color backgroundColor, Color borderColor, Color textColor, boolean editable, IDiagramItemRO item) {
		super(editable, surface, backgroundColor, borderColor, textColor, item);
		this.shape = newShape;
		
		group = IShapeFactory.Util.factory(editable).createGroup(surface.getElementLayer());
    // group.setAttribute("cursor", "default");

    path = IShapeFactory.Util.factory(editable).createPath(group, pathTransformer);
    path.setStroke(borderColor);
    path.setStrokeWidth(FREEHAND_STROKE_WIDTH);
    path.setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, backgroundColor.opacity);
    
    backgroundPath = IShapeFactory.Util.factory(editable).createPath(group, pathTransformer);
    backgroundPath.setStroke(0, 0, 0, 0);
    backgroundPath.setStrokeWidth(FREEHAND_TOUCH_WIDTH);
		
		addEvents(backgroundPath);
		
		addMouseDiagramHandler(this);
		
    shapes.add(path);
    
    setReadOnly(!editable);
    setShape(shape.points);

    enableDisableBackgroundEvents();
    
    setBorderColor(borderColor);

    super.constructorDone();
	}
	
	@Override
	public int getRelativeLeft() {
		return left;
	}
	@Override
	public int getRelativeTop() {
		return top;
	}
	@Override
	public int getWidth() {
		return width;
	}
	@Override
	public int getHeight() {
		return height;
	}
	
	public Point getDiffFromMouseDownLocation() {
		return new Point(diffFromMouseDownX, diffFromMouseDownY);
	}
	
	public void accept(ISurfaceHandler surface) {
	  super.accept(surface);
		surface.makeDraggable(this);
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
    FreehandShape newShape = new FreehandShape(DiagramHelpers.map(shape.points, 20 + getTransformX(), 20 + getTransformY()));
    Diagram result = new FreehandElement(surface, newShape, new Color(backgroundColor), new Color(borderColor), new Color(textColor), editable, new DiagramItemDTO());
    return result;
  }
	
	public boolean resize(Point diff) {
		return false;
	}

	protected boolean resize(int left, int top, int width, int height) {
	   return false;
	}

	public void resizeEnd() {
	}

	/**
	* Convert shape to normal shape to hide elements below it. Otherwise
	* after transform path will be broken.
	*/
  public void toSvgStart() {
  	this.tosvg = true;
  	doSetShape(this.shape.points);
  }

  /**
  * Restore shape back to as it is suppose to be. Not to hide elements.
  */
  public void toSvgEnd() {
  	this.tosvg = false;
  	doSetShape(this.shape.points);
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
  	this.shape.points = shape;
  	path.setShape(calcShape(shape, 0, 0));
  	backgroundPath.setShape(calcShape(shape, 0, 0));

  	this.left = DiagramHelpers.getLeftCoordinate(shape);
  	this.top = DiagramHelpers.getTopCoordinate(shape);
  	this.width = DiagramHelpers.getWidth(shape);
  	this.height = DiagramHelpers.getHeight(shape);
    connectionHelpers.setShape(left, top, width, height);
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

			if (first || (backgroundColor.opacity == 0 && !this.tosvg)) { // no background, so no need to hide anything below
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
	
  public void setHighlightColor(Color color) {
		path.setStroke(color);
  }
  
  @Override
  public void setBackgroundColor(int red, int green, int blue, double opacity) {
  	super.setBackgroundColor(red, green, blue, opacity);
    path.setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, backgroundColor.opacity);

    // need to draw differently with or without background, so redraw
    doSetShape(this.shape.points);
    enableDisableBackgroundEvents();
  }

  private void enableDisableBackgroundEvents() {
    if (backgroundColor.opacity == 0) {
    	// clear events from background
	    backgroundPath.setFill(null);
    } else {
	    // enable mouse events from background
	    backgroundPath.setFill(0, 0, 0, 0);
    }
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
  	return ContextMenuItem.FREEHAND_MENU.getValue() | ContextMenuItem.COLOR_MENU.getValue() |
           ContextMenuItem.LAYERS.getValue();
  }

}
