package net.sevenscales.editor.api.ot;

import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

import com.google.gwt.core.client.JsArray;

import net.sevenscales.domain.js.JsTimestamp;
import net.sevenscales.domain.CommentDTO;
import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.domain.ElementType;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.editor.api.ot.ApplyHelpers;
import net.sevenscales.domain.utils.SLogger;


public class OTBuffer {
	private static final SLogger logger = SLogger.createLogger(OTBuffer.class);
  private static final int MAX_COUNT = 10;
  		
	private LinkedList<List<CompensationModel>> undoBuffer;
	private LinkedList<List<CompensationModel>> redoBuffer;

	public OTBuffer() {
		undoBuffer = new LinkedList<List<CompensationModel>>();
		redoBuffer = new LinkedList<List<CompensationModel>>();
	}

	public void pushToUndoBufferAndResetRedo(CompensationModel compensationModel) {
		List<CompensationModel> models = new ArrayList<CompensationModel>();
		models.add(compensationModel);
		pushToUndoBufferAndResetRedo(models);
		state();
	}

	public void pushToUndoBufferAndResetRedo(List<CompensationModel> compensationModel) {
		alterCompensationModel(compensationModel);
		undoBuffer.addFirst(compensationModel);
		if (undoBuffer.size() > MAX_COUNT) {
      logger.debug2("pushToUndoBufferAndResetRedo: remove from bottom");
      undoBuffer.removeLast();
		}
		
		redoBuffer.clear();

		state();
		
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
	private void alterCompensationModel(List<CompensationModel> compensationModels) {
		for (CompensationModel compensationModel : compensationModels) {
			clearAwsExpiredUrls(compensationModel.undoJson);
			clearAwsExpiredUrls(compensationModel.redoJson);
		}
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

	public void updateTimestamps(JsArray<JsTimestamp> timestamps)	{
		updateBufferComments(undoBuffer, timestamps);
		updateBufferComments(redoBuffer, timestamps);
	}

	private void updateCommentInsertOperationTimeStamps(OTOperation operation, List<IDiagramItemRO> updates) {
		if (OTOperation.INSERT.equals(operation)) {
			updateBufferComments(undoBuffer, updates);
			updateBufferComments(redoBuffer, updates);
		}
	}

	private void updateBufferComments(LinkedList<List<CompensationModel>> buffer, List<IDiagramItemRO> updates) {
		for (List<CompensationModel> cms : buffer) {
			for (CompensationModel cm : cms) {
				updateCommentItems(cm.undoOperation, cm.undoJson, updates);
				updateCommentItems(cm.redoOperation, cm.redoJson, updates);
			}
		}
	}

	private void updateBufferComments(LinkedList<List<CompensationModel>> buffer, JsArray<JsTimestamp> timestamps) {
		for (List<CompensationModel> cms : buffer) {
			for (CompensationModel cm : cms) {
				updateTimestamps(cm.undoJson, timestamps);
				updateTimestamps(cm.redoJson, timestamps);
			}
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

	private void updateTimestamps(List<IDiagramItemRO> items, JsArray<JsTimestamp> timestamps) {
		for (IDiagramItemRO diro : items) {
			for (int i = 0; i < timestamps.length(); ++i) {
				JsTimestamp timestamp = timestamps.get(i);

				if (diro.getClientId().equals(timestamp.getClientId())) {
					if (diro instanceof CommentDTO) {
						CommentDTO old = (CommentDTO) diro;
						old.setCreatedAt((long)timestamp.getCreatedAt());
						old.setUpdatedAt((long)timestamp.getUpdatedAt());
					}
				}
			}
		}
	}

	public boolean isEmpty() {
		return undoBuffer.isEmpty();
	}

	public List<CompensationModel> topModel() {
		return undoBuffer.getFirst();
	}

	public List<CompensationModel> undoBuffer() {
     List<CompensationModel> result = popAndPush(undoBuffer, redoBuffer);

     state();
     return result;
	}

	private void state() {
		if (undoBuffer.size() <= 0) {
			undoStreamState(false);
		} else {
			undoStreamState(true);
		}

		if (redoBuffer.size() <= 0) {
			redoStreamState(false);
		} else {
			redoStreamState(true);
		}
	}

	private native void undoStreamState(boolean state)/*-{
		$wnd.globalStreams.undoStackStream.push(state)
	}-*/;
	private native void redoStreamState(boolean state)/*-{
		$wnd.globalStreams.redoStackStream.push(state)
	}-*/;

  public List<CompensationModel> redoBuffer() {
    List<CompensationModel> result = popAndPush(redoBuffer, undoBuffer);
    state();
    return result;
  }

  public List<CompensationModel> popAndPush(LinkedList<List<CompensationModel>> topop, LinkedList<List<CompensationModel>> topush) {
  	List<CompensationModel> result = null;
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
