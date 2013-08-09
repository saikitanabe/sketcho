package net.sevenscales.server.service.impl;


import java.io.IOException;

import net.sevenscales.domain.dto.AuthenticationDTO;
import net.sevenscales.domain.dto.SdServerEception;
import net.sevenscales.server.dao.IUserDAO;
import net.sevenscales.server.domain.RegistrationDTO;
import net.sevenscales.server.event.LogoutEvent;
import net.sevenscales.server.service.ILoginService;

import org.acegisecurity.Authentication;
import org.acegisecurity.AuthenticationManager;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.acegisecurity.providers.anonymous.AnonymousAuthenticationToken;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import com.wutka.jox.JOXBeanInputStream;

public class LoginService implements ILoginService, ApplicationEventPublisherAware {
  private IUserDAO userDAO;
  private AuthenticationManager authenticationManager;
  private ApplicationEventPublisher eventPublisher;
  
  public void setUserDAO(IUserDAO userDAO) {
    this.userDAO = userDAO;
  }
  
  public IUserDAO getUserDAO() {
    return userDAO;
  }
  
  public void setAuthenticationManager(
      AuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;
  }
  
//  @Override
  public AuthenticationDTO authenticate(String sessionId, String username, String password) throws SdServerEception {
    if (!userDAO.isEnabled(username)) {
      // if user name is not enabled check is it activated
      ClientResource itemsResource = new ClientResource(
        "http://sketcho-registration.appspot.com/registrations/search/"+username);
      //ClientResource itemsResource = new ClientResource(
      //"http://localhost:8080/registrations");
      tryenable(itemsResource);
    }

    AuthenticationDTO result = new AuthenticationDTO();
    UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken
      (username, password);

    try {
      Authentication authResult = authenticationManager.authenticate(authRequest);
      SecurityContextHolder.getContext().setAuthentication(authResult);
    } catch (Exception e){
      throw new SdServerEception("Authentication failure: username or password wrong");
    }
    result.userId = authRequest.getName();

//    result.user = userDAO.find(username, password);
//    if (result.user == null) {
//      throw new SdServerEception("Authentication failure: username or password wrong");
//    }
    return result;
  }
  
  public void tryenable(ClientResource clientResource) throws SdServerEception {
    try {
      clientResource.get();
      if (clientResource.getStatus().isSuccess()
            && clientResource.getResponseEntity().isAvailable()) {
        JOXBeanInputStream joxIn = new JOXBeanInputStream(clientResource.getResponseEntity().getStream());
        
        RegistrationDTO registration = (RegistrationDTO) joxIn.readObject(RegistrationDTO.class);
        if (!registration.isActivated()) {
          throw new SdServerEception("Username is not activated.");
        }
        userDAO.setEnabled(registration.getEmail());
      }
    } catch (ResourceException e) {
      throw new SdServerEception("Username activation check failure");
    } catch (IOException e) {
      throw new SdServerEception("Username activation check failure");
    }
  }

//  @Override
  public AuthenticationDTO relogin() throws SdServerEception {
//    IUser result = collaborationManager.relogin(sessionId);
    Authentication a = SecurityContextHolder.getContext().getAuthentication();
    if (a instanceof AnonymousAuthenticationToken) {
      throw new SdServerEception("Authentication doesn't exists: ");        
    }
    
    AuthenticationDTO result = new AuthenticationDTO();
    result.userId = a.getName(); 
    return result;
  }
  
//  @Override
  public void logout(String sessionId) {
    Authentication logout = SecurityContextHolder.getContext().getAuthentication();
    SecurityContextHolder.getContext().setAuthentication(null);
    eventPublisher.publishEvent(new LogoutEvent(logout.getName()));

//    collaborationManager.logout(sessionId);
  }

//  private String generateUniqueAuthenticationToken(IUser user) {
//    String time = Long.toString(System.currentTimeMillis());
//    return BCrypt.hashpw(user.getUserId() + time, BCrypt.gensalt());
//  }
  
  public void setApplicationEventPublisher(ApplicationEventPublisher eventPublisher) {
    this.eventPublisher = eventPublisher;
  }
  
}
