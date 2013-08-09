package net.sevenscales.webAdmin.client;

import net.sevenscales.appFrame.api.IContributor;
import net.sevenscales.appFrame.impl.ClassInfo;
import net.sevenscales.plugin.api.Context;
import net.sevenscales.plugin.api.ISdPlugin;
import net.sevenscales.plugin.api.ITopNavigationContributor;
import net.sevenscales.plugin.constants.RequestValue;
import net.sevenscales.webAdmin.client.controller.AdminController;
import net.sevenscales.webAdmin.client.controller.NewUserController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebAdminPlugin implements ISdPlugin {
  private static Map<String, ClassInfo> controllerMap;
  private static List<ClassInfo> defaultControllers = new ArrayList<ClassInfo>();  
  
  private ITopNavigationContributor topContributor;
  private Context context;

  static {
    controllerMap = new HashMap<String, ClassInfo>();
    controllerMap.put(RequestValue.ADMIN_CONTROLLER, AdminController.info());
    controllerMap.put(RequestValue.NEW_USER_CONTROLLER, NewUserController.info());
  };
  
  public void setContext(Context context) {
    this.context = context;
  }
  
  public String getName() {
    return "WebAdminPlugin";
  }

  public Map<String, ClassInfo> getControllerMap() {
    return controllerMap;
  }
  
  public List<ClassInfo> getDefaultControllers() {
    return defaultControllers;
  }
  
  // @Override
  public <T extends IContributor> T getContributor(Class<T> contributorClass) {
    if (contributorClass.equals(ITopNavigationContributor.class)) {
      topContributor = topContributor == null ? new AdminTopNavigationContributor(context) : topContributor;
      return (T) topContributor;
    }
    return null;
  }
  
}
