package net.sf.hibernate4gwt.testApplication.client.user;

import net.sf.hibernate4gwt.testApplication.domain.IUser;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface UserRemoteAsync
{
	/**
	 * Returns the user list
	 */
	public void getUserList(AsyncCallback callback);
	
	/**
	 * Save the argument user
	 * @param user the user to save
	 * @return the saved user
	 */
	public void saveUser(IUser user, AsyncCallback callback);
}
