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

package net.sf.hibernate4gwt.core.store.stateless;

import java.io.Serializable;

import net.sf.hibernate4gwt.core.IPersistenceUtil;
import net.sf.hibernate4gwt.core.hibernate.HibernateUtil;
import net.sf.hibernate4gwt.core.store.IPojoStore;

/**
 * Stateless Pojo Handler
 * @author bruno.marchesson
 *
 */
public class StatelessPojoStore implements IPojoStore
{
	//----
	// Attribute
	//----
	/**
	 * Persistence util
	 */
	protected IPersistenceUtil _persistenceUtil;
	
	//----
	// Properties
	//----	
	/**
	 * @return the persistence Util class
	 */
	public IPersistenceUtil getPersistenceUtil()
	{
		return _persistenceUtil;
	}

	/**
	 * @param util the persistence Util to set
	 */
	public void setPersistenceUtil(IPersistenceUtil util)
	{
		_persistenceUtil = util;
	}

	//-------------------------------------------------------------------------
	//
	// Constructor
	//
	//-------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public StatelessPojoStore()
	{
	//	Default persistence util
	//
		_persistenceUtil = HibernateUtil.getInstance();
	}

	//-------------------------------------------------------------------------
	//
	// Pojo Handler implementation
	//
	//-------------------------------------------------------------------------
	/* (non-Javadoc)
	 * @see org.dotnetguru.lazykiller.web.IPojoHandler#isSteteless()
	 */
	public boolean isStateless()
	{
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.dotnetguru.lazykiller.web.IPojoHandler#store(javax.servlet.http.HttpServletRequest, org.dotnetguru.lazykiller.pojo.ILazyPojo, java.lang.String)
	 */
	public void store(Object object)
	{
	//	Do nothing...
	}
	
	/* (non-Javadoc)
	 * @see org.dotnetguru.lazykiller.web.IPojoHandler#remove(javax.servlet.http.HttpServletRequest, org.dotnetguru.lazykiller.pojo.ILazyPojo, java.lang.String)
	 */
	public void remove(Object object)
	{
	//	Do nothing...
	}

	/* (non-Javadoc)
	 * @see org.dotnetguru.lazykiller.web.IPojoHandler#restore(javax.servlet.http.HttpServletRequest, org.dotnetguru.lazykiller.pojo.ILazyPojo, java.lang.String)
	 */
	public Object restore(Object clone, Class<?> persistentClass)
	{
		Serializable id = _persistenceUtil.getId(clone, persistentClass);
		return _persistenceUtil.load(id, persistentClass);
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.hibernate4gwt.core.store.IPojoStore#afterRestore()
	 */
	public void afterRestore()
	{
	//	Close current session
	//
		_persistenceUtil.closeCurrentSession();
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.hibernate4gwt.core.store.IPojoStore#beforeRestore()
	 */
	public void beforeRestore()
	{
	//	Open Persistent session
	//
		_persistenceUtil.openSession();
	}
}
