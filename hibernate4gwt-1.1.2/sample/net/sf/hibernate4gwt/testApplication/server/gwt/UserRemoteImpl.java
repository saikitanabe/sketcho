/**
 * 
 */
package net.sf.hibernate4gwt.testApplication.server.gwt;

import java.util.List;

import net.sf.hibernate4gwt.core.HibernateBeanManager;
import net.sf.hibernate4gwt.gwt.HibernateRemoteService;
import net.sf.hibernate4gwt.testApplication.client.user.UserRemote;
import net.sf.hibernate4gwt.testApplication.domain.IUser;
import net.sf.hibernate4gwt.testApplication.domain.dto.UserDTO;
import net.sf.hibernate4gwt.testApplication.server.ApplicationContext;
import net.sf.hibernate4gwt.testApplication.server.ApplicationContext.Configuration;
import net.sf.hibernate4gwt.testApplication.server.domain.User;
import net.sf.hibernate4gwt.testApplication.server.service.IIdentificationService;
import net.sf.hibernate4gwt.testApplication.server.service.IUserService;

/**
 * User remote service
 * @author bruno.marchesson
 *
 */
public class UserRemoteImpl extends HibernateRemoteService
							implements UserRemote
{
	//----
	// Attributes
	//----
	/**
	 * Serialisation ID																																	
	 */
	private static final long serialVersionUID = 5921199904102343567L;
	
	/**
	 * The identification Service
	 */
	private IIdentificationService identificationService;
	
	/**
	 * The user service
	 */
	private IUserService userService;
	
	//----
	// Properties
	//----
	/**
	 * @return the identification  Service
	 */
	public IIdentificationService getIdentificationService() {
		return identificationService;
	}

	/**
	 * @param identifcationService the identification Service to set
	 */
	public void setIdentifitcationService(IIdentificationService identificationService) {
		this.identificationService = identificationService;
	}

	//-------------------------------------------------------------------------
	//
	// Constructor
	//
	//-------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public UserRemoteImpl()
	{
		setBeanManager((HibernateBeanManager)ApplicationContext.getInstance().getBean("hibernateBeanManager"));
		identificationService = (IIdentificationService) ApplicationContext.getInstance().getBean(IIdentificationService.NAME);
		userService = (IUserService) ApplicationContext.getInstance().getBean(IUserService.NAME);
	}

	//-------------------------------------------------------------------------
	//
	// Team management
	//
	//-------------------------------------------------------------------------
	/**
	 * Return the user list
	 * @gwt.typeArgs <net.sf.hibernate4gwt.testApplication.domain.IUser>
	 */
	public List getUserList()
	{
		if (ApplicationContext.getInstance().getConfiguration() != Configuration.java5)
		{
		//	Just get all users
		//
			return identificationService.loadUserList();
		}
		else
		{
		//	Java 5 : explicit clone needed
		//
			return (List) clone(identificationService.loadUserList());
		}
	}

	/**
	 * Save the argument user
	 */
	public IUser saveUser(IUser user)
	{
		if (ApplicationContext.getInstance().getConfiguration() != Configuration.java5)
		{
		//	Just save the user
		//
			userService.saveUser(user);
			return user;
		}
		else
		{
		//	Java 5 : explicit clone needed
		//
			User mergedUser = (User) merge(user);
			userService.saveUser(mergedUser);
			return (UserDTO) clone(mergedUser);
		}
	}
}
