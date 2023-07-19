package net.sevenscales.editor.uicomponents.uml;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JsArray;

import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.constants.Constants;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.LibraryShapes;
import net.sevenscales.editor.api.Tools;
import net.sevenscales.editor.content.ui.UMLDiagramType;
import net.sevenscales.editor.content.utils.ContainerAttachHelpers;
import net.sevenscales.editor.diagram.ContainerType;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.drag.Anchor;
import net.sevenscales.editor.diagram.drag.AnchorElement;
import net.sevenscales.editor.diagram.shape.GenericShape;
import net.sevenscales.editor.diagram.shape.Info;
import net.sevenscales.editor.diagram.shape.RectShape;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.IGroup;
import net.sevenscales.editor.gfx.domain.IRectangle;
import net.sevenscales.editor.gfx.domain.IPath;
import net.sevenscales.editor.gfx.domain.IShapeFactory;
import net.sevenscales.editor.gfx.domain.Point;
import net.sevenscales.editor.gfx.domain.SupportsRectangleShape;
import net.sevenscales.editor.gfx.domain.Promise;
import net.sevenscales.editor.uicomponents.AbstractDiagramItem;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil;
import net.sevenscales.editor.uicomponents.helpers.ResizeHelpers;
import net.sevenscales.editor.content.ui.ContextMenuItem;


public class GenericContainerElement extends AbstractDiagramItem implements SupportsRectangleShape, ContainerType, IGenericElement, IShapeGroup.ShapeLoaded {
  private List<PathWrapper> paths;
  private int minimumWidth = 25;
  private int minimumHeight = 25;
  private GenericShape shape;
  private RectShape rectShape = new RectShape();
  private List<IRectangle> handles;
  private Point coords = new Point();
  private IGroup group;
  private IGroup subgroup;
  private IGroup textGroup;
  private TextElementFormatUtil textUtil;
  private GenericHasTextElement hasTextElement;
  private IShapeGroup theshape;
  private boolean pathsSetAtLeastOnce;

  public GenericContainerElement(
    ISurfaceHandler surface,
    GenericShape newShape,
    String text, 
    Color backgroundColor,
    Color borderColor,
    Color textColor,
    boolean editable,
    IDiagramItemRO item
  ) {
    super(editable, surface, backgroundColor, borderColor, textColor, item);
    this.shape = newShape;

    group = IShapeFactory.Util.factory(editable).createGroup(surface.getContainerLayer());
    subgroup = IShapeFactory.Util.factory(editable).createGroup(group);
    textGroup = IShapeFactory.Util.factory(editable).createGroup(group);

    handles = new ArrayList<IRectangle>();
    // create handle on each side
    handles.add(createHandle(group));
    handles.add(createHandle(group));
    handles.add(createHandle(group));
    handles.add(createHandle(group));

    for (IRectangle handle : handles) {
      addEvents(handle);
    }

    paths = new ArrayList<PathWrapper>();

    addMouseDiagramHandler(this);

    resizeHelpers = ResizeHelpers.createResizeHelpers(surface);
    hasTextElement = new GenericHasTextElement(this, shape);
    hasTextElement.setMarginLeft(getMarginLeft());
    // textUtil.setMarginTop(0);
    // textUtil.setRotate(-90);

    theshape = Shapes.get(getDiagramItem().getType(), Tools.isSketchMode());
    theshape.fetch(this);

    textUtil = new TextElementFormatUtil(this, hasTextElement, textGroup, surface.getEditorContext());
    setShape(shape.rectShape.left, shape.rectShape.top, shape.rectShape.width, shape.rectShape.height);
    hasTextElement.setY(0);
    setText(text);
    
    setReadOnly(!editable);
    
    setBorderColor(borderColor);
    setBackgroundColor(backgroundColor);
    super.constructorDone();
  }

  private IRectangle createHandle(
    IGroup group
  ) {
    IRectangle result = IShapeFactory.Util.factory(editable).createRectangle(group);
    result.setFill(0, 0 , 0, 0); // transparent 

    return result;
  }

	public void onSuccess() {
		// make sure shape is scaled and set
		pathsSetAtLeastOnce = false;
		createSubPaths(theshape.getShape());
		setShape(shape.rectShape.left, shape.rectShape.top, shape.rectShape.width, shape.rectShape.height);

		// needed to make shape visible
    setBackgroundColor(backgroundColor);
    setBorderColor(borderColor);
	}
  public void onError() {

  }  

