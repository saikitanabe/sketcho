package net.sevenscales.editor.uicomponents.uml;

// import net.sevenscales.editor.gfx.domain.IShapeFactory;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.LibraryShapes;
// import net.sevenscales.editor.gfx.domain.IPath;
import net.sevenscales.editor.content.ui.ContextMenuItem;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.shape.GenericShape;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.IGroup;


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

}