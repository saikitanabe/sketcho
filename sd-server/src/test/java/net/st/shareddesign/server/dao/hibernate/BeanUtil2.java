package net.st.shareddesign.server.dao.hibernate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sf.beanlib.hibernate.HibernateBeanReplicator;
import net.sf.beanlib.hibernate3.Hibernate3BeanTransformer;
import net.sf.beanlib.provider.BeanPopulator;
import net.sf.beanlib.provider.BeanTransformer;
import net.sf.beanlib.provider.replicator.BeanReplicator;
import net.sf.beanlib.provider.replicator.CollectionReplicator;
import net.sf.beanlib.spi.BeanTransformerSpi;
import net.sf.beanlib.spi.DetailedBeanPopulatable;
import net.sf.beanlib.spi.replicator.CollectionReplicatorSpi;

public class BeanUtil2 {
  private static ThreadLocal<Object> destinationBean = new ThreadLocal<Object>();

  private static class MergeCollectionReplicator extends CollectionReplicator {
    protected MergeCollectionReplicator(BeanTransformerSpi beanTransformer) {
       super(beanTransformer);
     }

    @Override
    protected Object replicate(Object from) {
      destinationBean.set(null);
      Class toClass = targetClass(from);
      return super.replicate(from, toClass);
    }
   @Override
    protected Collection<Object> createToCollection(Collection<?> from)
        throws InstantiationException, IllegalAccessException,
        SecurityException, NoSuchMethodException, InvocationTargetException {
     if (destinationBean.get() != null) {
       Collection<Object> c = (Collection<Object>)destinationBean.get();
       destinationBean.set(null);
       if (c.size() > 0) {
//         c.removeAll(c);
         c.clear();
       }
//       c.clear();
       return c;
     }
      return super.createToCollection(from);
    } 
   }
  
  private static class MergeDetailedPopulatable implements DetailedBeanPopulatable {
    public boolean shouldPopulate(String propertyName, Object fromBean,
        Method readerMethod, Object toBean, Method setterMethod) {
//      try {
//        String property = readerMethod.getName();
//        Method rm = getDeclaredMethod(toBean, property);
//        Object persisted = rm.invoke(toBean, (Object[]) null);
//        if (persisted instanceof Collection) {
//          destinationBean.set(persisted);
//        }
//      } catch (Exception e) {
//        throw new RuntimeException(e);
//      }
      
      if (propertyName.equals("id")) {
        return false;
      }
      return true;
    }

    private Method getDeclaredMethod(Object toBean, String property) {
      Class clazz = toBean.getClass();
      
      // search from super class as well
      while (clazz != null) {
        Method method;
        try {
          method = clazz.getDeclaredMethod(property, (Class[]) null);
          if (method != null) {
            return method;
          }
        } catch (SecurityException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (NoSuchMethodException e) {
          clazz = clazz.getSuperclass();
          if (clazz == null) {
            throw new RuntimeException(e);
          }
        }
        
      }
      return null;
    }
    
  }

  ////////////

  private static CollectionReplicatorSpi.Factory factory = new CollectionReplicatorSpi.Factory() {
    public CollectionReplicatorSpi newCollectionReplicatable(
        BeanTransformerSpi beanTransformer) {
      return new CollectionReplicatorSpi() {
        public <T,V> V replicateCollection(Collection<T> fromCollection,
            Class<V> toClass) {
          return (V) BeanUtil2.clone(fromCollection);
        }
      };
    }
  };
  
  private static BeanReplicator replicator;
  
  static {
    BeanTransformer bt = BeanTransformer.newBeanTransformer();
    bt.initCollectionReplicatable(factory);
    bt.initDetailedBeanPopulatable(new MergeDetailedPopulatable());
    bt.initDebug(true);
    replicator = new BeanReplicator(bt);
  }
  
  public static <T> Object clone(T persisted) {
    if (persisted instanceof List) {
      List newList = new ArrayList(((List) persisted).size());
      for (T p : (List<T>) persisted) {
        Object cloned = clone(p);
        newList.add(cloned);
      }
      return newList;
    } else if (persisted instanceof Set) {
      if (persisted instanceof SortedSet) {
        Set newSet = new TreeSet();
        for (T p : (SortedSet<T>) persisted) {
          Object cloned = clone(p);
          newSet.add(cloned);
        }
        return newSet;
      }
    }

    return clonePojo(persisted);
  }
  
  public static <T> Object cloneHibernate(T persisted) {
    Hibernate3BeanTransformer trans = Hibernate3BeanTransformer.newBeanTransformer();
    trans.initDebug(true);
    trans.initDetailedBeanPopulatable(new MergeDetailedPopulatable());
    HibernateBeanReplicator replicator = new HibernateBeanReplicator(trans);
    return replicator.copy(persisted);
  }
  
  public static <T,V> V clonePojo(T persisted) {
    Class<V> targetClass = targetClass(persisted);
    V to = replicator.replicateBean(persisted, targetClass);
    return to;
  }

  private static Class targetClass(Object dto) {
    Class result = dto.getClass();
    if (dto.getClass().getSimpleName().endsWith("DTO")) {
      String simple = dto.getClass().getSimpleName();
      String persistedName = dto.getClass().getSimpleName().substring(0, simple.length()-3);
      String persistedPath = "net.st.shareddesign.server.domain.";
      try {
        result = Class.forName(persistedPath+persistedName);
      } catch (Exception e) {
        throw new RuntimeException("DTO conversion failed");
      }
    }
    return result; 
  }

  private static Class targetDTOClass(Object persisted) {
//    String dtoName = persisted.getClass().getSimpleName()+"DTO";
//    String dtoPath = "net.sevenscales.issuemanager.client.domain.";
//    Class result = null;
//    try {
//      result = Class.forName(dtoPath+dtoName);
//    } catch (Exception e) {
//      throw new RuntimeException("DTO conversion failed");
//    }
    return persisted.getClass(); 
  }

  public static <T,D> D merge(T from, D to) {
    BeanPopulator replicator = BeanPopulator.newBeanPopulator(from, to);
    BeanTransformerSpi transformer = (BeanTransformerSpi) replicator.getTransformer();
    transformer.initCollectionReplicatable(new CollectionReplicatorSpi.Factory() {
      public CollectionReplicatorSpi newCollectionReplicatable(BeanTransformerSpi beanTransformer) {
        return new MergeCollectionReplicator(beanTransformer);
      }
    });
//    transformer.initReaderMethodFinder(readerMethodFinder)
    replicator.initDetailedBeanPopulatable(new MergeDetailedPopulatable());

    replicator.populate();
    return to;
  }

}
