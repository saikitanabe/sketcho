package net.sevenscales.server.service;

import net.sevenscales.domain.dto.ContactDTO;
import net.sevenscales.domain.dto.MessageDTO;

public interface IShareService {
  public void sendMessage(String sessionId, ContactDTO to, MessageDTO message);
  
}
