package net.sevenscales.editor.api.ot;

import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;

import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.domain.JSONParserHelpers;
import net.sevenscales.editor.api.IEditor;
import net.sevenscales.editor.content.utils.JsonHelpers;
import net.sevenscales.editor.content.ClientIdHelpers;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.Window;
import com.google.gwt.logging.client.LogConfiguration;
import java.util.logging.Level;


public class OperationQueue {
  private static SLogger logger = SLogger.createLogger(OperationQueue.class);
  
	private LinkedList<SendOperation> queuedOperations;
	private Acknowledged acknowledged;
	private IEditor editor;
	private String boardName;

	public OperationQueue(Acknowledged acknowledged, IEditor editor, String boardName) {
		this.acknowledged = acknowledged;
		this.editor = editor;
		this.boardName = boardName;
		queuedOperations = new LinkedList<SendOperation>();
	}
	
	public interface Acknowledged {
		boolean acknowledgedFromServer();
	}

	public static class SendOperation {
		@Override
		public String toString() {
			return "SendOperation [operation=" + operation + ", operationJson="
					+ operationJson + "]";
		}

		private OTOperation operation;
		private String operationJson;
		private String guid;

		public SendOperation(OTOperation operation, String operationJson) {
			this(operation, operationJson, null);
		}
		public SendOperation(OTOperation operation, String operationJson, String guid) {
			this.operation = operation;
			this.operationJson = operationJson;
			this.guid = guid;
		}

		public static SendOperation fromJson(JSONObject obj) {
			SendOperation result = null;
			if (obj != null) {
				String operation = JSONParserHelpers.getString(obj.get("operation"));
				// this is json not string, parse accordingly => then to string
				// converted to json, so string parsing can use gwt json parser!
				JSONValue vitems = obj.get("items");
				String items = "[]";
				if (vitems != null && vitems.isArray() != null) {
					items = vitems.toString();
				}
				String guid = null;
				JSONValue jguid = obj.get("guid");
				if (jguid != null && jguid.isString() != null && !"".equals(jguid.isString().stringValue())) {
					guid = jguid.isString().stringValue();
				}
				result = new SendOperation(OTOperation.getEnum(operation), items, guid);
			}
			return result;
		}

		public String toJson() {
			if (guid != null) {
				return SLogger.format("{\"operation\":\"{}\",\"items\":{},\"guid\":\"{}\"}", 
						operation.toString(),
						operationJson,
						guid);
			} else {
				return SLogger.format("{\"operation\":\"{}\",\"items\":{}}", 
						operation.toString(),
						operationJson);
			}
			// cannot use gwt utilities since already converted from JSONValue
			// JSONObject result = new JSONObject();
			// result.put("operation", new JSONString(operation.toString()));
			// result.put("items", new JSONString(operationJson));
			// return result;
		}

		public void setGuid(String guid) {
			this.guid = guid;
		}

		public String getGuid() {
			return guid;
		}

		public OTOperation getOperation() {
			return operation;
		}
		
		public String getOperationJson() {
			return operationJson;
		}

	}

	public void push(SendOperation item) {
		// need to add in reverse order to have items applied in correct order on server
		if (item.operationJson != null && !contains(item)) {
			queuedOperations.addLast(item);
			storeQueue();
		} else if (!item.operation.equals(OTOperation.USER_MOVE) && LogConfiguration.loggingIsEnabled(Level.FINEST)) {
			// not checkint user move operations
			Window.alert("push failed");
			debugger();
		}
	}

	private void storeQueue() {
		boolean filterOutUserOperations = true;
		String json = toJson(queuedOperations, filterOutUserOperations);
		if (json.length() > 0) {
			store(queueName(boardName), toJsonArray(json));
		} else {
			// nothing so remove whole queue
			webStorageRemove(queueName(boardName));
		}
	}

	private String queueName(String boardId) {
		return "q_" + boardId;
	}

	private native void store(String key, String jsonStr)/*-{
		if (typeof $wnd.webStorage !== 'undefined') {
			$wnd.webStorage.set(key, jsonStr);
		}
	}-*/;

