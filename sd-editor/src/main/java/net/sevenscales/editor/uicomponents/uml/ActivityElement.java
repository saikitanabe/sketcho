package net.sevenscales.editor.uicomponents.uml;


import java.util.ArrayList;
import java.util.List;

import net.sevenscales.editor.content.ui.ContextMenuItem;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.content.ui.UMLDiagramSelections.UMLDiagramType;
import net.sevenscales.editor.content.utils.AreaUtils;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.shape.ActivityShape;
import net.sevenscales.editor.diagram.shape.HasRectShape;
import net.sevenscales.editor.diagram.shape.Info;
import net.sevenscales.editor.diagram.utils.GridUtils;
import net.sevenscales.editor.gfx.base.GraphicsEventHandler;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.IGroup;
import net.sevenscales.editor.gfx.domain.IRectangle;
import net.sevenscales.editor.gfx.domain.IShape;
import net.sevenscales.editor.gfx.domain.IShapeFactory;
import net.sevenscales.editor.gfx.domain.SupportsRectangleShape;
import net.sevenscales.editor.uicomponents.AbstractDiagramItem;
import net.sevenscales.editor.uicomponents.Anchor;
import net.sevenscales.editor.uicomponents.AnchorElement;
import net.sevenscales.editor.uicomponents.Point;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil.AbstractHasTextElement;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil.HasTextElement;
import net.sevenscales.editor.uicomponents.helpers.ResizeHelpers;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.DiagramItemDTO;

public class ActivityElement extends AbstractDiagramItem implements SupportsRectangleShape {
//	private Rectangle rectSurface;
  private IRectangle boundary;
	private int minimumWidth = 25;
	private int minimumHeight = 25;
	private HasRectShape shape;
	private Point coords = new Point();
  // utility shape container to align text and make separators
  private List<IShape> innerShapes = new ArrayList<IShape>();
  private IGroup group;
  private int[] points;
  protected TextElementFormatUtil textUtil;
  private long dispachSequence;
  
	public ActivityElement(ISurfaceHandler surface, HasRectShape newShape, String text, 
												 Color backgroundColor, Color borderColor, Color textColor, boolean editable, IDiagramItemRO item) {
		super(editable, surface, backgroundColor, borderColor, textColor, item);
		this.shape = newShape;
		
		group = IShapeFactory.Util.factory(editable).createGroup(surface.getElementLayer());
    group.setAttribute("cursor", "default");

		boundary = IShapeFactory.Util.factory(editable).createRectangle(group);
		boundary.setStroke(borderWebColor);
		boundary.setStrokeWidth(getStrokeWidth());
		boundary.setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, backgroundColor.opacity);
		
		addEvents(boundary);

		addMouseDiagramHandler(this);
		
    shapes.add(boundary);
    
    resizeHelpers = createResizeHelpers();
    setReadOnly(!editable);
    setDimensions(shape.rectShape.left, shape.rectShape.top, 
             shape.rectShape.width, shape.rectShape.height);
    textUtil = new TextElementFormatUtil(this, hasTextElement, group, surface.getEditorContext());
    
    setText(text);
    setBorderColor(borderColor);
    
