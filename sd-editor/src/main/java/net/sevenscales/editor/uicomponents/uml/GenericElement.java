package net.sevenscales.editor.uicomponents.uml;

import java.util.ArrayList;
import java.util.List;

import net.sevenscales.domain.ElementType;
import net.sevenscales.domain.ExtensionDTO;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.IPathRO;
import net.sevenscales.domain.ShapeProperty;
import net.sevenscales.domain.js.JsShapeConfig;
import net.sevenscales.domain.constants.Constants;
import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.ActionType;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.LibraryShapes;
import net.sevenscales.editor.api.Tools;
import net.sevenscales.editor.api.impl.Theme;
import net.sevenscales.editor.content.ui.ContextMenuItem;
import net.sevenscales.editor.content.ui.UMLDiagramType;
import net.sevenscales.editor.content.utils.ContainerAttachHelpers;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.drag.Anchor;
import net.sevenscales.editor.diagram.drag.AnchorElement;
import net.sevenscales.editor.diagram.shape.GenericShape;
import net.sevenscales.editor.diagram.shape.Info;
import net.sevenscales.editor.diagram.utils.MouseDiagramEventHelpers;
import net.sevenscales.editor.diagram.utils.UiUtils;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.IGroup;
import net.sevenscales.editor.gfx.domain.IPath;
import net.sevenscales.editor.gfx.domain.IRectangle;
import net.sevenscales.editor.gfx.domain.IShapeFactory;
import net.sevenscales.editor.gfx.domain.Point;
import net.sevenscales.editor.gfx.domain.SupportsRectangleShape;
import net.sevenscales.editor.uicomponents.AbstractDiagramItem;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil;
import net.sevenscales.editor.uicomponents.TextElementVerticalFormatUtil;
import net.sevenscales.editor.uicomponents.helpers.ResizeHelpers;


public class GenericElement extends AbstractDiagramItem implements IGenericElement, SupportsRectangleShape, IShapeGroup.ShapeLoaded {
	private static SLogger logger = SLogger.createLogger(GenericElement.class);

	static {
		SLogger.addFilter(GenericElement.class);
	}

	public static double FREEHAND_STROKE_WIDTH = 2;
	public static int ACTIVITY_START_RADIUS = 10;
	public static int FREEHAND_TOUCH_WIDTH = 20;

	private GenericShape shape;
	private Point coords = new Point();
  private List<PathWrapper> paths;
  private IRectangle background;
  private IGroup group;
  private IGroup subgroup;
  private int left;
  private int top;
  private int width;
  private int height;
  private IShapeGroup theshape;
  private TextElementFormatUtil textUtil;
  private GenericHasTextElement hasTextElement;
  private boolean pathsSetAtLeastOnce;
  private boolean tosvg;
	private boolean forceTextRendering;
	private boolean legacy = true;
  
  private static final String BOUNDARY_COLOR 					= "#aaaaaa";
  private static final String FILL_BORDER_COLOR 			= "fill:bordercolor;";
  private static final String FILL_BORDER_COLOR_DARK 	= "fill:bordercolor-dark;";
  private static final String FILL_SHAPE_BG_COLOR 		= "fill:shape-bgcolor;";
  private static final String FILL_BG_COLOR 					= "fill:bgcolor;";
  private static final String FILL_BG_COLOR_LIGHT			= "fill:bgcolor-light;";

  private IPath.PathTransformer pathTransformer = new IPath.PathTransformer() {
  	public String getShapeStr(int dx, int dy) {
  		return null;
  	}
  };

  private static class PathWrapper {
  	IPath path;
  	ShapeProto proto;

  	PathWrapper(IPath path) {
  		this(path, null);
  	}
  	PathWrapper(IPath path, ShapeProto proto) {
  		this.path = path;
  		this.proto = proto;
  	}

  	boolean isProto() {
  		return this.proto != null;
  	}
  }

