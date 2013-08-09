package net.sevenscales.editor.api.ot;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.ot.BoardDocumentHelpers.ApplyOperation;

import com.google.gwt.logging.client.LogConfiguration;

public class OTCompensationTransformer {
	private static SLogger logger = SLogger.createLogger(OTCompensationTransformer.class);
	private List<? extends IDiagramItemRO> currentState;
	private boolean testMode;
	private List<ApplyOperation> applyOperations;
	private int currentApplyOperationIndex;
	
	public OTCompensationTransformer() {
	}
	
	public boolean isTestMode() {
		return testMode || LogConfiguration.loggingIsEnabled(Level.FINER);
	}
	
	public void setTestMode(boolean testMode) {
		this.testMode = testMode;
	}
	
	public CompensationModel compensate(OTOperation operation, List<? extends IDiagramItemRO> currentState, List<? extends IDiagramItemRO> newItems) throws MappingNotFoundException {
		this.currentState = currentState;
		// logger.debug("compensate operation {} newState {} currentState {}", operation, newItems, currentState);
		logger.debug("compensate operation {}", operation);
		return compensateOperation(operation, newItems);
	}
	
	public List<CompensationModel> compensateApplyOperations(List<ApplyOperation> applyOperations, List<? extends IDiagramItemRO> currentState) throws MappingNotFoundException {
		this.applyOperations = applyOperations;
		this.currentApplyOperationIndex = -1;
		this.currentState = currentState;
		List<CompensationModel> result = new ArrayList<CompensationModel>();
		for (int i = 0; i < applyOperations.size(); ++i) {
			ApplyOperation ap = applyOperations.get(i);
			this.currentApplyOperationIndex = i;
			result.add(compensateOperation(ap.getOperation(), ap.getItems()));
		}
		this.applyOperations = null;
		this.currentApplyOperationIndex = -1;
		return result;
	}
	
	private CompensationModel compensateOperation(OTOperation operation, List<? extends IDiagramItemRO> items) throws MappingNotFoundException {
		CompensationModel result = null;
		switch (operation) {
		case MODIFY:
			result = compensateModifyOperation(operation, items);
			break;
		case INSERT:
			result = compensateInsertOperation(operation, items);
			break;
		case DELETE:
			result = compensateDeleteOperation(operation, items);
			break;
		}
		logger.debug("COMPENSATED VALUES: {}", result);
		return result;
	}
	
  private CompensationModel compensateDeleteOperation(OTOperation operation, List<? extends IDiagramItemRO> items) throws MappingNotFoundException {
  	checkOperation(operation, OTOperation.DELETE);
		List<IDiagramItemRO> toInsert = mapNewToCurrent(operation, currentState, items);
//		String undoJson = JsonHelpers.json(toInsert);
//		String redoJson = JsonHelpers.json(items);
		return new CompensationModel(OTOperation.INSERT, toInsert, operation, BoardDocumentHelpers.copyDiagramItems(items));
	}

	private CompensationModel compensateInsertOperation(OTOperation operation, List<? extends IDiagramItemRO> items) {
  	checkOperation(operation, OTOperation.INSERT);
		List<IDiagramItemRO> justToDelete = mapToDeleteItems(items);
//		String undoJson = JsonHelpers.json(justToDelete);
//		String redoJson = JsonHelpers.json(items);
    return new CompensationModel(OTOperation.DELETE, justToDelete, operation, BoardDocumentHelpers.copyDiagramItems(items));
	}

	private CompensationModel compensateModifyOperation(OTOperation operation, List<? extends IDiagramItemRO> items) throws MappingNotFoundException {
  	checkOperation(operation, OTOperation.MODIFY);
  	
		List<IDiagramItemRO> mapped = mapNewToCurrent(operation, currentState, items);
//		String undoJson = JsonHelpers.json(mapped);
//		String redoJson = JsonHelpers.json(items);
		return new CompensationModel(OTOperation.MODIFY, mapped, operation, BoardDocumentHelpers.copyDiagramItems(items));
	}
	
	private IDiagramItemRO findPreviousItemStateFromAppliedOperations(IDiagramItemRO tofind, List<ApplyOperation> applyOperations, int currentApplyIndex) {
		// look from the current position to earlier applied operations
		for (int i = currentApplyIndex - 1; i >= 0; --i) {
			ApplyOperation ap = applyOperations.get(i);
			for (IDiagramItemRO previousStateCandidate : ap.getItems()) {
				if (tofind.getClientId().equals(previousStateCandidate.getClientId())) {
					return previousStateCandidate;
				}
			}
		}
		return null;
	}

	private void checkOperation(OTOperation given, OTOperation expected) {
  	if (given != expected) {
  		throw new RuntimeException(SLogger.format("Wrong type of operation, expected {}, given {}", expected.getValue(), given.getValue()));
  	}
	}

	private List<IDiagramItemRO> mapToDeleteItems(List<? extends IDiagramItemRO> newItems) {
  	List<IDiagramItemRO> result = new ArrayList<IDiagramItemRO>();
  	for (IDiagramItemRO n : newItems) {
  		result.add(new DiagramItemDTO(n.getClientId()));
  	}
		return result;
	}

	private List<IDiagramItemRO> mapNewToCurrent(OTOperation operation, List<? extends IDiagramItemRO> currentState, List<? extends IDiagramItemRO> newItems) throws MappingNotFoundException {
  	// ignore in filter condition items that have exactly the same content
		// those are not very useful to undo/redo
		
  	List<IDiagramItemRO> result = new ArrayList<IDiagramItemRO>();
  	for (IDiagramItemRO n : newItems) {
  		boolean mappingFound = false;
  		
    	if (applyOperations != null) {
    		IDiagramItemRO appliedMatch = findPreviousItemStateFromAppliedOperations(n, applyOperations, currentApplyOperationIndex);
    		if (appliedMatch != null) {
    			result.add(appliedMatch.copy());
    			mappingFound = true;
    		}
    	}
    	
    	if (!mappingFound) {
	  		for (IDiagramItemRO c : currentState) {
	  			if ("".equals(n.getClientId()) || "".equals(c.getClientId())) {
	  				throw new RuntimeException("Client ID cannot be empty!");
	  			}
	    		if (n.getClientId().equals(c.getClientId())) {
	    			result.add(c.copy());
	    			mappingFound = true;
	    			break;
	    		}
	  		}
    	}
    	
  		if (!mappingFound) {
  			String msg = SLogger.format("Operation {} failed, mapping not found. NEW ITEM STATE: {} \n CURR STATE: {}", operation.getValue(), n.toString(), currentState.toString());
  			logger.error(msg);
  			throw new MappingNotFoundException("mapNewToCurrent failed");
//  			if (LogConfiguration.loggingIsEnabled(Level.FINEST)) {
//  			  throw new RuntimeException(msg);
//  			}
  		}
  	}
  	
//  	// TODO potential prev state implementation
//  	if (!operationQueue.isEmpty()) {
//    	for (IDiagramItemRO n : newItems) {
//    		DiagramItemJS dijs = operationQueue.findPrevItemState(operation, n);
//    		logger.debug("dijs {}", dijs);
//    		if (dijs != null) {
//    			result.add(dijs.asDTO());
//    		}
//    	}
//  	}
  	return result;
  }

}
