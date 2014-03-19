package net.sevenscales.editor.uicomponents.uml;


import java.util.ArrayList;
import java.util.List;

import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.content.ui.UMLDiagramSelections.UMLDiagramType;
import net.sevenscales.editor.content.utils.AreaUtils;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.shape.ComponentShape;
import net.sevenscales.editor.diagram.shape.Info;
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
import net.sevenscales.editor.diagram.drag.Anchor;
import net.sevenscales.editor.diagram.drag.AnchorElement;
import net.sevenscales.editor.uicomponents.Point;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil.AbstractHasTextElement;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil.HasTextElement;
import net.sevenscales.editor.uicomponents.helpers.ResizeHelpers;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.editor.content.ui.ContextMenuItem;

import com.google.gwt.core.client.JavaScriptObject;

public class ComponentElement extends AbstractDiagramItem implements SupportsRectangleShape {
  private IRectangle rectSurface;
  private IRectangle comp1;
  private IRectangle small1;
  private IRectangle small2;
  private int minimumWidth = 25;
  private int minimumHeight = 25;
  private ComponentShape shape;
  private Point coords = new Point();
  // utility shape container to align text and make separators
  private List<IShape> innerShapes = new ArrayList<IShape>();
  private IGroup group;
  private TextElementFormatUtil textUtil;
  private static final int DEFAULT_CLASS_RADIUS = 2;

  public ComponentElement(ISurfaceHandler surface, ComponentShape newShape, String text, Color backgroundColor, Color borderColor, Color textColor, boolean editable, IDiagramItemRO item) {
    this(surface, newShape, text, backgroundColor, borderColor, textColor, editable, false, item);
  }
  
  public ComponentElement(ISurfaceHandler surface, ComponentShape newShape, String text, Color backgroundColor, Color borderColor, Color textColor, boolean editable, boolean delayText, IDiagramItemRO item) {
    super(editable, surface, backgroundColor, borderColor, textColor, item);
    this.shape = newShape;
    
    group = IShapeFactory.Util.factory(editable).createGroup(surface.getElementLayer());
    group.setAttribute("cursor", "default");

    rectSurface = (IRectangle) createElement(group);
    rectSurface.setStrokeWidth(STROKE_WIDTH);
    rectSurface.setStroke(borderWebColor);
    rectSurface.setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, backgroundColor.opacity);
    
    comp1 = IShapeFactory.Util.factory(editable).createRectangle(group);
    comp1.setStrokeWidth(1.0);
    comp1.setStroke(textColor.red, textColor.green, textColor.blue, 1);
    
