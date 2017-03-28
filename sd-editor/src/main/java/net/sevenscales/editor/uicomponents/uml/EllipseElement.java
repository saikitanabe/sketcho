package net.sevenscales.editor.uicomponents.uml;


import net.sevenscales.editor.content.ui.ContextMenuItem;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.content.ui.UMLDiagramType;
import net.sevenscales.editor.content.utils.AreaUtils;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.shape.EllipseShape;
import net.sevenscales.editor.diagram.shape.Info;
import net.sevenscales.editor.diagram.utils.GridUtils;
import net.sevenscales.editor.gfx.base.GraphicsEventHandler;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.IEllipse;
import net.sevenscales.editor.gfx.domain.IGroup;
import net.sevenscales.editor.gfx.domain.IShape;
import net.sevenscales.editor.gfx.domain.IShapeFactory;
import net.sevenscales.editor.gfx.domain.SupportsRectangleShape;
import net.sevenscales.editor.uicomponents.AbstractDiagramItem;
import net.sevenscales.editor.diagram.drag.Anchor;
import net.sevenscales.editor.diagram.drag.AnchorElement;
import net.sevenscales.editor.gfx.domain.Point;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil.AbstractHasTextElement;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil.HasTextElement;
import net.sevenscales.editor.uicomponents.helpers.ResizeHelpers;
import net.sevenscales.editor.uicomponents.AnchorUtils;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.DiagramItemDTO;

import com.google.gwt.core.client.JavaScriptObject;

public class EllipseElement extends AbstractDiagramItem implements SupportsRectangleShape {
	private IEllipse ellipse;
//	private IText textElement;
  private TextElementFormatUtil textUtil;
	private int minimumWidth = 10;
	private int minimumHeight = 10;
	private Point coords = new Point();
//	private IRectangle resizeElement;
//	private boolean onResizeArea;
	int edge = 10;
  private Point tempAnchorPoint = new Point();
//  private Line tempLine;
//  private Line tempLine2;
  private int dispatchSequence;
	private IGroup group;
	
  final static double angles[] = {0, Math.toRadians(45), Math.toRadians(90), 
      Math.toRadians(135), Math.toRadians(180), Math.toRadians(225), Math.toRadians(270), 
      Math.toRadians(-45), Math.toRadians(-90) };
	
	public EllipseElement(
			ISurfaceHandler surface, EllipseShape info, 
			String text, Color backgroundColor, Color borderColor, Color textColor, boolean editable, IDiagramItemRO item) {
		super(editable, surface, backgroundColor, borderColor, textColor, item);
		
//		tempLine = new Line(surface.getSurface());
//		tempLine.setStroke("red");
//    tempLine2 = new Line(surface.getSurface());
//    tempLine2.setStroke("green");
		
    group = IShapeFactory.Util.factory(editable).createGroup(surface.getElementLayer());
    // group.setAttribute("cursor", "default");
		
		ellipse = IShapeFactory.Util.factory(editable).createEllipse(group);
		ellipse.setShape(info.centerX, info.centerY, info.rx, info.ry);
		ellipse.setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, backgroundColor.opacity);
    ellipse.setStrokeWidth(STROKE_WIDTH);

    // anchorPoint = IShapeFactory.Util.factory(editable).createCircle(group);
    // anchorPoint.setStrokeWidth(1);
    // anchorPoint.setVisibility(false);

    
//		ellipse.setAttribute("cursor", "default");
		
//		textElement = IShapeFactory.Util.factory(editable).createText(group);
//		textElement.setShape(info.cx - info.rx, info.cy - info.ry);
//		textElement.setAttribute("cursor", "default");
		
//		resizeElement = IShapeFactory.Util.factory(editable).createRectangle(group);
//		resizeElement.setShape(info.cx+info.rx-edge, info.cy-edge/2, edge, edge, 0);
//		resizeElement.setFill(200, 200, 200, 0.4);		

