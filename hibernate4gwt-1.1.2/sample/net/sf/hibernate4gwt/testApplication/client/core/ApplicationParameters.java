package net.sf.hibernate4gwt.testApplication.client.core;

import net.sf.hibernate4gwt.testApplication.client.TestApplication;
import net.sf.hibernate4gwt.testApplication.domain.Configuration;
import net.sf.hibernate4gwt.testApplication.domain.IUser;

/**
 * Global application parameters
 * @author bruno.marchesson
 */
public class ApplicationParameters
{
	//----
	// Singleton
	//----
	/**
	 * The unique instance of the singleton
	 */
	private static ApplicationParameters instance = null;

	/**
	 * @return the instance of the singleton
	 */
	public static ApplicationParameters getInstance()
	{
		if (instance == null)
		{
			instance = new ApplicationParameters();
		}
		return instance;
	}
	
	//----
	// Attributes
	//----
	/**
	 * The root application
	 */
	private TestApplication application;
	
	/**
	 * The authenticated user
	 */
	private IUser user;
	
	/**
	 * The server configration
	 */
	private Configuration serverConfiguration;

	//----
	// Properties
	//----
	/**
	 * The application
	 */
	public TestApplication getApplication()
	{
		return application;
	}

	/**
	 * @param application the top application
	 */
	public void setApplication(TestApplication application)
	{
		this.application = application;
	}

	/**
	 * @return the user
	 */
	public IUser getUser()
	{
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(IUser user)
	{
		this.user = user;
	}
	
	/**
	 * @return the serverConfiguration
	 */
	public Configuration getServerConfiguration() {
		return serverConfiguration;
	}

	/**
	 * @param serverConfiguration the serverConfiguration to set
	 */
	public void setServerConfiguration(Configuration serverConfiguration) {
		this.serverConfiguration = serverConfiguration;
	}

	//-------------------------------------------------------------------------
	//
	// Constructor
	//
	//-------------------------------------------------------------------------
	/**
	 * Protected constructor
	 */
	protected ApplicationParameters()
	{
	}
}
