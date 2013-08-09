/**
 * 
 */
package net.sf.hibernate4gwt.core.beanlib.merge;

import static net.sf.beanlib.utils.ClassUtils.isJavaPackage;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import net.sf.beanlib.BeanlibException;
import net.sf.beanlib.hibernate3.Hibernate3MapReplicator;
import net.sf.beanlib.spi.BeanTransformerSpi;
import net.sf.beanlib.spi.replicator.MapReplicatorSpi;
import net.sf.hibernate4gwt.core.IPersistenceUtil;
import net.sf.hibernate4gwt.core.beanlib.BeanlibThreadLocal;

/**
 * Encapsulation of the collection replicator
 * @author bruno.marchesson
 *
 */
public class MergeMapReplicator extends Hibernate3MapReplicator {
	
	//----
	// Factory
	//----
	public static final Factory factory = new Factory();
	
    /**
     * Factory for {@link MergeClassBeanReplicator}
     * 
     * @author bruno.marchesson
     */
	private static class Factory implements MapReplicatorSpi.Factory {
        private Factory() {}
        
        public Hibernate3MapReplicator newMapReplicatable(BeanTransformerSpi beanTransformer) {
            return new MergeMapReplicator(beanTransformer);
        }
    }
    
    public static Hibernate3MapReplicator newMapReplicatable(BeanTransformerSpi beanTransformer) {
        return factory.newMapReplicatable(beanTransformer);
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
	protected MergeMapReplicator(BeanTransformerSpi beanTransformer) {
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
	 * Map replication override
	 * This copy/paste of beanLib code is needed because the createToMap
	 * method is private :(
	 */
	@Override
	public <K,V,T> T replicateMap(Map<K,V> from, Class<T> toClass)
    {
//		if (!Hibernate.isInitialized(from))
//            Hibernate.initialize(from);
		
        if (!toClass.isAssignableFrom(from.getClass()))
            return null;
        Map<Object, Object> toMap;
        try {
            toMap = createToMapIfNeeded(from);
        } catch (SecurityException e) {
            throw new BeanlibException(e);
        } catch (InstantiationException e) {
            throw new BeanlibException(e);
        } catch (IllegalAccessException e) {
            throw new BeanlibException(e);
        } catch (NoSuchMethodException e) {
            throw new BeanlibException(e);
        }
        putTargetCloned(from, toMap);
        Map fromMap = from;
        // recursively populate member objects.
        for (Iterator itr=fromMap.entrySet().iterator(); itr.hasNext(); ) {
            Map.Entry fromEntry = (Map.Entry)itr.next();
            Object fromKey = fromEntry.getKey();
            Object fromValue = fromEntry.getValue();
            Object toKey = replicate(fromKey);
            Object toValue = replicate(fromValue);
            toMap.put(toKey, toValue);
        }
        return toClass.cast(toMap);
    }
    
	/**
	 * Map creation override
	 */
    private Map<Object,Object> createToMapIfNeeded(Map<?,?> from) 
        throws InstantiationException, IllegalAccessException, SecurityException, NoSuchMethodException 
    {
    //   Get Hibernate map if needed
	//
		if (BeanlibThreadLocal.getDestinationBean() != null)
		{
			Map<Object,Object> map = (Map<Object,Object>)BeanlibThreadLocal.getDestinationBean();
			BeanlibThreadLocal.setDestinationBean(null);
			
			// Clean the Hibernate collection to prevent duplicate entries
			if (_persistenceUtil.isProxy(map))
			{
				_persistenceUtil.initializeProxy(map);
			}
			map.clear();
			
    		return map;
		}
		
	//	Default behavior
	//
		return createToMap(from);
    }

    //----
    // Other copy/pasted private methods
    //----
    private Map<Object,Object> createToMap(Map<?,?> from) 
    throws InstantiationException, IllegalAccessException, SecurityException, NoSuchMethodException 
	{
	    Class fromClass = from.getClass();
	    
	    if (isJavaPackage(fromClass)) {
	        if (from instanceof SortedMap) {
	            SortedMap fromSortedMap = (SortedMap<?,?>)from;
	            Comparator<Object> toComparator = createToComparator(fromSortedMap);
	            
	            if (toComparator != null)
	                return this.createToSortedMapWithComparator(fromSortedMap, toComparator);
	        }
	        return createToInstanceAsMap(from);
	    }
	    if (from instanceof SortedMap) {
	        SortedMap fromSortedMap = (SortedMap<?,?>)from;
	        Comparator<Object> toComparator = createToComparator(fromSortedMap);
	        return new TreeMap<Object,Object>(toComparator);
	    }
	    return new HashMap<Object,Object>();
	}
	
	@SuppressWarnings("unchecked")
	private Map<Object, Object> createToInstanceAsMap(Map<?, ?> from) 
	    throws InstantiationException, IllegalAccessException, NoSuchMethodException
	{
	    return (Map<Object,Object>)createToInstance(from);
	}
	
	/** Returns a replicated comparator of the given sorted map, or null if there is no comparator. */
	@SuppressWarnings("unchecked")
	private Comparator<Object> createToComparator(SortedMap fromSortedMap)
	{
	    Comparator fromComparator = fromSortedMap.comparator();
	    Comparator toComparator = fromComparator == null 
	                            ? null 
	                            : replicateByBeanReplicatable(fromComparator, Comparator.class)
	                            ;
	    return toComparator;
	}
	
	@SuppressWarnings("unchecked")
	private SortedMap<Object,Object> createToSortedMapWithComparator(SortedMap from, Comparator comparator) 
	    throws NoSuchMethodException, SecurityException
	{
	    return (SortedMap<Object,Object>)createToInstanceWithComparator(from, comparator);
	}
}