    small1 = IShapeFactory.Util.factory(editable).createRectangle(group);
    small1.setStrokeWidth(1.0);
    small1.setStroke(textColor.red, textColor.green, textColor.blue, 1);
    small1.setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, backgroundColor.opacity);

    small2 = IShapeFactory.Util.factory(editable).createRectangle(group);
    small2.setStrokeWidth(1.0);
    small2.setStroke(textColor.red, textColor.green, textColor.blue, 1);
    small2.setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, backgroundColor.opacity);

    addEvents(rectSurface);
    addMouseDiagramHandler(this);
    
    shapes.add(rectSurface);
    shapes.add(comp1);
    shapes.add(small1);
    shapes.add(small2);
    
    resizeHelpers = ResizeHelpers.createResizeHelpers(surface);
    
    // needs to be last to be on top
    textUtil = new TextElementFormatUtil(this, hasTextElement, group, surface.getEditorContext());

    _setShape(shape.rectShape.left, shape.rectShape.top, shape.rectShape.width, shape.rectShape.height);
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
      return ComponentElement.this.getLink();
    }

    public boolean isAutoResize() {
      return ComponentElement.this.isAutoResize();
    }

    public void resize(int x, int y, int width, int height) {
      // Text Element doesn't support resize
      ComponentElement.this.resize(x, y, width, height);
      fireSizeChanged();
    }

    public void setLink(String link) {
      ComponentElement.this.setLink(link);      
    }
    public boolean supportsTitleCenter() {
      return true;
    }
    public int getTextMargin(int defaultMargin) {
      switch (textUtil.getFontSize()) {
        case 12:
          return 45;
        case 14:
          return 47;
        default:
          return 0;
      }
      // return (int) (defaultMargin * 45f / 30f);
    }
    public boolean forceAutoResize() {
      return true;
    }
    
    public GraphicsEventHandler getGraphicsMouseHandler() {
      return ComponentElement.this;
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
////    int dx = SilverUtils.getTransformX(group.getContainer());
////    int dy = SilverUtils.getTransformY(group.getContainer());
//    int dx = group.getTransformX();
//    int dy = group.getTransformY();
//    MatrixPointJS point = MatrixPointJS.createScaledPoint(dx, dy, surface.getScaleFactor());
//      
//    // reset transformations
////    SilverUtils.resetRenderTransform(group.getContainer());
//    group.resetTransform();
//      
//    // apply transformations to shapes
//    for (IShape s : shapes) {
//      s.applyTransform(point.getX(), point.getY());
//    }
//    
//    // set clip according to new position
////    group.setClip(rectSurface.getX(), rectSurface.getY(), 
////                  rectSurface.getWidth(), rectSurface.getHeight());
//  }

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
  	ComponentShape newShape = new ComponentShape(x, y, rectSurface.getWidth(), rectSurface.getHeight());
    Diagram result = createDiagram(surface, newShape, getText(), getEditable());
    return result;
  }
  
  protected Diagram createDiagram(ISurfaceHandler surface, ComponentShape newShape,
      String text, boolean editable) {
    return new ComponentElement(surface, newShape, text, new Color(backgroundColor), new Color(borderColor), new Color(textColor), editable, new DiagramItemDTO());
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
  
  private void _setShape(int left, int top, int width, int height) {
    rectSurface.setShape(left, top, width, height, DEFAULT_CLASS_RADIUS);
//    resizeHelpers.setShape(left, top, width, height);
    connectionHelpers.setShape(left, top, width, height);
    int comp1left = left + width - 10;
    int comp1top = top + 5;
    comp1.setShape(comp1left - 1, comp1top, 8, 13, 0);
    small1.setShape(comp1left - 3, comp1top + 2, 5, 4, 0);
    small2.setShape(comp1left - 3, comp1top + 7, 5, 4, 0);
  }
  
  public void setShape(int left, int top, int width, int height) {
  	if (width >= minimumWidth && height >= minimumHeight) {
      left = GridUtils.align(left);
      top = GridUtils.align(top);
      width = GridUtils.align(width);
      height = GridUtils.align(height);
      
      _setShape(left, top, width, height);
      // set clipping area => text is visible only within canvas boundary
//      group.setClip(rectSurface.getX(), rectSurface.getY(), rectSurface.getWidth(), rectSurface.getHeight());
 
 //    setText(getText());
     textUtil.setTextShape();
     super.applyHelpersShape();
    }
  }

  protected boolean resize(int left, int top, int width, int height) {
  	setShape(left, top, width, height);
    if (resizeAnchors()) {
    	dispatchAndRecalculateAnchorPositions();
    }
    return true;
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
  	return UMLDiagramType.CLASS;
  }
  
  @Override
	public void setBackgroundColor(int red, int green, int blue, double opacity) {
  	super.setBackgroundColor(red, green, blue, opacity);
    rectSurface.setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, backgroundColor.opacity);
    small1.setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, backgroundColor.opacity);
    small2.setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, backgroundColor.opacity);
  }

  @Override
  protected void applyTextColor() {
    super.applyTextColor();
    // component icon bahaves like text color
    setComponentIconColor();
  }
  
	@Override
  public int getRelativeLeft() {
		return rectSurface.getX();
	}
	@Override
  public int getRelativeTop() {
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
  }

	private void setComponentIconColor() {
    comp1.setStroke(textColor.red, textColor.green, textColor.blue, textColor.opacity);
    small1.setStroke(textColor.red, textColor.green, textColor.blue, textColor.opacity);
    small2.setStroke(textColor.red, textColor.green, textColor.blue, textColor.opacity);
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
