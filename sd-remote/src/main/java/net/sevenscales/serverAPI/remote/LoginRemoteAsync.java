package net.sevenscales.serverAPI.remote;

import net.sevenscales.domain.dto.AuthenticationDTO;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LoginRemoteAsync {
  public void authenticate(String username, String password, Boolean staySignedIn, AsyncCallback<AuthenticationDTO> async);
  public void relogin(AsyncCallback<AuthenticationDTO> async);
  public void logout(AsyncCallback async);  
}
