
package net.sevenscales.editor.api.ot;

import java.util.logging.Level;

import net.sevenscales.domain.utils.JsonFormat;
import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.content.utils.JsonHelpers;
import net.sevenscales.editor.content.utils.JsonHelpers.MisMatchException;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.event.OperationQueueRequestEvent;
import net.sevenscales.editor.api.event.OperationQueueRequestEventHandler;

import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.Timer;

public class VerifyHelpers {
	private static final SLogger logger = SLogger
			.createLogger(VerifyHelpers.class);
	private static final int TIME_OUT = 3000;

	private JsonHelpers jsonHelpers;
	private ISurfaceHandler surface;
	private Integer version;
	private double checksum;
	// private VerifyTimer timer;
	private String boardName;
	private VerifyCallback callback;
	private OperationQueue operationQueue;
	private Integer versionVerified;
	private BoardDocument serverDocument;
	private BoardDocument graphicalViewCache;
	// private OperationQueueRequestEvent.QueueRequest queueRequest = OperationQueueRequestEvent.QueueRequest.FREE_SENDING;

	// private class VerifyTimer extends Timer {
	// 	private boolean isScheduled;
	// 	private Integer _version;

	// 	public VerifyTimer(Integer version) {
	// 		this._version = version;
	// 	}

	// 	@Override
	// 	public void cancel() {
	// 		isScheduled = false;
	// 		super.cancel();
	// 	}

	// 	public void scheduleRepeating(int periodMillis) {
	// 		super.scheduleRepeating(periodMillis);
	// 		isScheduled = true;
	// 	}

	// 	@Override
	// 	public void run() {
	// 		if (isScheduled && version == _version && operationQueue.flushedAndAcknowledgedFromServer()) {
	// 			verifyLatest();
				
	// 			if (LogConfiguration.loggingIsEnabled(Level.FINEST)) {
	// 				if (operationQueue.flushedAndAcknowledgedFromServer()) {
	// 					cancel();
	// 				}
	// 			} else {
	// 				// on production graphical view is not checked! It might lead to strange crashes.
	// 				cancel();
	// 			}
	// 		}
	// 	}
	// };

	// private boolean freehandMouseDown() {
	// 	return surface.getEditorContext().isTrue(EditorProperty.FREEHAND_MOUSE_DOWN);
	// }

	public interface VerifyCallback {
		void debugServer(String boardName, String msg, double checksum, String json, double serverChecksum, int serverVersion);
		void forceReload(String reason);
	}
	public VerifyHelpers(String boardName, VerifyCallback callback, JsonHelpers jsonHelpers, ISurfaceHandler surface, 
											 BoardDocument serverDocument, BoardDocument graphicalViewCache) {
		this.boardName = boardName;
		this.callback = callback;
		this.jsonHelpers = jsonHelpers;
		this.surface = surface;
		this.serverDocument = serverDocument;
		this.graphicalViewCache = graphicalViewCache;

  	// surface.getEditorContext().getEventBus().addHandler(OperationQueueRequestEvent.TYPE, new OperationQueueRequestEventHandler() {
  	// 	public void on(OperationQueueRequestEvent event) {
  	// 		queueRequest = event.getQueueRequest();
  	// 		// verifyLatest();
  	// 	}
  	// });

	}

	public void simpleVerify(double checksum, int version) {
		try {
			simpleDocumentVerify(serverDocument, checksum, version);
		} catch (MisMatchException e) {
			String msg = logger.format("checksum check failed for {}: board checksum {}, server checksum {}, json: {}",
						e.getLogicalName(), String.valueOf(e.checksum()), String.valueOf(checksum), e.getJson());
			logger.error(msg);
			callback.debugServer(boardName, SLogger.format("Logical Board Name {}, board ID {}", e.getLogicalName(), boardName), e.checksum(), e.getJson(), checksum, version);
			
			if (LogConfiguration.loggingIsEnabled(Level.FINEST)) {
				// Window.alert(msg);
        callback.forceReload("cli chsum: " + String.valueOf(e.checksum()) + " server chsum: " + String.valueOf(checksum));
			} else {
				// production build
        callback.forceReload("cli chsum: " + String.valueOf(e.checksum()) + " server chsum: " + String.valueOf(checksum));
			}
		}
	}

	private void simpleDocumentVerify(BoardDocument document, double checksum, int version) throws MisMatchException {
		long time = System.currentTimeMillis();
		double boardChecksum = document.calculateChecksum();
		if (boardChecksum != checksum) {
			String json = document.toJson(JsonFormat.SERVER_FORMAT);
			logger.debug("Logical name {}, CLIENT JSON: {}", document.getLogicalName(), json);
			throw new MisMatchException(document.getLogicalName(), boardChecksum, json);
		}

		logger.debug("VERIFIED({}): version {} took {}", document.getLogicalName(), version, System.currentTimeMillis() - time);
	}

