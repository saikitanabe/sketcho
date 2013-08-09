package net.sevenscales.server.gwt;


import java.util.List;

import javax.servlet.http.Cookie;

import net.sevenscales.domain.api.IProject;
import net.sevenscales.domain.api.IUser;
import net.sevenscales.domain.api.Member;
import net.sevenscales.domain.api.ProjectContextDTO;
import net.sevenscales.domain.dto.SdServerEception;
import net.sevenscales.server.Configuration;
import net.sevenscales.server.GwtController;
import net.sevenscales.server.service.IAdminService;
import net.sevenscales.serverAPI.remote.AdminRemote;
import net.sevenscales.serverAPI.remote.IProjectRemote;
import net.sevenscales.serverAPI.remote.ProjectRemote;
import net.sf.hibernate4gwt.core.HibernateBeanManager;

import org.springframework.beans.factory.annotation.Required;

import com.google.gwt.user.client.ui.SuggestOracle;

public class AdminRemoteImpl extends GwtController implements AdminRemote {
  private IAdminService adminService;
  private IProjectRemote projectRemote;
  private HibernateBeanManager beanManager;
  
  public AdminRemoteImpl() {
//    ApplicationContext.getInstance().loadConfiguration(getConfiguration());    
  }

  protected Configuration getConfiguration() {
    return new Configuration("applicationContext.xml");
  }
  
  @Required
  public void setBeanManager(HibernateBeanManager beanManager) {
	this.beanManager = beanManager;
  }
  
  public IAdminService getAdminService() {
    return adminService;
  }
  
  public void setAdminService(IAdminService adminService) {
    this.adminService = adminService;
  }
  
  public void setProjectRemote(ProjectRemote projectRemote) {
    this.projectRemote = projectRemote;
  }

//  @Override
  public IUser save(IUser user) {
    user = (IUser) beanManager.merge(user);
    // TODO: currently password is stored as plain text.
    // This is only on testing time, because otherwise I will
    // forget passwords and cannot read those later with db query browser :)
    IUser result = adminService.save(user);
    return (IUser) beanManager.clone(result);
  }

//  @Override
  public List<IUser> findAll() {
    Cookie[] cookies = getThreadLocalRequest().getCookies();
    List<IUser> result = adminService.findAll();
    result = (List<IUser>) beanManager.clone(result);
    return result;
  }

  public List<Member> findAll(Long projectId) {
    return adminService.findAll(projectId);
  }

  public SuggestOracle.Response findAll(SuggestOracle.Request request) {
    return null;
//    return adminService.findAll(request);
  }

//  @Override
  public void remove(IUser user) {
    user = (IUser) beanManager.merge(user);
    adminService.remove(user);
  }
  
//  @Override
  public void removeAll(List<IUser> users) {
    users = (List<IUser>) beanManager.merge(users);
    adminService.removeAll(users);
  }
  
  public Integer projectUserPermissions(Long projectId) {
    return adminService.projectUserPermissions(projectId);
  }

  public List<Member> addMember(Long id, IProject project) {
    return adminService.addMember(id, (IProject) beanManager.merge(project));
  }

  public List<Member> addMember(String username, IProject project) {
    return adminService.addMember(username, (IProject) beanManager.merge(project));
  }
  
  public List<Member> removeMember(String username, IProject project) {
    return adminService.removeMember(username, (IProject) beanManager.merge(project));
  }

  public Member addPermission(String username, IProject project,
      Integer permission) {
    return adminService.addPermission(username, (IProject) beanManager.merge(project), permission);
  }
  
  public Member deletePermission(String username, IProject project,
      Integer permission) {
    return adminService.deletePermission(username, (IProject) beanManager.merge(project), permission);
  }
  
  public void register(String userName, String nickName, String password) throws SdServerEception {
    adminService.register(userName, nickName, password);
  }
  
  public ProjectContextDTO openProjectContext(Long projectId) {
    ProjectContextDTO result = new ProjectContextDTO();
    result.setProject(projectRemote.open(projectId));
    result.setMemberPermissions(projectUserPermissions(projectId));
    return result;
  }
}
