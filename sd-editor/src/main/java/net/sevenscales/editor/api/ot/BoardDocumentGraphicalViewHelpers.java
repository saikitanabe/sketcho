package net.sevenscales.editor.api.ot;

import java.util.ArrayList;
import java.util.List;

import net.sevenscales.domain.api.IDiagramItem;
import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.ISurfaceHandler;

public class BoardDocumentGraphicalViewHelpers {
	private static final SLogger logger = SLogger.createLogger(BoardDocumentGraphicalViewHelpers.class);
	
	private ISurfaceHandler surface;
	private List<IDiagramItem> documentSnapshot;

	public BoardDocumentGraphicalViewHelpers(ISurfaceHandler surface) {
		this.surface = surface;
		documentSnapshot = new ArrayList<IDiagramItem>();
	}
	
	public void takeDocumentSnapshot() {
		takeDocumentSnapshot(getGraphicalView());
	}
	public void takeDocumentSnapshot(List<IDiagramItem> diagramItems) {
		documentSnapshot.clear();
		for (IDiagramItem di : diagramItems) {
			documentSnapshot.add(di.copy());
		}
		logger.debug("takeDocumentSnapshot {}", documentSnapshot.size());
	}
	
	public List<IDiagramItem> getDocumentSnapshot() {
		return documentSnapshot;
	}
	
	public List<IDiagramItem> getGraphicalView() {
		return BoardDocumentHelpers.getDiagramsAsDTO(surface.getVisualItems(), true);
	}
	
	public void clear() {
		documentSnapshot.clear();
	}
	
}
