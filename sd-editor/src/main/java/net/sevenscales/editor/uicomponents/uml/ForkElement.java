package net.sevenscales.editor.uicomponents.uml;


import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.impl.Theme;
import net.sevenscales.editor.api.impl.Theme.ElementColorScheme;
import net.sevenscales.editor.content.ui.ContextMenuItem;
import net.sevenscales.editor.content.ui.UMLDiagramType;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.shape.ForkShape;
import net.sevenscales.editor.diagram.shape.Info;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.IGroup;
import net.sevenscales.editor.gfx.domain.ILine;
import net.sevenscales.editor.gfx.domain.IRectangle;
import net.sevenscales.editor.gfx.domain.IShapeFactory;
import net.sevenscales.editor.gfx.domain.Point;
import net.sevenscales.editor.gfx.domain.SupportsRectangleShape;
import net.sevenscales.editor.uicomponents.AbstractDiagramItem;
import net.sevenscales.editor.uicomponents.helpers.ResizeHelpers;


public class ForkElement extends AbstractDiagramItem implements SupportsRectangleShape {
	public static int ACTIVITY_START_RADIUS = 10;
	public static int BOUNDARY_RADIUS = 20;
//	private Rectangle rectSurface;
  private IRectangle visible;
  private ILine boundary;

	private int minimumWidth = 25;
	private int minimumHeight = 25;
	private ForkShape shape;
	private Point coords = new Point();
  private IGroup group;
  
  public ForkElement(ISurfaceHandler surface, ForkShape newShape, boolean editable, IDiagramItemRO item) {
    this(surface, newShape, Theme.createDefaultBorderColor(), Theme.createDefaultBorderColor(), Theme.createDefaultTextColor(), editable, item);
  }

	public ForkElement(ISurfaceHandler surface, ForkShape newShape, Color backgroundColor, Color borderColor, Color textColor, boolean editable, IDiagramItemRO item) {
		super(editable, surface, backgroundColor, borderColor, textColor, item);
		this.shape = newShape;
		
		group = IShapeFactory.Util.factory(editable).createGroup(surface.getElementLayer());
    // group.setAttribute("cursor", "default");

		visible = IShapeFactory.Util.factory(editable).createRectangle(group);
		visible.setStroke(borderColor.red, borderColor.green, borderColor.blue, borderColor.opacity);
		visible.setFill(borderColor.red, borderColor.green, borderColor.blue, borderColor.opacity);
		
		boundary = IShapeFactory.Util.factory(editable).createLine(group);
		boundary.setStroke(0, 0, 0, 0);
		boundary.setFill(200, 200, 200, 0);
		boundary.setStrokeWidth(10);
		
		resizeHelpers = ResizeHelpers.createResizeHelpers(surface);
		
		addEvents(boundary);
		
		addMouseDiagramHandler(this);
		
    shapes.add(visible);
    setReadOnly(!editable);
    setShape(shape.rectShape.left, shape.rectShape.top, shape.rectShape.width, shape.rectShape.height);

    setBorderColor(borderColor);
    
    super.constructorDone();
	}
	
	@Override
	public int getRelativeLeft() {
		return visible.getX();
	}
	@Override
	public int getRelativeTop() {
		return visible.getY();
	}
	@Override
	public int getWidth() {
		return visible.getWidth();
	}
	@Override
	public int getHeight() {
		return visible.getHeight();
	}
	
	@Override
	public void setShape(int left, int top, int width, int height) {
    visible.setShape(left, top, width, height, 3);
    
		int x2 = shape.orientation == 0 ? left + width: left;
		int y2 = shape.orientation == 0 ? top : top + height;
    boundary.setShape(left, top, x2, y2);
    boundary.setStrokeWidth(10);
    super.applyHelpersShape();
	}
	
	public Point getDiffFromMouseDownLocation() {
		return new Point(diffFromMouseDownX, diffFromMouseDownY);
	}
	
	public void accept(ISurfaceHandler surface) {
	  super.accept(surface);
		surface.makeDraggable(this);
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
    ForkShape newShape = new ForkShape(x, y, getWidth(), getHeight(), shape.orientation);
    Diagram result = createDiagram(surface, newShape, getEditable());
    return result;
  }
	
  protected Diagram createDiagram(ISurfaceHandler surface, ForkShape newShape,
      boolean editable) {
    return new ForkElement(surface, newShape, editable, new DiagramItemDTO());
  }
	
//////////////////////////////////////////////////////////////////////
	
//	public boolean onResizeArea(int x, int y) {
//		return onResizeArea;
//	}

//	public JavaScriptObject getResizeElement() {
//		return rectSurface.getRawNode();
//	}
	
	public boolean resize(Point diff) {
		int width = visible.getWidth();
		width = shape.orientation == 0 ? width + diff.x : width;

		int height = visible.getHeight();
		height = shape.orientation == 1 ? height + diff.y : height;
		return resize(visible.getX(), visible.getY(), width, height);
	}

	protected boolean resize(int left, int top, int width, int height) {
	  if ( (shape.orientation == 0 && width >= minimumWidth) || (shape.orientation == 1 && height >= minimumHeight) ) {
      setShape(left, top, width, height);
      dispatchAndRecalculateAnchorPositions();
  		return true;
	  }
	  return false;
	}

	public void resizeEnd() {
	}

  public int getResizeIndentX() {
  	return shape.orientation == 0 ? -7 : -10;
  }

  public int getResizeIndentY() {
  	return -10;
  }

	public Info getInfo() {
    super.fillInfo(shape);
		return this.shape;
	}

	public void setShape(Info shape) {
		// TODO Auto-generated method stub
		
	}

	public void setReadOnly(boolean value) {
	  super.setReadOnly(value);
	  boundary.setVisibility(!value);
//	  resizeElement.setVisibility(!value);
	}
	
  public String getDefaultRelationship() {
    return "->";
  }
  
  @Override
  public UMLDiagramType getDiagramType() {
  	if (shape.orientation == 0) {
	  	return UMLDiagramType.FORK;
  	}
  	return UMLDiagramType.VFORK;
  }
  
  @Override
	protected void doSetShape(int[] shape) {
    setShape(shape[0], shape[1], shape[2], shape[3]);
	}
  
  public void setHighlightColor(Color color) {
  	visible.setStroke(color);
  }
  
  @Override
  public void setBackgroundColor(int red, int green, int blue, double opacity) {
    super.setBackgroundColor(red, green, blue, opacity);
    visible.setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, backgroundColor.opacity);
  }
  
	@Override
	public IGroup getGroup() {
		return group;
	}
	
	@Override
	public Color getDefaultBackgroundColor(ElementColorScheme colorScheme) {
	  return colorScheme.getBorderColor();
	}

  @Override
  public int supportedMenuItems() {
    return super.supportedMenuItems() |
           ContextMenuItem.LAYERS.getValue();
  }

}
