package net.sevenscales.editor.diagram;

import net.sevenscales.editor.gfx.domain.MatrixPointJS;
import net.sevenscales.editor.gfx.domain.OrgEvent;

public interface MouseDiagramHandler extends MouseDiagramMoveHandler {

	boolean onMouseDown(
    OrgEvent event,
    Diagram sender,
    MatrixPointJS point,
    int keys
  );

	void onMouseUp(Diagram sender, MatrixPointJS point, int keys);

	void onMouseLeave(Diagram sender, MatrixPointJS point);

	void onMouseEnter(OrgEvent event, Diagram sender, MatrixPointJS point);
	
	void onTouchStart(OrgEvent event, Diagram sender, MatrixPointJS point);

	void onTouchMove(OrgEvent event, Diagram sender, MatrixPointJS point);

	void onTouchEnd(Diagram sender, MatrixPointJS point);

}
