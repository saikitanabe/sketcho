package net.sevenscales.share.plugin;

import net.sevenscales.appFrame.api.IContributor;
import net.sevenscales.appFrame.impl.ClassInfo;
import net.sevenscales.plugin.api.Context;
import net.sevenscales.plugin.api.IContentShareContributor;
import net.sevenscales.plugin.api.ISdPlugin;
import net.sevenscales.share.plugin.controller.ShareController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SharePlugin implements ISdPlugin {
  private static Map<String, ClassInfo> controllerMap;
  private static List<ClassInfo> defaultControllers = new ArrayList<ClassInfo>();;
  private IContentShareContributor contentShareContributor;
  private Context context;

  static {
    controllerMap = new HashMap<String, ClassInfo>();
    
    defaultControllers.add(ShareController.info());
  };

  public void setContext(Context context) {
    this.context = context;
  }

  public String getName() {
    return "sharePlugin";
  }

  public Map<String, ClassInfo> getControllerMap() {
    return controllerMap;
  }
  
  public List<ClassInfo> getDefaultControllers() {
    return defaultControllers;
  }

  // @Override
  public <T extends IContributor> T getContributor(Class<T> contributorClass) {
//    if (contributorClass.equals(IContentShareContributor.class)) {
//      contentShareContributor = contentShareContributor == null ? 
//          new ContentShareContributor(context) : contentShareContributor;
//      return (T) contentShareContributor;
//    }
    return null;
  }

}
