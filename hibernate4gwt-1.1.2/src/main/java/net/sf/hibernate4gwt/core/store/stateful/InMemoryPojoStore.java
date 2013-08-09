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

package net.sf.hibernate4gwt.core.store.stateful;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.hibernate4gwt.core.IPersistenceUtil;
import net.sf.hibernate4gwt.core.store.IPojoStore;
import net.sf.hibernate4gwt.exception.TransientHibernateObjectException;

/**
 * In Memory POJO Handler
 * This class stores POJO in a simple hashmap
 * TODO recursive add and remove of persistent beans !
 * @author bruno.marchesson
 *
 */
public class InMemoryPojoStore implements IPojoStore
{
	//----
	// Attributes
	//----
	/**
	 * Log channel
	 */
	private static Log _log = LogFactory.getLog(InMemoryPojoStore.class);
	
	/**
	 * The store hashmap
	 */
	protected HashMap _map = new HashMap();
	
	/**
	 * The associated persistence util
	 */
	protected IPersistenceUtil _persistenceUtil;
	
	//----
	// Properties
	//----
	/**
	 * @return the persistence Util implementation
	 */
	public IPersistenceUtil getPersistenceUtil() {
		return _persistenceUtil;
	}

	/**
	 * @param persistenceUtil the persistence Util to set
	 */
	public void setPersistenceUtil(IPersistenceUtil persistenceUtil) {
		this._persistenceUtil = persistenceUtil;
	}
	
	//-------------------------------------------------------------------------
	//
	// IPojoHandler implementation
	//
	//-------------------------------------------------------------------------
	/* (non-Javadoc)
	 * @see org.dotnetguru.lazykiller.web.IPojoHandler#isSteteless()
	 */
	public boolean isStateless()
	{
		return false;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.hibernate4gwt.web.IPojoHandler#remove(javax.servlet.http.HttpServletRequest, java.lang.Object, java.lang.String)
	 */
	public void remove(Object object)
	{
		_map.remove(UniqueNameGenerator.generateUniqueName(_persistenceUtil, object));
	}

	/* (non-Javadoc)
	 * @see net.sf.hibernate4gwt.web.IPojoHandler#restore(javax.servlet.http.HttpServletRequest, java.lang.Object, java.lang.String)
	 */
	public Object restore(Object dto, Class<?> hibernateClass)
	{
		return _map.get(UniqueNameGenerator.generateUniqueName(_persistenceUtil, dto, hibernateClass));
	}

	/* (non-Javadoc)
	 * @see net.sf.hibernate4gwt.web.IPojoHandler#store(javax.servlet.http.HttpServletRequest, java.lang.Object, java.lang.String)
	 */
	public void store(Object object)
	{
		try
		{
			_map.put(UniqueNameGenerator.generateUniqueName(_persistenceUtil, object), object);
		}
		catch (TransientHibernateObjectException ex)
		{
			_log.info("Transient pojo not stored");
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.sf.hibernate4gwt.core.store.IPojoStore#afterRestore()
	 */
	public void afterRestore()
	{
		// nothing to do
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.hibernate4gwt.core.store.IPojoStore#beforeRestore()
	 */
	public void beforeRestore()
	{
		// nothing to do	
	}

}
