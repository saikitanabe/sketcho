/*
 * Copyright 2007 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.sf.hibernate4gwt.core;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;

import net.sf.beanlib.utils.ClassUtils;
import net.sf.hibernate4gwt.core.beanlib.IClassMapper;
import net.sf.hibernate4gwt.core.hibernate.HibernateUtil;
import net.sf.hibernate4gwt.core.store.IPojoStore;
import net.sf.hibernate4gwt.core.store.stateless.StatelessPojoStore;
import net.sf.hibernate4gwt.exception.CloneException;
import net.sf.hibernate4gwt.exception.InvocationException;
import net.sf.hibernate4gwt.exception.NotAssignableException;
import net.sf.hibernate4gwt.exception.NotHibernateObjectException;
import net.sf.hibernate4gwt.exception.TransientHibernateObjectException;
import net.sf.hibernate4gwt.pojo.base.ILazyPojo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;

/**
 * Manager for Hibernate POJO handling
 * @author bruno.marchesson
 *
 */
public class HibernateBeanManager
{
	//----
	// Attributes
	//----
	/**
	 * The unique instance of the Hibernate Helper
	 */
	private static HibernateBeanManager _instance = null;
	
	/**
	 * Log channel
	 */
	private Log _log = LogFactory.getLog(HibernateBeanManager.class);
	
	/**
	 * The associated POJO Handler
	 */
	protected IPojoStore _pojoStore;
	
	/**
	 * The Class mapper
	 */
	protected IClassMapper _classMapper;
		
	/**
	 * The POJO lazy killer
	 */
	protected LazyKiller _lazyKiller;
	
	/**
	 * The associated persistence util implementation
	 */
	protected IPersistenceUtil _persistenceUtil;
	
	//----
	// Property
	//----
	/**
	 * @return the unique instance of the singleton
	 */
	public synchronized static HibernateBeanManager getInstance()
	{
		if (_instance == null)
		{
			_instance = new HibernateBeanManager();
		}
		return _instance;
	}
	
	/**
	 * @return the pojo store
	 */
	public IPojoStore getPojoStore()
	{
		return _pojoStore;
	}
	
	/**
	 * set the used pojo store
	 */
	public void setPojoStore(IPojoStore pojoStore)
	{
		_log.info("Using PojoStore : " + pojoStore);
		_pojoStore = pojoStore;
		_pojoStore.setPersistenceUtil(_persistenceUtil);
		
		_lazyKiller.setPojoStore(_pojoStore);
	}
	
	/**
	 * @return the class mapper
	 */
	public IClassMapper getClassMapper()
	{
		return _classMapper;
	}
	
	/**
	 * @param mapper the class Mapper to set
	 */
	public void setClassMapper(IClassMapper mapper)
	{
		_log.info("Using class mapper : " + mapper);
		_classMapper = mapper;
		
		_lazyKiller.setClassMapper(mapper);
	}

	/**
	 * Helper setter to initialize HibernateHelper 
	 * Must be called on startup !
	 */
	public void setSessionFactory(SessionFactory sessionFactory)
	{
	//	TODO remove this method
	//
		((HibernateUtil)_persistenceUtil).setSessionFactory(sessionFactory);
	}
	
	/**
	 * Helper setter to initialize HibernateHelper with the embedded 
	 * Hibernate SessionFactory.
	 * Will not work with other JPA entity manager
	 * Lust be called on startup !
	 */
	public void setEntityManagerFactory(Object entityManagerFactory)
	{
		setSessionFactory(((org.hibernate.ejb.HibernateEntityManagerFactory) entityManagerFactory).getSessionFactory()); 
	}
	

	/**
	 * @return the _persistenceUtil
	 */
	public IPersistenceUtil getPersistenceUtil()
	{
		return _persistenceUtil;
	}

	/**
	 * @param util the _persistenceUtil to set
	 */
	public void setPersistenceUtil(IPersistenceUtil util)
	{
		_persistenceUtil = util;
		_pojoStore.setPersistenceUtil(util);
		
		_lazyKiller.setPersistenceUtil(util);
	}
	
	//-------------------------------------------------------------------------
	//
	// Constructor
	//
	//-------------------------------------------------------------------------
	/**
	 * Empty Constructor
	 */
	public HibernateBeanManager()
	{
	//	Default persistence utils
	//
		_persistenceUtil = HibernateUtil.getInstance();
		
		_pojoStore = new StatelessPojoStore();
		_pojoStore.setPersistenceUtil(_persistenceUtil);
		
		_lazyKiller = new LazyKiller();
		_lazyKiller.setPersistenceUtil(_persistenceUtil);
		_lazyKiller.setPojoStore(_pojoStore);
	}
		
