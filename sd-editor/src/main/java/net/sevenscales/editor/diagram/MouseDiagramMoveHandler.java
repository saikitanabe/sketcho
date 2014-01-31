package net.sevenscales.editor.diagram;

import net.sevenscales.editor.gfx.domain.MatrixPointJS;

public interface MouseDiagramMoveHandler {
	void onMouseMove(Diagram sender, MatrixPointJS point);
}
