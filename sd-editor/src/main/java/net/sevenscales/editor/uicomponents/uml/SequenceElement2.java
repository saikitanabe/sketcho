package net.sevenscales.editor.uicomponents.uml;


import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;

import net.sevenscales.domain.ElementType;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.constants.Constants;
import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.LibraryShapes;
import net.sevenscales.editor.content.ui.UMLDiagramType;
import net.sevenscales.editor.content.utils.AreaUtils;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.DiagramDragHandler;
import net.sevenscales.editor.diagram.ISequenceElement;
import net.sevenscales.editor.diagram.drag.Anchor;
import net.sevenscales.editor.diagram.drag.AnchorElement;
import net.sevenscales.editor.diagram.shape.GenericShape;
import net.sevenscales.editor.diagram.shape.Info;
import net.sevenscales.editor.diagram.shape.RectShape;
import net.sevenscales.editor.diagram.shape.SequenceShape;
import net.sevenscales.editor.diagram.utils.GridUtils;
import net.sevenscales.editor.diagram.utils.UiUtils;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.ILine;
import net.sevenscales.editor.gfx.domain.IRectangle;
import net.sevenscales.editor.gfx.domain.IShape;
import net.sevenscales.editor.gfx.domain.IShapeFactory;
import net.sevenscales.editor.gfx.domain.Point;
import net.sevenscales.editor.gfx.domain.SupportsRectangleShape;
import net.sevenscales.editor.silver.SilverUtils;
import net.sevenscales.editor.uicomponents.AnchorUtils;
import net.sevenscales.editor.uicomponents.CardinalDirection;
import net.sevenscales.editor.uicomponents.helpers.IConnectionHelpers.IExtraConnectionHandler;
import net.sevenscales.editor.uicomponents.helpers.ILifeLineEditor;
import net.sevenscales.editor.uicomponents.helpers.LifeLineEditorHelper;

public class SequenceElement2 extends GenericElement implements DiagramDragHandler, SupportsRectangleShape, IExtraConnectionHandler, ISequenceElement {
	private static SLogger logger = SLogger.createLogger(SequenceElement.class);
	private static final int RADIUS_EXTRA = 12;
	private static final int SELECTION_AREA_WIDTH = 12;
	
	public static final int RADIUS_START = 12;
	public static final int RADIUS_SELECTION = 25;
	
  private ILine line;
  private ILifeLineEditor lifeLineEditor;
  private ISurfaceHandler surface;
  private Point currentDragStartPoint = new Point();
//  private LineShape lineShape = new LineShape();
//  private SequenceShape seqShape;
  private IRectangle selectionArea;
  private Integer[] fixedAnchorPoints = new Integer[]{};
  
  private static final int CONNECT_DISTANCE = 15;
  
  public interface LifelineCall {
    void begin();
  	void makeCircle(int cx, int cy);
  }

  private static Integer resolveProperties() {
    LibraryShapes.ShapeProps sh = LibraryShapes.getShapeProps(ElementType.SEQUENCE.getValue());
    if (sh != null) {
      return sh.properties;
    }
    return null;
  }

	public SequenceElement2(
	      ISurfaceHandler parent, SequenceShape shape, String text, Color backgroundColor, Color borderColor, Color textColor, boolean editable, IDiagramItemRO item) {
		super(parent, shape.toGenericShape(resolveProperties()), text, backgroundColor, borderColor, textColor, editable, item);
		this.surface = parent;
    getDiagramItem().setShapeProperties(getGenericShape().getShapeProperties());

// 		this.seqShape = shape;
//		lineShape.x1 = shape.rectShape.left + shape.rectShape.width/2;
//		lineShape.y1 = shape.rectShape.top + shape.rectShape.height;
//		lineShape.x2 = lineShape.x1;
//		lineShape.y2 = lineShape.y1 + shape.lifeLineHeight;
    int x1 = GridUtils.align(shape.rectShape.left + shape.rectShape.width/2);
    int y1 = shape.rectShape.top + shape.rectShape.height;
    int x2 = x1;
    int y2 = y1 + shape.lifeLineHeight;

		line = IShapeFactory.Util.factory(editable).createLine(getGroup());
//		line.setShape(x1, y1, x2, y2);
    line.setStroke(borderColor);
    line.setStyle(ILine.DASHED);
		line.setStrokeWidth(Constants.SKETCH_MODE_REL_LINE_WEIGHT);
		
		addEvents(line);
		
		selectionArea = IShapeFactory.Util.factory(editable).createRectangle(getGroup());
//		selectionArea.setShape(x1-5, y1, 5*2, y2-y1, 0);
	  selectionArea.setFill(200, 200, 200, 0);
//	  selectionArea.setFill("transparent");
	  selectionArea.addGraphicsMouseDownHandler(this);
	  selectionArea.addGraphicsMouseUpHandler(this);
	  
	  selectionArea.addGraphicsMouseEnterHandler(this);
	  selectionArea.addGraphicsMouseLeaveHandler(this);
	  
	  addTouchSupport();
	  lifeLineEditor = LifeLineEditorHelper.createLifeLineEditorHelper(surface, this, getEditable());
    
    // shapes.add(line);
    // shapes.add(selectionArea);

    setReadOnly(!editable);
    
    setShape(getLeft(), getTop(), getWidth(), getHeight(), shape.lifeLineHeight);
    super.setText(text);

    setLineHighlightColor(borderColor);
    super.constructorDone();
	}

