/**
 * 
 */
package net.sf.hibernate4gwt.core;

import java.util.List;

import net.sf.hibernate4gwt.testApplication.domain.IMessage;
import net.sf.hibernate4gwt.testApplication.domain.IUser;

/**
 * Wrapping (non persistent) class containing persistent classes.
 * Must be cloned and merged as a regular persistent bean.
 * @author bruno.marchesson
 *
 */
public class WrappingClass
{
	//----
	// Attributes
	//----
	/**
	 * The associated user
	 */
	protected IUser _user;
	
	/**
	 * The associated message list
	 */
	protected List<IMessage> _messageList;
	
	/**
	 * User (and associated messages) list
	 */
	protected List<IUser> _completeUserList;
	
	/**
	 * User (without associated messages) list
	 */
	protected List<IUser> _userList;

	//----
	// Properties
	//----
	/**
	 * @return the user
	 */
	public IUser getUser()
	{
		return _user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(IUser user)
	{
		_user = user;
	}

	/**
	 * @return the messageList
	 */
	public List<IMessage> getMessageList()
	{
		return _messageList;
	}

	/**
	 * @param list the messageList to set
	 */
	public void setMessageList(List<IMessage> list)
	{
		_messageList = list;
	}
	

	public List<IUser> getCompleteUserList() {
		return _completeUserList;
	}

	public void setCompleteUserList(List<IUser> list)
	{
		_completeUserList = list;
	}
	
	public List<IUser> getUserList() {
		return _userList;
	}

	public void setUserList(List<IUser> list)
	{
		_userList = list;
	}
}