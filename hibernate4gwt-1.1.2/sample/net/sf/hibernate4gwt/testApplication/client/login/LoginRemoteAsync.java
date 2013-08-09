package net.sf.hibernate4gwt.testApplication.client.login;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LoginRemoteAsync
{
	/**
	 * Authenticate a user
	 */
	public void authenticate(String login, String password, AsyncCallback callback);
}
