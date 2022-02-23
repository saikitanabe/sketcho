package net.sevenscales.editor.uicomponents.uml;


import java.util.ArrayList;
import java.util.List;

import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.ElementType;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.content.ui.ContextMenuItem;
import net.sevenscales.editor.content.ui.UMLDiagramType;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.shape.ActivityShape;
import net.sevenscales.editor.diagram.shape.HasRectShape;
import net.sevenscales.editor.diagram.shape.Info;
import net.sevenscales.editor.gfx.base.GraphicsEventHandler;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.IGroup;
import net.sevenscales.editor.gfx.domain.IRectangle;
import net.sevenscales.editor.gfx.domain.IShape;
import net.sevenscales.editor.gfx.domain.IShapeFactory;
import net.sevenscales.editor.gfx.domain.Point;
import net.sevenscales.editor.gfx.domain.SupportsRectangleShape;
import net.sevenscales.editor.uicomponents.AbstractDiagramItem;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil.AbstractHasTextElement;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil.HasTextElement;
import net.sevenscales.editor.uicomponents.helpers.ResizeHelpers;

public class ActivityElement extends BaseCorporateElement {
//	private Rectangle rectSurface;
  
	public ActivityElement(
    ISurfaceHandler surface,
    HasRectShape newShape,
    String text, 
    Color backgroundColor,
    Color borderColor,
    Color textColor,
    boolean editable,
    IDiagramItemRO item
  ) {
		super(surface, newShape, text, backgroundColor, borderColor, textColor, editable, item);
		
    // group.setAttribute("cursor", "default");

		// boundary = IShapeFactory.Util.factory(editable).createRectangle(group);

    // sub class customizations
		boundary.setStroke(borderColor);
		boundary.setStrokeWidth(getStrokeWidth());
		boundary.setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, backgroundColor.opacity);
		
    super.constructorDone();
	}

  @Override
  protected String getElementType() {
    return ElementType.ACTIVITY.getValue();
  }

  @Override
	protected void setDimensions(int left, int top, int width, int height) {
    // group.setTransform(left, top);
    boundary.setShape(left, top, width, height, 9);
	}  
	
  // nice way to clearly separate interface methods :)
  @Override
  public UMLDiagramType getDiagramType() {
  	return UMLDiagramType.ACTIVITY;
  }
  
  @Override
	public void setBackgroundColor(Color color) {
  	super.setBackgroundColor(color);
  	boundary.setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, backgroundColor.opacity);
  }
  
  @Override
	public void setHighlightColor(Color color) {
		boundary.setStroke(color);
	}
	
}
