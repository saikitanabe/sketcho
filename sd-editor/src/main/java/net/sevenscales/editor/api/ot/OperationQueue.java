package net.sevenscales.editor.api.ot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.logging.Level;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.Window;

import net.sevenscales.domain.JSONParserHelpers;
import net.sevenscales.domain.js.JsSendOperation;
import net.sevenscales.domain.utils.Debug;
import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.IEditor;
import net.sevenscales.editor.api.ot.ApplyHelpers.DiagramApplyOperation;
import net.sevenscales.editor.content.ClientIdHelpers;
import net.sevenscales.editor.diagram.GlobalState;
import net.sevenscales.editor.utils.IWebStorageListener;
import net.sevenscales.editor.utils.WebStorage;


public class OperationQueue {
  private static SLogger logger = SLogger.createLogger(OperationQueue.class);

  private static long SEND_RETRY_TIMEOUT = 7000;
  
	private LinkedList<SendOperation> queuedOperations;
  // actually this is the update version == update.size()
  private Set<String> updates = new HashSet<String>();
	private Acknowledged acknowledged;
	private IEditor editor;
  private String boardName;
  private String tabId;
	private String checksum;

  private int baseUpdateVersion;
	private boolean nextOffline = false;
  private boolean transaction = false;

	public OperationQueue(
    Acknowledged acknowledged,
    IEditor editor,
    String boardName,
    String tabId
) {
		this.acknowledged = acknowledged;
		this.editor = editor;
    this.boardName = boardName;
    this.tabId = tabId;
		queuedOperations = new LinkedList<SendOperation>();
	}

  public void beginTransaction() {
    transaction = true;
  }

  public void commitTransaction() {
    transaction = false;
  }

  public boolean isTransaction() {
    return transaction;
  }
	
	public interface Acknowledged {
		boolean acknowledgedFromServerOrShouldRetry();
	}

	public static class SendOperation {
		@Override
		public String toString() {
			return "SendOperation [operation=" + operation + ", operationJson="
					+ operationJson + "]";
		}

		private OTOperation operation;
		private JSONArray operationJson;
		private String guid;
		private long gtime;

		public SendOperation(OTOperation operation, JSONArray operationJson) {
			this(operation, operationJson, null);
		}
		public SendOperation(OTOperation operation, JSONArray operationJson, String guid) {
			this.operation = operation;
			this.operationJson = JSONPack.packOperation(operation, operationJson);
			this.guid = guid;
			// when guid is added
			this.gtime = System.currentTimeMillis();
		}

		public static SendOperation fromJson(JSONObject obj) {
			SendOperation result = null;
			if (obj != null) {
				String operation = JSONParserHelpers.getString(obj.get("operation"));
				// this is json not string, parse accordingly => then to string
				// converted to json, so string parsing can use gwt json parser!
				JSONValue vitems = obj.get("items");
				// String items = "[]";
				// if (vitems != null && vitems.isArray() != null) {
				// 	items = JsonExtraction.escapeForSending(vitems.toString());
				// }
				String guid = null;
				JSONValue jguid = obj.get("guid");
				if (jguid != null && jguid.isString() != null && !"".equals(jguid.isString().stringValue())) {
					guid = jguid.isString().stringValue();
				}
				JSONValue _items = obj.get("items");
				if (_items.isArray() != null) {
					result = new SendOperation(OTOperation.getEnum(operation), _items.isArray(), guid);
				}
			}
			return result;
		}

		public JsSendOperation toJson() {
			// if (guid != null) {
			// 	return "{\"operation\":\"" + operation.toString() + 
			// 					"\",\"items\":" + operationJson + 
			// 					",\"guid\":\"" + guid + "\"}";
			// } else {
			// 	return "{\"operation\":\"" + operation.toString() + 
			// 				 "\",\"items\":" + operationJson + "\"}";
			// }
			// cannot use gwt utilities since already converted from JSONValue
			JSONObject result = new JSONObject();
			result.put("operation", new JSONString(operation.toString()));
			result.put("items", operationJson);
			if (guid != null) {
				// guid is set only when sending, not when storing to local storage
				result.put("guid", new JSONString(guid));
			}
			return result.getJavaScriptObject().cast();
		}

