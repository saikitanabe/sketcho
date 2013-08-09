/**
 * 
 */
package net.sf.hibernate4gwt.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import junit.framework.TestCase;
import net.sf.hibernate4gwt.core.beanlib.mapper.ProxyClassMapper;
import net.sf.hibernate4gwt.core.store.stateful.InMemoryPojoStore;
import net.sf.hibernate4gwt.rebind.Gwt14ProxyGenerator;
import net.sf.hibernate4gwt.rebind.ProxyManager;
import net.sf.hibernate4gwt.rebind.xml.AdditionalCode;
import net.sf.hibernate4gwt.rebind.xml.AdditionalCodeReader;
import net.sf.hibernate4gwt.testApplication.domain.IMessage;
import net.sf.hibernate4gwt.testApplication.domain.IUser;
import net.sf.hibernate4gwt.testApplication.server.ApplicationContext;
import net.sf.hibernate4gwt.testApplication.server.ApplicationContext.Configuration;
import net.sf.hibernate4gwt.testApplication.server.dao.IMessageDAO;
import net.sf.hibernate4gwt.testApplication.server.dao.IUserDAO;
import net.sf.hibernate4gwt.testApplication.server.service.IMessageService;
import net.sf.hibernate4gwt.testApplication.server.service.IStartupService;

import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.hibernate.collection.AbstractPersistentCollection;

/**
 * Test case clone and merge server operations
 * @author bruno.marchesson
 *
 */
public class CloneTest extends TestCase
{	
	//----
	// Attributes
	//----
	/**
	 * Hibernate lazy manager
	 */
	protected HibernateBeanManager lazyManager;
	
	/**
	 * Clone user class
	 */
	protected Class cloneUserClass = null;
	
	/**
	 * Domain message class
	 */
	protected Class cloneMessageClass = null;
	
	/**
	 * Domain user class
	 */
	protected Class domainUserClass = null;
	
	/**
	 * Domain message class
	 */
	protected Class domainMessageClass = null;
	
	
	//-------------------------------------------------------------------------
	//
	// Test initialisation
	//
	//-------------------------------------------------------------------------
	/**
	 * Test init
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		
	//	Init db if needed
	//
		IStartupService startupService = (IStartupService) ApplicationContext.getInstance().getBean("startupService");
		if (startupService.isInitialized() == false)
		{
			startupService.initialize();
		}
			
	//	Get LazyManager
	//
		lazyManager = (HibernateBeanManager) ApplicationContext.getInstance().getBean("hibernateBeanManager");
		
		if (ApplicationContext.getInstance().getConfiguration() == Configuration.stateful)
		{
		//	Change pojo store
		//
			lazyManager.setPojoStore(new InMemoryPojoStore());
			
			domainMessageClass = net.sf.hibernate4gwt.testApplication.domain.stateful.Message.class;
			domainUserClass = net.sf.hibernate4gwt.testApplication.domain.stateful.User.class;
			cloneMessageClass = domainMessageClass;
			cloneUserClass = domainUserClass;
		}
		else if (ApplicationContext.getInstance().getConfiguration() == Configuration.java5)
		{
		//	Define clone and domain classes
		//
			domainMessageClass = net.sf.hibernate4gwt.testApplication.server.domain.Message.class;
			domainUserClass = net.sf.hibernate4gwt.testApplication.server.domain.User.class;
			cloneMessageClass = net.sf.hibernate4gwt.testApplication.domain.dto.MessageDTO.class;
			cloneUserClass = net.sf.hibernate4gwt.testApplication.domain.dto.UserDTO.class;
			
		}
		else if (ApplicationContext.getInstance().getConfiguration() == Configuration.proxy)
		{
			lazyManager.setClassMapper(new ProxyClassMapper());

		//	Read additional code
		//
			AdditionalCode additionalCode = AdditionalCodeReader.readFromFile(Gwt14ProxyGenerator.ADDITIONAL_CODE);
			
		//	Create proxy classes
		//
			domainMessageClass = net.sf.hibernate4gwt.testApplication.domain.proxy.Message.class;
			domainUserClass = net.sf.hibernate4gwt.testApplication.domain.proxy.User.class;
			cloneMessageClass = ProxyManager.getInstance().generateProxyClass(domainMessageClass, additionalCode);
			cloneUserClass = ProxyManager.getInstance().generateProxyClass(domainUserClass, additionalCode);
		}
		else
		{
		//	Stateless mode
		//
			domainMessageClass = net.sf.hibernate4gwt.testApplication.domain.stateless.Message.class;
			domainUserClass = net.sf.hibernate4gwt.testApplication.domain.stateless.User.class;
			cloneMessageClass = domainMessageClass;
			cloneUserClass = domainUserClass;
		}
	}
	
	//--------------------------------------------------------------------------
	//
	// Test methods
	//
	//---------------------------------------------------------------------------
	/**
	 * Test clone of a loaded user and associated messages
	 */
	public void testCloneAndMergeUserAndMessages()
	{
	//	Get UserDAO
	//
		IUserDAO userDAO = (IUserDAO) ApplicationContext.getInstance().getBean("userDAO");
		assertNotNull(userDAO);
		
	//	Load user
	//
		IUser user = userDAO.searchUserAndMessagesByLogin("junit");
		assertNotNull(user);
		assertNotNull(user.getMessageList());
		assertFalse(user.getMessageList().isEmpty());
		
	//	Clone user
	//
		IUser cloneUser = (IUser) lazyManager.clone(user);
		
	//	Test cloned user
	//
		assertNotNull(cloneUser);
		assertEquals(cloneUserClass, cloneUser.getClass());
		
		// Test cloned message classes
		assertNotNull(cloneUser.getMessageList());
		assertEquals(cloneUser.getMessageList().size(), user.getMessageList().size());

		for (Object message : cloneUser.getMessageList())
		{
			assertEquals(cloneMessageClass, message.getClass());
		}
		
	//	Merge user
	//
		IUser mergeUser = (IUser) lazyManager.merge(cloneUser);
		
	//	Test merged user
	//
		assertNotNull(mergeUser);
		assertEquals(domainUserClass, mergeUser.getClass());
		
		// Test merged messages classes
		assertNotNull(mergeUser.getMessageList());
		assertEquals(mergeUser.getMessageList().size(), user.getMessageList().size());

		for (Object message : mergeUser.getMessageList())
		{
			assertEquals(domainMessageClass, message.getClass());
		}
	}
	
