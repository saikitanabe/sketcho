package net.sf.hibernate4gwt.testApplication.server.service;

import net.sf.hibernate4gwt.testApplication.domain.IUser;

/**
 * Interface of the user service
 * @author bruno.marchesson
 *
 */
public interface IUserService 
{
	//----
	// Constant
	//----
	/**
	 * The IoC name
	 */
	public static final String NAME = "userService";
	
	//-------------------------------------------------------------------------
	//
	// Public interface
	//
	//-------------------------------------------------------------------------
	/**
	 * Save the argument user
	 */
	public void saveUser(IUser user);
	
}