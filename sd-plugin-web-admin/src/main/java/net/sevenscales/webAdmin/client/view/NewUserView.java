package net.sevenscales.webAdmin.client.view;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import net.sevenscales.appFrame.api.IContributor;
import net.sevenscales.appFrame.impl.ActionFactory;
import net.sevenscales.appFrame.impl.DynamicParams;
import net.sevenscales.appFrame.impl.IController;
import net.sevenscales.appFrame.impl.ITilesEngine;
import net.sevenscales.appFrame.impl.View;
import net.sevenscales.plugin.constants.ActionId;
import net.sevenscales.plugin.constants.TileId;

public class NewUserView extends View {
	private TextBox userNameField;
  private TextBox passwordField;
	private VerticalPanel body;
	private HTML title;

	public NewUserView(IController controller) {
		super(controller);
		// title
		title = new HTML("<h1>New User</h1>");
		title.setStyleName("new-Project-Title");

		// construct body
		body = new VerticalPanel();

		body.add(new HTML("<h4>* User Name</h4>"));
		userNameField = new TextBox();
		body.add(userNameField);

    body.add(new HTML("<h4>* Password</h4>"));
    passwordField = new TextBox();
    body.add(passwordField);

		HorizontalPanel buttons = new HorizontalPanel();
		buttons.add(ActionFactory
				.createButtonAction("Submit", ActionId.CREATE_USER, controller));
		buttons.add(ActionFactory
				.createButtonAction("Cancel", ActionId.CANCEL, controller));
		buttons.setStyleName("command-Area");
		
		body.add(buttons);
		body.setStyleName("new-Project-View");
	}

	public void activate(ITilesEngine tilesEngine, 
						 DynamicParams params, IContributor contributor) {
		userNameField.setText("");
		tilesEngine.setTile(TileId.TITLE, title);
		tilesEngine.setTile(TileId.COMMAND_AREA, null);
		tilesEngine.setTile(TileId.CONTENT, body);
	}

	public TextBox getUserName() {
		return userNameField;
	}

  public TextBox getPassword() {
    return passwordField;
  }
}