	/**
	 * Test clone of a loaded (proxy) user
	 */
	public void testCloneAndMergeLoadedProxy()
	{
	//	Get UserDAO
	//
		SessionFactory sessionFactory = (SessionFactory) ApplicationContext.getInstance().getBean("sessionFactory");
		assertNotNull(sessionFactory);
		
	//	Load user
	//
		IUser user = (IUser) sessionFactory.openSession().load(domainUserClass, 1);
		user.getFirstName();
		assertNotNull(user);
		
	//	Clone user
	//
		IUser cloneUser = (IUser) lazyManager.clone(user);
		
	//	Test cloned user
	//
		assertNotNull(cloneUser);
		assertEquals(cloneUserClass, cloneUser.getClass());
				
	//	Merge user
	//
		IUser mergeUser = (IUser) lazyManager.merge(cloneUser);
		
	//	Test merged user
	//
		assertNotNull(mergeUser);
		assertEquals(domainUserClass, mergeUser.getClass());		
	}
	
	/**
	 * Test clone of a proxy
	 */
	public void testCloneProxy()
	{
	//	Get UserDAO
	//
		SessionFactory sessionFactory = (SessionFactory) ApplicationContext.getInstance().getBean("sessionFactory");
		assertNotNull(sessionFactory);
		
	//	Load user
	//
		IUser user = (IUser) sessionFactory.openSession().load(domainUserClass, 1);
		assertNotNull(user);
		
	//	Clone user
	//
		IUser cloneUser = (IUser) lazyManager.clone(user);
		
	//	Test cloned user
	//
		assertNull(cloneUser);
	}
	
	/**
	 * Test clone and merge of a loaded message and associated user
	 */
	public void testCloneAndMergeMessageAndUser()
	{
	//	Get UserDAO
	//
		IMessageDAO messageDAO = (IMessageDAO) ApplicationContext.getInstance().getBean("messageDAO");
		assertNotNull(messageDAO);
		
	//	Load message and user
	//
		IMessage message = messageDAO.loadDetailedMessage(1);
		assertNotNull(message);
		assertNotNull(message.getAuthor());
		assertFalse(Hibernate.isInitialized(message.getAuthor().getMessageList()));
		
	//	Clone message
	//
		IMessage cloneMessage = (IMessage) lazyManager.clone(message);
		
	//	Test cloned message
	//
		assertNotNull(cloneMessage);
		assertEquals(cloneMessageClass, cloneMessage.getClass());
		
		// Test cloned user class
		assertNotNull(cloneMessage.getAuthor());
		assertNull(cloneMessage.getAuthor().getMessageList());
				
	//	Merge message
	//
		IMessage mergeMessage = (IMessage) lazyManager.merge(cloneMessage);
		
	//	Test merged message
	//
		assertNotNull(mergeMessage);
		assertEquals(domainMessageClass, mergeMessage.getClass());
		
		// Test cloned user class
		assertNotNull(mergeMessage.getAuthor());
		assertNotNull(mergeMessage.getAuthor().getMessageList());
		assertFalse(Hibernate.isInitialized(mergeMessage.getAuthor().getMessageList()));
	}
	
