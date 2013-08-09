package net.sevenscales.login.client.view;

import java.util.HashMap;
import java.util.Map;

import net.sevenscales.appFrame.api.IContributor;
import net.sevenscales.appFrame.impl.Action;
import net.sevenscales.appFrame.impl.ActionFactory;
import net.sevenscales.appFrame.impl.ButtonAction;
import net.sevenscales.appFrame.impl.DynamicParams;
import net.sevenscales.appFrame.impl.IController;
import net.sevenscales.appFrame.impl.ITilesEngine;
import net.sevenscales.appFrame.impl.View;
import net.sevenscales.login.client.controller.LoginController;
import net.sevenscales.plugin.constants.ActionId;
import net.sevenscales.plugin.constants.RequestId;
import net.sevenscales.plugin.constants.RequestValue;
import net.sevenscales.plugin.constants.TileId;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class LoginView extends View implements KeyboardListener {
	private TextBox userNameField;
  private TextBox passwordField;
	private VerticalPanel body;
	private HTML title;
  private ButtonAction submit;
  private Action register;
  private CheckBox staySignedIn;
  private HTML staySignedInText;

	public LoginView(IController controller) {
		super(controller);
		// title
		title = new HTML("Login");
		title.setStyleName("title-text");

		// construct body
		body = new VerticalPanel();
		body.setWidth("100%");
    this.submit = ActionFactory
      .createButtonAction("Sign In", ActionId.LOGIN, controller);
    
    HorizontalPanel buttons = new HorizontalPanel();
    buttons.addStyleName("login-commands");
    buttons.add(submit);
//    buttons.add(ActionFactory
//        .createButtonAction("Cancel", ActionId.CANCEL, controller));
    
    VerticalPanel commandsBackground = new VerticalPanel();
    commandsBackground.setStyleName("login-commands-background");
    commandsBackground.add(buttons);
    commandsBackground.setCellVerticalAlignment(buttons, VerticalPanel.ALIGN_MIDDLE);
    commandsBackground.setWidth("100%");
    body.add(commandsBackground);
    body.setCellWidth(commandsBackground, "100%");

    
    HTML userNameTitle = new HTML("Email Address:");
    userNameTitle.addStyleName("generic-line-title");
		userNameField = new TextBox();
		userNameField.addKeyboardListener(this);
		userNameField.setWidth("200px");
		userNameField.addStyleName("login-textarea");

    HTML passwordTitle = new HTML("Password:");
    passwordTitle.addStyleName("generic-line-title");
    passwordField = new PasswordTextBox();
    passwordField.addKeyboardListener(this);
    passwordField.setWidth("200px");
    passwordField.addStyleName("login-textarea");

    this.staySignedIn = new CheckBox();
    this.staySignedInText = new HTML("Stay signed in");

    FlexTable grid = new FlexTable();

    grid.setWidget(0, 0, userNameTitle);
    grid.setWidget(0, 1, userNameField);

    grid.setWidget(1, 0, passwordTitle);
    grid.setWidget(1, 1, passwordField);

    grid.setWidget(3, 0, staySignedIn);
    grid.setWidget(3, 1, staySignedInText);

    // register
    Map<Object, Object> requests = new HashMap<Object, Object>();
    requests.put(RequestId.CONTROLLER, RequestValue.REGISTER_CONTROLLER);
    this.register = ActionFactory.createLinkAction("No account. Register", requests);
    
    // body layout
    body.add(grid);
    body.add(register);
	}

	public void activate(ITilesEngine tilesEngine, 
						 DynamicParams params, IContributor contributor) {
		userNameField.setText("");
		passwordField.setText("");
		tilesEngine.setTile(TileId.TITLE, title);
		tilesEngine.setTile(TileId.CONTENT, body);
		tilesEngine.clear(TileId.COMMAND_AREA);
		userNameField.setFocus(true);
	}

	public String getUserName() {
		return userNameField.getText();
	}

  public String getPassword() {
    return passwordField.getText();
  }
  
  public void clear() {
    userNameField.setText("");
    passwordField.setText("");
  }

	public void onKeyDown(Widget sender, char keyCode, int modifiers) {
		((LoginController)controller).processKeyEvent(keyCode);
	}
	
	public void onKeyPress(Widget sender, char keyCode, int modifiers) {
		// TODO Auto-generated method stub
		
	}
	
	public void onKeyUp(Widget sender, char keyCode, int modifiers) {
		// TODO Auto-generated method stub
		
	}

  public boolean getStaySignedIn() {
    return staySignedIn.getValue();
  }
  
}
