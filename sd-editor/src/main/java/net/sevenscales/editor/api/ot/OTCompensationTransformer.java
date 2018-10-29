package net.sevenscales.editor.api.ot;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.CommentDTO;
import net.sevenscales.domain.ElementType;
import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.ot.ApplyHelpers.DiagramApplyOperation;
import net.sevenscales.domain.utils.DiagramItemList;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.logging.client.LogConfiguration;

public class OTCompensationTransformer {
	private static SLogger logger = SLogger.createLogger(OTCompensationTransformer.class);

	static {
		SLogger.addFilter(OTCompensationTransformer.class);
	}

	private List<? extends IDiagramItemRO> currentState;
	private boolean testMode;
	private List<DiagramApplyOperation> applyOperations;
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
	
	public List<CompensationModel> compensateApplyOperations(List<ApplyHelpers.DiagramApplyOperation> applyOperations, List<? extends IDiagramItemRO> currentState) throws MappingNotFoundException {
		this.applyOperations = applyOperations;
		this.currentApplyOperationIndex = -1;
		this.currentState = currentState;
		List<CompensationModel> result = new ArrayList<CompensationModel>();
		for (int i = 0; i < applyOperations.size(); ++i) {
			DiagramApplyOperation ap = applyOperations.get(i);
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
		List<IDiagramItemRO> undoItems = mapNewToCurrent(operation, currentState, items);
		List<IDiagramItemRO> redoItems = BoardDocumentHelpers.copyDiagramItems(items);
		
		handleParentOnDelete(undoItems);
		handleParentOnDelete(redoItems);

		return new CompensationModel(OTOperation.INSERT, undoItems, operation, redoItems);
	}

	private void handleParentOnDelete(List<IDiagramItemRO> items) {
		// if delete is last on comment thread, then needs to include also comment thread
		// delete last comment => 
		// 				undo: insert parent + insert comment
		// 				redo: delete parent + delete comment
		for (IDiagramItemRO item : items) {
			if (item instanceof CommentDTO) {
				CommentDTO comment = (CommentDTO) item;
				List<IDiagramItemRO> children = findChildren(comment.getParentId());
				if (children.size() == 0) {
					handleParentBefore(comment, items);
				}
			}
		}
	}

	private void handleParentOnInsert(List<IDiagramItemRO> undoItems, List<IDiagramItemRO> redoItems) {
		// if insert is first on comment thread, then needs to include also comment thread
		// insert first comment => 
		// 				undo: delete comment + delete parent
		// 				redo: insert parent + insert comment
		if (undoItems.size() == 1 && undoItems.get(0) instanceof CommentDTO) {
			CommentDTO comment = (CommentDTO) undoItems.get(0);
			List<IDiagramItemRO> children = findChildren(comment.getParentId());
			if (children.size() == 0) {
				// first comment on this thread
				handleParent(comment, undoItems);
			}
		}

		if (redoItems.size() == 1 && redoItems.get(0) instanceof CommentDTO) {
			CommentDTO comment = (CommentDTO) redoItems.get(0);
			List<IDiagramItemRO> children = findChildren(comment.getParentId());
			if (children.size() == 0) {
				// first comment on this thread
				handleParentBefore(comment, redoItems);
			}
		}
	}

	private List<IDiagramItemRO> findChildren(String parentClientId) {
		List<IDiagramItemRO> result = new DiagramItemList();
		for (IDiagramItemRO item : currentState) {
			CommentDTO child = cast(item);
			if (child != null && parentClientId.equals(child.getParentId())) {
				result.add(item);
			}
		}
		return result;
	}

	private CommentDTO cast(IDiagramItemRO item) {
		if (item instanceof CommentDTO) {
			return (CommentDTO) item;
		}
		return null;
	}

	private boolean undoForInsertExists(List<? extends IDiagramItemRO> items) {
		if (items.size() == 1 && items.get(0).getType().equals(ElementType.COMMENT_THREAD.getValue())) {
			// comment thread single insert undo does not exists since first comment
			// undo includes parent element insert
			return false;
		}
		return true;
	}

	private CompensationModel compensateInsertOperation(OTOperation operation, List<? extends IDiagramItemRO> items) {
		if (!undoForInsertExists(items)) {
			return null;
		}
  	checkOperation(operation, OTOperation.INSERT);
		List<IDiagramItemRO> undoItems = mapToDeleteItems(items);

		List<IDiagramItemRO> redoItems = BoardDocumentHelpers.copyDiagramItems(items);
		handleParentOnInsert(undoItems, redoItems);

    return new CompensationModel(OTOperation.DELETE, undoItems, operation, redoItems);
	}

	private CompensationModel compensateModifyOperation(OTOperation operation, List<? extends IDiagramItemRO> items) throws MappingNotFoundException {
  	checkOperation(operation, OTOperation.MODIFY);
  	
		List<IDiagramItemRO> mapped = mapNewToCurrent(operation, currentState, items);
//		String undoJson = JsonHelpers.json(mapped);
//		String redoJson = JsonHelpers.json(items);
		return new CompensationModel(OTOperation.MODIFY, mapped, operation, BoardDocumentHelpers.copyDiagramItems(items));
	}
	
	private IDiagramItemRO findPreviousItemStateFromAppliedOperations(IDiagramItemRO tofind, List<DiagramApplyOperation> applyOperations, int currentApplyIndex) {
		// look from the current position to earlier applied operations
		for (int i = currentApplyIndex - 1; i >= 0; --i) {
			DiagramApplyOperation ap = applyOperations.get(i);
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
  	List<IDiagramItemRO> result = new DiagramItemList();
  	for (IDiagramItemRO n : newItems) {
  		if (n instanceof CommentDTO) {
  			CommentDTO c = (CommentDTO) n;
  			// for comments parent needs to be found as well and handled
  			result.add(new CommentDTO(c.getClientId(), c.getParentId()));
  		} else {
	  		// it is fine to use simple DiagramItemDTO, since delete is just about client id
	  		result.add(new DiagramItemDTO(n.getClientId()));
  		}
  	}
		return result;
	}

	private IDiagramItemRO findItem(String clientId) {
		for (IDiagramItemRO current : currentState) {
			if (clientId.equals(current.getClientId())) {
				return current;
			}
		}
		return null;
	}

	private void handleParent(CommentDTO child, List<IDiagramItemRO> result) {
		IDiagramItemRO parent = findItem(child.getParentId());
		if (parent != null) {
			result.add(parent.copy());
		}
	}

	private void handleParentBefore(CommentDTO child, List<IDiagramItemRO> result) {
		IDiagramItemRO parent = findItem(child.getParentId());
		if (parent != null) {
			result.add(0, parent.copy());
		}
	}

	private String formatListToString(List<? extends IDiagramItemRO> items) {
		String result = "";
		boolean first = true;
		for (IDiagramItemRO item : items) {
			if (!first) {
				result += ",\n";
			}
			result += item.toString();
			first = false;
		}
		return result;
	}

	private List<IDiagramItemRO> mapNewToCurrent(OTOperation operation, List<? extends IDiagramItemRO> currentState, List<? extends IDiagramItemRO> newItems) throws MappingNotFoundException {
  	// ignore in filter condition items that have exactly the same content
		// those are not very useful to undo/redo
		
		// if (LogConfiguration.loggingIsEnabled(Level.FINEST)) {
		// 	String ci = formatListToString(currentState);
		// 	String ni = formatListToString(newItems);
		// 	logger.debug("mapNewToCurrent operation {}\ncurrentState:\n{}\n\nnewItems:\n{}", operation, ci, ni);
		// }

  	List<IDiagramItemRO> result = new DiagramItemList();
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
	  			if (LogConfiguration.loggingIsEnabled(Level.FINEST) && "".equals(n.getClientId()) || "".equals(c.getClientId())) {
	  				throw new RuntimeException("Client ID cannot be empty!");
					}
					// ST 29.10.2018: check that client id is defined
					// due to undefined crash in here
					if (n.getClientId() != null &&
							c.getClientId() != null &&
							n.getClientId().equals(c.getClientId())) {
	    			result.add(c.copy());
	    			mappingFound = true;
	    			break;
	    		}
	  		}
    	}
    	
  		if (!mappingFound) {
  			// cannot fail whole apply; so better to fail 
  			String msg = SLogger.format("Operation {} failed, mapping not found. NEW ITEM STATE: {} \n CURR STATE: {}", operation.getValue(), n.toString(), currentState.toString());
				logger.error(msg);
				
				// ST 27.10.2018: Do not throw and modify will become
				// automatically an insert operation if not found.
  			// GWT.debugger();
  			// throw new MappingNotFoundException("mapNewToCurrent failed");
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