  private void createSubPaths(ShapeGroup groupData) {
    GenericElementUtil.createSubPaths(
      groupData,
      new GenericElementUtil.ElementData(
        paths,
        shapes,
        subgroup,
        backgroundColor,
        borderColor,
        getDiagramItem(),
        surface,
        editable
      )
    );
  }  

  protected int getMarginLeft() {
    return 0;
  }

  public Point getDiffFromMouseDownLocation() {
    return new Point(diffFromMouseDownX, diffFromMouseDownY);
  }
  
  public void accept(ISurfaceHandler surface) {
    super.accept(surface);
    surface.makeDraggable(this);
  }
  
  public AnchorElement onAttachArea(Anchor anchor, int x, int y) {
  	return ContainerAttachHelpers.onAttachAreaManualOnly(this, anchor, x, y);
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
    Integer props = getDiagramItem().getShapeProperties();
    if (props == null) {
      props = 0;
    }

    GenericShape newShape = new GenericShape(getDiagramItem().getType(), x, y, getWidth() * factorX, getHeight() * factorY, props, shape.getSvgData());
    return createGenericContainerElement(surface, newShape);
  }

  protected Diagram createGenericContainerElement(ISurfaceHandler surface, GenericShape newShape) {
 		return new GenericContainerElement(surface, newShape, getText(), new Color(backgroundColor), new Color(borderColor), new Color(textColor), editable, LibraryShapes.createByType(getDiagramItem().getType()));
  }
  
//////////////////////////////////////////////////////////////////////
  
  public boolean onResizeArea(int x, int y) {
    return resizeHelpers.isOnResizeArea();
  }
  
  @Override
  public boolean resize(Point diff) {
    int width = getWidth() + diff.x;
    int height = getHeight() + diff.y;

    textUtil.setShapeSize(width, height);

    return resize(getRelativeLeft(), getRelativeTop(), width, height);
  }

  public void setShape(int left, int top, int width, int height) {
    if (width >= 0 && height >= 0) {
      int orgwidth = getWidth();
      int orgheight = getHeight();

      rectShape.left = left;
      rectShape.top = top;
      rectShape.width = width;
      rectShape.height = height;

      // setting some minimum width and height in case those are zero
      // background.setShape(left, top, width == 0 ? 4 : width, height == 0 ? 4 : height, 0);

      setShapeHandles(left, top, width, height);

      // cannot divide with zero!!
      double factorX = getShapeFactorX();
      double factorY = getShapeFactorY();

      if (!pathsSetAtLeastOnce || width != orgwidth || height != orgheight) {
        scalePaths(factorX, factorY);
      }
      // better to scale paths not to scale stroke width
      // or would need to use vector-effect="non-scaling-stroke"
      // which actually should work as well. That is why subgroup setScale
      // is commented out.
      // subgroup.setScale(factorX, factorY);
      subgroup.setTransform(left, top);
      textGroup.setTransform(left, top);

      if (textUtil != null) {
        textUtil.setShapeSize(width, height);
      }

      // if (UiUtils.isIE()) {
      //   // no need to use, which doesn't work svg => pdf, scale down stroke width
      //   // vector-effect="non-scaling-stroke"
      //   // ie8 - ie10 doesn't support vector-effect
      //   doSetStrokeWidth();
      // }

      super.applyHelpersShape();
    }

  }

  private void setShapeHandles(
    int left,
    int top,
    int width,
    int height
  ) {

    if (handles.size() == 4) {
      IRectangle topbar = handles.get(0);
      IRectangle right = handles.get(1);
      IRectangle bottom = handles.get(2);
      IRectangle leftbar = handles.get(3);

      int margin = 30;

      topbar.setShape(
        left,
        top,
        width,
        margin,
        0
      );
      right.setShape(
        left + width - margin,
        top + margin,
        margin, 
        height - margin * 2,
        0
      );
      bottom.setShape(
        left,
        top + height - margin,
        width,
        margin,
        0
      );
      leftbar.setShape(
        left,
        top + margin,
        margin,
        height - margin * 2,
        0
      );
    }

    // >>>>> DEBUG
    // for (IRectangle handle : handles) {
    //   handle.setFill(0, 0 , 0, 0.5);
    // }
    // <<<<< DEBUG
  }

  public void select() {
    super.select();
    
    for (IRectangle handle : handles) {
      handle.setFill(0, 0 , 0, 0.05);
    }
  }

  @Override
  public void unselect() {
  	super.unselect();
    for (IRectangle handle : handles) {
      handle.setFill(0, 0 , 0, 0);
    }
  }  

