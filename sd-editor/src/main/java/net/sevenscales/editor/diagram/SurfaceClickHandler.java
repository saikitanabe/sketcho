package net.sevenscales.editor.diagram;

import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.event.BoardEmptyAreaClickedEvent;
import net.sevenscales.editor.gfx.domain.MatrixPointJS;

public class SurfaceClickHandler implements MouseDiagramHandler, ClickDiagramHandler {
	private Diagram currentDiagram;
	private ISurfaceHandler surface;
	
	public SurfaceClickHandler(ISurfaceHandler surface) {
		this.surface = surface;
	}

	@Override
	public boolean onMouseDown(Diagram sender, MatrixPointJS point, int keys) {
		if (currentDiagram == null) {
			// first there will be a diagram event; and after that a surface event
			// do not let surface event override current diagram
			this.currentDiagram = sender;
		}
		return false;
	}

	@Override
	public void onMouseUp(Diagram sender, final MatrixPointJS point, int keys) {
//		System.out.println("sender: " + sender + " " + currentDiagram);
//		if (sender == null && currentDiagram == null && surface.getEditorContext().isEditable()) {
//			System.out.println("Click");
//			surface.getEditorContext().getEventBus().fireEvent(new SurfaceMouseUpNoHandlingYetEvent(point.getScreenX(), point.getScreenY()));
//		}
		
		// sender is null when background is clicked
		// this has to be last if background click is wanted event
		currentDiagram = sender;
	}

	@Override
	public void onMouseMove(Diagram sender, MatrixPointJS point) {
	}

	@Override
	public void onMouseLeave(Diagram sender, MatrixPointJS point) {
	}

	@Override
	public void onMouseEnter(Diagram sender, MatrixPointJS point) {
	}
	
	@Override
	public void onTouchStart(Diagram sender, MatrixPointJS point) {
	}
  @Override
  public void onTouchMove(Diagram sender, MatrixPointJS point) {
  }
  @Override
  public void onTouchEnd(Diagram sender, MatrixPointJS point) {
  }

//	@Override
//	public void onDoubleClick(DoubleClickEvent event) {
//		if (currentDiagram == null) {
//			surface.getEditorContext().getEventBus().fireEvent(new SurfaceMouseUpNoHandlingYetEvent(event.getClientX(), event.getClientY()));
//		}
//		currentDiagram = null;
//	}

	@Override
	public void onClick(Diagram sender, int x, int y, int keys) {
		// TODO Auto-generated method stub
//		if (sender == null && currentDiagram == null && surface.getEditorContext().isEditable()) {
//			surface.getEditorContext().getEventBus().fireEvent(new SurfaceMouseUpNoHandlingYetEvent(point.getScreenX(), point.getScreenY()));
//		}
	}

	@Override
	public void onDoubleClick(Diagram sender, MatrixPointJS point) {
		if (sender == null && currentDiagram == null && surface.getEditorContext().isEditable()) {
			surface.getEditorContext().getEventBus().fireEvent(new BoardEmptyAreaClickedEvent(point.getScreenX(), point.getScreenY()));
		}
		currentDiagram = null;
	}

}
