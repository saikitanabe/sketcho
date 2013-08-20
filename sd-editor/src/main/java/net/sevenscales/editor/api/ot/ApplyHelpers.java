package net.sevenscales.editor.api.ot;

import java.util.List;
import java.util.ArrayList;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;

import net.sevenscales.editor.content.OperationJS;
import net.sevenscales.domain.DiagramItemJS;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.editor.api.ot.OTOperation;

public class ApplyHelpers {

	public static ApplyOperations toApplyOperations(String operations) {
		ApplyOperations result = new ApplyOperations();
		result.diagramOperations = new ArrayList<DiagramApplyOperation>();
		result.userOperations = new ArrayList<BoardUserApplyOperation>();

		JsArray<OperationJS> ops = JsonUtils.safeEval(operations);
		for (int i = 0; i < ops.length(); ++i) {
			OperationJS opjs = ops.get(i);
			OTOperation operation = OTOperation.getEnum(opjs.getOperation());
			if (opjs.getOperation().startsWith("user.")) {
				BoardUserApplyOperation applyOperation = createBoardUserApplyOperation(operation, opjs.getUsers());
				if (applyOperation != null) {
					result.userOperations.add(applyOperation);
				}
			} else {
				DiagramApplyOperation applyOperation = createDiagramApplyOperation(operation, opjs.getItems());
				result.diagramOperations.add(applyOperation);
			}
		}
		return result;
	}

	public static class ApplyOperation {
		private OTOperation operation;

		public ApplyOperation(OTOperation operation) {
			this.operation = operation;
		}

		public OTOperation getOperation() {
			return operation;
		}
	}

	public static class BoardUserApplyOperation extends ApplyOperation {
		private String username;
		private String avatarUrl;
		private int x;
		private int y;
		private int targetx;
		private int targety;
		private String selectedCids;
		private String clientIdentifier;

		public BoardUserApplyOperation(OTOperation operation, String username, String avatarUrl, int x, int y, int targetx, int targety, String selectedCids, String clientIdentifier) {
			super(operation);
			this.username = username;
			this.avatarUrl = avatarUrl;
			this.x = x;
			this.y = y;
			this.targetx = targetx;
			this.targety = targety;
			this.selectedCids = selectedCids;
			this.clientIdentifier = clientIdentifier;
		}

		public String getUsername() {
			return username;
		}

		public String getAvatarUrl() {
			return avatarUrl;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public int getTargetX() {
			return targetx;
		}

		public int getTargetY() {
			return targety;
		}

		public String getSelectedCids() {
			return selectedCids;
		}

		public String getClientIdentifier() {
			return clientIdentifier;
		}
	}

	public static class DiagramApplyOperation extends ApplyOperation {
		private List<IDiagramItemRO> items;
		
		public DiagramApplyOperation(OTOperation operation, List<IDiagramItemRO> items) {
			super(operation);
			this.items = items;
		}
				
		public List<IDiagramItemRO> getItems() {
			return items;
		}
	}

	public static class ApplyOperations {
		public List<DiagramApplyOperation> diagramOperations;
		public List<BoardUserApplyOperation> userOperations;
	}
	
	private static BoardUserApplyOperation createBoardUserApplyOperation(OTOperation operation, JsArray<BoardUser.BoardUserJson> usersJs) {
		int length = usersJs.length();
		if (length > 0) {
			// read only last
			BoardUser.BoardUserJson json = usersJs.get(length - 1);
			return new BoardUserApplyOperation(operation, json.getUsername(), json.getAvatarUrl(), json.getX(), json.getY(), json.getTargetX(), json.getTargetY(), json.getSelectedCids(), json.getClientIdentifier());
		}
		return null;
	}

	private static DiagramApplyOperation createDiagramApplyOperation(OTOperation operation, JsArray<DiagramItemJS> itemsJs) {
		List<IDiagramItemRO> items = new ArrayList<IDiagramItemRO>();
		for (int x = 0; x < itemsJs.length(); ++x) {
			items.add(itemsJs.get(x).asDTO());
		}

		return new DiagramApplyOperation(operation, items);
	}

}