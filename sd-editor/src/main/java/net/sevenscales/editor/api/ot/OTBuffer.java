package net.sevenscales.editor.api.ot;

import java.util.LinkedList;
import java.util.List;

import net.sevenscales.domain.CommentDTO;
import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.domain.ElementType;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.editor.api.ot.ApplyHelpers;
import net.sevenscales.domain.utils.SLogger;


public class OTBuffer {
	private static final SLogger logger = SLogger.createLogger(OTBuffer.class);
  private static final int MAX_COUNT = 10;
  		
	private LinkedList<CompensationModel> undoBuffer;
	private LinkedList<CompensationModel> redoBuffer;

	public OTBuffer() {
		undoBuffer = new LinkedList<CompensationModel>();
		redoBuffer = new LinkedList<CompensationModel>();
	}

	public void pushToUndoBufferAndResetRedo(CompensationModel compensationModel) {
		alterCompensationModel(compensationModel);
		undoBuffer.addFirst(compensationModel);
		if (undoBuffer.size() > MAX_COUNT) {
      logger.debug2("pushToUndoBufferAndResetRedo: remove from bottom");
      undoBuffer.removeLast();
		}
		
		redoBuffer.clear();
		
    logger.debug2("pushToUndoBufferAndResetRedo: undo size {}, redo size {}", undoBuffer.size(), redoBuffer.size());
	}

	public void updateCommentInsertOperationsTimeStamps(List<ApplyHelpers.DiagramApplyOperation> applyOperations) {
		for (ApplyHelpers.DiagramApplyOperation ap : applyOperations) {
			updateCommentInsertOperationTimeStamps(ap.getOperation(), ap.getItems());
		}
	}

	/**
	* Clears aws image url.
	*/
	private void alterCompensationModel(CompensationModel compensationModel) {
		clearAwsExpiredUrls(compensationModel.undoJson);
		clearAwsExpiredUrls(compensationModel.redoJson);
	}

	private void clearAwsExpiredUrls(List<IDiagramItemRO> items) {
		for (IDiagramItemRO item : items) {
			if (item.getType().equals(ElementType.IMAGE.getValue())) {
				if (item.getCustomData() != null && item instanceof DiagramItemDTO) {
					DiagramItemDTO i = (DiagramItemDTO) item;
					String[] awsUrlAndFile = item.getCustomData().split(",");
					if (awsUrlAndFile.length == 2) {
						String url = awsUrlAndFile[0];
						String filename = awsUrlAndFile[1];
						// compensation operations cannot contain expired URLs
						i.setCustomData("*," + filename);
					}
				}
			}
		}
	}

	private void updateCommentInsertOperationTimeStamps(OTOperation operation, List<IDiagramItemRO> updates) {
		if (OTOperation.INSERT.equals(operation)) {
			updateBufferComments(undoBuffer, updates);
			updateBufferComments(redoBuffer, updates);
		}
	}

	private void updateBufferComments(LinkedList<CompensationModel> buffer, List<IDiagramItemRO> updates) {
		for (CompensationModel cm : buffer) {
			updateCommentItems(cm.undoOperation, cm.undoJson, updates);
			updateCommentItems(cm.redoOperation, cm.redoJson, updates);
		}
	}

	private void updateCommentItems(OTOperation bufferoper, List<IDiagramItemRO> items, List<IDiagramItemRO> updates) {
		if (OTOperation.INSERT.equals(bufferoper)) {
			for (IDiagramItemRO diro : items) {
				for (IDiagramItemRO update : updates) {
					if (diro.getClientId().equals(update.getClientId())) {
						if (diro instanceof CommentDTO && update instanceof CommentDTO) {
							CommentDTO old = (CommentDTO) diro;
							CommentDTO up = (CommentDTO) update;
							old.setCreatedAt(up.getCreatedAt());
							old.setUpdatedAt(up.getUpdatedAt());
						}
					}
				}
			}
		}
	}

	public CompensationModel undoBuffer() {
    return popAndPush(undoBuffer, redoBuffer);
	}

  public CompensationModel redoBuffer() {
    return popAndPush(redoBuffer, undoBuffer);
  }

  public CompensationModel popAndPush(LinkedList<CompensationModel> topop, LinkedList<CompensationModel> topush) {
  	CompensationModel result = null;
  	if (topop.size() > 0) {
	    result = topop.getFirst();
	    topop.removeFirst();
	    topush.addFirst(result);
	    
	    logger.debug2("popAndPush: undo size {}, redo size {}", undoBuffer.size(), redoBuffer.size());
  	}

    return result;
  }

	public void clear() {
		logger.debug("OTBuffer cleared");
		undoBuffer.clear();
		redoBuffer.clear();
	}

}