		private void setGuid(String guid) {
			this.guid = guid;
			this.gtime = System.currentTimeMillis();
		}

		private String getGuid() {
			return guid;
		}

		public OTOperation getOperation() {
			return operation;
		}
		
		public JSONArray getOperationJson() {
			return operationJson;
		}

	}

	public void listen(IWebStorageListener listener) {
	 	WebStorage.listen(queueName(boardName), listener);
	}

	public void push(SendOperation item) {
		// need to add in reverse order to have items applied in correct order on server
		if (item.operationJson != null && !contains(item)) {
			queuedOperations.addLast(item);
			storeQueue();
		} else if (!item.operation.equals(OTOperation.USER_MOVE) && LogConfiguration.loggingIsEnabled(Level.FINEST)) {
			// not checkint user move operations
			Window.alert("push failed");
			com.google.gwt.core.shared.GWT.debugger();
		}
	}

	private void storeQueue() {
		boolean filterOutUserOperations = true;
		JsArray<JsSendOperation> joperations = toJson(queuedOperations, filterOutUserOperations);
		if (joperations.length() > 0) {
      JsOperationQueueStorageItem tabOperations = tabQueue(tabId, joperations);

			WebStorage.setJson(queueName(boardName), tabOperations);
		} else {
			// nothing so remove whole queue
			WebStorage.remove(queueName(boardName));
		}

		checkSaveStatus();
  }
  
  private native JsOperationQueueStorageItem tabQueue(
    String tabId,
    JsArray<JsSendOperation> operations
  )/*-{
    return {
      "tab_id": tabId,
      "operations": operations
    }
  }-*/;

	private void checkSaveStatus() {
		GlobalState.notifySaveStatusChanged();
		// if (isEmpty()) {
		// 	GlobalState.notifySaved();
		// } else {
		// 	GlobalState.notifySaving();
		// }
	}

	private String queueName(String boardId) {
		return "q_" + boardId;
	}

	private boolean contains(SendOperation item) {
		for (SendOperation o : queuedOperations) {
			if (o.operation.equals(item.operation) && o.operationJson.equals(item.operationJson)) {
				return true;
			}
		}
		return false;
	}

	public boolean containsInsertOperation() {
		for (SendOperation o : queuedOperations) {
			if (OTOperation.INSERT.equals(o.getOperation())) {
				return true;
			}
		}
		return false;
	}

	public boolean containsOnlyUserMoveOperations() {
		for (SendOperation o : queuedOperations) {
			if (!OTOperation.USER_MOVE.equals(o.getOperation())) {
				return false;
			}
		}
		return true;
	}

	// TODO should this return
	// JsonData {
	// 	json: String
	// 	offline: Boolean
	// }
	// - if offline, then should on send originator + "offline", and this client would
	// update itself as well on lo

	public static class QueueData {
		public JsArray<JsSendOperation> operations;
		// true if queue has been created offline and not from active memory
		// - user edited board offline
		// - user got online and board is reloaded and send offline data to server
		public boolean offline;

		private QueueData(JsArray<JsSendOperation> operations, boolean offline) {
			this.operations = operations;
			this.offline = offline;
		}
	}

	public QueueData toJsonAndClear() {
		JsArray<JsSendOperation> result = null;

    // After reload content feature introduced, offline state
    // cannot be checked completely from queuedOperations.
		boolean offline = this.nextOffline;

		if (!queuedOperations.isEmpty()) {
			// queuedOperations.clear();
			result = prepareForSending();
		} else {
			restore();
			// result = webStorageGet(queueName(boardName));
			result = prepareForSending();
			offline = true;
		}

    this.nextOffline = false;

		return new QueueData(result, offline);
	}

	private JsArray<JsSendOperation> prepareForSending() {
		String guid = ClientIdHelpers.guid();
		// mark sending with guid
		// List<SendOperation> tosend = new ArrayList<SendOperation>();
		for (SendOperation so : queuedOperations) {
			if (so.getGuid() == null) {
				// if not marked as sent
        if (so.operation.getValue().startsWith("user.")) {
          so.setGuid("u_" + guid);
        } else {
          so.setGuid(guid);
        }
				
				// tosend.add(so);
			}
		}
		// sends all operations; all should have guid!
		// could be debug assert!!
		boolean filterOutUserOperations = false;
		return toJson(queuedOperations, filterOutUserOperations);
	}

