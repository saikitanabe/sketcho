package net.sevenscales.editor.uicomponents.uml;

import java.util.List;
import java.util.ArrayList;

import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.impl.Theme;
import net.sevenscales.editor.content.ui.ContextMenuItem;
import net.sevenscales.editor.content.ui.UMLDiagramSelections.UMLDiagramType;
import net.sevenscales.editor.content.utils.AreaUtils;
import net.sevenscales.editor.content.utils.ContainerAttachHelpers;
import net.sevenscales.editor.content.utils.DiagramHelpers;
import net.sevenscales.editor.content.utils.IntegerHelpers;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.shape.GenericShape;
import net.sevenscales.editor.diagram.shape.Info;
import net.sevenscales.editor.diagram.utils.UiUtils;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.IShape;
import net.sevenscales.editor.gfx.domain.IGroup;
import net.sevenscales.editor.gfx.domain.ILine;
import net.sevenscales.editor.gfx.domain.IPath;
import net.sevenscales.editor.gfx.domain.IRectangle;
import net.sevenscales.editor.gfx.domain.IShapeFactory;
import net.sevenscales.editor.gfx.domain.SupportsRectangleShape;
import net.sevenscales.editor.gfx.base.GraphicsEventHandler;
import net.sevenscales.editor.silver.SilverUtils;
import net.sevenscales.editor.uicomponents.AbstractDiagramItem;
import net.sevenscales.editor.diagram.drag.Anchor;
import net.sevenscales.editor.diagram.drag.AnchorElement;
import net.sevenscales.editor.gfx.domain.Point;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil;
import net.sevenscales.editor.uicomponents.TextElementVerticalFormatUtil;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil.AbstractHasTextElement;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil.HasTextElement;
import net.sevenscales.editor.uicomponents.helpers.ResizeHelpers;
import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.domain.ElementType;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.domain.ShapeProperty;
import net.sevenscales.domain.IPathRO;
import net.sevenscales.domain.ExtensionDTO;


public class GenericElement extends AbstractDiagramItem implements SupportsRectangleShape {
	private static SLogger logger = SLogger.createLogger(GenericElement.class);

	public static double FREEHAND_STROKE_WIDTH = 2;
	public static int ACTIVITY_START_RADIUS = 10;
	public static int FREEHAND_TOUCH_WIDTH = 20;

	private GenericShape shape;
	private Point coords = new Point();
  private List<IPath> paths;
  private IRectangle background;
  private IGroup group;
  private IGroup subgroup;
  private int left;
  private int top;
  private int width;
  private int height;
  private Shapes.Group theshape;
  private TextElementFormatUtil textUtil;
  private HasTextElement hasTextElement;

  private boolean tosvg;
  
  private static final String BOUNDARY_COLOR = "#aaaaaa";

  private IPath.PathTransformer pathTransformer = new IPath.PathTransformer() {
  	public String getShapeStr(int dx, int dy) {
  		return null;
  	}
  };

