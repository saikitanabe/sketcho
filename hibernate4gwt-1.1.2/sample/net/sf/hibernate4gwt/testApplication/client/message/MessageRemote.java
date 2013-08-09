/**
 * 
 */
package net.sf.hibernate4gwt.testApplication.client.message;

import java.util.List;

import net.sf.hibernate4gwt.testApplication.domain.IMessage;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.ServiceDefTarget;


/**
 * Message remote service
 * @author bruno.marchesson
 *
 */
public interface MessageRemote extends RemoteService
{
	/**
	 * Utility class for simplifing access to the instance of async service.
	 */
	public static class Util {
		private static MessageRemoteAsync instance;
		public static MessageRemoteAsync getInstance(){
			if (instance == null) {
				instance = (MessageRemoteAsync) GWT.create(MessageRemote.class);
				ServiceDefTarget target = (ServiceDefTarget) instance;
				target.setServiceEntryPoint(GWT.getModuleBaseURL() + "/MessageRemote");
			}
			return instance;
		}
	}
	
	/**
	 * Get the last messages
	 * @gwt.typeArgs <net.sf.hibernate4gwt.testApplication.domain.IMessage>
	 */
	public List getAllMessages(int startIndex, int maxResult);
	
	/**
	 * Get the messages count
	 */
	public int countAllMessages();
	
	/**
	 * Get the last message
	 */
	public IMessage getLastMessage();
	
	/**
	 * Get the message details
	 */
	public IMessage getMessageDetails(IMessage message);
	
	/**
	 * Save the argument message
	 */
	public IMessage saveMessage(IMessage message);
	
	/**
	 * Delete the argument message
	 */
	public void deleteMessage(IMessage message);
}
