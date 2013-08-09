package net.sevenscales.editor.diagram;



public interface DiagramDragHandler {
	public void dragStart(Diagram sender);
	public void dragEnd(Diagram sender);
	public void onDrag(Diagram sender, int dx, int dy);
	boolean isSelected();
}
