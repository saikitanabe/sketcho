package net.sevenscales.serverAPI.remote;

import java.util.List;

import net.sevenscales.domain.api.ILabel;
import net.sevenscales.domain.api.IPage;
import net.sevenscales.domain.api.IPageWithNamedContentValues;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LabelRemoteAsync {
  public void save(ILabel label, AsyncCallback<ILabel> async);
  public void update(ILabel label, AsyncCallback<ILabel> async);
  public void open(Long id, AsyncCallback<ILabel> async);
  public void removeFromPage(ILabel label, Long pageId, AsyncCallback<Void> async);
  public void remove(ILabel label, AsyncCallback<Void> async);
  public void findAll(Long projectId, AsyncCallback< List<ILabel> > async);
  public void findAllPages(Long labelId, List<String> namedItems, String filter, String sort, AsyncCallback<List<IPageWithNamedContentValues>> async);
  public void addPageToLabel(ILabel label, IPage page, AsyncCallback<ILabel> async);
}
