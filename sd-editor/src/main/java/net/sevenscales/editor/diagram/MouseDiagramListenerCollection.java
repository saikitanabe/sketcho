package net.sevenscales.editor.diagram;

import java.util.HashSet;

import net.sevenscales.editor.gfx.domain.MatrixPointJS;

public class MouseDiagramListenerCollection extends HashSet<MouseDiagramHandler> {

	private static final long serialVersionUID = 1L;

	public void fireMouseDown(Diagram sender, MatrixPointJS point, int keys) {
		for (MouseDiagramHandler l : this) {
			l.onMouseDown(sender, point, keys);
		}
	}

	public void fireMouseUp(Diagram sender, MatrixPointJS point, int keys) {
		for (MouseDiagramHandler l : this) {
			l.onMouseUp(sender, point, keys);
		}
	}

	public void fireMouseMove(Diagram sender, MatrixPointJS point) {
		for (MouseDiagramHandler l : this) {
			l.onMouseMove(sender, point);
		}
	}

	public void fireMouseLeave(Diagram sender, MatrixPointJS point) {
		for (MouseDiagramHandler l : this) {
			l.onMouseLeave(sender, point);
		}
	}

	public void fireMouseEnter(Diagram sender, MatrixPointJS point) {
		for (MouseDiagramHandler l : this) {
			l.onMouseEnter(sender, point);
		}
	}

	public void fireTouchStart(Diagram sender,
			MatrixPointJS point) {
		for (MouseDiagramHandler l : this) {
			l.onTouchStart(sender, point);
		}
	}
	
	public void fireTouchMove(Diagram sender, MatrixPointJS point) {
		for (MouseDiagramHandler l : this) {
			l.onTouchMove(sender, point);
		}
	}

	public void fireTouchEnd(Diagram sender,
			MatrixPointJS point) {
		for (MouseDiagramHandler l : this) {
			l.onTouchEnd(sender, point);
		}
	}

}
