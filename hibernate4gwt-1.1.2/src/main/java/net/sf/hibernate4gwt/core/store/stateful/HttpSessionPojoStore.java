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

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.hibernate4gwt.core.IPersistenceUtil;
import net.sf.hibernate4gwt.core.store.IPojoStore;
import net.sf.hibernate4gwt.exception.TransientHibernateObjectException;

/**
 * Pojo Handler for Stateful web application
 * @author bruno.marchesson
 *
 */
public class HttpSessionPojoStore implements IPojoStore
{
	//----
	// Attributes
	//----
	/**
	 * Log channel
	 */
	private Log _log = LogFactory.getLog(HttpSessionPojoStore.class);
	
	/**
	 * The storage thread local
	 */
	private static ThreadLocal<HttpSession> _httpSession = new ThreadLocal<HttpSession>();
	
	/**
	 * The persistance utils
	 */
	private IPersistenceUtil _persistenceUtil;
	
	//----
	// Properties
	//----
	/**
	 * Store the current HTTP session in the thread local
	 */
	public static void setHttpSession(HttpSession session)
	{
		_httpSession.set(session);
	}
	
	/**
	 * @return the associated persistence util class
	 */
	public IPersistenceUtil getPersistenceUtil()
	{
		return _persistenceUtil;
	}

	/**
	 * @param persistenceUtil the persistence Util instance to set
	 */
	public void setPersistenceUtil(IPersistenceUtil persistenceUtil)
	{
		this._persistenceUtil = persistenceUtil;
	}

	//-------------------------------------------------------------------------
	//
	// Pojo Handling
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
	 * @see org.dotnetguru.lazykiller.web.IPojoHandler#store(java.lang.Object, java.lang.String)
	 */
	public void store(Object object)
	{
		try
		{
			String name = UniqueNameGenerator.generateUniqueName(_persistenceUtil, object);
			_log.info("Storing object as " + name);
			
			getSession().setAttribute(name, object);
		}
		catch (TransientHibernateObjectException ex)
		{
			_log.info("Transient pojo not stored");
		}
	}
	
	/**
	 * Remove the argument object
	 */
	public void remove(Object object)
	{
		String name = UniqueNameGenerator.generateUniqueName(_persistenceUtil, object);
		_log.info("Removing object " + name);
		
		getSession().removeAttribute(name);
	}
	

	/* (non-Javadoc)
	 * @see org.dotnetguru.lazykiller.web.IPojoHandler#restore(java.lang.String, java.lang.Object)
	 */
	public Object restore(Object clone, Class<?> hibernateClass)
	{
		String name = UniqueNameGenerator.generateUniqueName(_persistenceUtil, clone, hibernateClass);
		_log.info("Restoring object from " + name);
		
		return getSession().getAttribute(name);
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
	
	//-------------------------------------------------------------------------
	//
	// Internal methods
	//
	//------------------------------------------------------------------------
	/**
	 * @return the HTTP session stored in thread local
	 */
	private HttpSession getSession()
	{
		HttpSession session = (HttpSession) _httpSession.get();
		if (session == null)
		{
			throw new RuntimeException("No HTTP session stored");
		}
		return session;
	}
}
