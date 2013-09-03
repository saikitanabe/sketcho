package net.sevenscales.editor.uicomponents.uml;


import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.content.ui.UMLDiagramSelections.UMLDiagramType;
import net.sevenscales.editor.content.utils.AreaUtils;
import net.sevenscales.editor.diagram.ContainerType;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.shape.Info;
import net.sevenscales.editor.diagram.shape.RectContainerShape;
import net.sevenscales.editor.diagram.utils.GridUtils;
import net.sevenscales.editor.gfx.base.GraphicsEvent;
import net.sevenscales.editor.gfx.base.GraphicsEventHandler;
import net.sevenscales.editor.gfx.base.GraphicsMouseEnterHandler;
import net.sevenscales.editor.gfx.base.GraphicsMouseLeaveHandler;
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
import net.sevenscales.editor.uicomponents.AnchorUtils;
import net.sevenscales.editor.uicomponents.Point;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil.AbstractHasTextElement;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil.HasTextElement;

import com.google.gwt.core.client.JavaScriptObject;

public class RectBoundaryVerticalElement extends AbstractDiagramItem implements SupportsRectangleShape, ContainerType {
  private IRectangle rectSurface;
  private IRectangle headerBackground;
  private int minimumWidth = 25;
  private int minimumHeight = 25;
  private RectContainerShape shape;
  private Point coords = new Point();
  private IRectangle resizeElement;
  private boolean onResizeArea;
  private IGroup group;
  private TextElementFormatUtil textUtil;
  private static final int HEADER_HEIGHT = 25;
	
  public RectBoundaryVerticalElement(ISurfaceHandler surface, RectContainerShape  newShape, String text, Color backgroundColor, Color borderColor, Color textColor, boolean editable) {
    this(surface, newShape, text, backgroundColor, borderColor, textColor, editable, false);
  }
  
  public RectBoundaryVerticalElement(ISurfaceHandler surface, RectContainerShape newShape, String text, 
  		Color backgroundColor, Color borderColor, Color textColor, boolean editable, boolean delayText) {
    super(editable, surface, backgroundColor, borderColor, textColor);
    this.shape = newShape;
    
    group = IShapeFactory.Util.factory(editable).createGroup(surface.getContainerLayer());
    group.setAttribute("cursor", "default");

    // set clipping area => text is visible only within canvas boundary
//    group.setClip(shape.left, shape.top, shape.width, shape.height);

    rectSurface = (IRectangle) createElement(group);
//    rectSurface.setAttribute("cursor", "pointer");
    rectSurface.setShape(shape.rectShape.left, shape.rectShape.top, shape.rectShape.width, shape.rectShape.height, 2);
    rectSurface.setStrokeWidth(3.0);
    rectSurface.setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, 0); // force transparent
    
    headerBackground = (IRectangle) createElement(group);
    headerBackground.setShape(shape.rectShape.left, shape.rectShape.top, shape.rectShape.width, HEADER_HEIGHT, 2);
    headerBackground.setStrokeWidth(3.0);
    headerBackground.setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, backgroundColor.opacity);
    
    resizeElement = IShapeFactory.Util.factory(editable).createRectangle(group);
    resizeElement.setShape(shape.rectShape.left + shape.rectShape.width - 10, shape.rectShape.top + shape.rectShape.height - 10, 10, 10, 1);
    resizeElement.setFill(200, 200, 200, 0.4);

