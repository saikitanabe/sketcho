package net.sf.hibernate4gwt.testApplication.server.service;

import java.util.List;

import net.sf.hibernate4gwt.testApplication.domain.IUser;

/**
 * Interface of the authentication service
 * @author bruno.marchesson
 *
 */
public interface IIdentificationService {

	//----
	// Constant
	//----
	/**
	 * The IoC name
	 */
	public static final String NAME = "identificationService";
	
	//-------------------------------------------------------------------------
	//
	// Public interface
	//
	//-------------------------------------------------------------------------
	/**
	 * Authenticates the user from its login and password
	 */
	public IUser authenticate(String login, String password);
	
	/**
	 * @return the list of all users
	 */
	public List<IUser> loadUserList();

}