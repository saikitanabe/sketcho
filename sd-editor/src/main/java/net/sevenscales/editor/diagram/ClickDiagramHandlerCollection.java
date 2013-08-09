package net.sevenscales.editor.diagram;

import java.util.ArrayList;

import net.sevenscales.editor.gfx.domain.MatrixPointJS;

public class ClickDiagramHandlerCollection extends ArrayList<ClickDiagramHandler> {
	public void addClickHandler(ClickDiagramHandler listener) {
		add(listener);
	}
	
	public void removeClickHandler(ClickDiagramHandler listener) {
		remove(listener);
	}
	
	public void fireClick(Diagram sender, int x, int y, int keys) {
		for (int i = 0; i < size(); ++i) {
			ClickDiagramHandler l = (ClickDiagramHandler) get(i);
			l.onClick(sender, x, y, keys);
		}
	}
	
	public void fireDoubleClick(Diagram sender, MatrixPointJS point) {
		for (int i = 0; i < size(); ++i) {
			ClickDiagramHandler l = (ClickDiagramHandler) get(i);
			l.onDoubleClick(sender, point);
		}
	}

	public void addClickHandler(Diagram diagram) {
		for (int i = 0; i < size(); ++i) {
			diagram.registerClickHandler((ClickDiagramHandler) get(i));
		}
	}

}