	//-------------------------------------------------------------------------
	//
	// Hibernate Java 1.4 POJO public interface
	//
	//-------------------------------------------------------------------------
	/**
	 * Clone and store the Hibernate POJO(s)
	 */
	public Object clone(Object object)
	{
	//	Explicit clone : no assignable compatibility checking
	//
		return clone(object, false);
	}
	
	/**
	 * Clone and store the Hibernate POJO
	 * @param object the object to store
	 * @param assignable if the assignation from source to target class (via ClassMapper) must be checked
	 * @return the clone
	 */
	public Object clone(Object object, boolean assignable)
	{
	//	Precondition checking
	//
		if (object == null)
		{
			return null;
		}
	
	//	Collection handling
	//
		if (object instanceof Collection)
		{
			return cloneCollection((Collection)object, assignable);
		}
		else if (object instanceof Map)
		{
			return cloneMap((Map)object, assignable);
		}
		else if (object.getClass().isArray())
		{
		//	Clone as a collection
		//
			Object[] array = (Object[]) object;
			Collection<?> result = cloneCollection(Arrays.asList(array), assignable);
			
		//	Get the result as an array (much more tricky !!!)
		//
			Class<?> componentType = object.getClass().getComponentType();
			Object[] copy = (Object[])java.lang.reflect.Array.newInstance(componentType, array.length);
			return result.toArray(copy);
		}
		else
		{
			return clonePojo(object, assignable);
		}
	}
	
	/**
	 * Merge the clone POJO to its Hibernate counterpart
	 */
	public Object merge(Object object)
	{
	//	Explicit merge
	//
		return merge(object, false);
	}
	
	/**
	 * Merge the clone POJO to its Hibernate counterpart
	 */
	public Object merge(Object object, boolean assignable)
	{
	//	Precondition checking
	//
		if (object == null)
		{
			return null;
		}
	
	//	Collection handling
	//
		if (object instanceof Collection)
		{
			return mergeCollection((Collection)object, assignable);
		}
		else if (object instanceof Map)
		{
			return mergeMap((Map)object, assignable);
		}
		else if (object.getClass().isArray())
		{
		//	Merge as a collection
		//
			Object[] array = (Object[]) object;
			Collection<?> result = mergeCollection(Arrays.asList(array), assignable);
			
		//	Get the result as an array (much more tricky !!!)
		//
			Class<?> componentType = object.getClass().getComponentType();
			Object[] copy = (Object[])java.lang.reflect.Array.newInstance(componentType, array.length);
			return result.toArray(copy);
		}
		else
		{
			return mergePojo(object, assignable);
		}
	}
	
	/**
	 * Remove the object of the pojo store
	 */
	public void remove(Object object)
	{
	//	Precondition checking
	//
		if (object == null)
		{
			return;
		}
	
	//	Collection handling
	//
		if (object instanceof Collection)
		{
			removeCollection((Collection)object);
		}
		else if (object instanceof Map)
		{
			removeMap((Map)object);
		}
		else
		{
			removePojo(object);
		}
	}
	
	//-------------------------------------------------------------------------
	//
	// Hibernate Java 1.4 POJO internal methods
	//
	//-------------------------------------------------------------------------
	/**
	 * Clone and store the Hibernate POJO
	 * @param pojo the pojo to store
	 * @param assignable does the source and target class must be assignable?
	 * @exception NotAssignableException if source and target class are not assignable
	 */
	protected Object clonePojo(Object pojo, boolean assignable)
	{
	//	Null checking
	//
		if (pojo == null)
		{
			return null;
		}
		
	//	Precondition checking : is the pojo managed by Hibernate
	//
		Class targetClass = pojo.getClass();
		if (_persistenceUtil.isPersistentPojo(pojo) == true)
		{
		//	Assignation test
		//
			Class hibernateClass = _persistenceUtil.getPersistentClass(pojo.getClass());
			targetClass = null;
			if (_classMapper != null)
			{
				targetClass = _classMapper.getTargetClass(hibernateClass);
			}
			
			if (targetClass == null)
			{
				targetClass = hibernateClass;
			}
			
			if ((assignable == true) &&
				(hibernateClass.isAssignableFrom(targetClass) == false))
			{
				throw new NotAssignableException(hibernateClass, targetClass);
			}
			
		//	Proxy checking
		//
			if (_persistenceUtil.isProxy(pojo) == true)
			{
			//	If the root pojo is not initialized, replace it by null
			//
				return null;
			}
	
		//	Store the pojo
		//
			_pojoStore.store(pojo);
		}
		else if (holdPersistentObject(pojo) == false)
		{
		//	Do not clone not persistent classes, since they do not necessary
		//	implement Java Bean specification.
		//
			_log.info("Third party instance, not cloned : " + pojo.toString());
			return pojo;
		}
		
	//	Clone the pojo
	//
		return _lazyKiller.detach(pojo, targetClass);
	}
	
