package net.sevenscales.editor.diagram.drag;

public interface AnchorMoveHandler {
  void moving(AnchorElement anchorElement, int dx, int dy, int dispachSequence);
  void rotate(
  	AnchorElement anchorElement,
  	boolean save,
    Integer oldRotate,
    Integer newRotate,  	
  	net.sevenscales.editor.api.ISurfaceHandler surface
  );
  // Relationship2 connection();
}
