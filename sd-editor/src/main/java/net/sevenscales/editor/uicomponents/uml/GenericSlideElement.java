package net.sevenscales.editor.uicomponents.uml;

import java.util.List;

// import net.sevenscales.editor.gfx.domain.IShapeFactory;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.api.IDiagramItem;
import net.sevenscales.domain.ElementType;
import net.sevenscales.domain.js.JsSlideData;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.LibraryShapes;
// import net.sevenscales.editor.gfx.domain.IPath;
import net.sevenscales.editor.content.ui.ContextMenuItem;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.shape.GenericShape;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.IGroup;
import net.sevenscales.editor.gfx.domain.Point;


public class GenericSlideElement extends GenericElement {
	public GenericSlideElement(
    ISurfaceHandler surface,
    GenericShape newShape,
    String text,
    Color backgroundColor,
    Color borderColor,
    Color textColor,
    boolean editable,
    IDiagramItemRO item
  ) {
		super(surface, newShape, text, backgroundColor, borderColor, textColor, editable, item);
	}

  @Override
  protected Diagram createGenericElement(ISurfaceHandler surface, GenericShape newShape) {
    return new GenericSlideElement(surface, newShape, getText(), new Color(backgroundColor), new Color(borderColor), new Color(textColor), editable, LibraryShapes.createByType(getDiagramItem().getType()));
  }

  @Override
  protected IGroup getLayer(ISurfaceHandler surface) {
    return surface.getSlideLayer();
  }

  @Override
	public Diagram duplicate(boolean partOfMultiple) {
		return duplicate(surface, partOfMultiple);
	}
	
  @Override
	public Diagram duplicate(ISurfaceHandler surface, boolean partOfMultiple) {

		Point p = getCoords();
		Diagram result = duplicate(surface, p.x + 20, p.y + 20);

    List<Diagram> found = surface.createDiagramSearch().findAllByType(ElementType.SLIDE.getValue());

    IDiagramItem item = result.getDiagramItem();
    item.setData(JsSlideData.newSlideData(found.size()+1));

    return result;
	}

}