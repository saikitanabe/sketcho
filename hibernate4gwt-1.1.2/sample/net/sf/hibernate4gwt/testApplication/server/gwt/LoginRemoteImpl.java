/**
 * 
 */
package net.sf.hibernate4gwt.testApplication.server.gwt;

import net.sf.hibernate4gwt.core.HibernateBeanManager;
import net.sf.hibernate4gwt.gwt.HibernateRemoteService;
import net.sf.hibernate4gwt.testApplication.client.login.LoginRemote;
import net.sf.hibernate4gwt.testApplication.domain.IUser;
import net.sf.hibernate4gwt.testApplication.domain.dto.UserDTO;
import net.sf.hibernate4gwt.testApplication.server.ApplicationContext;
import net.sf.hibernate4gwt.testApplication.server.ApplicationContext.Configuration;
import net.sf.hibernate4gwt.testApplication.server.service.IIdentificationService;

/**
 * Login remote service
 * @author bruno.marchesson
 *
 */
public class LoginRemoteImpl extends HibernateRemoteService
							implements LoginRemote
{
	//----
	// Attributes
	//----
	/**
	 * Serialisation ID																																	
	 */
	private static final long serialVersionUID = -5399410538322914497L;
	
	/**
	 * The message Service
	 */
	private IIdentificationService identificationService;
	
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
	public LoginRemoteImpl()
	{
		setBeanManager((HibernateBeanManager)ApplicationContext.getInstance().getBean("hibernateBeanManager"));
		identificationService = (IIdentificationService) ApplicationContext.getInstance().getBean(IIdentificationService.NAME);
	}

	//-------------------------------------------------------------------------
	//
	// Team management
	//
	//-------------------------------------------------------------------------
	/**
	 * Return the last message
	 */
	public IUser authenticate(String login, String password)
	{
		if (ApplicationContext.getInstance().getConfiguration() != Configuration.java5)
		{
		//	Just authenticate the user
		//
			return identificationService.authenticate(login, password);
		}
		else
		{
		//	JAVA5 : explicit clone needed
		//
			return (UserDTO) clone(identificationService.authenticate(login, password));
		}
	}
}
