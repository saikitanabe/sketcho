package net.sevenscales.editor.diagram.utils;

import java.util.TreeSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.sevenscales.editor.uicomponents.uml.CommentElement;

/**
 * Ordered comment list.
 */
public class CommentList extends TreeSet<CommentElement> {
  public static final CommentSorter COMMENT_COMPARATOR = new CommentSorter();

  public CommentList() {
    super(COMMENT_COMPARATOR);
  }

  private static class CommentSorter implements Comparator<CommentElement> {
    @Override
    public int compare(CommentElement c1, CommentElement c2) {
      double result = c1.getJsComment().getCreatedAt() - c2.getJsComment().getCreatedAt();
      if (result == 0) {
        return 0;
      } else if (result > 0) {
        return 1;
      } 
      return -1;
    }
  }

}