		// set clipping area => text is visible only within canvas boundary
//		Rect r = new Rect(0, 0, shape.width, shape.height);
//		SilverUtils.setRectClip(rectSurface, r);

//		addObserver(ellipse.getRawNode(), AbstractDiagramItem.EVENT_DOUBLE_CLICK);
		
		addEvents(ellipse);

//    textElement.addGraphicsMouseDownHandler(this);
//    textElement.addGraphicsMouseUpHandler(this);
//    textElement.addGraphicsMouseMoveHandler(this);
//    textElement.addGraphicsMouseEnterHandler(this);
//    textElement.addGraphicsMouseLeaveHandler(this);

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
		
		// need to add resize element act as use case, since otherwise it 
		// will hide connection helpers
//		resizeElement.addGraphicsMouseEnterHandler(this);
//		resizeElement.addGraphicsMouseLeaveHandler(this);
//		
//		resizeElement.addGraphicsMouseDownHandler(this);
		
//		addMouseDiagramHandler(this);
		
    shapes.add(ellipse);
//    shapes.add(textElement);
//    shapes.add(resizeElement);
    
    int left = ellipse.getCx() - ellipse.getRx();
    int top = ellipse.getCy() - ellipse.getRy();
    int width = 2 * ellipse.getRx();
    int height = 2 * ellipse.getRy();
    
    textUtil = new TextElementFormatUtil(this, hasTextElement, group, surface.getEditorContext());
    textUtil.setForceTextAlign(true);

//    shapes.add(tempLine);
//    shapes.add(tempLine2);
    
    // TODO here 1 was used, can be changed when selected
    resizeHelpers = ResizeHelpers.createResizeHelpers(surface);

