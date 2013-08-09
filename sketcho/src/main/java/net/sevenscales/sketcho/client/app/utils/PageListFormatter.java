package net.sevenscales.sketcho.client.app.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import net.sevenscales.domain.api.IPage;
import net.sevenscales.domain.api.IProject;
import net.sevenscales.domain.utils.PageIterator.IteratorCallback;

import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class PageListFormatter implements IteratorCallback {
  private Map<Integer, TreeItem> levelItems = new HashMap<Integer, TreeItem>();
  private IProject project;
  private Stack<Integer> numberings = new Stack<Integer>();
  private int prevLevel = -1;
  private Tree pages;
  private INameFormatter nameFormatter;
  
  public interface INameFormatter {
    public Widget format(IPage page, int level);
  }

  public PageListFormatter(IProject project, Tree pages, INameFormatter nameFormatter) {
    this.project = project;
    this.pages = pages;
    this.nameFormatter = nameFormatter;
  }

  public void iteration(IPage page, int level) {
    if (level > prevLevel) {
      numberings.push(new Integer(0));
    } else if (level < prevLevel) {
      numberings.pop();
    }
    
    Integer n = numberings.pop();
    n = n.intValue() + 1;
    numberings.add(n);
    
    Widget w = nameFormatter.format(page, level);
    prevLevel = level;
    
    if (w != null) {
      if (levelItems.get(level - 1) == null) {
        TreeItem ti = new TreeItem(w);
        pages.addItem(ti);
        levelItems.put(level, ti);
      } else {
        TreeItem ti = new TreeItem(w);
        levelItems.put(level, ti);
        levelItems.get(level - 1).addItem(ti);
      }
    }
  }
}
