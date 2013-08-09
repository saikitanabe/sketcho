package net.sf.hibernate4gwt.testApplication.client;

import net.sf.hibernate4gwt.testApplication.client.login.LoginPanel;

/**
 * Static helper for test application unit test
 * @author bruno.marchesson
 *
 */
public class TestHelper
{
	/**
	 * @return the test application, logged in as 'junit'
	 */
	public static TestApplication createLoggedApplication()
	{
		TestApplication application = new TestApplication();
		application.onModuleLoad();
		
		// log as junit
		LoginPanel loginPanel = application.getLoginPanel();
		loginPanel.getLoginTextBox().setText("junit");
		loginPanel.getPasswordTextBox().setText("junit");
		loginPanel.getConnectButton().click();
		
		return application;
	}
}
