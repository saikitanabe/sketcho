package net.sevenscales.editor.api.ot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.ot.ApplyHelpers.DiagramApplyOperation;

public class OTCompensationTransformerTest extends TestCase {
	private List<? extends IDiagramItemRO> currentState;
	private static final Map<OTOperation, OTOperation> mappedCompensatedOperations;
	
	static {
		mappedCompensatedOperations = new HashMap<OTOperation, OTOperation>();
		mappedCompensatedOperations.put(OTOperation.INSERT, OTOperation.DELETE);
		mappedCompensatedOperations.put(OTOperation.DELETE, OTOperation.INSERT);
		mappedCompensatedOperations.put(OTOperation.MODIFY, OTOperation.MODIFY);
	}
	
	private List<DiagramApplyOperation> generateApplyOperations(OTOperation[] operations, int maxItemsPerApplyOperation) {
		List<DiagramApplyOperation> result = new ArrayList<DiagramApplyOperation>();
		
		for (int i = 0; i < operations.length; ++i) {
			int itemCount = (int) (Math.random() * maxItemsPerApplyOperation);
			// at least one
			itemCount = itemCount == 0 ? 1 : itemCount;
			
			List<IDiagramItemRO> items = TestUtils.generateDiagramItems(itemCount);
			DiagramApplyOperation ap = new DiagramApplyOperation(operations[i], items);
			result.add(ap);
		}
		
		return result;
	}

	public void testCompensateApplyOperationsInsert() throws MappingNotFoundException {
		OTCompensationTransformer t = new OTCompensationTransformer();
		t.setTestMode(true);
		currentState = TestUtils.generateDiagramItems(0);
		List<DiagramApplyOperation> applyOperations = generateApplyOperations(new OTOperation[]{OTOperation.INSERT}, 1);
		
		List<CompensationModel> models = t.compensateApplyOperations(applyOperations, currentState);
		assertNotNull("compensation models should not be null", models);
		assertEquals("there should be same amount of compensations as there are apply operations", applyOperations.size(), models.size());
		assertTrue("should be compensated to DELETE", OTOperation.DELETE == models.get(0).undoOperation);
		assertNotSame("compensation model need to have copied diagram item", applyOperations.get(0).getItems().get(0), models.get(0).undoJson.get(0));
		assertEquals("client id should match", applyOperations.get(0).getItems().get(0).getClientId(), models.get(0).undoJson.get(0).getClientId());
		assertEquals("only client id should have been set", new DiagramItemDTO(TestUtils.generateId(0)), models.get(0).undoJson.get(0));
	}

	public void testCompensateApplyOperationsDelete() throws MappingNotFoundException {
		OTCompensationTransformer t = new OTCompensationTransformer();
		t.setTestMode(true);
		currentState = TestUtils.generateDiagramItems(7);
		List<DiagramApplyOperation> applyOperations = generateApplyOperations(new OTOperation[]{OTOperation.DELETE}, 7);
		
		List<CompensationModel> models = t.compensateApplyOperations(applyOperations, currentState);
		assertEquals("there should be same amount of compensations as there are apply operations", applyOperations.size(), models.size());
		assertTrue("should be compensated to INSERT", OTOperation.INSERT == models.get(0).undoOperation);
		assertEquals("there should be undo state for each item", applyOperations.get(0).getItems().size(), models.get(0).undoJson.size());
		assertTrue("redo should be DELETE", OTOperation.DELETE == models.get(0).redoOperation);

		compareApplyOperationsToCompensatedModels(applyOperations, models);
		
		assertNotSame("compensation model need to have copied diagram item", currentState.get(0), models.get(0).undoJson.get(0));
		assertEquals("client id should match", applyOperations.get(0).getItems().get(0).getClientId(), models.get(0).undoJson.get(0).getClientId());
		assertEquals("current item state should be the undo item state, as copied", currentState.get(0), models.get(0).undoJson.get(0));
		System.out.println(SLogger.format("current state {}, \nnew state {}", currentState.get(0).toString(), applyOperations.get(0).getItems().get(0).toString()));
		assertFalse("new and prev state should not be the same", currentState.get(0).equals(applyOperations.get(0).getItems().get(0)));
	}

	private void compareApplyOperationsToCompensatedModels(List<DiagramApplyOperation> applyOperations, List<CompensationModel> models) {
		for (int i = 0; i < applyOperations.size(); ++i) {
			DiagramApplyOperation ao = applyOperations.get(i);
			CompensationModel cm = models.get(i);
			for (int x = 0; x < ao.getItems().size(); ++x) {
				IDiagramItemRO expectedItem = ao.getItems().get(x);
				IDiagramItemRO actualRedo = cm.redoJson.get(x);
				
				assertRedo(expectedItem, actualRedo, ao, cm);
				
				// undo check
				// undo operation is calculated from apply operations in this case when apply operations are given
				// in case previous state is not found from apply operations, current state is used to calculate compensated
				// state.
				IDiagramItemRO expectedUndo = findPreviousItemState(expectedItem, applyOperations, i, currentState);
				IDiagramItemRO actualUndo = cm.undoJson.get(x);
				
				assertUndo(expectedUndo, actualUndo, ao, cm);
			}
		}
	}

