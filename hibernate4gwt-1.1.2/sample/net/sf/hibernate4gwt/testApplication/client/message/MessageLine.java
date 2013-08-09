package net.sf.hibernate4gwt.testApplication.client.message;

import net.sf.hibernate4gwt.testApplication.client.command.DeleteMessageCommand;
import net.sf.hibernate4gwt.testApplication.client.ui.ConfirmationDialogBox;
import net.sf.hibernate4gwt.testApplication.domain.IMessage;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class MessageLine extends Composite
{
	//----
	// Constants
	//----
	/**
	 * Default path for delete button
	 */
	private static final String DELETE_BUTTON_IMG = "img/delete.png";
	
	//----
	// Attributes
	//---
	/**
	 * The message panel
	 */
	private MessagePanel _messagePanel;
	
	/**
	 * The delete button
	 */
	private Image _deleteButton;
	
	//----
	// Properties (for unit test)
	//----
	/**
	 * @return the message panel
	 */
	public MessagePanel getMessagePanel()
	{
		return _messagePanel;
	}
	
	/**
	 * @return the delete button
	 */
	public Image getDeleteButton()
	{
		return _deleteButton;
	}
	
	//-------------------------------------------------------------------------
	//
	// Constructor
	//
	//-------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public MessageLine()
	{
		init();
	}
	
	//-------------------------------------------------------------------------
	//
	// Public interface
	//
	//-------------------------------------------------------------------------
	/**
	 * Set a new message
	 */
	public void setMessage(IMessage message)
	{
		_messagePanel.setMessage(message);
		_deleteButton.setVisible(MessageHelper.isEditable(message));
	}
	
	/**
	 * @return the displayed message
	 */
	public IMessage getMessage()
	{
		return _messagePanel.getMessage();
	}
	
	//-------------------------------------------------------------------------
	//
	// Internal method
	//
	//-------------------------------------------------------------------------
	/**
	 * Graphic initialisation
	 */
	private void init()
	{
		HorizontalPanel line = new HorizontalPanel();
		line.setStyleName("gwt-BorderedPanel");
		line.setWidth("100%");
		initWidget(line);
		
		// Message panel
		_messagePanel = new MessagePanel();
		_messagePanel.setWidth("100%");
		line.add(_messagePanel);
		
		// Delete button
		_deleteButton = new Image(DELETE_BUTTON_IMG);
		_deleteButton.addClickListener(new ClickListener(){

			public void onClick(Widget sender)
			{
				doDelete(true);
			}
			
		});
		_deleteButton.setVisible(false);
		
		line.add(_deleteButton);
		line.setCellHorizontalAlignment(_deleteButton, HasHorizontalAlignment.ALIGN_RIGHT);
		line.setHeight("20px");
	}
	
	/**
	 * Delete the current message
	 * This method is public to enable unit test
	 */
	public void doDelete(boolean showConfirmation)
	{
		DeleteMessageCommand command = new DeleteMessageCommand(getMessage());
		if (showConfirmation == true)
		{
		//	Wait for user confirmation
		//
			ConfirmationDialogBox dialogBox = new ConfirmationDialogBox("Delete message",
									  									"Are you sure you want to delete this message ?",
									  									command, null);
			dialogBox.show();
		}
		else
		{
		//	Unit test case
		//
			command.execute();
		}
	}
}