	public GenericElement(ISurfaceHandler surface, GenericShape newShape, String text, Color backgroundColor, Color borderColor, Color textColor, boolean editable, IDiagramItemRO item) {
		super(editable, surface, backgroundColor, borderColor, textColor, item);

		this.shape = newShape;
		getDiagramItem().setShapeProperties(newShape.getShapeProperties());

		group = IShapeFactory.Util.factory(editable).createGroup(surface.getElementLayer());
    group.setAttribute("cursor", "default");

		subgroup = IShapeFactory.Util.factory(editable).createGroup(group);
    // subgroup.setAttribute("cursor", "default");

  	paths = new ArrayList<IPath>(); // theshape.protos.length
  	if (shape.getSvgData() != null) {
  		createCustomPaths(shape.getSvgData().getPaths());
  		// diagram item needs to have extionsion data as well for undo/redo calculation
			getDiagramItem().setExtension(new ExtensionDTO(shape.getSvgData().copy()));
  	} else {
	  	theshape = Shapes.get(getDiagramItem().getType());
	    createSubPaths(theshape);
  	}

    background = IShapeFactory.Util.factory(editable).createRectangle(group);
    background.setFill(0, 0 , 0, 0); // transparent
    // background.setStroke("#363636");

		addEvents(background);
		
		addMouseDiagramHandler(this);

		for (IPath path : paths) {
	    shapes.add(path);
		}

    resizeHelpers = ResizeHelpers.createResizeHelpers(surface);
    hasTextElement = new GenericHasTextElement(this, shape);
    if (ShapeProperty.isTextResizeDimVerticalResize(shape.getShapeProperties())) {
	    textUtil = new TextElementVerticalFormatUtil(this, hasTextElement, group, surface.getEditorContext());
	    textUtil.setMarginTop(0);
    } else {
	    textUtil = new TextElementFormatUtil(this, hasTextElement, group, surface.getEditorContext());
    }

    setReadOnly(!editable);
    setShape(shape.rectShape.left, shape.rectShape.top, shape.rectShape.width, shape.rectShape.height);

    setBorderColor(borderColor);

    if (!"".equals(text)) {
    	// do not set empty initial text, to keep shape dimensions as defined
	    setText(text);
    } else {
    	textUtil.setStoreText("");
    }

    super.constructorDone();
	}

	private void createSubPaths(Shapes.Group groupData) {
		for (Shapes.Proto p : groupData.protos) {
			IPath path = createSubPath(p);
			paths.add(path);
		}
	}

	private IPath createSubPath(Shapes.Proto proto) {
		return createSubPath(proto.path, proto.style);
	}

	private IPath createSubPath(String path, String style) {
    IPath result = IShapeFactory.Util.factory(editable).createPath(subgroup, pathTransformer);
    result.setStroke(borderColor);
    result.setStrokeWidth(FREEHAND_STROKE_WIDTH);
    result.setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, backgroundColor.opacity);
    // path.setStrokeCap("round");
    if (style != null && !"".equals(style)) {
    	style = handleStyle(result, style);
	    result.setAttribute("style", style);
    }