	/**
	 * Clone and store a map of Hibernate POJO
	 */
	protected Map cloneMap(Map hibernatePojoMap, boolean assignable)
	{
	//	Clone each element of the map
	//
		Map cloneMap = createNewMap(hibernatePojoMap);
		
		Iterator iterator = hibernatePojoMap.entrySet().iterator();
		while (iterator.hasNext())
		{
			Entry entry = (Entry) iterator.next();
			cloneMap.put(clone(entry.getKey(), assignable), 
						 clone(entry.getValue(), assignable));
		}
		
		return cloneMap;
	}
	
	/**
	 * Clone and store a collection of Hibernate POJO
	 */
	protected Collection cloneCollection(Collection hibernatePojoList, boolean assignable)
	{
	//	Clone each element of the collection
	//
		Collection clonePojoList = createNewCollection(hibernatePojoList);
		for (Object hibernatePojo : hibernatePojoList)
		{
			clonePojoList.add(clone(hibernatePojo, assignable));
		}
		
		return clonePojoList;
	}
	
	/**
	 * Retrieve the Hibernate Pojo and merge the modification from GWT 
	 * @param clonePojo the clone pojo
	 * @param assignable does the source and target class must be assignable
	 * @return the merged Hibernate POJO
	 * @exception UnsupportedOperationException if the clone POJO does not 
	 * implements ILazyPojo and the POJO store is stateless
	 * @exception NotAssignableException if source and target class are not assignable
	 */
	protected Object mergePojo(Object clonePojo, boolean assignable)
	{
	//	Get Hibernate associated class
	//
		Class cloneClass = clonePojo.getClass();
		Class hibernateClass = null;
		if (_classMapper != null)
		{
			hibernateClass =_classMapper.getSourceClass(cloneClass);
		}
		if (hibernateClass == null)
		{
		//	Not a clone : take the inner class
		//
			hibernateClass = clonePojo.getClass();
		}
		
	//	Precondition checking : is the pojo managed by Hibernate
	//
		if (_persistenceUtil.isPersistentClass(hibernateClass) == true)
		{
			//	Check lazy pojo inheritance
			//
			if ((clonePojo instanceof ILazyPojo == false) &&
				(_pojoStore.isStateless() == true))
			{
				throw new UnsupportedOperationException("Cannot merge Pure POJO without a stateful POJO store !\n"+
														cloneClass.getName() + " should implement ILazyPojo...");
			}
			
		//	Assignation checking
		//
			if ((assignable == true) &&
				(hibernateClass.isAssignableFrom(cloneClass) == false))
			{
				throw new NotAssignableException(hibernateClass, cloneClass);
			}
		}
		
	//	Retrieve the pojo
	//
		try
		{
			Object hibernatePojo = null;
			_pojoStore.beforeRestore();
			try
			{
				hibernatePojo = _pojoStore.restore(clonePojo, hibernateClass);
				if (hibernatePojo == null)
				{
					_log.info("HibernatePOJO not found : can be transient or deleted data : " + clonePojo);
				}
			}
			catch(TransientHibernateObjectException ex)
			{
				_log.info("Transient object : " + clonePojo);
			}
			catch(NotHibernateObjectException ex)
			{
				if (holdPersistentObject(clonePojo) == false)
				{
				//	Do not merge not persistent instance, since they do not necessary
				//	implement the Java bean specification
				//
					_log.info("Third party object, not merged : " + clonePojo);
					return clonePojo;
				}
				else
				{
					_log.info("Wrapping object : " + clonePojo);
				}
			}
			
			if (hibernatePojo == null)
			{
				if (ClassUtils.immutable(hibernateClass))
				{
				//	Do not clone immutable types
				//
					return clonePojo;
				}
				
			//	Create a new wrapper instance
			//
				try
				{
					hibernatePojo = hibernateClass.newInstance();
				}
				catch(Exception e)
				{
					throw new RuntimeException("Cannot create a fresh new instance of the class " + hibernateClass, e);
				} 
			}
			
		//	Merge the modification in the Hibernate Pojo
		//
			_lazyKiller.attach(hibernatePojo, clonePojo);
			return hibernatePojo;
		}
		finally
		{
			_pojoStore.afterRestore();
		}
	}

