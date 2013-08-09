package net.sevenscales.editor.diagram;

import net.sevenscales.editor.uicomponents.Point;

public interface DiagramResizeHandler {

	void resizeStart(Diagram sender);

	void onResize(Diagram sender, Point diff);

	void resizeEnd(Diagram sender);

}
