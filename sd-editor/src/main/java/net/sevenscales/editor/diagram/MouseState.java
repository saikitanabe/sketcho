package net.sevenscales.editor.diagram;

interface MouseState {
	boolean isResizing();
	boolean isDragging();
	boolean isMovingBackground();
	boolean isLassoing();
}