	private void restore() {
		String jsonStr = WebStorage.get(queueName(boardName));
		if (jsonStr != null && !"".equals(jsonStr)) {
			fromJsonStr(jsonStr);
		}
	}

	private void fromJsonStr(String jsonStr) {
    JSONValue jvalue = JSONParser.parseStrict(jsonStr);
    
		JSONArray jqueue = jvalue.isArray();
		if (jqueue != null) {
      parseOperations(jqueue);
		} else if (jvalue.isObject() != null) {
      JSONValue jTabId = jvalue.isObject().get("tab_id");
      if (jTabId.isString() != null) {
        // nothing to do with tab_id
        // result.tabId = jTabId.isString().stringValue();
      }

      JSONValue jops = jvalue.isObject().get("operations");
      if (jops.isArray() != null) {
        // restore operations
        parseOperations(jops.isArray());
      }
    }
  }
  
  private void parseOperations(JSONArray jqueue) {
    for (int i = 0; i < jqueue.size(); ++i) {
      JSONObject obj = jqueue.get(i).isObject();
      if (obj != null) {
        SendOperation so = SendOperation.fromJson(obj);
        if (so != null) {
          queuedOperations.add(so);
        }
      }
    }
  }

	public boolean ack(JsArrayString guids) {
    // at least one non user operation
    boolean diagramOperation = false;

		List<String> matched = new ArrayList<String>();
		for (int i = 0; i < guids.length(); ++i) {
			String guid = guids.get(i);
			if (!matched.contains(guid)) {
				matched.add(guid);
			}

      if (!guid.startsWith("u_")) {
        diagramOperation = true;
        addUpdateVersion(guid);
      }
		}

		// remove guided operations from the queue
    ListIterator<SendOperation> listIterator = queuedOperations.listIterator();
    while (listIterator.hasNext()) {
    	SendOperation so = listIterator.next();
			for (int i = 0; i < guids.length(); ++i) {
				String guid = guids.get(i);
				if (guid != null && guid.equals(so.getGuid())) {
					listIterator.remove();
					matched.remove(guid);
				}
    	}
    }

		storeQueue();

    // there was originally some guids and all matched
    boolean result = guids.length() > 0 && matched.size() == 0;

    if (result && diagramOperation) {
      // guids were removed and at least one was a diagram operation
      // Need to notify save state that something has been
      // successfully saved to start the save counter from
      // the beginng.
      notifySomeOperationsSaved();
    }

		
		return result;
	}

  private void addUpdateVersion(String guid) {
    updates.add(guid);
    Debug.debugConsole("sbcomet", "new update version", this.getUpdateVersion());
  }

  private native void notifySomeOperationsSaved()/*-{
    $wnd.ReactEventStream.fire("acksuccess");
  }-*/;

	public JsArray<JsSendOperation> getOperationsByGuids(JsArrayString guids) {
		JsArray<JsSendOperation> result = JavaScriptObject.createArray().cast();
		// remove guided operations from the queue
    ListIterator<SendOperation> listIterator = queuedOperations.listIterator();
    while (listIterator.hasNext()) {
    	SendOperation so = listIterator.next();
			for (int i = 0; i < guids.length(); ++i) {
				String guid = guids.get(i);
				if (guid != null && guid.equals(so.getGuid())) {
					result.push(so.toJson());
				}
    	}
    }

    return result;
	}

	public void clear() {
		queuedOperations.clear();
		storeQueue();			
	}

	public boolean acknowledgedFromServerOrShouldRetry() {
		long now = System.currentTimeMillis();

		for (SendOperation op : queuedOperations) {
			long diffSinceCreated = now - op.gtime;

			if (op.getGuid() != null && diffSinceCreated <= SEND_RETRY_TIMEOUT) {
				// needs to be less than the timeout or should retry sending the guid
				return false;
			}
		}
		return true;
	}

