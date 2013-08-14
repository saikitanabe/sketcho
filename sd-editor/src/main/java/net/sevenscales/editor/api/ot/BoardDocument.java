package net.sevenscales.editor.api.ot;

import java.util.List;

import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.api.IDiagramItem;
import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.ot.BoardDocumentHelpers.DiagramApplyOperation;
import net.sevenscales.editor.content.ClientIdHelpers.UniqueChecker;

public class BoardDocument implements UniqueChecker {
	private static final SLogger logger = SLogger.createLogger(BoardDocument.class);
	private List<IDiagramItemRO> document;
	private IDiagramItem searchHelper;
	private String logicalName;
	
	public BoardDocument(String logicalName) {
		this.logicalName = logicalName;
		searchHelper = new DiagramItemDTO();
	}

	public BoardDocument(String boardJson, String logicalName) {
		this(logicalName);
		document = BoardDocumentHelpers.fromJson(boardJson);
	}
	
	public BoardDocument(List<? extends IDiagramItemRO> clientDoc, String logicalName) {
		this(logicalName);
		reset(clientDoc);
	}
	
	public void apply(List<DiagramApplyOperation> operations) {
		logger.debug("BoardDocument.apply...");
		for (DiagramApplyOperation op : operations) {
			apply(op.getOperation(), op.getItems());
		}
		logger.debug("BoardDocument.apply... done");
	}

	/**
	 * Applies single operation to the document. Always copies items not using as reference,
	 * except on delete where item is removed from the document.
	 */
	public void apply(OTOperation operation, List<? extends IDiagramItemRO> items) {
		// logger.debug("Document BEFORE apply({}), operation({}), items({}), DOCUMENT({})", logicalName, operation.toString(), items, document);
		merge(operation, items, document);
		// logger.debug("Document AFTER apply({}) DOCUMENT: {}", logicalName, document);
	}
	
	public int size() {
		return document.size();
	}
	
	/**
	 * Returning read only IDiagramItem.
	 * 
	 * NOTE: Could be returning a snapshot copy of the list..., but this is more efficient.
	 * But client need to know it and use it before client doc is changed!
	 * @return
	 */
	public List<? extends IDiagramItemRO> getDocument() {
		return document;
	}
	
	public void clear() {
		document.clear();
	}
	
	private void merge(OTOperation operation, List<? extends IDiagramItemRO> from, List<IDiagramItemRO> to) {
		switch (operation) {
		case MODIFY:
		case UNDO_MODIFY:
		case REDO_MODIFY:
			modify(from, to);
			break;
		case INSERT:
		case UNDO_INSERT:
		case REDO_INSERT:
			insert(from, to);
			break;
		case DELETE:
		case UNDO_DELETE:
		case REDO_DELETE:
			delete(from, to);
			break;
		}
	}
	
	public IDiagramItemRO findItem(String clientId) {
	  if (clientId == null || "".equals(clientId)) {
	    return null;
	  }
	  int index = findIndex(clientId);
	  if (index >= 0) {
	    return document.get(index);
	  }
	  return null;
	}
	
	private int findIndex(String clientId) {
    searchHelper.setClientId(clientId);
    return BoardDocumentHelpers.binarySearch(document, searchHelper);
	}
	
	private void modify(List<? extends IDiagramItemRO> from, List<IDiagramItemRO> to) {
		for (IDiagramItemRO di : from) {
			modifyInOrder(di, to);
		}
	}
	
	private void modifyInOrder(IDiagramItemRO di, List<IDiagramItemRO> to) {
		int index = BoardDocumentHelpers.binarySearch(to, di);
		if (index >= 0) {
			to.get(index).copyFrom(di);
		}
	}
	
	private void insert(List<? extends IDiagramItemRO> from, List<IDiagramItemRO> to) {
		for (IDiagramItemRO di : from) {
			BoardDocumentHelpers.insertInOrder(new DiagramItemDTO(di), to);
		}
	}
	
	private void delete(List<? extends IDiagramItemRO> from, List<IDiagramItemRO> to) {
		for (IDiagramItemRO di : from) {
			deleteInOrder(di, to);
		}
	}

	private void deleteInOrder(IDiagramItemRO di, List<IDiagramItemRO> to) {
		int index = BoardDocumentHelpers.binarySearch(to, di);
		if (index >= 0) {
			to.remove(index);
		}
	}

	public void reset(List<? extends IDiagramItemRO> diagramItems) {
		this.document = BoardDocumentHelpers.copyDiagramItems(diagramItems);
	}

	@Override
	public boolean isUnique(String clientId) {
		int index = findIndex(clientId);
		// index is negative if clientId is not found
		return index < 0;
	}
	
	public String getLogicalName() {
		return logicalName;
	}

}
