package net.sevenscales.editor.uicomponents.uml;

import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.LibraryShapes;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.shape.GenericShape;
import net.sevenscales.editor.gfx.domain.Color;


public class PackageElementCorporate extends PackageElement {
  private static final int HEADER_OFF = 0;
  private static final int OFF = 0;

  public PackageElementCorporate(
    ISurfaceHandler surface,
    GenericShape newShape,
    String text, 
    Color backgroundColor,
    Color borderColor,
    Color textColor,
    boolean editable,
    IDiagramItemRO item
  ) {
    this(surface, newShape, text, backgroundColor, borderColor, textColor, editable, false, item);
  }
  
  public PackageElementCorporate(
    ISurfaceHandler surface,
    GenericShape newShape,
    String text, 
    Color backgroundColor,
    Color borderColor,
    Color textColor,
    boolean editable,
    boolean delayText,
    IDiagramItemRO item
  ) {
    super(surface, newShape, text, backgroundColor, borderColor, textColor, editable, item);
  }

  @Override
  protected int off() {
    return OFF;
  }

  @Override
  protected int headerOff() {
    return HEADER_OFF;
  }

  protected Diagram createDiagram(ISurfaceHandler surface, GenericShape newShape,
      String text, boolean editable) {
    DiagramItemDTO item = LibraryShapes.createFrom(getDiagramItem());
    return new PackageElementCorporate(surface, newShape, text, new Color(backgroundColor), new Color(borderColor), new Color(textColor), editable, item);
  }

}
