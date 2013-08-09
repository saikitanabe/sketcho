package net.sevenscales.server.service;

import net.sevenscales.domain.dto.AuthenticationDTO;
import net.sevenscales.domain.dto.SdServerEception;

public interface ILoginService {
  public AuthenticationDTO authenticate(String sessionId, String username, String password) throws SdServerEception;
  public AuthenticationDTO relogin() throws SdServerEception;
  public void logout(String sessionId);  
}
