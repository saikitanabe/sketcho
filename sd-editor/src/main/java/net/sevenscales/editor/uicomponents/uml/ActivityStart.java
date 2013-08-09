package net.sevenscales.editor.uicomponents.uml;


import net.sevenscales.editor.api.SurfaceHandler;
import net.sevenscales.editor.api.impl.Theme;
import net.sevenscales.editor.api.impl.Theme.ElementColorScheme;
import net.sevenscales.editor.content.ui.UMLDiagramSelections.UMLDiagramType;
import net.sevenscales.editor.content.utils.AreaUtils;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.shape.ActivityStartShape;
import net.sevenscales.editor.diagram.shape.Info;
import net.sevenscales.editor.diagram.utils.GridUtils;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.ICircle;
import net.sevenscales.editor.gfx.domain.IGroup;
import net.sevenscales.editor.gfx.domain.IShape;
import net.sevenscales.editor.gfx.domain.IShapeFactory;
import net.sevenscales.editor.gfx.domain.SupportsRectangleShape;
import net.sevenscales.editor.silver.SilverUtils;
import net.sevenscales.editor.uicomponents.AbstractDiagramItem;
import net.sevenscales.editor.uicomponents.Point;

public class ActivityStart extends AbstractDiagramItem implements SupportsRectangleShape {
	public static int ACTIVITY_START_RADIUS = 10;
	public static int BOUNDARY_RADIUS = 20;
//	private Rectangle rectSurface;
  private ICircle visible;
  private ICircle boundary;

	private int minimumWidth = 25;
	private int minimumHeight = 25;
	private ActivityStartShape shape;
	private Point coords = new Point();
//	private IRectangle resizeElement;
//	private boolean onResizeArea;
  private IGroup group;
  
  public ActivityStart(SurfaceHandler surface, ActivityStartShape newShape, boolean editable) {
    this(surface, newShape, Theme.createDefaultBorderColor(), Theme.createDefaultBorderColor(), Theme.createDefaultTextColor(), editable);
  }
  
	public ActivityStart(SurfaceHandler surface, ActivityStartShape newShape, Color backgroundColor, Color borderColor, Color textColor, boolean editable) {
		super(editable, surface, backgroundColor, borderColor, textColor);
		this.shape = newShape;
		
		group = IShapeFactory.Util.factory(editable).createGroup(surface.getElementLayer());
    group.setAttribute("cursor", "default");

		visible = IShapeFactory.Util.factory(editable).createCircle(group);
		visible.setStroke("#" + this.borderColor.toHexString());
		visible.setFill("#" + this.backgroundColor.toHexString());
		
		boundary = IShapeFactory.Util.factory(editable).createCircle(group);
		boundary.setStroke(0, 0, 0, 0);
		boundary.setFill(200, 200, 200, 0);
		
//		resizeElement = IShapeFactory.Util.factory(editable).createRectangle(group);
//		resizeElement.setFill(240, 255, 240, 0.4);
		
		addEvents(boundary);
		
		// resize support
//		resizeElement.addGraphicsMouseEnterHandler(new GraphicsMouseEnterHandler() {
//      public void onMouseEnter(GraphicsEvent event) {
//        onResizeArea = true;
//      }
//    });
//		resizeElement.addGraphicsMouseLeaveHandler(new GraphicsMouseLeaveHandler() {
//      public void onMouseLeave(GraphicsEvent event) {
//        onResizeArea = false;
//      }
//    });
		
//		resizeElement.addGraphicsMouseDownHandler(this);
//    resizeElement.addGraphicsMouseUpHandler(this);
		
		addMouseDiagramHandler(this);
		
    shapes.add(visible);
    shapes.add(boundary);
//    shapes.add(resizeElement);
    setReadOnly(!editable);
    setShape(shape.centerX, shape.centerY, shape.radius);
    
    super.constructorDone();
	}
	
	@Override
	protected int doGetLeft() {
		return visible.getX() - shape.radius;
	}
	@Override
	protected int doGetTop() {
		return visible.getY() - shape.radius;
	}
	@Override
	public int getWidth() {
		return 2 * shape.radius;
	}
	@Override
	public int getHeight() {
		return 2 * shape.radius;
	}
	
	@Override
	public void setShape(int left, int top, int width, int height) {
    visible.setShape(left + width / 2, top + height / 2, width / 2);
    boundary.setShape(left + width / 2, top + height / 2, BOUNDARY_RADIUS);
    connectionHelpers.setShape(getLeft(), getTop(), getWidth(), getHeight());
	}
	
	public void setShape(int cx, int cy, int radius) {
    visible.setShape(cx, cy, radius);
    boundary.setShape(cx, cy, BOUNDARY_RADIUS);
    connectionHelpers.setShape(getLeft(), getTop(), getWidth(), getHeight());
//    resizeElement.setShape(cx + radius * 2, cy, 10, 10, 0);
    
//    relationshipHandle.setShape(left+width/2, top);
	}
	
	public void saveLastTransform() {
	  // get transformation
    int dx = SilverUtils.getTransformX(group.getContainer());
    int dy = SilverUtils.getTransformY(group.getContainer());
	    
	  // reset transformations
    SilverUtils.resetRenderTransform(group.getContainer());
	    
    // apply transformations to shapes
    for (IShape s : shapes) {
      s.applyTransform(dx, dy);
    }
	}

	public Point getDiffFromMouseDownLocation() {
		return new Point(diffFromMouseDownX, diffFromMouseDownY);
	}
	
	public void accept(SurfaceHandler surface) {
	  super.accept(surface);
		surface.makeDraggable(this);
	}

	public void removeFromParent() {
		surface.remove(this);
    surface.remove(group.getContainer());
//		for (Shape s : shapes) {
//		  surface.remove(s.getRawNode());
//		}
	}
  
  @Override
	public Diagram duplicate(boolean partOfMultiple) {
		return duplicate(surface, partOfMultiple);
	}
	
  @Override
	public Diagram duplicate(SurfaceHandler surface, boolean partOfMultiple) {
		Point p = getCoords();
		return duplicate(surface, p.x + 20, p.y + 20);
	}
	
  @Override
  public Diagram duplicate(SurfaceHandler surface, int x, int y) {
    ActivityStartShape newShape = new ActivityStartShape(x, y, visible.getRadius());
    Diagram result = createDiagram(surface, newShape, getEditable());
    return result;
  }
	
  protected Diagram createDiagram(SurfaceHandler surface, ActivityStartShape newShape,
      boolean editable) {
    return new ActivityStart(surface, newShape, editable);
  }
	
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
		return resize(visible.getX(), visible.getY(), visible.getRadius() + diff.x, visible.getRadius() + diff.y);			
	}

	protected boolean resize(int left, int top, int width, int height) {
	   if (width >= minimumWidth && height >= minimumHeight) {
  	   left = GridUtils.align(left);
       top = GridUtils.align(top);
       width = GridUtils.align(width);
       height = GridUtils.align(height);
       
       setShape(left, top, width);
  		return true;
	   }
	   return false;
	}

	public void resizeEnd() {
	}

	public Info getInfo() {
	  shape.centerX = visible.getX() + getTransformX();
	  shape.centerY = visible.getY() + getTransformY();
	  shape.radius = visible.getRadius();
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
  	return UMLDiagramType.START;
  }
  
  @Override
	protected void doSetShape(int[] shape) {
    setShape(shape[0], shape[1], shape[2]);
	}
  
  public void setHighlightColor(String color) {
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

}
