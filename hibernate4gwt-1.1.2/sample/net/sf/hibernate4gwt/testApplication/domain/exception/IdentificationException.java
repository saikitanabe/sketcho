/**
 * 
 */
package net.sf.hibernate4gwt.testApplication.domain.exception;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Identification exception
 * @author bruno.marchesson
 *
 */
public class IdentificationException extends RuntimeException 
									 implements IsSerializable
{
	//----
	// Attributes
	//----
	/**
	 * Serializartion ID
	 */
	private static final long serialVersionUID = -3974804157089131282L;

	/**
	 * The failed user name
	 */
	private String _userName;
	
	//----
	// Properties
	//----
	/**
	 * @return the user name
	 */
	public String getUserName()
	{
		return _userName;
	}
	
	/**
	 * Sets the user name
	 */
	public void setUserName(String userName)
	{
		_userName = userName;
	}

	//-----
	// Constructor
	//----
	/**
	 * Empty constructor
	 */
	public IdentificationException()
	{
	}
	
	/**
	 * Constructor
	 */
	public IdentificationException(String userName)
	{
		_userName = userName;
	}
	
	//----
	// Public interface
	//----
	public String getMessage()
	{
		return "Invalid user or password for " + _userName;
	}
}
