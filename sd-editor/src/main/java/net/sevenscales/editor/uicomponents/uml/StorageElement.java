package net.sevenscales.editor.uicomponents.uml;


import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.content.ui.UMLDiagramSelections.UMLDiagramType;
import net.sevenscales.editor.content.utils.AreaUtils;
import net.sevenscales.editor.content.utils.Strings;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.shape.DbShape;
import net.sevenscales.editor.diagram.shape.Info;
import net.sevenscales.editor.diagram.utils.GridUtils;
import net.sevenscales.editor.gfx.base.GraphicsEventHandler;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.IEllipse;
import net.sevenscales.editor.gfx.domain.IGroup;
import net.sevenscales.editor.gfx.domain.ILine;
import net.sevenscales.editor.gfx.domain.IPath;
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

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

public class StorageElement extends AbstractDiagramItem implements SupportsRectangleShape {
	private static final SLogger logger = SLogger.createLogger(StorageElement.class);
  private static final int RY = 4;

	public static final double MAGIC_ELLIPSE_DIVISION = 2.8;

//	private Rectangle rectSurface;
  private IRectangle background;
//  private IRectangle boundary;
  private IEllipse topCircle;
  private IPath bottomCircle;
  private IRectangle fillrect;
  private ILine leftLine;
  private ILine rightLine;
	private int minimumWidth = 25;
	private int minimumHeight = 25;
	private DbShape shape;
	private Point coords = new Point();
  private IGroup group;
  private TextElementFormatUtil textUtil;
  private HalfEllipseTransformer halfEllipseTransformer = new HalfEllipseTransformer();
  
	public StorageElement(ISurfaceHandler surface, DbShape newShape, String text, 
			Color backgroundColor, Color borderColor, Color textColor, boolean editable, IDiagramItemRO item) {
		super(editable, surface, backgroundColor, borderColor, textColor, item);
		this.shape = newShape;
		
		group = IShapeFactory.Util.factory(editable).createGroup(surface.getElementLayer());
    group.setAttribute("cursor", "default");
		
		bottomCircle = IShapeFactory.Util.factory(editable).createPath(group, halfEllipseTransformer);
		bottomCircle.setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, backgroundColor.opacity);
		bottomCircle.setStrokeWidth(STROKE_WIDTH);

		fillrect = IShapeFactory.Util.factory(editable).createRectangle(group);
		fillrect.setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, backgroundColor.opacity);

		topCircle = IShapeFactory.Util.factory(editable).createEllipse(group);
		topCircle.setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, backgroundColor.opacity);
		topCircle.setStrokeWidth(STROKE_WIDTH);

		leftLine = IShapeFactory.Util.factory(editable).createLine(group);
		leftLine.setStrokeWidth(STROKE_WIDTH);
		
		rightLine = IShapeFactory.Util.factory(editable).createLine(group);
		rightLine.setStrokeWidth(STROKE_WIDTH);
		
    background = IShapeFactory.Util.factory(editable).createRectangle(group);
    background.setFill("transparent");
    addEvents(background);
		
    resizeHelpers = ResizeHelpers.createResizeHelpers(surface);
		
//		addMouseDiagramHandler(this);
		
		// to save last transform to shapes as well
    shapes.add(background);
    shapes.add(bottomCircle);
    shapes.add(fillrect);
    shapes.add(topCircle);
    shapes.add(leftLine);
    shapes.add(rightLine);
    
    setReadOnly(!editable);
    setDimensions(shape.rectShape.left, shape.rectShape.top, 
             shape.rectShape.width, shape.rectShape.height);
    textUtil = new TextElementFormatUtil(this, hasTextElement, group, surface.getEditorContext());
    textUtil.setMarginTop(TextElementFormatUtil.DEFAULT_MARGIN_TOP + RY);
    
    setText(text);
    setBorderColor(borderColor);
    super.constructorDone();
	}

	@Override
	public int getResizeIndentX() {
		return -4;
	}

	@Override
	public int getResizeIndentY() {
		return -6;
	}
	
	private void setDimensions(int left, int top, int width, int height) {
    background.setShape(left, top, width, height, 0);
//    resizeHelpers.setShape(left, top, width, height);
    
    int tcy = top + RY;
		topCircle.setShape(left + width / 2, tcy, width / 2, RY);
		bottomCircle.setShape(calcBottomEllipse(left, top, width, height));
				
		fillrect.setShape(left, tcy, width, height - RY * 2, 0);

		leftLine.setShape(left, tcy, left, tcy + height - RY * 2);
		rightLine.setShape(topCircle.getCx() + topCircle.getRx(), tcy, topCircle.getCx() + topCircle.getRx(), tcy + height - RY * 2);
	}

