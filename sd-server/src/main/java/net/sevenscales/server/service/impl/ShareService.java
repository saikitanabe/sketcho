package net.sevenscales.server.service.impl;


import net.sevenscales.domain.dto.ContactDTO;
import net.sevenscales.domain.dto.MessageDTO;
import net.sevenscales.server.service.IShareService;

public class ShareService implements IShareService {
  
//  @Override
  public void sendMessage(String sessionId, ContactDTO to, MessageDTO message) {
    // find sender
    // find receiver
    // create event
    // add event
    // notifyAll receiver to get events 
  }

}
