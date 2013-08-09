package net.sevenscales.login.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sevenscales.appFrame.api.IContributor;
import net.sevenscales.appFrame.impl.ClassInfo;
import net.sevenscales.login.client.controller.LoginController;
import net.sevenscales.login.client.controller.RegisterController;
import net.sevenscales.plugin.api.Context;
import net.sevenscales.plugin.api.ISdPlugin;
import net.sevenscales.plugin.api.ITopNavigationContributor;
import net.sevenscales.plugin.constants.RequestValue;

public class LoginPlugin implements ISdPlugin {
  private static Map<String, ClassInfo> controllerMap;
  private static List<ClassInfo> defaultControllers = new ArrayList<ClassInfo>();
  
  private ITopNavigationContributor topContributor;
  private Context context;

  static {
    controllerMap = new HashMap<String, ClassInfo>();
    controllerMap.put(RequestValue.LOGIN_CONTROLLER, LoginController.info());
    controllerMap.put(RequestValue.REGISTER_CONTROLLER, RegisterController.info());
  };

  public void setContext(Context context) {
    this.context = context;
  }

  public String getName() {
    return "loginPlugin";
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
      topContributor = topContributor == null ? new LoginTopNavigationContributor(context) : topContributor;
      return (T) topContributor;
    }
    return null;
  }

}
