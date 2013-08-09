package net.sf.hibernate4gwt.core.beanlib.clone;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;

import net.sf.beanlib.provider.collector.PublicSetterMethodCollector;
import net.sf.beanlib.spi.BeanMethodCollector;
import net.sf.hibernate4gwt.pojo.java5.SortedAsLastMethod;


public class SortedSetterMethodCollector implements BeanMethodCollector {  
  private BeanMethodCollector methodCollector;
  public static final SortedSetterMethodCollector inst = new SortedSetterMethodCollector();
  
  private SortedSetterMethodCollector() {
    methodCollector = PublicSetterMethodCollector.inst;
  }
  
//  @Override
  public Method[] collect(Object bean) {
    Method[] result = methodCollector.collect(bean);
    Arrays.sort(result, new Comparator<Method>() {
//      @Override
      public int compare(Method arg0, Method arg1) {
        Annotation a0 = arg0.getAnnotation(SortedAsLastMethod.class);
        Annotation a1 = arg1.getAnnotation(SortedAsLastMethod.class);
        if (a0 != null && a1 != null) {
          // normal sorting
        } else if (a0 != null) {
          // arg0 is greater
          return 1;
        } else if (a1 != null) {
          // arg0 is less
          return -1;
        }
        return arg0.getName().compareTo(arg1.getName());
      }
    });
    return result;
  }
  
//  @Override
  public String getMethodPrefix() {
    return methodCollector.getMethodPrefix();
  }

}