	private String toJsonArray(String result) {
		return "[" + result + "]";
	}

	private static JsArray<JsSendOperation> toJson(List<SendOperation> operations, boolean filterOutUserOperations) {
		JsArray<JsSendOperation> result = JavaScriptObject.createArray().cast();
		for (SendOperation o : operations) {
			boolean add = false;
			if (filterOutUserOperations && !OTOperation.USER_MOVE.equals(o.getOperation())) {
				add = true;
			} else if (!filterOutUserOperations) {
				add = true;
			}

			// if (add) {
			// 	if (result.length() > 0) {
			// 		result += ",";
			// 	}
			// 	result += o.toJson();
			// }
			if (add) {
				result.push(o.toJson());
			}
		}
		return result;
	}

	public boolean isEmpty() {
		if (queuedOperations.isEmpty()) {
			// check if something is still in web storage
			return WebStorage.isEmpty(queueName(boardName));
		} else {
			return false;
		}
	}

	public boolean isSaved() {
		boolean result = isEmpty();
		if (!result) {
			// if contains only user operations then document is saved
			result = containsOnlyUserMoveOperations();
		}
		return result;
	}

	public boolean flushedAndAcknowledgedFromServer() {
		// need to check that server has ack last change, queue must be empty 
		// and editor doesn't have any pending changes
	  logger.debug("acknowledged.acknowledgedFromServer({}) && isEmpty({}) && !editor.hasPendingChanges({})", 
	      acknowledged.acknowledgedFromServerOrShouldRetry(), isEmpty(), !editor.hasPendingChanges());
		return acknowledged.acknowledgedFromServerOrShouldRetry() && isEmpty() && !editor.hasPendingChanges();
	}

	public void setServerDocumentChecksum(String checksum) {
		if ("".equals(checksum)) {
			// special case when synchronizing from another tab
			return;
		}

		this.checksum = checksum;
		WebStorage.setString(checksumName(), checksum);
	}

	public String getServerDocumentChecksum() {
		if (checksum == null) {
			return WebStorage.get(checksumName());
		}
		return checksum;
	}

	private String checksumName() {
		return boardName + "-server-checksum";
	}

  public boolean isApplied(String guid) {
    return updates.contains(guid);
  }

  public int getUpdateVersion() {
    return this.baseUpdateVersion +  this.updates.size();
  }

  public int getBaseUpdateVersion() {
    return this.baseUpdateVersion;
  }

  public void setBaseUpdateVersion(int updateVersion) {
    this.baseUpdateVersion = updateVersion;

    // need to handle updates from this point forward
    this.clearUpdates();
  }

  public void clearUpdates() {
    this.updates.clear();
    Debug.debugConsole("sbcomet", "clear update version", this.getUpdateVersion());
  }

  public void setNextOffline(boolean nextOffline) {
    this.nextOffline = nextOffline;
  }

  public void applied(List<DiagramApplyOperation> applied) {
    for (DiagramApplyOperation dao : applied) {
      if (dao.getGuid() != null) {
        this.updates.add(dao.getGuid());
      }
    }
  }

	/**
	 * Finds previous state of the item if any from the queue.
	 */
	// public DiagramItemJS findPrevItemState(OTOperation operation, IDiagramItemRO tofind) {
	// 	boolean found = false;
	// 	for (int x = queuedOperations.size() - 1; x >= 0; --x) {
	// 		SendOperation so = queuedOperations.get(x);
	// 		JsArray<DiagramItemJS> items = JsonUtils.safeEval(so.getOperationJson());
	// 		for (int i = 0; i < items.length(); ++i) {
	// 			DiagramItemJS dijs = items.get(i);
	// 			// is	item state and operation exactly the same
	// 			if (!found && operation == so.operation && tofind.equals(dijs)) {
	// 				// first occurrence found
	// 				found = true;
	// 			}
	// 			if (found && tofind.getClientId().equals(dijs.getClientId())) {
	// 				// this is previous
	// 				return dijs;
	// 			}
	// 		}
	// 	}
	// 	return null;
	// }

}