    if (!surface.isLibrary()) {
	    result.setAttribute("vector-effect", "non-scaling-stroke");
    }
  	result.setShape(path);
  	return result;
	}

	private String handleStyle(IPath path, String style) {
		if (style.contains("fill:bordercolor")) {
			path.setFillAsBorderColor(true);
			// need to clear or will contain invalid fill valud since bordercolor is not hex code or pre color code
			style = style.replace("fill:bordercolor", "");
		}
		return style;
	}

	private void createCustomPaths(List<? extends IPathRO> pathros) {
		for (IPathRO p : pathros) {
			IPath path = createSubPath(p.getPath(), p.getStyle());
			paths.add(path);
		}
	}
	
	@Override
	public int getRelativeLeft() {
		return background.getX();
	}
	@Override
	public int getRelativeTop() {
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

	public String getText() {
		return textUtil.getText();
	}

	public void doSetText(String newText) {
		if (textUtil instanceof TextElementVerticalFormatUtil) {
			((TextElementVerticalFormatUtil) textUtil).setText(newText, editable, true);
		} else {
	    textUtil.setText(newText, editable);
		}
	}

	public Point getDiffFromMouseDownLocation() {
		return new Point(diffFromMouseDownX, diffFromMouseDownY);
	}
	
	public void accept(ISurfaceHandler surface) {
	  super.accept(surface);
		surface.makeDraggable(this);
	}
	
	@Override
	public AnchorElement onAttachArea(Anchor anchor, int x, int y) {
  	return ContainerAttachHelpers.onAttachArea(this, anchor, x, y);
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
    GenericShape newShape = new GenericShape(getDiagramItem().getType(), x, y, getWidth() * factorX, getHeight() * factorY, shape.getShapeProperties(), shape.getSvgData());
    Diagram result = new GenericElement(surface, newShape, getText(), new Color(backgroundColor), new Color(borderColor), new Color(textColor), editable, DiagramItemDTO.createGenericItem(ElementType.getEnum(getDiagramItem().getType())));
    return result;
  }
	
	public void resizeStart() {
	}

  public boolean resize(Point diff) {
    return resize(getRelativeLeft(), getRelativeTop(), getWidth() + diff.x, getHeight() + diff.y);
  }

  protected boolean resize(int left, int top, int width, int height) {
    setShape(getRelativeLeft(), getRelativeTop(), width, height);
    dispatchAndRecalculateAnchorPositions();
    return true;
  }

	@Override	
	public void setHeight(int height) {
		setShape(getRelativeLeft(), getRelativeTop(), getWidth(), height);
		// dispatchAndRecalculateAnchorPositions();
	}

	public void resizeEnd() {
		if (textUtil instanceof TextElementVerticalFormatUtil) {
			((TextElementVerticalFormatUtil)textUtil).setText(getText(), editable, true);
		}
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
  	return UMLDiagramType.FREE_HAND;
  }
  
 //  @Override
	// protected void doSetShape(int[] shape) {
 //  	this.shape.points = shape;
 //  	path.setShape(calcShape(shape));
 //  	// backgroundPath.setShape(calcShape(shape, 0, 0));

 //  	this.left = DiagramHelpers.getLeftCoordinate(shape);
 //  	this.top = DiagramHelpers.getTopCoordinate(shape);
 //  	this.width = DiagramHelpers.getWidth(shape);
 //  	this.height = DiagramHelpers.getHeight(shape);
 //    connectionHelpers.setShape(left, top, width, height);
	// }

  @Override
  protected void doSetShape(int[] shape) {
    // resize(shape[0], shape[1], shape[2], shape[3]);
    setShape(shape[0], shape[1], shape[2], shape[3]);
  }

  @Override
  public void setShape(int left, int top, int width, int height) {
  	// if ((width > 1 && height >= 0) || (height > 1 && width >= 0)) {
  	if (width >= 0 && height >= 0) {
	  	this.left = left;
	  	this.top = top;
	  	this.width = width;
	  	this.height = height;

	  	// setting some minimum width and height in case those are zero
	    background.setShape(left, top, width == 0 ? 4 : width, height == 0 ? 4 : height, 0);

			// cannot divide with zero!!
			double factorX = getFactorX();
			double factorY = getFactorY();

	  	subgroup.setScale(factorX, factorY);
	  	subgroup.setTransform(left, top);
	  	if (UiUtils.isIE() || surface.isLibrary()) {
			  // no need to use, which doesn't work svg => pdf, scale down stroke width
			  // vector-effect="non-scaling-stroke"
	  		// ie8 - ie10 doesn't support vector-effect
	  		double strokeWidth = scaledStrokeWidth(factorX, factorY);
		  	for (IPath path : paths) {
			  	path.setStrokeWidth(strokeWidth);
		  	}
	  	}

	    textUtil.setTextShape();
			super.applyHelpersShape();
  	}
  }

  public double scaledStrokeWidth(double factorX, double factorY) {
  	double factor = Math.max(factorX, factorY);
  	double strokeWidth = FREEHAND_STROKE_WIDTH / factor;
  	return strokeWidth;
  }

  public double getFactorX() {
		double factorX = 1;
		if (width > 0) {
			factorX = (width / shapeWidth());
		}
  	if (shape.getSvgData() != null && shape.getSvgData().getWidth() == 0) {
  		// cannot scale width or svg line will disappear
  		factorX = 1;
  	}

		return factorX;
	}

	public double getFactorY() {
		double factorY = 1; 
		if (height > 0) {
			factorY = (height / shapeHeight());
		}

  	if (shape.getSvgData() != null && shape.getSvgData().getHeight() == 0) {
  		// cannot scale height or svg line will disappear
  		factorY = 1;
  	}

		return factorY;
	}

  public double shapeWidth() {
  	return theshape != null ? theshape.width : shape.getSvgData().getWidth();
  }
  public double shapeHeight() {
  	return theshape != null ? theshape.height : shape.getSvgData().getHeight();
  }

  public void setHighlightColor(Color color) {
  	for (IPath path : paths) {
			path.setStroke(color);
			if (path.isFillAsBorderColor()) {
				path.setFill(color);
			}
  	}
		// background.setStroke(color);
  }
  
  @Override
  public void setBackgroundColor(int red, int green, int blue, double opacity) {
  	super.setBackgroundColor(red, green, blue, opacity);
  	for (IPath path : paths) {
  		if (!path.isFillAsBorderColor()) {
		    path.setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, backgroundColor.opacity);
  		}
	    // if (path.isFillAsBorderColor()) {
	    // 	path.setFill(color);
	    // }
  	}
  }

  @Override
	public void setTextColor(int red, int green, int blue) {
	  super.setTextColor(red, green, blue);
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
	public boolean supportsOnlyTextareaDynamicHeight() {
		if (textUtil instanceof TextElementVerticalFormatUtil) {
			return true;
		}
		return false;
	}

	public IGroup getSubgroup() {
		return subgroup;
	}
	
	@Override
  public boolean supportsTextEditing() {
  	return true;
  }
  
	@Override
	public int getTextAreaLeft() {
  	if (ShapeProperty.isTextPositionBottom(shape.getShapeProperties())) {
			return (int) (getLeft() + getWidth() / 2 - textUtil.getTextWidth() / 2);
		} else {
			return super.getTextAreaLeft();
		}
	}

	@Override
	public int getTextAreaHeight() {
		if (ShapeProperty.isTextPositionBottom(shape.getShapeProperties())) {
    	return (int) textUtil.getTextHeight();
    } else {
			return super.getTextAreaHeight();
  	}
	}
	
	@Override
	public int getTextAreaWidth() {
  	if (ShapeProperty.isTextPositionBottom(shape.getShapeProperties())) {
    	return (int) textUtil.getTextWidth();
    } else {
			return super.getTextAreaWidth();
  	}
	}
	
	@Override
	public int getTextAreaTop() {
  	if (ShapeProperty.isTextPositionBottom(shape.getShapeProperties())) {
			return getTop() + getHeight() - 1;
		} else {
			return super.getTextAreaTop();
  	}
	}

	@Override
	public String getTextAreaAlign() {
		if (ShapeProperty.isTextResizeDimVerticalResize(shape.getShapeProperties())) {
			return super.getTextAreaAlign();
		} else {
			return "center";
		}
	}

	@Override
	public String getTextAreaBackgroundColor() {
  	if (ShapeProperty.isTextPositionBottom(shape.getShapeProperties())) {
			return "transparent";
		} else {
			return super.getTextAreaBackgroundColor();
  	}
	}

  @Override
  public boolean isTextElementBackgroundTransparent() {
  	if (ShapeProperty.isTextPositionBottom(shape.getShapeProperties())) {
	    return true;
	  } else {
			return super.isTextElementBackgroundTransparent();
		}
  }
  
  @Override
  public boolean isTextColorAccordingToBackgroundColor() {
  	if (ShapeProperty.isTextPositionBottom(shape.getShapeProperties())) {
	    return true;
	  } else {
			return super.isTextColorAccordingToBackgroundColor();
  	}
  }

  @Override
  public int getHeightWithText() {
  	if (ShapeProperty.isTextPositionBottom(shape.getShapeProperties())) {
	  TextElementFormatUtil textFormatter = getTextFormatter();
    return getHeight() + (int) textFormatter.getTextHeight();
	  } else {
			return super.getHeightWithText();
  	}
  }

  public double getTextHeight() {
  	return textUtil.getTextHeight();
  }

}
