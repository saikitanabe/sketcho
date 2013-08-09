package net.sevenscales.plugin.api;

import net.sevenscales.appFrame.api.IContributor;
import net.sevenscales.appFrame.impl.ClassInfo;
import net.sevenscales.pluginManager.api.IPlugin;

import java.util.List;
import java.util.Map;

public interface ISdPlugin extends IPlugin {
  public void setContext(Context context);
  public Map<String, ClassInfo> getControllerMap();
  public List<ClassInfo> getDefaultControllers();
  public <T extends IContributor> T getContributor(Class<T> contributorClass);
}