    super.constructorDone();
	}
	
	protected ResizeHelpers createResizeHelpers() {
		return ResizeHelpers.createResizeHelpers(surface);
	}

	protected double getStrokeWidth() {
		return STROKE_WIDTH;
	}

	private void setDimensions(int left, int top, int width, int height) {
    boundary.setShape(left, top, width, height, 9);
	}

	public void setShape(int left, int top, int width, int height) {
    setDimensions(left, top, width, height);
    textUtil.setTextShape();
    super.applyHelpersShape();
	}
	
  // nice way to clearly separate interface methods :)
  private HasTextElement hasTextElement = new AbstractHasTextElement() {
    public void addShape(IShape shape) {
      shapes.add(shape);    
    }
    public int getWidth() {
      return boundary.getWidth();
    }
    public int getX() {
    	return boundary.getX();
    }
    public int getY() {
    	return boundary.getY();
    }
    
    public int getHeight() {
    	return boundary.getHeight();
    }
    
    public void removeShape(IShape shape) {
      group.remove(shape);
      shapes.remove(shape);
    }

    public String getLink() {
      return ActivityElement.this.getLink();
    }

    public boolean isAutoResize() {
      return ActivityElement.this.isAutoResize();
    }

    public void resize(int x, int y, int width, int height) {
      // Text Element doesn't support resize
      ActivityElement.this.resize(x, y, width, height);
      fireSizeChanged();
    }

    public void setLink(String link) {
      ActivityElement.this.setLink(link);      
    }
    public boolean supportsTitleCenter() {
      return true;
    }
    public boolean forceAutoResize() {
      return true;
    }
    
    public GraphicsEventHandler getGraphicsMouseHandler() {
      return ActivityElement.this;
    };
    
		@Override
		public String getTextColorAsString() {
			return "#" + textColor.toHexString();
		};

  };

	public Point getDiffFromMouseDownLocation() {
		return new Point(diffFromMouseDownX, diffFromMouseDownY);
	}
	
	public void accept(ISurfaceHandler surface) {
	  super.accept(surface);
		surface.makeDraggable(this);
	}
  
	public String getText() {
		return textUtil.getText();
	}

	public void doSetText(String newText) {
    textUtil.setText(newText, editable);
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
    ActivityShape newShape = new ActivityShape(x, y, 
    		boundary.getWidth(), boundary.getHeight());
    Diagram result = createDiagram(surface, newShape, getText(), getEditable());
    return result;
  }
	
  protected Diagram createDiagram(ISurfaceHandler surface, ActivityShape newShape,
      String text, boolean editable) {
    return new ActivityElement(surface, newShape, text, new Color(backgroundColor), new Color(borderColor), new Color(textColor), editable, new DiagramItemDTO());
  }
	
//////////////////////////////////////////////////////////////////////
	
	public boolean onResizeArea(int x, int y) {
		return resizeHelpers.isOnResizeArea();
	}

	public void resizeStart() {
	}

	public boolean resize(Point diff) {
		return resize(boundary.getX(), boundary.getY(), boundary.getWidth() + diff.x, boundary.getHeight() + diff.y);			
	}

	protected boolean resize(int left, int top, int width, int height) {
    if (width >= minimumWidth && height >= minimumHeight) {
      setShape(left, top, width, height);
      dispatchAndRecalculateAnchorPositions();
      return true;
    }
	   return false;
	}

	public void resizeEnd() {
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
	}
	
  public String getDefaultRelationship() {
    return "->";
  }
  
  @Override
  public UMLDiagramType getDiagramType() {
  	return UMLDiagramType.ACTIVITY;
  }
  
  @Override
	public void setBackgroundColor(int red, int green, int blue, double opacity) {
  	super.setBackgroundColor(red, green, blue, opacity);
  	boundary.setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, backgroundColor.opacity);
  }
  
  @Override
	public void setTextColor(int red, int green, int blue) {
  	super.setTextColor(red, green, blue);
  	textUtil.applyTextColor();
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
	protected void doSetShape(int[] shape) {
  	setShape(shape[0], shape[1], shape[2], shape[3]);
	}
  
  @Override
	public void setHighlightColor(String color) {
		boundary.setStroke(color);
	}
	
	@Override
	public IGroup getGroup() {
		return group;
	}

	@Override
	protected TextElementFormatUtil getTextFormatter() {
		return textUtil;
	}
	
  @Override
  public boolean supportsTextEditing() {
  	return true;
  }

  @Override
  public int supportedMenuItems() {
    return super.supportedMenuItems() | ContextMenuItem.FONT_SIZE.getValue();
  }

}
