package net.sevenscales.serverAPI.remote;

import java.util.List;

import net.sevenscales.domain.api.ILabel;
import net.sevenscales.domain.api.IPage;
import net.sevenscales.domain.api.IPageWithNamedContentValues;

public interface ILabelRemote {
  public ILabel save(ILabel label);
  public ILabel update(ILabel label);
  public ILabel open(Long id);
  public void removeFromPage(ILabel label, Long pageId);
  public void remove(ILabel label);
  public List<ILabel> findAll(Long projectId);
  public List<IPageWithNamedContentValues> findAllPages(Long labelId, List<String> namedItems, String filter, String sort);
  public ILabel addPageToLabel(ILabel label, IPage page);
}
