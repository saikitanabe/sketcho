package net.sevenscales.editor.api.ot;

import java.util.ArrayList;
import java.util.List;

import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.domain.IDiagramItemRO;

public class TestUtils {
	private static final String[] ELEMENT_ITEMS = {"ellipseitem","sequenceitem", "comp", "server"};
	private static final String[] TEXTS = {"Fast", "forward", "to", "today.", "The", "pace", "of", "innovation", "has", "never", "been", "greater,", "and", 
																					"Android", "is", "the", "most", "used", "mobile", "operating", "system", "in", "the", "world:", "we", "have", "a", 
																					"global", "partnership", "of", "over", "60", "manufacturers;", "more", "than", "750", "million", "devices", 
																					"have", "been", "activated", "globally;", "and", "25", "billion", "apps", "have", "now", "been", "downloaded", 
																					"from", "Google", "Play.", "Pretty", "extraordinary", "progress", "for", "a", "decade’s", "work.", "Having", 
																					"exceeded", "even", "the", "crazy", "ambitious", "goals", "we", "dreamed", "of", "for", "Android—and", "with", 
																					"a", "really", "strong", "leadership", "team", "in", "place—Andy’s", "decided", "it’s", "time", "to", "hand", 
																					"over", "the", "reins", "and", "start", "a", "new", "chapter", "at", "Google.", "Andy,", "more", "moonshots", "please!"};

	public static List<IDiagramItemRO> generateDiagramItems(int n) {
		List<IDiagramItemRO> result = new ArrayList<IDiagramItemRO>();
		for (int i = 0; i < n; ++i) {
			String id = generateId(i * 2); // only even IDs are used
			DiagramItemDTO di = new DiagramItemDTO(generateText(), 
					 ELEMENT_ITEMS[(int)(Math.random() * (ELEMENT_ITEMS.length - 1))], 
					 "530,115,63,19", 
					 /*extension*/ null,
					 "204,204,255,0:51,51,51,1",
					 "68,68,68,1", 
					 /*fontSize*/ null,
					 /*shapeProperties*/ null,
					 /*displayOrder*/ null,
					 1, 
					 1L, 
					 id,
					 /*customData*/null,
					 /*links*/null,
					 /*parentId*/null);
			result.add(di);
			System.out.println(di);
		}
		return result;
	}
	
	public static String generateId(int n) {
		return String.format("%05d", n);
	}
	
	public static String generateText() {
		String result = "";
		int wordCount = (int) (Math.random() * 10) + 1;
		for (int i = 0; i < wordCount; ++i) {
			result += TEXTS[(int)(Math.random() * (TEXTS.length - 1))] + " ";
		}
		return result;
	}

}
