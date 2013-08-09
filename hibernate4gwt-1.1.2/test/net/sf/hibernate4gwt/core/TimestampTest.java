package net.sf.hibernate4gwt.core;

import java.sql.Timestamp;
import java.util.Date;

import net.sf.hibernate4gwt.testApplication.domain.IMessage;
import net.sf.hibernate4gwt.testApplication.server.ApplicationContext;
import net.sf.hibernate4gwt.testApplication.server.ApplicationContext.Configuration;
import net.sf.hibernate4gwt.testApplication.server.dao.IMessageDAO;

import junit.framework.TestCase;

public class TimestampTest extends TestCase
{
	//-------------------------------------------------------------------------
	//
	// Test methods
	//
	//-------------------------------------------------------------------------
	/**
	 * Test timestamp conversion
	 */
	public void testTimestampConversion()
	{
	//	Hibernate lazy manager
	//
		ApplicationContext.getInstance().setConfiguration(Configuration.stateless);
		HibernateBeanManager lazyManager = (HibernateBeanManager)ApplicationContext.getInstance().getBean("hibernateBeanManager");
		
	//	Test data
	//
		IMessageDAO messageDAO = (IMessageDAO)ApplicationContext.getInstance().getBean("messageDAO");
		IMessage message = messageDAO.loadLastMessage();
		assertNotNull(message);
		message = messageDAO.loadDetailedMessage(message.getId());
		assertNotNull(message);
		
		Timestamp date = new Timestamp(new Date().getTime());
		date.setNanos(200);
		message.setDate(date); 
		
	//	Clone
	//
		IMessage cloneMessage = (IMessage) lazyManager.clone(message);
		
	//	Clone verification
	//
		assertNotNull(cloneMessage);
		assertNotNull(cloneMessage.getDate());
		assertTrue(cloneMessage.getDate() instanceof Date);
		assertTrue(cloneMessage.getDate() instanceof java.sql.Timestamp);
		assertEquals(message.getDate(), cloneMessage.getDate());
		
		assertEquals(message.getMessage(), cloneMessage.getMessage());
		assertEquals(message.getId(), cloneMessage.getId());
		
		// Author must have been cloned, not copied
		assertNotNull(cloneMessage.getAuthor());
		assertEquals(message.getAuthor().getId(), cloneMessage.getAuthor().getId());
		assertFalse(message.getAuthor() == cloneMessage.getAuthor());
		
	//	Merge
	//
		IMessage mergedMessage = (IMessage)lazyManager.merge(cloneMessage);
		
	//	Merge verification
	//
		assertNotNull(mergedMessage);
		assertNotNull(mergedMessage.getDate());
		assertTrue(mergedMessage.getDate() instanceof Date);
		assertTrue(mergedMessage.getDate() instanceof java.sql.Timestamp);
		assertEquals(message.getDate(), mergedMessage.getDate());
		
		assertEquals(message.getMessage(), mergedMessage.getMessage());
		assertEquals(message.getId(), mergedMessage.getId());
		
		// Author must have been merged, not copied
		assertNotNull(mergedMessage.getAuthor());
		assertEquals(message.getAuthor().getId(), mergedMessage.getAuthor().getId());
		assertFalse(message.getAuthor() == mergedMessage.getAuthor());
	}
}