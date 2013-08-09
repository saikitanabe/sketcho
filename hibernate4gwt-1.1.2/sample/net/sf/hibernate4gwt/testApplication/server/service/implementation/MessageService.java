/**
 * 
 */
package net.sf.hibernate4gwt.testApplication.server.service.implementation;

import java.util.List;

import net.sf.hibernate4gwt.testApplication.domain.IMessage;
import net.sf.hibernate4gwt.testApplication.server.MessageHelper;
import net.sf.hibernate4gwt.testApplication.server.dao.IMessageDAO;
import net.sf.hibernate4gwt.testApplication.server.service.IMessageService;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the message service
 * @author bruno.marchesson
 *
 */
public class MessageService implements IMessageService
{
	//----
	// Attributes
	//----
	/**
	 * the associated DAO
	 */
	private IMessageDAO messageDAO;
	
	//----
	// Properties
	//----
	/**
	 * @return the messageDAO
	 */
	public IMessageDAO getMessageDAO() {
		return messageDAO;
	}

	/**
	 * @param messageDAO the messageDAO to set
	 */
	public void setMessageDAO(IMessageDAO messageDAO) {
		this.messageDAO = messageDAO;
	}

	//-------------------------------------------------------------------------
	//
	// Implementation of the message service
	//
	//-------------------------------------------------------------------------
	/* (non-Javadoc)
	 * @see net.sf.hibernate4gwt.testApplication.server.service.IMessageService#loadLastMessage()
	 */
	public IMessage loadLastMessage()
	{
		return messageDAO.loadLastMessage();
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.sf.hibernate4gwt.testApplication.server.service.IMessageService#loadAllMessage(int, int)
	 */
	public List<IMessage> loadAllMessage(int startIndex, int maxResult)
	{
		return messageDAO.loadAllMessage(startIndex, maxResult);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.sf.hibernate4gwt.testApplication.server.service.IMessageService#countAllMessage()
	 */
	public int countAllMessage()
	{
		return messageDAO.countAllMessages();
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.sf.hibernate4gwt.testApplication.server.service.IMessageService#loadMessageDetails(net.sf.hibernate4gwt.testApplication.domain.IMessage)
	 */
	public IMessage loadMessageDetails(IMessage message)
	{
		return messageDAO.loadDetailedMessage(message.getId());
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.sf.hibernate4gwt.testApplication.server.service.IMessageService#saveMessage(net.sf.hibernate4gwt.testApplication.domain.IMessage)
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	public void saveMessage(IMessage message)
	{
		messageDAO.lockMessage(message);
		MessageHelper.computeKeywords(message);
		
		messageDAO.saveMessage(message);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.sf.hibernate4gwt.testApplication.server.service.IMessageService#deleteMessage(net.sf.hibernate4gwt.testApplication.domain.IMessage)
	 */
	public void deleteMessage(IMessage message)
	{
		messageDAO.deleteMessage(message);
	}
}
