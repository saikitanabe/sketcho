package net.sevenscales.editor.uicomponents.uml;

import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.LibraryShapes;
import net.sevenscales.editor.diagram.shape.GenericShape;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.gfx.domain.Color;
// import net.sevenscales.editor.gfx.domain.IShapeFactory;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.ElementType;
import net.sevenscales.domain.DiagramItemDTO;
// import net.sevenscales.editor.gfx.domain.IPath;
import net.sevenscales.editor.content.ui.ContextMenuItem;


public class GenericNoteElement extends GenericElement {
  // private IPath tape;

	public GenericNoteElement(ISurfaceHandler surface, GenericShape newShape, String text, Color backgroundColor, Color borderColor, Color textColor, boolean editable, IDiagramItemRO item) {
		super(surface, newShape, text, backgroundColor, borderColor, textColor, editable, item);
    // tape = IShapeFactory.Util.factory(editable).createPath(getGroup(), null);
    // tape.setShape("m11.955922,-4.529300 l25.717882,-1.877900 l-0.408657,1.877900 l2.545811,0.414700 l-1.441629,1.907800 l-0.888524,1.444800 l2.444658,0 l-1.944964,2.221800 l1.499488,2.111600 l-27.727585,1.755200 l0.203520,-1.877300 l-1.837339,-0.704100 l2.245186,-1.173000 l-2.605694,-2.723400 l2.276746,-1.000200 l-1.388625,-1.110600 l1.309726,-1.267300  z");

    //   tape.setFill(0xe2, 0x56, 0x56, 0.7);
    //   tape.setStroke(0xe2, 0x56, 0x56, 0.7);
	}

  @Override
  protected Diagram createGenericElement(ISurfaceHandler surface, GenericShape newShape) {
    return new GenericNoteElement(surface, newShape, getText(), new Color(backgroundColor), new Color(borderColor), new Color(textColor), editable, LibraryShapes.createByType(ElementType.getEnum(getDiagramItem().getType())));
  }

  @Override
  protected int getMarginLeft() {
    return 15;
  }

  @Override
  protected int getMarginBottom() {
    return 2;
  }

  @Override
  protected int getMarginTop() {
    return 7;
  }

  @Override
  public int supportedMenuItems() {
    return ContextMenuItem.NO_MENU.getValue() | 
           ContextMenuItem.DUPLICATE.getValue() |
           ContextMenuItem.COLOR_MENU.getValue() |
           ContextMenuItem.URL_LINK.getValue() | 
           ContextMenuItem.LAYERS.getValue() |
           ContextMenuItem.DELETE.getValue();
  }

}