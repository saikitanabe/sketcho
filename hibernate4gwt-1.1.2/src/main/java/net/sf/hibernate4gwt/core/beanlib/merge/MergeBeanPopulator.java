package net.sf.hibernate4gwt.core.beanlib.merge;

import net.sf.beanlib.provider.BeanPopulator;
import net.sf.beanlib.provider.finder.PrivateReaderMethodFinder;
import net.sf.beanlib.spi.BeanTransformerSpi;
import net.sf.beanlib.spi.CustomBeanTransformerSpi;
import net.sf.hibernate4gwt.core.IPersistenceUtil;
import net.sf.hibernate4gwt.core.beanlib.IClassMapper;
import net.sf.hibernate4gwt.core.beanlib.TimestampCustomTransformer;
import net.sf.hibernate4gwt.core.beanlib.clone.SortedSetterMethodCollector;
import net.sf.hibernate4gwt.core.store.IPojoStore;

/**
 * Bean populator for merge operation
 * @author bruno.marchesson
 *
 */
public class MergeBeanPopulator
{
	/**
	 * Create a new populator for merge operation
	 * @param from the source class
	 * @param to the target class
	 * @param classMapper the associated class mapper
	 * @return the created populator
	 */
	public static BeanPopulator newBeanPopulator(Object from, Object to, 
												 IClassMapper classMapper,
												 IPersistenceUtil persistenceUtil,
												 IPojoStore pojoStore) 
    {
		BeanPopulator replicator = BeanPopulator.newBeanPopulator(from, to);
		
	//	Change bean class replicator
	//
		BeanTransformerSpi transformer = (BeanTransformerSpi) replicator.getTransformer();
		transformer.initBeanReplicatable(MergeClassBeanReplicator.factory);
		((MergeClassBeanReplicator) transformer.getBeanReplicatable()).setClassMapper(classMapper);
		((MergeClassBeanReplicator) transformer.getBeanReplicatable()).setPersistenceUtil(persistenceUtil);
		((MergeClassBeanReplicator) transformer.getBeanReplicatable()).setPojoStore(pojoStore);
		
		transformer.initCollectionReplicatable(MergeCollectionReplicator.factory);
		((MergeCollectionReplicator) transformer.getCollectionReplicatable()).setPersistenceUtil(persistenceUtil);
		
		transformer.initMapReplicatable(MergeMapReplicator.factory);
		((MergeMapReplicator) transformer.getMapReplicatable()).setPersistenceUtil(persistenceUtil);
		
	//	Timestamp handling
	//
		transformer.initCustomTransformer(new CustomBeanTransformerSpi.Factory()
		{
			public CustomBeanTransformerSpi newCustomBeanTransformer(final BeanTransformerSpi beanTransformer)
			{
				return new TimestampCustomTransformer(beanTransformer);
			}
		});
		
	//	Lazy properties handling
	//
		MergeBeanPopulatable hibernatePopulatable = new MergeBeanPopulatable(persistenceUtil, pojoStore);
		replicator.initDetailedBeanPopulatable(hibernatePopulatable);
		replicator.initDebug(true);
		
	//	Merge based on protected and private setters
	//
		replicator.initSetterMethodCollector(SortedSetterMethodCollector.inst);
		replicator.initReaderMethodFinder(PrivateReaderMethodFinder.inst);
		
		return replicator;
    }

}
