package net.sevenscales.plugin.impl;

import net.sevenscales.domain.api.IContent;
import net.sevenscales.plugin.api.IContentShareContributor;
import net.sevenscales.plugin.api.ISdPlugin;

import java.util.List;

public class ShareContributorManager implements IContentShareContributor {
  
  private List<ISdPlugin> plugins;

  public ShareContributorManager(List<ISdPlugin> plugins) {
    this.plugins = plugins;
  }

  // @Override
  public void share(IContent content) {
    for (ISdPlugin p : plugins) {
      IContentShareContributor contributor = p.getContributor(IContentShareContributor.class);
      if (contributor != null) {
        contributor.share(content);
      }
    }
  }

  // @Override
  public void save(IContent content) {
    for (ISdPlugin p : plugins) {
      IContentShareContributor contributor = p.getContributor(IContentShareContributor.class);
      if (contributor != null) {
        contributor.save(content);
      }
    }
  }


  public <T> T cast(Class<T> clazz) {
    return null;
  }

}