	// public void verify(Integer version, double checksum, OperationQueue operationQueue) {
	// 	this.version = version;
	// 	this.checksum = checksum;
	// 	this.operationQueue = operationQueue;
	// 	restartTimer();
	// }

	// private boolean isVerifyBlocked() {
	// 	return OperationQueueRequestEvent.QueueRequest.BLOCK_SENDING.equals(queueRequest);
	// }
	
// 	private void verifyLatest() {
// 		try {
// 			if (!isVerifyBlocked()) {
// 				_verifyLatest();
// 			}
// 		} catch (MisMatchException e) {
// 			String msg = logger.format("checksum check failed for {}: board checksum {}, server checksum {}, json: {}",
// 						e.getLogicalName(), String.valueOf(e.checksum()), String.valueOf(checksum), e.getJson());
// 			logger.error(msg);
// 			callback.debugServer(boardName, SLogger.format("Logical Board Name {}, board ID {}", e.getLogicalName(), boardName), e.checksum(), e.getJson(), checksum, version);
			
// 			if (LogConfiguration.loggingIsEnabled(Level.FINEST)) {
// //				Window.alert(msg);
//         callback.forceReload();
// 			} else {
// 				// production build
// 				callback.forceReload();
// 			}
// 		}
// 	}

// 	private void _verifyLatest() throws MisMatchException {
// 		logger.debug2("VERIFYING {} CHECKSUM {}, operationQueue.flushedAndacknowledgedFromServer()({})...", version, checksum, operationQueue.flushedAndAcknowledgedFromServer());
// 		if (versionVerified != version) {
// 			verifyDocument(serverDocument, JsonFormat.SERVER_FORMAT);
// 			if (operationQueue.flushedAndAcknowledgedFromServer()) {
// 				// NOTE could implement reload from server document if this is not in sync after flush and ack.
// 				// Translate to graphical view not in sync exception that can be passed to real time board to 
// 				// reload the board. Then OT buffer can be kept as is though it is not working perfectly.
// 				// verifyDocument(graphicalViewCache, JsonFormat.SERVER_FORMAT);
				
// 				// NOTE enable when suspecting that graphical rendering is not correct; server can get rendered result as json 
// 				// if verify fails
// //				verifyRenderedGraphics();
// 			}
// 		}
// 		logger.debug2("VERIFYING... done");
// 	}

	// private void verifyDocument(BoardDocument document, JsonFormat jsonFormat)  throws MisMatchException {
	// 	long time = System.currentTimeMillis();
	// 	double boardChecksum = document.calculateChecksum();
	// 	if (boardChecksum != checksum) {
	// 		String json = document.toJson(JsonFormat.SERVER_FORMAT);
	// 		logger.debug("Logical name {}, CLIENT JSON: {}", document.getLogicalName(), json);
	// 		throw new MisMatchException(document.getLogicalName(), boardChecksum, json);
	// 	}

	// 	// jsonHelpers.verify(document.getLogicalName(), document.getDocument(), checksum, jsonFormat);
	// 	versionVerified = version;
	// 	logger.debug("VERIFIED({}): version {} time {}", document.getLogicalName(), versionVerified, System.currentTimeMillis() - time);
	// }

	private void verifyRenderedGraphics()  throws MisMatchException {
		if (LogConfiguration.loggingIsEnabled(Level.FINEST) && operationQueue.flushedAndAcknowledgedFromServer()) {
			// NOTE: this cannot be tested between iPad and Macbook PRO (old) items are rendered slightly differently
			// left pixel is one pixel of. Graphical document can be verified only between same machine and browsers.
			long time = System.currentTimeMillis();
			BoardDocumentGraphicalViewHelpers gv = new BoardDocumentGraphicalViewHelpers(surface);
			gv.takeDocumentSnapshot();
			jsonHelpers.verify("Rendered Document", gv.getDocumentSnapshot(), checksum, JsonFormat.SERVER_FORMAT);
			logger.debug("GRAPHICAL VIEW VERIFIED: version {} time {}", version, System.currentTimeMillis() - time);
		}
	}

	// private void restartTimer() {
	// 	if (timer != null) {
	// 		timer.cancel();
	// 	}
		
	// 	if (!operationQueue.flushedAndAcknowledgedFromServer()) {
	// 		return;
	// 	}
	// 	timer = new VerifyTimer(version);
	// 	timer.scheduleRepeating(TIME_OUT + 5);
	// 	timer.isScheduled = true;
	// }
}
