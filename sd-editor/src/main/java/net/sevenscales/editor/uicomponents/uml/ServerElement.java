package net.sevenscales.editor.uicomponents.uml;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.content.ui.UMLDiagramSelections.UMLDiagramType;
import net.sevenscales.editor.content.utils.AreaUtils;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.shape.Info;
import net.sevenscales.editor.diagram.shape.ServerShape;
import net.sevenscales.editor.diagram.utils.GridUtils;
import net.sevenscales.editor.gfx.base.GraphicsEventHandler;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.IContainer;
import net.sevenscales.editor.gfx.domain.IGroup;
import net.sevenscales.editor.gfx.domain.ILine;
import net.sevenscales.editor.gfx.domain.IPolyline;
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

public class ServerElement extends AbstractDiagramItem implements SupportsRectangleShape {
  private IRectangle boundary;
  private IPolyline roof;
  private IPolyline front;
  private ILine frontPanel1;
  private ILine frontPanel2;
  private ILine frontPanel3;
//  private IPath frontPanel3;
//  private IPath frontPanel1;
//  private IPath frontPanel2;
//  private IPath frontPanel3;
//  private IRectangle roof;
//  private IRectangle front;
//  private IRectangle side;
  private int minimumWidth = 25;
  private int minimumHeight = 25;
  private ServerShape shape;
  private Point coords = new Point();
  // utility shape container to align text and make separators
  private List<IShape> innerShapes = new ArrayList<IShape>();
  private IGroup group;
  private TextElementFormatUtil textUtil;
  private static final int DEFAULT_CLASS_RADIUS = 2;
  private Map<String,String> params = new HashMap<String, String>();

  public ServerElement(ISurfaceHandler surface, ServerShape newShape, String text, 
  		Color backgroundColor, Color borderColor, Color textColor, boolean editable, IDiagramItemRO item) {
    this(surface, newShape, text, backgroundColor, borderColor, textColor, editable, false, item);
  }
  
  public ServerElement(ISurfaceHandler surface, ServerShape newShape, String text, 
  		Color backgroundColor, Color borderColor, Color textColor, boolean editable, boolean delayText, IDiagramItemRO item) {
    super(editable, surface, backgroundColor, borderColor, textColor, item);
    // text color is skipped and theme color is always used => text has board as background, text is always visible
    this.shape = newShape;
    
    group = IShapeFactory.Util.factory(editable).createGroup(surface.getElementLayer());
    group.setAttribute("cursor", "default");

    roof = IShapeFactory.Util.factory(editable).createPolyline(group);
    roof.setStroke(getBorderDrawingColor().red, getBorderDrawingColor().green, getBorderDrawingColor().blue, getBorderDrawingColor().opacity);
    roof.setFill(getBackgroundDrawingColor().red, getBackgroundDrawingColor().green, getBackgroundDrawingColor().blue, getBackgroundDrawingColor().opacity);
    roof.setStrokeWidth(2.0);
    
    front = IShapeFactory.Util.factory(editable).createPolyline(group);
    front.setStroke(getBorderDrawingColor().red, getBorderDrawingColor().green, getBorderDrawingColor().blue, getBorderDrawingColor().opacity);
    front.setFill(getBackgroundDrawingColor().red, getBackgroundDrawingColor().green, getBackgroundDrawingColor().blue, getBackgroundDrawingColor().opacity);
    front.setStrokeWidth(2.0);
    
    frontPanel1 = IShapeFactory.Util.factory(editable).createLine(group);
    frontPanel1.setStrokeWidth(1);
    frontPanel1.setStroke(getBorderDrawingColor().red, getBorderDrawingColor().green, getBorderDrawingColor().blue, getBorderDrawingColor().opacity);
    
    frontPanel2 = IShapeFactory.Util.factory(editable).createLine(group);
    frontPanel2.setStrokeWidth(1);
    frontPanel2.setStroke(getBorderDrawingColor().red, getBorderDrawingColor().green, getBorderDrawingColor().blue, getBorderDrawingColor().opacity);

    frontPanel3 = IShapeFactory.Util.factory(editable).createLine(group);
    frontPanel3.setStrokeWidth(1);
    frontPanel3.setStroke(getBorderDrawingColor().red, getBorderDrawingColor().green, getBorderDrawingColor().blue, getBorderDrawingColor().opacity);

    boundary = (IRectangle) createElement(group);
    boundary.setStrokeWidth(0);
    boundary.setStroke(0, 0, 0, 0);
    boundary.setFill(getBackgroundDrawingColor().red, getBackgroundDrawingColor().green, getBackgroundDrawingColor().blue, 0);

//    
//    front = IShapeFactory.Util.factory(editable).createRectangle(group);
//    front.setStrokeWidth(1.0);
//    front.setStroke(textColor.red, textColor.green, textColor.blue, 1);
//    front.setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, backgroundColor.opacity);

//    side = IShapeFactory.Util.factory(editable).createRectangle(group);
//    side.setStrokeWidth(1.0);
//    side.setStroke(textColor.red, textColor.green, textColor.blue, 1);
//    side.setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, backgroundColor.opacity);
    
//    addObserver(rectSurface.getRawNode(), AbstractDiagramItem.EVENT_DOUBLE_CLICK);
    addEvents(boundary);

    addMouseDiagramHandler(this);
    
    shapes.add(boundary);
    shapes.add(roof);
    shapes.add(front);
    shapes.add(frontPanel1);
    shapes.add(frontPanel2);
    shapes.add(frontPanel3);
//    shapes.add(roof);
//    shapes.add(front);
//    shapes.add(side);
    
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
      return boundary.getWidth();
    }
    public int getX() {
      return boundary.getX();
    }
    public int getY() {
    	return boundary.getY() + boundary.getHeight() - TextElementFormatUtil.ROW_HEIGHT + 5;
    }
    public int getHeight() {
      return boundary.getHeight();
    }
    public void removeShape(IShape shape) {
      group.remove(shape);
      shapes.remove(shape);
    }