	/**
	 * Retrieve the Hibernate Pojo List and merge the modification from GWT 
	 * @param clonePojoList the clone pojo list
	 * @return a list of merged Hibernate POJO 
	 * @exception UnsupportedOperationException if a POJO from the list does 
	 * not implements ILazyPojo and the POJO store is stateless
	 */
	protected Collection mergeCollection(Collection clonePojoList, boolean assignable)
	{
		Collection hibernatePojoList = createNewCollection(clonePojoList);
		
	//	Retrieve every hibernate from pojo list
	//
		for (Object clonePojo : clonePojoList)
		{
			try
			{
				hibernatePojoList.add(merge(clonePojo, assignable));
			}
			catch(TransientHibernateObjectException e)
			{
			//	Keep new pojo (probably created from GWT)
			//
				hibernatePojoList.add(clonePojo);
			}
		}
		
		return hibernatePojoList;
	}
	
	/**
	 * Fill copy map with Hibernate merged POJO
	 * @param cloneMap
	 * @return a map with merge Hibernate POJO
	 */
	protected Map mergeMap(Map cloneMap, boolean assignable)
	{
		Map hibernateMap = new HashMap();
		
	//	Iterate over map
	//
		Iterator iterator = cloneMap.entrySet().iterator();
		while (iterator.hasNext())
		{
			Entry entry = (Entry) iterator.next();
			
			// Merge key
			Object key = entry.getKey();
			try
			{
				key = merge(key, assignable);
			}
			catch (TransientHibernateObjectException ex)
			{ /* keep key untouched */ }
			
			// Merge value
			Object value = entry.getValue();
			try
			{
				value = merge(value, assignable);
			}
			catch (TransientHibernateObjectException ex)
			{ /* keep value untouched */ }
			
			hibernateMap.put(key, value);
		}
		
		return hibernateMap;
	}
	
	/**
	 * Remove the pojo from the pojo store
	 * @param pojo hibernate pojo
	 */
	protected void removePojo(Object pojo)
	{
		_pojoStore.remove(pojo);
	}
	
	/**
	 * Remove the content of the map of pojo from the pojo store
	 * @param pojoList hibernate pojo list
	 */
	protected void removeMap(Map pojoMap)
	{
		Iterator iterator = pojoMap.entrySet().iterator();
		while (iterator.hasNext())
		{
			Entry entry = (Entry) iterator.next();
			
			remove(entry.getKey());
			remove(entry.getValue());
		}
	}
	/**
	 * Remove the collection of pojo from the pojo store
	 * @param pojoList hibernate pojo collection
	 */
	protected void removeCollection(Collection pojoList)
	{
		for (Object pojo : pojoList)
		{
			remove(pojo);
		}
	}
	/**
	 * Create a new collection with the same behavior than the argument one
	 * @param pojoCollection the source collection
	 * @return a newly created, empty collection
	 */
	@SuppressWarnings("unchecked")
	protected Collection<Object> createNewCollection(Collection<?> pojoCollection)
	{
		Class<? extends Collection> collectionClass = pojoCollection.getClass(); 
		
		if (_persistenceUtil.isPersistentCollection(collectionClass) ||
			collectionClass.isAnonymousClass() ||
			collectionClass.isMemberClass() ||
			collectionClass.isLocalClass())
		{
		//	Create a basic collection
		//
			if (pojoCollection instanceof List)
			{
				return new ArrayList<Object>(pojoCollection.size());
			}
			else if (pojoCollection instanceof Set)
			{
				if (pojoCollection instanceof SortedSet)
				{
					return new TreeSet<Object>();
				}
				else
				{
					return new HashSet<Object>(pojoCollection.size());
				}
			}
			else
			{
				throw new CloneException("Unhandled collection type : " + pojoCollection.getClass().toString());
			}
		}
		else
		{
		//	Create the same collection
		//
			Collection<Object> result = null;
			try
			{
			//	First, search constructor with initial capacity argument
			//
				Constructor<?> constructor = collectionClass.getConstructor(Integer.TYPE); 
				result = (Collection<Object>) constructor.newInstance(pojoCollection.size());
			}
			catch(NoSuchMethodException e)
			{
			//	No such constructor, so search the empty one
				try
				{
					Constructor<?> constructor = collectionClass.getConstructor((Class[]) null); 
					result = (Collection<Object>) constructor.newInstance();
				}
				catch(Exception ex)
				{
					throw new RuntimeException("Cannot instantiate collection !", ex);
				}
			}
			catch(Exception ex)
			{
				throw new RuntimeException("Cannot instantiate collection !", ex);
			}
			
			if (collectionClass.getPackage().getName().startsWith("java") == false)
			{
			//	Extend collections (such as PagingList)
			//
				_lazyKiller.populate(result, pojoCollection);
			}
			
			return result;
		}
	}
	
