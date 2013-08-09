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

import net.sf.beanlib.hibernate.HibernateBeanReplicator;
import net.sf.beanlib.provider.BeanPopulator;
import net.sf.hibernate4gwt.core.beanlib.BeanlibThreadLocal;
import net.sf.hibernate4gwt.core.beanlib.IClassMapper;
import net.sf.hibernate4gwt.core.beanlib.clone.CloneBeanReplicator;
import net.sf.hibernate4gwt.core.beanlib.merge.MergeBeanPopulator;
import net.sf.hibernate4gwt.core.store.IPojoStore;
import net.sf.hibernate4gwt.rebind.ProxyManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class replaces all "lazy but not loaded" Hibernate association with null
 * to allow the argument POJO to be used in other libraries without additional loading
 * of 'LazyInitializationException'.  
 * The lazy properties are stored as string. They can be reused for "reattaching" the clone
 * pojo to a fresh Hibernate object.
 * @author bruno.marchesson
 */
public class LazyKiller
{
	//----
	// Attributes
	//----
	/**
	 * Log channel
	 */
	private static Log _log = LogFactory.getLog(LazyKiller.class);
	
	/**
	 * The class mapper
	 */
	private IClassMapper _classMapper;
	
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
	 * @param mapper the class Mapper to set
	 */
	public void setClassMapper(IClassMapper mapper)
	{
		_classMapper = mapper;
	}
	
	/**
	 * @return the associated pojo Store
	 */
	public IPojoStore getPojoStore()
	{
		return _pojoStore;
	}

	/**
	 * @param store the POJO Store to set
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
	 * Empty constructor
	 */
	public LazyKiller()
	{
		this(null, null, null);
	}
	
	/**
	 * Base constructor
	 * @param classMapper the class mapping service
	 * @param persistenceUtil persistence util implementation
	 * @param 
	 */
	public LazyKiller(IClassMapper classMapper, 
					  IPersistenceUtil persistenceUtil,
					  IPojoStore pojoStore)
	{
		setClassMapper(classMapper);
		setPersistenceUtil(persistenceUtil);
		setPojoStore(pojoStore);
	}

	//------------------------------------------------------------------------
	//
	// Public interface
	//
	//------------------------------------------------------------------------
	/**
	 * Hibernate detachment
	 * @param hibernatePojo the input hibernate pojo
	 * @return a pure Java clone
	 */
	public Object detach(Object hibernatePojo)
	{
	//	Precondition checking
	//
		if (hibernatePojo == null)
		{
			return null;
		}
			
		_log.info("Detaching " + hibernatePojo.toString());
		
	// 	Search for Proxy
	//
		Class proxyClass = ProxyManager.getInstance().getProxyClass(hibernatePojo.getClass());
		if (proxyClass != null)
		{
			// Generate proxy
			return clone(hibernatePojo, proxyClass);
		}
		else
		{
			// No proxy mode
			return clone(hibernatePojo, hibernatePojo.getClass());
		}
	}
	
	/**
	 * Hibernate detachment
	 * @param hibernatePojo the input hibernate pojo
	 * @return a pure Java clone
	 */
	public Object detach(Object hibernatePojo, Class cloneClass)
	{
	//	Precondition checking
	//
		if (hibernatePojo == null)
		{
			return null;
		}
			
		_log.info("Detaching " + hibernatePojo.toString());
		
	//	 Clone with beanLib
	//
		return clone(hibernatePojo, cloneClass);
	}
	
	/**
	 * Hibernate attachment
	 * @param hibernatePojo the stored or fresh Hibernate POJO
	 * @param clonePojo the cloned pojo
	 */
	public void attach(Object hibernatePojo, Object clonePojo)
	{
    //	Precondition checking
	//
		if ((hibernatePojo == null) ||
			(clonePojo == null))
		{
			return;
		}
		
		_log.info("Attaching " + clonePojo.toString());
		
	//	Populate with BeanLib
	//
		populate(hibernatePojo, clonePojo);
	}
	
	//-------------------------------------------------------------------------
	//
	// Internal methods
	//
	//-------------------------------------------------------------------------
	/**
	 * Clone the abstract POJO with BeanLib
	 * Every time a lazy property is detected, it is replaced with null.
	 * It is also marked as "lazy" for ILazyPojo sub-classes
	 * @param pojo
	 * @return
	 */
	protected Object clone(Object hibernatePojo, Class cloneClass)
	{
		HibernateBeanReplicator replicator = new CloneBeanReplicator(_classMapper, 
																	 _persistenceUtil,
																	 _pojoStore);
		
		return replicator.copy(hibernatePojo, cloneClass);
	}
	
	/**
	 * Populate the hibernatePojo (a fresh new one or the one used to clone)
	 * with the clone detached object.
	 * This Hibernate POJO holds the lazy properties information
	 */
	public void populate(Object hibernatePojo, Object clonePojo)
	{
	//	Populate hibernate POJO from the cloned pojo
	//
		BeanPopulator replicator = MergeBeanPopulator.newBeanPopulator(clonePojo, hibernatePojo, 
																	   _classMapper, 
																	   _persistenceUtil,
																	   _pojoStore);

	//	Reset BeanLib thread local
	//
		BeanlibThreadLocal.setDestinationBean(null);

		replicator.populate();
	}
}
