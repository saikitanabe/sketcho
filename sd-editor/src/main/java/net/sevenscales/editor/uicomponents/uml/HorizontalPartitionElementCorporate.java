package net.sevenscales.editor.uicomponents.uml;

import net.sevenscales.domain.ElementType;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.LibraryShapes;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.shape.HorizontalPartitionShape;
import net.sevenscales.editor.gfx.domain.Color;


public class HorizontalPartitionElementCorporate extends HorizontalPartitionElement4 {
  private static final int OFF = 0;

  public HorizontalPartitionElementCorporate(
    ISurfaceHandler surface,
    HorizontalPartitionShape newShape,
    String text, 
    Color backgroundColor,
    Color borderColor,
    Color textColor,
    boolean editable,
    IDiagramItemRO item) {
    super(surface, newShape, text, backgroundColor, borderColor, textColor, editable, false, item);
  }

  public HorizontalPartitionElementCorporate(
    ISurfaceHandler surface,
    HorizontalPartitionShape newShape,
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

  protected Diagram createDiagram(ISurfaceHandler surface, HorizontalPartitionShape newShape,
      String text, boolean editable) {
    return new HorizontalPartitionElementCorporate(surface, newShape, text, new Color(backgroundColor), new Color(borderColor), new Color(textColor), editable, LibraryShapes.createByType(ElementType.HORIZONTAL_PARTITION.getValue()));
  }

}
