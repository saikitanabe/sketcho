package net.sf.hibernate4gwt.gwt;

import java.lang.reflect.Method;
import java.util.List;

import junit.framework.TestCase;
import net.sf.hibernate4gwt.core.HibernateBeanManager;
import net.sf.hibernate4gwt.core.beanlib.mapper.DirectoryClassMapper;
import net.sf.hibernate4gwt.core.store.stateful.InMemoryPojoStore;
import net.sf.hibernate4gwt.rebind.Gwt14ProxyGenerator;
import net.sf.hibernate4gwt.rebind.ProxyManager;
import net.sf.hibernate4gwt.rebind.xml.AdditionalCode;
import net.sf.hibernate4gwt.rebind.xml.AdditionalCodeReader;
import net.sf.hibernate4gwt.testApplication.domain.IUser;
import net.sf.hibernate4gwt.testApplication.server.ApplicationContext;
import net.sf.hibernate4gwt.testApplication.server.ApplicationContext.Configuration;
import net.sf.hibernate4gwt.testApplication.server.dao.IUserDAO;

import org.hibernate.SessionFactory;

import com.google.gwt.user.server.rpc.RPCCopy_GWT14;
import com.google.gwt.user.server.rpc.RPCRequest;

/**
 * Test call for Hibernate RPC Helper
 * @author bruno.marchesson
 */
public class HibernateRPCHelperTest extends TestCase
{
	//----
	// Attributes
	//----
	/**
	 * The associated bean manager
	 */
	private HibernateBeanManager _beanManager;
	//------------------------------------------------------------------------
	//
	// Test initialisation
	//
	//------------------------------------------------------------------------
	/**
	 * Test setup
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		
		// Hibernate Bean Manager
		_beanManager = new HibernateBeanManager();
		_beanManager.setSessionFactory((SessionFactory) ApplicationContext.getInstance().getBean("sessionFactory"));
		
		if (ApplicationContext.getInstance().getConfiguration() == Configuration.java5)
		{
			// Class mapper
			DirectoryClassMapper classMapper = new DirectoryClassMapper();
			classMapper.setRootDomainPackage("net.sf.hibernate4gwt.testApplication.server.domain");
			classMapper.setRootClonePackage("net.sf.hibernate4gwt.testApplication.domain.dto");
			classMapper.setCloneSuffix("DTO");
			
			_beanManager.setClassMapper(classMapper);	
		}
		else if (ApplicationContext.getInstance().getConfiguration() == Configuration.proxy)
		{
		//	Init Proxy Manager
		//
			AdditionalCode additionalCode = AdditionalCodeReader.readFromFile(Gwt14ProxyGenerator.ADDITIONAL_CODE);
			ProxyManager.getInstance().generateProxyClass(net.sf.hibernate4gwt.testApplication.domain.proxy.User.class, additionalCode);
		}
		else if (ApplicationContext.getInstance().getConfiguration() == Configuration.stateful)
		{
			_beanManager.setPojoStore(new InMemoryPojoStore());
		}
	}
	
	//-------------------------------------------------------------------------
	//
	// Test methods
	//
	//-------------------------------------------------------------------------
	/**
	 * Test input parameters substitution for Java5
	 */
	public final void testParseInputParameters()
	{
	//	Test data
	//
		// Input parameters
		IUserDAO userDAO = (IUserDAO)ApplicationContext.getInstance().getBean("userDAO");
		
		// GWT is sending back clone POJO
		List<IUser> userList = (List<IUser>)_beanManager.clone(userDAO.loadAll());
		IUser firstUser = userList.get(0);
		
		Object[] parameters = new Object[] { firstUser, userList };
		
		// RPC Request
		Method method = this.getClass().getMethods()[0];
		RPCRequest request = new RPCRequest(method, parameters, RPCCopy_GWT14.getDefaultSerializationPolicy());
		
	//	Call Method to test
	//
		HibernateRPCHelper.parseInputParameters(request, _beanManager, null);
		
	//	Post test verification
	//
		if (ApplicationContext.getInstance().getConfiguration() == Configuration.java5)
		{
		//	No substitution
		//
			assertSame(firstUser, request.getParameters()[0]);
			assertSame(userList, request.getParameters()[1]);
		}
		else
		{
		//	Check subsitution : user must be equals (same ID) but not same
		//
			assertNotSame(firstUser, request.getParameters()[0]);
			assertEquals(firstUser.getId(), ((IUser)request.getParameters()[0]).getId());
			
			List<IUser> modifiedList = (List<IUser>)request.getParameters()[1];
			assertNotSame(userList, modifiedList);
			assertEquals(userList.size(), modifiedList.size());
			for(IUser user : userList)
			{
			//	Get clone user
			//
				boolean found = false;
				for (IUser modifiedUser : modifiedList)
				{
					if (modifiedUser.getId() == user.getId())
					{
						assertNotSame(modifiedUser, user);
						found = true;
						break;
					}
				}
				assertTrue("User " + user.getId() + " not found !", found);
			}
		}
	}

	public final void testParseReturnValue()
	{
	//	Test data
	//
		// Input parameters
		IUserDAO userDAO = (IUserDAO)ApplicationContext.getInstance().getBean("userDAO");
		
		// Sending POJO to GWT
		List<IUser> userList = userDAO.loadAll();
		if (ApplicationContext.getInstance().getConfiguration() == Configuration.java5)
		{
		//	Explicit clone needed
		//
			userList = (List<IUser>)_beanManager.clone(userList);
		}
		
	//	Call Method to test
	//
		List<IUser> cloneList = (List<IUser>) HibernateRPCHelper.parseReturnValue(userList, _beanManager);
		
	//	Post test verification
	//
		if (ApplicationContext.getInstance().getConfiguration() == Configuration.java5)
		{
		//	No substitution : list can be changed but must contains the same elements
		//
			assertEquals(userList.size(), cloneList.size());
			for(IUser user : userList)
			{
				assertFalse(cloneList.indexOf(user) == -1);
			}
		}
		else
		{
		//	Check subsitution : users must be equals (same ID) but not same
		//
			assertNotSame(userList, cloneList);
			assertEquals(userList.size(), cloneList.size());
			for(IUser user : userList)
			{
			//	Get clone user
			//
				boolean found = false;
				for (IUser cloneUser : cloneList)
				{
					if (cloneUser.getId() == user.getId())
					{
						assertNotSame(cloneUser, user);
						found = true;
						break;
					}
				}
				assertTrue("User " + user.getId() + " not found !", found);
			}
		}
	}

}
