package net.sevenscales.editor.uicomponents.uml;

import com.google.gwt.core.client.Scheduler;

import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.impl.Theme;
import net.sevenscales.editor.api.ActionType;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.shape.Info;
import net.sevenscales.editor.diagram.shape.ChildTextShape;
import net.sevenscales.editor.diagram.drag.Anchor;
import net.sevenscales.editor.diagram.drag.AnchorElement;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.IShape;
import net.sevenscales.editor.gfx.base.GraphicsEventHandler;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.ShapeProperty;
import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil;
import net.sevenscales.editor.uicomponents.TextElementHorizontalFormatUtil;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil.HasTextElement;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil.AbstractHasTextElement;
import net.sevenscales.editor.uicomponents.helpers.ResizeHelpers;
import net.sevenscales.editor.uicomponents.helpers.IConnectionHelpers;
import net.sevenscales.editor.gfx.domain.IParentElement;
import net.sevenscales.editor.gfx.domain.IChildElement;
import net.sevenscales.editor.gfx.domain.SegmentPoint;
import net.sevenscales.editor.gfx.domain.PointDouble;
import net.sevenscales.editor.content.ui.ContextMenuItem;


public class ChildTextElement extends TextElement implements IChildElement {
	private IParentElement parent;
	private double rleft;
	private double rtop;
	private boolean initialized;
	private SegmentPoint fixedPointIndex;
	private double fixedLeft;
	private double fixedTop;
	private int originalX;
	private int originalY;
	private int prevDX;
	private int prevDY;
	private boolean partOfMultipleSelection;
  // private net.sevenscales.editor.gfx.domain.ICircle tempC1;


	public ChildTextElement(ISurfaceHandler surface, ChildTextShape newShape,
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

	// @Override
	// protected int getTextX() {
	// 	if (fixedPointIndex != null && fixedPointIndex.inSegmentIndex == 1) {
	// 		// center if attached to middle point
	// 		return super.getTextX() - (int) getTextWidth() / 2;
	// 	} else {
	// 		return super.getTextX();
	// 	}
	// }

  @Override
	public Diagram duplicate(boolean partOfMultiple) {
		return null;
	}

  @Override
	public Diagram duplicate(ISurfaceHandler surface, boolean partOfMultiple) {
		return null;
	}

  @Override
	public Diagram duplicate(ISurfaceHandler surface, int x, int y) {
		// TextShape newShape = new TextShape(x, y, attachBoundary.getWidth(),
		// 		attachBoundary.getHeight());
		// Diagram result = createDiagram(surface, newShape, getText(), getEditable());
		// return result;
		return null;
	}

	@Override
	public Diagram duplicate(IParentElement parent) {
		ChildTextShape newShape = new ChildTextShape(getLeft() + 20, getTop() + 20, getWidth(), getHeight());
		DiagramItemDTO dto = new DiagramItemDTO();
		dto.setParentId(parent.asDiagram().getDiagramItem().getParentId());

		return new ChildTextElement(surface, newShape, new Color(backgroundColor), new Color(borderColor), new Color(textColor), getText(), editable, dto, parent);
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
	public void snapshotTransformations() {
		super.snapshotTransformations();
		// called before this element is dragged
		resetStartDragPosition();
		partOfMultipleSelection = surface.getSelectionHandler().getSelectedItems().size() > 1;
  }

  @Override
  public void setTransform(int dx, int dy) {
  	if (!partOfMultipleSelection)	{
  		// ignore child text movement if part of multiple selection
  		// supports only to move child text alone
  		super.setTransform(dx, dy);
  	}
  }

	private void resetStartDragPosition() {
  	originalX = 0;
  	originalY = 0;
  	prevDX = 0;
  	prevDY = 0;
	}

	@Override
  public void setPosition(double left, double top) {
    // tempC1.setShape(left, top, 5);
    // tempC1.setStroke(150, 150, 150, 1);
    // tempC1.setFill(150, 150, 150, 1);
    // potential code to move attached relationships
    if (originalX == 0 && originalY == 0) {
	  	originalX = getLeft();
	  	originalY = getTop();
    }

    int roundedLeft = (int) left;
    int roundedTop = (int) top;
    int ddx = roundedLeft - originalX;
    int ddy = roundedTop - originalY;

		int dx = ddx - prevDX;
		int dy = ddy - prevDY;

  	moveAttachedRelationships(dx, dy);

  	prevDX = ddx;
  	prevDY = ddy;
  	// potential code ends

    setShape(new int[]{roundedLeft, roundedTop, getWidth(), getHeight()});
  	// // setShape((int) left, (int) top, getWidth(), getTop());
  }

	private void moveAttachedRelationships(int dx, int dy) {
		for (AnchorElement ae : getAnchors()) {
			ae.dispatch(dx, dy, 0L);
		}
  }

	@Override
	public void editingEnded() {
		super.editingEnded();

		if ("".equals(getText())) {
			Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
				public void execute() {
					// Deferred auto delete, so empty text modify will be before
					// delete OT
					surface.getSelectionHandler().remove(ChildTextElement.this);
				}
			});
		} else {
	  	fixedPointIndex = parent.findClosestSegmentPointIndex(getLeft(), getTop());
	  	if (fixedPointIndex != null && fixedPointIndex.inSegmentIndex == 1 &&
	  			!ShapeProperty.isNoTextAutoAlign(getDiagramItem().getShapeProperties())) {
				PointDouble anchorPoint = parent.getPoint(fixedPointIndex);
				double left = anchorPoint.x - getTextWidth() / 2.0;
				fixedLeft = left - anchorPoint.x;
				setPosition(left, getTop());
	  	}
		}
	}

  // @Override
  // public void editingEnded() {
  //   super.editingEnded();
  //   updateFixedDistance();
  //   parent.moveChild(this);
  // }

	@Override
  public double getFixedDistanceLeft() {
  	return fixedLeft;
  }
  @Override
  public double getFixedDistanceTop() {
  	return fixedTop;
  }

  @Override
	public void saveLastTransform(int dx, int dy) {
		super.saveLastTransform(dx, dy);
		resetStartDragPosition();
		getDiagramItem().addShapeProperty(ShapeProperty.NO_TEXT_AUTO_ALIGN);
		updateFixedDistance();
	}

	@Override
	public Diagram getOwnerComponent(ActionType actionType) {
		switch (actionType) {
			case DRAGGING:
				// need to send parent before child text or text position will be
				// incorrect on the other side
				// if moved as part of other selected items
				if (partOfMultipleSelection) {
					return parent.asDiagram();
				}
		}
		return this;
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
	public void resetInitialized() {
		initialized = false;
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
  public void updateFixedSegment() {
		fixedPointIndex = parent.findClosestSegmentPointIndex(getLeft(), getTop());
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

	@Override
	public int getTextAreaLeft() {
		return getLeft();
	}
	
	@Override
	public int getTextAreaTop() {
		return getTop();
	}
	
	@Override
	public int getTextAreaWidth() {
		return getWidth() + 40;
	}
	
	@Override
	public int getTextAreaHeight() {
		return getHeight();
	}

  @Override
  public int supportedMenuItems() {
  	return ContextMenuItem.NO_MENU.getValue() | 
  				 ContextMenuItem.DUPLICATE.getValue() |
           ContextMenuItem.COLOR_MENU.getValue() |
           ContextMenuItem.URL_LINK.getValue() | 
           ContextMenuItem.DELETE.getValue();
  }

  @Override
	public void removeFromParent() {
		super.removeFromParent();
		parent.removeChild(this);
	}

}