    setReadOnly(!editable);
		setText(text);
    setBorderColor(borderColor);
    super.constructorDone();
	}
	
	public int getResizeIndentY() {
		return -3;
	}
	
  private HasTextElement hasTextElement = new AbstractHasTextElement(this) {
    public int getWidth() {
    	return EllipseElement.this.getWidth();
    }
    public int getX() {
    	return getRelativeLeft();
    }
    public int getY() {
    	return getRelativeTop();
    }
    public int getHeight() {
    	return EllipseElement.this.getHeight();
    }
    public void removeShape(IShape shape) {
      group.remove(shape);
      shapes.remove(shape);
    }

    public String getLink() {
    	return EllipseElement.this.getLink();
    }

    public boolean isAutoResize() {
      return true;
    }

    public void resize(int x, int y, int width, int height) {
    	EllipseElement.this.setShape(x, y, width, height);
      dispatch();
      fireSizeChanged();
    }

    public void setLink(String link) {
//      NoteElement.this.setLink(link);      
    }
    public boolean supportsTitleCenter() {
      return false;
    }
    public int getTextMargin(int defaultMargin) {
      // return 70;
      return (int) (defaultMargin * 70f / 30f);
    }
    public boolean forceAutoResize() {
      return true;
    }
    
    public GraphicsEventHandler getGraphicsMouseHandler() {
      return EllipseElement.this;
    }
		@Override
		public Color getTextColor() {
			return textColor;
		};

  };

	public Point getDiffFromMouseDownLocation() {
		return new Point(diffFromMouseDownX, diffFromMouseDownY);
	}
	
	public void accept(ISurfaceHandler surface) {
	  super.accept(surface);
		surface.makeDraggable(this);
	}

  public AnchorElement onAttachArea(Anchor anchor, int x, int y) {
  	// put all values to 0 not to attach any other than connection handle
  	
  	// use case decides connection points itself.
//  	AnchorElement a = super.onAttachArea(anchor, x, y, 0, 0, 0, 0);
//  	if (a != null) {
//  		return a;
//  	}
  	
//	public boolean onAttachArea(int x, int y) {
    int cx = ellipse.getCx() + getTransformX();
    int cy = ellipse.getCy() + getTransformY();
		int x0 = x - cx;
		int y0 = y - cy;
		double calc = Math.pow(x0, 2)/Math.pow(ellipse.getRx() + 5, 2) + Math.pow(y0, 2)/Math.pow(ellipse.getRy() + 5, 2);
		
    if (calc <= 1) {
      AnchorElement result = getAnchorElement(anchor);
      x0 = cx;
      y0 = cy;
      
//      tempLine.setShape(x0, y0, x, y);
//      tempLine.moveToFront();
      
//      double alpha = AngleUtil2.beta(x, y, x0, y0);
//      double alpha = AngleUtil2.slope2(x0, y0, x, y);
//      double k = (y-y0)/(x-x0);
      
      // hack to get angles correct...
//      final double angles[] = {0, Math.toRadians(45), Math.toRadians(90), 
//                               Math.toRadians(135), Math.toRadians(180), Math.toRadians(225), Math.toRadians(270), 
//                               Math.toRadians(-45), Math.toRadians(-90) };
//    final double angles[] = {0, Math.toRadians(45), Math.toRadians(90), 
//                            Math.toRadians(135), Math.toRadians(180), Math.toRadians(225), Math.toRadians(270), 
//                            Math.toRadians(-45), Math.toRadians(-90) };
      
      // - have angles ready
      double alpha = calcAnchorPoint(x, y, tempAnchorPoint, angles);
//      alpha = AngleUtil2.align(alpha, angles);
      
//      int index = (int)Math.random()*(angles.length);
//      double alpha = angles[index];
      tempAnchorProperties.cardinalDirection = AnchorUtils.findCardinalDirection(x, y, getLeft(), getTop(), getWidth(), getHeight());
      result.setAx(tempAnchorPoint.x);
      result.setAy(tempAnchorPoint.y);
      result.setRelativeX(alpha);
      result.setCardinalDirection(tempAnchorProperties.cardinalDirection);
      
//      result.setRelativeX(tempAnchorProperties.relativeValueX);
//      result.setRelativeY(tempAnchorProperties.relativeValueY);
      return result;
    }
    return null;
	}
  
  @Override
  protected boolean setFixedOrRelativeAnchor(int x, int y, Anchor anchor) {
  	double alpha = calcAnchorPoint(x, y, tempAnchorPoint, angles);
  	tempAnchorProperties.relativeValueX = alpha;
    tempAnchorProperties.cardinalDirection = AnchorUtils.findCardinalDirection(x, y, getLeft(), getTop(), getWidth(), getHeight());
    return false; // relative
  }
  
  private void calcAnchorPointWithAngle(int x, int y, double angle, Point anchorPoint) {
    int x0 = ellipse.getCx();
    int y0 = ellipse.getCy();

    // different browsers calculates ellipse slightly differently
    // therefore using bigger ellipse for calculations to keep connections to ellipses
    int a = ellipse.getRx() + 2; 
    int b = ellipse.getRy() + 2;

    int ex = (int) (x0 + a * Math.cos(angle));
    int ey = (int) (y0 + b * Math.sin(angle));
    
    anchorPoint.x = ex;
    anchorPoint.y = ey;
    
    setAnchorPointShape(ex, ey);
  }

  private double calcAnchorPoint(int x, int y, Point anchorPoint, double[] angles) {
    // - calculate dynamic fixed points. This could be optimized, because sector is known.
    // - calculate point distance (hypotenusa) from angle points and select shortest distance
    // - set angle point to anchor point
    // - most probably resize needs to calculate closest point with old angle
    // - then point doesn't move on reload
//    double diminish = 0.25;
    int x0 = ellipse.getCx() + getTransformX();
    int y0 = ellipse.getCy() + getTransformY();

    // different browsers calculates ellipse slightly differently
    // therefore using bigger ellipse for calculations to keep connections to ellipses
    int a = ellipse.getRx() + 2; 
    int b = ellipse.getRy() + 2;

    // ellipse points could be cached if ellipse a and b are the same
    int ax = 0;
    int ay = 0;
    int minDist = Integer.MAX_VALUE;
    double result = 0;
    for (double ag : angles) {
      int ex = (int) (x0 + a * Math.cos(ag));
      int ey = (int) (y0 + b * Math.sin(ag));
      
      // calc distance
      int ka = x-ex;
      int kb = y-ey;
      int c = (int) Math.sqrt((Math.pow(ka, 2)+Math.pow(kb, 2)));
      if (c < minDist) {
        minDist = c;
        ax = ex;
        ay = ey;
        result = ag;
      }
    }
    
    anchorPoint.x = ax;
    anchorPoint.y = ay;
    
    setAnchorPointShape(ax, ay);

    return result;
  }

  public String getText() {
		return textUtil.getText();
	}

	public void doSetText(String newText) {
    textUtil.setText(newText, editable);
	}

  @Override
  public Diagram duplicate(boolean partOfMultiple) {
		return duplicate(surface, getCenterX() + 20, getCenterY() + 20);
	}

  @Override
	public Diagram duplicate(ISurfaceHandler surface, boolean partOfMultiple) {
    Point p = getCoords();
    return duplicate(surface, getCenterX() + 20, getCenterY() + 20);
	}

  @Override
	public Diagram duplicate(ISurfaceHandler surface, int x, int y) {
		EllipseShape info = new EllipseShape(x, y, ellipse.getRx(), ellipse.getRy());
    super.fillInfo(info);
		Diagram result = 
			new EllipseElement(surface, info,
			getText(), new Color(backgroundColor), new Color(borderColor), new Color(textColor), getEditable(), new DiagramItemDTO());
		return result;
	}
	

