package net.sevenscales.server.service;

import net.sevenscales.domain.api.ITicket;
import net.sevenscales.serverAPI.remote.ITicketRemote;

public interface ITicketService extends ITicketRemote {
  public ITicket save(ITicket ticket);
  public ITicket update(ITicket ticket);
}
