package net.sevenscales.editor.uicomponents.uml;


import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.content.ui.UMLDiagramSelections.UMLDiagramType;
import net.sevenscales.editor.content.utils.AreaUtils;
import net.sevenscales.editor.content.utils.ContainerAttachHelpers;
import net.sevenscales.editor.diagram.ContainerType;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.shape.Info;
import net.sevenscales.editor.diagram.shape.HorizontalPartitionShape;
import net.sevenscales.editor.diagram.utils.GridUtils;
import net.sevenscales.editor.gfx.base.GraphicsEventHandler;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.IContainer;
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

import com.google.gwt.core.client.JavaScriptObject;

public class HorizontalPartitionElement extends AbstractDiagramItem implements SupportsRectangleShape, ContainerType {
  private IRectangle rectSurface;
  private IRectangle headerBackground;
  private int minimumWidth = 25;
  private int minimumHeight = 25;
  private HorizontalPartitionShape shape;
  private Point coords = new Point();
  private IGroup group;
  private TextElementFormatUtil textUtil;
  private static final int HEADER_HEIGHT = 25;
	
  public HorizontalPartitionElement(ISurfaceHandler surface, HorizontalPartitionShape newShape, String text, 
  		Color backgroundColor, Color borderColor, Color textColor, boolean editable, IDiagramItemRO item) {
    this(surface, newShape, text, backgroundColor, borderColor, textColor, editable, false, item);
  }
  
  public HorizontalPartitionElement(ISurfaceHandler surface, HorizontalPartitionShape newShape, String text, 
  		Color backgroundColor, Color borderColor, Color textColor, boolean editable, boolean delayText, IDiagramItemRO item) {
    super(editable, surface, backgroundColor, borderColor, textColor, item);
    this.shape = newShape;
    
    group = IShapeFactory.Util.factory(editable).createGroup(surface.getContainerLayer());
    group.setAttribute("cursor", "default");

    // set clipping area => text is visible only within canvas boundary
//    group.setClip(shape.left, shape.top, shape.width, shape.height);

    rectSurface = (IRectangle) createElement(group);
//    rectSurface.setAttribute("cursor", "pointer");
    // rectSurface.setShape(shape.rectShape.left, shape.rectShape.top, shape.rectShape.width, shape.rectShape.height, 2);
    rectSurface.setStrokeWidth(2.0);
    rectSurface.setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, 0); // force transparent
    
    headerBackground = (IRectangle) createElement(group);
    // headerBackground.setShape(shape.rectShape.left, shape.rectShape.top, HEADER_HEIGHT, shape.rectShape.height, 2);
    headerBackground.setStrokeWidth(2.0);
    headerBackground.setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, backgroundColor.opacity);
    
    addEvents(headerBackground);

    addMouseDiagramHandler(this);
    
    shapes.add(rectSurface);
    shapes.add(headerBackground);
    
    resizeHelpers = ResizeHelpers.createResizeHelpers(surface);
    textUtil = new TextElementFormatUtil(this, hasTextElement, group, surface.getEditorContext());
    textUtil.setMarginTop(0);
    textUtil.setRotate(-90);
    
    if (!delayText) {
    	setText(text);
    }

    setShape(shape.rectShape.left, shape.rectShape.top, shape.rectShape.width, shape.rectShape.height);

    setReadOnly(!editable);
    
    setBorderColor(borderColor);
    super.constructorDone();
  }
  
  // nice way to clearly separate interface methods :)
  private HasTextElement hasTextElement = new AbstractHasTextElement() {
    public void addShape(IShape shape) {
      shapes.add(shape);    
    }
    public int getWidth() {
      return rectSurface.getWidth();
    }
    public int getX() {
      return rectSurface.getX();
    }
    public int getY() {
      return rectSurface.getY();
    }
    public int getHeight() {
      return rectSurface.getHeight();
    }
    
    public void removeShape(IShape shape) {
      group.remove(shape);
      shapes.remove(shape);
    }

    public String getLink() {
      return HorizontalPartitionElement.this.getLink();
    }

    public boolean isAutoResize() {
      return false;
    }

    public void resize(int x, int y, int width, int height) {
      // Text Element doesn't support resize
      HorizontalPartitionElement.this.resize(x, y, width, height);
    }

    public void setLink(String link) {
      HorizontalPartitionElement.this.setLink(link);      
    }
    public boolean supportsTitleCenter() {
      return false;
    }
    public boolean forceAutoResize() {
      return false;
    }
    
    public GraphicsEventHandler getGraphicsMouseHandler() {
      return HorizontalPartitionElement.this;
    };
    
		@Override
		public String getTextColorAsString() {
			return "#" + textColor.toHexString();
		};

  };

  protected IShape createElement(IContainer surface) {
    return IShapeFactory.Util.factory(editable).createRectangle(surface);
  }

  public Point getDiffFromMouseDownLocation() {
    return new Point(diffFromMouseDownX, diffFromMouseDownY);
  }
  
  public void accept(ISurfaceHandler surface) {
    super.accept(surface);
    surface.makeDraggable(this);
  }
  
  public AnchorElement onAttachArea(Anchor anchor, int x, int y) {
  	return ContainerAttachHelpers.onAttachArea(this, anchor, x, y);
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
    return duplicate(surface, p.x, p.y + getHeight());
  }
  
  @Override
  public Diagram duplicate(ISurfaceHandler surface, int x, int y) {
  	HorizontalPartitionShape newShape = new HorizontalPartitionShape(x, y, rectSurface.getWidth() * factorX, rectSurface.getHeight() * factorY);
    Diagram result = createDiagram(surface, newShape, getText(), getEditable());
    return result;
  }
  
  protected Diagram createDiagram(ISurfaceHandler surface, HorizontalPartitionShape newShape,
      String text, boolean editable) {
    return new HorizontalPartitionElement(surface, newShape, text, new Color(backgroundColor), new Color(borderColor), new Color(textColor), editable, new DiagramItemDTO());
  }
  