	private native void debugger()/*-{
		debugger;
	}-*/;

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
		public String operations;
		// true if queue has been created offline and not from active memory
		// - user edited board offline
		// - user got online and board is reloaded and send offline data to server
		public boolean offline;

		private QueueData(String operations, boolean offline) {
			this.operations = operations;
			this.offline = offline;
		}
	}

	public QueueData toJsonAndClear() {
		String result = "";
		boolean offline = false;
		if (!queuedOperations.isEmpty()) {
			// queuedOperations.clear();
			result = prepareForSending();
		} else {
			restore();
			// result = webStorageGet(queueName(boardName));
			result = prepareForSending();
			offline = true;
		}

		return new QueueData(toJsonArray(result), offline);
	}

	private String prepareForSending() {
		String guid = ClientIdHelpers.guid();
		// mark sending with guid
		// List<SendOperation> tosend = new ArrayList<SendOperation>();
		for (SendOperation so : queuedOperations) {
			if (so.getGuid() == null) {
				// if not marked as sent
				so.setGuid(guid);
				// tosend.add(so);
			}
		}
		// sends all operations; all should have guid!
		// could be debug assert!!
		boolean filterOutUserOperations = false;
		return toJson(queuedOperations, filterOutUserOperations);
	}

	private void restore() {
		String jsonStr = webStorageGet(queueName(boardName));
		if (jsonStr != null && !"".equals(jsonStr)) {
			fromJsonStr(jsonStr);
		}
	}

	private void fromJsonStr(String jsonStr) {
		JSONValue jvalue = JSONParser.parseStrict(jsonStr);
		JSONArray jqueue = jvalue.isArray();
		if (jqueue != null) {
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
	}

	public boolean ack(JsArrayString guids) {
		boolean result = false;
		List<String> matched = new ArrayList<String>();
		for (int i = 0; i < guids.length(); ++i) {
			String guid = guids.get(i);
			if (!matched.contains(guid)) {
				matched.add(guid);
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
		return guids.length() > 0 && matched.size() == 0;
	}

	public boolean acknowledgedFromServer() {
		for (SendOperation op : queuedOperations) {
			if (op.getGuid() != null) {
				return false;
			}
		}
		return true;
	}

	private native String webStorageGet(String key)/*-{
		if (typeof $wnd.webStorage !== 'undefined') {
			var result = $wnd.webStorage.get(key);
			return result
		}
		return ""
	}-*/;

	private native boolean webStorageRemove(String key)/*-{
		if (typeof $wnd.webStorage !== 'undefined') {
			return $wnd.webStorage.remove(key)
		}
		return false
	}-*/;

	private String toJsonArray(String result) {
		return "[" + result + "]";
	}

	private static String toJson(List<SendOperation> operations, boolean filterOutUserOperations) {
		String result = "";
		for (SendOperation o : operations) {
			boolean add = false;
			if (filterOutUserOperations && !OTOperation.USER_MOVE.equals(o.getOperation())) {
				add = true;
			} else if (!filterOutUserOperations) {
				add = true;
			}

			if (add) {
				if (result.length() > 0) {
					result += ",";
				}
				result += o.toJson();
			}
		}
		return result;
	}

	public boolean isEmpty() {
		if (queuedOperations.isEmpty()) {
			// check if something is still in web storage
			return isWebStorageEmpty(queueName(boardName));
		} else {
			return false;
		}
	}

	private native boolean isWebStorageEmpty(String key)/*-{
		if (typeof $wnd.webStorage !== 'undefined') {
			return $wnd.webStorage.isEmpty(key)
		}
		return true
	}-*/;

	public boolean flushedAndAcknowledgedFromServer() {
		// need to check that server has ack last change, queue must be empty 
		// and editor doesn't have any pending changes
	  logger.debug("acknowledged.acknowledgedFromServer({}) && isEmpty({}) && !editor.hasPendingChanges({})", 
	      acknowledged.acknowledgedFromServer(), isEmpty(), !editor.hasPendingChanges());
		return acknowledged.acknowledgedFromServer() && isEmpty() && !editor.hasPendingChanges();
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
