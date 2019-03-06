package net.sevenscales.editor.diagram;

import java.util.HashSet;

import net.sevenscales.editor.gfx.domain.MatrixPointJS;
import net.sevenscales.editor.gfx.domain.OrgEvent;

public class MouseDiagramListenerCollection extends HashSet<MouseDiagramHandler> {

	private static final long serialVersionUID = 1L;

	public void fireMouseDown(OrgEvent event, Diagram sender, MatrixPointJS point, int keys) {
		for (MouseDiagramHandler l : this) {
			l.onMouseDown(event, sender, point, keys);
		}
	}

	public void fireMouseUp(Diagram sender, MatrixPointJS point, int keys) {
		for (MouseDiagramHandler l : this) {
			l.onMouseUp(sender, point, keys);
		}
	}

	public void fireMouseMove(OrgEvent event, Diagram sender, MatrixPointJS point) {
		for (MouseDiagramHandler l : this) {
			l.onMouseMove(event, sender, point);
		}
	}

	public void fireMouseLeave(Diagram sender, MatrixPointJS point) {
		for (MouseDiagramHandler l : this) {
			l.onMouseLeave(sender, point);
		}
	}

	public void fireMouseEnter(OrgEvent event, Diagram sender, MatrixPointJS point) {
		for (MouseDiagramHandler l : this) {
			l.onMouseEnter(event, sender, point);
		}
	}

	public void fireTouchStart(OrgEvent event, Diagram sender,
			MatrixPointJS point) {
		for (MouseDiagramHandler l : this) {
			l.onTouchStart(event, sender, point);
		}
	}
	
	public void fireTouchMove(OrgEvent event, Diagram sender, MatrixPointJS point) {
		for (MouseDiagramHandler l : this) {
			l.onTouchMove(event, sender, point);
		}
	}

	public void fireTouchEnd(Diagram sender,
			MatrixPointJS point) {
		for (MouseDiagramHandler l : this) {
			l.onTouchEnd(sender, point);
		}
	}

}
