package net.sf.hibernate4gwt.testApplication.server.service;

/**
 * Interface of the application startup service
 * @author bruno.marchesson
 *
 */
public interface IStartupService
{
	//----
	// Constant
	//----
	/**
	 * The IoC name
	 */
	public static final String NAME = "startupService";
	
	//-------------------------------------------------------------------------
	//
	// Public interface
	//
	//-------------------------------------------------------------------------
	/**
	 * Indicates if the needed data has been initialized
	 */
	public boolean isInitialized();

	/**
	 * Initialize the underlying data
	 */
	public void initialize();

}