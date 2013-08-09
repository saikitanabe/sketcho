package net.sf.hibernate4gwt.core.hibernate;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import net.sf.beanlib.hibernate.UnEnhancer;
import net.sf.hibernate4gwt.core.IPersistenceUtil;
import net.sf.hibernate4gwt.exception.NotHibernateObjectException;
import net.sf.hibernate4gwt.exception.TransientHibernateObjectException;
import net.sf.hibernate4gwt.pojo.base.IUserType;

import org.hibernate.EntityMode;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.collection.PersistentCollection;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.CollectionType;
import org.hibernate.type.Type;

/**
 * Persistent helper for Hibernate implementation
 * Centralizes the SessionFactory and add some needed methods.
 * Not really a singleton, since there can be as many HibernateUtil instance as different sessionFactories
 * @author BMARCHESSON
 */
public class HibernateUtil implements IPersistenceUtil
{
	//----
	// Attributes
	//----
	/**
	 * The pseudo unique instance of the singleton
	 */
	private static HibernateUtil _instance = null;
	
	/**
	 * The Hibernate session factory
	 */
	private SessionFactory _sessionFactory;	
	
	/**
	 * The persistance map, with persistance status of all classes
	 * including persistent component classes
	 */
	private Map<Class<?>, Boolean> _persistenceMap;
	
	/**
	 * The current opened session
	 */
	private ThreadLocal<Session> _session;
	
	//----
	// Properties
	//----
	/**
	 * @return the unique instance of the singleton
	 */
	public static HibernateUtil getInstance()
	{
		if (_instance == null)
		{
			_instance = new HibernateUtil();
		}
		return _instance;
	}
	
	/**
	 * @return the hibernate session Factory
	 */
	public SessionFactory getSessionFactory()
	{
		return _sessionFactory;
	}

	/**
	 * @param sessionFactory the factory to set
	 */
	public void setSessionFactory(SessionFactory sessionFactory)
	{
		_sessionFactory = sessionFactory;
	}
	
	//-------------------------------------------------------------------------
	//
	// Constructor
	//
	//-------------------------------------------------------------------------
	/**
	 * Default constructor
	 */
	public HibernateUtil()
	{
		_session = new ThreadLocal<Session>();
		_persistenceMap = new HashMap<Class<?>, Boolean>();
		
		// Filling persistence map with primitive types
		_persistenceMap.put(Byte.class, false);
		_persistenceMap.put(Short.class, false);
		_persistenceMap.put(Integer.class, false);
		_persistenceMap.put(Long.class, false);
		_persistenceMap.put(Float.class, false);
		_persistenceMap.put(Double.class, false);
		_persistenceMap.put(Boolean.class, false);
		_persistenceMap.put(String.class, false);
	}
	
	//-------------------------------------------------------------------------
	//
	// Public interface
	//
	//-------------------------------------------------------------------------
	/* (non-Javadoc)
	 * @see net.sf.hibernate4gwt.core.hibernate.IPersistenceUtil#getId(java.lang.Object)
	 */
	public Serializable getId(Object pojo)
	{
		return getId(pojo, pojo.getClass());
	}
	
