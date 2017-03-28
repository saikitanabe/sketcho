package net.sevenscales.editor.diagram.drag;

public interface AnchorMoveHandler {
  void moving(AnchorElement anchorElement, int dx, int dy, int dispachSequence);
  // Relationship2 connection();
}
