package net.sevenscales.editor.diagram;

import net.sevenscales.editor.gfx.domain.MatrixPointJS;
import net.sevenscales.editor.gfx.domain.OrgEvent;

public interface MouseDiagramMoveHandler {
	void onMouseMove(OrgEvent event, Diagram sender, MatrixPointJS point);
}
