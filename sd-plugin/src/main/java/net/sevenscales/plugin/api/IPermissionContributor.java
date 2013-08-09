package net.sevenscales.plugin.api;

import net.sevenscales.appFrame.api.IContributor;

public interface IPermissionContributor extends IContributor {

  boolean hasEditPermission();

  boolean hasDeletePermission();

  boolean hasAdminPermission();
  
  boolean hasCreatePermission();
}
