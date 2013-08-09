/**
 * 
 */
package net.sf.hibernate4gwt.exception;

/**
 * Pojo store exception
 * @author bruno.marchesson
 *
 */
public class PojoStoreException extends RuntimeException
{
	//----
	// Attribute
	//----
	/**
	 * Serialisation ID
	 */
	private static final long serialVersionUID = 2652532805114101598L;
	
	/**
	 * The exception object
	 */
	private Object _object;
	
	//----
	// Property
	//----
	/**
	 * @return the object
	 */
	public final Object getObject()
	{
		return _object;
	}

	//----
	// Constructor
	//----
	/**
	 * Base constructor
	 */
	public PojoStoreException(String message, Object object)
	{
		super(message);
		_object = object;
	}
}
