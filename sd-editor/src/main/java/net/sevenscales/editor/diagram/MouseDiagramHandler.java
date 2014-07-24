package net.sevenscales.editor.diagram;

import net.sevenscales.editor.gfx.domain.MatrixPointJS;

public interface MouseDiagramHandler extends MouseDiagramMoveHandler {

	boolean onMouseDown(Diagram sender, MatrixPointJS point, int keys);

	void onMouseUp(Diagram sender, MatrixPointJS point, int keys);

	void onMouseLeave(Diagram sender, MatrixPointJS point);

	void onMouseEnter(Diagram sender, MatrixPointJS point);
	
	void onTouchStart(Diagram sender, MatrixPointJS point);

	void onTouchMove(Diagram sender, MatrixPointJS point);

	void onTouchEnd(Diagram sender, MatrixPointJS point);

}
