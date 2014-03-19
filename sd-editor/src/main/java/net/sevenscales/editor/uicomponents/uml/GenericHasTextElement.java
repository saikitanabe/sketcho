package net.sevenscales.editor.uicomponents.uml;  

import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil.AbstractHasTextElement;
import net.sevenscales.editor.uicomponents.TextElementVerticalFormatUtil;
import net.sevenscales.editor.diagram.shape.GenericShape;
import net.sevenscales.domain.ElementType;
import net.sevenscales.domain.ShapeProperty;
import net.sevenscales.editor.gfx.domain.IShape;
import net.sevenscales.editor.gfx.base.GraphicsEventHandler;


class GenericHasTextElement extends AbstractHasTextElement {
	private float marginLeftFactor;
	private float marginTopFactor;
	private GenericElement parent;
	private GenericShape shape;

	GenericHasTextElement(GenericElement parent, GenericShape shape) {
		this.parent = parent;
		this.shape = shape;
		// possible to customize for each element type
		ElementType elementType = ElementType.getEnum(shape.getElementType());
		switch (elementType) {
			case BUBBLE:
			case BUBBLE_R: {
				this.marginLeftFactor = 0.09f;
				this.marginTopFactor = 0.09f;
				break;
			}
		}
	}

	@Override
  public int getWidth() {
    return parent.getWidth() - getMarginLeft();
  }

	@Override
  public int getX() {
    return parent.getRelativeLeft() + getMarginLeft();
  }

	@Override
  public int getY() {
  	if (ShapeProperty.isTextPositionBottom(shape.getShapeProperties())) {
			return parent.getRelativeTop() + parent.getHeight() - TextElementFormatUtil.ROW_HEIGHT + 5;
  	} else if (ShapeProperty.isTextResizeDimVerticalResize(shape.getShapeProperties())) {
  		return parent.getRelativeTop() + parent.getHeight() / 2 - ((int) parent.getTextHeight() / 2 + TextElementVerticalFormatUtil.DEFAULT_TOP_MARGIN / 2);
  	} else {
  		return parent.getRelativeTop();
  	}
  }

	@Override
  public int getHeight() {
  	return parent.getHeight();
  }
 
	@Override
  public boolean verticalAlignMiddle() {
  	if (ShapeProperty.isTextPositionBottom(shape.getShapeProperties()) || 
  		  ShapeProperty.isTextResizeDimVerticalResize(shape.getShapeProperties())) {
			return false;
		} else {
			return true;
		}
  }

	@Override
  public boolean supportElementResize() {
  	if (ShapeProperty.isTextPositionBottom(shape.getShapeProperties())) {
			return false;
  	} else {
			return true;
  	}
  }

	@Override
	public boolean boldText() {
		return false;
	}

	@Override
  public String getLink() {
    return parent.getLink();
  }

	@Override
  public boolean isAutoResize() {
    return parent.isAutoResize();
  }

	@Override
  public void resize(int x, int y, int width, int height) {
    parent.resize(x, y, width, width);
    parent.fireSizeChanged();
  }

	@Override
  public void setLink(String link) {
    parent.setLink(link);
  }

	@Override
  public boolean supportsTitleCenter() {
  	if (ShapeProperty.isTextResizeDimVerticalResize(shape.getShapeProperties())) {
  		return false;
  	}
    return true;
  }

	@Override
  public int getTextMargin(int defaultMargin) {
  	return (int) (defaultMargin * 50f/30f);
  }

	@Override
  public boolean forceAutoResize() {
  	if (ShapeProperty.isTextResizeDimVerticalResize(shape.getShapeProperties())) {
			return true;
		}
		return false;
  }
  
	@Override
	public GraphicsEventHandler getGraphicsMouseHandler() {
    return parent;
  };
  
	@Override
	public String getTextColorAsString() {
		return "#" + parent.getTextColor();
	}

	@Override
	public int getMarginLeft() {
		return (int) (parent.getWidth() * marginLeftFactor);
	}

	@Override
	public int getMarginTop() {
		return (int) (parent.getHeight() * marginTopFactor);
	}

};
