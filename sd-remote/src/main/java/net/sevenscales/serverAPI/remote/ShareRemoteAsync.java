package net.sevenscales.serverAPI.remote;

import net.sevenscales.domain.api.IEvent;
import net.sevenscales.domain.dto.ContactDTO;
import net.sevenscales.domain.dto.MessageDTO;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ShareRemoteAsync {
//  public void getEvents(AsyncCallback< List<IEvent> > async);
  @SuppressWarnings("unchecked")
  public void sendEvent(ContactDTO to, IEvent event, AsyncCallback async);
//  @SuppressWarnings("unchecked")
  public void sendMessage(ContactDTO to, MessageDTO message, AsyncCallback async);
}
