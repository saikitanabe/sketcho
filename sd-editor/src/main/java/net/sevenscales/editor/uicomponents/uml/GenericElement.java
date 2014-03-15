
package net.sevenscales.editor.uicomponents.uml;

import java.util.List;
import java.util.ArrayList;

import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.impl.Theme;
import net.sevenscales.editor.content.ui.ContextMenuItem;
import net.sevenscales.editor.content.ui.UMLDiagramSelections.UMLDiagramType;
import net.sevenscales.editor.content.utils.AreaUtils;
import net.sevenscales.editor.content.utils.ContainerAttachHelpers;
import net.sevenscales.editor.content.utils.DiagramHelpers;
import net.sevenscales.editor.content.utils.IntegerHelpers;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.shape.GenericShape;
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
import net.sevenscales.editor.uicomponents.Point;
import net.sevenscales.editor.uicomponents.helpers.ResizeHelpers;
import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.domain.ElementType;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.DiagramItemDTO;


public class GenericElement extends AbstractDiagramItem {
	private static SLogger logger = SLogger.createLogger(GenericElement.class);

	public static int FREEHAND_STROKE_WIDTH = 2;
	public static int ACTIVITY_START_RADIUS = 10;
	public static int FREEHAND_TOUCH_WIDTH = 20;

	private GenericShape shape;
	private Point coords = new Point();
  private List<IPath> paths;
  private IRectangle background;
  private IGroup group;
  private IGroup subgroup;
  private int left;
  private int top;
  private int width;
  private int height;
  private Shapes.Group theshape;

  private boolean tosvg;
  
  private static final String BOUNDARY_COLOR = "#aaaaaa";

  private IPath.PathTransformer pathTransformer = new IPath.PathTransformer() {
  	public String getShapeStr(int dx, int dy) {
  		return "";
  	}
  };
  
	public GenericElement(ISurfaceHandler surface, GenericShape newShape, Color backgroundColor, Color borderColor, Color textColor, boolean editable, IDiagramItemRO item) {
		super(editable, surface, backgroundColor, borderColor, textColor, item);
		this.shape = newShape;
		
		group = IShapeFactory.Util.factory(editable).createGroup(surface.getElementLayer());
    group.setAttribute("cursor", "default");

		subgroup = IShapeFactory.Util.factory(editable).createGroup(group);
    // subgroup.setAttribute("cursor", "default");

  	theshape = Shapes.get(getDiagramItem().getType());

  	paths = new ArrayList<IPath>(theshape.protos.length);
    createSubPaths(theshape);

    background = IShapeFactory.Util.factory(editable).createRectangle(group);
    background.setFill(0, 0 , 0, 0); // transparent

		addEvents(background);
		
		addMouseDiagramHandler(this);

		for (IPath path : paths) {
	    shapes.add(path);
		}

    resizeHelpers = ResizeHelpers.createResizeHelpers(surface);


    setReadOnly(!editable);
    setShape(shape.rectShape.left, shape.rectShape.top, shape.rectShape.width, shape.rectShape.height);

    setBorderColor(borderColor);

    super.constructorDone();
	}

	private void createSubPaths(Shapes.Group groupData) {
		for (Shapes.Proto p : groupData.protos) {
			IPath path = createSubPath(p);
			paths.add(path);
		}
	}

