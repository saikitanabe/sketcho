/**
 * 
 */
package net.sf.hibernate4gwt.testApplication.server.service.implementation;

import java.util.Date;

import net.sf.hibernate4gwt.testApplication.domain.IMessage;
import net.sf.hibernate4gwt.testApplication.domain.IUser;
import net.sf.hibernate4gwt.testApplication.server.ApplicationContext;
import net.sf.hibernate4gwt.testApplication.server.MessageHelper;
import net.sf.hibernate4gwt.testApplication.server.ApplicationContext.Configuration;
import net.sf.hibernate4gwt.testApplication.server.dao.IUserDAO;
import net.sf.hibernate4gwt.testApplication.server.service.IStartupService;

/**
 * Service used to initialize the underlying data for demo application
 * @author bruno.marchesson
 *
 */
public class StartupService implements IStartupService
{
	//----
	// Attributes
	//----
	/**
	 * The User DAO
	 */
	private IUserDAO _userDAO;
	
	//----
	// Properties
	//----
	/**
	 * @return the userDAO
	 */
	public IUserDAO getUserDAO() {
		return _userDAO;
	}

	/**
	 * @param userDAO the userDAO to set
	 */
	public void setUserDAO(IUserDAO userDAO) {
		_userDAO = userDAO;
	}
	
	//-------------------------------------------------------------------------
	//
	// Public interface
	//
	//-------------------------------------------------------------------------
	/* (non-Javadoc)
	 * @see net.sf.hibernate4gwt.testApplication.server.service.implementation.IStartupService#isInitialized()
	 */
	public boolean isInitialized()
	{
		return (_userDAO.countAll() > 0);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.hibernate4gwt.testApplication.server.service.implementation.IStartupService#initialize()
	 */
	public void initialize()
	{
	//	Create guest user (no password)
	//
		IUser guestUser = createUser();
		guestUser.setLogin("guest");
		guestUser.setFirstName("No");
		guestUser.setLastName("name");
		
		// create welcome message
		IMessage guestMessage = createMessage();
		guestMessage.setMessage("Welcome in hibernate4gwt sample application");
		guestMessage.setDate(new Date());
		MessageHelper.computeKeywords(guestMessage);
		guestUser.addMessage(guestMessage);
		
		// save user (message is cascaded)
		_userDAO.saveUser(guestUser);
		
	//	Create JUnit user
	//
		IUser junitUser = createUser();
		junitUser.setLogin("junit");
		junitUser.setPassword("junit");
		junitUser.setFirstName("Unit");
		junitUser.setLastName("Test");
		
		// create message
		IMessage junitMessage = createMessage();
		junitMessage.setMessage("JUnit first message");
		junitMessage.setDate(new Date());
		MessageHelper.computeKeywords(junitMessage);
		junitUser.addMessage(junitMessage);
		
		// save user (message is cascaded)
		_userDAO.saveUser(junitUser);
	}
	
	//--------------------------------------------------------------------------
	//
	// Internal methods
	//
	//--------------------------------------------------------------------------
	/**
	 * Create a new user (depends on the server configuration)
	 */
	private IUser createUser()
	{
		Configuration configuration = ApplicationContext.getInstance().getConfiguration();
		
		if (configuration == Configuration.stateless) 
		{
			// stateless
			return new net.sf.hibernate4gwt.testApplication.domain.stateless.User();
		}
		else if (configuration == Configuration.stateful) 
		{
			// stateful
			return new net.sf.hibernate4gwt.testApplication.domain.stateful.User();
		}
		else if (configuration == Configuration.proxy) 
		{
			// dynamic proxy
			return new net.sf.hibernate4gwt.testApplication.domain.proxy.User();
		}
		else
		{
			// Java5
			return new net.sf.hibernate4gwt.testApplication.server.domain.User();
		}
	}
	
	/**
	 * Create a new message (depends on the server configuration)
	 */
	private IMessage createMessage()
	{
		Configuration configuration = ApplicationContext.getInstance().getConfiguration();
		
		if (configuration == Configuration.stateless) 
		{
			// stateless
			return new net.sf.hibernate4gwt.testApplication.domain.stateless.Message();
		}
		else if (configuration == Configuration.stateful) 
		{
			// stateful
			return new net.sf.hibernate4gwt.testApplication.domain.stateful.Message();
		}
		else if (configuration == Configuration.proxy) 
		{
			// dynamic proxy
			return new net.sf.hibernate4gwt.testApplication.domain.proxy.Message();
		}
		else
		{
			// Java5
			return new net.sf.hibernate4gwt.testApplication.server.domain.Message();
		}
	}
}
