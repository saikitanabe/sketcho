package net.sevenscales.editor.uicomponents.uml;

import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.ot.BoardOTHelpers;
import net.sevenscales.editor.api.ActionType;
import net.sevenscales.editor.content.ui.UMLDiagramSelections.UMLDiagramType;
import net.sevenscales.editor.content.ui.ContextMenuItem;
import net.sevenscales.editor.content.utils.AreaUtils;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.shape.Info;
import net.sevenscales.editor.diagram.shape.TextShape;
import net.sevenscales.editor.diagram.utils.GridUtils;
import net.sevenscales.editor.diagram.utils.MouseDiagramEventHelpers;
import net.sevenscales.editor.gfx.base.GraphicsEventHandler;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.IContainer;
import net.sevenscales.editor.gfx.domain.IGroup;
import net.sevenscales.editor.gfx.domain.IRectangle;
import net.sevenscales.editor.gfx.domain.IShape;
import net.sevenscales.editor.gfx.domain.IShapeFactory;
import net.sevenscales.editor.gfx.domain.SupportsRectangleShape;
import net.sevenscales.editor.silver.SilverUtils;
import net.sevenscales.editor.uicomponents.AbstractDiagramItem;
import net.sevenscales.editor.gfx.domain.Point;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil.AbstractHasTextElement;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil.HasTextElement;
import net.sevenscales.editor.uicomponents.TextElementVerticalFormatUtil;
import net.sevenscales.editor.uicomponents.helpers.ResizeHelpers;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.domain.ShapeProperty;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;


