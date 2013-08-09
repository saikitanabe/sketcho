package net.sevenscales.sketcho.client.app.view;

import java.util.ArrayList;
import java.util.List;

import net.sevenscales.appFrame.api.IContributor;
import net.sevenscales.appFrame.impl.ActionFactory;
import net.sevenscales.appFrame.impl.DynamicParams;
import net.sevenscales.appFrame.impl.IController;
import net.sevenscales.appFrame.impl.ITilesEngine;
import net.sevenscales.appFrame.impl.View;
import net.sevenscales.appFrame.impl.uicomponents.ListUiHelper;
import net.sevenscales.domain.api.IProject;
import net.sevenscales.plugin.api.Context;
import net.sevenscales.plugin.api.IPermissionContributor;
import net.sevenscales.plugin.constants.ActionId;
import net.sevenscales.plugin.constants.TileId;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ProjectsView extends View<Context> {
	private HTML title;
	private HorizontalPanel commandArea;
//	private ListSelectionComponent listContent;
  private ListUiHelper<IProject> listContent;

  private Widget newProject;
//  private Widget deleteProject;
  private ITilesEngine tilesEngine;
  private HorizontalPanel buttons;

	public ProjectsView(IController<Context> controller) {
		super(controller);

		// construct body
		// construct body
		listContent = new ListUiHelper<IProject>();
//		listContent.setStyleName("content-Area");
		
		// title
		title = new HTML("Projects");
		title.setStyleName("title-text");

		// construct menu
		commandArea = new HorizontalPanel();
		commandArea.setStyleName("command-Area");
		
		this.buttons = new HorizontalPanel();
		buttons.addStyleName("command-area-buttons");
		
		this.newProject = ActionFactory
      .createButtonAction("New Project", ActionId.NEW_PROJECT, controller)
      .getWidget();
//		this.deleteProject = ActionFactory
//      .createButtonAction("Delete Project", ActionId.DELETE_PROJECT, controller)
//      .getWidget();
		
		buttons.add(newProject);
		
		commandArea.add(buttons);
	}

	public void activate(ITilesEngine tilesEngine,
						 DynamicParams params, IContributor contributor) {
	  this.tilesEngine = tilesEngine;
		tilesEngine.setTile(TileId.TITLE, title);
		tilesEngine.setTile(TileId.COMMAND_AREA, commandArea);
		tilesEngine.setTile(TileId.FOOTER, null);
		
    IPermissionContributor permissionContributor = controller.getContext().getContributor().cast(
        IPermissionContributor.class);
    newProject.setVisible(permissionContributor.hasCreatePermission());
    buttons.setVisible(true);
		
		tilesEngine.setTile(TileId.CONTENT, listContent);
	}

	public void setProjects(List<IProject> projects) {
    listContent.clear();
    List<String> header = new ArrayList<String>();
    header.add("Project");
    header.add("Description");
//    header.add("Modified");
//    header.add("Created");
//    header.add("Owner");
    listContent.setHeader(header);
    
//	  List<ListItemData> items = constructListItems(projects);
//		listContent.setListItems(items);
	  for (IProject p : projects) {
	    List<Widget> columns = createColumns(p);
	    listContent.addRow(columns, p);
	  }
	}

  private List<Widget> createColumns(IProject p) {
    List<Widget> result = new ArrayList<Widget>();
    HTML name = new HTML(p.getName());
    result.add(name);
    
    String desc = "";
    if (p.isPublicProject()) {
      // TODO: modify this later. Project needs to have description field in db. Get it there!
      desc = "This is an example project. It can be viewed, but not modified. Create your own project by pressing <b>New Project</b> when signed in.";
    }
    result.add(new HTML(desc));

    return result;
  }
  
  public void addClickHandler(ClickHandler handler) {
    listContent.addClickHandler(handler);
  }

  public IProject getDomainObject(ClickEvent event) {
    return listContent.getDomainObject(event);
  }


//  public void addProjects(List<IProject> projects) {
//	    List<ListItemData> items = constructListItems(projects);
//	    listContent.addListItems(items);
//	  }
//
//	 public List<ListItemData> constructListItems(List<IProject> projects) {
//	    List<ListItemData> items = new ArrayList<ListItemData>();
//	    Iterator<IProject> iter = projects.iterator();
//	    while (iter.hasNext()) {
//	      ListItemData lid = new ListItemData();
//	      IProject p = (IProject) iter.next();
//	      Map requests = new HashMap();
//	      requests.put(RequestId.CONTROLLER, RequestValue.PAGE_CONTROLLER);
//	      requests.put(RequestId.PROJECT_ID, p.getId());
//	      requests.put(RequestId.PAGE_ID, p.getDashboard().getId());
//	      
////	      requests.put(RequestId.CONTROLLER, RequestValue.PROJECT_CONTROLLER);
////	      requests.put(RequestId.PROJECT_ID, p.getId());
//	      lid.action = ActionFactory.createLinkAction(p.getName(), requests);
//	      lid.action.setData(p);
//	      items.add(lid);
//	    }
//	    
//	    return items;
//	  }

//	public ArrayList getSelectedProjects() {
//		return listContent.getSelectedItems();
//	}

}