//    addObserver(rectSurface.getRawNode(), AbstractDiagramItem.EVENT_DOUBLE_CLICK);
//    rectSurface.addGraphicsMouseDownHandler(this);
//    rectSurface.addGraphicsMouseUpHandler(this);
//    rectSurface.addGraphicsMouseMoveHandler(this);
//    rectSurface.addGraphicsMouseEnterHandler(this);
//    rectSurface.addGraphicsMouseLeaveHandler(this);
    
    addEvents(headerBackground);

    // resize support
    resizeElement.addGraphicsMouseEnterHandler(new GraphicsMouseEnterHandler() {
      public void onMouseEnter(GraphicsEvent event) {
        onResizeArea = true;
      }
    });
    resizeElement.addGraphicsMouseLeaveHandler(new GraphicsMouseLeaveHandler() {
      public void onMouseLeave(GraphicsEvent event) {
        onResizeArea = false;
      }
    });

    resizeElement.addGraphicsMouseDownHandler(this);
    resizeElement.addGraphicsMouseUpHandler(this);
    
    addMouseDiagramHandler(this);
    
    shapes.add(rectSurface);
    shapes.add(headerBackground);
    shapes.add(resizeElement);
    
    textUtil = new TextElementFormatUtil(this, hasTextElement, group, surface.getEditorContext());
    textUtil.setMarginTop(0);
    
    if (!delayText) {
    	setText(text);
    }

    setReadOnly(!editable);
    moveToBack();
    
    setBorderColor(borderWebColor);
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
      return RectBoundaryVerticalElement.this.getLink();
    }

    public boolean isAutoResize() {
      return false;
    }

    public void resize(int x, int y, int width, int height) {
      // Text Element doesn't support resize
      RectBoundaryVerticalElement.this.resize(x, y, width, height);
    }

    public void setLink(String link) {
      RectBoundaryVerticalElement.this.setLink(link);      
    }
    public boolean supportsTitleCenter() {
      return true;
    }
    public int getTextMargin() {
      return 30;
    }
    public boolean forceAutoResize() {
      return false;
    }
    
    public GraphicsEventHandler getGraphicsMouseHandler() {
      return RectBoundaryVerticalElement.this;
    };
    
		@Override
		public String getTextColorAsString() {
			return "#444444"; // #" + textColor.toHexString();
		};

  };


  protected IShape createElement(IContainer surface) {
    return IShapeFactory.Util.factory(editable).createRectangle(surface);
  }

  public void saveLastTransform() {
    // get transformation
    int dx = group.getTransformX();
    int dy = group.getTransformY();
      
    // reset transformations
//    SilverUtils.resetRenderTransform(group.getContainer());
    group.resetTransform();
      
    // apply transformations to shapes
    for (IShape s : shapes) {
      s.applyTransform(dx, dy);
    }
  }

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
  
  public AnchorElement onAttachArea(Anchor anchor, int x, int y) {
		// container attach is different only border areas can attach
		// TODO make this as utility to be used by other container elements
	//	return super.onAttachArea(anchor, x, y, rectSurface.getX(), rectSurface.getY() - CORNER_HEIGHT, rectSurface.getWidth(), rectSurface.getHeight() + CORNER_HEIGHT);
		// put all values to 0 not to attach any other than connection handle
		AnchorElement a = super.onAttachArea(anchor, x, y, 0, 0, 0, 0);
		if (a != null) {
			return a;
		}
		
	  if (AnchorUtils.onAttachArea(x, y, rectSurface.getX(), rectSurface.getY(), rectSurface.getWidth(), rectSurface.getHeight(), 20)) {
	    AnchorElement result = getAnchorElement(anchor);
	    AnchorUtils.anchorPoint(x, y, tempAnchorProperties , rectSurface.getX(), rectSurface.getY(), rectSurface.getWidth(), rectSurface.getHeight());
	
	    result.setAx(tempAnchorProperties.x);
	    result.setAy(tempAnchorProperties.y);
	    result.setRelativeX(tempAnchorProperties.relativeValueX);
	    result.setRelativeY(tempAnchorProperties.relativeValueY);
	    
      setAnchorPointShape(tempAnchorProperties.x, tempAnchorProperties.y);
	
	    return result;
	  }
	  return null;
  }

  public String getText() {
    return textUtil.getText();
  }

  public void setText(String newText) {
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
  	RectContainerShape newShape = new RectContainerShape(x, y, rectSurface.getWidth(), rectSurface.getHeight());
    Diagram result = createDiagram(surface, newShape, getText(), getEditable());
    return result;
  }
  
  protected Diagram createDiagram(ISurfaceHandler surface, RectContainerShape newShape,
      String text, boolean editable) {
    return new RectBoundaryVerticalElement(surface, newShape, text, 
    		new Color(backgroundColor), new Color(borderColor), new Color(textColor), editable);
  }
  

//////////////////////////////////////////////////////////////////////
  
  public boolean onResizeArea(int x, int y) {
    return onResizeArea;
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
  	resize(left, top, width, height);
  }

  protected boolean resize(int left, int top, int width, int height) {
     if (width >= minimumWidth && height >= minimumHeight) {
       left = GridUtils.align(left);
       top = GridUtils.align(top);
       width = GridUtils.align(width);
       height = GridUtils.align(height);
       
       // some bug in with package element... => disabled
//       if (resizeAnchors()) {
//         ++this.dispachSequence;
//         for (AnchorElement a : anchorMap.values()) {
//           a.setAx(((int)(width*a.getRelativeX()))+left);
//           a.setAy(((int)(height*a.getRelativeY()))+top);
//           a.dispatch(dispachSequence);
//         }
//       }
       
       rectSurface.moveToBack();
       rectSurface.setShape(left, top, width, height, 2);
       headerBackground.setShape(left, top, width, HEADER_HEIGHT, 2);
       resizeElement.setShape(
          rectSurface.getX() + rectSurface.getWidth() - 10, rectSurface.getY() + rectSurface.getHeight() - 10, 10, 10, 1);
       
       // set clipping area => text is visible only within canvas boundary
//       group.setClip(rectSurface.getX(), rectSurface.getY(), rectSurface.getWidth(), rectSurface.getHeight());
  
  //    setText(getText());
      textUtil.setTextShape();
      super.applyHelpersShape();
      return true;
     }
     return false;
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
    super.fillInfo(shape);
    return this.shape;
  }

  public void setShape(Info shape) {
    // TODO Auto-generated method stub
    
  }

  public void setReadOnly(boolean value) {
    super.setReadOnly(value);
    resizeElement.setVisibility(!value);
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
	
	public String getTextAreaBackgroundColor() {
		return "transparent"; // other wise looks little bit funny with rect background
	}
	
	@Override
	public String getTextAreaAlign() {
		return "center";
	}
	
	@Override
	public String getTextColor() {
		return "#444444";
	}
	
  @Override
  public boolean supportsTextEditing() {
  	return true;
  }

}
