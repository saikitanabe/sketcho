package net.sf.hibernate4gwt.core.hibernate;

import java.io.Serializable;

import org.hibernate.SessionFactory;

import net.sf.hibernate4gwt.core.LazyKiller;
import net.sf.hibernate4gwt.core.hibernate.HibernateUtil;
import net.sf.hibernate4gwt.exception.NotHibernateObjectException;
import net.sf.hibernate4gwt.exception.TransientHibernateObjectException;
import net.sf.hibernate4gwt.testApplication.domain.Configuration;
import net.sf.hibernate4gwt.testApplication.domain.IUser;
import net.sf.hibernate4gwt.testApplication.domain.stateless.Message;
import net.sf.hibernate4gwt.testApplication.domain.stateless.User;
import net.sf.hibernate4gwt.testApplication.server.ApplicationContext;
import net.sf.hibernate4gwt.testApplication.server.dao.IUserDAO;
import junit.framework.TestCase;

/**
 * Hibernate Helper test case
 * @author bruno.marchesson
 *
 */
public class HibernateUtilTest extends TestCase
{
	//-------------------------------------------------------------------------
	//
	// Test init
	//
	//-------------------------------------------------------------------------
	/**
	 * Test initialisation
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
		
		// Retrieve Hibernate session factory
		ApplicationContext.getInstance().setConfiguration(ApplicationContext.Configuration.stateless);
		
		SessionFactory factory = (SessionFactory) ApplicationContext.getInstance().getBean("sessionFactory");
		HibernateUtil.getInstance().setSessionFactory(factory);
	}

	//-------------------------------------------------------------------------
	//
	// Test methods
	//
	//-------------------------------------------------------------------------
	/**
	 * Test ID retrieving
	 */
	public final void testGetIdObject()
	{
	//	Get test user
	//
		IUserDAO userDAO = (IUserDAO) ApplicationContext.getInstance().getBean("userDAO");
		IUser user = userDAO.loadUser(Integer.valueOf(1));
		assertNotNull(user);
		
	//	Test ID retrieving
	//
		assertEquals(user.getId(), HibernateUtil.getInstance().getId(user));
		
	//	Error test on transient object
	//
		user = new User();
		try
		{
			HibernateUtil.getInstance().getId(user);
			fail("Expected an exception on transient object");
		}
		catch(TransientHibernateObjectException ex)
		{ /* expected behavior */ }
		
	//	Error test on non Hibernate object
	//
		Configuration configuration = new Configuration();
		try
		{
			HibernateUtil.getInstance().getId(configuration);
			fail("Expected an exception on not Hibernate object");
		}
		catch(NotHibernateObjectException ex)
		{ /* expected behavior */ }
		
	}

	/**
	 * Test Hibernate POJO checking
	 */
	public final void testIsPersistentPojo()
	{
	//	Get test user
	//
		IUserDAO userDAO = (IUserDAO) ApplicationContext.getInstance().getBean("userDAO");
		IUser user = userDAO.loadUser(Integer.valueOf(1));
		assertNotNull(user);
		
	//	Test ID retrieving
	//
		assertTrue(HibernateUtil.getInstance().isPersistentPojo(user));
		
	//	Test on transient object
	//
		Message message = new Message();
		assertTrue(HibernateUtil.getInstance().isPersistentPojo(message));
		
	//	Error test on non Hibernate object
	//
		Configuration configuration = new Configuration();
		assertFalse(HibernateUtil.getInstance().isPersistentPojo(configuration));
	}

	/**
	 * Test Hibernate class checking
	 */
	public final void testIsHibernateClass()
	{
		assertTrue(HibernateUtil.getInstance().isPersistentClass(User.class));
		assertTrue(HibernateUtil.getInstance().isPersistentClass(Message.class));
		assertFalse(HibernateUtil.getInstance().isPersistentClass(Configuration.class));
	}
	
	/**
	 * Test entity loading
	 * @param sessionFactory
	 * @param hibernatePojo
	 */
	protected void testLoad(SessionFactory sessionFactory, Object hibernatePojo)
	{
	//	Hibernate helper init
	//
		HibernateUtil.getInstance().setSessionFactory(sessionFactory);
		
	//	Clone hibernate pojo
	//
		Object clonePojo = new LazyKiller().detach(hibernatePojo);
		
	//	Load the hibernate POJO from its clone
	//
		HibernateUtil.getInstance().openSession();
		Serializable id = HibernateUtil.getInstance().getId(clonePojo, clonePojo.getClass());
		assertNotNull(id);
		
		Object loadedPojo = HibernateUtil.getInstance().load(id, clonePojo.getClass());
		HibernateUtil.getInstance().closeCurrentSession();
		
	//	Post test verification
	//
		assertEquals(loadedPojo, hibernatePojo);
	}
}
