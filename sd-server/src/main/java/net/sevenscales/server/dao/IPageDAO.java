package net.sevenscales.server.dao;

import java.util.List;
import java.util.Map;

import net.sevenscales.domain.api.IPage;
import net.sevenscales.domain.api.IPageWithNamedContentValues;
import net.sevenscales.domain.api.ITemplate;
import net.sevenscales.server.domain.Page;
import net.sevenscales.serverAPI.remote.IPageRemote;


public interface IPageDAO extends IPageRemote {
  public Page openLazy(Long id);
  public Page createPage(ITemplate template, Long projectId);
  public Map<String, String> loadNamedContentValues(IPage page, List<String> namedItems);
  public List<IPageWithNamedContentValues> filterAndSort(
      List<Page> sketches, List<String> namedItems, String filter, String sort);
}
