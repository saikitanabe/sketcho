/**
 * 
 */
package net.sf.hibernate4gwt.testApplication.client.message;

import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import com.google.gwt.core.client.GWT;

import net.sf.hibernate4gwt.testApplication.client.core.ApplicationParameters;
import net.sf.hibernate4gwt.testApplication.domain.IMessage;
import net.sf.hibernate4gwt.testApplication.domain.IUser;

/**
 * Message static helper
 * @author bruno.marchesson
 *
 */
public class MessageHelper
{
	/**
	 * Indicate if the message is editable
	 */
	public static boolean isEditable(IMessage message)
	{
	//	Precondition checking
	//
		if (message == null)
		{
			return false;
		}
	//	Iterate over user messages
	//
		IUser currentUser = ApplicationParameters.getInstance().getUser();
		Set messageList = currentUser.getMessageList();
		Iterator iterator = messageList.iterator();
		
		// Note : the contains method does not work, event with 'equals' implementation :(
		while (iterator.hasNext())
		{
			IMessage userMessage = (IMessage) iterator.next();
			if (message.equals(userMessage))
			{
				return true;
			}
		}
	
		return false;
	}
	
	/**
	 * Create a new message
	 * @return
	 */
	public static IMessage createNewMessage()
	{
		if (ApplicationParameters.getInstance().getServerConfiguration().getName().equals("stateful"))
		{
		// Create a stateful message
		//
			net.sf.hibernate4gwt.testApplication.domain.stateful.Message message = 
				new net.sf.hibernate4gwt.testApplication.domain.stateful.Message();
			message.setAuthor((net.sf.hibernate4gwt.testApplication.domain.stateful.User)
							  ApplicationParameters.getInstance().getUser());
			message.setDate(new Date());
			return message;
		}
		else if (ApplicationParameters.getInstance().getServerConfiguration().getName().equals("proxy"))
		{
		// Create a proxy message
		//
			net.sf.hibernate4gwt.testApplication.domain.proxy.Message message =
				(net.sf.hibernate4gwt.testApplication.domain.proxy.Message)
				GWT.create(net.sf.hibernate4gwt.testApplication.domain.proxy.Message.class);
			message.setAuthor((net.sf.hibernate4gwt.testApplication.domain.proxy.User)
							  ApplicationParameters.getInstance().getUser());
			message.setDate(new Date());
			return message;
		}
		else if (ApplicationParameters.getInstance().getServerConfiguration().getName().equals("java5"))
		{
		// Create a DTO message
		//
			net.sf.hibernate4gwt.testApplication.domain.dto.MessageDTO message =
				(net.sf.hibernate4gwt.testApplication.domain.dto.MessageDTO)
				// new net.sf.hibernate4gwt.testApplication.domain.dto.MessageDTO();
				GWT.create(net.sf.hibernate4gwt.testApplication.domain.dto.MessageDTO.class);
			message.setAuthor((net.sf.hibernate4gwt.testApplication.domain.dto.UserDTO)
							  ApplicationParameters.getInstance().getUser());
			message.setDate(new Date());
			return message;
		}
		else
		{
		//	Create a stateless message
		//
			net.sf.hibernate4gwt.testApplication.domain.stateless.Message message = 
				new net.sf.hibernate4gwt.testApplication.domain.stateless.Message();
			message.setAuthor((net.sf.hibernate4gwt.testApplication.domain.stateless.User)
							  ApplicationParameters.getInstance().getUser());
			message.setDate(new Date());
			return message;
		}
	}
}