	/**
	 * Test clone and merge of a partially loaded message
	 */
	public void testCloneAndMergePartiallyLoadedMessage()
	{
	//	Get UserDAO
	//
		IMessageDAO messageDAO = (IMessageDAO) ApplicationContext.getInstance().getBean("messageDAO");
		assertNotNull(messageDAO);
		
	//	Load message and user
	//
		IMessage message = messageDAO.loadLastMessage();
		assertNotNull(message);
		assertFalse(Hibernate.isInitialized(message.getAuthor()));
		assertFalse(Hibernate.isInitialized(message.getKeywords()));
		
	//	Clone message
	//
		IMessage cloneMessage = (IMessage) lazyManager.clone(message);
		
	//	Test cloned message
	//
		assertNotNull(cloneMessage);
		assertEquals(cloneMessageClass, cloneMessage.getClass());
		
		// Test cloned user class
		assertNull(cloneMessage.getAuthor());
		assertNull(cloneMessage.getKeywords());
				
	//	Merge message
	//
		IMessage mergeMessage = (IMessage) lazyManager.merge(cloneMessage);
		
	//	Test merged message
	//
		assertNotNull(mergeMessage);
		assertEquals(domainMessageClass, mergeMessage.getClass());
		
		// Test cloned user class
		assertNotNull(mergeMessage.getAuthor());
		assertFalse(Hibernate.isInitialized(mergeMessage.getAuthor()));
		assertFalse(Hibernate.isInitialized(mergeMessage.getKeywords()));
		
	//	Compute keywords (reattachement checking)
	//
		IMessageService messageService = (IMessageService) ApplicationContext.getInstance().getBean(IMessageService.NAME);
		messageService.saveMessage(mergeMessage);
	}
	
	/**
	 * Test clone and merge of a complete message (with associated map)
	 */
	public void testCloneAndMergeDetailedMessage()
	{
	//	Get UserDAO
	//
		IMessageDAO messageDAO = (IMessageDAO) ApplicationContext.getInstance().getBean("messageDAO");
		assertNotNull(messageDAO);
		
	//	Load message and user
	//
		IMessage message = messageDAO.loadDetailedMessage(1);
		assertNotNull(message);
		assertTrue(Hibernate.isInitialized(message.getAuthor()));
		assertTrue(Hibernate.isInitialized(message.getKeywords()));
		
	//	Clone message
	//
		IMessage cloneMessage = (IMessage) lazyManager.clone(message);
		
	//	Test cloned message
	//
		assertNotNull(cloneMessage);
		assertEquals(cloneMessageClass, cloneMessage.getClass());
		
		// Test cloned associations
		assertNotNull(cloneMessage.getAuthor());
		assertNotNull(cloneMessage.getKeywords());
		assertEquals(message.getKeywords().size(), cloneMessage.getKeywords().size());
				
	//	Merge message
	//
		IMessage mergeMessage = (IMessage) lazyManager.merge(cloneMessage);
		
	//	Test merged message
	//
		assertNotNull(mergeMessage);
		assertEquals(domainMessageClass, mergeMessage.getClass());
		
		// Test merged associations
		assertNotNull(mergeMessage.getAuthor());
		assertTrue(Hibernate.isInitialized(mergeMessage.getAuthor()));
		
		assertNotNull(mergeMessage.getKeywords());
		assertTrue(Hibernate.isInitialized(mergeMessage.getKeywords()));
		assertEquals(message.getKeywords().size(), mergeMessage.getKeywords().size());
		
	//	Test save merged message
	//
		messageDAO.saveMessage(mergeMessage);
	}
	
	/**
	 * Test clone of a loaded user list
	 */
	public void testCloneUserList()
	{
	//	Get UserDAO
	//
		IUserDAO userDAO = (IUserDAO) ApplicationContext.getInstance().getBean("userDAO");
		assertNotNull(userDAO);
		
	//	Load user
	//
		List<IUser> userList = userDAO.loadAll();
		assertNotNull(userList);
		assertFalse(userList.isEmpty());
		
	//	Clone user
	//
		List cloneUserList = (List) lazyManager.clone(userList);
		
	//	Test cloned user
	//
		assertNotNull(cloneUserList);
		assertEquals(cloneUserList.size(), userList.size());
		
		for (Object user : cloneUserList)
		{
			assertEquals(cloneUserClass, user.getClass());
		}
	}
	
