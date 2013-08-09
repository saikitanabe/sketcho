package net.sevenscales.server.service.impl;


import java.util.List;

import net.sevenscales.domain.api.IProject;
import net.sevenscales.server.dao.IProjectDAO;
import net.sevenscales.server.domain.Project;
import net.sevenscales.server.service.IProjectService;

import org.acegisecurity.Authentication;
import org.acegisecurity.acls.MutableAcl;
import org.acegisecurity.acls.MutableAclService;
import org.acegisecurity.acls.Permission;
import org.acegisecurity.acls.domain.AclImpl;
import org.acegisecurity.acls.domain.BasePermission;
import org.acegisecurity.acls.objectidentity.ObjectIdentity;
import org.acegisecurity.acls.objectidentity.ObjectIdentityImpl;
import org.acegisecurity.acls.sid.PrincipalSid;
import org.acegisecurity.context.SecurityContextHolder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;

public class ProjectService implements IProjectService, InitializingBean {
  private IProjectDAO projectDAO;
  private MutableAclService mutableAclService;
  private TransactionTemplate tt;

  public void setProjectDAO(IProjectDAO projectDAO) {
    this.projectDAO = projectDAO;
  }

  public void setMutableAclService(MutableAclService mutableAclService) {
      this.mutableAclService = mutableAclService;
  }

  public void setPlatformTransactionManager(PlatformTransactionManager platformTransactionManager) {
      this.tt = new TransactionTemplate(platformTransactionManager);
  }
  
  public void afterPropertiesSet() throws Exception {
    Assert.notNull(mutableAclService, "mutableAclService required");
    Assert.notNull(tt, "platformTransactionManager required");
  }

//  @Override
  public List<IProject> findAll() {
    return projectDAO.findAll();
  }
  
  public List<IProject> findAllPublicProjects() {
    return projectDAO.findAllPublicProjects();
  }

//  @Override
  public IProject save(IProject page) {
    Project result = (Project) projectDAO.save(page);
    
    // add object to acl
    final ObjectIdentity objectIdentity = new ObjectIdentityImpl(Project.class, result.getId());
    tt.execute(new TransactionCallback() {
            public Object doInTransaction(TransactionStatus arg0) {
                mutableAclService.createAcl(objectIdentity);
                return null;
            }
        });
    
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    grantPermissions(result.getId(), auth.getName(), BasePermission.ADMINISTRATION);
    
    return result;
  }
  
  private void grantPermissions(Long projectId, String userName, Permission permission) {
    AclImpl acl = (AclImpl) mutableAclService.readAclById(
          new ObjectIdentityImpl(Project.class, projectId));
    acl.insertAce(null, permission, new PrincipalSid(userName), true);
    updateAclInTransaction(acl);
  }

  private void updateAclInTransaction(final MutableAcl acl) {
      tt.execute(new TransactionCallback() {
              public Object doInTransaction(TransactionStatus arg0) {
                  mutableAclService.updateAcl(acl);
                  return null;
              }
          });
  }
  
//  @Override
  public IProject update(IProject project) {
    return projectDAO.update(project);
  }
  
//  @Override
  public IProject open(Long id) {
    return projectDAO.open(id);
  }
  
}