//////////////////////////////////////////////////////////////////////
  
  public boolean onResizeArea(int x, int y) {
    return resizeHelpers.isOnResizeArea();
  }

  public JavaScriptObject getResizeElement() {
    return rectSurface.getRawNode();
  }
  
  public void resizeStart() {
  }

  public boolean resize(Point diff) {
    return resize(rectSurface.getX(), rectSurface.getY(), 
                  rectSurface.getWidth() + diff.x, rectSurface.getHeight() + diff.y);     
  }
  
  public void setShape(int left, int top, int width, int height) {
    rectSurface.setShape(left, top, width, height, 2);
    headerBackground.setShape(left, top, HEADER_HEIGHT, height, 2);
    textUtil.setTextShape();
  }

  protected boolean resize(int left, int top, int width, int height) {
    if (width >= minimumWidth && height >= minimumHeight) {
      setShape(left, top, width, height);
      super.applyHelpersShape();
      dispatchAndRecalculateAnchorPositions();
      return true;
    }
    return false;
  }
  
  @Override
  public int getResizeIndentX() {
  	return 0;
  }

  /**
   * subclasses to override to decide own resize anchor algorithm.
   * @return
   */
  protected boolean resizeAnchors() {
    return true;
  }

  public void resizeEnd() {
  }

  public Info getInfo() {
    shape.rectShape.left = rectSurface.getX();
    shape.rectShape.top = rectSurface.getY();
    shape.rectShape.width = rectSurface.getWidth();
    shape.rectShape.height = rectSurface.getHeight();
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
  	return UMLDiagramType.PACKAGE;
  }
  
  @Override
	public void setBackgroundColor(int red, int green, int blue, double opacity) {
  	super.setBackgroundColor(red, green, blue, opacity);
    rectSurface.setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, 0);
    headerBackground.setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, backgroundColor.opacity);
  }
  
  @Override
	public void setTextColor(int red, int green, int blue) {
  	super.setTextColor(red, green, blue);
  	textUtil.applyTextColor();
  }
  
  @Override
  public void moveToBack() {
  	group.moveToBack();
  }
  
  @Override
  protected int doGetLeft() {
  	return rectSurface.getX();
  }
  
  @Override
  protected int doGetTop() {
  	return rectSurface.getY();
  }
  
  @Override
  public int getWidth() {
  	return rectSurface.getWidth();
  }
  @Override
  public int getHeight() {
  	return rectSurface.getHeight();
  }
  
  @Override
  protected void doSetShape(int[] shape) {
  	setShape(shape[0], shape[1], shape[2], shape[3]);
  }

  @Override
  public void setHighlightColor(String color) {
    rectSurface.setStroke(color);
    headerBackground.setStroke(color);
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
	public int getTextAreaHeight() {
		return HEADER_HEIGHT;
	}
	
	@Override
	public int getTextAreaTop() {
		return getTop() + 5;
	}
	
	@Override
	public String getTextAreaAlign() {
		return "center";
	}
	
  @Override
  public boolean supportsTextEditing() {
  	return true;
  }

}
