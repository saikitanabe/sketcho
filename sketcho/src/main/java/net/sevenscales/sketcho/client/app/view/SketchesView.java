package net.sevenscales.sketcho.client.app.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.sevenscales.appFrame.api.IContributor;
import net.sevenscales.appFrame.impl.DynamicParams;
import net.sevenscales.appFrame.impl.IController;
import net.sevenscales.appFrame.impl.ITilesEngine;
import net.sevenscales.appFrame.impl.LinkAction;
import net.sevenscales.appFrame.impl.View;
import net.sevenscales.appFrame.impl.uicomponents.ListUiHelper.LabelDropHandler;
import net.sevenscales.domain.api.IPage;
import net.sevenscales.domain.api.IPageWithNamedContentValues;
import net.sevenscales.domain.api.IProject;
import net.sevenscales.domain.api.SketchesSearch;
import net.sevenscales.plugin.api.Context;
import net.sevenscales.plugin.api.IPermissionContributor;
import net.sevenscales.plugin.api.SketchUiList;
import net.sevenscales.plugin.constants.TileId;
import net.sevenscales.sketcho.client.uicomponents.LabelWidget;
import net.sevenscales.sketcho.client.uicomponents.LabelWidget.LabelClickHandler;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SketchesView extends View<Context> /*implements ChangeListener*/ {
	private SketchUiList listContent;
	private VerticalPanel menuItems;
	private VerticalPanel commandArea;
	private Tree pages;
//  private Widget newPage;
  private IProject project;
  private LinkAction currentLocation;
  private HorizontalPanel hierarchy;
  private HorizontalPanel buttons;
  private ListBox searchIssues;
  private ICommandCallback callback;
  private ListBox sortLabels;
  
  public interface ICommandCallback extends LabelDropHandler, LabelClickHandler {
    void search(String filter, String sort);
    void sortByLabels();
  }


	public SketchesView(IController<Context> controller, ICommandCallback callback) {
		super(controller);
		this.callback = callback;
		
		// construct body
//		listContent.addStyleName("sd-app-SketchesView-ListSelectionComponent");
		listContent = new SketchUiList();
		listContent.setLabelDropHandler(callback);
		controller.getContext().getEventRegistry().registerLabelsDropTarget(listContent);
//		listContent.setStyleName("content-Area");
		
		// command area
		commandArea = new VerticalPanel();
		commandArea.setStyleName("command-Area");
//		commandArea.setHeight("25px");
		
		this.hierarchy = new HorizontalPanel();
    hierarchy.setStyleName("page-link-hierarchy");

    HTML currentLocation = new HTML("Sketches");
    currentLocation.setStyleName("page-Hierarchy");
    hierarchy.add(currentLocation);
		
		// back link
//		Map<Object, String> requests = new HashMap<Object, String>();
//		requests.put(RequestId.CONTROLLER, RequestValue.PROJECTS_CONTROLLER);
//		commandArea.add(ActionFactory
//			.createLinkAction("&laquo; back to projects", requests)
//			.getWidget());

		// new page
//		this.newPage = ActionFactory
//      .createButtonAction("New Sketch", ActionId.NEW_TICKET, controller)
//      .getWidget();

		this.buttons = new HorizontalPanel();
    buttons.addStyleName("command-area-buttons");
//    buttons.add(newPage);

    this.searchIssues = new ListBox();
    searchIssues.addItem(SketchesSearch.TEXT_OPEN_SKETCHES);
    searchIssues.addItem(SketchesSearch.TEXT_ALL_SKETCHES);
    searchIssues.addChangeHandler(new ChangeHandler() {
      public void onChange(ChangeEvent event) {
        SketchesView.this.callback.search(
            searchIssues.getItemText(searchIssues.getSelectedIndex()),
            sortLabels.getItemText(sortLabels.getSelectedIndex()));
      }
    });

    this.sortLabels = new ListBox();
    sortLabels.addItem(SketchesSearch.TEXT_SORT_BY_MODIFIED);
    sortLabels.addItem(SketchesSearch.TEXT_SORT_BY_LABELS);
    sortLabels.addChangeHandler(new ChangeHandler() {
      public void onChange(ChangeEvent event) {
        SketchesView.this.callback.search(
            searchIssues.getItemText(searchIssues.getSelectedIndex()),
            sortLabels.getItemText(sortLabels.getSelectedIndex()));
      }
    });
    
    buttons.add(searchIssues);
    buttons.add(sortLabels);

		commandArea.add(hierarchy);
    commandArea.add(buttons);

		pages = new Tree();
	}

	public void activate(ITilesEngine tilesEngine,
						 DynamicParams params, IContributor contributor) {
//		IProject project = (IProject) params.getParam(ParamId.PROJECT_PARAM);

		tilesEngine.setTile(TileId.COMMAND_AREA, commandArea);
		
    IPermissionContributor permissionContributor = controller.getContext().getContributor().cast(
        IPermissionContributor.class);
//    newPage.setVisible(permissionContributor.hasEditPermission());
		
//    listContent.clear();
    
    tilesEngine.setTile(TileId.CONTENT, listContent);
	}

//  public void setSketches(List<IPage> sketches) {
//    List<ListItemData> items = new ArrayList<ListItemData>();
////    if (sketches != null) {
//      for (IPage page : sketches) {
//        ListItemData lid = new ListItemData();
//        Map<Object, Object> requests = new HashMap<Object, Object>();
//        requests.put(RequestId.CONTROLLER, RequestValue.SKETCH_CONTROLLER);
//        requests.put(RequestId.PROJECT_ID, page.getProject().getId());
//        requests.put(RequestId.PAGE_ID, page.getId());
//        
//        String name = PageUtil.getContentTextValue(page, "Title");
//        lid.action = ActionFactory.createLinkAction(name, requests);     
//        lid.action.setData(page);
//        
//        lid.others = new ArrayList<Widget>();
//
//        lid.others.add(new HTML("statea"/*t.getState()*/));
//
////        Date date = new Date(t.getModifiedTime());
////        lid.others.add(new HTML(date.toLocaleString()));
////
////        date = new Date(t.getCreatedTime());
////        lid.others.add(new HTML(date.toLocaleString()));
//
////        lid.others.add(new HTML(t.getModifier()));
//        items.add(lid);
//      }
////    }
//
//    listContent.setListItems(items);
//  }

  public void setProject(IProject project) {
    this.project = project;
  }
  
  public void setSketches(List<IPageWithNamedContentValues> result) {
    clear();
    if (result.size() == 0) {
      
    }
    for (IPageWithNamedContentValues r : result) {
      addSketch(r.getPage(), r.getNamedContentValues());
    }
  }
  public void addSketch(IPage sketch, Map<String, String> namedItems) {
    List<Widget> columns = createColumns(sketch, namedItems);
    String stateValue = namedItems.get("Status");
    String style = "";
    if (stateValue != null) {
      if (stateValue.equals("New")) {
        columns.get(0).addStyleName("sd-app-SketchesView-SketchNew");
  //      style = "sd-app-SketchesView-SketchNew";
      } else if (stateValue.equals("Started")) {
  //      columns.get(0).addStyleName("sd-app-SketchesView-SketchStarted");
        style = "sd-app-SketchesView-SketchStarted";
      } else if (stateValue.equals("Accepted")) {
  //      columns.get(0).addStyleName("sd-app-SketchesView-SketchAccepted");
        style = "sd-app-SketchesView-SketchAccepted";
      } else if (stateValue.equals("Designed")) {
        style = "sd-app-SketchesView-SketchDesigned";
      } else if (stateValue.equals("Future")) {
        style = "sd-app-SketchesView-SketchFuture";
      } else if (stateValue.equals("Discarded")) {
        style = "sd-app-SketchesView-SketchDiscarded";
      }
    }

    listContent.addRow(columns, sketch, style);
  }
  
  private List<Widget> createColumns(IPage sketch, Map<String, String> namedItems) {
    List<Widget> columns = new ArrayList<Widget>();
    
//    HorizontalPanel titleColumn = new HorizontalPanel();
//    titleColumn.getElement().getStyle().setProperty("border", "none");

//    titleColumn.setWidth("100%");
//    titleColumn.setStyleName("empty-borders");
    HTML title = new HTML();
    title.setText(sketch.getName());
    columns.add(title);
//    titleColumn.add(title);
    
    LabelWidget lw = new LabelWidget(sketch.getLabels(), sketch, callback);
    columns.add(lw);
//    titleColumn.add(lw);
//    titleColumn.setCellHorizontalAlignment(lw, HorizontalPanel.ALIGN_RIGHT);
//    titleColumn.setCellVerticalAlignment(lw, HorizontalPanel.ALIGN_MIDDLE);
//    columns.add(titleColumn);
    
    HTML state = new HTML();
    state.setText(namedItems.get("Status"));
    columns.add(state);
    
//    HTML desc = new HTML();
//    String d = namedItems.get("Description");
//    if (d != null) {
//      // cleanup output
//      d = d.replaceAll("\\<.*?>", "");
//      
//      // line breaks are not in one line on IE => needs to be stripped out
//      // remove both windows and unix line breaks
//      String windowsLineBreak = "\r\n";
//      String unixLineBreak = "\n";
//      
//      boolean startsWithLineBreak = d.indexOf(windowsLineBreak) == 0;
//      if (!startsWithLineBreak) {
//    	  startsWithLineBreak = d.indexOf(unixLineBreak) == 0 ? true : false;
//      }
//      
//      d = d.replaceAll(windowsLineBreak, "... "); 
//      d = d.replaceAll(unixLineBreak, "... ");
//      
////      String lineBreak = UiUtils.isIE() ? "\r\n" : "\n";
////      boolean startsWithLineBreak = d.indexOf(lineBreak) == 0;
////      d = d.replaceAll(lineBreak,"... ");
//      if (startsWithLineBreak) {
//        d = d.replaceFirst("... ", "");
//      }
//    }
//    desc.setText(d);
//    desc.addStyleName("sd-app-SketchesView-Description");
//    columns.add(desc);
    
    Date date = new Date(sketch.getModifiedTime());
    DateTimeFormat dtf = DateTimeFormat.getMediumDateFormat();
    
    HTML dateHtml = new HTML();
    dateHtml.setText(dtf.format(date));
    columns.add(dateHtml);

//    date = new Date(sketch.getCreatedTime());
//    HTML createdTime = new HTML();
//    createdTime.setText(dtf.format(date));
//    columns.add(createdTime);

    HTML modifier = new HTML();
    modifier.setText(sketch.getModifier());
    columns.add(modifier);
    
    return columns;
  }

  public void clear() {
    listContent.clear();    
  }

  public void fillSketch(IPage sketch, Map<String, String> namedItems) {
//    ListItemData lid = new ListItemData();
//    Map<Object, Object> requests = new HashMap<Object, Object>();
//    requests.put(RequestId.CONTROLLER, RequestValue.SKETCH_CONTROLLER);
//    requests.put(RequestId.PROJECT_ID, sketch.getProject().getId());
//    requests.put(RequestId.PAGE_ID, sketch.getId());
//    
//    lid.action = ActionFactory.createLinkAction(sketch.getId().toString(), requests);
//    lid.action.setData(sketch);
//    
//    lid.others = new ArrayList<Widget>();
//    
//    String title = namedItems.get("Title");
//    lid.others.add(ActionFactory.createLinkAction(title, requests));
//    String state = namedItems.get("State");
//    lid.others.add(new HTML(state));
//    String desc = namedItems.get("Description");
//    
//    if (desc != null && desc.length() > 80) {
//      desc = desc.substring(0, 80) + "...";
//    }
//    HTML descHtml = new HTML();
//    descHtml.setText(desc);
//    lid.others.add(descHtml);
    
    List<Widget> columns = createColumns(sketch, namedItems);
    listContent.replaceRow(columns, sketch);
  }

  public String getFilterOption() {
    return searchIssues.getItemText(searchIssues.getSelectedIndex());
  }

  public String getSortOption() {
    return sortLabels.getItemText(sortLabels.getSelectedIndex());
  }
  
}
