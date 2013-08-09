/**
 * 
 */
package net.sf.hibernate4gwt.core.beanlib.merge;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import net.sf.beanlib.hibernate3.Hibernate3CollectionReplicator;
import net.sf.beanlib.spi.BeanTransformerSpi;
import net.sf.beanlib.spi.replicator.CollectionReplicatorSpi;
import net.sf.hibernate4gwt.core.IPersistenceUtil;
import net.sf.hibernate4gwt.core.beanlib.BeanlibThreadLocal;

/**
 * Encapsulation of the collection replicator
 * @author bruno.marchesson
 *
 */
public class MergeCollectionReplicator extends Hibernate3CollectionReplicator {
	
	//----
	// Factory
	//----
	public static final Factory factory = new Factory();
	
    /**
     * Factory for {@link MergeClassBeanReplicator}
     * 
     * @author bruno.marchesson
     */
	private static class Factory implements CollectionReplicatorSpi.Factory {
        private Factory() {}
        
        public Hibernate3CollectionReplicator newCollectionReplicatable(BeanTransformerSpi beanTransformer) {
            return new MergeCollectionReplicator(beanTransformer);
        }
    }
    
    public static Hibernate3CollectionReplicator newCollectionReplicatable(BeanTransformerSpi beanTransformer) {
        return factory.newCollectionReplicatable(beanTransformer);
    }
    
    //----
    // Attributes
    //----
    /**
     * The associated persistence util
     */
    private IPersistenceUtil _persistenceUtil;
    
    //----
    // Properties
    //----
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
	}

    
    //----
	// Constructor
	//----
	/**
	 * Constructor
	 * @param beanTransformer
	 */
	protected MergeCollectionReplicator(BeanTransformerSpi beanTransformer) {
		super(beanTransformer);
	}
	
	//----
	// Overrides
	//----
	@Override
	protected Object replicate(Object from)
	{
	//	Reset bean local
	//
		BeanlibThreadLocal.setDestinationBean(null);
		
		return super.replicate(from);
	}
	
	/**
	 * Collection creation override
	 */
	@Override
	protected Collection<Object> createToCollection(Collection<?> from)
			throws InstantiationException, IllegalAccessException,
			SecurityException, NoSuchMethodException, InvocationTargetException
	{
	//	Get Hibernate collection if needed
	//
		if (BeanlibThreadLocal.getDestinationBean() != null)
		{
			Collection<Object> collection = (Collection<Object>)BeanlibThreadLocal.getDestinationBean();
			BeanlibThreadLocal.setDestinationBean(null);
			
			// Clean the Hibernate collection to prevent duplicate entries
			if (_persistenceUtil.isProxy(collection))
			{
				_persistenceUtil.initializeProxy(collection);
			}
			collection.clear();
//			while(collection.isEmpty() == false)
//			{
//				collection.remove(collection.iterator().next());
//			}
			
    		return collection;
		}
		
	//	Default behavior
	//
		return super.createToCollection(from);
	}
}
