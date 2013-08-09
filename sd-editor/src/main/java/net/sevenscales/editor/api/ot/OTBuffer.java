package net.sevenscales.editor.api.ot;

import java.util.LinkedList;

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
		undoBuffer.addFirst(compensationModel);
		if (undoBuffer.size() > MAX_COUNT) {
      logger.debug2("pushToUndoBufferAndResetRedo: remove from bottom");
      undoBuffer.removeLast();
		}
		
		redoBuffer.clear();
		
    logger.debug2("pushToUndoBufferAndResetRedo: undo size {}, redo size {}", undoBuffer.size(), redoBuffer.size());
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
