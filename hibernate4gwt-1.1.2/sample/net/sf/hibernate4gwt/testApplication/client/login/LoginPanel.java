package net.sf.hibernate4gwt.testApplication.client.login;

import net.sf.hibernate4gwt.testApplication.client.core.Action;
import net.sf.hibernate4gwt.testApplication.client.core.ApplicationParameters;
import net.sf.hibernate4gwt.testApplication.client.core.DefaultCallback;
import net.sf.hibernate4gwt.testApplication.domain.IUser;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.KeyboardListenerAdapter;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class LoginPanel extends Composite
{
	//----
	// Attributes
	//----	
	/**
	 * Main panel
	 */
	private FlexTable _authenticationPanel;
	
	/**
	 * The login text box
	 */
	private TextBox _loginTextBox;
	
	/**
	 * The password text box
	 */
	private PasswordTextBox _passwordTextBox;
	
	/**
	 * The connect button
	 */
	private Button _connectButton;
	
	//----
	// Properties
	//----
	/**
	 * @return the login TextBox
	 */
	public TextBox getLoginTextBox() {
		return _loginTextBox;
	}

	/**
	 * @return the password TextBox
	 */
	public PasswordTextBox getPasswordTextBox() {
		return _passwordTextBox;
	}

	/**
	 * @return the connect Button
	 */
	public Button getConnectButton() {
		return _connectButton;
	}
	
	//-------------------------------------------------------------------------
	//
	// Constructor
	//
	//-------------------------------------------------------------------------
	public LoginPanel()
	{
		init();
	}

	//------------------------------------------------------------------------
	//
	// Internal methods
	//
	//------------------------------------------------------------------------
	/**
	 * Graphic initialisation
	 */
	protected void init()
	{
	//	Main panel
	//
		_authenticationPanel = new FlexTable();
		_authenticationPanel.setStyleName("gwt-BorderedPanel");
		_authenticationPanel.setCellPadding(2);
		initWidget(_authenticationPanel);
		
		int row = 0;
		
		// Title
		_authenticationPanel.setWidget(row, 0, new Label("Connexion"));
		_authenticationPanel.getFlexCellFormatter().setColSpan(row, 0, 2);
		_authenticationPanel.getRowFormatter().addStyleName(0, "gwt-Table-header");
		
		row++;
		
		// Login
		_authenticationPanel.setWidget(row, 0, new Label("Login : "));
		
		_loginTextBox = new TextBox();
		_loginTextBox.setText("guest");
		_loginTextBox.addKeyboardListener(new KeyboardListenerAdapter(){
			
			public void onKeyPress (Widget sender, char keyCode, int modifiers)
            {
                if (keyCode == 13)
                {
                	doLogin();
                }
            }
		});
		_authenticationPanel.setWidget(row, 1, _loginTextBox);
		row++;
		
		// Password
		_authenticationPanel.setWidget(row, 0, new Label("Password : "));
		
		_passwordTextBox = new PasswordTextBox();
		_passwordTextBox.addKeyboardListener(new KeyboardListenerAdapter(){
			
			public void onKeyPress (Widget sender, char keyCode, int modifiers)
            {
                if (keyCode == 13)
                {
                	doLogin();
                }
            }
		});
		_authenticationPanel.setWidget(row, 1, _passwordTextBox);
		row++;
		
	//	Connect button
	//
		_connectButton = new Button("Login");
		_authenticationPanel.setWidget(row, 0, _connectButton);
		_authenticationPanel.getFlexCellFormatter().setColSpan(row, 0, 2);
		_authenticationPanel.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);
		
		_connectButton.addClickListener(new ClickListener(){

			public void onClick(Widget sender) {
				doLogin();
			}
		});
	}

	/**
	 * Login process
	 */
	private void doLogin()
	{
		setStatusMessage("Connecting...");
		_connectButton.setEnabled(false);
		
		LoginRemote.Util.getInstance().authenticate(_loginTextBox.getText(), 
													_passwordTextBox.getText(), 
													new DefaultCallback(){
			public void onSuccess(Object result)
			{
			// 	Get the authenticated user
			//
				IUser user = (IUser) result;
				setStatusMessage("Connected as " + user.getLogin());
				
				ApplicationParameters.getInstance().setUser(user);
				
			//	Send action
			//
				Action.executeAction(Action.CONNECTED);
			}
			
			public void onFailure(Throwable caught)
			{
				super.onFailure(caught);
				
			//	Enable connect button again
			//
				_connectButton.setEnabled(true);
			}
		});
	}
	
	/**
	 * Change the status message
	 * @param status the message to display
	 */
	private void setStatusMessage(String status)
	{
		ApplicationParameters.getInstance().getApplication().displayStatus(status);
	}
}
