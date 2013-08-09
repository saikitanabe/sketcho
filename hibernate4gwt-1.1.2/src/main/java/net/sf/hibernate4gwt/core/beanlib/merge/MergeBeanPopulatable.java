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
package net.sf.hibernate4gwt.core.beanlib.merge;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import net.sf.beanlib.spi.DetailedBeanPopulatable;
import net.sf.hibernate4gwt.core.IPersistenceUtil;
import net.sf.hibernate4gwt.core.beanlib.BeanlibThreadLocal;
import net.sf.hibernate4gwt.core.store.IPojoStore;
import net.sf.hibernate4gwt.exception.TransientHibernateObjectException;
import net.sf.hibernate4gwt.pojo.base.ILazyPojo;
import net.sf.hibernate4gwt.util.IntrospectionHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Populatable for POJO with the lazy information of the Hibernate POJO
 * This populatable is used to fill back an Hibernate POJO from a pure clone
 * POJO (not implementing ILazyPojo).
 * Only the not lazy properties of the Hibernate POJO are filled.
 * To be correct, the clone and merge operations must be managed with the
 * <b>same</b> Hibernate POJO, or a POJO with the <b>same</b> fetching strategy
 * @author bruno.marchesson
 */
public class MergeBeanPopulatable implements DetailedBeanPopulatable 
{
	//----
	// Attributes
	//----
	/**
	 * Log channel
	 */
	private static Log _log = LogFactory.getLog(MergeBeanPopulatable.class);
	
	/**
	 * The associated persistence utils
	 */
	private IPersistenceUtil _persistenceUtil;
	
	/**
	 * The used Pojo store
	 */
	private IPojoStore _pojoStore;
	
	//----
	// Properties
	//----
	/**
	 * @return the persistence Util implementation to use
	 */
	public IPersistenceUtil getPersistenceUtil()
	{
		return _persistenceUtil;
	}

	/**
	 * @param util the persistenceUtil to set
	 */
	public void setPersistenceUtil(IPersistenceUtil util)
	{
		_persistenceUtil = util;
	}
	
	/**
	 * @return the pojo store
	 */
	public IPojoStore getPojoStore()
	{
		return _pojoStore;
	}

	/**
	 * @param store the pojo store to set
	 */
	public void setPojoStore(IPojoStore store)
	{
		_pojoStore = store;
	}

	//-------------------------------------------------------------------------
	//
	// Constructor
	//
	//-------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public MergeBeanPopulatable(IPersistenceUtil persistenceUtil, IPojoStore pojoStore)
	{
		setPersistenceUtil(persistenceUtil);
		setPojoStore(pojoStore);
	}
	
	//-------------------------------------------------------------------------
	//
	// DetailedBeanPopulatable implementation
	//
	//-------------------------------------------------------------------------	
	/* (non-Javadoc)
	 * @see net.sf.beanlib.spi.DetailedBeanPopulatable#shouldPopulate(java.lang.String, java.lang.Object, java.lang.reflect.Method, java.lang.Object, java.lang.reflect.Method)
	 */
	public boolean shouldPopulate(String propertyName, 
								  Object cloneBean, 
								  Method readerMethod, 
								  Object persistentBean, 
								  Method setterMethod)
	{
	//	Reinit beanlib thread local
	//
		BeanlibThreadLocal.setDestinationBean(null);
		try
		{
		//	Get clone and persistent value
		//
			Object cloneValue = readPropertyValue(cloneBean, readerMethod.getName());
			Object persistentValue = readPropertyValue(persistentBean, readerMethod.getName());
			
			// Null value : can be a proxy
			if (isNullValue(cloneValue))
			{
				if (isLazyProperty(cloneBean, persistentValue, propertyName) == true)
				{
				//	Let the proxy untouched
				//
					if (_log.isDebugEnabled())
					{
						_log.debug(propertyName + " --> lazy loaded !");
					}
					return false;
				}
				else
				{
				//	Regular case
				//
					return true;
				}
			}
		
			
			if ((persistentValue instanceof Collection) ||
				(persistentValue instanceof Map))
			{
			//	collection handling
			//
				BeanlibThreadLocal.setDestinationBean(persistentValue);
			}
			else if (_persistenceUtil.isPersistentPojo(persistentValue))
			{
			//	Persistent value : load the associated proxy
			//
				try
				{
					Object loadedProxy = _pojoStore.restore(cloneValue, persistentValue.getClass());
					BeanlibThreadLocal.setDestinationBean(loadedProxy);
				}
				catch(TransientHibernateObjectException e)
				{
				//	Transient value : no proxy
				//
					BeanlibThreadLocal.setDestinationBean(null);
				}
			}
			
			return true;
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	
	//-------------------------------------------------------------------------
	//
	// Internal method
	//
	//-------------------------------------------------------------------------
	/**
	 * Indicates if the argument property is lazy or not
	 * @param cloneBean
	 * @param persistentBean
	 * @param property
	 * @param readerMethod
	 * @return
	 */
	protected boolean isLazyProperty(Object cloneBean,
									 Object persistentValue,
									 String property)
	{
		if (cloneBean instanceof ILazyPojo)
		{
			return ((ILazyPojo) cloneBean).isLazyProperty(property);
		}
		else
		{
		//	Stateful mode
		//
			return _persistenceUtil.isProxy(persistentValue);
		}
	}
	
	/**
	 * Read a property value, even if it has a private getter
	 */
	private Object readPropertyValue(Object bean, String propertyGetter) 
					throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException
	{
		Method readMethod = IntrospectionHelper.getRecursiveDeclaredMethod(bean.getClass(), propertyGetter, (Class[]) null);
		readMethod.setAccessible(true);
		
		return readMethod.invoke(bean, (Object[]) null);
	}
	
	/**
	 * Indicates if the argument value must be considered as null
	 * (empty collections or map are also considered as null)
	 * @param value
	 * @return
	 */
	private boolean isNullValue(Object value)
	{
		if (value == null)
		{
			return true;
		}
		else if (value instanceof Collection)
		{
			return ((Collection<?>) value).isEmpty();
		}
		else if (value instanceof Map)
		{
			return ((Map<?,?>)value).isEmpty();
		}
		
		return false;
	}
}
