package net.sf.hibernate4gwt.testApplication.client.message;

import net.sf.hibernate4gwt.testApplication.domain.IMessage;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface MessageRemoteAsync
{
	/**
	 * Get the last message
	 */
	public void getLastMessage(AsyncCallback callback);
	
	/**
	 * Get all the posted messages
	 */
	public void getAllMessages(int startIndex, int maxResult, AsyncCallback callback);
	
	/**
	 * Count all posted messages
	 */
	public void countAllMessages(AsyncCallback callback);
	
	/**
	 * Get the message details
	 */
	public void getMessageDetails(IMessage message, AsyncCallback callback);
	
	/**
	 * Save the argument message
	 */
	public void saveMessage(IMessage message, AsyncCallback callback);
	
	/**
	 * Delete the argument message
	 */
	public void deleteMessage(IMessage message, AsyncCallback callback);
}