//////////////////////////////////////////////////////////////////////
	
	public boolean onResizeArea(int x, int y) {
		return resizeHelpers.isOnResizeArea();
	}

	public JavaScriptObject getResizeElement() {
		return ellipse.getRawNode();
	}
	
	public boolean resize(Point diff) {
			return resize(ellipse.getCx(), ellipse.getCy() + diff.y, 
				   ellipse.getRx() + diff.x, ellipse.getRy() + diff.y);
	
			// set clipping area => text is visible only within canvas boundary
//			Rect r = new Rect(0, 0, newWidth, newHeight);
//			SilverUtils.setRectClip(rectSurface, r);
	}

	protected boolean resize(int cx, int cy, int rx, int ry) {
	  rx = rx < minimumWidth ? minimumWidth : rx;
    ry = ry < minimumHeight ? minimumHeight : ry;

    _setShape(cx, cy, rx, ry);
    super.applyHelpersShape();
    
    dispatch();
    return true;
	}

  private void dispatch() {
    ++this.dispatchSequence;
    for (AnchorElement ae : getAnchors()) {
      calcAnchorPointWithAngle(ae.getAx(), ae.getAy(), ae.getRelativeFactorX(), tempAnchorPoint);
      ae.setAx(tempAnchorPoint.x + getTransformX());
      ae.setAy(tempAnchorPoint.y + getTransformY());
      ae.dispatch(dispatchSequence);
    }
  }

//  private boolean outside(int ax, int ay) {
//    int x0 = ax - ellipse.getCx();
//    int y0 = ay - ellipse.getCy();
//    double calc = Math.pow(x0, 2)/Math.pow(ellipse.getRx(), 2) + Math.pow(y0, 2)/Math.pow(ellipse.getRy(), 2);
////    System.out.println("outside: "+calc);
//    return calc > 1.0;
//  }

