package net.sevenscales.editor.api.ot;

import java.util.LinkedList;
import java.util.List;

import net.sevenscales.domain.DiagramItemJS;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.IEditor;
import net.sevenscales.editor.content.utils.JsonHelpers;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;


public class OperationQueue {
  private static SLogger logger = SLogger.createLogger(OperationQueue.class);
  
	private LinkedList<SendOperation> queuedOperations;
	private Acknowledged acknowledged;
	private IEditor editor;

	public OperationQueue(Acknowledged acknowledged, IEditor editor) {
		this.acknowledged = acknowledged;
		this.editor = editor;
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

		public SendOperation(OTOperation operation, String operationJson) {
			super();
			this.operation = operation;
			this.operationJson = operationJson;
		}

		public JSONValue toJson() {
			JSONObject result = new JSONObject();
			result.put("operation", new JSONString(operation.toString()));
			result.put("json", new JSONString(operationJson));
			return result;
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
		queuedOperations.addLast(item);
	}

	public boolean containsInsertOperation() {
		for (SendOperation o : queuedOperations) {
			if (OTOperation.INSERT.equals(o.getOperation())) {
				return true;
			}
		}
		return false;
	}

	public String toJsonAndClear() {
		String result = toJson(queuedOperations);
		queuedOperations.clear();
		return result;
	}
	
	private static String toJson(List<SendOperation> operations) {
		String result = "";
		for (SendOperation o : operations) {
			result += o.toJson() + ",";
		}
		result = JsonHelpers.removeLastComma(result);
		return "[" + result + "]";
	}

	public boolean isEmpty() {
		return queuedOperations.isEmpty();
	}

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
	public DiagramItemJS findPrevItemState(OTOperation operation, IDiagramItemRO tofind) {
		boolean found = false;
		for (int x = queuedOperations.size() - 1; x >= 0; --x) {
			SendOperation so = queuedOperations.get(x);
			JsArray<DiagramItemJS> items = JsonUtils.safeEval(so.getOperationJson());
			for (int i = 0; i < items.length(); ++i) {
				DiagramItemJS dijs = items.get(i);
				// is	item state and operation exactly the same
				if (!found && operation == so.operation && tofind.equals(dijs)) {
					// first occurrence found
					found = true;
				}
				if (found && tofind.getClientId().equals(dijs.getClientId())) {
					// this is previous
					return dijs;
				}
			}
		}
		return null;
	}

}
