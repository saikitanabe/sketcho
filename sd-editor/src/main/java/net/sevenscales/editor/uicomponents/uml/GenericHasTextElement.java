package net.sevenscales.editor.uicomponents.uml;  

import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil.AbstractHasTextElement;
import net.sevenscales.editor.diagram.shape.GenericShape;
import net.sevenscales.domain.ElementType;
import net.sevenscales.domain.ShapeProperty;
import net.sevenscales.editor.gfx.domain.IShape;
import net.sevenscales.editor.gfx.base.GraphicsEventHandler;


class GenericHasTextElement extends AbstractHasTextElement {
	private int marginLeft;
	private GenericElement parent;
	private GenericShape shape;

	GenericHasTextElement(GenericElement parent, GenericShape shape) {
		this.parent = parent;
		this.shape = shape;
		// possible to customize for each element type
		ElementType elementType = ElementType.getEnum(shape.getElementType());
		switch (elementType) {
			case BUBBLE: {
				this.marginLeft = 40;
				break;
			}
		}
	}
  public void addShape(IShape shape) {
    // shapes.add(shape);    
  }
  public int getWidth() {
    return parent.getWidth();
  }
  public int getX() {
    return parent.getRelativeLeft();
  }
  public int getY() {
  	if (ShapeProperty.isTextPositionBottom(shape.getShapeProperties())) {
			return parent.getRelativeLeft() + parent.getHeight() - TextElementFormatUtil.ROW_HEIGHT + 5;
  	} else {
  		return parent.getRelativeLeft();
  	}
  }
  public int getHeight() {
  	return parent.getHeight();
  }
  
  public boolean verticalAlignMiddle() {
  	if (ShapeProperty.isTextPositionBottom(shape.getShapeProperties()) || 
  		  ShapeProperty.isTextResizeDimVerticalResize(shape.getShapeProperties())) {
			return false;
		} else {
			return true;
		}
  }

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

  public String getLink() {
    return parent.getLink();
  }

  public boolean isAutoResize() {
    return parent.isAutoResize();
  }

  public void resize(int x, int y, int width, int height) {
    parent.resize(x, y, width, width);
    parent.fireSizeChanged();
  }

  public void setLink(String link) {
    parent.setLink(link);
  }
  public boolean supportsTitleCenter() {
  	if (ShapeProperty.isTextResizeDimVerticalResize(shape.getShapeProperties())) {
  		return false;
  	}
    return true;
  }
  public int getTextMargin(int defaultMargin) {
  	return (int) (defaultMargin * 50f/30f);
  }
  public boolean forceAutoResize() {
  	if (ShapeProperty.isTextResizeDimVerticalResize(shape.getShapeProperties())) {
			return true;
		}
		return false;
  }
  
  public GraphicsEventHandler getGraphicsMouseHandler() {
    return parent;
  };
  
	@Override
	public String getTextColorAsString() {
		return "#" + parent.getTextColor();
	};

};
