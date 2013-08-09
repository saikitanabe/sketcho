package net.sevenscales.server.acl;

import net.sevenscales.server.domain.Project;

import org.acegisecurity.Authentication;
import org.acegisecurity.acls.AclService;
import org.acegisecurity.acls.Permission;
import org.acegisecurity.afterinvocation.AclEntryAfterInvocationProvider;

public class AclEntryAfterInvocationProjectPublicProvider extends AclEntryAfterInvocationProvider {

  public AclEntryAfterInvocationProjectPublicProvider(AclService aclService,
      Permission[] requirePermission) {
    super(aclService, requirePermission);
  }

  @Override
  protected boolean hasPermission(Authentication authentication,
      Object domainObject) {
    Project p = (Project) domainObject;
    if (p.isPublicProject() != null && p.isPublicProject()) {
      return true;
    }
    return super.hasPermission(authentication, domainObject);
  }

}
