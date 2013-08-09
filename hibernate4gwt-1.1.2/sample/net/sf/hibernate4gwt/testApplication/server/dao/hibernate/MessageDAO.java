/**
 * 
 */
package net.sf.hibernate4gwt.testApplication.server.dao.hibernate;

import java.util.List;

import net.sf.hibernate4gwt.testApplication.domain.IMessage;
import net.sf.hibernate4gwt.testApplication.server.dao.IMessageDAO;

import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * DAO for message beans.
 * This implementation use HQL to work seamlessly with all implementation of the Message domain class
 * (Java 1.4 _ stateful or stateless _ and Java5)
 * @author bruno.marchesson
 *
 */
public class MessageDAO implements IMessageDAO
{
//	----
	// Attributes
	//----
    /**
     * The Hibernate session factory
     */
    private SessionFactory _sessionFactory;
    
    //----
    // Properties
    //----
    /**
     * @return the Hibernate session factory
     */
    public SessionFactory getSessionFactory()
    {
        return _sessionFactory;
    }

    /**
     * Sets the associated Hibernate session facgtory
     * @param factory
     */
    public void setSessionFactory(SessionFactory factory)
    {
    	_sessionFactory = factory;
    }
    
	//-------------------------------------------------------------------------
	//
	// Public interface
	//
	//-------------------------------------------------------------------------
	/* (non-Javadoc)
	 * @see org.dotnetguru.hottrick.server.dao.hibernate.IMessageDAO#loadMessage(int)
	 */
	@Transactional(propagation=Propagation.SUPPORTS)
	public IMessage loadLastMessage()
	{
	//	Create query
	//
		Query query = _sessionFactory.getCurrentSession().createQuery("from Message order by date desc");
		query.setMaxResults(1);
		
	//	Execute query
	//
		return (IMessage) query.uniqueResult();
	}
	
	/* (non-Javadoc)
	 * @see org.dotnetguru.hottrick.server.dao.hibernate.IMessageDAO#loadMessage(int)
	 */
	@SuppressWarnings("unchecked")
	@Transactional(propagation=Propagation.SUPPORTS)
	public List<IMessage> loadAllMessage(int startIndex, int maxResult)
	{
	//	Create query
	//
		Query query = _sessionFactory.getCurrentSession().createQuery("from Message order by date desc");
		query.setFirstResult(startIndex);
		query.setMaxResults(maxResult);
		
	//	Execute query
	//
		return (List<IMessage>) query.list();
	}
	
	/* (non-Javadoc)
	 * @see org.dotnetguru.hottrick.server.dao.hibernate.IMessageDAO#loadMessageAndUser(Integer)
	 */
	@Transactional(propagation=Propagation.SUPPORTS)
	public IMessage loadDetailedMessage(Integer id)
	{
	//	Create query
	//
		StringBuffer hqlQuery = new StringBuffer();
		hqlQuery.append("from Message message");
		hqlQuery.append(" inner join fetch message.author");
		hqlQuery.append(" left join fetch message.keywords");
		hqlQuery.append(" where message.id = :id");
		
	//	Fill query
	//
		Query query = _sessionFactory.getCurrentSession().createQuery(hqlQuery.toString());
		query.setInteger("id", id);
		
	//	Execute query
	//
		IMessage message = (IMessage) query.uniqueResult();
		
		return message;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.sf.hibernate4gwt.testApplication.server.dao.IMessageDAO#saveMessage(net.sf.hibernate4gwt.testApplication.domain.IMessage)
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	public void saveMessage(IMessage message)
	{
		_sessionFactory.getCurrentSession().saveOrUpdate(message);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.sf.hibernate4gwt.testApplication.server.dao.IMessageDAO#saveMessage(net.sf.hibernate4gwt.testApplication.domain.IMessage)
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	public void deleteMessage(IMessage message)
	{
		_sessionFactory.getCurrentSession().delete(message);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.sf.hibernate4gwt.testApplication.server.dao.IMessageDAO#lockMessage(net.sf.hibernate4gwt.testApplication.domain.IMessage)
	 */
	@Transactional(propagation=Propagation.SUPPORTS)
	public void lockMessage(IMessage message)
	{
		if (message.getId() > 0)
		{
			_sessionFactory.getCurrentSession().lock(message, LockMode.UPGRADE);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.sf.hibernate4gwt.testApplication.server.dao.IMessageDAO#countAllMessages()
	 */
	@Transactional(propagation=Propagation.SUPPORTS)
	public int countAllMessages()
	{
	//	Create query
	//
		Query query = _sessionFactory.getCurrentSession().createQuery("select count(*) from Message");
		
	//	Execute query
	//
		return ((Long) query.uniqueResult()).intValue();
	}
}