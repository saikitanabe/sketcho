package net.sevenscales.server;

import java.io.Serializable;

import net.sf.hibernate4gwt.core.hibernate.HibernateUtil;
import net.sf.hibernate4gwt.core.store.stateless.StatelessPojoStore;

import org.springframework.orm.hibernate3.HibernateTemplate;

public class OpenViewStatelessPojoStore extends StatelessPojoStore {
  
  @Override
  public Object restore(Object clone, Class<?> hibernateClass) {
    HibernateTemplate t = new HibernateTemplate();
    t.setSessionFactory(HibernateUtil.getInstance().getSessionFactory());
    
    Serializable id = HibernateUtil.getInstance().getId(clone, hibernateClass); 

    return HibernateUtil
      .getInstance()
      .getSessionFactory()
      .getCurrentSession()
      .get(hibernateClass, id);
  }
  @Override
  public void beforeRestore() {
  }
  @Override
  public void afterRestore() {
  }
}
