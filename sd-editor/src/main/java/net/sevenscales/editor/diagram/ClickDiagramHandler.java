package net.sevenscales.editor.diagram;

import net.sevenscales.editor.gfx.domain.MatrixPointJS;

public interface ClickDiagramHandler {
	public void onClick(Diagram sender, int x, int y, int keys);	
	void onDoubleClick(Diagram sender, MatrixPointJS point);
}
