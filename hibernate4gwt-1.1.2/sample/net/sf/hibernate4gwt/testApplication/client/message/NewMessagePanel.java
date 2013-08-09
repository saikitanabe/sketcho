/**
 * 
 */
package net.sf.hibernate4gwt.testApplication.client.message;

import net.sf.hibernate4gwt.testApplication.client.command.RefreshMessageListCommand;
import net.sf.hibernate4gwt.testApplication.client.core.ApplicationParameters;
import net.sf.hibernate4gwt.testApplication.client.core.DefaultCallback;
import net.sf.hibernate4gwt.testApplication.domain.IMessage;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.KeyboardListenerAdapter;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Message panel
 * @author bruno.marchesson
 *
 */
public class NewMessagePanel extends Composite
{
	//----
	// Attributes
	//----
	/**
	 * The last created message (for unit test)
	 */
	protected IMessage _lastCreateMessage;
	
	/**
	 * message label
	 */
	protected TextBox _messageTextbox;
	
	/**
	 * Post button
	 */
	protected Button _postButton;
		
	//----
	// Properties
	//----
	/**
	 * @return the message to display
	 */
	public final IMessage getMessage()
	{
		return _lastCreateMessage;
	}
	
	/**
	 * @return the message text box
	 */
	public final TextBox getTextBox()
	{
		return _messageTextbox;
	}

	/**
	 * @return the "post" button
	 */
	public final Button getPostButton()
	{
		return _postButton;
	}
	
	//-------------------------------------------------------------------------
	//
	// Constructor
	//
	//-------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public NewMessagePanel()
	{
		init();
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
		final FlexTable mainPanel = new FlexTable();
		mainPanel.setStyleName("gwt-BorderedPanel");
		mainPanel.setCellPadding(2);
		initWidget(mainPanel);
		
		int row = 0;
		
		// Title
		mainPanel.setWidget(row, 0, new Label("Post a new message"));
		mainPanel.getFlexCellFormatter().setColSpan(row, 0, 2);
		mainPanel.getRowFormatter().addStyleName(0, "gwt-Table-header");
		row++;
		
		// Message text
		_messageTextbox = new TextBox();
		_messageTextbox.addKeyboardListener(new KeyboardListenerAdapter(){
			
			public void onKeyPress (Widget sender, char keyCode, int modifiers)
            {
                if (keyCode == 13)
                {
                	saveMessage();
                }
            }
		});
		mainPanel.setWidget(row, 0, _messageTextbox);
		
		//	Post button
		_postButton = new Button("Post");
		mainPanel.setWidget(row, 1, _postButton);
		_postButton.addClickListener(new ClickListener(){

			public void onClick(Widget sender) {
				saveMessage();
			}
		});
	}
	
	/**
	 * Save the current message
	 */
	private void saveMessage()
	{
		setStatusMessage("Posting a new message...");
		
	//	Disable post button
	//
		_postButton.setEnabled(false);
		
	//	Create a new message
	//
		IMessage message = MessageHelper.createNewMessage();
		message.setMessage(_messageTextbox.getText());
		
	//	Call asynchronous method on server
	//
		MessageRemote.Util.getInstance().saveMessage(message, new DefaultCallback(){

			public void onSuccess(Object result)
			{
				setStatusMessage("The message has been successfully posted");
				
			//	Get last created message
			//
				_lastCreateMessage = (IMessage)result;
				ApplicationParameters.getInstance().getUser().addMessage(_lastCreateMessage);
				
			//	Reset graphical state
			//
				_messageTextbox.setText("");
				_postButton.setEnabled(true);
				
			//	Refresh message list
			//
				new RefreshMessageListCommand().execute();
			}
			
			public void onFailure(Throwable caught)
			{
				super.onFailure(caught);
				
				_postButton.setEnabled(true);
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
