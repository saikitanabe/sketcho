package net.sevenscales.server.service.impl;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import net.sevenscales.domain.api.IProject;
import net.sevenscales.domain.api.IUser;
import net.sevenscales.domain.api.Member;
import net.sevenscales.domain.api.ProjectContextDTO;
import net.sevenscales.domain.dto.SdServerEception;
import net.sevenscales.server.dao.IUserDAO;
import net.sevenscales.server.service.IAdminService;

import org.acegisecurity.context.SecurityContextHolder;
import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import com.google.gwt.user.client.ui.SuggestOracle.Request;
import com.google.gwt.user.client.ui.SuggestOracle.Response;

public class AdminService implements IAdminService {
  private IUserDAO userDAO;
//  final Logger logger = LoggerFactory.getLogger(AdminService.class);

  public void setUserDAO(IUserDAO userDAO) {
    this.userDAO = userDAO;
  }
  
  public IUserDAO getUserDAO() {
    return userDAO;
  }
  
//  @Override
  public IUser save(IUser user) {
    return userDAO.save(user);
  }
  
//  @Override
  public List<IUser> findAll() {
    return userDAO.findAll();
  }
  
  public List<Member> findAll(Long projectId) {
    return userDAO.findAll(projectId);
  }
  
  public Response findAll(Request request) {
    return userDAO.findAll(request);
  }
  
//  @Override
  public void remove(IUser user) {
    userDAO.remove(user);
  }

//  @Override
  public void removeAll(List<IUser> users) {
    userDAO.remove(users);
  }
  
  public Integer projectUserPermissions(Long projectId) {
    String userName = SecurityContextHolder.getContext().getAuthentication().getName();
    return userDAO.projectUserPermissions(projectId, userName);
  }

  public List<Member> addMember(Long id, IProject project) {
    return userDAO.addMember(id, project);
  }

  public List<Member> addMember(String username, IProject project) {
    return userDAO.addMember(username, project);
  }
  
  public List<Member> removeMember(String username, IProject project) {
    return userDAO.removeMember(username, project);
  }

  public Member addPermission(String username, IProject project,
      Integer permission) {
    return userDAO.addPermission(username, project, permission);
  }
  
  public Member deletePermission(String username, IProject project,
      Integer permission) {
    return userDAO.deletePermission(username, project, permission);
  }
  
  public void register(String userName, String nickName, String password) throws SdServerEception {
    // add user with password as disabled
    if (userDAO.find(userName) == null) {
      userDAO.register(userName, nickName, password);
      postRegistrationRequest(userName, password);
    } else {
      throw new SdServerEception("User name already exists.");
    }
  }
  
  private void postRegistrationRequest(String userName, String password) {
    ClientResource itemsResource = new ClientResource(
        "http://sketcho-registration.appspot.com/registrations");
    //ClientResource itemsResource = new ClientResource(
    //"http://localhost:8080/registrations");
    
    // post registration
    Representation r;
    try {
      r = itemsResource.post(getRepresentation(userName, password));
    if (itemsResource.getStatus().isSuccess()) {
      System.out.println("Created: "+r.getIdentifier());
    } else {
      BufferedReader br = new BufferedReader(
          new InputStreamReader(itemsResource.getResponseEntity().getStream()));
      
      StringBuffer sb = new StringBuffer();
      String line = null;
      while ((line = br.readLine()) != null) {
        sb.append(line + "\n");
      }
      System.out.println("Failed: "+itemsResource.getStatus()+sb);
    //  itemsResource.getResponseEntity().write(System.out);
      throw new RuntimeException("FAILURE: "+itemsResource.getStatus()+sb);
    }
    } catch (ResourceException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static Representation getRepresentation(String email, String password) {
    // Gathering informations into a Web form.
    Form form = new Form();
    form.add("email", email);
    form.add("password", password);
    return form.getWebRepresentation();
  }
  
  // jaxrs
//  private void postRegistrationRequest(String userName) {
//    // Sent HTTP POST request to add registration request
//    PostMethod post = new PostMethod("http://localhost:8989/rest/registrationservice/registrations");
//    post.addRequestHeader("Accept" , "text/xml");
//    String format = "<RegistrationDTO>"+
//                    "<email>%s</email>"+
//                "</RegistrationDTO>";
//    String xml = String.format(format, userName);
//  
//    try {
//        RequestEntity entity = new StringRequestEntity(xml, "text/xml", "ISO-8859-1");
//        post.setRequestEntity(entity);
//        HttpClient httpclient = new HttpClient();
//        int result = httpclient.executeMethod(post);
//        logger.info("Response status code: " + result);
//        logger.info("Response body: ");
//        logger.info(post.getResponseBodyAsString());
//    } catch (UnsupportedEncodingException e) {
//      throw new RuntimeException(e);
//    } catch (HttpException e) {
//      throw new RuntimeException(e);
//    } catch (IOException e) {
//      throw new RuntimeException(e);
//    } finally {
//        // Release current connection to the connection pool once you are
//        // done
//        post.releaseConnection();
//    }
//  }
//
//  private static String getStringFromInputStream(InputStream in) throws Exception {
//    CachedOutputStream bos = new CachedOutputStream();
//    IOUtils.copy(in, bos);
//    in.close();
//    bos.close();
//    return bos.getOut().toString();
//  }

  public ProjectContextDTO openProjectContext(Long projectId) {
    return null;
  }
}
