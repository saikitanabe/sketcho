package net.sevenscales.editor.api.ot;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.domain.IDiagramItemRO;

public class BoardDocumentTest extends TestCase {
	private List<IDiagramItemRO> original1Item;

	public BoardDocumentTest() {
	}
	
	private BoardDocument create1ItemBaseDoc() {
		this.original1Item = new ArrayList<IDiagramItemRO>();
		original1Item.add(new DiagramItemDTO("Use Case", 
															 "ellipseitem", 
															 "530,115,63,19", 
															 "204,204,255,0:51,51,51,1",
															 "68,68,68,1", 
															 1, 
															 1L, 
															 "1", 
															 "", 0f));

		return new BoardDocument(original1Item, "Test 1 Item Document");
	}
	
	private BoardDocument createNItemBaseDoc(int n) {
		this.original1Item = TestUtils.generateDiagramItems(n);

		return new BoardDocument(original1Item, "Test N Item Document");
	}
	
	public void testCopied() {
		BoardDocument doc = createNItemBaseDoc(100);
		assertEquals("copied size should same", 100, doc.size());
		int i = 0;
		for (IDiagramItemRO di : doc.getDocument()) {
			IDiagramItemRO expected = original1Item.get(i++);
			assertNotSame("object should be copied and not referenced", expected, di);
			assertEquals("should be having exactly same content", expected, di);
		}
	}
	
	public void testInsert1() {
		BoardDocument doc = createNItemBaseDoc(100);

		List<IDiagramItemRO> insert = new ArrayList<IDiagramItemRO>();
		insert.add(new DiagramItemDTO("Insert Me", 
															 "ellipseitem", 
															 "530,115,63,19", 
															 "204,204,255,0:51,51,51,1",
															 "68,68,68,1", 
															 1, 
															 2L, 
															 TestUtils.generateId(1), 
															 "", 
															 0f));
		doc.apply(OTOperation.INSERT, insert);
		
		assertEquals("after insert there should be 2 items", 101, doc.size());
		assertEquals("insert me should be second in the doc", TestUtils.generateId(1), doc.getDocument().get(1).getClientId());
		assertEquals("wrong text, see insert list", "Insert Me", doc.getDocument().get(1).getText());
	}
	
	public void testDelete1() {
		BoardDocument doc = createNItemBaseDoc(100);
		List<IDiagramItemRO> delete = new ArrayList<IDiagramItemRO>();
		delete.add(new DiagramItemDTO(TestUtils.generateId(2)));
		delete.add(new DiagramItemDTO(TestUtils.generateId(4)));
		doc.apply(OTOperation.DELETE, delete);
		
		assertEquals("wrong number of items left", 98, doc.size());
	}
	
	public void testModify1() {
		BoardDocument doc = createNItemBaseDoc(100);

		List<IDiagramItemRO> modify = new ArrayList<IDiagramItemRO>();
		modify.add(new DiagramItemDTO("Modify Me", 
															 "ellipseitem", 
															 "530,115,63,19", 
															 "204,204,255,0:51,51,51,1",
															 "68,68,68,1", 
															 1, 
															 2L, 
															 TestUtils.generateId(2), 
															 "",
															 0f));
		doc.apply(OTOperation.MODIFY, modify);
		
		assertEquals("wrong text", "Modify Me", doc.getDocument().get(1).getText());
	}
	
	public void testIsUnique() {
		BoardDocument doc = createNItemBaseDoc(100);

		List<IDiagramItemRO> modify = new ArrayList<IDiagramItemRO>();
		modify.add(new DiagramItemDTO("Modify Me", 
															 "ellipseitem", 
															 "530,115,63,19", 
															 "204,204,255,0:51,51,51,1",
															 "68,68,68,1", 
															 1, 
															 2L, 
															 TestUtils.generateId(2), 
															 "",
															 0f));
		doc.apply(OTOperation.MODIFY, modify);
		
		assertTrue("ID is not unique", doc.isUnique(TestUtils.generateId(1)));
		assertTrue("ID should be existing", !doc.isUnique(TestUtils.generateId(2)));
	}


}
