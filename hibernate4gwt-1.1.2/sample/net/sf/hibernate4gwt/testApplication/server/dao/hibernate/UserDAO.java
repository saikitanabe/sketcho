/**
 * 
 */
package net.sf.hibernate4gwt.testApplication.server.dao.hibernate;

import java.util.List;

import net.sf.hibernate4gwt.testApplication.domain.IUser;
import net.sf.hibernate4gwt.testApplication.server.dao.IUserDAO;

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
public class UserDAO implements IUserDAO
{
	//----
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
    /**
     * Load the user with the argument ID
     */
    @Transactional(propagation=Propagation.SUPPORTS)
	public IUser loadUser(Integer id)
	{
	//	Create query
	//
		Query query = _sessionFactory.getCurrentSession().createQuery("from User user where user.id=:id");
		query.setInteger("id", id);
		
	//	Execute query
	//
		return (IUser) query.uniqueResult();
	}
    
    /**
     * Load the user with the argument login
     */
    @Transactional(propagation=Propagation.SUPPORTS)
	public IUser searchUserAndMessagesByLogin(String login)
	{
	//	Create query
	//
    	StringBuffer hqlQuery = new StringBuffer();
    	hqlQuery.append("from User user");
    	hqlQuery.append(" left join fetch user.messageList");
    	hqlQuery.append(" where user.login=:login");
    	
    //	Fill query
    //
		Query query = _sessionFactory.getCurrentSession().createQuery(hqlQuery.toString());
		query.setString("login", login);
		
	//	Execute query
	//
		return (IUser) query.uniqueResult();
	}
    
    /**
     * Load all the users
     */
    @SuppressWarnings("unchecked")
    @Transactional(propagation=Propagation.SUPPORTS)
	public List<IUser> loadAll()
	{
	//	Create query
	//
		Query query = _sessionFactory.getCurrentSession().createQuery("from User user");
		
	//	Execute query
	//
		return (List<IUser>) query.list();
	}
    
    /**
     * Count all the users
     */
    @Transactional(propagation=Propagation.SUPPORTS)
	public int countAll()
	{
	//	Create query
	//
		Query query = _sessionFactory.getCurrentSession().createQuery("select count(*) from User user");
		
	//	Execute query
	//
		return ((Long) query.uniqueResult()).intValue();
	}
	
    /**
     * Save the argument user
     * @param user the user to save or create
     */
	@Transactional(propagation=Propagation.REQUIRED)
	public void saveUser(IUser user)
	{
		_sessionFactory.getCurrentSession().saveOrUpdate(user);
	}
}