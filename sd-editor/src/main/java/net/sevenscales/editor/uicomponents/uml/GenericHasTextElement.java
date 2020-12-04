package net.sevenscales.editor.uicomponents.uml;  

import net.sevenscales.domain.ElementType;
import net.sevenscales.domain.ShapeProperty;
import net.sevenscales.editor.api.Tools;
import net.sevenscales.editor.diagram.shape.GenericShape;
import net.sevenscales.editor.gfx.base.GraphicsEventHandler;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil.AbstractHasTextElement;
import net.sevenscales.editor.uicomponents.TextElementVerticalFormatUtil;


class GenericHasTextElement extends AbstractHasTextElement {
	private float marginLeftFactor;
	private float marginTopFactor;
	private float marginBottomFactor;
	private int marginLeft;
	private int marginTop;
	private int marginBottom;

	private boolean yDefined;
	private int y;

	private IGenericElement parent;
	private GenericShape shape;
	private String elementType;
	private boolean legacy = false;

	GenericHasTextElement(IGenericElement parent, GenericShape shape) {
		super(parent.getDiagram());
		this.parent = parent;
		this.shape = shape;
		this.elementType = shape.getElementType();
		// possible to customize for each element type
		if (elementType.equals(ElementType.BUBBLE.getValue()) || 
				elementType.equals(ElementType.BUBBLE_R.getValue())) {
			this.marginLeftFactor = 0.09f;
			if (Tools.isSketchMode()) {
				this.marginTopFactor = 0.05f;
			} else {
				this.marginTopFactor = 0.09f;
			}
			this.marginBottomFactor = 0.11f;
		}
	}

	@Override
  public int getWidth() {
    return parent.getWidth() - getMarginLeft() * 2;
  }

	@Override
  public int getX() {
		if (legacy) {
			// ST 20.11.2017: text moved with a subgroup transformation
			return parent.getRelativeLeft() + getMarginLeft();
		}
    return getMarginLeft();
  }

  public void setY(int y) {
  	yDefined = true;
  	this.y = y;
  }

	@Override
  public int getY() {
  	if (yDefined) {
  		return y;
  	}

  	if (Tools.isSketchMode() && ElementType.BUBBLE_R.getValue().equals(shape.getElementType())) {
			// ST 20.11.2017: text moved with a subgroup transformation
			if (legacy) {
				return parent.getRelativeTop() + getMarginTop();
			}
  		return getMarginTop();
  	} else if (Tools.isSketchMode() && ElementType.BUBBLE.getValue().equals(shape.getElementType())) {
			// ST 20.11.2017: text moved with a subgroup transformation			
			if (legacy) {
				return parent.getRelativeTop() + getMarginTop();
			}
			return getMarginTop();
  	}

  	if (ShapeProperty.isTextPositionBottom(parent.getDiagramItem().getShapeProperties())) {
			// ST 20.11.2017: text moved with a subgroup transformation			
			if (legacy) {
				return parent.getRelativeTop() + parent.getHeight() - TextElementFormatUtil.ROW_HEIGHT + 8;
			}
			return parent.getHeight() - TextElementFormatUtil.ROW_HEIGHT + 8;
  	} else if (ShapeProperty.isTextResizeDimVerticalResize(parent.getDiagramItem().getShapeProperties())) {
			// ST 20.11.2017: text moved with a subgroup transformation			
			if (legacy) {
				return parent.getRelativeTop() + parent.getHeight() / 2
						- ((int) parent.getTextHeight() / 2 + TextElementVerticalFormatUtil.DEFAULT_TOP_MARGIN / 2);
			}
  		return parent.getHeight() / 2 - ((int) parent.getTextHeight() / 2 + TextElementVerticalFormatUtil.DEFAULT_TOP_MARGIN / 2);
  	} else {
			// switch (elementType) {
			// 	case STORAGE: {
		 //  		return parent.getRelativeTop() + 5;
		 //  	}
			// }

			// ST 20.11.2017: text moved with a subgroup transformation
			if (legacy) {
				return parent.getRelativeTop();
			}
  		return 0;
  	}
  }

	@Override
  public int getHeight() {
  	return parent.getHeight();
  }
 
