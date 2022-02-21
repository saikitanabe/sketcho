package net.sevenscales.editor.uicomponents.uml;

import java.util.ArrayList;
import java.util.List;

import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.content.ui.ContextMenuItem;
import net.sevenscales.editor.content.ui.UMLDiagramType;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.shape.ActivityShape;
import net.sevenscales.editor.diagram.shape.HasRectShape;
import net.sevenscales.editor.diagram.shape.Info;
import net.sevenscales.editor.gfx.base.GraphicsEventHandler;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.IGroup;
import net.sevenscales.editor.gfx.domain.IRectangle;
import net.sevenscales.editor.gfx.domain.IShape;
import net.sevenscales.editor.gfx.domain.IShapeFactory;
import net.sevenscales.editor.gfx.domain.Point;
import net.sevenscales.editor.gfx.domain.SupportsRectangleShape;
import net.sevenscales.editor.uicomponents.AbstractDiagramItem;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil.AbstractHasTextElement;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil.HasTextElement;
import net.sevenscales.editor.uicomponents.helpers.ResizeHelpers;


class BaseCorporateElement extends AbstractDiagramItem implements SupportsRectangleShape {

  protected HasRectShape shape;
  protected IRectangle boundary;
	protected int minimumWidth = 25;
	protected int minimumHeight = 25;
	protected Point coords = new Point();
  // utility shape container to align text and make separators
  protected List<IShape> innerShapes = new ArrayList<IShape>();
  private IGroup group;
  // private IGroup subgroup;
  private IGroup rotategroup;
  private IGroup textGroup;
  protected int[] points;
  protected TextElementFormatUtil textUtil;
  protected long dispachSequence;

  BaseCorporateElement(
    ISurfaceHandler surface,
    HasRectShape newShape,
    String text, 
    Color backgroundColor,
    Color borderColor,
    Color textColor,
    boolean editable,
    IDiagramItemRO item
  ) {
    super(editable, surface, backgroundColor, borderColor, textColor, item);

    this.shape = newShape;

		group = IShapeFactory.Util.factory(editable).createGroup(surface.getElementLayer());
    rotategroup = IShapeFactory.Util.factory(editable).createGroup(group);
		// subgroup = IShapeFactory.Util.factory(editable).createGroup(rotategroup);
		// order is important or cannot move shape with background
    boundary = IShapeFactory.Util.factory(editable).createRectangle(group);
    boundary.setFill(0, 0 , 0, 0); // transparent

		// separate text group is needed or can't interact with links that are behind
		// background rectangle
		textGroup = IShapeFactory.Util.factory(editable).createGroup(group);

    addEvents(boundary);

		addMouseDiagramHandler(this);
		
    shapes.add(boundary);

    resizeHelpers = createResizeHelpers();
    setReadOnly(!editable);
    setDimensions(shape.rectShape.left, shape.rectShape.top, 
             shape.rectShape.width, shape.rectShape.height);
    textUtil = new TextElementFormatUtil(this, hasTextElement, textGroup, surface.getEditorContext());
    
    setText(text);
    setBorderColor(borderColor);

    setShape(shape.rectShape.left, shape.rectShape.top, shape.rectShape.width, shape.rectShape.height);    
  }

  private HasTextElement hasTextElement = new AbstractHasTextElement(this) {
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
      return BaseCorporateElement.this.getLink();
    }

    public boolean isAutoResize() {
      return BaseCorporateElement.this.isAutoResize();
    }

    public void resize(int x, int y, int width, int height) {
      // Text Element doesn't support resize
      BaseCorporateElement.this.resize(x, y, width, height);
      fireSizeChanged();
    }

    public void setLink(String link) {
      BaseCorporateElement.this.setLink(link);      
    }
    public boolean supportsTitleCenter() {
      return true;
    }
    public boolean forceAutoResize() {
      return true;
    }
    
    public GraphicsEventHandler getGraphicsMouseHandler() {
      return BaseCorporateElement.this;
    };
    
		@Override
		public Color getTextColor() {
			return textColor;
		};

  };

	protected ResizeHelpers createResizeHelpers() {
		return ResizeHelpers.createResizeHelpers(surface);
	}

	protected double getStrokeWidth() {
		return STROKE_WIDTH;
	}

	protected void setDimensions(int left, int top, int width, int height) {
    // group.setTransform(left, top);
    boundary.setShape(left, top, width, height, 0);
	}

	public void setShape(int left, int top, int width, int height) {
    setDimensions(left, top, width, height);
    textGroup.setTransform(left, top);
    textUtil.setShapeSize(width, height);
    super.applyHelpersShape();
	}

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
    textUtil.setText(newText, editable, isForceTextRendering());
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
	public void setTextColor(int red, int green, int blue) {
  	super.setTextColor(red, green, blue);
  	textUtil.applyTextColor();
  }
  
  @Override
  public int getRelativeLeft() {
  	return boundary.getX();
  }
  
  @Override
  public int getRelativeTop() {
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
	public IGroup getGroup() {
		return group;
	}

  @Override
	public IGroup getTextGroup() {
		return textGroup;
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
    return super.supportedMenuItems() | ContextMenuItem.FONT_SIZE.getValue() |
           ContextMenuItem.LAYERS.getValue();
  }
  
}