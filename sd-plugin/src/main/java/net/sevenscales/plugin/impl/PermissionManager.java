package net.sevenscales.plugin.impl;

import net.sevenscales.plugin.api.Context;
import net.sevenscales.plugin.api.IPermissionContributor;
import net.sevenscales.plugin.api.ISdPlugin;
import net.sevenscales.plugin.api.PermissionUtil;

import java.util.List;

public class PermissionManager implements IPermissionContributor {
  
  private List<ISdPlugin> plugins;
  private Context context;
  
  public static final int EDIT_PERMISSION = 2;
  public static final int DELETE_PERMISSION = 8;
  public static final int ADMIN_PERMISSION = 16;

  public PermissionManager(List<ISdPlugin> plugins, Context context) {
    this.plugins = plugins;
    this.context = context;
  }

  public <T> T cast(Class<T> clazz) {
    return null;
  }
  
  // @Override
  public boolean hasEditPermission() {
    boolean result = PermissionUtil.hasPermission(context.getMemberPermissions(), EDIT_PERMISSION);
    if (!result) {
      result = hasAdminPermission();
    }
    return result;
  }
  
  public boolean hasDeletePermission() {
    boolean result = PermissionUtil.hasPermission(context.getMemberPermissions(), DELETE_PERMISSION);
    if (!result) {
      result = hasAdminPermission();
    }
    return result;
  }
  
  public boolean hasAdminPermission() {
    return PermissionUtil.hasPermission(context.getMemberPermissions(), ADMIN_PERMISSION);
  }
  
  public boolean hasCreatePermission() {
	  return context.getUserId() != null;
  }

}
