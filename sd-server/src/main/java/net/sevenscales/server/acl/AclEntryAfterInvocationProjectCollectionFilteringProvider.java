package net.sevenscales.server.acl;

import org.acegisecurity.Authentication;
import org.acegisecurity.acls.AclService;
import org.acegisecurity.acls.Permission;
import org.acegisecurity.afterinvocation.AclEntryAfterInvocationCollectionFilteringProvider;


public class AclEntryAfterInvocationProjectCollectionFilteringProvider extends AclEntryAfterInvocationCollectionFilteringProvider {
  
  private AclEntryAfterInvocationProjectPublicProvider publicProjectProvider;

  public AclEntryAfterInvocationProjectCollectionFilteringProvider(
      AclService aclService, Permission[] requirePermission) {
    super(aclService, requirePermission);
    publicProjectProvider = new AclEntryAfterInvocationProjectPublicProvider(aclService, requirePermission);
  }

  @Override
  protected boolean hasPermission(Authentication authentication,
      Object domainObject) {
    if (publicProjectProvider.hasPermission(authentication, domainObject)) {
      return true;
    }
    return super.hasPermission(authentication, domainObject);
  }
}
