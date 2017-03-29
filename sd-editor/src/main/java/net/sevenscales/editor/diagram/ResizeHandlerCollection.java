package net.sevenscales.editor.diagram;

import java.util.ArrayList;

import net.sevenscales.editor.gfx.domain.Point;

public class ResizeHandlerCollection extends ArrayList<DiagramResizeHandler> {

	public void fireResizeStart(Diagram sender) {
		for (int i = 0; i < size(); ++i) {
			get(i).resizeStart(sender);
		}
		
	}

	public void fireOnResize(Diagram sender, Point diff) {
		for (int i = 0; i < size(); ++i) {
			get(i).onResize(sender, diff);
		}
	}

	public void fireResizeEnd(Diagram sender) {
		for (int i = 0; i < size(); ++i) {
			get(i).resizeEnd(sender);
		}
	}
}
