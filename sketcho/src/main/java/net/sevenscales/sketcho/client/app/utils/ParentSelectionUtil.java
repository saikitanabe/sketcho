package net.sevenscales.sketcho.client.app.utils;

import net.sevenscales.domain.api.IPage;
import net.sevenscales.domain.api.IProject;
import net.sevenscales.domain.utils.PageIterator;
import net.sevenscales.sketcho.client.uicomponents.ListBoxMap;

public class ParentSelectionUtil {
  private IPage currentPage;
  private ListBoxMap<IPage> parentList;

  public ParentSelectionUtil(IProject project, IPage current, ListBoxMap<IPage> parentPageList) {
    this.currentPage = current;
    this.parentList = parentPageList;
    PageIterator pi = new PageIterator(project.getDashboard(), new PageIterator.IteratorCallback() {
      public void iteration(IPage page, int level) {
        // some indentation for different levels
        String indent = "";
        for (int i = 0; i < level; ++i) {
          indent += intentString(level);
        }
    
        boolean focus = false;
        if (currentPage != null && page.equals(currentPage.getParent())) {
          focus = true;
        }
        
        boolean filter = false;
        if ( (currentPage != null && currentPage.equals(page)) || isParent(currentPage, page)) {
          // currently can't move under itself, its children and sub children
          filter = true;
        }
        
        if (!filter) {
          parentList.addItem(indent + " " + page.getName(), page, focus);
        }
      }
    
      private boolean isParent(IPage currentPage, IPage page) {
        while (page.getParent() != null) {
          if (page.getParent().equals(currentPage)) {
            return true;
          }
          page = page.getParent();
        }
        return false;
      }
    });
    pi.iterate();
    
  }
  
  // only way to add indentation to select box using javascript
  private native String intentString(int size)/*-{
    return String.fromCharCode(160, 160, 160, 160);
  }-*/;

}

