package net.sevenscales.editor.uicomponents.uml;

import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.diagram.shape.*;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.editor.api.Tools;
import net.sevenscales.domain.ElementType;


public class ElementFactory {
	public static Diagram createClassElement(ISurfaceHandler surface, RectShape newShape, String text, Color backgroundColor, Color borderColor, Color textColor, boolean editable, IDiagramItemRO item) {
		// if (Tools.isSketchMode() && Shapes.getSketch(ElementType.CLASS) != null) {
		// 	LibraryShapes.LibraryShape ls = LibraryShapes.get(item.getType());

  //     AbstractDiagramFactory factory = ShapeParser.factory(item);
  //     Info shape = factory.parseShape(item, 0, 0);
  //     shape.setProperties(ls.shapeProperties);
  //     Diagram result = factory.parseDiagram(surface, shape, editable, item, parent);

		// 	// could multiply width and height
		// 	GenericElement element = new GenericElement(surface,
	 //        new GenericShape(ls.elementType.getValue(), x, y, ls.width, ls.height, ls.shapeProperties, null),
	 //        type.getValue(),
	 //        background, borderColor, color, true, DiagramItemDTO.createGenericItem(ls.elementType));
		// 	result = element;

		// } else {
			return new ClassElement2(surface, newShape, text, backgroundColor, borderColor, textColor, editable, item);
		// }
	}
	public static Diagram createClassElement(ISurfaceHandler surface, RectShape newShape, String text, Color backgroundColor, Color borderColor, Color textColor, boolean editable, boolean delayText, IDiagramItemRO item) {
		return new ClassElement2(surface, newShape, text, backgroundColor, borderColor, textColor, editable, delayText, item);
	}
}