	/**
	 * Test modification of the last posted message
	 */
	public void testModifyLastMessage()
	{
	//	Get MessageDAO
	//
		IMessageDAO messageDAO = (IMessageDAO) ApplicationContext.getInstance().getBean("messageDAO");
		assertNotNull(messageDAO);
		
	//	Load last message
	//
		IMessage message = messageDAO.loadDetailedMessage(1);
		assertNotNull(message);
		assertEquals(domainMessageClass, message.getClass());
		
		// author verification
		assertNotNull(message.getAuthor());
		assertTrue(domainUserClass.isAssignableFrom(message.getAuthor().getClass()));
		
	//	Clone message
	//
		IMessage cloneMessage = (IMessage) lazyManager.clone(message);
		
	//	Test cloned message
	//
		assertNotNull(cloneMessage);
		assertEquals(cloneMessageClass, cloneMessage.getClass());
		assertEquals(message.getMessage(), cloneMessage.getMessage());
		
		assertNotNull(cloneMessage.getAuthor());
		assertEquals(cloneUserClass, cloneMessage.getAuthor().getClass());
		
	//	Modify clone message
	//
		cloneMessage.setMessage("Modified on " + new Date().toString());
		
	//	Merge message
	//
		IMessage mergeMessage = (IMessage) lazyManager.merge(cloneMessage);
		
	//	Clone verification
	//
		assertNotNull(mergeMessage);
		assertEquals(domainMessageClass, mergeMessage.getClass());
		
		assertNotNull(mergeMessage.getAuthor());
		assertTrue(domainUserClass.isAssignableFrom(message.getAuthor().getClass()));
		
	//	Save message
	//
		messageDAO.saveMessage(mergeMessage);
	}
	
	/**
	 * Test modification of the last posted message
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public void testCreateMessage() throws InstantiationException, IllegalAccessException
	{
	//	Get User and Message DAO
	//
		IUserDAO userDAO = (IUserDAO) ApplicationContext.getInstance().getBean("userDAO");
		assertNotNull(userDAO);
		
		IMessageDAO messageDAO = (IMessageDAO) ApplicationContext.getInstance().getBean("messageDAO");
		assertNotNull(messageDAO);
		
	//	Load user
	//
		IUser user = userDAO.searchUserAndMessagesByLogin("junit");
		assertNotNull(user);
		assertEquals(domainUserClass, user.getClass());
		
		assertNotNull(user.getMessageList());
		assertFalse(user.getMessageList().isEmpty());
		
	//	Clone user
	//
		IUser cloneUser = (IUser) lazyManager.clone(user);
		
	//	Clone verification
	//
		assertNotNull(cloneUser);
		assertEquals(cloneUserClass, cloneUser.getClass());

		assertNotNull(cloneUser.getMessageList());
		assertFalse(cloneUser.getMessageList().isEmpty());
		for (Object subMessage : cloneUser.getMessageList())
		{
			assertEquals(cloneMessageClass, subMessage.getClass());
		}
		
	//	Create new message
	//
		IMessage message = createNewCloneMessage(cloneUser);
		message.setDate(new Date());
		message.setMessage("test Create message");
		
	//	Merge message
	//
		IMessage mergeMessage = (IMessage) lazyManager.merge(message);
		
	//	Clone verification
	//
		assertNotNull(mergeMessage);
		assertEquals(domainMessageClass, mergeMessage.getClass());
		assertEquals(message.getMessage(), mergeMessage.getMessage());
		
		assertNotNull(mergeMessage.getAuthor());
		assertEquals(domainUserClass, mergeMessage.getAuthor().getClass());
		
		// message verification
		for (Object subMessage : mergeMessage.getAuthor().getMessageList())
		{
			assertEquals(domainMessageClass, subMessage.getClass());
		}
		
	//	Save message
	//
		messageDAO.saveMessage(mergeMessage);
	}
	
	
	/**
	 * Test map cloning
	 */
	public void testCloneAndMergeMap()
	{
	//	Get User DAO
	//
		IUserDAO userDAO = (IUserDAO) ApplicationContext.getInstance().getBean("userDAO");
		assertNotNull(userDAO);
		
	//	Create a map with all users and message
	//
		Map<IUser, Set<IMessage>> userMap = new HashMap<IUser, Set<IMessage>>();
		List<IUser> userList = userDAO.loadAll();
		for (IUser user : userList)
		{
			IUser completeUser = userDAO.searchUserAndMessagesByLogin(user.getLogin());
			userMap.put(user, completeUser.getMessageList());
		}
		
	//	Clone map
	//
		Map<IUser, Set<IMessage>> cloneMap = (Map<IUser, Set<IMessage>>) lazyManager.clone(userMap);
		assertNotNull(cloneMap);
		assertEquals(cloneMap.size(), userMap.size());
		
	//	Clone verification
	//
		for (Entry<IUser, Set<IMessage>> entry : cloneMap.entrySet())
		{
		//	User checking
		//
			IUser cloneUser = entry.getKey();
			assertNotNull(cloneUser);
			assertEquals(cloneUserClass, cloneUser.getClass());
			
		//	Message checking
		//
			for (IMessage message : entry.getValue())
			{
				assertNotNull(message);
				assertEquals(cloneMessageClass, message.getClass());
			}
		}
		
	//	Merge map
	//
		Map<IUser, Set<IMessage>> mergeMap = (Map<IUser, Set<IMessage>>) lazyManager.merge(cloneMap);
		assertNotNull(mergeMap);
		assertEquals(mergeMap.size(), cloneMap.size());
		
	//	Merge verification
	//
		for (Entry<IUser, Set<IMessage>> entry : mergeMap.entrySet())
		{
		//	User checking
		//
			IUser mergeUser = entry.getKey();
			assertNotNull(mergeUser);
			assertEquals(domainUserClass, mergeUser.getClass());
			
		//	Message checking
		//
			for (IMessage message : entry.getValue())
			{
				assertNotNull(message);
				assertEquals(domainMessageClass, message.getClass());
			}
		}
	}
	
