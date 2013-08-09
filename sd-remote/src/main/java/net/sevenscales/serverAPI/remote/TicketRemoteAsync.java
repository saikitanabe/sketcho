package net.sevenscales.serverAPI.remote;

import com.google.gwt.user.client.rpc.AsyncCallback;

import net.sevenscales.domain.api.ITicket;

import java.util.List;

public interface TicketRemoteAsync {
  public void save(ITicket ticket, AsyncCallback<ITicket> async);
  public void update(ITicket sketch, AsyncCallback<ITicket> async);
  public void open(Long id, AsyncCallback<ITicket> async);
  public void findAll(Long projectId, AsyncCallback< List<ITicket> > async);
  public void findAll(Long projectId, Integer max, String where, String orderBy, AsyncCallback< List<ITicket> > async);
  public void remove(ITicket ticket, AsyncCallback async);
  public void removeAll(List<ITicket> tickets, AsyncCallback async);
}
