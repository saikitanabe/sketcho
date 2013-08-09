package net.sevenscales.sketcho.client.app.view;

import java.util.HashMap;
import java.util.Map;

import net.sevenscales.appFrame.api.IContributor;
import net.sevenscales.appFrame.impl.Action;
import net.sevenscales.appFrame.impl.ActionFactory;
import net.sevenscales.appFrame.impl.ButtonAction;
import net.sevenscales.appFrame.impl.DynamicParams;
import net.sevenscales.appFrame.impl.IController;
import net.sevenscales.appFrame.impl.ITilesEngine;
import net.sevenscales.appFrame.impl.RequestUtils;
import net.sevenscales.appFrame.impl.View;
import net.sevenscales.domain.api.IPage;
import net.sevenscales.domain.api.IProject;
import net.sevenscales.domain.constants.Constants;
import net.sevenscales.plugin.api.Context;
import net.sevenscales.plugin.api.IPermissionContributor;
import net.sevenscales.plugin.constants.ActionId;
import net.sevenscales.plugin.constants.RequestId;
import net.sevenscales.plugin.constants.RequestValue;
import net.sevenscales.plugin.constants.Styles;
import net.sevenscales.plugin.constants.TileId;
import net.sevenscales.sketcho.client.app.utils.ParentSelectionUtil;
import net.sevenscales.sketcho.client.app.view.constants.ParamId;
import net.sevenscales.sketcho.client.uicomponents.ListBoxMap;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class NewPageView extends View<Context> implements ChangeListener {
	private TextBox pageNameField;
	private VerticalPanel body;
	private HTML title;
	private VerticalPanel commandArea;
  private ListBoxMap<IPage> parentPageList;
  private IPage currentPage;
  private ListBox orderValues;
  private PagePropertiesChangeListener changeListener;
  private HorizontalPanel hierarchy;
  private HorizontalPanel buttons;
  private ButtonAction deletePage;
  private HTML parentPage;
  private Integer actionId;
  private HTML orderValue;
  
  public interface PagePropertiesChangeListener {
    public void parentPageChanged();
  }

	public NewPageView(IController controller, Map requests, PagePropertiesChangeListener changeListener) {
		super(controller);
		this.changeListener = changeListener;
		Long projectId = Long.valueOf( (String) requests.get(RequestId.PROJECT_ID));		
		
		// title
		title = new HTML("New Page");
		title.setStyleName("title-text");
		
    // layout code
    commandArea = new VerticalPanel();
    commandArea.setStyleName(Styles.COMMAND_AREA);

    hierarchy =  new HorizontalPanel();
    hierarchy.setStyleName(Styles.PAGE_LINK_HIERARCHY);

    buttons = new HorizontalPanel();
    buttons.addStyleName(Styles.COMMAND_AREA_BUTTONS);

    commandArea.add(hierarchy);
    commandArea.add(buttons);

		Map backRequest = new HashMap();
		backRequest.put(RequestId.CONTROLLER, RequestValue.PROJECT_CONTROLLER);
		backRequest.put(RequestId.PROJECT_ID, projectId);
		Widget root = ActionFactory
      .createLinkAction("back to project &laquo;", backRequest)
      .getWidget();
		root.addStyleName("page-Hierarchy");
		hierarchy.add(root);
		
    this.actionId = RequestUtils.parseInt(RequestId.ACTION_ID, requests);

    String actionName = "Create";
    int action = ActionId.CREATE_PAGE;
    if (actionId != null && actionId.equals(ActionId.EDIT_PROPERTIES)) {
      actionName = "Save";
      action = ActionId.SUBMIT;
    }
    buttons.add(ActionFactory
      .createButtonAction(actionName, action, controller));

    this.deletePage = ActionFactory
      .createButtonAction("Delete", ActionId.DELETE_PAGE, controller);
    buttons.add(deletePage);

		// body data model
		HTML pageName = new HTML("Page Name:");
		pageName.setStyleName(Styles.GENERIC_LINE_TITLE);
		pageNameField = new TextBox();
		
		this.parentPage = new HTML("Parent Page:");
		parentPage.setStyleName(Styles.GENERIC_LINE_TITLE);

		parentPageList = new ListBoxMap<IPage>();
    parentPageList.addChangeListener(this);

    this.orderValue = new HTML("Order Value");
    orderValue.setStyleName(Styles.GENERIC_LINE_TITLE);
    orderValues = new ListBox();

    // layout body
    body = new VerticalPanel();
    body.setSpacing(20);
    
    FlexTable grid = new FlexTable();
    grid.setWidget(0, 0, pageName);
    grid.setWidget(0, 1, pageNameField);
    grid.setWidget(1, 0, parentPage);
    grid.setWidget(1, 1, parentPageList);
    
    if (actionId != null && actionId.equals(ActionId.EDIT_PROPERTIES)) {
      grid.setWidget(2, 0, orderValue);
      grid.setWidget(2, 1, orderValues);
    }
    
    body.add(grid);
	}

	public void activate(ITilesEngine tilesEngine, 
						 DynamicParams params, IContributor contributor) {
		pageNameField.setText("");
    this.currentPage = (IPage) params.getParam(ParamId.SUBPAGE_PARAM);
    
    IPermissionContributor permissionContributor = controller.getContext().getContributor().cast(
        IPermissionContributor.class);

    this.deletePage.setVisible(false);
    if (actionId != null && actionId.equals(ActionId.EDIT_PROPERTIES)) {
      this.deletePage.setVisible(permissionContributor.hasAdminPermission());
    }
    
    if (currentPage != null && currentPage.getId() != null) {
      pageNameField.setText(currentPage.getName());
      refreshOrderValues();
      title.setHTML("Page properties - " + currentPage.getName());
      
      Map<String, Object> requests = new HashMap<String, Object>();
      requests.put(RequestId.CONTROLLER, RequestValue.PAGE_CONTROLLER);
      requests.put(RequestId.PAGE_ID, currentPage.getId());
      requests.put(RequestId.PROJECT_ID, controller.getContext().getProjectId());
      Action root = ActionFactory.createLinkAction(currentPage.getName() + "  &laquo;", requests);
      root.addStyleName("page-Hierarchy");
      hierarchy.clear();
      hierarchy.add(root);
      HTML props = new HTML("Properties");
      props.addStyleName("page-Hierarchy");
      hierarchy.add(props);
    }

		IProject project = (IProject) params.getParam(ParamId.PROJECT_PARAM);
		parentPageList.clear();
    parentPageList.setVisible(false);
    parentPage.setVisible(false);
    orderValue.setVisible(false);
    orderValues.setVisible(false);
		if (currentPage.getType() == null || !currentPage.getType().equals(Constants.PAGE_TYPE_SKETCH)) {
		  parentPageList.setVisible(true);
		  parentPage.setVisible(true);
		  new ParentSelectionUtil(project, currentPage, parentPageList);
		  
	    orderValue.setVisible(true);
	    orderValues.setVisible(true);
		}

		tilesEngine.setTile(TileId.TITLE, title);
		tilesEngine.setTile(TileId.COMMAND_AREA, commandArea);
		tilesEngine.setTile(TileId.CONTENT, body);
	}

	public String getPageName() {
		return pageNameField.getText();
	}
	
	public IPage getParentPage() {
	  return parentPageList.getData(parentPageList.getSelectedIndex());
	}
	
	public int getOrderValue() {
	  int index = orderValues.getSelectedIndex();
	  if (index >= 0) {
	    return Integer.valueOf(orderValues.getItemText(index));
	  }
	  return 0;
	}

  public void clear() {
    pageNameField.setText("");
    parentPageList.clear();
    orderValues.clear();
  }

  public void refreshOrderValues() {
    if (currentPage != null && currentPage.getOrderValue() != null
        && orderValues != null) {
      orderValues.clear();
      for (int i = 1; i <= currentPage.getParent().getSubpages().size(); ++i) {
        orderValues.addItem(String.valueOf(i));
      }
      // if parent changed set order as last
      int index = currentPage.getOrderValue() - 1;
      int itemCount = currentPage.getParent().getSubpages().size();
      index = index >=  itemCount ? itemCount - 1 : index; 
      orderValues.setSelectedIndex(index);
    }
  }
  
  // @Override
  public void onChange(Widget sender) {
    changeListener.parentPageChanged();
  }
  
}