  /**
  * Line is absolute element and not relative at the moment so passed to svg 
  * generation as absolute shape as text shapes are as well.
  */
  @Override
  public List<List<IShape>> getTextElements() {
    List<List<IShape>> texts = super.getTextElements();
    List<List<IShape>> result = new ArrayList<List<IShape>>();
    for (List<IShape> l : texts) {
      result.add(l);
    }

    List<IShape> lineShapes = new ArrayList<IShape>();
    lineShapes.add(line);
    result.add(lineShapes);
    return result;
  }
	
	private void addTouchSupport() {
	  selectionArea.addGraphicsTouchStartHandler(this);
	  selectionArea.addGraphicsTouchEndHandler(this);
	}

	private LifelineCall addExtraConnections = new LifelineCall() {
    @Override
    public void begin() {
    }
		@Override
		public void makeCircle(int cx, int cy) {
			connectionHelpers.addExtraConnectionHandle(SequenceElement2.this, cx, cy, RADIUS_EXTRA);
		}
	};
	private void addRemoveVisibleConnectionHelpers() {
		logger.debug2("addRemoveVisibleConnectionHelpers...");
		removeExtraConnectionHandles();
		
		iterateLifelinePoints(addExtraConnections);
		
    selectionArea.moveToBack();
    line.moveToBack();
    
    if (UiUtils.isIE()) {
    	// IE hack, selectionArea and line are after canvas Rectangle
    	surface.moveToBack();
    }
	}
	
  private void removeExtraConnectionHandles() {
		connectionHelpers.removeExtraConnectionHandles();
	}

	
	//	@Override
	public void accept(ISurfaceHandler surface) {
	  super.accept(surface);
	  
	  // NOTE: there is a special handling on MouseDiagramDragHandler for CircleElement follow
//	  surface.addDragHandler(this);
	}

//	@Override
  protected JavaScriptObject createElement(JavaScriptObject canvas, RectShape shape) {
    return SilverUtils.createRect(canvas, 0, 0, shape.height, shape.width);
  }
  
  public Diagram duplicate(boolean partOfMultiple) {
    return duplicate(surface, partOfMultiple);
  }

  public Diagram duplicate(ISurfaceHandler surface, boolean partOfMultiple) {
    GenericShape rs = (GenericShape) super.getInfo();
    if (!partOfMultiple) {
    	return duplicate(surface, rs.rectShape.left + rs.rectShape.width + 30, rs.rectShape.top);
    }
    
    return duplicate(surface, rs.rectShape.left + 20, rs.rectShape.top);
  }

  public Diagram duplicate(ISurfaceHandler surface, int x, int y) {
    GenericShape rs = (GenericShape) super.getInfo();
    SequenceShape ss = new SequenceShape(x, y, 
        rs.rectShape.width, rs.rectShape.height, line.getY2() - line.getY1());
    return new SequenceElement2(surface, ss, getText(), new Color(backgroundColor), new Color(borderColor), new Color(textColor), editable, LibraryShapes.createByType(ElementType.SEQUENCE.getValue()));
  }

	public void setShape(int left, int top, int width, int height, int lifelineheight) {
    left = GridUtils.align(left);
    top = GridUtils.align(top);
    width = GridUtils.align(width);
    height = GridUtils.align(height);

	  int x1 = GridUtils.align(left + width/2);
//	  int dy = top + height - line.getY1();
	  int y1 = top + height;
	  int x2 = x1;
	  int y2 = y1 + lifelineheight;
	  
	  line.setShape(x1, y1, x2, y2);
		selectionArea.setShape(x1 - SELECTION_AREA_WIDTH, y1, SELECTION_AREA_WIDTH * 2, y2-y1, 0);
    
    makeFixedAnchorPoints();
    lifeLineEditor.setShape(this);

//    ++this.dispatchSequence;
//    for (AnchorElement a : anchorMap.values()) {
//      a.setAx(x1);
////      a.setAy(((int)(height*a.getRelativeY()))+top);
//      a.dispatch(dispatchSequence);
//    }
    
//    addRemoveVisibleConnectionHelpers();
	}
	
