/**
 * 
 */
package net.sf.hibernate4gwt.testApplication.client.core;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Default implementation of the AsyncCallback.
 * Implements the onFailure method.
 * @author bruno.marchesson
 *
 */
public abstract class DefaultCallback implements AsyncCallback
{
	//-------------------------------------------------------------------------
	//
	// Public interface
	//
	//-------------------------------------------------------------------------
	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.rpc.AsyncCallback#onFailure(java.lang.Throwable)
	 */
	public void onFailure(Throwable caught)
	{
	//	Message to display
	//
		String message = caught.getMessage();
		if ((message == null) ||
			(message.length() == 0))
		{
			message = caught.toString();
		}
		
	//	Display from the top application
	//
		ApplicationParameters.getInstance().getApplication().displayErrorMessage(message);
	}
}
