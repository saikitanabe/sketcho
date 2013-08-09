package net.sevenscales.server.service;

import net.sevenscales.domain.api.ITemplate;
import net.sevenscales.server.domain.Page;
import net.sevenscales.serverAPI.remote.IPageRemote;

public interface IPageService extends IPageRemote {
  public Page openLazy(Long id);
  public Page createPage(ITemplate template, Long projectId);
}
