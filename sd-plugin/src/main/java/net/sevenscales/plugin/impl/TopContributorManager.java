package net.sevenscales.plugin.impl;

import net.sevenscales.plugin.api.ISdPlugin;
import net.sevenscales.plugin.api.ITopNavigationContributor;

import java.util.List;

public class TopContributorManager implements ITopNavigationContributor {
  
  private List<ISdPlugin> plugins;

  public TopContributorManager(List<ISdPlugin> plugins) {
    this.plugins = plugins;
  }

  public void addToRight(ITopNaviPanel right) {
    for (ISdPlugin p : plugins) {
      ITopNavigationContributor topContributor = p.getContributor(ITopNavigationContributor.class);
      if (topContributor != null) {
        topContributor.addToRight(right);
      }
    }
  }

  public <T> T cast(Class<T> clazz) {
    return null;
  }
}
