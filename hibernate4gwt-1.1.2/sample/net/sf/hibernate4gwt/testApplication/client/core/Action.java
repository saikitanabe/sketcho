/**
 * 
 */
package net.sf.hibernate4gwt.testApplication.client.core;

import com.google.gwt.user.client.History;

/**
 * Action class for navigation
 * @author bruno.marchesson
 *
 */
public class Action
{
	//----
	// Constants
	//----
	/**
	 * Name and data separator
	 */
	private static final String SEPARATOR= "$$";
	
	/**
	 * Connexion action
	 */
	public static final String CONNECTED = "connected";
	
	/**
	 * Authentication action
	 */
	public static final String AUTHENTICATION = "authentication";
	
	//----
	// Attributes
	//----
	/**
	 * The command name
	 */
	private String _name;
	
	/**
	 * The associated data
	 */
	private Object _data;

	//----
	// Properties
	//----
	public Object getData()
	{
		return _data;
	}

	public void setData(Object data)
	{
		this._data = data;
	}

	public String getName()
	{
		return _name;
	}

	//-------------------------------------------------------------------------
	//
	// Constructors
	//
	//-------------------------------------------------------------------------
	/**
	 * Complete constructor
	 */
	public Action(String name, Object data)
	{
		_name = name;
		_data = data;
	}
	
	/**
	 * Basic constructor
	 */
	public Action(String name)
	{
		this(name, null);
	}
	
	//-------------------------------------------------------------------------
	//
	// Public interface
	//
	//-------------------------------------------------------------------------
	/**
	 * Execute the action
	 */
	public void execute()
	{
		History.newItem(toString());
	}
	
	/**
	 * Static helper for action execution
	 * @param name the name of the action to execute
	 */
	public static void executeAction(String name)
	{
		executeAction(name, null);
	}
	
	/**
	 * Static helper for action execution
	 * @param name the name of the action to execute
	 * @param data the data associated with the action
	 */
	public static void executeAction(String name, Object data)
	{
		new Action(name, data).execute();
	}

	//-------------------------------------------------------------------------
	//
	// Serialization interface
	//
	//-------------------------------------------------------------------------
	/**
	 * String conversion
	 */
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		result.append(_name);
		if (_data != null)
		{
			result.append(SEPARATOR);
			result.append(_data.toString());
		}
		
		return result.toString();
	}
	
	/**
	 * String deserialisation
	 */
	public static Action fromString(String commandToken)
	{
		String name = null;
		String data = null;
		
	//	Deserialisation
	//
		int index = commandToken.indexOf(SEPARATOR);
		if (index == -1)
		{
			name = commandToken;
		}
		else
		{
			name = commandToken.substring(0, index);
			data = commandToken.substring(index + SEPARATOR.length());
		}
		
	//	Create new command
	//
		return new Action(name, data);
	}
}
