package net.sevenscales.editor.uicomponents.uml;

import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.diagram.shape.GenericShape;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.editor.content.ui.ContextMenuItem;
import net.sevenscales.editor.api.impl.Theme.ElementColorScheme;

public class GenericFreehandElement extends GenericElement {
	public GenericFreehandElement(ISurfaceHandler surface, GenericShape newShape, String text, Color backgroundColor, Color borderColor, Color textColor, boolean editable, IDiagramItemRO item) {
		super(surface, newShape, text, backgroundColor, borderColor, textColor, editable, item);
	}

  @Override
  public void setLineWeight(Integer lineWeight) {
    super.setLineWeight(lineWeight);
    doSetStrokeWidth();
  }

  @Override
  public int supportedMenuItems() {
		return super.supportedMenuItems() | ContextMenuItem.LINE_WEIGHT.getValue();
  }

  @Override
  public boolean usesSchemeDefaultBackgroundColor(
    ElementColorScheme colorScheme
  ) {
    // to fix white backround problem on export
    return false;
  }  

}