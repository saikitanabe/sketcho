package net.sevenscales.server.service.impl;


import java.util.List;

import net.sevenscales.domain.api.ITicket;
import net.sevenscales.server.dao.ITicketDAO;
import net.sevenscales.server.service.ITicketService;

import org.acegisecurity.context.SecurityContextHolder;

public class TicketService implements ITicketService {
  private ITicketDAO ticketDAO;

  public void setTicketDAO(ITicketDAO userDAO) {
    this.ticketDAO = userDAO;
  }

  public ITicketDAO getTicketDAO() {
    return ticketDAO;
  }

  public ITicket save(ITicket ticket, String sessionId) {
    String user = SecurityContextHolder.getContext().getAuthentication().getName();
    ticket.setModifier(user);
    return save(ticket);
  }

//  @Override
  public ITicket save(ITicket ticket) {
    return ticketDAO.save(ticket);
  }

//  @Override
  public ITicket update(ITicket sketch) {
    String user = SecurityContextHolder.getContext().getAuthentication().getName();
    sketch.setModifier(user);
    return ticketDAO.update(sketch);
  }
  
//  @Override
  public ITicket open(Long id) {
    return ticketDAO.open(id);
  }
  
//  @Override
  public List<ITicket> findAll(Long projectId) {
    return ticketDAO.findAll(projectId);
  }
  
//  @Override
  public List<ITicket> findAll(Long projectId, Integer max, String where, String orderBy) {
    return ticketDAO.findAll(projectId, max, where, orderBy);
  }

//  @Override
  public void remove(ITicket ticket) {
    ticketDAO.remove(ticket);
  }

//  @Override
  public void removeAll(List<ITicket> tickets) {
    ticketDAO.removeAll(tickets);
  }
}
