package net.sevenscales.server.dao.hibernate;


import java.util.List;

import net.sevenscales.domain.api.IContent;
import net.sevenscales.server.dao.IContentDAO;
import net.sevenscales.server.domain.Content;
import net.sevenscales.server.domain.DiagramContent;

import org.hibernate.Hibernate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly=true)
public class ContentDAO extends HibernateDaoSupport implements IContentDAO {
  
//  @Override
  @Transactional(propagation=Propagation.REQUIRED)
  public IContent save(IContent content) {
    getHibernateTemplate().save(content);
    return content;
  }
  
//  @Override
  @Transactional(propagation=Propagation.REQUIRED)
  public IContent update(IContent content) {
    getHibernateTemplate().update(content);
    return content;
  }

//  @Override
  @Transactional(propagation=Propagation.SUPPORTS)
  public IContent open(Long id) {
    Content result = (Content) getHibernateTemplate().get(Content.class, id);
    // load lazy initialized values
    if (result instanceof DiagramContent) {
      Hibernate.initialize(((DiagramContent)result).getDiagramItems());
    }

    return result;
  }  

//  @Override
  @Transactional(propagation=Propagation.SUPPORTS)
  public List<IContent> findAll() {
    return getHibernateTemplate().find("from Content c order by c.id asc");
  }

//  @Override
  @Transactional(propagation=Propagation.REQUIRED)
  public void remove(IContent content) {
    getHibernateTemplate().delete(content);
  }
  
//  @Override
  @Transactional(propagation=Propagation.REQUIRED)
  public void removeAll(List<IContent> contents) {
    getHibernateTemplate().deleteAll(contents);
  }
  
  public String downloadImage(String svg, Long contentId) {
    return null;
  }

}