	private void iterateLifelinePoints(LifelineCall call) {
    call.begin();
    // This would be correct implementation, but due to early bug data is already attached to different points!
		for (int y = getTop() + getHeight() + CONNECT_DISTANCE - 4; y < line.getY2() + getTransformY() - CONNECT_DISTANCE; y += CONNECT_DISTANCE) {
      // after all putting more stable version for the future, this could lead to some feedback... that not aligning correctly...
    // for (int y = getCenterY() + (CONNECT_DISTANCE * 2); y < line.getY2() + getTransformY() - CONNECT_DISTANCE; y += CONNECT_DISTANCE) {
    //   if (y > getTop() + getHeight()) {
    //    // HACK: do not create circles if not over the edge
        call.makeCircle(line.getX1() + getTransformX(), y);
      // }
		}
	}

  @Override
  protected boolean fixedIncludesTransformation() {
    return true;
  }
	
	private class FixedPointsCalc implements LifelineCall {
		public FixedPointsCalc() {
		}
    @Override
    public void begin() {
    }
		
		@Override
		public void makeCircle(int cx, int cy) {
		}
	};

  private static class MakeLifeLineFactory implements LifelineCall {
    private List<Integer> fixed;

    @Override
    public void begin() {
    }

    @Override
    public void makeCircle(int cx, int cy) {
      if (fixed != null) {
        fixed.add(cx);
        fixed.add(cy);
      }
    }

    public void setFixed(List<Integer> fixed) {
      this.fixed = fixed;
    }
  }

  private MakeLifeLineFactory makeLifeLineFactory = new MakeLifeLineFactory();

  private LifelineCall updateLifeLineFactory = new LifelineCall() {
    private int index = 0;

    @Override
    public void begin() {
      index = 0;
    }

    @Override
    public void makeCircle(int cx, int cy) {
      if (fixedAnchorPoints != null && index + 1 < fixedAnchorPoints.length) {
        fixedAnchorPoints[index] = cx;
        fixedAnchorPoints[++index] = cy;
      }
    }
  };

  private void makeFixedAnchorPoints() {
//		List<Integer> fixed = new ArrayList<Integer>(connectionHelpers.getExtraConnectionHandles().size() * 2 + (2 * 4));
  	final List<Integer> fixed = new ArrayList<Integer>();
		
    makeLifeLineFactory.setFixed(fixed);
  	iterateLifelinePoints(makeLifeLineFactory);
//		DiagramHelpers.fill4FixedRectPoints(fixed, getLeft(), getTop(), getWidth(), getHeight());
		
//		for (ConnectionHandle c : connectionHelpers.getExtraConnectionHandles()) {
//			fixed.add(c.visibleHandle.getX());
//			fixed.add(c.visibleHandle.getY());
//		}
		
//  	logger.debug("makeFixedAnchorPoints {} ...", fixed);

  	// Bug fix.
  	// it's either a bug somewhere or fixedAnchorPoints kept all
  	// even thought the size got smaller => caused null entries in fixedAnchorPoints
  	// and Integer.intValue started to fail
  	fixedAnchorPoints = new Integer[fixed.size()];
		fixedAnchorPoints = fixed.toArray(fixedAnchorPoints);
//  	logger.debug("makeFixedAnchorPoints {} ... done", this);
	}

  private void updateFixedAnchorPoints() {
    iterateLifelinePoints(updateLifeLineFactory);
  }

	public void setShape(int left, int top, int width, int height) {
  	super.setShape(left, top, width, height);
    if (line != null) {
      setShape(left, top, width, height, line.getY2() - line.getY1());
    }
  }
	
	@Override
	public boolean resize(int left, int top, int width, int height) {
//	  int length = line.getY2() - line.getY1();
	  boolean result = super.resize(left, top, width, height);
//	  int dy = top + height - line.getY1();
//	  setShape(left, top, width, height, length);
    return result;
	}
	
