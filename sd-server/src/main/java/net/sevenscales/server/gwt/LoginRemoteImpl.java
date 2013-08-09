package net.sevenscales.server.gwt;


import net.sevenscales.domain.dto.AuthenticationDTO;
import net.sevenscales.domain.dto.SdServerEception;
import net.sevenscales.server.Configuration;
import net.sevenscales.server.GwtController;
import net.sevenscales.server.acegi.GwtTokenBasedRememberMeServices;
import net.sevenscales.server.service.ILoginService;
import net.sevenscales.serverAPI.remote.LoginRemote;
import net.sf.hibernate4gwt.core.HibernateBeanManager;

import org.acegisecurity.context.SecurityContextHolder;

public class LoginRemoteImpl extends GwtController implements LoginRemote {
  private ILoginService loginService;
  private GwtTokenBasedRememberMeServices rememberMeServices;
  private HibernateBeanManager beanManager;

  public LoginRemoteImpl() {
//    ApplicationContext.getInstance().loadConfiguration(getConfiguration());
  }
  
  public void setBeanManager(HibernateBeanManager beanManager) {
	this.beanManager = beanManager;
  }
  
  public void setLoginService(ILoginService loginService) {
    this.loginService = loginService;
  }

  protected Configuration getConfiguration() {
    return new Configuration("applicationContext.xml");
  }

//  @Override
  public AuthenticationDTO authenticate(String username, String password, Boolean staySignedIn) throws SdServerEception {
    AuthenticationDTO result = loginService.authenticate(
        getThreadLocalRequest().getSession().getId(), username, password);

    rememberMeServices.setRememberMeRequested(staySignedIn);
    if (staySignedIn) {
      // set remember me cookie
      rememberMeServices.loginSuccess(getThreadLocalRequest(), getThreadLocalResponse(), 
          SecurityContextHolder.getContext().getAuthentication());
    } else {
      // remove cookie
      rememberMeServices.loginFail(getThreadLocalRequest(), getThreadLocalResponse());
    }
    return result;
  }

//  @Override
  public AuthenticationDTO relogin() throws SdServerEception {
    return loginService.relogin();
  }
  
//  @Override
  public void logout() {
    rememberMeServices.logout(getThreadLocalRequest(), getThreadLocalResponse(), 
        SecurityContextHolder.getContext().getAuthentication());
    loginService.logout(getThreadLocalRequest().getSession().getId());
  }

  public void setRememberMeServices(GwtTokenBasedRememberMeServices rememberMeServices) {
    this.rememberMeServices = rememberMeServices;
  }

}
