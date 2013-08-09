package net.sevenscales.server.acegi;

import javax.servlet.http.HttpServletRequest;

import org.acegisecurity.ui.rememberme.TokenBasedRememberMeServices;

public class GwtTokenBasedRememberMeServices extends
    TokenBasedRememberMeServices {
  
  private boolean rememberMeRequested;

  @Override
  protected boolean rememberMeRequested(HttpServletRequest request,
      String parameter) {
    if (rememberMeRequested) {
      return true;
    }
    return super.rememberMeRequested(request, parameter);
  }
  
  public void setRememberMeRequested(boolean rememberMeRequested) {
    this.rememberMeRequested = rememberMeRequested;
  }
}
