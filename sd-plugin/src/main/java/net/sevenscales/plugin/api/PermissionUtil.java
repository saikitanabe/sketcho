package net.sevenscales.plugin.api;

import net.sevenscales.domain.api.IUser;

public class PermissionUtil {

  public static boolean hasPermission(IUser user,
      Integer permission) {
//    if (user != null && 
//        (user.getPermissions().getPermissions() & permission) == permission) {
//      return true;
//    }
    return false;
  }

  public static boolean hasPermission(Integer permissions,
      Integer permission) {
    if (permissions != null && (permissions & permission) == permission) {
      return true;
    }
    return false;
  }

}
