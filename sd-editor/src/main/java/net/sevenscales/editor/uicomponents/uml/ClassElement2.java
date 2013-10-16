package net.sevenscales.editor.uicomponents.uml;


import java.util.ArrayList;
import java.util.List;

import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.content.ui.UMLDiagramSelections.UMLDiagramType;
import net.sevenscales.editor.content.utils.AreaUtils;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.shape.Info;
import net.sevenscales.editor.diagram.shape.RectShape;
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

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;

public class ClassElement2 extends AbstractDiagramItem implements SupportsRectangleShape {
  private IRectangle rectSurface;
  private int minimumWidth = 25;
  private int minimumHeight = 25;
  private RectShape shape;
  // utility shape container to align text and make separators
  private List<IShape> innerShapes = new ArrayList<IShape>();
  private IGroup group;
  private TextElementFormatUtil textUtil;
  private static final int DEFAULT_CLASS_RADIUS = 2;

  public ClassElement2(ISurfaceHandler surface, RectShape newShape, String text, Color backgroundColor, Color borderColor, Color textColor, boolean editable) {
    this(surface, newShape, text, backgroundColor, borderColor, textColor, editable, false);
  }
  
  public ClassElement2(ISurfaceHandler surface, RectShape newShape, String text, Color backgroundColor, Color borderColor, Color textColor, boolean editable, boolean delayText) {
    super(editable, surface, backgroundColor, borderColor, textColor);
    this.shape = newShape;
    
    group = IShapeFactory.Util.factory(editable).createGroup(surface.getElementLayer());
    group.setAttribute("cursor", "default");

    rectSurface = (IRectangle) createElement(group);
    rectSurface.setShape(shape.left, shape.top, shape.width, shape.height, DEFAULT_CLASS_RADIUS);
    rectSurface.setStrokeWidth(STROKE_WIDTH);
    rectSurface.setStroke(borderWebColor);
    rectSurface.setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, backgroundColor.opacity);
    addEvents(rectSurface);

    addMouseDiagramHandler(this);
    
    shapes.add(rectSurface);
    resizeHelpers = ResizeHelpers.createResizeHelpers(surface);
    
    // needs to be last to be on top
    textUtil = new TextElementFormatUtil(this, hasTextElement, group, surface.getEditorContext());

    if (!delayText) {
    	setText(text);
    }

    setReadOnly(!editable);
    
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
      return ClassElement2.this.getLink();
    }

    public boolean isAutoResize() {
      return ClassElement2.this.isAutoResize();
    }

    public void resize(int x, int y, int width, int height) {
      // Text Element doesn't support resize
      ClassElement2.this.resize(x, y, width, height);
      fireSizeChanged();
    }

    public void setLink(String link) {
      ClassElement2.this.setLink(link);      
    }
    public boolean supportsTitleCenter() {
      return true;
    }
    public int getTextMargin() {
      return 30;
    }
    public boolean forceAutoResize() {
      return true;
    }
    
    public GraphicsEventHandler getGraphicsMouseHandler() {
      return ClassElement2.this;
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
    RectShape newShape = new RectShape(x, y, rectSurface.getWidth(), rectSurface.getHeight());
    Diagram result = createDiagram(surface, newShape, getText(), getEditable());
    return result;
  }
  
  protected Diagram createDiagram(ISurfaceHandler surface, RectShape newShape,
      String text, boolean editable) {
    return new ClassElement2(surface, newShape, text, new Color(backgroundColor), new Color(borderColor), new Color(textColor), editable);
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
    return resize(doGetLeft(), doGetTop(), getWidth() + diff.x, getHeight() + diff.y);
  }

  protected boolean resize(int left, int top, int width, int height) {
    setShape(left, top, width, height);
    if (resizeAnchors()) {
      dispatchAndRecalculateAnchorPositions();
    }
    return true;
  }
  
  public void setShape(int left, int top, int width, int height) {
  	if (width >= minimumWidth && height >= minimumHeight) {
      rectSurface.setShape(left, top, width, height, DEFAULT_CLASS_RADIUS);
      textUtil.setTextShape();
      super.applyHelpersShape();
    }
  }

  @Override
  protected void doSetShape(int[] shape) {
    // resize(shape[0], shape[1], shape[2], shape[3]);
    setShape(shape[0], shape[1], shape[2], shape[3]);
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
  public void setHighlightColor(String color) {
    rectSurface.setStroke(color);
  }

	// @Override
	// public void resetTransform() {
 //    group.resetTransform();
	// }
	
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
