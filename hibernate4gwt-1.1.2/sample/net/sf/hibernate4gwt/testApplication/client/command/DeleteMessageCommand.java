package net.sf.hibernate4gwt.testApplication.client.command;

import net.sf.hibernate4gwt.testApplication.client.core.ApplicationParameters;
import net.sf.hibernate4gwt.testApplication.client.core.DefaultCallback;
import net.sf.hibernate4gwt.testApplication.client.message.MessageRemote;
import net.sf.hibernate4gwt.testApplication.domain.IMessage;

import com.google.gwt.user.client.Command;

/**
 * Message deletion command
 * @author bruno.marchesson
 */
public class DeleteMessageCommand implements Command
{
	//----
	// Attribute
	//----
	/**
	 * The message to delete
	 */
	private IMessage _message;
	
	//----
	// Constructor
	//----
	public DeleteMessageCommand(IMessage message)
	{
		_message = message;
	}
	
	//----
	// Public interface
	//----
	public void execute()
	{
	//	Call asynchronous server service
	//
		MessageRemote.Util.getInstance().deleteMessage(_message, new DefaultCallback(){
			
			public void onSuccess(Object result)
			{
				ApplicationParameters.getInstance().getApplication().displayStatus("Message deleted");
				
			//	Refresh message list
			//
				new RefreshMessageListCommand().execute();
			}
		});
	}
}