	public GenericElement(ISurfaceHandler surface, GenericShape newShape, String text, Color backgroundColor, Color borderColor, Color textColor, boolean editable, IDiagramItemRO item) {
		super(editable, surface, backgroundColor, borderColor, textColor, item);

		this.shape = newShape;
		getDiagramItem().setShapeProperties(newShape.getShapeProperties());

		group = IShapeFactory.Util.factory(editable).createGroup(surface.getElementLayer());
    // group.setAttribute("cursor", "default");

		subgroup = IShapeFactory.Util.factory(editable).createGroup(group);
    // sub// group.setAttribute("cursor", "default");

    background = IShapeFactory.Util.factory(editable).createRectangle(group);
    background.setFill(0, 0 , 0, 0); // transparent
    // background.setStroke("#363636");

  	paths = new ArrayList<PathWrapper>(); // theshape.protos.length

		addEvents(background);
		
		addMouseDiagramHandler(this);

    resizeHelpers = ResizeHelpers.createResizeHelpers(surface);
    hasTextElement = new GenericHasTextElement(this, shape);
    hasTextElement.setMarginLeft(getMarginLeft());
    hasTextElement.setMarginTop(getMarginTop());
		hasTextElement.setMarginBottom(getMarginBottom());

		// ST 20.11.2017: Commented out due to SVG text moved under subgroup
		// and text layer was under background color
		if (legacy) {
			if (ShapeProperty.isTextResizeDimVerticalResize(shape.getShapeProperties())) {
				textUtil = new TextElementVerticalFormatUtil(this, hasTextElement, group, surface.getEditorContext());
				textUtil.setMarginTop(0);
			} else {
				textUtil = new TextElementFormatUtil(this, hasTextElement, group, surface.getEditorContext());
			}
		}

    setReadOnly(!editable);

  	if (shape.getSvgData() != null) {
  		// this is the freehand drawing or any custom svg case!
  		createCustomPaths(shape.getSvgData().getPaths());
  		// diagram item needs to have extionsion data as well for undo/redo calculation
  		Integer lineWeight = item.getExtension() != null ? item.getExtension().getLineWeight() : null;
			getDiagramItem().setExtension(new ExtensionDTO(shape.getSvgData().copy(), lineWeight));
	    setShape(shape.rectShape.left, shape.rectShape.top, shape.rectShape.width, shape.rectShape.height);
	    addPaths();
  	} else {
	  	theshape = Shapes.get(getDiagramItem().getType(), Tools.isSketchMode());
	  	// set initial shape or it will be overridden through getInfo()
	    background.setShape(shape.rectShape.left, shape.rectShape.top, shape.rectShape.width, shape.rectShape.height, 0);
	  	theshape.fetch(this);
		}
		
		if (!legacy) {
			// ST 20.11.2017: Moved due to SVG text layer was behind background color
			// order changed due to text is now part of subgroup
			// and moved with the subgroup
			if (ShapeProperty.isTextResizeDimVerticalResize(shape.getShapeProperties())) {
				textUtil = new TextElementVerticalFormatUtil(this, hasTextElement, subgroup, surface.getEditorContext());
				textUtil.setMarginTop(0);
			} else {
				textUtil = new TextElementFormatUtil(this, hasTextElement, subgroup, surface.getEditorContext());
			}
		}

    // NOTE setShape is called after fetch is ready
    // - fetch is synchronous on page load and when ever shape is found from cache
    // - fetch is asynchronous when shape is not found from cache
    // - in case undo/redo would be implemented in a way that board library shape is deleted
    // immediately if there are no references, it should be removed from local cache as well
    // so shape could be readded to board library. But for now going with cleanup on session end.

    setBackgroundColor(backgroundColor);
    setBorderColor(borderColor);

    if (!"".equals(text)) {
    	// do not set empty initial text, to keep shape dimensions as defined

    	// force to render text even if property editor is open on 
    	// shape creation always, case: onboarding new shape property editor
    	// is open when inserting shapes in delay
    	this.forceTextRendering = true;
	    setText(text);
	    this.forceTextRendering = false;
    } else {
    	textUtil.setStoreText("");
    }

    super.constructorDone();
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

	protected GenericHasTextElement getHasTextElement() {
		return hasTextElement;
	}

	protected int getMarginLeft() {
		if (ShapeProperty.isTextResizeDimVerticalResize(getDiagramItem().getShapeProperties())) {
			return 10;
		}

		return 0;
	}
	protected int getMarginBottom() {
		if (ShapeProperty.isTextResizeDimVerticalResize(getDiagramItem().getShapeProperties())) {
			return 7;
		}
		return 0;
	}
	protected int getMarginTop() {
		return 0;
	}

	private void createSubPaths(ShapeGroup groupData) {
		if (paths.size() == 0) {
			// just in case make sure that initialized only once
			for (ShapeProto p : groupData.protos) {
				IPath path = createSubPath(p);
				paths.add(new PathWrapper(path, p));
			}

			addPaths();
		}
	}

	private void addPaths() {
		for (PathWrapper path : paths) {
	    shapes.add(path.path);
		}
	}

	private IPath createSubPath(ShapeProto proto) {
		return createSubPath(/*proto.toPath(1, 1)*/ null, proto.style);
	}	

	private IPath createSubPath(String path, String style) {
    IPath result = IShapeFactory.Util.factory(editable).createPath(subgroup, pathTransformer);
    result.setStroke(borderColor);
    if (getDiagramItem().getLineWeight() != null) {
			result.setStrokeWidth(getDiagramItem().getLineWeight());
    } else {
    	if (Tools.isSketchMode()) {
    		if (surface.isLibrary()) {
					result.setStrokeWidth(Constants.SKETCH_MODE_LINE_WEIGHT_LIBRARY);
    		} else {
    			result.setStrokeWidth(Constants.SKETCH_MODE_LINE_WEIGHT);
    		}
    	} else {
		    result.setStrokeWidth(FREEHAND_STROKE_WIDTH);
    	}
    }
    // result.setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, backgroundColor.opacity);
    // path.setStrokeCap("round");
    if (style != null && !"".equals(style)) {
    	style = handleStyle(result, style);
	    result.setAttribute("style", style);
    }

		// >>>>>>>>>>> Commented out 4.11.2014 - can be deleted after half a year :)
		// now scaling down line width and each point as inkscape
		// if (!surface.isLibrary()) {
	   //  result.setAttribute("vector-effect", "non-scaling-stroke");
    // }
    // <<<<<<<<<<< Commented out 4.11.2014
    if (path != null) {
	  	result.setShape(path);
    }
  	return result;
	}

	private String handleStyle(IPath path, String style) {
		if (style.contains(FILL_BORDER_COLOR)) {
			path.setFillAsBorderColor(true);
			// need to clear or will contain invalid fill valud since bordercolor is not hex code or pre color code
			style = style.replace(FILL_BORDER_COLOR, "");
		} else if (style.contains(FILL_SHAPE_BG_COLOR)) {
			path.setFillAsShapeBackgroundColor(true);
			style = style.replace(FILL_SHAPE_BG_COLOR, "");
		} else if (style.contains(FILL_BG_COLOR)) {
			path.setFillAsBoardBackgroundColor(true);
			style = style.replace(FILL_BG_COLOR, "");
		} else if (style.contains(FILL_BORDER_COLOR_DARK)) {
			path.setFillAsBorderColorDark(true);
			style = style.replace(FILL_BORDER_COLOR_DARK, "");
		} else if (style.contains(FILL_BG_COLOR_LIGHT)) {
			path.setFillAsBackgroundColorLight(true);
			style = style.replace(FILL_BG_COLOR_LIGHT, "");
		}
		return style;
	}

	private void createCustomPaths(List<? extends IPathRO> pathros) {
		for (IPathRO p : pathros) {
			IPath path = createSubPath(p.getPath(), p.getStyle());
			paths.add(new PathWrapper(path, null));
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
	public int getLeftWithText() {
		int twidth = (int) textUtil.getTextWidth();
		int width = getWidth();
		if (twidth > width) {
			return getLeftText();
		}

		return getLeft();
	}
	private int getLeftText() {
		return (int) (getLeft() + getWidth() / 2 - textUtil.getTextWidth() / 2);
	}
	@Override
	public int getWidth() {
		return background.getWidth();
	}
	@Override
	public int getWidthWithText() {
		int twidth = (int) textUtil.getTextWidth();
		int width = getWidth();
		if (twidth > width) {
			return twidth;
		}
    return width;
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
			((TextElementVerticalFormatUtil) textUtil).setText(newText, editable, forceTextRendering);
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
    GenericShape newShape = new GenericShape(getDiagramItem().getType(), x, y, getWidth() * factorX, getHeight() * factorY, getDiagramItem().getShapeProperties(), shape.getSvgData());
    return createGenericElement(surface, newShape);
  }

  protected Diagram createGenericElement(ISurfaceHandler surface, GenericShape newShape) {
 		return new GenericElement(surface, newShape, getText(), new Color(backgroundColor), new Color(borderColor), new Color(textColor), editable, LibraryShapes.createByType(getDiagramItem().getType()));
  }
	
  public boolean resize(Point diff) {
    return resize(getRelativeLeft(), getRelativeTop(), getWidth() + diff.x, getHeight() + diff.y);
  }

  @Override
  public boolean resize(int left, int top, int width, int height) {
    setShape(getRelativeLeft(), getRelativeTop(), width, height);
    dispatchAndRecalculateAnchorPositions();
    return true;
  }

	@Override
	public void setHeight(int height) {
		resize(getRelativeLeft(), getRelativeTop(), getWidth(), height);
	}

	@Override
	public void setWidth(int width) {
		resize(getRelativeLeft(), getRelativeTop(), width, getHeight());
	}

	public void resizeEnd() {
		super.resizeEnd();

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
  	if (ElementType.NOTE.getValue().equals(shape.getElementType())) {
	    return "--";
  	}
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

	  	int orgwidth = getWidth();
	  	int orgheight = getHeight();

	  	// setting some minimum width and height in case those are zero
	    background.setShape(left, top, width == 0 ? 4 : width, height == 0 ? 4 : height, 0);

			// cannot divide with zero!!
			double factorX = getFactorX();
			double factorY = getFactorY();

			if (shape.getSvgData() != null) {
				// freehand and any custom svg case
		  	subgroup.setScale(factorX, factorY);
			} else if (!pathsSetAtLeastOnce || width != orgwidth || height != orgheight) {
		  	scalePaths(factorX, factorY);
			}
	  	subgroup.setTransform(left, top);
	  	if (UiUtils.isIE()) {
			  // no need to use, which doesn't work svg => pdf, scale down stroke width
			  // vector-effect="non-scaling-stroke"
	  		// ie8 - ie10 doesn't support vector-effect
	  		doSetStrokeWidth();
	  	}

			// ST 15.11.2017: commented out after text moved under subgroup
			// and is moved with the subgroup
			// could speed moving large text shapes...
			if (legacy) {
				textUtil.setTextShape();
			}
			super.applyHelpersShape();
  	}
  }

  private void scalePaths(double factorX, double factorY) {
  	if (theshape.isReady()) {
	  	for (PathWrapper pw : paths) {
	  		if (pw.isProto()) {
		  		pw.path.setShape(pw.proto.toPath(factorX, factorY, theshape.getShape().width));
	  		}
	  	}
	  	pathsSetAtLeastOnce = true;
  	}
		// for (ShapeProto p : groupData.protos) {
  // 	for (IPath path : paths) {
  // 		result.setShape(proto.toPath());

	 //  	// path.scale(factorX, factorY);
  // 	}
  }

  protected void doSetStrokeWidth() {
		double strokeWidth = scaledStrokeWidth(factorX, factorY);
  	for (PathWrapper path : paths) {
	  	path.path.setStrokeWidth(strokeWidth);
  	}
  }

  public double scaledStrokeWidth(double factorX, double factorY) {
  	double factor = Math.max(factorX, factorY);

		double strokeWidth = FREEHAND_STROKE_WIDTH / factor;
  	if (getDiagramItem().getLineWeight() != null) {
	  	strokeWidth = getDiagramItem().getLineWeight() / factor;
  	}
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
  	if (theshape == null) {
			return shape.getSvgData().getWidth();
  	} else if (theshape != null && theshape.isReady()) {
  		return theshape.getShape().width;
  	}
  	return 0;
  }
  public double shapeHeight() {
  	if (theshape == null) {
  		return shape.getSvgData().getHeight();
  	} else if (theshape != null && theshape.isReady()) {
  		return theshape.getShape().height;
  	}
  	return 0;
  }

  public void setHighlightColor(Color color) {
  	for (PathWrapper path : paths) {
			path.path.setStroke(color);
			if (path.path.isFillAsBorderColor()) {
				path.path.setFill(color);
			} else if (path.path.isFillAsBoardBackgroundColor()) {
  			path.path.setFill(Theme.getCurrentThemeName().getBoardBackgroundColor());
  		} else if (path.path.isFillAsBorderColorDark()) {
  			path.path.setFill(color.toDarker());
  		}
  	}
		// background.setStroke(color);
  }
  
  @Override
  public void setBackgroundColor(int red, int green, int blue, double opacity) {
  	super.setBackgroundColor(red, green, blue, opacity);
  	for (PathWrapper path : paths) {
  		if (!path.path.isFillAsBorderColor() && !path.path.isFillAsBoardBackgroundColor()) {
			    path.path.setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, backgroundColor.opacity);
			  }

			if (path.path.isFillAsBackgroundColorLight()) {
				Color color = getBackgroundColorAsColor();
  			path.path.setFill(color.toLighter());
  		} else if (path.path.isFillAsShapeBackgroundColor()) {
  			path.path.setFill(red, green, blue, opacity);
			}
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

	@Override
	public IGroup getSubgroup() {
		return subgroup;
	}
	
	@Override
  public boolean supportsTextEditing() {
  	return true;
  }

  @Override
  public void editingEnded(boolean modified) {
		if (textUtil instanceof TextElementVerticalFormatUtil) {
	  	if (modified) {
		  	// Scheduler.get().scheduleDeferred(new ScheduledCommand() {
		 		// 	public void execute() {
		 				applyText();
		 		// 	}
		 		// });
		  }
		}

	  // need to call as last to make sure attached relationships use
	  // closest path if set
  	super.editingEnded(modified);
  }

  private void applyText() {
  	textUtil.setText(textUtil.getText(), true, true);
  	MouseDiagramEventHelpers.fireChangedWithRelatedRelationships(surface, this, ActionType.TEXT_CHANGED);
  }

	@Override
	public int getTextAreaLeft() {
  	if (ShapeProperty.isTextPositionBottom(shape.getShapeProperties())) {
			return getLeftText();
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
		// cannot find enum since user custom types will not be part of enums!!
		if (hasTextElement.verticalAlignMiddle()) {
			return textUtil.middleY(0) + getTransformY();
		}
  	if (ShapeProperty.isTextPositionBottom(shape.getShapeProperties())) {
			return getTop() + getHeight() - 1;
		} else {
			return super.getTextAreaTop();
  	}
	}

	@Override
	public String getTextAreaAlign() {
		if (ShapeProperty.isTextAlignCenter(shape.getShapeProperties())) {
			return "center";
		}
		return super.getTextAreaAlign();
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

  @Override
	public double getTextHeight() {
  	return textUtil.getTextHeight();
  }  

  @Override
  public void select() {
  	super.select();
  	if (!getDiagramItem().isGroup()) {
	  	background.setStroke(DEFAULT_SELECTION_COLOR);
  	}
  }

  @Override
  public void unselect() {
  	super.unselect();
  	background.setStroke(0x33, 0x33, 0x33, 0);
  }

  @Override
  public void setHighlight(boolean highlight) {
  	super.setHighlight(highlight);
  	if (highlight) {
	  	background.setStroke(HIGHLIGHT_COLOR);
  	} else {
	  	background.setStroke(0x33, 0x33, 0x33, 0);
  	}
  }

  @Override
  public void setHighlightBackgroundBorder(Color color) {
  	background.setStroke(color);
  }

  @Override
  public void clearHighlightBackgroundBorder() {
  	background.setStroke(0x33, 0x33, 0x33, 0);
  }

  @Override
  public boolean isSketchiness() {
    return getDiagramItem().isSketchiness();
  }

  @Override
  public int supportedMenuItems() {
    int result = super.supportedMenuItems() |
			ContextMenuItem.FONT_SIZE.getValue() |
			ContextMenuItem.LAYERS.getValue();
		result |= extraSupportedMenuItemsByType();
		return result;
  }

  public AbstractDiagramItem getDiagram() {
  	return this;
  }

  protected GenericShape getGenericShape() {
    return shape;
  }

  private int extraSupportedMenuItemsByType() {
		if (textUtil instanceof TextElementVerticalFormatUtil) {
			return ContextMenuItem.TEXT_ALIGN.getValue();
		}
		return 0;
  }

  @Override
  public boolean hasDefaultColors() {
  	ShapeGroup sg = theshape.getShape();
  	if (sg != null) {
	  	JsShapeConfig config = sg.getShapeConfig();
	  	if (config != null) {
	  		// either has default shape background color or border color defined
				return config.isDefaultBgColor() || config.isDefaultBorderColor();
	  	}
	  }
    return false;
  }

  @Override
  public void restoreDefaultColors() {
  	ShapeGroup sg = theshape.getShape();
  	if (sg != null) {

	  	JsShapeConfig config = sg.getShapeConfig();

	  	if (config != null && config.isDefaultBgColor()) {
	  		Color c = Color.hexToColor(config.getDefaultBgColor());
		  	setBackgroundColor(c);
	  	}

	  	if (config != null && config.isDefaultBorderColor()) {
	  		Color c = Color.hexToColor(config.getDefaultBorderColor());
		  	setBorderColor(c);
	  	}

	  }
	}
}
