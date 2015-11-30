package net.sevenscales.editor.diagram;

interface MouseState {
	boolean isResizing();
	boolean isDragging();
	boolean isMovingBackground();

	/**
	* Is shift key down and starting to lassoing.
	*/
	boolean isLassoOn();
	/**
	* Actually lassoing.
	*/
	boolean isLassoing();
}