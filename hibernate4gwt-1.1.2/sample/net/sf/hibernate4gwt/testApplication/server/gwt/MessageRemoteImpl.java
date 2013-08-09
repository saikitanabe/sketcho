/**
 * 
 */
package net.sf.hibernate4gwt.testApplication.server.gwt;


import java.util.List;

import net.sf.hibernate4gwt.core.HibernateBeanManager;
import net.sf.hibernate4gwt.gwt.HibernateRemoteService;
import net.sf.hibernate4gwt.testApplication.client.message.MessageRemote;
import net.sf.hibernate4gwt.testApplication.domain.IMessage;
import net.sf.hibernate4gwt.testApplication.domain.dto.MessageDTO;
import net.sf.hibernate4gwt.testApplication.server.ApplicationContext;
import net.sf.hibernate4gwt.testApplication.server.ApplicationContext.Configuration;
import net.sf.hibernate4gwt.testApplication.server.domain.Message;
import net.sf.hibernate4gwt.testApplication.server.service.IMessageService;

/**
 * Message remote service
 * @author bruno.marchesson
 *
 */
public class MessageRemoteImpl extends HibernateRemoteService
							implements MessageRemote
{
	//----
	// Attributes
	//----
	/**
	 * Serialisation ID																																	
	 */
	private static final long serialVersionUID = -7208813584472295675L;

	/**
	 * The message Service
	 */
	private IMessageService messageService;
	
	//----
	// Properties
	//----
	/**
	 * @return the messageService
	 */
	public IMessageService getMessageService() {
		return messageService;
	}

	/**
	 * @param messageService the messageService to set
	 */
	public void setMessageService(IMessageService messageService) {
		this.messageService = messageService;
	}

	//-------------------------------------------------------------------------
	//
	// Constructor
	//
	//-------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public MessageRemoteImpl()
	{
		setBeanManager((HibernateBeanManager)ApplicationContext.getInstance().getBean("hibernateBeanManager"));
		messageService = (IMessageService) ApplicationContext.getInstance().getBean(IMessageService.NAME);
	}

	//-------------------------------------------------------------------------
	//
	// Team management
	//
	//-------------------------------------------------------------------------
	/**
	 * Return the last posted messages
	 * @gwt.typeArgs <net.sf.hibernate4gwt.testApplication.domain.IMessage>
	 */
	public List getAllMessages(int startIndex, int maxResult)
	{
		if (ApplicationContext.getInstance().getConfiguration() != Configuration.java5)
		{
		//	Just load the message
		//
			return messageService.loadAllMessage(startIndex, maxResult);
		}
		else
		{
		//	JAVA5 : explicit DTO conversion needed
		//
			return (List) clone(messageService.loadAllMessage(startIndex, maxResult));
		}
	}
	
	/**
	 * Count all posted messages
	 */
	public int countAllMessages()
	{
		return messageService.countAllMessage();
	}
	
	/**
	 * Return the last message
	 */
	public IMessage getLastMessage()
	{
		if (ApplicationContext.getInstance().getConfiguration() != Configuration.java5)
		{
		//	Just load the message
		//
			return messageService.loadLastMessage();
		}
		else
		{
		//	JAVA5 : explicit DTO conversion needed
		//
			return (MessageDTO) clone(messageService.loadLastMessage());
		}
	}
	
	/**
	 * @return the Message loaded with all associations
	 */
	public IMessage getMessageDetails(IMessage message)
	{
		if (ApplicationContext.getInstance().getConfiguration() != Configuration.java5)
		{
		//	Just call the service
		//
			return messageService.loadMessageDetails(message);
		}
		else
		{
		//	JAVA5 : explicit DTO conversion needed
		//
			Message mergedMessage = (Message) merge(message);
			
			IMessage completeMessage = messageService.loadMessageDetails(mergedMessage);
			
			return (MessageDTO) clone(completeMessage);
		}
	}
	
	/**
	 * Save the argument message
	 */
	public IMessage saveMessage(IMessage message)
	{
		if (ApplicationContext.getInstance().getConfiguration() != Configuration.java5)
		{
		//	Just save the message
		//
			messageService.saveMessage(message);
			return message;
		}
		else
		{
		//	JAVA5 : explicit DTO conversion needed
		//
			Message mergedMessage = (Message) merge(message);
			
			messageService.saveMessage(mergedMessage);
			
			return (MessageDTO) clone(mergedMessage);
		}
	}
	
	/**
	 * Delete the argument message
	 */
	public void deleteMessage(IMessage message)
	{
		if (ApplicationContext.getInstance().getConfiguration() != Configuration.java5)
		{
		//	Just delete the message
		//
			messageService.deleteMessage(message);
		}
		else
		{
		//	JAVA5 : explicit DTO conversion needed
		//
			Message mergedMessage = (Message) merge(message);			
			messageService.deleteMessage(mergedMessage);
		}
	}
}
