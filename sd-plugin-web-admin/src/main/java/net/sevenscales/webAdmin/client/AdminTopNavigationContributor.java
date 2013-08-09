package net.sevenscales.webAdmin.client;

import java.util.HashMap;
import java.util.Map;

import net.sevenscales.appFrame.impl.Action;
import net.sevenscales.appFrame.impl.ActionFactory;
import net.sevenscales.plugin.api.Context;
import net.sevenscales.plugin.api.IPermissionContributor;
import net.sevenscales.plugin.api.ITopNavigationContributor;
import net.sevenscales.plugin.api.PermissionUtil;
import net.sevenscales.plugin.constants.Permissions;
import net.sevenscales.plugin.constants.RequestId;
import net.sevenscales.plugin.constants.RequestValue;
import net.sevenscales.serverAPI.remote.AdminRemote;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class AdminTopNavigationContributor implements ITopNavigationContributor {
  
  private Context context;
  private ITopNaviPanel right;

  public AdminTopNavigationContributor(Context context) {
    this.context = context;
  }

  public void addToRight(ITopNaviPanel right) {
    this.right = right;
    if (context.getProjectId() != null) {
      IPermissionContributor permissionContributor = context.getContributor().cast(
          IPermissionContributor.class);
//      if (PermissionUtil.hasPermission(
//          context.getPermissions(), Permissions.ADMIN_VIEW)) {
      if (permissionContributor.hasAdminPermission()) {
        Map<Object, Object> requests = new HashMap<Object, Object>();
        requests.put(RequestId.CONTROLLER, RequestValue.ADMIN_CONTROLLER);
        requests.put(RequestId.PROJECT_ID, context.getProjectId());
        Action a = ActionFactory.createLinkAction("Admin", requests);
        AdminTopNavigationContributor.this.right.addItem(a);
      }
    }
  }

  public <T> T cast(Class<T> clazz) {
    return null;
  }

}