	/* (non-Javadoc)
	 * @see net.sf.hibernate4gwt.core.hibernate.IPersistenceUtil#getId(java.lang.Object, java.lang.Class)
	 */
	public Serializable getId(Object pojo, Class<?> hibernateClass)
	{
	//	Precondition checking
	//
		if (_sessionFactory == null)
		{
			throw new NullPointerException("No Hibernate Session Factory defined !");
		}
		
	//	Unenhance Class<?> if needed
	//
		hibernateClass = UnEnhancer.unenhanceClass(hibernateClass);
		
	//	Persistence checking
	//
		if (isPersistentClass(hibernateClass) == false)
		{
		//	Not an hibernate Class !
		//
			throw new NotHibernateObjectException(hibernateClass);			
		}
		
	//	Retrieve Class<?> hibernate metadata
	//
		ClassMetadata hibernateMetadata = _sessionFactory.getClassMetadata(hibernateClass);
		if (hibernateMetadata == null)
		{
		//	Component class (persistent but not metadata) : no associated id
		//	So must be considered as transient
		//
			throw new TransientHibernateObjectException(pojo);
		}
		
	//	Retrieve ID
	//
		Serializable id = null;
		
		if (hibernateClass.equals(pojo.getClass()))
		{
		//	the pojo has the same class, simple use metadata
		//
			id = hibernateMetadata.getIdentifier(pojo, EntityMode.POJO);	
		}
		else
		{
		//	DTO case : invoke the method with the same name
		//
			String property = hibernateMetadata.getIdentifierPropertyName();
			
			try
			{
				// compute getter method name
				property = property.substring(0,1).toUpperCase() + 
						   property.substring(1);
				String getter = "get" + property;
				
				// Find getter method
				Class<?> pojoClass = pojo.getClass();
				Method method = pojoClass.getMethod(getter, (Class[])null);
				if (method == null)
				{
					throw new RuntimeException("Cannot find method " + getter + " for Class<?> " + pojoClass);
				}
				id = (Serializable) method.invoke(pojo,(Object[]) null);
			}
			catch (Exception ex)
			{
				throw new RuntimeException("Invocation exception ", ex);
			}
		}
		
	//	Post condition checking
	//
		if (id == null)
		{
			throw new TransientHibernateObjectException(pojo);
		}
		return id;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.hibernate4gwt.core.hibernate.IPersistenceUtil#isHibernatePojo(java.lang.Object)
	 */
	public boolean isPersistentPojo(Object pojo)
	{
		if (pojo == null)
		{
			return false;
		}
		return isPersistentClass(pojo.getClass());
	}
	
	/* (non-Javadoc)
	 * @see net.sf.hibernate4gwt.core.hibernate.IPersistenceUtil#isHibernateClass(java.lang.Class)
	 */
	public boolean isPersistentClass(Class<?> clazz)
	{
	//	Precondition checking
	//
		if (_sessionFactory == null)
		{
			throw new NullPointerException("No Hibernate Session Factory defined !");
		}
		
	//	Check proxy (based on beanlib Enhancer class)
	//
		clazz = UnEnhancer.unenhanceClass(clazz);
		
	//	Look into the persistence map
	//
		synchronized (_persistenceMap) {
			Boolean persistent = _persistenceMap.get(clazz);
			if (persistent != null)
			{
				return persistent.booleanValue();
			}
		}
		
	//	First clall for this Class<?> : compute persistence class
	//
		computePersistenceForClass(clazz);
		return _persistenceMap.get(clazz).booleanValue();
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.sf.hibernate4gwt.core.IPersistenceUtil#isPersistentCollection(java.lang.Class)
	 */
	public boolean isPersistentCollection(Class<?> collectionClass)
	{
		return (PersistentCollection.class.isAssignableFrom(collectionClass));
	}
	
	/* (non-Javadoc)
	 * @see net.sf.hibernate4gwt.core.hibernate.IPersistenceUtil#getPersistentClass(java.lang.Class)
	 */
	public Class<?> getPersistentClass(Class<?> clazz)
	{
	//	Precondition checking
	//
		if (_sessionFactory == null)
		{
			throw new NullPointerException("No Hibernate Session Factory defined !");
		}
		
	//	Check proxy (based on beanlib Enhancer class)
	//
		clazz = UnEnhancer.unenhanceClass(clazz);
		
		return clazz;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.hibernate4gwt.core.hibernate.IPersistenceUtil#isEnhanced(java.lang.Class)
	 */
	public boolean isEnhanced(Class<?> clazz)
	{
	//	Check proxy (based on beanlib Enhancer class)
	//
		return (UnEnhancer.unenhanceClass(clazz) != clazz);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.hibernate4gwt.core.hibernate.IPersistenceUtil#openSession()
	 */
	public void openSession()
	{
	//	Precondition checking
	//
		if (_sessionFactory == null)
		{
			throw new NullPointerException("No Hibernate Session Factory defined !");
		}
		
	//	Open a new session
	//
		Session session = _sessionFactory.openSession();
		
	//	Store the session in ThreadLocal
	//
		_session.set(session);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.hibernate4gwt.core.hibernate.IPersistenceUtil#closeSession(java.lang.Object)
	 */
	public void closeCurrentSession()
	{
		Session session = _session.get();
		if (session != null)
		{
			session.close();
			_session.remove();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.hibernate4gwt.core.IPersistenceUtil#load(java.io.Serializable, java.lang.Class)
	 */
	public Object load(Serializable id, Class<?> persistentClass)
	{
	//	Get current opened session
	//
		Session session = _session.get();
		if (session == null)
		{
			throw new NullPointerException("Cannot load : no session opened !");
		}
		
	//	Unenhance persistent class if needed
	//
		persistentClass = getPersistentClass(persistentClass);
		
	//	Load the entity
	//
		return session.get(persistentClass, id);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.sf.hibernate4gwt.core.IPersistenceUtil#isProxy(java.lang.Object)
	 */
	public boolean isProxy(Object proxy)
	{
		return (Hibernate.isInitialized(proxy) == false);
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.hibernate4gwt.core.IPersistenceUtil#initializeProxy(java.lang.Object)
	 */
	public void initializeProxy(Object proxy)
	{
		Hibernate.initialize(proxy);	
	}

	//-------------------------------------------------------------------------
	//
	// Internal methods
	//
	//-------------------------------------------------------------------------
	/**
	 * Compute embedded persistence (Component, UserType) for argument class
	 */
	private void computePersistenceForClass(Class<?> clazz)
	{
		ClassMetadata metadata = _sessionFactory.getClassMetadata(clazz);
		if (metadata == null)
		{
		//	Not persistent !
		//
			synchronized (_persistenceMap) {
				_persistenceMap.put(clazz, false);
			}
			return;
		}

	//	Persistent class
	//
		synchronized (_persistenceMap) {
			_persistenceMap.put(clazz, true);
		}
		
	//	Look for component classes
	//
		Type[] types = metadata.getPropertyTypes();
		for (int index = 0; index < types.length; index++)
		{
			Type type = types[index];
			if ((type.isComponentType()) ||
				(IUserType.class.isAssignableFrom(type.getReturnedClass())))
			{
			//	Add the Class to the persistent map
			//
				synchronized (_persistenceMap)
				{
					_persistenceMap.put(type.getReturnedClass(), true);
				}
			}
			else if(type.isCollectionType()) 
			{
			//	Check collection element type
			//
				Type elementType = ((CollectionType)type).getElementType((SessionFactoryImplementor)_sessionFactory); 
				if(elementType.isComponentType()) 
				{
					synchronized (_persistenceMap)
					{
						_persistenceMap.put(elementType.getReturnedClass(), true);
					}
				} 
			} 
		}
		
	}
}