	private void assertUndo(IDiagramItemRO expectedUndo, IDiagramItemRO actualUndo, DiagramApplyOperation ao, CompensationModel cm) {
		assertTrue("udo operation should be the opposite", mappedCompensatedOperations.get(ao.getOperation()) == cm.undoOperation);
		assertNotSame("undo item should not be a same instance", expectedUndo, actualUndo);
		if (cm.undoOperation == OTOperation.DELETE) {
			// delete operation has only client id, cannot compare anything else
			// in the future also other undo modify + insert calculates only difference so only diff fields should be checked
			// that those are restored correctly
			assertEquals("undo DELETE should have same client id", expectedUndo.getClientId(), actualUndo.getClientId());
		} else {
			assertEquals("undo item should be copy of the original item", expectedUndo, actualUndo);
		}
	}

	private void assertRedo(IDiagramItemRO expectedItem, IDiagramItemRO actualRedo, DiagramApplyOperation ao, CompensationModel cm) {
		assertTrue("redo operation should be the same", ao.getOperation() == cm.redoOperation);
		assertNotSame("redo item should not be a same as applied item", expectedItem, actualRedo);
		assertEquals("redo item should be copy of the applied item", expectedItem, actualRedo);
		System.out.println("REDO state matches with apply state: " + expectedItem);
	}

	private IDiagramItemRO findPreviousItemState(IDiagramItemRO tofind, List<DiagramApplyOperation> applyOperations, int currentApplyIndex, List<? extends IDiagramItemRO> currentState) {
		IDiagramItemRO result = findPreviousItemStateFromAppliedOperations(tofind, applyOperations, currentApplyIndex);
		// search from current state if item is not found from applied operations
		if (result == null) {
			result = findPreviousStateFromCurrentState(tofind, currentState);
		}
		
		return result;
	}

	private IDiagramItemRO findPreviousStateFromCurrentState(IDiagramItemRO tofind, List<? extends IDiagramItemRO> currentState) {
		for (IDiagramItemRO previousStateCandidate : currentState) {
			if (tofind.getClientId().equals(previousStateCandidate.getClientId())) {
				return previousStateCandidate;
			}
		}
		return null;
	}

	private IDiagramItemRO findPreviousItemStateFromAppliedOperations(IDiagramItemRO tofind, List<DiagramApplyOperation> applyOperations, int currentApplyIndex) {
		for (int i = currentApplyIndex - 1; i >= 0; --i) {
			DiagramApplyOperation ap = applyOperations.get(i);
			for (IDiagramItemRO previousStateCandidate : ap.getItems()) {
				if (tofind.getClientId().equals(previousStateCandidate.getClientId())) {
					return previousStateCandidate;
				}
			}
		}
		return null;
	}

	public void testCompensateApplyOperationsModify() throws MappingNotFoundException {
		OTCompensationTransformer t = new OTCompensationTransformer();
		t.setTestMode(true);
		currentState = TestUtils.generateDiagramItems(7);
		List<DiagramApplyOperation> applyOperations = generateApplyOperations(new OTOperation[]{OTOperation.MODIFY}, 5);
		
		// add extra apply operation manually and check that it works as planned; item is found from apply operations not from the server document
		List<IDiagramItemRO> shouldBeFound = TestUtils.generateDiagramItems(1); // client id 0000
		DiagramApplyOperation previousApplyOperationState = new DiagramApplyOperation(OTOperation.INSERT, shouldBeFound);
		// add it as earlier operation => first on the list so that it is applied earlier, so it is the previous state
		IDiagramItemRO expectedUndoState = shouldBeFound.get(0);
		applyOperations.add(0, previousApplyOperationState);

		List<CompensationModel> models = t.compensateApplyOperations(applyOperations, currentState);
		
		// check that manually added apply operation state is found as undo state
		int indexOfModifyOperation = 1;
		int firstDiagramItemShouldHaveClientId0000 = 0;
		IDiagramItemRO actualUndoState = models.get(indexOfModifyOperation).undoJson.get(firstDiagramItemShouldHaveClientId0000);
		assertNotSame("actual undo state should not be the same instance", expectedUndoState, actualUndoState);
		assertEquals("expected undo state should equal with actual", expectedUndoState, actualUndoState);

		assertEquals("there should be the same amount of compensations as there are apply operations", applyOperations.size(), models.size());
		compareApplyOperationsToCompensatedModels(applyOperations, models);

//		assertTrue("should be compensated to MODIFY", OTOperation.MODIFY == models.get(0).undoOperation);
//		assertEquals("there should be undo state for each item", applyOperations.get(0).getItems().size(), models.get(0).undoJson.size());
//		assertNotSame("compensation model need to have copied diagram item", currentState.get(0), models.get(0).undoJson.get(0));
//		assertEquals("client id should match", applyOperations.get(0).getItems().get(0).getClientId(), models.get(0).undoJson.get(0).getClientId());
//		assertEquals("current item state should be the undo item state, as copied", currentState.get(0), models.get(0).undoJson.get(0));
//		System.out.println(SLogger.format("current state {}, \nnew state {}", currentState.get(0).toString(), applyOperations.get(0).getItems().get(0).toString()));
//		assertFalse("new and prev state should not be the same", currentState.get(0).equals(applyOperations.get(0).getItems().get(0)));
	}


}
