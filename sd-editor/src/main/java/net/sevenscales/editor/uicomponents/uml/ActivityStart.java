package net.sevenscales.editor.uicomponents.uml;


import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.impl.Theme;
import net.sevenscales.editor.api.impl.Theme.ElementColorScheme;
import net.sevenscales.editor.content.ui.ContextMenuItem;
import net.sevenscales.editor.content.ui.UMLDiagramType;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.shape.ActivityStartShape;
import net.sevenscales.editor.diagram.shape.Info;
import net.sevenscales.editor.diagram.utils.GridUtils;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.ICircle;
import net.sevenscales.editor.gfx.domain.IGroup;
import net.sevenscales.editor.gfx.domain.IShapeFactory;
import net.sevenscales.editor.gfx.domain.Point;
import net.sevenscales.editor.gfx.domain.SupportsRectangleShape;
import net.sevenscales.editor.uicomponents.AbstractDiagramItem;


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
  
  public ActivityStart(ISurfaceHandler surface, ActivityStartShape newShape, boolean editable, IDiagramItemRO item) {
    this(surface, newShape, Theme.createDefaultBorderColor(), Theme.createDefaultBorderColor(), Theme.createDefaultTextColor(), editable, item);
  }
  
	public ActivityStart(ISurfaceHandler surface, ActivityStartShape newShape, Color backgroundColor, Color borderColor, Color textColor, boolean editable, IDiagramItemRO item) {
		super(editable, surface, backgroundColor, borderColor, textColor, item);
		this.shape = newShape;
		
		group = IShapeFactory.Util.factory(editable).createGroup(surface.getElementLayer());
    // group.setAttribute("cursor", "default");

		visible = IShapeFactory.Util.factory(editable).createCircle(group);
		visible.setStroke(this.borderColor);
		visible.setFill(this.backgroundColor);
		
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
    setReadOnly(!editable);
    setShape(shape.centerX, shape.centerY, shape.radius);

    setBorderColor(borderColor);
    
    super.constructorDone();
	}
	
	@Override
	public int getRelativeLeft() {
		return visible.getX() - shape.radius;
	}
	@Override
	public int getRelativeTop() {
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
    connectionHelpers.setShape(getLeft(), getTop(), getWidth(), getHeight(), getDiagramItem().getRotateDegrees());
	}
	
	public void setShape(int cx, int cy, int radius) {
    visible.setShape(cx, cy, radius);
    boundary.setShape(cx, cy, BOUNDARY_RADIUS);
    connectionHelpers.setShape(getLeft(), getTop(), getWidth(), getHeight(), getDiagramItem().getRotateDegrees());
//    resizeElement.setShape(cx + radius * 2, cy, 10, 10, 0);
    
//    relationshipHandle.setShape(left+width/2, top);
	}
	
	// public void saveLastTransform() {
	//   // get transformation
 //    int dx = SilverUtils.getTransformX(group.getContainer());
 //    int dy = SilverUtils.getTransformY(group.getContainer());
	    
	//   // reset transformations
 //    SilverUtils.resetRenderTransform(group.getContainer());
	    
 //    // apply transformations to shapes
 //    for (IShape s : shapes) {
 //      s.applyTransform(dx, dy);
 //    }
	// }

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
    ActivityStartShape newShape = new ActivityStartShape(x, y, visible.getRadius());
    Diagram result = createDiagram(surface, newShape, getEditable());
    return result;
  }
	
  protected Diagram createDiagram(ISurfaceHandler surface, ActivityStartShape newShape,
      boolean editable) {
    return new ActivityStart(surface, newShape, editable, new DiagramItemDTO());
  }
	
//////////////////////////////////////////////////////////////////////
	
//	public boolean onResizeArea(int x, int y) {
//		return onResizeArea;
//	}

//	public JavaScriptObject getResizeElement() {
//		return rectSurface.getRawNode();
//	}
	
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
  
  public void setHighlightColor(Color color) {
  	visible.setStroke(color);
  }
  
  @Override
	public void setBackgroundColor(Color color) {
    super.setBackgroundColor(color);
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