	/**
	 * Create a new map with the same behavior than the argument one
	 * @param pojoMap the source map
	 * @return a newly created, empty map
	 */
	protected Map createNewMap(Map pojoMap)
	{
		Class<? extends Map> mapClass = pojoMap.getClass(); 
		
		if (_persistenceUtil.isPersistentCollection(mapClass) ||
			mapClass.isAnonymousClass() ||
			mapClass.isMemberClass() ||
			mapClass.isLocalClass())
		{
			return new HashMap();
		}
		else
		{
		//	Create the same map
		//
			try
			{
				Constructor<?> constructor = mapClass.getConstructor((Class[]) null); 
				return (Map) constructor.newInstance();
			}
			catch(Exception ex)
			{
				throw new RuntimeException("Cannot instantiate collection !", ex);
			}	
		}
	}
	/**
	 * In deep persistent association checking.
	 * This method is used to detect wrapping object (ie not persistent
	 * class holding persistent associations)
	 * @param pojo the wrapping pojo
	 * @return true if the pojo contains persistent member, false otherwise
	 */
	protected boolean holdPersistentObject(Object pojo)
	{
		try
		{
		//	Precondition checking
		//
			if (pojo == null)
			{
				return false;
			}
			
			if ((_persistenceUtil.isPersistentPojo(pojo) == true) ||
				(_persistenceUtil.isPersistentCollection(pojo.getClass())))
			{
				return true;
			}
			
		//	Iterate over properties
		//
			BeanInfo info = Introspector.getBeanInfo(pojo.getClass());
			PropertyDescriptor[] descriptors = info.getPropertyDescriptors();
			for (int index = 0; index < descriptors.length; index++)
			{
				PropertyDescriptor descriptor = descriptors[index];
				Class<?> propertyClass = descriptor.getPropertyType();
				boolean isCollection = Collection.class.isAssignableFrom(propertyClass) ||
				   					   Map.class.isAssignableFrom(propertyClass);

				if ((ClassUtils.immutable(propertyClass) == true) ||
					((ClassUtils.isJavaPackage(propertyClass) == true) &&
					 (isCollection == false)))
				{
				//	Basic type : no check needed
				//
					continue;
				}
				// Get property value
				Method readMethod = descriptor.getReadMethod();
				if (readMethod == null)
				{
				//	No reader, cannot check
				//
					continue;
				}
				
				readMethod.setAccessible(true);
				Object propertyValue = readMethod.invoke(pojo, (Object[])null);
				
				if (propertyValue == null)
				{
					continue;
				}
				// Get real property class
				propertyClass = propertyValue.getClass();
				
				if ((_classMapper != null) &&
					(_classMapper.getSourceClass(propertyClass) != null))
				{
					propertyClass = _classMapper.getSourceClass(propertyClass);
				}
				
				if ((_persistenceUtil.isPersistentClass(propertyClass) == true) ||
					(_persistenceUtil.isPersistentCollection(propertyClass) == true))
				{
					return true;
				}
				
				// collection and recursive search handling
				if (propertyValue != null)
				{
					if (propertyValue instanceof Collection<?>)
					{
					//	Check collection values
					//
						Collection<?> propertyCollection = (Collection<?>)propertyValue;
						for(Object value : propertyCollection)
						{
							if (holdPersistentObject(value) == true)
							{
								return true;
							}
						}
					}
					else if (propertyValue instanceof Map<?, ?>)
					{
					//	Check map entry and values
					//
						Map<?,?> propertyMap = (Map<?, ?>) propertyValue;
						for(Map.Entry<?, ?> value : propertyMap.entrySet())
						{
							if ((holdPersistentObject(value.getKey()) == true) ||
								(holdPersistentObject(value.getValue()) == true))
							{
								return true;
							}
						}
					}
					else
					{
					//	Recursive search
					//
						if (holdPersistentObject(propertyValue) == true)
						{
							return true;
						}
					}
				}
			}
			
			// No persistent property
			return false;
		}
		catch (Exception e)
		{
			throw new InvocationException(e);
		}
	}
}