	/**
	 * Test clone and merge operations on wrapper object
	 * (ie a non persistent class containing persistent classes)
	 */
	public void testCloneAndMergeWrapperObject()
	{
	//	Create wrapping object
	//
		WrappingClass wrapper = new WrappingClass();
		
	//	Get Message and User DAO
	//
		IMessageDAO messageDAO = (IMessageDAO) ApplicationContext.getInstance().getBean("messageDAO");
		assertNotNull(messageDAO);
		
		IUserDAO userDAO = (IUserDAO) ApplicationContext.getInstance().getBean("userDAO");
		assertNotNull(userDAO);
		
	//	Fill wrapper
	//
		wrapper.setUser(userDAO.loadUser(1));
		wrapper.setMessageList(messageDAO.loadAllMessage(0, 10));
		wrapper.setUserList(userDAO.loadAll());
		
		List<IUser> userList = new ArrayList<IUser>();
		userList.add((userDAO.searchUserAndMessagesByLogin("junit")));
		wrapper.setCompleteUserList(userList);
		
	//	Loading verification
	//
		assertNotNull(wrapper.getUser().getMessageList());
		assertFalse(Hibernate.isInitialized(wrapper.getUser().getMessageList()));
		assertNotNull(wrapper.getMessageList());
		for (IMessage message : wrapper.getMessageList())
		{
			assertNotNull(message);
			assertFalse(Hibernate.isInitialized(message.getAuthor()));
		}
		
		for (IUser user : wrapper.getCompleteUserList())
		{
			assertNotNull(user.getMessageList());
			assertTrue(user.getMessageList() instanceof AbstractPersistentCollection);
		}
		
		for (IUser user : wrapper.getUserList())
		{
			assertNotNull(user.getMessageList());
			assertTrue(user.getMessageList() instanceof AbstractPersistentCollection);
		}
		
	//	Clone wrapper
	//
		WrappingClass cloneWrapper = (WrappingClass) lazyManager.clone(wrapper);
		
	//	Clone verification
	//
		assertNotSame(cloneWrapper, wrapper);
		assertNotNull(cloneWrapper.getUser());
		assertEquals(wrapper.getUser().getId(), cloneWrapper.getUser().getId());
		assertNull(cloneWrapper.getUser().getMessageList());
		assertNotNull(cloneWrapper.getMessageList());
		for (IMessage message : cloneWrapper.getMessageList())
		{
			assertNotNull(message);
			assertNull(message.getAuthor());
		}
		
		for (IUser user : cloneWrapper.getCompleteUserList())
		{
			assertNotNull(user.getMessageList());
			assertFalse(user.getMessageList() instanceof AbstractPersistentCollection);
		}
		for (IUser user : cloneWrapper.getUserList())
		{
			assertNull(user.getMessageList());
		}
		
	//	Merge wrapper
	//
		WrappingClass mergedWrapper = (WrappingClass) lazyManager.merge(cloneWrapper);
		
	//	Merge verification
	//
		assertEquals(wrapper.getUser().getId(), mergedWrapper.getUser().getId());
		assertNotNull(mergedWrapper.getUser().getMessageList());
		assertFalse(Hibernate.isInitialized(mergedWrapper.getUser().getMessageList()));
		for (IMessage message : mergedWrapper.getMessageList())
		{
			assertNotNull(message);
//			assertFalse(Hibernate.isInitialized(message.getAuthor()));
		}
		for (IUser user : mergedWrapper.getCompleteUserList())
		{
			assertNotNull(user.getMessageList());
			assertTrue(user.getMessageList() instanceof AbstractPersistentCollection);
		}
		for (IUser user : mergedWrapper.getUserList())
		{
			assertNotNull(user.getMessageList());
			assertTrue(user.getMessageList() instanceof AbstractPersistentCollection);
		}
	}
	
