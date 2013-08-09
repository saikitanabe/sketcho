package net.sevenscales.server.acl;

import java.util.Iterator;

import net.sevenscales.domain.api.IPage;
import net.sevenscales.domain.api.IProject;
import net.sevenscales.server.dao.IPageDAO;
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

public class AclPageMoveEntryVoter extends AclEntryVoter {
  
  private SidRetrievalStrategy sidRetrievalStrategy = new SidRetrievalStrategyImpl();
  private AclService aclService;
  private Permission[] requirePermission;
  private IPageDAO pageDAO;

  public AclPageMoveEntryVoter(AclService aclService,
      String processConfigAttribute, Permission[] requirePermission) {
    super(aclService, processConfigAttribute, requirePermission);
    this.aclService = aclService;
    this.requirePermission = requirePermission;
  }
  
  public void setPageDAO(IPageDAO pageDAO) {
    this.pageDAO = pageDAO;
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
            Long pageId = (Long) domainObject;
            
            IPage page = pageDAO.openLazy(pageId);
            IProject project = page.getProject();
            
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
