/**
 * 
 */
package net.sf.hibernate4gwt.testApplication.client.user;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import net.sf.hibernate4gwt.testApplication.client.command.RefreshMessageListCommand;
import net.sf.hibernate4gwt.testApplication.client.core.Action;
import net.sf.hibernate4gwt.testApplication.client.core.ApplicationParameters;
import net.sf.hibernate4gwt.testApplication.client.core.DefaultCallback;
import net.sf.hibernate4gwt.testApplication.client.message.MessageHelper;
import net.sf.hibernate4gwt.testApplication.domain.IMessage;
import net.sf.hibernate4gwt.testApplication.domain.IUser;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;

/**
 * Header panel
 * Contains user name, status message and log out link
 * @author bruno.marchesson
 *
 */
public class HeaderPanel extends Composite
{
	//----
	// Constants
	//----
	/**
	 * Last connection message
	 */
	private static String LAST_CONNECTION_MESSAGE = "Last connection for ";
	
	//----
	// Attributes
	//----
	/**
	 * The welcome panel
	 */
	protected Label _userLabel;
	
	/**
	 * The status label
	 */
	protected HTML _statusLabel;
	
	/**
	 * User list box
	 */
	protected Hyperlink _logoutLink;
	
	//----
	// Properties
	//----
	/**
	 * @return the status label
	 */
	public HTML getStatusLabel()
	{
		return _statusLabel;
	}	

	//-------------------------------------------------------------------------
	//
	// Constructor
	//
	//-------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public HeaderPanel()
	{
		init();
	}
	
	//-------------------------------------------------------------------------
	//
	// Public interface
	//
	//-------------------------------------------------------------------------
	/**
	 * User change
	 */
	public void setUser(IUser user)
	{
		if (user != null)
		{
			_userLabel.setText("Welcome " + user.getLogin());
			_logoutLink.setVisible(true);
			
			updateLastConnectionMessage(user);
		}
		else
		{
			_userLabel.setText("");
			_logoutLink.setVisible(false);
		}
	}
	
	//-------------------------------------------------------------------------
	//
	// Internal methods
	//
	//-------------------------------------------------------------------------
	/**
	 * Graphic init
	 */
	protected void init()
	{
	//	Main panel
	//
		Grid mainPanel = new Grid(1,3);
		mainPanel.setWidth("100%");
		initWidget(mainPanel);
		
		// User label
		_userLabel = new Label();
		mainPanel.setWidget(0, 0, _userLabel);
		mainPanel.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
		
		// Status label
		_statusLabel = new HTML();
		mainPanel.setWidget(0, 1, _statusLabel);
		mainPanel.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_CENTER);
		
		// Log off 
		_logoutLink = new Hyperlink();
		_logoutLink.setText("log out");
		_logoutLink.setVisible(false);
		_logoutLink.setTargetHistoryToken(new Action(Action.AUTHENTICATION).toString());
		
		mainPanel.setWidget(0, 2, _logoutLink);
		mainPanel.getCellFormatter().setHorizontalAlignment(0, 2, HasHorizontalAlignment.ALIGN_RIGHT);
	}
	
	/**
	 * Update the last connection message for the user
	 */
	private void updateLastConnectionMessage(IUser user)
	{
		IMessage lastConnectionMessage = null;
		
	//	Search for an existing last connection message
	//
		Set messageList = user.getMessageList();
		if (messageList != null)
		{
			for (Iterator iterator = messageList.iterator(); iterator.hasNext();)
			{
				IMessage message = (IMessage) iterator.next();
				if ((message != null) &&
					(message.getMessage().startsWith(LAST_CONNECTION_MESSAGE)))
				{
				//	We found the last connection message
				//
					lastConnectionMessage = message;
					break;
				}	
			}
		}
		
	//	Create last connection message if needed
	//
		if (lastConnectionMessage == null)
		{
			lastConnectionMessage = MessageHelper.createNewMessage();
			user.addMessage(lastConnectionMessage);
		}
		
	//	Update last connection date
	//
		Date now = new Date();
		lastConnectionMessage.setMessage(LAST_CONNECTION_MESSAGE + user.getLogin() + 
										 " : " + now.toString());
		
	//	Modified the embedded keywords
	//
		if (lastConnectionMessage.getKeywords() == null)
		{
			lastConnectionMessage.setKeywords(new HashMap());
		}
		lastConnectionMessage.getKeywords().put(now.toString(), new Integer(now.getSeconds()));
		
	//	Save the entire user
	//
		setStatusMessage("Saving last connection");
		UserRemote.Util.getInstance().saveUser(user, new DefaultCallback(){

			public void onSuccess(Object result) {
				setStatusMessage("Last connection saved");
				new RefreshMessageListCommand().execute();
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
