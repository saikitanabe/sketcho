package net.sevenscales.editor.uicomponents.uml;

import net.sevenscales.domain.ElementType;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.LibraryShapes;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.shape.GenericShape;
import net.sevenscales.editor.gfx.domain.Color;


public class VerticalPartitionElementCorporate extends VerticalPartitionElement {
  private static final int OFF = 0;

  public VerticalPartitionElementCorporate(ISurfaceHandler surface, GenericShape newShape, String text, 
  		Color backgroundColor, Color borderColor, Color textColor, boolean editable, IDiagramItemRO item) {
    super(surface, newShape, text, backgroundColor, borderColor, textColor, editable, false, item);
  }
  
  public VerticalPartitionElementCorporate(
    ISurfaceHandler surface,
    GenericShape newShape,
    String text, 
    Color backgroundColor,
    Color borderColor,
    Color textColor,
    boolean editable,
    boolean delayText,
    IDiagramItemRO item) {
    super(surface, newShape, text, backgroundColor, borderColor, textColor, editable, delayText, item);
  }

  @Override
  protected int off() {
    return OFF;
  }

  protected Diagram createDiagram(ISurfaceHandler surface, GenericShape newShape,
      String text, boolean editable) {
    return new VerticalPartitionElementCorporate(surface, newShape, text, new Color(backgroundColor), new Color(borderColor), new Color(textColor), editable, LibraryShapes.createByType(ElementType.VERTICAL_PARTITION.getValue()));
  }

}
