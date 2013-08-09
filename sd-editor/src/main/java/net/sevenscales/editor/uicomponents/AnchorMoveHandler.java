package net.sevenscales.editor.uicomponents;

import net.sevenscales.editor.uicomponents.uml.Relationship2;


public interface AnchorMoveHandler {
  void moving(AnchorElement anchorElement, int dx, int dy, long dispachSequence);
  Relationship2 connection();
}
