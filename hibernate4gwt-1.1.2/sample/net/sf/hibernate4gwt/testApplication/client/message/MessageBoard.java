/**
 * 
 */
package net.sf.hibernate4gwt.testApplication.client.message;

import java.util.ArrayList;
import java.util.List;

import net.sf.hibernate4gwt.testApplication.client.core.ApplicationParameters;
import net.sf.hibernate4gwt.testApplication.client.core.DefaultCallback;
import net.sf.hibernate4gwt.testApplication.domain.IMessage;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Message board, containing all messages
 * @author bruno.marchesson
 *
 */
public class MessageBoard extends Composite
{
	//----
	// Constants
	//----
	/**
	 * Number of messages for one page
	 */
	private static final int MESSAGE_PER_PAGE = 10;
	
	//----
	// Attributes
	//----
	/**
	 * Current message index
	 */
	private int _currentIndex;
	
	/**
	 * Total message count
	 */
	private int _messageCount;
	
	/**
	 * Title label
	 */
	private Label _title;
	
	/**
	 * message line list
	 */
	private List _messageLineList;
	
	/**
	 * "Next" button for pagination
	 */
	private Button _nextButton;
	
	/**
	 * "Previous" button for pagination
	 */
	private Button _previousButton;

	//----
	// Properties (for junit test)
	//----
	/**
	 * @return the first message panel
	 */
	public final MessageLine getFirstMessageLine()
	{
		return ((MessageLine)_messageLineList.get(0));
	}

	//-------------------------------------------------------------------------
	//
	// Constructor
	//
	//-------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public MessageBoard()
	{
		init();
	//	doRefresh();
	}
	
	//-------------------------------------------------------------------------
	//
	// Public interface
	//
	//-------------------------------------------------------------------------
	/**
	 * Refresh the message board
	 */
	public void refresh()
	{
		doRefresh();
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
		mainPanel.setCellPadding(2);
		mainPanel.setWidth("100%");
		initWidget(mainPanel);
		
		int row = 0;
		
	//	Title
	//
		_title = new Label("Messages");
		mainPanel.setWidget(row, 0, _title);
		mainPanel.getFlexCellFormatter().setColSpan(row, 0, 3);
		mainPanel.getRowFormatter().addStyleName(0, "gwt-Table-header");
		row++;
		
	//	Message Container
	//
		_messageLineList = new ArrayList(MESSAGE_PER_PAGE);
		VerticalPanel messageContainer = new VerticalPanel();
		messageContainer.setWidth("100%");
		for (int panel = 0; panel < MESSAGE_PER_PAGE ; panel ++)
		{
			MessageLine line = new MessageLine();
			_messageLineList.add(line);
			messageContainer.add(line);
		}
		mainPanel.setWidget(row, 0, messageContainer);
		mainPanel.getFlexCellFormatter().setColSpan(row, 0, 3);
		
		row++;
		
	//	Navigation buttons
	//
		// Previous
		_previousButton = new Button("<< Previous");
		_previousButton.addClickListener(new ClickListener(){

			public void onClick(Widget sender)
			{
				doPrevious();
			}
			
		});
		mainPanel.setWidget(row, 0, _previousButton);
		mainPanel.getCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_LEFT);
		
		// Refresh
		Button refreshButton = new Button("< Refresh >");
		refreshButton.addClickListener(new ClickListener(){

			public void onClick(Widget sender)
			{
				doRefresh();
			}
			
		});
		mainPanel.setWidget(row, 1, refreshButton);
		mainPanel.getCellFormatter().setHorizontalAlignment(row, 1, HasHorizontalAlignment.ALIGN_CENTER);
		
		
		// Next
		_nextButton = new Button("Next >>");
		_nextButton.addClickListener(new ClickListener(){

			public void onClick(Widget sender)
			{
				doNext();
			}
			
		});
		mainPanel.setWidget(row, 2, _nextButton);
		mainPanel.getCellFormatter().setHorizontalAlignment(row, 2, HasHorizontalAlignment.ALIGN_RIGHT);
	}
	
	/**
	 * Previous button action
	 */
	private void doPrevious()
	{
	//	Current index update
	//
		if (_currentIndex > 0)
		{
			_currentIndex -= MESSAGE_PER_PAGE;
			if (_currentIndex < 0)
			{
				_currentIndex = 0;
			}
		}
		
	//	Load messages
	//
		loadMessages();
	}
	
	/**
	 * Next button action
	 */
	private void doNext()
	{
	//	Current index update
	//
		_currentIndex += MESSAGE_PER_PAGE;
		
	//	Load messages
	//
		loadMessages();
	}
	
	/**
	 * Refresh button action
	 */
	private void doRefresh()
	{
	//	Reload last messages
	//
		loadMessages();
		
	//	Load message count
	//
		loadMessageCount();
	}

	/**
	 * Load messages from the current index
	 */
	private void loadMessages()
	{
		setStatusMessage("Loading messages...");
		
		MessageRemote.Util.getInstance().getAllMessages(_currentIndex, MESSAGE_PER_PAGE, 
														new DefaultCallback(){

			public void onSuccess(Object result)
			{
				setStatusMessage("Messages loaded");
				displayMessageList((List) result);
			}
		});
	}
	
	/**
	 * Load messages count 
	 */
	private void loadMessageCount()
	{
		setStatusMessage("Loading messages count...");
		
		MessageRemote.Util.getInstance().countAllMessages(new DefaultCallback(){

			public void onSuccess(Object result)
			{
				setStatusMessage("Messages count loaded");
			
				_messageCount = ((Integer)result).intValue();
				updateLabel(); 
			}
		});
	}
	
	/**
	 * Display the argument message list
	 * @param messageList
	 */
	private void displayMessageList(List messageList)
	{
		int messageCount = messageList.size();
		
	//	Iterate over Message panels
	//
		for (int index = 0; index < MESSAGE_PER_PAGE ; index++)
		{
			MessageLine messageLine = (MessageLine) _messageLineList.get(index);
			if (index < messageCount)
			{
				messageLine.setMessage((IMessage) messageList.get(index));
			}
			else
			{
			//	No more message
				messageLine.setMessage(null);
			}
		}
		
	//	Navigation buttons update
	//
		_previousButton.setEnabled(_currentIndex > 0);
		_nextButton.setEnabled(messageCount == MESSAGE_PER_PAGE);
		
	//	Update label
	//
		updateLabel();
	}
	
	/**
	 * Update the message label
	 * @param messageCount
	 */
	private void updateLabel()
	{
		StringBuffer title = new StringBuffer();
		title.append("Messages ");
		title.append(_currentIndex + 1);
		
		title.append(" to ");
		int toIndex = _currentIndex + MESSAGE_PER_PAGE;
		if (toIndex > _messageCount)
		{
			toIndex = _messageCount;
		}
		title.append(toIndex);
		
		title.append(" /  ");
		title.append(_messageCount);
		
		_title.setText(title.toString());
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