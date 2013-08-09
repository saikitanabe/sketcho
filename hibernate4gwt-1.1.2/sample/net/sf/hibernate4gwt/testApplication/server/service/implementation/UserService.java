/**
 * 
 */
package net.sf.hibernate4gwt.testApplication.server.service.implementation;

import net.sf.hibernate4gwt.testApplication.domain.IUser;
import net.sf.hibernate4gwt.testApplication.server.dao.IUserDAO;
import net.sf.hibernate4gwt.testApplication.server.service.IUserService;

/**
 * Implementation of the message service
 * @author bruno.marchesson
 *
 */
public class UserService implements IUserService
{
	//----
	// Attributes
	//----
	/**
	 * the associated DAO
	 */
	private IUserDAO userDAO;
	
	//----
	// Properties
	//----
	/**
	 * @return the userDAO
	 */
	public IUserDAO getUserDAO() {
		return userDAO;
	}

	/**
	 * @param userDAO the userDAO to set
	 */
	public void setUserDAO(IUserDAO userDAO) {
		this.userDAO = userDAO;
	}

	//-------------------------------------------------------------------------
	//
	// Implementation of the user service
	//
	//-------------------------------------------------------------------------
	/*
	 * (non-Javadoc)
	 * @see net.sf.hibernate4gwt.testApplication.server.service.IMessageService#saveMessage(net.sf.hibernate4gwt.testApplication.domain.IMessage)
	 */
	public void saveUser(IUser user)
	{
		userDAO.saveUser(user);
	}
}
