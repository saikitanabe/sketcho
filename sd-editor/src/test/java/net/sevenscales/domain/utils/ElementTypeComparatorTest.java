package net.sevenscales.domain.utils;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import junit.framework.TestCase;

import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.domain.ElementType;


public class ElementTypeComparatorTest extends TestCase {
  public void test1() {
    List<IDiagramItemRO> list = new ArrayList<IDiagramItemRO>();
    list.add(createDTO("5", ElementType.CHILD_TEXT));
    list.add(createDTO("2", ElementType.CHILD_TEXT));
    list.add(createDTO("1", ElementType.RELATIONSHIP));
    list.add(createDTO("4", ElementType.NOTE));
    list.add(createDTO("6", ElementType.ELLIPSE));
    list.add(createDTO("3", ElementType.ELLIPSE));

    Collections.sort(list, new ElementTypeComparator.DiagramItemComparator());

    for (IDiagramItemRO diro : list) {
        System.out.println("diro.getType(): " + diro.getType() + " diro.getClientId(): " + diro.getClientId());
    }
    
    assertTrue("wrong amount of items", list.size() == 6);
    assertTrue("normal element should be before relationships and child elements", "3".equals(list.get(0).getClientId()));
    assertTrue("normal element should be before relationships and child elements", "4".equals(list.get(1).getClientId()));
    assertTrue("normal element should be before relationships and child elements", "6".equals(list.get(2).getClientId()));
    assertTrue("relationship should be after normal elements", "1".equals(list.get(3).getClientId()));
    assertTrue("child text should be after relationship", "2".equals(list.get(4).getClientId()));
    assertTrue("child text should be after relationship", "5".equals(list.get(5).getClientId()));
  }

  private DiagramItemDTO createDTO(String clientId, ElementType type) {
    DiagramItemDTO result = new DiagramItemDTO(clientId);
    result.setType(type.getValue());
    return result;
  }
}