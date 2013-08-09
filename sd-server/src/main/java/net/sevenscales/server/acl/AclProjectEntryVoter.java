package net.sevenscales.server.acl;

import java.util.Iterator;

import net.sevenscales.domain.api.ILabel;
import net.sevenscales.domain.api.IPageOrderedContent;
import net.sevenscales.domain.api.IProject;
import net.sevenscales.server.dao.ILabelDAO;
import net.sevenscales.server.dao.IProjectDAO;
import net.sevenscales.server.domain.Label;
import net.sevenscales.server.domain.Project;

import org.acegisecurity.Authentication;
import org.acegisecurity.ConfigAttribute;
import org.acegisecurity.ConfigAttributeDefinition;
import org.acegisecurity.acls.Acl;
import org.acegisecurity.acls.AclService;
import org.acegisecurity.acls.NotFoundException;
import org.acegisecurity.acls.Permission;
import org.acegisecurity.acls.objectidentity.ObjectIdentity;
import org.acegisecurity.acls.objectidentity.ObjectIdentityImpl;
import org.acegisecurity.acls.sid.Sid;
import org.acegisecurity.acls.sid.SidRetrievalStrategy;
import org.acegisecurity.acls.sid.SidRetrievalStrategyImpl;
import org.acegisecurity.vote.AccessDecisionVoter;
import org.acegisecurity.vote.AclEntryVoter;

public class AclProjectEntryVoter extends AclEntryVoter {
  
  private SidRetrievalStrategy sidRetrievalStrategy = new SidRetrievalStrategyImpl();
  private AclService aclService;
  private Permission[] requirePermission;
  private boolean publicOk;
  private IProjectDAO projectDAO;

  public AclProjectEntryVoter(AclService aclService,
      String processConfigAttribute, Permission[] requirePermission) {
    super(aclService, processConfigAttribute, requirePermission);
    this.aclService = aclService;
    this.requirePermission = requirePermission;
  }
  
  public void setPublicOk(boolean publicOk) {
    this.publicOk = publicOk;
  }
  public void setProjectDAO(IProjectDAO projectDAO) {
    this.projectDAO = projectDAO;
  }
  
  public int vote(Authentication authentication, Object object,
      ConfigAttributeDefinition config) {
    Iterator iter = config.getConfigAttributes();

    while (iter.hasNext()) {
        ConfigAttribute attr = (ConfigAttribute) iter.next();

        if (this.supports(attr)) {
            // Need to make an access decision on this invocation
            // Attempt to locate the domain object instance to process
            Object domainObject = getDomainObjectInstance(object);
            
            IProject project = null;
            if (domainObject instanceof IPageOrderedContent) {
              IPageOrderedContent poc = (IPageOrderedContent) domainObject;
              project = poc.getPage().getProject();
            } else if (domainObject instanceof ILabel) {
              project = ((ILabel) domainObject).getProject();
              if (publicOk) {
                project = projectDAO.open(project.getId());
                if (project.isPublicProject()) {
                  return AccessDecisionVoter.ACCESS_GRANTED;
                }
              }
            }
            
            ObjectIdentityImpl impl = new ObjectIdentityImpl(project);
            ObjectIdentity oid = new ObjectIdentityImpl(Project.class, project.getId());

            // Obtain the SIDs applicable to the principal
            Sid[] sids = sidRetrievalStrategy.getSids(authentication);

            Acl acl;

            try {
                // Lookup only ACLs for SIDs we're interested in
                acl = aclService.readAclById(oid, sids);
            } catch (NotFoundException nfe) {
              return AccessDecisionVoter.ACCESS_DENIED;
            }

            try {
              if (acl.isGranted(requirePermission, sids, false)) {
                  return AccessDecisionVoter.ACCESS_GRANTED;
              }
            } catch (NotFoundException nfe) {
              return AccessDecisionVoter.ACCESS_DENIED;
            }
        }
    }
    return AccessDecisionVoter.ACCESS_DENIED;
  }

}
