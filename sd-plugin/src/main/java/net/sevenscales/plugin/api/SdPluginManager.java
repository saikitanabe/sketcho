package net.sevenscales.plugin.api;

import net.sevenscales.appFrame.api.IContributor;
import net.sevenscales.appFrame.impl.ClassInfo;
import net.sevenscales.plugin.impl.PermissionManager;
import net.sevenscales.plugin.impl.ShareContributorManager;
import net.sevenscales.plugin.impl.TopContributorManager;
import net.sevenscales.pluginManager.api.IPlugin;
import net.sevenscales.pluginManager.api.IPluginFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SdPluginManager implements IContributor {
  
  private IPluginFactory pluginFactory;
  private Map<String,ClassInfo> controllerMap;
  private List<ClassInfo> defaultControllers;
  private List<ISdPlugin> plugins;
  private ITopNavigationContributor topContributorManager;
  private IContentShareContributor contentShareManager;
  private Context context;
  private PermissionManager permissionManager;
  
  public SdPluginManager(IPluginFactory pluginFactory, Context context) {
    this.pluginFactory = pluginFactory;
    this.context = context;
  }
  
  public Map<String,ClassInfo> getControllerMap() {
    loadPlugins();
    if (controllerMap == null) {
      // load controller maps
      controllerMap = new HashMap<String, ClassInfo>();
      for (ISdPlugin p : plugins) {
        System.out.println(p.getName());
        controllerMap.putAll(p.getControllerMap());
      }
    }
    return controllerMap;
  }
  
  public List<ClassInfo> getDefaultControllers() {
    loadPlugins();
    if (defaultControllers == null) {
      // load controller maps
      defaultControllers = new ArrayList<ClassInfo>();
      for (ISdPlugin p : plugins) {
        System.out.println(p.getName());
        defaultControllers.addAll(p.getDefaultControllers());
      }
    }
    return defaultControllers;
  }

  @SuppressWarnings("unchecked")
  public <T> T cast(Class<T> clazz) {
    if (clazz.equals(ITopNavigationContributor.class)) {
      topContributorManager = topContributorManager == null ? new TopContributorManager(
          plugins) : topContributorManager;
      return (T) topContributorManager;
    } else if (clazz.equals(IContentShareContributor.class)) {
      contentShareManager = contentShareManager == null ? new ShareContributorManager(
          plugins) : contentShareManager;
      return (T) contentShareManager;
    } else if (clazz.equals(IPermissionContributor.class)) {
      permissionManager = permissionManager == null ? new PermissionManager(
          plugins, context) : permissionManager;
      return (T) permissionManager;
    }

    return null;
  }

  private void loadPlugins() {
    if (plugins == null) {
      plugins = new ArrayList<ISdPlugin>();
      for (IPlugin p : pluginFactory.plugins()) {
        ISdPlugin sdp = (ISdPlugin) p;
        sdp.setContext(context);
        plugins.add(sdp);
      }
    }
  }
}
