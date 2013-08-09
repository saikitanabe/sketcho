package net.sevenscales.sketcho.client.app.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sevenscales.appFrame.api.IContributor;
import net.sevenscales.appFrame.impl.ActionFactory;
import net.sevenscales.appFrame.impl.DynamicParams;
import net.sevenscales.appFrame.impl.IController;
import net.sevenscales.appFrame.impl.ITilesEngine;
import net.sevenscales.appFrame.impl.View;
import net.sevenscales.domain.api.Member;
import net.sevenscales.plugin.api.Context;
import net.sevenscales.plugin.api.PermissionUtil;
import net.sevenscales.plugin.constants.ActionId;
import net.sevenscales.plugin.constants.RequestId;
import net.sevenscales.plugin.constants.RequestValue;
import net.sevenscales.plugin.constants.Styles;
import net.sevenscales.plugin.constants.TileId;
import net.sevenscales.sketcho.client.uicomponents.ManageLabelsViewContent;
import net.sevenscales.webAdmin.client.view.CheckBoxData;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ManageLabelsView extends View<Context> {
  public interface IManageViewCallback {
    
  }

  private HTML title;
  private VerticalPanel commandArea;
  private HorizontalPanel hierarchy;
  private HorizontalPanel buttons;
  private ManageLabelsViewContent content;
	
	public ManageLabelsView(IController controller, IManageViewCallback manageViewCallback) {
		super(controller);
		// title
		title = new HTML("Manage Labels");		
		title.setStyleName("title-text");
		
    // layout code
    commandArea = new VerticalPanel();
    commandArea.setStyleName(Styles.COMMAND_AREA);

    hierarchy =  new HorizontalPanel();
    hierarchy.setStyleName(Styles.PAGE_LINK_HIERARCHY);

    buttons = new HorizontalPanel();
    buttons.addStyleName(Styles.COMMAND_AREA_BUTTONS);

    commandArea.add(hierarchy);
//    commandArea.add(buttons);
    
    // back link
//    Map<Object, Object> requests = new HashMap<Object, Object>();
//    requests.put(RequestId.CONTROLLER, RequestValue.PROJECTS_CONTROLLER);
//    hierarchy.add(ActionFactory
//      .createLinkAction("&laquo; back to projects", requests)
//      .getWidget());    

    buttons.add(ActionFactory
        .createButtonAction("Add User", ActionId.ADD_USER, controller)
        .getWidget());
    buttons.add(ActionFactory
        .createButtonAction("Delete User", ActionId.DELETE_USER, controller)
        .getWidget());
	}

	public void activate(ITilesEngine tilesEngine, 
						 DynamicParams params, IContributor contributor) {
    content = new ManageLabelsViewContent(controller.getContext());
	  title.setHTML(controller.getContext().getProject().getName()+ " - Manage Labels");
		tilesEngine.setTile(TileId.TITLE, title);
    tilesEngine.setTile(TileId.COMMAND_AREA, commandArea);
    tilesEngine.setTile(TileId.CONTENT, content);
	}

}