  private void scalePaths(double factorX, double factorY) {
    boolean result = GenericElementUtil.scalePaths(
      factorX,
      factorY,
      theshape,
      paths
    );

    if (result) {
      // will not be able to set it false if once set to true
      pathsSetAtLeastOnce = result;
    }
  }  

  public double getShapeFactorX() {
    double factorX = 1;
    if (rectShape.width > 0) {
      factorX = (rectShape.width / shapeWidth());
    }

    return factorX;
  }

  public double getShapeFactorY() {
    double factorY = 1; 
    if (rectShape.height > 0) {
      factorY = (rectShape.height / shapeHeight());
    }

    return factorY;
  }

  public double shapeWidth() {
    if (theshape != null && theshape.isReady()) {
      return theshape.getShape().width;
    }

    return 1;
  }
  public double shapeHeight() {
    if (theshape != null && theshape.isReady()) {
      return theshape.getShape().height;
    }
    return 1;
  }


  private String round(String path) {
    JsArray<Shapes.JsPathData> pdata = parse(path);
    // return toPath(_round(pdata));
    return toPath(pdata);
  }

  private String toPath(JsArray<Shapes.JsPathData> pathDatas) {
    String result = "";
    for (int i = 0; i < pathDatas.length(); ++i) {
      Shapes.JsPathData current = pathDatas.get(i);
      result += current.toPath(factorX, factorY);
    }
    return result;
  }

  private native JsArray<Shapes.JsPathData> _round(JsArray<Shapes.JsPathData> commands)/*-{
    return $wnd.pathHelpers.round(commands, 0.05, true)
  }-*/;

  private native JsArray<Shapes.JsPathData> parse(String d)/*-{
    var result = $wnd.svgPathParser.parse(d)
    return result
  }-*/;

  @Override
  public boolean resize(int left, int top, int width, int height) {
    if (width >= minimumWidth && height >= minimumHeight) {
      setShape(getRelativeLeft(), getRelativeTop(), width, height);
      textUtil.setTextShape();
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
    super.resizeEnd();
  }

  public Info getInfo() {
    // shape.rectShape.left = rectSurface.getX();
    // shape.rectShape.top = rectSurface.getY();
    // shape.rectShape.width = rectSurface.getWidth();
    // shape.rectShape.height = rectSurface.getHeight();
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
  public void setBackgroundColor(Color clr) {
    super.setBackgroundColor(clr);
    GenericElementUtil.setBackgroundColor(
      clr,
      new GenericElementUtil.ElementData(
        paths,
        shapes,
        subgroup,
        backgroundColor,
        borderColor,
        getDiagramItem(),
        surface,
        editable
      )
    );
  }
    
  @Override
  public void moveToBack() {
  	group.moveToBack();
  }
  
  @Override
  public int getRelativeLeft() {
  	return rectShape.left;
  }
  
  @Override
  public int getRelativeTop() {
  	return rectShape.top;
  }
  
  @Override
  public int getWidth() {
  	return rectShape.width;
  }
  @Override
  public int getHeight() {
  	return rectShape.height;
  }
  
  @Override
  protected void doSetShape(int[] shape) {
  	setShape(shape[0], shape[1], shape[2], shape[3]);
  }

  @Override
  public void setHighlightColor(Color color) {
    for (PathWrapper p : paths) {
      p.path.setStroke(color);
    }
  }
	
	@Override
	public IGroup getGroup() {
		return group;
  }
  
	@Override
	public IGroup getSubgroup() {
		return subgroup;
  }
  
	@Override
	public IGroup getTextGroup() {
		return textGroup;
	}

	@Override
	protected TextElementFormatUtil getTextFormatter() {
		return textUtil;
	}
		
	// @Override
	// public int getTextAreaTop() {
	// 	return getTop() + 5;
	// }
	
	@Override
	public String getTextAreaAlign() {
		return "center";
	}
	
  @Override
  public boolean supportsTextEditing() {
  	return true;
  }

  @Override
  public boolean supportsModifyToCenter() {
    return false;
  }

  // @Override
  // public double getTextHeight() {
  //   return textUtil.getTextHeight();
  // }  

  @Override
  public Promise getTextSize() {
    return textUtil.getTextSize();
  }

  @Override
  public AbstractDiagramItem getDiagram() {
    return this;
  }

  protected GenericShape getGenericShape() {
    return shape;
  }

  @Override
  public int supportedMenuItems() {
    int result = super.supportedMenuItems() |
			ContextMenuItem.FONT_SIZE.getValue() |
			ContextMenuItem.LAYERS.getValue();
		return result;
  }  

}
