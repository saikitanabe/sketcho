package net.sevenscales.editor.uicomponents.uml;


import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.content.ui.UMLDiagramSelections.UMLDiagramType;
import net.sevenscales.editor.content.utils.AreaUtils;
import net.sevenscales.editor.diagram.ContainerType;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.shape.Info;
import net.sevenscales.editor.diagram.shape.UMLPackageShape;
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
import net.sevenscales.editor.uicomponents.AnchorUtils;
import net.sevenscales.editor.uicomponents.Point;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil.AbstractHasTextElement;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil.HasTextElement;
import net.sevenscales.editor.uicomponents.helpers.ResizeHelpers;
import net.sevenscales.editor.content.utils.ContainerAttachHelpers;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.DiagramItemDTO;

import com.google.gwt.core.client.JavaScriptObject;

public class UMLPackageElement extends AbstractDiagramItem implements SupportsRectangleShape, ContainerType {
  private IRectangle rectSurface;
  private int minimumWidth = 25;
  private int minimumHeight = 25;
  private UMLPackageShape shape;
  private Point coords = new Point();
  private IGroup group;
  private TextElementFormatUtil textUtil;
	private IRectangle cornerRect;
	
	private static final int CORNER_HEIGHT = 13;
	private static final int CORNER_WIDTH = (int) (CORNER_HEIGHT * 1.5);

  public UMLPackageElement(ISurfaceHandler surface, UMLPackageShape  newShape, String text, 
  		Color backgroundColor, Color borderColor, Color textColor, boolean editable, IDiagramItemRO item) {
    this(surface, newShape, text, backgroundColor, borderColor, textColor, editable, false, item);
  }
  public UMLPackageElement(ISurfaceHandler surface, UMLPackageShape newShape, String text, 
  		Color backgroundColor, Color borderColor, Color textColor, boolean editable, boolean delayText, IDiagramItemRO item) {
    super(editable, surface, backgroundColor, borderColor, textColor, item);
    this.shape = newShape;
    
    group = IShapeFactory.Util.factory(editable).createGroup(surface.getContainerLayer());
    group.setAttribute("cursor", "default");

    // set clipping area => text is visible only within canvas boundary
//    group.setClip(shape.left, shape.top, shape.width, shape.height);

    rectSurface = (IRectangle) createElement(group);
    net.sevenscales.editor.gfx.domain.Rect r = new net.sevenscales.editor.gfx.domain.Rect();
    r.x = shape.rectShape.left;
    r.y = shape.rectShape.top;
    r.width = shape.rectShape.width;
    r.height = shape.rectShape.height;
    rectSurface.setShape(r);
    rectSurface.setStrokeWidth(STROKE_WIDTH);
    rectSurface.setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, backgroundColor.opacity);
    
    cornerRect = IShapeFactory.Util.factory(editable).createRectangle(group);
    cornerRect.setShape(shape.rectShape.left, shape.rectShape.top - CORNER_HEIGHT, CORNER_WIDTH, CORNER_HEIGHT, 0);
    cornerRect.setStrokeWidth(STROKE_WIDTH);
    cornerRect.setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, backgroundColor.opacity);
    

    addEvents(rectSurface);
    addMouseDiagramHandler(this);
    
    shapes.add(rectSurface);
    shapes.add(cornerRect);
    
    resizeHelpers = ResizeHelpers.createResizeHelpers(surface);
    textUtil = new TextElementFormatUtil(this, hasTextElement, group, surface.getEditorContext());
    
    if (!delayText) {
    	setText(text);
    }

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
      return UMLPackageElement.this.getLink();
    }

    public boolean isAutoResize() {
      return false;
    }

    public void resize(int x, int y, int width, int height) {
      // Text Element doesn't support resize
      UMLPackageElement.this.resize(x, y, width, height);
    }

    public void setLink(String link) {
      UMLPackageElement.this.setLink(link);      
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
      return UMLPackageElement.this;
    };
    
		@Override
		public String getTextColorAsString() {
			return "#" + textColor.toHexString();
		};

  };


  protected IShape createElement(IContainer surface) {
    return IShapeFactory.Util.factory(editable).createRectangle(surface);
  }

