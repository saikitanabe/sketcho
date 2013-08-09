package net.sevenscales.sketcho.client.app.view;

import net.sevenscales.appFrame.api.IContributor;
import net.sevenscales.appFrame.impl.ActionFactory;
import net.sevenscales.appFrame.impl.DynamicParams;
import net.sevenscales.appFrame.impl.IController;
import net.sevenscales.appFrame.impl.ITilesEngine;
import net.sevenscales.appFrame.impl.View;
import net.sevenscales.plugin.constants.ActionId;
import net.sevenscales.plugin.constants.Styles;
import net.sevenscales.plugin.constants.TileId;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class NewProjectView extends View {
	private TextBox projectNameField;
	private HorizontalPanel body;
	private HTML title;
  private VerticalPanel commandArea;
  private HorizontalPanel hierarchy;
  private HorizontalPanel buttons;
  private CheckBox publicPropertyValue;

	public NewProjectView(IController controller) {
		super(controller);
		// title
		title = new HTML("New Project");
		title.setStyleName(Styles.TITLE_TEXT);

		// layout code
    commandArea = new VerticalPanel();
    commandArea.setStyleName(Styles.COMMAND_AREA);

    hierarchy =  new HorizontalPanel();
    hierarchy.setStyleName(Styles.PAGE_LINK_HIERARCHY);

    buttons = new HorizontalPanel();
    buttons.addStyleName(Styles.COMMAND_AREA_BUTTONS);

    commandArea.add(hierarchy);
    commandArea.add(buttons);
    
    // ui elements
    buttons.add(ActionFactory
        .createButtonAction("Submit", ActionId.CREATE_PROJECT, controller));
    buttons.add(ActionFactory
        .createButtonAction("Cancel", ActionId.CANCEL, controller));
    
    
		// ui elements construction
		HTML name = new HTML("Project Name:");
    name.setStyleName(Styles.GENERIC_LINE_TITLE);
		
		projectNameField = new TextBox();
		projectNameField.setWidth("200px");
		projectNameField.addStyleName("login-textarea");
		
    HTML publicProperty = new HTML("Public Project:");
    publicProperty.setStyleName(Styles.GENERIC_LINE_TITLE);
		this.publicPropertyValue = new CheckBox();
		publicPropertyValue.setValue(false);
		// allowing users to create private projects, but not public! Confuses usage.
		// increases amount of users
		publicPropertyValue.setEnabled(false); 
		
		// position ui elements
    Grid grid = new Grid(2, 2);
    grid.setWidget(0, 0, name);
    grid.setWidget(0, 1, projectNameField);
    
    grid.setWidget(1, 0, publicProperty);
    grid.setWidget(1, 1, publicPropertyValue);
		
    body = new HorizontalPanel();
    body.addStyleName(Styles.CONTENT_BODY_PROPERTIES);

    body.add(grid);
	}

	public void activate(ITilesEngine tilesEngine, 
						 DynamicParams params, IContributor contributor) {
		projectNameField.setText("");
		tilesEngine.setTile(TileId.TITLE, title);
		tilesEngine.setTile(TileId.COMMAND_AREA, commandArea);
		tilesEngine.setTile(TileId.CONTENT, body);
		projectNameField.setFocus(true);
	}

	public TextBox getProjectNameField() {
		return projectNameField;
	}
	
	public Boolean isPublicProject() {
	  return publicPropertyValue.isChecked();
	}
}
