package net.sevenscales.confluence.plugins;

import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;

public class SpaceEditCondition extends BaseConfluenceCondition {
  private PermissionManager permissionManager;
  
  public void setPermissionManager(PermissionManager permissionManager) {
    this.permissionManager = permissionManager;
  }

  public boolean shouldDisplay(WebInterfaceContext context) {
    boolean editable = permissionManager.hasPermission(context.getUser(), Permission.EDIT, context.getPage());
    return editable;
  }
}
