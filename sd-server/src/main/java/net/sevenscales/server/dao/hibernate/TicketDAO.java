package net.sevenscales.server.dao.hibernate;


import java.util.List;

import net.sevenscales.domain.api.ITicket;
import net.sevenscales.server.dao.ITicketDAO;
import net.sevenscales.server.domain.DiagramContent;
import net.sevenscales.server.domain.Project;
import net.sevenscales.server.domain.Ticket;

import org.hibernate.Hibernate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly=true)
public class TicketDAO extends HibernateDaoSupport implements ITicketDAO {
  
//  @Override
  @Transactional(propagation=Propagation.REQUIRED)
  public ITicket save(ITicket ticket) {
    ticket.setModifiedTime(System.currentTimeMillis());
    ticket.setCreatedTime(System.currentTimeMillis());
    getHibernateTemplate().save(ticket);
    return ticket;
  }
  
//  @Override
  @Transactional(propagation=Propagation.REQUIRED)
  public ITicket update(ITicket sketch) {
    sketch.setModifiedTime(System.currentTimeMillis());
    getHibernateTemplate().update(sketch);
    getHibernateTemplate().update(sketch.getDescription());
    getHibernateTemplate().update(sketch.getDiagram());
    return sketch;
  }  

//  @Override
  public ITicket open(Long id) {
    Ticket result = (Ticket) getHibernateTemplate().get(Ticket.class, id);
    // load lazy initialized values
//    getHibernateTemplate().initialize(result);
    Hibernate.initialize(result);
    Hibernate.initialize(result.getDescription());
//    Hibernate.initialize(result.getDiagram());
    DiagramContent dc = (DiagramContent) result.getDiagram();
    Hibernate.initialize(dc.getDiagramItems());
    
//    for (PageOrderedContent c : result.getDiagram()) {
//      if (c.getContent() instanceof DiagramContent) {
//        DiagramContent dc = (DiagramContent) c.getContent();
//        Hibernate.initialize(dc.getDiagramItems());
//      }
//    }
    
    return result;
  }  

//  @Override
  @Transactional(propagation=Propagation.SUPPORTS)
  public List<ITicket> findAll(Long projectId) {
    Project p = (Project) getHibernateTemplate().get(Project.class, projectId);
    
    String[] keys = {"project"};
    Project[] values = {p};
    
    List<ITicket> result = getHibernateTemplate().findByNamedParam
      ("from Ticket t where t.project=:project order by t.id asc", keys, values);
    
    return result;
  }
  
//  @Override
  public List<ITicket> findAll(Long projectId, Integer max, String where, String orderBy) {
    Project p = (Project) getHibernateTemplate().get(Project.class, projectId);
    
    String[] keys = {"project"};
    Object[] values = {p};
    
    if (orderBy != null && orderBy.length() > 0) {
      orderBy = "order by t." + orderBy;
    }
    
    if (where != null && where.length() > 0) {
      where = "and t." + where;
    }
    
    if (max != null) {
      getHibernateTemplate().setMaxResults(max);
    }

    List<ITicket> result = getHibernateTemplate().findByNamedParam
      (String.format("from Ticket t where t.project=:project %s %s", where, orderBy), keys, values);
    getHibernateTemplate().setMaxResults(0);
    
    // TODO: there should be requested a list of fields and then some content
    // wouldn't needt be initialized => performance
    for (ITicket t : result) {
      Hibernate.initialize(t.getDescription());
    }
    
    return result;
  }

//  @Override
  @Transactional(propagation=Propagation.REQUIRED)
  public void remove(ITicket ticket) {
    getHibernateTemplate().delete(ticket);
  }
  
//  @Override
  @Transactional(propagation=Propagation.REQUIRED)
  public void removeAll(List<ITicket> tickets) {
    getHibernateTemplate().deleteAll(tickets);
  }

}