//  protected void setElement(JavaScriptObject element, int left, int top, int width, int height) {
//	  // TODO: memory
//    SilverUtils.setShape(element, new RectShape(left, top, width, height));
//    textElement.setShape(left, top);
//	}

	public void resizeEnd() {
//    for (AnchorElement a : anchorMap.values()) {
//      int ax = (int) (ellipse.getCx() + ellipse.getRx() * Math.cos(a.getRelativeX()));
//      int ay = (int) (ellipse.getCy() + ellipse.getRy() * Math.sin(a.getRelativeX()));
//
//      a.setAx(ax);
//      a.setAy(ay);
//      a.dispatch();
//    }
	}

	public Info getInfo() {
	  EllipseShape info = new EllipseShape(ellipse.getCx() + getTransformX(), ellipse.getCy() + getTransformY(), ellipse.getRx(), ellipse.getRy());
    super.fillInfo(info);
		return info;
	}

	@Override
	public void setReadOnly(boolean value) {
	  super.setReadOnly(value);
	}
	
  public String getDefaultRelationship() {
    return "-";
  }
  
  @Override
  public UMLDiagramType getDiagramType() {
  	return UMLDiagramType.USE_CASE;
  }
  
  @Override
	public void setBackgroundColor(int red, int green, int blue, double opacity) {
  	super.setBackgroundColor(red, green, blue, opacity);
    ellipse.setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, backgroundColor.opacity);
  }
  
//	@Override
//	public void reattach(AnchorElement anchorElement) {
//    int cx = ellipse.getCx();
//    int cy = ellipse.getCy();
//    int rx = ellipse.getRx() - 1;
//    int ry = ellipse.getRy() - 1;
//    int ax = (int) (cx + rx * Math.cos(anchorElement.getRelativeX()));
//    int ay = (int) (cy + ry * Math.sin(anchorElement.getRelativeX()));
//    
//    int x0 = ax - ellipse.getCx();
//    int y0 = ay - ellipse.getCy();
//    double calc = Math.pow(x0, 2)/Math.pow(ellipse.getRx(), 2) + Math.pow(y0, 2)/Math.pow(ellipse.getRy(), 2);
//    System.out.println("Ellipse reattach: "+calc);
//
//    anchorElement.setAx(ax);
//    anchorElement.setAy(ay);
//    anchorElement.dispatch();
//	}
  
  @Override
	public int getRelativeLeft() {
		return ellipse.getCx() - ellipse.getRx();
	}
	@Override
	public int getRelativeTop() {
		return ellipse.getCy() - ellipse.getRy();
	}
	@Override
	public int getWidth() {
		return ellipse.getRx() * 2;
	}
	@Override
	public int getHeight() {
		return ellipse.getRy() * 2;
	}
  
  @Override
  // currently stored as cx, cy, rx, ry => translate to rectangle
  protected void doSetShape(int[] shape) {
  	setShape(shape[0] - shape[2], shape[1] - shape[3], shape[2] * 2, shape[3] * 2);
  }

  public void setShape(Info shape) {
    // TODO Auto-generated method stub
  }
  
  public void setShape(int left, int top, int width, int height) {
    int cx = left + (width - (width % 2)) / 2;
    int cy = top + (height - (height % 2)) / 2;
    int rx = width / 2;
    int ry = height / 2;
    _setShape(cx, cy, rx, ry);
  }

  private void _setShape(int cx, int cy, int rx, int ry) {
    ellipse.setShape(cx, cy, rx, ry);
    textUtil.setTextShape();
  }
  
  @Override
  public int[] getShape() {
  	int[] result = new int[]{getLeft(), getTop(), getWidth(), getHeight()};
  	return result;
  }

  @Override
  public void setHighlightColor(Color color) {
		ellipse.setStroke(color);
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
	public String getTextAreaAlign() {
		return "center";
	}
	
  @Override
  public boolean supportsTextEditing() {
  	return true;
  }

  @Override
  public int supportedMenuItems() {
    return super.supportedMenuItems() | 
           ContextMenuItem.FONT_SIZE.getValue() |
           ContextMenuItem.LAYERS.getValue();
  }

  @Override
  public boolean supportsModifyToCenter() {
    return false;
  }

  
}
