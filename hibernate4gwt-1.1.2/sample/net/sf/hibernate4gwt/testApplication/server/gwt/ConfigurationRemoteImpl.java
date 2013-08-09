/**
 * 
 */
package net.sf.hibernate4gwt.testApplication.server.gwt;

import net.sf.hibernate4gwt.core.HibernateBeanManager;
import net.sf.hibernate4gwt.gwt.HibernateRemoteService;
import net.sf.hibernate4gwt.testApplication.client.configuration.ConfigurationRemote;
import net.sf.hibernate4gwt.testApplication.domain.Configuration;
import net.sf.hibernate4gwt.testApplication.server.ApplicationContext;
import net.sf.hibernate4gwt.testApplication.server.service.IStartupService;

/**
 * Configuration remote service
 * @author bruno.marchesson
 *
 */
public class ConfigurationRemoteImpl extends HibernateRemoteService
							implements ConfigurationRemote
{
	//----
	// Attributes
	//----
	/**
	 * Serialisation ID																																	
	 */
	private static final long serialVersionUID = 8336011091333062487L;
	
	/**
	 * The startup service
	 */
	private IStartupService _startupService;
	
	//----
	// Properties
	//----
	/**
	 * @return the startup Service
	 */
	public IStartupService getStartupService() {
		return _startupService;
	}

	/**
	 * @param service the startup Service to set
	 */
	public void setStartupService(IStartupService service) {
		_startupService = service;
	}

	//-------------------------------------------------------------------------
	//
	// Constructor
	//
	//-------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public ConfigurationRemoteImpl()
	{
		setBeanManager((HibernateBeanManager)ApplicationContext.getInstance().getBean("hibernateBeanManager"));
		_startupService = (IStartupService) ApplicationContext.getInstance().getBean(IStartupService.NAME);
	}

	//-------------------------------------------------------------------------
	//
	// Team management
	//
	//-------------------------------------------------------------------------
	/**
	 * @return the server configuration
	 */
	public Configuration getServerConfiguration()
	{
	//	Check server initialisation
	//
		if (_startupService.isInitialized() == false)
		{
		//	First call on server
		//
			_startupService.initialize();
		}
		
	//	Return server configuration
	//
		Configuration configuration = new Configuration();
		configuration.setName(ApplicationContext.getInstance().getConfiguration().toString());
		return configuration;
	}
}
