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

import net.sf.beanlib.hibernate.UnEnhancer;
import net.sf.beanlib.hibernate3.Hibernate3JavaBeanReplicator;
import net.sf.beanlib.spi.BeanTransformerSpi;
import net.sf.beanlib.spi.replicator.BeanReplicatorSpi;
import net.sf.hibernate4gwt.core.IPersistenceUtil;
import net.sf.hibernate4gwt.core.beanlib.BeanlibThreadLocal;
import net.sf.hibernate4gwt.core.beanlib.IClassMapper;
import net.sf.hibernate4gwt.core.store.IPojoStore;
import net.sf.hibernate4gwt.exception.TransientHibernateObjectException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Bean replicator with different from and to classes for merge operation
 * @author bruno.marchesson
 *
 */
public class MergeClassBeanReplicator extends Hibernate3JavaBeanReplicator
{
	//---
	// Attributes
	//---

	/**
	 * Log channel
	 */
	private static Log _log = LogFactory.getLog(MergeClassBeanReplicator.class);
	
	/**
	 * The class mapper (can be null)
	 */
	private IClassMapper _classMapper;
	
	/**
	 * The persistent util class
	 */
	private IPersistenceUtil _persistenceUtil;
	
	/**
	 * The current pojo store
	 */
	private IPojoStore _pojoStore;
	
	//----
	// Factory
	//----
	public static final Factory factory = new Factory();
	
    /**
     * Factory for {@link MergeClassBeanReplicator}
     * 
     * @author bruno.marchesson
     */
    public static class Factory implements BeanReplicatorSpi.Factory {
        private Factory() {}
        
        public MergeClassBeanReplicator newBeanReplicatable(BeanTransformerSpi beanTransformer) {
            return new MergeClassBeanReplicator(beanTransformer);
        }
    }
    
    public static MergeClassBeanReplicator newBeanReplicatable(BeanTransformerSpi beanTransformer) {
        return factory.newBeanReplicatable(beanTransformer);
    }
    
    //----
	// Constructor
	//----
    protected MergeClassBeanReplicator(BeanTransformerSpi beanTransformer) 
    {
        super(beanTransformer);
    }
    
    //----
    // Properties
    //----
    /**
	 * @return the Class Mapper
	 */
	public IClassMapper getClassMapper() {
		return _classMapper;
	}

	/**
	 * @param mapper the classMapper to set
	 */
	public void setClassMapper(IClassMapper mapper) {
		_classMapper = mapper;
	}
	
	/**
	 * @return the _persistenceUtil
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

	/**
	 * @return the _pojoStore
	 */
	public IPojoStore getPojoStore() {
		return _pojoStore;
	}

	/**
	 * @param store the _pojoStore to set
	 */
	public void setPojoStore(IPojoStore store) {
		_pojoStore = store;
	}

	//----
    // Override
    //----
	@Override
	protected Object replicate(Object from)
	{
	//	Reset bean local
	//
		BeanlibThreadLocal.setDestinationBean(null);
		
		return super.replicate(from);
	}
	
    @Override
    protected <T extends Object> T createToInstance(Object from, java.lang.Class<T> toClass) 
    		throws InstantiationException ,IllegalAccessException ,SecurityException ,NoSuchMethodException
    {
    	if (BeanlibThreadLocal.getDestinationBean() != null)
    	{ 
    		Object instance = BeanlibThreadLocal.getDestinationBean();
    		
    		if (_log.isDebugEnabled())
    		{
    			_log.debug("Using destination bean of class " + instance.getClass());
    		}
    		BeanlibThreadLocal.setDestinationBean(null);
    		return (T)instance;
    	}
    	       
    //	Clone mapper indirection
    //
        if (_classMapper != null)
        {
        	Class sourceClass = _classMapper.getSourceClass(UnEnhancer.unenhanceClass(from.getClass())); 
        	if (sourceClass != null)
        	{
        		if (_log.isDebugEnabled())
        		{
        			_log.debug("Creating mapped class " + sourceClass);
        		}
        		toClass = sourceClass;
        	}
        	else
        	{
        		if (_log.isDebugEnabled())
        		{
        			_log.debug("Creating merged class " + toClass);
        		}
        	}
        }
        
    //	Get real target class
    //
        if (toClass.isInterface())
    	{
    	//	Keep the from class
    	//
    		toClass = (Class<T>) from.getClass();
    	}
        
        T result = null;
    	if (_persistenceUtil.isPersistentClass(toClass))
    	{
    	//	Wrapper case : the class is persistent but not the wrapper
    	//	so we have to initialize the proxy by ourselves
    	//
    		try
    		{
    			result = (T) _pojoStore.restore(from, toClass);
    		}
    		catch(TransientHibernateObjectException ex)
    		{
    			if (_log.isDebugEnabled())
    			{
    				_log.debug("Transient clone " + from + "  : no proxy needed");
    			}
    		}
    	}
    	
    	if (result != null)
    	{
    		return result;
    	}
    	else
    	{
    		result = super.createToInstance(from, toClass);
    		
    	//	Dynamic proxy workaround : for inheritance purpose
    	//	beanlib returns an instance of the proxy class
    	//	since it inherits from the source class...
    	//
    		if ((_classMapper != null) &&
    			(_classMapper.getSourceClass(result.getClass()) != null))
    		{
    			return newInstanceAsPrivileged(toClass);
    		}
    		return result;
    	}
    }
}