    public String getLink() {
      return ServerElement.this.getLink();
    }

    public boolean isAutoResize() {
      return false;
    }

    public void resize(int x, int y, int width, int height) {
      // Text Element doesn't support resize
      ServerElement.this.resize(x, y, width, height);
      fireSizeChanged();
    }

    public void setLink(String link) {
      ServerElement.this.setLink(link);      
    }
    public boolean supportsTitleCenter() {
      return true;
    }
    public int getTextMargin(int defaultMargin) {
      return 45;
    }
    public boolean forceAutoResize() {
      return false;
    }
    public boolean supportElementResize() {
      return false;
    }
    public GraphicsEventHandler getGraphicsMouseHandler() {
      return ServerElement.this;
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
  	ServerShape newShape = new ServerShape(x, y, boundary.getWidth(), boundary.getHeight());
    Diagram result = createDiagram(surface, newShape, getText(), getEditable());
    return result;
  }
  
  protected Diagram createDiagram(ISurfaceHandler surface, ServerShape newShape,
      String text, boolean editable) {
    return new ServerElement(surface, newShape, text, new Color(backgroundColor), new Color(borderColor), new Color(textColor), editable, new DiagramItemDTO());
  }
  

//////////////////////////////////////////////////////////////////////
  
  public boolean onResizeArea(int x, int y) {
    return resizeHelpers.isOnResizeArea();
  }

  public JavaScriptObject getResizeElement() {
    return boundary.getRawNode();
  }
  
  public void resizeStart() {
  }

  public boolean resize(Point diff) {
    return resize(boundary.getX(), boundary.getY(), 
                  boundary.getWidth() + diff.x, boundary.getHeight() + diff.y);     
  }
  