//	<path d="M300,200 h-150 a150,150 0 1,0 150,-150 z"
//	    fill="red" stroke="blue" stroke-width="5" />
	
	public static class Template {
		// M48,-77 0 a16,4 0 0,0 45,0 
		public static final RegExp parserRegExp = RegExp.compile("^M(\\-?\\d+),(\\-?\\d+).*\\s(\\-?\\d+),(\\-?\\d+)$");

		public static String bottomEllipse(int left, int top, int right, int bottom, int rx, int ry) {
    	return Strings.format("M %s %s a%s %s 0 0 0 %s %s", left, top, rx, ry, right, bottom);
    }
  }
  public class HalfEllipseTransformer implements IPath.PathTransformer {
		@Override
		public String getShapeStr(int dx, int dy) {
			return calcBottomEllipse(doGetLeft() + dx, doGetTop() + dy, getWidth(), getHeight());
		}
  };
	
	private static String calcBottomEllipse(int left, int top, int width, int height) {
		return Template.bottomEllipse(left, top + height - RY, width - 1, 0, (int) (width / MAGIC_ELLIPSE_DIVISION), RY);
	}

	public void setShape(int left, int top, int width, int height) {
		setDimensions(left, top, width, height);
		textUtil.setTextShape();
	}
	
  // nice way to clearly separate interface methods :)
  private HasTextElement hasTextElement = new AbstractHasTextElement() {
    public void addShape(IShape shape) {
      shapes.add(shape);    
    }
    public int getWidth() {
      return background.getWidth();
    }
    public int getX() {
    	return background.getX();
    }
    public int getY() {
    	return background.getY();
    }
    public int getHeight() {
    	return background.getHeight();
    }
    public void removeShape(IShape shape) {
      group.remove(shape);
      shapes.remove(shape);
    }

    public String getLink() {
      return StorageElement.this.getLink();
    }

    public boolean isAutoResize() {
      return StorageElement.this.isAutoResize();
    }

    public void resize(int x, int y, int width, int height) {
      StorageElement.this.resize(x, y, width, height);
      fireSizeChanged();
    }

    public void setLink(String link) {
      StorageElement.this.setLink(link);      
    }
    public boolean supportsTitleCenter() {
      return true;
    }
    public boolean forceAutoResize() {
      return true;
    }
    
    public GraphicsEventHandler getGraphicsMouseHandler() {
      return StorageElement.this;
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
    DbShape newShape = new DbShape(x, y, background.getWidth(), background.getHeight());
    Diagram result = createDiagram(surface, newShape, getText(), getEditable());
    return result;
  }
	
  protected Diagram createDiagram(ISurfaceHandler surface, DbShape newShape,
      String text, boolean editable) {
    return new StorageElement(surface, newShape, text, 
    		new Color(backgroundColor), new Color(borderColor), new Color(textColor), editable, new DiagramItemDTO());
  }
	
//////////////////////////////////////////////////////////////////////
	
	public boolean onResizeArea(int x, int y) {
		return resizeHelpers.isOnResizeArea();
	}

	public void resizeStart() {
	}

	public boolean resize(Point diff) {
		return resize(background.getX(), background.getY(), background.getWidth() + diff.x, background.getHeight() + diff.y);			
	}

	protected boolean resize(int left, int top, int width, int height) {
		if (width >= minimumWidth && height >= minimumHeight) {
			setShape(left, top, width, height);
       dispatchAndRecalculateAnchorPositions();
       
       super.applyHelpersShape();
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
    return "-->";
  }
  
  @Override
  public UMLDiagramType getDiagramType() {
  	return UMLDiagramType.DB;
  }
  
  @Override
	public void setBackgroundColor(int red, int green, int blue, double opacity) {
  	super.setBackgroundColor(red, green, blue, opacity);
		bottomCircle.setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, backgroundColor.opacity);
		fillrect.setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, backgroundColor.opacity);
		topCircle.setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, backgroundColor.opacity);
  }
    
  @Override
	protected int doGetLeft() {
  	return background.getX();
  }
  
  @Override
	protected int doGetTop() {
  	return background.getY();
  }
  
  @Override
  public int getWidth() {
  	return background.getWidth();
  }
  @Override
  public int getHeight() {
  	return background.getHeight();
  }
  
  @Override
	protected void doSetShape(int[] shape) {
  	setShape(shape[0], shape[1], shape[2], shape[3]);
	}
  
  @Override
	public void setHighlightColor(String color) {
		topCircle.setStroke(color);
		bottomCircle.setStroke(color);
		leftLine.setStroke(color);
		rightLine.setStroke(color);
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
	public int getTextAreaTop() {
		return getTop() + RY + 11;
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
