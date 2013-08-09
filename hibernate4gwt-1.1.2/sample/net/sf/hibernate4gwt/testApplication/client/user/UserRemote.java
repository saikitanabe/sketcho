/**
 * 
 */
package net.sf.hibernate4gwt.testApplication.client.user;

import java.util.List;

import net.sf.hibernate4gwt.testApplication.domain.IUser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.ServiceDefTarget;


/**
 * User update
 * @author bruno.marchesson
 *
 */
public interface UserRemote extends RemoteService
{
	/**
	 * Utility class for simplifing access to the instance of async service.
	 */
	public static class Util {
		private static UserRemoteAsync instance;
		public static UserRemoteAsync getInstance(){
			if (instance == null) {
				instance = (UserRemoteAsync) GWT.create(UserRemote.class);
				ServiceDefTarget target = (ServiceDefTarget) instance;
				target.setServiceEntryPoint(GWT.getModuleBaseURL() + "/UserRemote");
			}
			return instance;
		}
	}
	
	/**
	 * @return the user list
	 * @gwt.typeArgs <net.sf.hibernate4gwt.testApplication.domain.IUser>
	 */
	public List getUserList();
	
	/**
	 * Save the argument user
	 * @param user the user to save
	 * @return
	 */
	public IUser saveUser(IUser user);
	
}