	/**
	 * Test clone and merge operations on wrapper transient object
	 * (ie a non persistent class containing transient instance)
	 */
	public void testCloneAndMergeTransientWrapperObject() throws Exception
	{
	//	Create wrapping object
	//
		WrappingClass wrapper = new WrappingClass();
		
	//	Fill wrapper
	//
		IUser user = (IUser) domainUserClass.newInstance();
		wrapper.setUser(user);
		List<IMessage> messageList = new ArrayList<IMessage>();
		IMessage message = (IMessage) domainMessageClass.newInstance();
		messageList.add(message);
		wrapper.setMessageList(messageList);
		
	//	Loading verification
	//
		assertNull(wrapper.getUser().getMessageList());
		assertEquals(wrapper.getMessageList().size(), 1);
		
	//	Clone wrapper
	//
		WrappingClass cloneWrapper = (WrappingClass) lazyManager.clone(wrapper);
		
	//	Clone verification
	//
		assertNotNull(cloneWrapper.getUser());
		assertEquals(wrapper.getUser().getId(), cloneWrapper.getUser().getId());
		assertNull(cloneWrapper.getUser().getMessageList());
		assertEquals(cloneWrapper.getMessageList().size(), 1);
		
	//	Merge wrapper
	//
		WrappingClass mergedWrapper = (WrappingClass) lazyManager.merge(cloneWrapper);
		
	//	Merge verification
	//
		assertEquals(wrapper.getUser().getId(), mergedWrapper.getUser().getId());
		assertNull(mergedWrapper.getUser().getMessageList());
		assertEquals(mergedWrapper.getMessageList().size(), 1);
	}
	
	/**
	 * Test clone and merge operations on transient object
	 */
	public void testCloneAndMergeTransientObject() throws Exception
	{
	//	Clone and merge integer
	//
		Integer clone = (Integer) lazyManager.clone(new Integer(2));
		assertEquals(2, clone.intValue());
		
		Integer merge = (Integer) lazyManager.merge(clone);
		assertEquals(2, merge.intValue());
	}
	
	/**
	 * Test changing association between clone and merge
	 */
	public void testChangeAssociationAfterClone()
	{
	//	Get Message and User DAO
	//
		IMessageDAO messageDAO = (IMessageDAO) ApplicationContext.getInstance().getBean("messageDAO");
		assertNotNull(messageDAO);
		
		IUserDAO userDAO = (IUserDAO) ApplicationContext.getInstance().getBean("userDAO");
		assertNotNull(userDAO);
		
	//	Load last message
	//
		IMessage message = messageDAO.loadDetailedMessage(1);
		assertNotNull(message);
		assertEquals(domainMessageClass, message.getClass());
		
		assertNotNull(message.getAuthor());
		assertEquals(domainUserClass, message.getAuthor().getClass());
		
	//	Load user
	//
		IUser user = null;
		if (message.getAuthor().getLogin().equals("junit"))
		{
			user = userDAO.loadUser(1);
		}
		else
		{
			user = userDAO.loadUser(2);
		}
		assertNotNull(user);
		assertEquals(domainUserClass, user.getClass());
		assertFalse(user.equals(message.getAuthor()));
		
	//	Clone message
	//
		IMessage cloneMessage = (IMessage) lazyManager.clone(message);
		
	//	Test cloned message
	//
		assertNotNull(cloneMessage);
		assertEquals(cloneMessageClass, cloneMessage.getClass());
		assertEquals(message.getMessage(), cloneMessage.getMessage());
		
		assertNotNull(cloneMessage.getAuthor());
	
	//	Change clone user
	//
		IUser cloneUser = (IUser) lazyManager.clone(user);
		assertNotNull(cloneUser);
		assertEquals(cloneUserClass, cloneUser.getClass());
		
		changeAuthorForClone(cloneMessage, cloneUser);
		
	//	Merge message
	//
		IMessage mergeMessage = (IMessage) lazyManager.merge(cloneMessage);
		
	//	Clone verification
	//
		assertNotNull(mergeMessage);
		assertEquals(domainMessageClass, mergeMessage.getClass());
		assertEquals(message.getMessage(), mergeMessage.getMessage());
		
		assertNotNull(mergeMessage.getAuthor());
		assertEquals(domainUserClass, mergeMessage.getAuthor().getClass());
		assertEquals(user.getId(), mergeMessage.getAuthor().getId());
		
	//	Save message
	//
		messageDAO.saveMessage(mergeMessage);
	}
	
