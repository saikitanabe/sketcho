package net.sevenscales.serverAPI.remote;

import java.util.List;

import net.sevenscales.domain.api.ITicket;

public interface ITicketRemote {  
  public ITicket save(ITicket ticket);
  public ITicket update(ITicket sketch);
  public ITicket open(Long id);
  public List<ITicket> findAll(Long projectId);
  public List<ITicket> findAll(Long projectId, Integer max, String where, String orderBy);
  public void remove(ITicket ticket);
  public void removeAll(List<ITicket> tickets);
}
