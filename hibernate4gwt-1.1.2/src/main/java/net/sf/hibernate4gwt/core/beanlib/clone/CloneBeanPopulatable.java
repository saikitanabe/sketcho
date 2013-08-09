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
package net.sf.hibernate4gwt.core.beanlib.clone;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.sf.beanlib.spi.DetailedBeanPopulatable;
import net.sf.hibernate4gwt.core.IPersistenceUtil;
import net.sf.hibernate4gwt.core.store.IPojoStore;
import net.sf.hibernate4gwt.pojo.base.ILazyPojo;
import net.sf.hibernate4gwt.util.IntrospectionHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Populatable for Hibernate lazy handling
 * @author bruno.marchesson
 */
public class CloneBeanPopulatable implements DetailedBeanPopulatable 
{
	//----
	// Constants
	//----
	/**
	 * Name of the lazy properties attributes
	 */
	private final String LAZY_PROPERTIES = "lazyProperties";
	
	//----
	// Attributes
	//----
	/**
	 * Log channel
	 */
	private static Log _log = LogFactory.getLog(CloneBeanPopulatable.class);
	
	/**
	 * The associated persistence utils
	 */
	private IPersistenceUtil _persistenceUtil;
	
	/**
	 * The used pojo store
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
	 * @return the used pojo Store
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
	public CloneBeanPopulatable(IPersistenceUtil persistenceUtil,
								IPojoStore pojoStore)
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
								  Object fromBean, 
								  Method readerMethod, 
								  Object toBean, 
								  Method setterMethod)
	{
	//	Is the property lazy loaded ?
	//
		try
		{
			if (LAZY_PROPERTIES.equals(propertyName) == true)
			{
				return false;
			}
			Object result = readPropertyValue(fromBean, readerMethod.getName());
			if (_persistenceUtil.isProxy(result) == true)
			{
			//	Lazy property !
			//
				_log.info(fromBean.toString()+ "." + propertyName + " --> lazy loaded ");
				if (toBean instanceof ILazyPojo == true)
				{
				//	Mark as lazy
				//
					((ILazyPojo)toBean).addLazyProperty(propertyName);
				}
				return false;
			}
			else
			{
			//	Store the pojo before clone
			//
				if (_persistenceUtil.isPersistentPojo(result))
				{
					_pojoStore.store(result);
				}
				return true;
			}
		}
		catch(Exception ex)
		{
			throw new RuntimeException(ex);
		}
	}

	//-------------------------------------------------------------------------
	//
	// Internal methods
	//
	//-------------------------------------------------------------------------
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
}
