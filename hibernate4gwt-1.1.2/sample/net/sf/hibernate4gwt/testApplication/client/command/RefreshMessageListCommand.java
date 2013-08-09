/**
 * 
 */
package net.sf.hibernate4gwt.testApplication.client.command;

import net.sf.hibernate4gwt.testApplication.client.core.ApplicationParameters;

import com.google.gwt.user.client.Command;

/**
 * Force message list refresh
 * @author bruno.marchesson
 *
 */
public class RefreshMessageListCommand implements Command
{

	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.Command#execute()
	 */
	public void execute()
	{ 
	//	Just refresh message board
	//
		ApplicationParameters.getInstance().getApplication().getMessageBoard().refresh();
	}

}
