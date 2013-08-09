package net.sevenscales.serverAPI.remote;

import net.sevenscales.domain.dto.AuthenticationDTO;
import net.sevenscales.domain.dto.SdServerEception;

public interface ILoginRemote {
  public AuthenticationDTO authenticate(String username, String password, Boolean staySignedIn) throws SdServerEception;
  public AuthenticationDTO relogin() throws SdServerEception;
  public void logout();
}
