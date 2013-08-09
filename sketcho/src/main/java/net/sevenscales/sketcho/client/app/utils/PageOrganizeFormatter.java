package net.sevenscales.sketcho.client.app.utils;

import java.util.HashMap;
import java.util.Map;

import net.sevenscales.domain.api.IPage;
import net.sevenscales.domain.utils.PageIterator;
import net.sevenscales.plugin.api.Context;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;


public class PageOrganizeFormatter extends SimplePanel implements PageIterator.IteratorCallback {
    private Map<Integer,PageOrganizeHandle> columns = new HashMap<Integer, PageOrganizeHandle>(); 
    private HorizontalPanel verticalPanel = new HorizontalPanel();
    private PickupDragController widgetDragController;
    private Context context;
    private PageOrganizeHandle rootPageHandle;
    
    public PageOrganizeFormatter(PickupDragController widgetDragController, Context context) {
      this.widgetDragController = widgetDragController;
      this.context = context;
      setWidget(verticalPanel);
      setStyleName("sd-app-PageOrganizeFormatter");
    }

    public void iteration(IPage page, int level) {
//      System.out.println(page.getName()+level);

      PageOrganizeHandle pageHandle = new PageOrganizeHandle(page, widgetDragController, context);
      columns.put(level, pageHandle);

      PageOrganizeHandle prevLevelHandle = columns.get(level-1);
      if (prevLevelHandle != null) {
//        System.out.println("prevLevelHandle:"+prevLevelHandle.getPage().getName());
        prevLevelHandle.add(pageHandle);
      }

      if (verticalPanel.getWidgetCount() == 0) {
        this.rootPageHandle = pageHandle;
        rootPageHandle.getPageName().setText("Project Design Map");
        rootPageHandle.getPageName().addStyleName("sd-app-PageOrganizeFormatter-Root");
        rootPageHandle.getContainer().addStyleName("sd-app-PageOrganizeHandle-Root");
        verticalPanel.add(pageHandle.getContainer());
      }
    }
    
    public void clear() {
      verticalPanel.clear();
      rootPageHandle = null;
      setWidget(verticalPanel);
    }

    public PageOrganizeHandle getRootPageHandle() {
      return rootPageHandle;
    }
}

