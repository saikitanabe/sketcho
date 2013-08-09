package net.sevenscales.serverAPI.remote;

import java.util.List;
import java.util.Map;

import net.sevenscales.domain.api.IPage;
import net.sevenscales.domain.api.IPageOrderedContent;
import net.sevenscales.domain.api.IPageWithNamedContentValues;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface PageRemoteAsync {
  public void save(IPage page, AsyncCallback<IPage> async);
  /**
   * Opens page with content.
   * @param id
   * @param async
   */
  public void open(Long id, AsyncCallback<IPage> async);
  public void findAll(AsyncCallback< List<IPage> > async);
  public void findAll(Long projectId, Integer type, AsyncCallback< List<IPage> > callback);
  public void findAll(Long projectId, String filterOption, String order, List<String> namedItems, AsyncCallback<List<IPageWithNamedContentValues>> callback);
  public void findAllWithNamedContentValues(Long projectId, Integer type, List<String> namedItems, 
      AsyncCallback< List<IPageWithNamedContentValues> > callback);
  public void findAllWithNamedContentValues(Long projectId, Integer type, List<String> namedItems,
      Integer max, String where, String orderBy,
      AsyncCallback< List<IPageWithNamedContentValues> > callback);
  public void findAllWithNamedContentValues(Long projectId, String filterOption, Integer type, List<String> namedItems,
      Integer max, String where, String orderBy,
      AsyncCallback< List<IPageWithNamedContentValues> > callback);
  
  public void addContent(IPageOrderedContent orderedContent, AsyncCallback<IPageOrderedContent> async);
  public void updateContent(IPageOrderedContent content, AsyncCallback<IPageOrderedContent> async);
  public void moveContent(IPageOrderedContent content, Integer newPos, AsyncCallback<IPageOrderedContent> async);
  public void deleteContent(IPageOrderedContent content, AsyncCallback<IPage> callback);
  
  public void addPage(IPage page, IPage parent, AsyncCallback<IPage> async);
  public void update(IPage currentSubpage, AsyncCallback<IPage> asyncCallback);
  public void moveAndUpdate(IPage subpage, Long newParentId, AsyncCallback<IPage> callback);
  public void move(Long pageId, Integer orderValue, Long newParentId, AsyncCallback<IPage> callback);
  public void delete(IPage page, AsyncCallback callback);
  public void createPage(String templateClassName, Long projectId, AsyncCallback<IPage> asyncCallback);
  public void loadNamedContentValues(Long pageId, List<String> namedItems, AsyncCallback<Map<String, String>> asyncCallback);
}