public class TextElement extends AbstractDiagramItem implements
		SupportsRectangleShape {
	private IRectangle attachBoundary;
	private TextShape shape;
	private Point coords = new Point();
	private IGroup group;
	private TextElementFormatUtil textUtil;
	private int minimumWidth = 5;
	private int minimumHeight = 5;

	public TextElement(ISurfaceHandler surface, TextShape newShape,
			Color backgroundColor, Color borderColor, Color textColor, String text, boolean editable, IDiagramItemRO item) {
		super(editable, surface, backgroundColor, borderColor, textColor, item);
		this.shape = newShape;

		group = IShapeFactory.Util.factory(editable).createGroup(
				surface.getElementLayer());
		// group.setAttribute("cursor", "default");

		attachBoundary = IShapeFactory.Util.factory(editable)
				.createRectangle(group);
		// attachBoundary.setFill(255, 255, 255, 0.1);
		attachBoundary.setFill(new Color(0, 0, 0, 0));

		// child text element prints background as well
		// so should text element do later
		shapes.add(attachBoundary);
		addEvents(attachBoundary);

		resizeHelpers = createResizeHelpers();
//		resizeHelpers.setVisible(false);

		// attachBoundary.addGraphicsMouseDownHandler(this);
		// attachBoundary.addGraphicsMouseUpHandler(this);
		// attachBoundary.addGraphicsMouseMoveHandler(this);

		addMouseDiagramHandler(this);

		// shapes.add(attachBoundary);
		
		textUtil = createTextFormatter(hasTextElement);
		setReadOnly(!editable);

		setShape(shape.rectShape.left, shape.rectShape.top, shape.rectShape.width, shape.rectShape.height);
		setText(text);
		
    super.constructorDone();
	}

	protected ResizeHelpers createResizeHelpers() {
		return ResizeHelpers.createResizeHelpers(surface);
	}

	protected TextElementFormatUtil createTextFormatter(HasTextElement hasTextElement) {
		return new TextElementVerticalFormatUtil(this, hasTextElement, group, surface.getEditorContext());
	}

	// nice way to clearly separate interface methods :)
	private HasTextElement hasTextElement = createHasTextElement();

	protected HasTextElement createHasTextElement() {
		return new AbstractHasTextElement(this) {
				public int getWidth() {
					return attachBoundary.getWidth();
				}

				public int getX() {
					return getTextX();
				}

				public int getY() {
					return attachBoundary.getY();
				}
				public int getHeight() {
					return attachBoundary.getHeight();
				}

				public void removeShape(IShape shape) {
					group.remove(shape);
					shapes.remove(shape);
				}

			  public boolean centeredText() {
			  	return ShapeProperty.isTextAlignCenter(getDiagramItem().getShapeProperties());
			  }

				public String getLink() {
					return TextElement.this.getLink();
				}

				public boolean isAutoResize() {
					return TextElement.this.isAutoResize();
				}

				public void resize(int x, int y, int width, int height) {
					TextElement.this.resize(x, y, width, height);
					fireSizeChanged();
				}

		    public void resizeHeight(int height) {
		      TextElement.this.setHeight(height);
				}    

				public void setLink(String link) {
					TextElement.this.setLink(link);
				}

				public boolean supportsTitleCenter() {
					return false;
				}

				// public int getTextMargin() {
				// 	return 0;
				// }

				public boolean forceAutoResize() {
					return true;
				}

				public GraphicsEventHandler getGraphicsMouseHandler() {
					return TextElement.this;
				}

				@Override
				public Color getTextColor() {
					return TextElement.this.getTextColor();
				}

			};
	}

	protected int getTextX() {
		return attachBoundary.getX();
	}

	protected IShape createElement(IContainer surface) {
		return IShapeFactory.Util.factory(editable).createRectangle(surface);
	}

	public Point getDiffFromMouseDownLocation() {
		return new Point(diffFromMouseDownX, diffFromMouseDownY);
	}

	public void accept(ISurfaceHandler surface) {
		super.accept(surface);
		surface.makeDraggable(this);
	}

//	public AnchorElement onAttachArea(Anchor anchor, int x, int y) {
//		if (AnchorUtils.onAttachArea(x, y, attachBoundary)) {
//			AnchorElement result = anchorMap.get(anchor);
//			AnchorUtils.anchorPoint(x, y, tempAnchorProperties, attachBoundary);
//
//			if (result == null) {
//				result = new AnchorElement(anchor, this);
//				anchorMap.put(anchor, result);
//			}
//
//			result.setAx(tempAnchorProperties.x);
//			result.setAy(tempAnchorProperties.y);
//			result.setRelativeX(tempAnchorProperties.relativeValueX);
//			result.setRelativeY(tempAnchorProperties.relativeValueY);
//
//			this.anchorPoint.setShape(tempAnchorProperties.x, tempAnchorProperties.y,
//					6);
//			this.anchorPoint.moveToFront();
//
//			return result;
//		}
//		return null;
//	}

	public boolean resize(Point diff) {
		return resize(getRelativeLeft(), getRelativeTop(), getWidth() + diff.x, getHeight()	+ diff.y);
	}

	protected boolean resize(int left, int top, int width, int height) {
		if (width >= minimumWidth && height >= minimumHeight) {
			setShape(left, top, width, height);
			dispatchAndRecalculateAnchorPositions();
			return true;
		}
		return false;
	}

	public void resizeEnd() {
		super.resizeEnd();
		textUtil.setText(getText(), editable, true);
	}

	public String getText() {
		return textUtil.getText();
	}

	public double getTextWidth() {
		return textUtil.getTextWidth();
	}

	@Override
	public void doSetText(String text) {
		textUtil.setText(text, editable);
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
		TextShape newShape = new TextShape(x, y, attachBoundary.getWidth(),
				attachBoundary.getHeight());
		Diagram result = createDiagram(surface, newShape, getText(), getEditable());
		return result;
	}

	protected Diagram createDiagram(ISurfaceHandler surface, TextShape newShape,
			String text, boolean editable) {
		return new TextElement(surface, newShape, new Color(backgroundColor), new Color(borderColor), new Color(textColor), text, editable, new DiagramItemDTO());
	}

	// ////////////////////////////////////////////////////////////////////

	// @Override
	// public void applyTransform(int dx, int dy) {
	// group.applyTransform(dx, dy);
	//
	// // group.setClip(shape.left, shape.top, shape.width, shape.height);
	// }

	public Info getInfo() {
		fillInfo(shape);
		return this.shape;
	}

	@Override
	public int getRelativeLeft() {
		return attachBoundary.getX();
	}

	@Override
	public int getRelativeTop() {
		return attachBoundary.getY();
	}

	@Override
	public int getWidth() {
		return attachBoundary.getWidth();
	}

	@Override
	public int getHeight() {
		return attachBoundary.getHeight();
	}

	@Override	
	public void setHeight(int height) {
		setShape(getRelativeLeft(), getRelativeTop(), getWidth(), height);
	}

	@Override	
	public void setWidth(int width) {
		setShape(getRelativeLeft(), getRelativeTop(), width, getHeight());
	}

	public void setShape(int left, int top, int width, int height) {
		// if (textUtil.isAlignMiddle()) {
		// 	left -= width / 2.0;
		// }
		group.setTransform(left, top);
		attachBoundary.setShape(0, 0, width, height, 4);
		textUtil.setTextShape();
    super.applyHelpersShape();
	}

	public void setShape(Info shape) {

	}

	public void setHighlightColor(Color color) {
		if (!color.equals(BoardOTHelpers.HIGHLIGHT_COLOR)
				&& !color.equals(DEFAULT_SELECTION_COLOR)) {
			// text element default border color is transparent
			color = new Color(0, 0, 0, 0);
		}

		attachBoundary.setStroke(color);
	}

  @Override
  public void setHighlightBackgroundBorder(Color color) {
  	attachBoundary.setStroke(color);
  }

  @Override
  public void clearHighlightBackgroundBorder() {
  	attachBoundary.setStroke(0x33, 0x33, 0x33, 0);
  }
	
	
	@Override
	public void saveLastTransform(int dx, int dy) {
		super.saveLastTransform(dx, dy);
		textUtil.show();
	}

	public void setReadOnly(boolean value) {
		super.setReadOnly(value);
//		attachBoundary.setVisibility(!value);
	}

	public String getDefaultRelationship() {
		return "-";
	}

	@Override
	public UMLDiagramType getDiagramType() {
		return UMLDiagramType.TEXT;
	}

	@Override
	protected void doSetShape(int[] shape) {
		setShape(shape[0], shape[1], shape[2], shape[3]);
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
		return getTop() + 5;
	}

	@Override
	public boolean supportsOnlyTextareaDynamicHeight() {
		return true;
	}
	
	@Override
	public String getBackgroundColor() {
		return "transparent";
	}

	public String getTextAreaBackgroundColor() {
		return "transparent";
	}
	
	@Override
	public void select() {
		super.select();
	}
	
	@Override
	public void unselect() {
		super.unselect();
	}
	
  @Override
  public boolean supportsTextEditing() {
  	return true;
  }

  @Override
  public void editingEnded(boolean modified) {
  	if (modified) {
	  	// Scheduler.get().scheduleDeferred(new ScheduledCommand() {
	 		// 	public void execute() {
	 				applyText();
	 		// 	}
	 		// });
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
  public boolean isTextElementBackgroundTransparent() {
    return true;
  }

  @Override
  public boolean canSetBackgroundColor() {
  	return false;
  }

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		textUtil.setVisible(visible);
	}

  @Override
  public int supportedMenuItems() {
  	return super.supportedMenuItems() |
           ContextMenuItem.LAYERS.getValue() |
           ContextMenuItem.TEXT_ALIGN.getValue();
  }

  protected IRectangle getAttachBoundary() {
  	return attachBoundary;
  }

}
