package net.sevenscales.editor.content.utils;

import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.diagram.shape.Info;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.shape.*;
import net.sevenscales.editor.uicomponents.uml.*;


public interface AbstractDiagramFactory {
	Info parseShape(IDiagramItemRO item, int moveX, int moveY);
	Diagram parseDiagram(ISurfaceHandler surface, Info shape, String text, Color backgroundColor, Color borderColor, Color textColor, boolean editable, IDiagramItemRO item);


	public class EllipseFactory implements AbstractDiagramFactory {
		public Info parseShape(IDiagramItemRO item, int moveX, int moveY) {
      return new EllipseShape(item.getShape().split(","), moveX, moveY);
		}

		public Diagram parseDiagram(ISurfaceHandler surface, Info shape, String text, Color backgroundColor, Color borderColor, Color textColor, boolean editable, IDiagramItemRO item) {
      return new EllipseElement(surface,
              (EllipseShape) shape,
              item.getText(),
              DiagramItemFactory.parseBackgroundColor(item),
              DiagramItemFactory.parseBorderColor(item),
              DiagramItemFactory.parseTextColor(item),
              editable,
              item);
		}
	}
}
