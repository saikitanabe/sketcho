package net.sevenscales.editor.diagram;

import net.sevenscales.editor.gfx.domain.Point;

public interface DiagramResizeHandler {

	void resizeStart(Diagram sender);

	void onResize(Diagram sender, Point diff);

	void resizeEnd(Diagram sender);

}
