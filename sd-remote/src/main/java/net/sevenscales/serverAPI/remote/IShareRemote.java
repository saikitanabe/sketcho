package net.sevenscales.serverAPI.remote;

import net.sevenscales.domain.api.IEvent;
import net.sevenscales.domain.dto.ContactDTO;
import net.sevenscales.domain.dto.MessageDTO;

public interface IShareRemote {
  public void sendEvent(ContactDTO to, IEvent event);
  public void sendMessage(ContactDTO to, MessageDTO message);
}
