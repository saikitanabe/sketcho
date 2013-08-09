package net.sevenscales.editor.api.ot;

import java.util.List;

import net.sevenscales.domain.IDiagramItemRO;

public class CompensationModel {
	@Override
	public String toString() {
		return "CompensationModel [undoOperation=" + undoOperation + ", undoJson="
				+ undoJson + ", redoOperation=" + redoOperation + ", redoJson="
				+ redoJson + "]";
	}

	OTOperation undoOperation;
	List<IDiagramItemRO> undoJson;
	OTOperation redoOperation;
	List<IDiagramItemRO> redoJson;
	
	public CompensationModel(OTOperation undoOperation,
			List<IDiagramItemRO> undoJson, OTOperation redoOperation,
			List<IDiagramItemRO> redoJson) {
		super();
		this.undoOperation = undoOperation;
		this.undoJson = undoJson;
		this.redoOperation = redoOperation;
		this.redoJson = redoJson;
	}
	
}
