package net.sevenscales.editor.api.ot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.api.IDiagramItem;
import net.sevenscales.domain.utils.DiagramItemIdComparator;
import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.content.utils.DiagramItemFactory;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.DiagramSearch;
import net.sevenscales.editor.gfx.domain.IChildElement;
import net.sevenscales.editor.gfx.domain.IRelationship;
import net.sevenscales.editor.uicomponents.CircleElement;
import net.sevenscales.editor.uicomponents.uml.ChildTextElement;

public class BoardDocumentHelpers {
	private static final SLogger logger = SLogger.createLogger(BoardDocumentHelpers.class);

	public static final DiagramItemIdComparator DIAGRAM_ITEM_IDENTIFIER_COMPARATOR = new DiagramItemIdComparator();
	public static final ClientIdComparator DIAGRAM_IDENTIFIER_COMPARATOR = new ClientIdComparator();

	private static class ClientIdComparator implements Comparator<Diagram> {
		@Override
		public int compare(Diagram diagram, Diagram theother) {
			return DiagramItemIdComparator.compareClientId(diagram.getDiagramItem(), theother.getDiagramItem());
		}
	}

	public BoardDocumentHelpers() {
	}

	public static List<Diagram> resolveAlsoParents(Set<Diagram> diagrams, DiagramSearch search) {
		List<Diagram> result = new ArrayList<Diagram>();
		for (Diagram d : diagrams) {
			String parentId = d.getDiagramItem().getParentId();
			if (parentId != null && d instanceof ChildTextElement) {
				Diagram parent = search.findByClientId(parentId);
				addUnique(parent, result);
			} else if (d instanceof IRelationship) {
				IRelationship parent = (IRelationship) d;
				for (IChildElement child : parent.getChildren()) {
					addUnique(child.asDiagram(), result);
				}
			}
			addUnique(d, result);
		}
		return result;
	}

	public static void addUnique(Diagram d, List<Diagram> list) {
		if (!list.contains(d)) {
			list.add(d);
		}
	}


	public static List<? extends IDiagramItemRO> diagramsToItems(Iterable<Diagram> diagrams) {
    List<Diagram> filteredDiagrams = net.sevenscales.editor.content.utils.DiagramHelpers.filterOwnerDiagramsAsListKeepOrder(diagrams, net.sevenscales.editor.api.ActionType.NONE);
    
    List<? extends IDiagramItemRO> operationItems = BoardDocumentHelpers.getDiagramsAsDTOKeepOrder(filteredDiagrams, true);
    
		return operationItems;
	}

  public static JsArrayString getDiagramClientIdsOrdered(Iterable<Diagram> diagrams) {
    List<? extends IDiagramItemRO> items = BoardDocumentHelpers.diagramsToItems(
      diagrams
    );

    JsArrayString result = JavaScriptObject.createArray().cast();
    for (IDiagramItemRO diro : items) {
      result.push(diro.getClientId());
    }
    return result;
  }

  public static JsArrayString getDiagramClientIds(Iterable<Diagram> diagrams) {
    JsArrayString result = JavaScriptObject.createArray().cast();
    for (Diagram d : diagrams) {
    	// if (d.getDiagramItem().getClientId() != null) {
    	if (!(d instanceof CircleElement)) {
    		// now constantly using circle element... though client id would
    		// be more generic even in future, but is it possible that it is missing
    		// in some cases?!?
	      result.push(d.getDiagramItem().getClientId());
    	}
    }
    return result;
  }

	public static List<IDiagramItem> getDiagramsAsDTO(List<Diagram> diagrams, boolean updateDiagramItem) {
		Collections.sort(diagrams, DIAGRAM_IDENTIFIER_COMPARATOR);
		List<IDiagramItem> result = new ArrayList<IDiagramItem>();
		for (Diagram d : diagrams) {
			if ( !(d instanceof CircleElement) ) {
				IDiagramItem di = d.getDiagramItem().copy();
				if (updateDiagramItem) {
					di = DiagramItemFactory.createOrUpdate(d);
				}
				result.add(di);
			}
		}
		return result;
	}

	public static List<IDiagramItem> getDiagramsAsDTOKeepOrder(List<Diagram> diagrams, boolean updateDiagramItem) {
		List<IDiagramItem> result = new ArrayList<IDiagramItem>();
		for (Diagram d : diagrams) {
			if ( !(d instanceof CircleElement) ) {
				IDiagramItem di = d.getDiagramItem().copy();
				if (updateDiagramItem) {
					di = DiagramItemFactory.createOrUpdate(d);
				}
				result.add(di);
			}
		}
		return result;
	}
	
	public static List<IDiagramItemRO> copyDiagramItems(List<? extends IDiagramItemRO> items) {
		List<IDiagramItemRO> result = new ArrayList<IDiagramItemRO>();
		for (IDiagramItemRO di : items) {
			insertInOrder(di.copy(), result);
		}
		return result;
	}

	// public static List<IDiagramItemRO> fromJson(String json) {
	// 	JsArray<DiagramItemJS> items = JsonUtils.safeEval(json);
	// 	return fromDiagramItemJsArray(items);
	// }
		
//	public static List<IDiagramItemRO> applyOperationsToOrderedDiagramItems(List<ApplyOperation> applyOperations) {
//		List<IDiagramItemRO> result = new ArrayList<IDiagramItemRO>();
//		for (ApplyOperation ap : applyOperations) {
//			result.addAll(BoardDocumentHelpers.fromDiagramItemJsArray(ap.getItems()));
//		}
//		return result;
//	}
	
	public static int binarySearch(List<IDiagramItemRO> items, IDiagramItemRO di) {
		return Collections.binarySearch(items, di, DIAGRAM_ITEM_IDENTIFIER_COMPARATOR);
	}
	
	public static int binarySearch(List<Diagram> items, Diagram di) {
		return Collections.binarySearch(items, di, DIAGRAM_IDENTIFIER_COMPARATOR);
	}
	
	public static void insertInOrder(IDiagramItemRO di, List<IDiagramItemRO> list) {
		int index = binarySearch(list, di);
    if (index < 0) index = ~index;
    list.add(index, di);
	}

	// public static List<IDiagramItemRO> fromApplyOperations(JsArray<DiagramItemJS> items) {
	// 	List<IDiagramItemRO> result = new ArrayList<IDiagramItemRO>();
	// 	for (int i = 0; i < items.length(); ++i) {
	// 		result.add(items.get(i).asDTO());
	// 	}
	// 	return result;
	// }
	
	// public static JsArray<DiagramItemJS> toJsDiagramItems(String items) {
	// 	return JsonUtils.safeEval(items);
	// }

}
