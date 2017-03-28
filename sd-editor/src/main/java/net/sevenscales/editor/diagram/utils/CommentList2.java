package net.sevenscales.editor.diagram.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import net.sevenscales.domain.CommentDTO;
import net.sevenscales.editor.uicomponents.uml.CommentElement;


/**
 * Ordered comment list.
 */
public class CommentList2 extends ArrayList<CommentElement> {
  public static final CommentSorter COMMENT_COMPARATOR = new CommentSorter();

  // public CommentList() {
  //   super(COMMENT_COMPARATOR);
  // }

  private static class CommentSorter implements Comparator<CommentElement> {
    @Override
    public int compare(CommentElement c1, CommentElement c2) {
      // if (BoardDocumentHelpers.DIAGRAM_IDENTIFIER_COMPARATOR(c1, c2) == 0) {
      //   return 0;
      // }
      if (c1 == c2) {
        return 0;
      }

      CommentDTO i1 = (CommentDTO) c1.getDiagramItem();
      CommentDTO i2 = (CommentDTO) c2.getDiagramItem();

      double cat1 = i1.getCreatedAt();
      double cat2 = i2.getCreatedAt();
      if (cat1 == 0) {
        return -1;
      }

      if (cat2 == 0) {
        return -1;
      }

      double result = cat1 - cat2;
      if (result == 0) {
        return 0;
      } else if (result > 0) {
        return 1;
      } 
      return -1;
    }
  }

  public boolean add(CommentElement comment) {
    int index = binarySearch(comment);
    if (index < 0) {
      index = ~index;
      add(index, comment);
      return true;
    }
    return false;
  }

  public int binarySearch(CommentElement comment) {
    return Collections.binarySearch(this, comment, COMMENT_COMPARATOR);
  }

}
