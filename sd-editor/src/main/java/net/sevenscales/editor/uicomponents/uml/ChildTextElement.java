package net.sevenscales.editor.uicomponents.uml;

import com.google.gwt.core.client.Scheduler;

import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.ShapeProperty;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.impl.Theme;
import net.sevenscales.editor.content.ui.ContextMenuItem;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.drag.Anchor;
import net.sevenscales.editor.diagram.drag.AnchorElement;
import net.sevenscales.editor.diagram.shape.ChildTextShape;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.ElementSize;
import net.sevenscales.editor.gfx.domain.IChildElement;
import net.sevenscales.editor.gfx.domain.IParentElement;
import net.sevenscales.editor.gfx.domain.PointDouble;
import net.sevenscales.editor.gfx.domain.Promise;
import net.sevenscales.editor.gfx.domain.ElementSize;
import net.sevenscales.editor.gfx.domain.SegmentPoint;
import net.sevenscales.editor.uicomponents.AbstractDiagramItem;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil.HasTextElement;
import net.sevenscales.editor.uicomponents.TextElementHorizontalFormatUtil;
import net.sevenscales.editor.uicomponents.helpers.ResizeHelpers;


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
		return new TextElementHorizontalFormatUtil(this, hasTextElement, getTextGroup(), surface.getEditorContext());
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
		// 	||
		// anchor.getRelationship().getDiagramItem().getClientId().equals(parent.asDiagram().getDiagramItem().getClientId())) {
			// ST 22.11.2018: One extra check that child text cannot attach to parent
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

		if (!surface.getEditorContext().isTrue(EditorProperty.ON_SURFACE_LOAD)) {
			// ST 22.11.2018: Do not calculate attached relationships on board load
			moveAttachedRelationships(dx, dy);
		}

  	prevDX = ddx;
  	prevDY = ddy;
  	// potential code ends

    setShape(new int[]{roundedLeft, roundedTop, getWidth(), getHeight()});
		// applyTextAlignment(text, hasTextElement.getX());
  	// // setShape((int) left, (int) top, getWidth(), getTop());
  }

	private void moveAttachedRelationships(int dx, int dy) {
		for (AnchorElement ae : getAnchors()) {
			if (!parent.asDiagram().getDiagramItem().getClientId().equals(ae.getRelationship().getDiagramItem().getClientId())) {
				// ST 22.11.2018: Fix forever loop when child text tries to move parent relationship that tries to move child text
				// do not move attached relationship if pointing to parent
				ae.dispatch(dx, dy, 0);
			}
		}
  }

	@Override
	public void editingEnded(boolean modified) {
		super.editingEnded(modified);

		if ("".equals(getText())) {
			Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
				public void execute() {
					// Deferred auto delete, so empty text modify will be before
					// delete OT
					surface.getSelectionHandler().remove(ChildTextElement.this, true);
					removeFromParent();
				}
			});
		} else {
	  	fixedPointIndex = parent.findClosestSegmentPointIndex(getLeft(), getTop());
	  	if (fixedPointIndex != null && fixedPointIndex.inSegmentIndex == 1 &&
	  			!ShapeProperty.isNoTextAutoAlign(getDiagramItem().getShapeProperties())) {
				final PointDouble anchorPoint = parent.getPoint(fixedPointIndex);

        getTextSize().then(new Promise.FunctionParam<ElementSize>() {
          public void accept(ElementSize size) {
            double left = anchorPoint.x - size.getWidth() / 2.0;
            fixedLeft = left - anchorPoint.x;
            setPosition(left, getTop());
          }
        });
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
	public void setBackgroundColor(Color color) {
		if (color.opacity == 0) {
			// if setting transparent color, use theme board background color
			// to have some background to hide relationship
			super.setBackgroundColor(Theme.getCurrentThemeName().getBoardBackgroundColor().create());
		} else {
			super.setBackgroundColor(color);
		}
	  getAttachBoundary().setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, backgroundColor.opacity);
  }

  @Override
	public Color getDefaultBackgroundColor(Theme.ElementColorScheme colorScheme) {
		// provided PAPER theme as reference when needed e.g. on save
		return colorScheme.getBoardBackgroundColor();
	}

	@Override
	// Copied from AbstractDiagramItem since TextElement doesn't have
	// currently background color. To be changed in the future
	// that TextElement can have background color as well.
	public String getTextAreaBackgroundColor() {
		if (backgroundColor.opacity == 0) {
			// transparent
			return "transparent";
		}
		// NOTE differs from AbstractDiagramItem implementation
		// due to inheriting from TextElement that always returns
		// transparent for getBackgroundColor()
		return getBackgroundColorAsColor().toHexStringWithHash();
	}


	@Override
  public boolean usesSchemeDefaultTextColor(Theme.ElementColorScheme colorScheme) {
    if (colorScheme.getBoardBackgroundColor().equals(backgroundColor) && 
    	  getDefaultTextColor(colorScheme).equals(getTextColor())) {
      return true;
    }
    return false; 
  }	

	// @Override
	// public int getTextAreaLeft() {
	// 	return getLeft();
	// }
	
	// @Override
	// public int getTextAreaTop() {
	// 	return getTop();
	// }
	
	// @Override
	// public int getTextAreaWidth() {
	// 	return getWidth() + 40;
	// }
	
	// @Override
	// public int getTextAreaHeight() {
	// 	return getHeight();
	// }

  @Override
  public int supportedMenuItems() {
  	return ContextMenuItem.NO_MENU.getValue() | 
  				 ContextMenuItem.DUPLICATE.getValue() |
           ContextMenuItem.COLOR_MENU.getValue() |
           ContextMenuItem.URL_LINK.getValue() | 
           ContextMenuItem.DELETE.getValue() |
           ContextMenuItem.TEXT_ALIGN.getValue();
  }

  @Override
	public void removeFromParent() {
		super.removeFromParent();
		parent.removeChild(this);
	}

}
