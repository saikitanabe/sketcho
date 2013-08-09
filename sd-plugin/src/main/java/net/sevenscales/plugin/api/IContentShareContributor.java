package net.sevenscales.plugin.api;

import net.sevenscales.appFrame.api.IContributor;
import net.sevenscales.domain.api.IContent;

public interface IContentShareContributor extends IContributor {
  public void share(IContent content);
  public void save(IContent result);
}