	@Override
	public void applyTransform(int dx, int dy) {
	  super.applyTransform(dx, dy);
	  lifeLineEditor.applyTransform(dx, dy);
	}
	
//	@Override
//	public void saveLastTransform() {
//		super.saveLastTransform();
//	  int dx = SilverUtils.getTransformX(line.getRawNode());
//    int dy = SilverUtils.getTransformY(line.getRawNode());
//    line.applyTransform(dx, dy);
//    selectionArea.applyTransform(dx, dy);
//    SilverUtils.resetRenderTransform(line.getRawNode());
//    SilverUtils.resetRenderTransform(selectionArea.getRawNode());
////    
//    startSelection.saveLastTransform();
////	  startSelection.setShape(line.getX2()+dx, line.getY2()+dy, startSelection.getRadius());
////    
////    lineShape.x1 = line.getX1();
////    lineShape.y1 = line.getY1();
////    lineShape.x2 = line.getX2();
////    lineShape.y2 = line.getY2();
////    SilverUtils.resetRenderTransform(line);
////    SilverUtils.setDashedLineShape(
////        line, lineShape.x1, lineShape.y1, lineShape.x2, lineShape.y2, color);
//	}

//	@Override
	public void select() {
	  super.select();
	  setHighlightColor(DEFAULT_SELECTION_COLOR);

	  // HACK: when background is moved lineShape is no longer
	  // in correct position. Probably it lineShape should be removed
	  // and used only line. No duplicate information.
//    lineShape.x1 = line.getX1();
//    lineShape.y1 = line.getY1();
//	  lineShape.x2 = line.getX2();
//	  lineShape.y2 = line.getY2();
	  lifeLineEditor.show(this);
    // TODO: unselect startSelection
    // callback if startSelection is selected unselect parent
	}
	
	//	@Override
	public void unselect() {
	  super.unselect();
    setHighlightColor(borderColor);
    lifeLineEditor.hide(this);
	}
	
  public void dragStart(Diagram sender) {
    if (sender == lifeLineEditor.getStartSelection()) {
      currentDragStartPoint.x = line.getX2();
      currentDragStartPoint.y = line.getY2();
      
      removeExtraConnectionHandles();
    }
  }

	public void onDrag(Diagram sender, int dx, int dy) {
    if (sender == lifeLineEditor.getStartSelection()) {
      // redraw while dragging
//	    int x2 = startSelection.getLocationX()+startSelection.getTransformX();
//	    int y2 = startSelection.getLocationY()+startSelection.getTransformY();
      line.setShape(line.getX1(), line.getY1(), line.getX1(), line.getY2() + dy);
      selectionArea.setShape(line.getX1() - SELECTION_AREA_WIDTH, line.getY1(), SELECTION_AREA_WIDTH * 2, line.getY2()-line.getY1(), 0);
    }
  }

  public void dragEnd(Diagram sender) {
  	if (sender == lifeLineEditor.getStartSelection()) {
  		// better to modify visible connection helpers as well
  		addRemoveVisibleConnectionHelpers();
  		makeFixedAnchorPoints();
  	}
  }
   
  @Override
  public AnchorElement onAttachArea(Anchor anchor, int x, int y) {
  	AnchorElement result = null;
  	if (onDynamicAttachArea(anchor, x, y)) {
      result = makeDynamicTempAnchorProperties(anchor, x, y);
  	}
    if (AnchorUtils.onAttachArea(x, y, selectionArea.getX() + getTransformX(), selectionArea.getY() + getTransformY(),
      selectionArea.getWidth(), selectionArea.getHeight())) {
    	// anchor to life line if on selection area
    	result = makeFixedTempAnchorProperties(anchor, x, y);
      fixCardinality(tempAnchorProperties, anchor);
    }
    return result;
  }

  @Override
  protected boolean setFixedOrRelativeAnchor(int x, int y, Anchor anchor) {
    if (AnchorUtils.onAttachArea(x, y, getLeft(), getTop(), getWidth(), getHeight())) {
      // if on rectangle area
      AnchorUtils.relativeValue(tempAnchorProperties, x, y, getLeft(), getTop(), getWidth(), getHeight());
      return false;
    } else {
      // it's on fixed anchor area
      // fix anchor points, just in case
      makeFixedAnchorPoints();
      AnchorUtils.anchorPoint(x, y, getLeft(), getTop(), getWidth(), getHeight(), tempAnchorProperties, fixedAnchorPoints);
      fixCardinality(tempAnchorProperties, anchor);
      anchor.setPoint(x, y);
      return true;
    }
  }

  /**
  * Cardinality for sequence line can be only from east or west and needs to be checked from
  * arrow direction is it coming from left to right or vice versa.
  */
  @Override
  protected void fixCardinality(AnchorUtils.AnchorProperties aprops, Anchor anchor) {
    Relationship2 r = anchor.getRelationship();
    if (r != null) {
      if (AnchorUtils.isRightToLeft(r.getStartX(), r.getStartY(), r.getEndX(), r.getEndY())) {
        aprops.cardinalDirection = CardinalDirection.EAST;
      } else {
        aprops.cardinalDirection = CardinalDirection.WEST;
      }
    }
    super.fixCardinality(aprops, anchor);
  }

