package net.sf.hibernate4gwt.testApplication.server.service;

import java.util.List;

import net.sf.hibernate4gwt.testApplication.domain.IMessage;

public interface IMessageService 
{
	//----
	// Constant
	//----
	/**
	 * The IoC name
	 */
	public static final String NAME = "messageService";
	
	//-------------------------------------------------------------------------
	//
	// Public interface
	//
	//-------------------------------------------------------------------------
	/**
	 * Load the last posted message
	 */
	public IMessage loadLastMessage();
	
	/**
	 * Load all the posted messages
	 * @param startIndex first index of the message to load
	 * @param maxResult max number of message to load
	 * @return a list of IMessage
	 */
	public List<IMessage> loadAllMessage(int startIndex, int maxResult);
	
	/**
	 * Count all posted messages
	 */
	public int countAllMessage();
	
	/**
	 * Load the complete message, with associations
	 */
	public IMessage loadMessageDetails(IMessage message);
	
	/**
	 * Save the argument message
	 */
	public void saveMessage(IMessage message);
	
	/**
	 * Delete the argument message
	 */
	public void deleteMessage(IMessage message);

}