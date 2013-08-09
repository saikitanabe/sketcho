package net.sevenscales.server.gwt;


import java.util.List;

import net.sevenscales.domain.api.ITicket;
import net.sevenscales.server.Configuration;
import net.sevenscales.server.GwtController;
import net.sevenscales.server.service.ITicketService;
import net.sevenscales.serverAPI.remote.TicketRemote;
import net.sf.hibernate4gwt.core.HibernateBeanManager;

public class TicketRemoteImpl extends GwtController implements TicketRemote {
  private ITicketService ticketService;
  private HibernateBeanManager beanManager;

  public TicketRemoteImpl() {
  }

  protected Configuration getConfiguration() {
    return new Configuration("applicationContext.xml");
  }
  
  public ITicketService getTicketService() {
    return ticketService;
  }
  
  public void setTicketService(ITicketService ticketService) {
    this.ticketService = ticketService;
  }
  
  public void setBeanManager(HibernateBeanManager beanManager) {
	this.beanManager = beanManager;
  }

//  @Override
  public ITicket save(ITicket ticket) {
    ticket = (ITicket) beanManager.merge(ticket);
    ITicket result = ticketService.save(ticket);
    return (ITicket) beanManager.clone(result);
  }
  
//  @Override
  public ITicket update(ITicket sketch) {
    ITicket orig = ticketService.open(sketch.getId());
    sketch.setCreatedTime(orig.getCreatedTime()); 
    ITicket result = ticketService.update( (ITicket) beanManager.merge(sketch) );
    return (ITicket) beanManager.clone(result);
  }

//  @Override
  public ITicket open(Long id) {
    ITicket result = (ITicket) beanManager.clone(ticketService.open(id));
//    ITextContent desc = (ITextContent) getBeanManager().clonePojoEnhanced(result.getDescription());
//    result.setDescription(desc);

    return result;
  }

//  @Override
  public List<ITicket> findAll(Long projectId) {
    List<ITicket> result = ticketService.findAll(projectId);
    result = (List<ITicket>) beanManager.clone(result);
    return result;
  }
  
//  @Override
  public List<ITicket> findAll(Long projectId, Integer max, String where, String orderBy) {
    return (List<ITicket>)beanManager.clone(ticketService.findAll(projectId, max, where, orderBy));
  }

//  @Override
  public void remove(ITicket ticket) {
    ticket = (ITicket) beanManager.merge(ticket);
    ticketService.remove(ticket);
  }
  
//  @Override
  public void removeAll(List<ITicket> tickets) {
    tickets = (List<ITicket>) beanManager.merge(tickets);
    ticketService.removeAll(tickets);
  }

}
