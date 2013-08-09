package net.sevenscales.login.client.view;

import net.sevenscales.appFrame.api.IContributor;
import net.sevenscales.appFrame.impl.ActionFactory;
import net.sevenscales.appFrame.impl.ButtonAction;
import net.sevenscales.appFrame.impl.DynamicParams;
import net.sevenscales.appFrame.impl.IController;
import net.sevenscales.appFrame.impl.ITilesEngine;
import net.sevenscales.appFrame.impl.View;
import net.sevenscales.login.client.controller.RegisterController;
import net.sevenscales.plugin.constants.ActionId;
import net.sevenscales.plugin.constants.TileId;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class RegisterView extends View implements KeyboardListener {
	private TextBox userNameField;
//  private TextBox nickNameField;
  private TextBox passwordField;
	private VerticalPanel body;
	private HTML title;
  private PasswordTextBox passwordVerifyField;
  private HTML passwordTitleVerify;
  private ButtonAction submit;

  private HTML acceptTosText;

	public RegisterView(IController controller) {
		super(controller);
		// title
		title = new HTML("Register");
		title.setStyleName("title-text");

		// construct body
		body = new VerticalPanel();
		body.setWidth("100%");
    this.submit = ActionFactory
      .createButtonAction("Register", ActionId.REGISTER, controller);
    
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
		
//    HTML nickNameTitle = new HTML("Screen Name:");
//    nickNameTitle.addStyleName("generic-line-title");
//		nickNameField = new TextBox();
//		nickNameField.addKeyboardListener(this);
//		nickNameField.setWidth("200px");
//		nickNameField.addStyleName("login-textarea");

    HTML passwordTitle = new HTML("Password:");
    passwordTitle.addStyleName("generic-line-title");
    passwordField = new PasswordTextBox();
    passwordField.addKeyboardListener(this);
    passwordField.setWidth("200px");
    passwordField.addStyleName("login-textarea");

    this.passwordTitleVerify = new HTML("Verify password:");
    passwordTitleVerify.addStyleName("generic-line-title");
    passwordVerifyField = new PasswordTextBox();
    passwordVerifyField.addKeyboardListener(this);
    passwordVerifyField.setWidth("200px");
    passwordVerifyField.addStyleName("login-textarea");
    
    this.acceptTosText = new HTML("By clicking 'Register' button above you are agreeing to the <a href='http://7scales.net/7scales/tos.html' target='_blank'>7scales Terms of Service</a>.");

    FlexTable grid = new FlexTable();

    grid.setWidget(0, 0, userNameTitle);
    grid.setWidget(0, 1, userNameField);

//    grid.setWidget(1, 0, nickNameTitle);
//    grid.setWidget(1, 1, nickNameField);

    grid.setWidget(2, 0, passwordTitle);
    grid.setWidget(2, 1, passwordField);

    grid.setWidget(3, 0, passwordTitleVerify);
    grid.setWidget(3, 1, passwordVerifyField);
    
    grid.setWidget(4, 1, acceptTosText);

    // body layout
    body.add(grid);
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
	
	public String getNickName() {
		return null;
//    return nickNameField.getText();
  }

  public String getPassword() {
    return passwordField.getText();
  }
  
  public String getPasswordVerifyField() {
    return passwordVerifyField.getText();
  }

  public void clear() {
    userNameField.setText("");
//    nickNameField.setText("");
    passwordField.setText("");
    passwordVerifyField.setText("");
  }

	public void onKeyDown(Widget sender, char keyCode, int modifiers) {
		((RegisterController)controller).processKeyEvent(keyCode);
	}
	
	public void onKeyPress(Widget sender, char keyCode, int modifiers) {
		// TODO Auto-generated method stub
		
	}
	
	public void onKeyUp(Widget sender, char keyCode, int modifiers) {
		// TODO Auto-generated method stub
		
	}

  public void enableSubmit(boolean enable) {
    submit.getButton().setEnabled(enable);
  }
}