	@Override
  public boolean verticalAlignMiddle() {
  	if (ShapeProperty.isTextPositionBottom(parent.getDiagramItem().getShapeProperties()) || 
  		  ShapeProperty.isTextResizeDimVerticalResize(parent.getDiagramItem().getShapeProperties())) {
			return false;
		} else if (ShapeProperty.isTextPositionTop(parent.getDiagramItem().getShapeProperties())) {
			return false;
		} else {
			return true;
		}
  }

  @Override
  public boolean centeredText() {
  	return ShapeProperty.isTextAlignCenter(parent.getDiagramItem().getShapeProperties());
  }

	@Override
  public boolean supportElementResize() {
  	if (ShapeProperty.isTextPositionBottom(parent.getDiagramItem().getShapeProperties())) {
			return false;
  	} else if (ShapeProperty.isShapeAutoResizeDisabled(parent.getDiagramItem().getShapeProperties())) {
  		return false;
  	} else {
			return true;
  	}
  }

	@Override
	public boolean boldText() {
  	if (ShapeProperty.isTextResizeDimHorizontalResize(parent.getDiagramItem().getShapeProperties())) {
  		return true;
  	}
  	return ShapeProperty.boldTitle(parent.getDiagramItem().getShapeProperties());
	}

	@Override
  public String getLink() {
    return parent.getDiagram().getLink();
  }

	@Override
  public boolean isAutoResize() {
    return parent.getDiagram().isAutoResize();
  }

	@Override
  public void resize(int x, int y, int width, int height) {
  	if (ShapeProperty.isTextResizeDimHorizontalResize(parent.getDiagramItem().getShapeProperties())) {
	    parent.resize(x, y, width, height);
  	} else {
	    parent.resize(x, y, width, width);
  	}
    parent.fireSizeChanged();
  }

  @Override
	public void resizeHeight(int height) {
		parent.getDiagram().setHeight(height);
  }

	@Override
  public void setLink(String link) {
    // parent.setLink(link);
  }

	@Override
  public boolean supportsTitleCenter() {
  	return ShapeProperty.isTextTitleCenter(parent.getDiagramItem().getShapeProperties());
  }

	@Override
  public int getTextMargin(int defaultMargin) {
  	if (elementType.equals(ElementType.COMPONENT.getValue())) {
  		return (int) (defaultMargin * 20f/30f);
  	} else if (elementType.equals(ElementType.BUBBLE_R.getValue()) || 
  		elementType.equals(ElementType.BUBBLE.getValue())) {
			return getMarginTop();
  	}
  	return (int) (defaultMargin * 50f/30f);
  }

	@Override
  public boolean forceAutoResize() {
  	if (ShapeProperty.isTextResizeDimVerticalResize(parent.getDiagramItem().getShapeProperties())) {
			return true;
		}
		return false;
  }
  
	@Override
	public GraphicsEventHandler getGraphicsMouseHandler() {
    return parent.getDiagram();
  };
  
	@Override
	public Color getTextColor() {
		return parent.getDiagram().getTextColor();
	}

	@Override
	public int getMarginLeft() {
		if (marginLeft > 0) {
			return marginLeft;
		}
  	if (ElementType.BUBBLE_R.getValue().equals(shape.getElementType()) || 
  			ElementType.BUBBLE.getValue().equals(shape.getElementType())) {
  		return 20;
  	}
		return (int) (parent.getWidth() * marginLeftFactor);
	}
	protected void setMarginLeft(int marginLeft) {
		this.marginLeft = marginLeft;
	}
	protected void setMarginTop(int marginTop) {
		this.marginTop = marginTop;
	}

	@Override
	public int getMarginTop() {
		if (marginTop > 0) {
			return marginTop;
		}

		if (elementType.equals(ElementType.COMPONENT.getValue())) {
			return 0;
		} else if (elementType.equals(ElementType.STORAGE.getValue())) {
			return 9;
		}

		return (int) (parent.getHeight() * marginTopFactor);
	}

	@Override
	public int getMarginBottom() {
		if (marginBottom > 0) {
			return marginBottom;
		}

		if (elementType.equals(ElementType.STORAGE.getValue())) {
			return 15;
		} else if (elementType.equals(ElementType.BUBBLE) ||
							 elementType.equals(ElementType.BUBBLE_R)) {
			return (int) (parent.getHeight() * marginBottomFactor);
		}
		return 0;
	}
	protected void setMarginBottom(int marginBottom) {
		this.marginBottom = marginBottom;
	}

};
