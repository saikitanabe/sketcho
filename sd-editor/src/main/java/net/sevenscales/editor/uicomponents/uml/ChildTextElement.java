package net.sevenscales.editor.uicomponents.uml;

import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.impl.Theme;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.shape.Info;
import net.sevenscales.editor.diagram.shape.TextShape;
import net.sevenscales.editor.diagram.drag.Anchor;
import net.sevenscales.editor.diagram.drag.AnchorElement;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.IShape;
import net.sevenscales.editor.gfx.base.GraphicsEventHandler;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil;
import net.sevenscales.editor.uicomponents.TextElementHorizontalFormatUtil;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil.HasTextElement;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil.AbstractHasTextElement;
import net.sevenscales.editor.uicomponents.helpers.ResizeHelpers;
import net.sevenscales.editor.gfx.domain.IParentElement;
import net.sevenscales.editor.gfx.domain.IChildElement;
import net.sevenscales.editor.gfx.domain.SegmentPoint;
import net.sevenscales.editor.gfx.domain.PointDouble;


public class ChildTextElement extends TextElement implements IChildElement {
	private IParentElement parent;
	private double rleft;
	private double rtop;
	private boolean initialized;
	private SegmentPoint fixedPointIndex;
	private double fixedLeft;
	private double fixedTop;
  // private net.sevenscales.editor.gfx.domain.ICircle tempC1;


	public ChildTextElement(ISurfaceHandler surface, TextShape newShape,
			Color backgroundColor, Color borderColor, Color textColor, String text, boolean editable, IDiagramItemRO item, IParentElement parent) {
		super(surface, newShape, backgroundColor, borderColor, textColor, text, editable, item);
		this.parent = parent;
		parent.addChild(this);

		// tempC1 = net.sevenscales.editor.gfx.domain.IShapeFactory.Util.factory(editable).createCircle(getGroup());
    setBackgroundColor(backgroundColor);
    super.constructorDone();
	}

	@Override
	protected ResizeHelpers createResizeHelpers() {
		return null;
	}

	@Override
	protected TextElementFormatUtil createTextFormatter(HasTextElement hasTextElement) {
		return new TextElementHorizontalFormatUtil(this, hasTextElement, getGroup(), surface.getEditorContext());
	}

	@Override
  public AnchorElement onAttachArea(Anchor anchor, int x, int y) {
  	if (anchor.getRelationship() == parent.asDiagram()) {
  		return null;
  	}
    return super.onAttachArea(anchor, x, y);
  }

  @Override
  public void copyFrom(IDiagramItemRO diagramItem) {
  	super.copyFrom(diagramItem);
  	updateFixedDistance();
  }

	@Override
	public Info getInfo() {
		// TODO add parent client id
		return super.getInfo();
	}

	@Override
	public Diagram getParent() {
		return parent.asDiagram();
	}

	@Override
	public Diagram asDiagram() {
		return this;
	}

	@Override
  public void saveRelativeDistance(double rleft, double rtop) {
  	this.rleft = rleft;
  	this.rtop = rtop;
  	// cachedWidth = getWidth();
  	// cachedHeight = getHei
  }
	@Override
  public double getRelativeDistanceLeft() {
  	return rleft;
  }
	@Override
  public double getRelativeDistanceTop() {
  	return rtop;
  }
	@Override
  public void setPosition(double left, double top) {
    // tempC1.setShape(left, top, 5);
    // tempC1.setStroke(150, 150, 150, 1);
    // tempC1.setFill(150, 150, 150, 1);
    setShape(new int[]{(int) left, (int) top, getWidth(), getHeight()});
  	// // setShape((int) left, (int) top, getWidth(), getTop());
  	// select();
  }

	@Override
  public double getFixedLeft() {
  	return fixedLeft;
  }
  @Override
  public double getFixedTop() {
  	return fixedTop;
  }

  @Override
	public void saveLastTransform(int dx, int dy) {
		super.saveLastTransform(dx, dy);
		updateFixedDistance();
	}

	@Override
	public SegmentPoint fixedPointIndex() {
		return fixedPointIndex;
	}

	@Override
	public boolean isInitialized() {
		return initialized;
	}

  @Override
  public void updateFixedDistance() {
  	fixedPointIndex = parent.findClosestSegmentPointIndex(getLeft(), getTop());
  	PointDouble anchorPoint = parent.getPoint(fixedPointIndex);
		fixedLeft = getLeft() - anchorPoint.x;
		fixedTop =  getTop() - anchorPoint.y;
		initialized = true;
  }

  @Override
  public boolean canSetBackgroundColor() {
  	return true;
  }

  @Override
	public void setBackgroundColor(int red, int green, int blue, double opacity) {
  	super.setBackgroundColor(red, green, blue, opacity);
	  getAttachBoundary().setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, backgroundColor.opacity);
  }

  @Override
	public Color getDefaultBackgroundColor(Theme.ElementColorScheme colorScheme) {
		// provided PAPER theme as reference when needed e.g. on save
		return colorScheme.getBoardBackgroundColor();
	}

	@Override
  public boolean usesSchemeDefaultTextColor(Theme.ElementColorScheme colorScheme) {
    if (colorScheme.getBoardBackgroundColor().equals(backgroundColor) && 
    	  getDefaultTextColor(colorScheme).equals(getTextColorAsColor())) {
      return true;
    }
    return false; 
  }	

}