//  public void saveLastTransform() {
//    // get transformation
//    int dx = group.getTransformX();
//    int dy = group.getTransformY();
//      
//    // reset transformations
////    SilverUtils.resetRenderTransform(group.getContainer());
//    group.resetTransform();
//      
//    // apply transformations to shapes
//    for (IShape s : shapes) {
//      s.applyTransform(dx, dy);
//    }
//  }

  public Point getDiffFromMouseDownLocation() {
    return new Point(diffFromMouseDownX, diffFromMouseDownY);
  }
  
  public void accept(ISurfaceHandler surface) {
    super.accept(surface);
    surface.makeDraggable(this);
  }

  public AnchorElement onAttachArea(Anchor anchor, int x, int y) {
    return ContainerAttachHelpers.onAttachArea(this, anchor, x, y);

//   	// container attach is different only border areas can attach
//   	// TODO make this as utility to be used by other container elements
// //  	return super.onAttachArea(anchor, x, y, rectSurface.getX(), rectSurface.getY() - CORNER_HEIGHT, rectSurface.getWidth(), rectSurface.getHeight() + CORNER_HEIGHT);
//   	// put all values to 0 not to attach any other than connection handle
//   	AnchorElement a = super.onAttachArea(anchor, x, y, 0, 0, 0, 0);
//   	if (a != null) {
//   		return a;
//   	}
  	
//     if (AnchorUtils.onAttachArea(x, y, rectSurface.getX(), rectSurface.getY() - CORNER_HEIGHT, rectSurface.getWidth(), rectSurface.getHeight() + CORNER_HEIGHT, 20)) {
//       AnchorElement result = getAnchorElement(anchor);
//       AnchorUtils.anchorPoint(x, y, tempAnchorProperties , rectSurface.getX(), rectSurface.getY() - CORNER_HEIGHT, rectSurface.getWidth(), rectSurface.getHeight() + CORNER_HEIGHT);

//       result.setAx(tempAnchorProperties.x);
//       result.setAy(tempAnchorProperties.y);
//       result.setRelativeX(tempAnchorProperties.relativeValueX);
//       result.setRelativeY(tempAnchorProperties.relativeValueY);
      
//       this.anchorPoint.setShape(tempAnchorProperties.x, tempAnchorProperties.y, 6);
//       this.anchorPoint.moveToFront();

//       return result;
//     }
//     return null;
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
    UMLPackageShape newShape = new UMLPackageShape(x, y, rectSurface.getWidth(), rectSurface.getHeight());
    Diagram result = createDiagram(surface, newShape, getText(), getEditable());
    return result;
  }
  
  protected Diagram createDiagram(ISurfaceHandler surface, UMLPackageShape newShape,
      String text, boolean editable) {
    return new UMLPackageElement(surface, newShape, text, 
    		new Color(backgroundColor), new Color(borderColor), new Color(textColor), editable, new DiagramItemDTO());
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
    rectSurface.moveToBack();
    rectSurface.setShape(left, top, width, height, 0);
    cornerRect.setShape(left, top-CORNER_HEIGHT, CORNER_WIDTH, CORNER_HEIGHT, 0);
    textUtil.setTextShape();
  }

  protected boolean resize(int left, int top, int width, int height) {
    if (width >= minimumWidth && height >= minimumHeight) {
      setShape(left, top, width, height);
      dispatchAndRecalculateAnchorPositions(getLeft(), getTop() - CORNER_HEIGHT, rectSurface.getWidth(), rectSurface.getHeight() + CORNER_HEIGHT);
       
       // set clipping area => text is visible only within canvas boundary
//       group.setClip(rectSurface.getX(), rectSurface.getY(), rectSurface.getWidth(), rectSurface.getHeight());
  
  //    setText(getText());
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
    rectSurface.setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, backgroundColor.opacity);
    cornerRect.setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, backgroundColor.opacity);
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
    cornerRect.setStroke(color);
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

}