  private void _setShape(int left, int top, int width, int height) {
    boundary.setShape(left, top, width, height, DEFAULT_CLASS_RADIUS);
//    resizeHelpers.setShape(left, top, width, height);
    int[] points = new int[]{
    		left + 20, top + 25,
    		left, top + 10, 
    		left + width - 19, top, 
    		left + width, top + 15, 
    		left + width, top + height - 15, 
    		left + 20, top + height,
    		left + 20, top + 25,
    		left + width, top + 15};
    roof.setShape(points);

    int[] fpoints = new int[]{
    		left + 20, top + 25,
    		left, top + 10,
    		left, top + height - 15,    		
    		left + 20, top + height
    };
    front.setShape(fpoints);

		int leftc = left + 5;
		int topc = (int) (top + (height * 0.35));
		int widthc = 7;
		int endx = leftc + widthc;
		int endy = topc + 6;

    frontPanel1.setShape(leftc, topc, endx, endy);
    topc += 3;
    endy += 3;
    frontPanel2.setShape(leftc, topc, endx, endy);
    topc += 3;
    endy += 3;
    frontPanel3.setShape(leftc, topc, endx, endy);
    
//		String template = "M%left%,%top% C%leftc%,%topc% %endxc%,%endyc% %endx%,%endy%";
//		
//		int leftc = left + 5;
//		int topc = top + (height / 2);
//		int widthc = 7;
//		int endy = topc - 2;
//		
//		params.put("left", String.valueOf(leftc));
//		params.put("top", String.valueOf(topc));
//		params.put("leftc", String.valueOf(leftc));
//		params.put("topc", String.valueOf(topc - 3));
//		
//		params.put("endxc", String.valueOf(leftc + widthc));
//		params.put("endyc", String.valueOf(topc + 3));
//		params.put("endx", String.valueOf(leftc + widthc));
//		params.put("endy", String.valueOf(topc));
//		
//		String shape = StringUtil.parse(template, params);
//    frontPanel1.setShape(shape);

//    topc += 3;
//		params.put("left", String.valueOf(leftc));
//		params.put("top", String.valueOf(topc));
//		params.put("leftc", String.valueOf(leftc));
//		params.put("topc", String.valueOf(topc - 3));
//		
//		params.put("endxc", String.valueOf(leftc + widthc));
//		params.put("endyc", String.valueOf(topc + 3));
//		params.put("endx", String.valueOf(leftc + widthc));
//		params.put("endy", String.valueOf(topc));
//		shape = StringUtil.parse(template, params);
//    frontPanel2.setShape(shape);

//    roof.resetAllTransforms();
//    roof.setShape(left, top + 20, width, 15, 0);
//    roof.rotate(-18, left, top);
//    roof.applyTransform(-7, 0);
//    
//    front.resetAllTransforms();
//    front.setShape(left, top + 20, width, 15, 0);
//    front.rotate(-18, left, top);
//    front.applyTransform(-7, 0);

//    roor.
//    roof.skewg(30);
//    front.setShape(comp1left - 3, comp1top + 2, 5, 4, 0);
//    side.setShape(comp1left - 3, comp1top + 7, 5, 4, 0);
  }
  
  public void setShape(int left, int top, int width, int height) {
    if (width >= minimumWidth && height >= minimumHeight) {
      _setShape(left, top, width, height);
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
    boundary.setVisibility(!value);
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
    front.setFill(getBackgroundDrawingColor().red, getBackgroundDrawingColor().green, getBackgroundDrawingColor().blue, getBackgroundDrawingColor().opacity);
    roof.setFill(getBackgroundDrawingColor().red, getBackgroundDrawingColor().green, getBackgroundDrawingColor().blue, getBackgroundDrawingColor().opacity);
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
    roof.setStroke(color);
    front.setStroke(color);
    frontPanel1.setStroke(color);
    frontPanel2.setStroke(color);
    frontPanel3.setStroke(color);
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
	public int getTextAreaLeft() {
		return getLeft() + getWidth() / 2 - textUtil.getTextWidth() / 2;
	}
	
	@Override
	public int getTextAreaHeight() {
		return textUtil.getTextHeight();
	}
	
	@Override
	public int getTextAreaWidth() {
		return textUtil.getTextWidth();
	}
	
	@Override
	public int getTextAreaTop() {
		return getTop() + getHeight() - 1;
	}
	
	@Override
	public String getTextAreaBackgroundColor() {
		return "transparent";
	}
	
	@Override
	public String getTextAreaAlign() {
		return "center";
	}
	
  @Override
  public boolean supportsTextEditing() {
  	return true;
  }
  
  @Override
  public boolean isTextElementBackgroundTransparent() {
    return true;
  }
  
  @Override
  public boolean isTextColorAccordingToBackgroundColor() {
    return true;
  }

  @Override
  public int supportedMenuItems() {
    return super.supportedMenuItems() | ContextMenuItem.FONT_SIZE.getValue();
  }

}