	/**
	 * Test setting association between clone and merge
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public void testSetAssociationAfterClone() throws InstantiationException, IllegalAccessException
	{
	//	Get Message and User DAO
	//
		IMessageDAO messageDAO = (IMessageDAO) ApplicationContext.getInstance().getBean("messageDAO");
		assertNotNull(messageDAO);
		
		IUserDAO userDAO = (IUserDAO) ApplicationContext.getInstance().getBean("userDAO");
		assertNotNull(userDAO);
		
	//	Load last message
	//
		IMessage message = messageDAO.loadDetailedMessage(1);
		assertNotNull(message);
		assertEquals(domainMessageClass, message.getClass());
		
		// set null author
		changeAuthorForDomain(message, null);
		assertNull(message.getAuthor());
		
	//	Clone message
	//
		IMessage cloneMessage = (IMessage) lazyManager.clone(message);
		
	//	Test cloned message
	//
		assertNotNull(cloneMessage);
		assertEquals(cloneMessageClass, cloneMessage.getClass());
		assertEquals(message.getMessage(), cloneMessage.getMessage());
		
		assertNull(cloneMessage.getAuthor());
	
	//	Create new user
	//
		IUser user = createNewCloneUser();
		user.setLogin("test");
		assertNotNull(user);
		
		changeAuthorForClone(cloneMessage, user);
		
	//	Merge message
	//
		IMessage mergeMessage = (IMessage) lazyManager.merge(cloneMessage);
		
	//	Merge verification
	//
		assertNotNull(mergeMessage);
		assertEquals(domainMessageClass, mergeMessage.getClass());
		assertEquals(message.getMessage(), mergeMessage.getMessage());
		
		assertNotNull(mergeMessage.getAuthor());
		assertEquals(domainUserClass, mergeMessage.getAuthor().getClass());
	}
	
	/**
	 * Test change property on client side
	 */
	public void testChangePropertyAfterClone()
	{  
	//	Get UserDAO
	//
		IUserDAO userDAO = (IUserDAO) ApplicationContext.getInstance().getBean("userDAO");
		assertNotNull(userDAO);
		
	//	Load user
	//
		IUser user = userDAO.searchUserAndMessagesByLogin("junit");
		assertNotNull(user);
		assertNotNull(user.getMessageList());
		assertFalse(user.getMessageList().isEmpty());
		
	//	Clone user
	//
		IUser cloneUser = (IUser) lazyManager.clone(user); 
		 
		//Change the clone name 
		String newName = "new name"; 
		cloneUser.setLogin(newName); 
		 
	//	Merge user
	//
		IUser mergeUser = (IUser) lazyManager.merge(cloneUser); 
		 
		assertEquals("New name after merge", newName, mergeUser.getLogin()); 
	}
	
