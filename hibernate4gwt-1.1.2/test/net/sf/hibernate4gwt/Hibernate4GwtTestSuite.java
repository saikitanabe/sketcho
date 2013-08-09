package net.sf.hibernate4gwt;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.sf.hibernate4gwt.core.CloneTest;
import net.sf.hibernate4gwt.core.TimestampTest;
import net.sf.hibernate4gwt.gwt.HibernateRPCHelperTest;
import net.sf.hibernate4gwt.testApplication.client.LoginRemoteTest;
import net.sf.hibernate4gwt.testApplication.client.MessageRemoteTest;
import net.sf.hibernate4gwt.testApplication.server.ApplicationContext;

/**
 * Complete hibernate4gwt test suite
 * @author BMARCHESSON
 *
 */
public class Hibernate4GwtTestSuite extends TestSuite
{
	/**
	 * Definition of the test suite
	 */
	public static Test suite()
	{
		TestSuite suite = new TestSuite("hibernate4gwt test suite : " + 
										ApplicationContext.getInstance().getConfiguration());
		
	//	Timestamp test
	//
		suite.addTestSuite(TimestampTest.class);
		
	//	GWT test cases
	//
		suite.addTestSuite(LoginRemoteTest.class);
		suite.addTestSuite(MessageRemoteTest.class);
		
	//	Server clone and merge test case
	//
		suite.addTestSuite(CloneTest.class);
		
	//	Hibernate RPC Helper test case
	//
		suite.addTestSuite(HibernateRPCHelperTest.class);
		
		return suite;
	}
}
