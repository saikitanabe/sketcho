/**
 * 
 */
package net.sf.hibernate4gwt.testApplication.client.login;

import net.sf.hibernate4gwt.testApplication.domain.IUser;
import net.sf.hibernate4gwt.testApplication.domain.exception.IdentificationException;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.ServiceDefTarget;


/**
 * User update
 * @author bruno.marchesson
 *
 */
public interface LoginRemote extends RemoteService
{
	/**
	 * Utility class for simplifing access to the instance of async service.
	 */
	public static class Util {
		private static LoginRemoteAsync instance;
		public static LoginRemoteAsync getInstance(){
			if (instance == null) {
				instance = (LoginRemoteAsync) GWT.create(LoginRemote.class);
				ServiceDefTarget target = (ServiceDefTarget) instance;
				target.setServiceEntryPoint(GWT.getModuleBaseURL() + "/LoginRemote");
			}
			return instance;
		}
	}
	
	/**
	 * Authenticate a user
	 */
	public IUser authenticate(String login, String password) throws IdentificationException;
	
}