	/**
	 * Test delete property on client side
	 */
	public void testDeletePropertyAfterClone() throws Exception
	{  
	//	Get UserDAO
	//
		IUserDAO userDAO = (IUserDAO) ApplicationContext.getInstance().getBean("userDAO");
		assertNotNull(userDAO);
		
	//	Load user
	//
		IUser user = userDAO.searchUserAndMessagesByLogin("junit");
		assertNotNull(user);
		assertNotNull(user.getMessageList());
		int messageCount = user.getMessageList().size();
		
	//	Create and save a new message
	//
		IMessage message = createNewMessage(user);
		message.setDate(new Date());
		message.setMessage("message to delete");
		user.addMessage(message);
		userDAO.saveUser(user);
		
	//	Reload user
	//
		user = userDAO.searchUserAndMessagesByLogin("junit");
		assertNotNull(user);
		assertNotNull(user.getMessageList());
		assertFalse(user.getMessageList().isEmpty());
		assertEquals(messageCount + 1, user.getMessageList().size());
		
	//	Clone user
	//
		IUser cloneUser = (IUser) lazyManager.clone(user); 
		 
		// delete the message
		for (Object item : cloneUser.getMessageList())
		{
			IMessage cloneMessage = (IMessage) item;
			if (message.getMessage().equals(cloneMessage.getMessage()))
			{
				cloneUser.removeMessage(cloneMessage);
				break;
			}
		} 
		assertEquals(messageCount, cloneUser.getMessageList().size());
		 
	//	Merge user
	//
		IUser mergeUser = (IUser) lazyManager.merge(cloneUser); 
		assertNotNull(mergeUser);
		assertNotNull(mergeUser.getMessageList());
		 
		assertEquals(messageCount, mergeUser.getMessageList().size());
		
	//	Save merged user
	//
		userDAO.saveUser(mergeUser);
		
	//	Reload user to count messages
	//
		user = userDAO.searchUserAndMessagesByLogin("junit");
		assertNotNull(user);
		assertNotNull(user.getMessageList());
		assertEquals(user.getMessageList().size(), messageCount);
	}
	
	//-------------------------------------------------------------------------
	//
	// Internal methods
	//
	//-------------------------------------------------------------------------
	/**
	 * Create a new message
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	protected IMessage createNewCloneMessage(IUser user) throws InstantiationException, IllegalAccessException
	{
	//	Create message
	//
		IMessage result = (IMessage) cloneMessageClass.newInstance();
		
	//	Change author
	//
		changeAuthorForClone(result, user);
			
		return result;
	}
	
	/**
	 * Create a new message
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	protected IMessage createNewMessage(IUser user) throws InstantiationException, IllegalAccessException
	{
	//	Create message
	//
		IMessage result = (IMessage) domainMessageClass.newInstance();
		
	//	Change author
	//
		changeAuthorForDomain(result, user);
			
		return result;
	}
	
	/**
	 * Create a new user
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	protected IUser createNewCloneUser() throws InstantiationException, IllegalAccessException
	{
	//	Create user
	//
		IUser result = (IUser) cloneUserClass.newInstance();
			
		return result;
	}
	
	/**
	 * Change the author of the message
	 * @param message
	 * @param user
	 */
	protected void changeAuthorForClone(IMessage message, IUser user)
	{
		if (ApplicationContext.getInstance().getConfiguration() == Configuration.stateful)
		{
			((net.sf.hibernate4gwt.testApplication.domain.stateful.Message)message).
				setAuthor((net.sf.hibernate4gwt.testApplication.domain.stateful.User)user);
		}
		else if (ApplicationContext.getInstance().getConfiguration() == Configuration.proxy)
		{
			((net.sf.hibernate4gwt.testApplication.domain.proxy.Message)message).
				setAuthor((net.sf.hibernate4gwt.testApplication.domain.proxy.User)user);
		}
		else if (ApplicationContext.getInstance().getConfiguration() == Configuration.stateless)
		{
			((net.sf.hibernate4gwt.testApplication.domain.stateless.Message)message).
				setAuthor((net.sf.hibernate4gwt.testApplication.domain.stateless.User)user);
		}
		else // Java5
		{
			((net.sf.hibernate4gwt.testApplication.domain.dto.MessageDTO)message).
				setAuthor((net.sf.hibernate4gwt.testApplication.domain.dto.UserDTO)user);
		}
	}
	
	/**
	 * Change the author of the message
	 * @param message
	 * @param user
	 */
	protected void changeAuthorForDomain(IMessage message, IUser user)
	{
		if (ApplicationContext.getInstance().getConfiguration() == Configuration.stateful)
		{
			((net.sf.hibernate4gwt.testApplication.domain.stateful.Message)message).
				setAuthor((net.sf.hibernate4gwt.testApplication.domain.stateful.User)user);
		}
		else if (ApplicationContext.getInstance().getConfiguration() == Configuration.proxy)
		{
			((net.sf.hibernate4gwt.testApplication.domain.proxy.Message)message).
				setAuthor((net.sf.hibernate4gwt.testApplication.domain.proxy.User)user);
		}
		else if (ApplicationContext.getInstance().getConfiguration() == Configuration.stateless)
		{
			((net.sf.hibernate4gwt.testApplication.domain.stateless.Message)message).
				setAuthor((net.sf.hibernate4gwt.testApplication.domain.stateless.User)user);
		}
		else // Java5
		{
			((net.sf.hibernate4gwt.testApplication.server.domain.Message)message).
				setAuthor((net.sf.hibernate4gwt.testApplication.server.domain.User)user);
		}
	}
}