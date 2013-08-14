package net.sevenscales.editor.api.ot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.domain.DiagramItemJS;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.api.IDiagramItem;
import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.content.OperationJS;
import net.sevenscales.editor.content.utils.DiagramItemFactory;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.uicomponents.CircleElement;
import net.sevenscales.sketchoconfluenceapp.client.util.BoardUser.BoardUserJson;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;

public class BoardDocumentHelpers {
	private static final SLogger logger = SLogger.createLogger(BoardDocumentHelpers.class);
	
	public static final ClientIdComparator DIAGRAM_IDENTIFIER_COMPARATOR = new ClientIdComparator();

	private static class ClientIdComparator implements Comparator<Diagram> {
		@Override
		public int compare(Diagram diagram, Diagram theother) {
			return compareClientId(diagram.getDiagramItem(), theother.getDiagramItem());
		}
	}
	
	public static final DiagramItemIdComparator DIAGRAM_ITEM_IDENTIFIER_COMPARATOR = new DiagramItemIdComparator();

	private static class DiagramItemIdComparator implements Comparator<IDiagramItemRO> {
		@Override
		public int compare(IDiagramItemRO diagram, IDiagramItemRO theother) {
			return compareClientId(diagram, theother);
		}
	}
	
	private static int compareClientId(IDiagramItemRO item, IDiagramItemRO theother) {
		String clientId = item.getClientId();
		String theOtherClientId = theother.getClientId();
		int result = clientId.compareTo(theOtherClientId);
		return result;
	}


	public BoardDocumentHelpers() {
	}
	
	public static List<IDiagramItem> getDiagramsAsDTO(List<Diagram> diagrams, boolean updateDiagramItem) {
		Collections.sort(diagrams, DIAGRAM_IDENTIFIER_COMPARATOR);
		List<IDiagramItem> result = new ArrayList<IDiagramItem>();
		for (Diagram d : diagrams) {
			if ( !(d instanceof CircleElement) ) {
				IDiagramItem di = d.getDiagramItem().copy();
				if (updateDiagramItem) {
					di = DiagramItemFactory.createOrUpdate(d, false);
				}
				result.add(di);
			}
		}
		return result;
	}
	
	public static List<IDiagramItemRO> copyDiagramItems(List<? extends IDiagramItemRO> items) {
		List<IDiagramItemRO> result = new ArrayList<IDiagramItemRO>();
		for (IDiagramItemRO di : items) {
			insertInOrder(new DiagramItemDTO(di), result);
		}
		return result;
	}

	public static List<IDiagramItemRO> fromJson(String json) {
		JsArray<DiagramItemJS> items = JsonUtils.safeEval(json);
		return fromDiagramItemJsArray(items);
	}
	
	public static List<IDiagramItemRO> fromDiagramItemJsArray(JsArray<DiagramItemJS> items) {
		List<IDiagramItemRO> result = new ArrayList<IDiagramItemRO>();
		for (int i = 0; i < items.length(); ++i) {
			DiagramItemDTO di = items.get(i).asDTO();
			result.add(di);
		}
		return result;
	}
	
//	public static List<IDiagramItemRO> applyOperationsToOrderedDiagramItems(List<ApplyOperation> applyOperations) {
//		List<IDiagramItemRO> result = new ArrayList<IDiagramItemRO>();
//		for (ApplyOperation ap : applyOperations) {
//			result.addAll(BoardDocumentHelpers.fromDiagramItemJsArray(ap.getItems()));
//		}
//		return result;
//	}
	
	public static int binarySearch(List<IDiagramItemRO> items, IDiagramItemRO di) {
		return Collections.binarySearch(items, di, BoardDocumentHelpers.DIAGRAM_ITEM_IDENTIFIER_COMPARATOR);
	}
	
	public static int binarySearch(List<Diagram> items, Diagram di) {
		return Collections.binarySearch(items, di, BoardDocumentHelpers.DIAGRAM_IDENTIFIER_COMPARATOR);
	}
	
	public static void insertInOrder(IDiagramItemRO di, List<IDiagramItemRO> list) {
		int index = binarySearch(list, di);
    if (index < 0) index = ~index;
    list.add(index, di);
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

		public BoardUserApplyOperation(OTOperation operation, String username, String avatarUrl, int x, int y) {
			super(operation);
			this.username = username;
			this.avatarUrl = avatarUrl;
			this.x = x;
			this.y = y;
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
	
	public static ApplyOperations toApplyOperations(String operations) {
		ApplyOperations result = new ApplyOperations();
		result.diagramOperations = new ArrayList<DiagramApplyOperation>();
		result.userOperations = new ArrayList<BoardUserApplyOperation>();

		JsArray<OperationJS> ops = JsonUtils.safeEval(operations);
		for (int i = 0; i < ops.length(); ++i) {
			OperationJS opjs = ops.get(i);
			OTOperation operation = OTOperation.getEnum(opjs.getOperation());
			if (opjs.getOperation().startsWith("user.")) {
				BoardUserApplyOperation applyOperation = createBoardUserApplyOperation(operation, opjs.getUser());
				result.userOperations.add(applyOperation);
			} else {
				DiagramApplyOperation applyOperation = createDiagramApplyOperation(operation, opjs.getItems());
				result.diagramOperations.add(applyOperation);
			}
		}
		return result;
	}

	private static BoardUserApplyOperation createBoardUserApplyOperation(OTOperation operation, BoardUserJson boardUserJson) {
		return new BoardUserApplyOperation(operation, boardUserJson.getUsername(), boardUserJson.getAvatarUrl(), boardUserJson.getX(), boardUserJson.getY());
	}

	private static DiagramApplyOperation createDiagramApplyOperation(OTOperation operation, JsArray<DiagramItemJS> itemsJs) {
		List<IDiagramItemRO> items = new ArrayList<IDiagramItemRO>();
		for (int x = 0; x < itemsJs.length(); ++x) {
			items.add(itemsJs.get(x).asDTO());
		}

		return new DiagramApplyOperation(operation, items);
	}

	public static List<IDiagramItemRO> fromApplyOperations(JsArray<DiagramItemJS> items) {
		List<IDiagramItemRO> result = new ArrayList<IDiagramItemRO>();
		for (int i = 0; i < items.length(); ++i) {
			result.add(items.get(i).asDTO());
		}
		return result;
	}
	
	public static JsArray<DiagramItemJS> toJsDiagramItems(String items) {
		return JsonUtils.safeEval(items);
	}

}