	private IPath createSubPath(Shapes.Proto proto) {
    IPath path = IShapeFactory.Util.factory(editable).createPath(subgroup, pathTransformer);
    path.setStroke(borderWebColor);
    path.setStrokeWidth(FREEHAND_STROKE_WIDTH);
    path.setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, backgroundColor.opacity);
    // path.setStrokeCap("round");
    if (proto.style != null) {
	    path.setAttribute("style", proto.style);
    }
    path.setAttribute("vector-effect", "non-scaling-stroke");
  	path.setShape(proto.path);
  	return path;
	}
	
	// @Override
	// protected int doGetLeft() {
	// 	return left;
	// }
	// @Override

	// protected int doGetTop() {
	// 	return top;
	// }
	// @Override
	// public int getWidth() {
	// 	return width;
	// }
	// @Override
	// public int getHeight() {
	// 	return height;
	// }

	@Override
	protected int doGetLeft() {
		return left;
	}
	@Override
	protected int doGetTop() {
		return top;
	}
	@Override
	public int getWidth() {
		return background.getWidth();
	}
	@Override
	public int getHeight() {
		return background.getHeight();
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
    GenericShape newShape = new GenericShape(getDiagramItem().getType(), x, y, getWidth(), getHeight());
    Diagram result = new GenericElement(surface, newShape, new Color(backgroundColor), new Color(borderColor), new Color(textColor), editable, DiagramItemDTO.createGenericItem(ElementType.getEnum(getDiagramItem().getType())));
    return result;
  }
	
	public void resizeStart() {
	}

  public boolean resize(Point diff) {
    return resize(doGetLeft(), doGetTop(), getWidth() + diff.x, getHeight() + diff.y);
  }

  protected boolean resize(int left, int top, int width, int height) {
    setShape(left, top, width, height);
    dispatchAndRecalculateAnchorPositions();
    return true;
  }

	public void resizeEnd() {
	}

	/**
	* Convert shape to normal shape to hide elements below it. Otherwise
	* after transform path will be broken.
	*/
  // public void toSvgStart() {
  // 	this.tosvg = true;
  // 	doSetShape(this.shape.points);
  // }

  /**
  * Restore shape back to as it is suppose to be. Not to hide elements.
  */
  // public void toSvgEnd() {
  // 	this.tosvg = false;
  // 	doSetShape(this.shape.points);
  // }

	/**
	* Copy points since shape.points is the model of this runtime instance.
	* Shape is copied e.g. to OT or to export svg.
	*/
	// public Info getInfo() {
	// 	// copy points from model
	// 	int[] points = new int[shape.points.length];
	// 	for (int i = 0; i < shape.points.length; i += 2) {
	// 		points[i] = shape.points[i] + getTransformX();
	// 		points[i + 1] = shape.points[i + 1] + getTransformY();
	// 	}
	// 	// fill other stuff
	// 	FreehandShape result = new FreehandShape(points);
 //    super.fillInfo(result);
	// 	return result;
	// }

  public Info getInfo() {
    super.fillInfo(shape);
    return this.shape;
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
  
 //  @Override
	// protected void doSetShape(int[] shape) {
 //  	this.shape.points = shape;
 //  	path.setShape(calcShape(shape));
 //  	// backgroundPath.setShape(calcShape(shape, 0, 0));

 //  	this.left = DiagramHelpers.getLeftCoordinate(shape);
 //  	this.top = DiagramHelpers.getTopCoordinate(shape);
 //  	this.width = DiagramHelpers.getWidth(shape);
 //  	this.height = DiagramHelpers.getHeight(shape);
 //    connectionHelpers.setShape(left, top, width, height);
	// }

  @Override
  protected void doSetShape(int[] shape) {
    // resize(shape[0], shape[1], shape[2], shape[3]);
    setShape(shape[0], shape[1], shape[2], shape[3]);
  }

  public void setShape(int left, int top, int width, int height) {
  	this.left = left;
  	this.top = top;
  	this.width = width;
  	this.height = height;

    background.setShape(left, top, width, height, 0);

  	subgroup.setScale(width / theshape.width, height / theshape.height);
  	subgroup.setTransform(left, top);

    connectionHelpers.setShape(left, top, width, height);
  }
  
  public void setHighlightColor(String color) {
  	for (IPath path : paths) {
			path.setStroke(color);
  	}
		// background.setStroke(color);
  }
  
  @Override
  public void setBackgroundColor(int red, int green, int blue, double opacity) {
  	super.setBackgroundColor(red, green, blue, opacity);
  	for (IPath path : paths) {
	    path.setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, backgroundColor.opacity);
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
  	return ContextMenuItem.FREEHAND_MENU.getValue() | ContextMenuItem.COLOR_MENU.getValue();
  }

}
