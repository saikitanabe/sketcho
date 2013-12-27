package net.sevenscales.editor.diagram.drag;

import net.sevenscales.editor.uicomponents.uml.Relationship2;
import net.sevenscales.editor.diagram.drag.AnchorElement;


public interface AnchorMoveHandler {
  void moving(AnchorElement anchorElement, int dx, int dy, long dispachSequence);
  // Relationship2 connection();
}
