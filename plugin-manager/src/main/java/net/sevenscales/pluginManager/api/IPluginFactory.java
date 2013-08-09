package net.sevenscales.pluginManager.api;


public interface IPluginFactory {
  public IPlugin[] plugins();
  public String getFactoryName();
}