  @Override
  public boolean onArea(int left, int top, int right, int bottom) {
  	if (super.onArea(left, top, right, bottom)) {
  		// check first rectangle area
  		int x = line.getX1() + getTransformX();
  		int y = line.getY1() + getTransformY();
  		int bx = line.getX2() + getTransformX();
  		int by = line.getY2() + getTransformY();
  		return AreaUtils.onArea(x, y, bx, by, left, top, right, bottom);
  	}
  	return false;
  }
  
  @Override
	public Info getInfo() {
	    GenericShape gs = (GenericShape) super.getInfo();
	    SequenceShape result = new SequenceShape();
	    result.rectShape = gs.rectShape;
	    result.lifeLineHeight = getLifelineHeight();
	    
	    super.fillInfo(result);
	    return result;
	}

  private int getLifelineHeight() {
    return line.getY2() - line.getY1();
  }

  @Override
  public int getSvgHeightWithText() {
    return super.getHeight() + getLifelineHeight();
  }

  @Override
  public void setReadOnly(boolean value) {
    if (selectionArea != null) {
      // due to inheritance base class calls setReadOnly before sub class construction
    	// that's why check existance
      selectionArea.setVisibility(!value);
      lifeLineEditor.hide(this);
//      if (startSelection.isVisible()) {
//    	  // do not set visibility if not already visible
//    	  // might change state in undesirable way...
//    	  // check SvgConverter for details
//    	  startSelection.setVisible(!value);
//      }
    }
    super.setReadOnly(value);
  }

  @Override
  public UMLDiagramType getDiagramType() {
  	return UMLDiagramType.SEQUENCE;
  }
  
  @Override
  protected void doSetShape(int[] shape) {
  	// note the order, lifeline height is the first value
  	super.setShape(shape[1], shape[2], shape[3], shape[4]);
  	setShape(shape[1], shape[2], shape[3], shape[4], shape[0]);
  }
  
  @Override
  public void setHighlightColor(Color color) {
  	super.setHighlightColor(color);
    setLineHighlightColor(color);
  }

  private void setLineHighlightColor(Color color) {
    if (line != null) {
      line.setStroke(color);
    }
  }

  @Override
  public boolean onArea(int x, int y) {
  	if (onAttachAreaListener == null) {
  		return false;
  	}

  	if (this instanceof SupportsRectangleShape) {
  		if (AnchorUtils.onAttachArea(x, y, getLeft(), getTop(), getWidth(), getHeight() + line.getY2() - line.getY1())) {
  			onAttachAreaListener.onAttachArea(this, x, y);
  			return true;
  		} else {
  			onAttachAreaListener.notOnArea(this);
  		}
  	}
  	return false;
  }
  
  @Override
  public void removeFromParent() {
  	super.removeFromParent();
  	lifeLineEditor.forceHide();
  	connectionHelpers.removeExtraConnectionHandles();
  }

  @Override
  public void setTransform(int dx, int dy) {
    super.setTransform(dx, dy);
    lifeLineEditor.hide(this);
  }
    
  @Override
  public void saveLastTransform(int dx, int dy) {
  	super.saveLastTransform(dx, dy);
  	lifeLineEditor.saveLastTransform(dx, dy);
    lifeLineEditor.show(this);
  	addRemoveVisibleConnectionHelpers();
  	makeFixedAnchorPoints();
  }

  @Override
  protected Integer[] getFixedAnchorPoints() {
  	return fixedAnchorPoints;
  }

	@Override
	public void showExtraConnectionHandles() {
		addRemoveVisibleConnectionHelpers();
	}
	
	@Override
	public boolean disableBottom() {
		return true;
	}
	
	public ILine getLine() {
    return line;
  }
	
  @Override
  public boolean isSequenceElement() {
    return true;
  }

  @Override
  public boolean supportsModifyToCenter() {
    return false;
  }

  // cannot be used directly like this, doubles height all the time 
  // @Override
  // public int getHeight() {
  //   int lineHeight = 0;
  //   if (line != null) {
  //     lineHeight = line.getY2() - line.getY1();
  //   }
  //   return super.getHeight() + lineHeight;
  // }
 

//  @Override
//  public int getHeight() {
//  	return line.getY2() - line.getY1() + super.getHeight();
//  }

}