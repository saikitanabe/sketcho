package net.sevenscales.sketcho.client.app.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sevenscales.appFrame.api.IContributor;
import net.sevenscales.appFrame.impl.ActionFactory;
import net.sevenscales.appFrame.impl.DynamicParams;
import net.sevenscales.appFrame.impl.IController;
import net.sevenscales.appFrame.impl.ITilesEngine;
import net.sevenscales.appFrame.impl.LinkAction;
import net.sevenscales.appFrame.impl.View;
import net.sevenscales.appFrame.impl.uicomponents.ListSelectionComponent;
import net.sevenscales.domain.api.IProject;
import net.sevenscales.domain.utils.PageIterator;
import net.sevenscales.plugin.api.Context;
import net.sevenscales.plugin.api.IPermissionContributor;
import net.sevenscales.plugin.constants.ActionId;
import net.sevenscales.plugin.constants.RequestId;
import net.sevenscales.plugin.constants.RequestValue;
import net.sevenscales.plugin.constants.TileId;
import net.sevenscales.sketcho.client.app.utils.PageOrganizeFormatter;
import net.sevenscales.sketcho.client.app.utils.PageOrganizeHandle;
import net.sevenscales.sketcho.client.app.view.constants.ParamId;

import com.allen_sauer.gwt.dnd.client.DragHandler;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ProjectView extends View<Context> /*implements ChangeListener*/ {
//	private ListSelectionComponent listContent;
	private HTML title;
	private VerticalPanel commandArea;
//	private Tree pages;
  private Widget newPage;
  private IProject project;
  private HorizontalPanel hierarchy;
  private HorizontalPanel buttons;
  private DragHandler dragHandler;
  private PageOrganizeFormatter organizer;
  private AbsolutePanel boundaryPanel;
  private LinkAction documentation;

	public ProjectView(IController<Context> controller, DragHandler dragHandler) {
		super(controller);
		this.dragHandler = dragHandler;
		
		// construct body
//		listContent = new ListSelectionComponent();
//		listContent.setStyleName("content-Area");
		
		// title
		title = new HTML();
		title.setStyleName("title-text");
		
		// command area
		commandArea = new VerticalPanel();
		commandArea.setStyleName("command-Area");
//		commandArea.setHeight("25px");
		
    this.hierarchy = new HorizontalPanel();
    hierarchy.setStyleName("page-link-hierarchy");
    
    Map<Object, Object> requests = new HashMap<Object, Object>();
    this.documentation = (LinkAction) ActionFactory
      .createLinkAction("Design &laquo;", requests);
    documentation.setStyleName("page-Hierarchy");
    hierarchy.add(documentation);

    HTML h = new HTML("Organize");
    h.setStyleName("page-Hierarchy");
    hierarchy.add(h);

    this.buttons = new HorizontalPanel();
    buttons.addStyleName("command-area-buttons");

		// back link
//		Map requests = new HashMap();
//		requests.put(RequestId.CONTROLLER, RequestValue.PROJECTS_CONTROLLER);
//		commandArea.add(ActionFactory
//			.createLinkAction("&laquo; back to projects", requests)
//			.getWidget());

		// new page
		this.newPage = ActionFactory
      .createButtonAction("New Page", ActionId.NEW_PAGE, controller)
      .getWidget();

		buttons.add(newPage);
		
		commandArea.add(hierarchy);
		commandArea.add(buttons);

//		pages = new Tree();
		
    this.boundaryPanel = new AbsolutePanel();
    boundaryPanel.setSize("100%", "100%");
	}
	
	public void activate(ITilesEngine tilesEngine,
						 DynamicParams params, IContributor contributor) {
		this.project = (IProject) params.getParam(ParamId.PROJECT_PARAM);
		
    title.setHTML(project.getName() + " - Organize");
		tilesEngine.setTile(TileId.TITLE, title);
		tilesEngine.setTile(TileId.COMMAND_AREA, commandArea);
		
    Map<Object, Object> requests = new HashMap<Object, Object>();
    requests.put(RequestId.CONTROLLER, RequestValue.PROJECT_ARCHITECTURE_CONTROLLER);
    requests.put(RequestId.PROJECT_ID, controller.getContext().getProjectId());    
    documentation.setRequest(requests);
		
    IPermissionContributor permissionContributor = controller.getContext().getContributor().cast(
        IPermissionContributor.class);
    newPage.setVisible(permissionContributor.hasEditPermission());
		
//		listContent.clear();
		
    PickupDragController widgetDragController = new PickupDragController(boundaryPanel, false);
    widgetDragController.setBehaviorMultipleSelection(false);
    widgetDragController.addDragHandler(dragHandler);

    this.organizer = new PageOrganizeFormatter(widgetDragController, controller.getContext());

//		organizer.clear();
    PageIterator i = new PageIterator(project.getDashboard(), organizer);
    i.iterate();
    boundaryPanel.clear();
    boundaryPanel.add(organizer);

    tilesEngine.setTile(TileId.CONTENT, boundaryPanel);

//    pages.clear();
//    PageIterator pi = new PageIterator(project.getDashboard(), new PageListFormatter(project, pages, new INameFormatter() {
//      public Widget format(IPage page) {
//        Map<Object, Object> requests = new HashMap<Object, Object>();
//        requests.put(RequestId.CONTROLLER, RequestValue.PAGE_CONTROLLER);
//        requests.put(RequestId.PAGE_ID, page.getId());
//        requests.put(RequestId.PROJECT_ID, project.getId());
//        
//        Grid p = new Grid(1, 3);
//        p.setWidth("100%");
//        p.addStyleName("debug");
//        
//        p.setWidget(0, 0, ActionFactory.createLinkAction(page.getName(), requests));
//        
//        ListBoxMap<IPage> parentList = new ListBoxMap<IPage>();
//        new ParentSelectionUtil(project, page, parentList);
//        p.setWidget(0, 1, parentList);
//        
//        ListBox orderValues = new ListBox();
//        refreshOrderValues(page, orderValues);
//        p.setWidget(0, 2, orderValues);
//        return p;
//      }
//      public void refreshOrderValues(IPage currentPage, ListBox orderValues) {
//        if (currentPage != null && currentPage.getParent() != null) {
//          for (int i = 1; i <= currentPage.getParent().getSubpages().size(); ++i) {
//            orderValues.addItem(String.valueOf(i));
//          }
//          // if parent changed set order as last
//          int index = currentPage.getOrderValue() - 1;
//          int itemCount = currentPage.getParent().getSubpages().size();
//          index = index >=  itemCount ? itemCount - 1 : index; 
//          orderValues.setSelectedIndex(index);
//        }
//      }
//
//    }));
//    pi.iterate();
//
//    Iterator<TreeItem> i = pages.treeItemIterator();
//    while (i.hasNext()) {
//      i.next().setState(true);
//    }
//		pages.setStyleName("content-Area");
//		tilesEngine.setTile(TileId.CONTENT, pages);
	}

//	public List getSelectedPages() {
//		return listContent.getSelectedItems();
//	}
	
	public PageOrganizeHandle getRootOrganizeHandle() {
	  return organizer.getRootPageHandle();
	}
}
