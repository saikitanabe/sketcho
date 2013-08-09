package net.sf.hibernate4gwt.testApplication.client;

import net.sf.hibernate4gwt.testApplication.client.core.ApplicationParameters;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.Timer;

/**
 * Login remote service test case
 * @author bruno.marchesson
 *
 */
public class LoginRemoteTest extends GWTTestCase
{
	//-------------------------------------------------------------------------
	//
	// Required method
	//
	// -------------------------------------------------------------------------
	/**
	 * Must refer to a valid module that sources this class.
	 */
	public String getModuleName() {
		return "net.sf.hibernate4gwt.testApplication.TestApplication";
	}

	// -----------------------------------------------------------------------
	//
	// Test method
	//
	// -----------------------------------------------------------------------
	/**
	 * Test login
	 */
	public void testLogin()
	{
	// 	Load main module
	//
		TestHelper.createLoggedApplication();

	// 	Test that the user is connected
	//  (asynchronous RPC call)
	//
		Timer timer = new Timer() {
			public void run() {
				assertNotNull(ApplicationParameters.getInstance().getUser());

				// tell the test system the test is now done
				finishTest();
			}
		};

		// Set a delay period significantly longer than the
		// event is expected to take.
		delayTestFinish(10000);

		// Schedule the event and return control to the test system.
		timer.schedule(8000);
	}
}
