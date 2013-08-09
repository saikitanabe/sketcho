package net.sevenscales.serverAPI.remote;

import java.util.List;
import java.util.Map;

import net.sevenscales.domain.api.IPage;
import net.sevenscales.domain.api.IPageOrderedContent;
import net.sevenscales.domain.api.IPageWithNamedContentValues;
import net.sevenscales.domain.dto.SdServerEception;

public interface IPageRemote {
  public static final String ORDER_BY_LABELS = "order-by-labels";
  
  public IPage save(IPage page);
  public IPage open(Long id);
  public List<IPage> findAll(); 
  public List<IPage> findAll(Long projectId, Integer type);
  public List<IPageWithNamedContentValues> findAll(Long projectId, String filterOption, String order, List<String> namedItems);
  public List<IPageWithNamedContentValues> findAllWithNamedContentValues(Long projectId, Integer type, List<String> namedItems);
  public List<IPageWithNamedContentValues> findAllWithNamedContentValues(Long projectId, Integer type, List<String> namedItems,
      Integer max, String where, String orderBy);
  public List<IPageWithNamedContentValues> findAllWithNamedContentValues(Long projectId, String filterOption, Integer type, List<String> namedItems,
      Integer max, String where, String orderBy);
  
  public IPageOrderedContent addContent(IPageOrderedContent orderedContent);
  public IPageOrderedContent updateContent(IPageOrderedContent content);
  public IPageOrderedContent moveContent(IPageOrderedContent content, Integer newPos);
  public IPage deleteContent(IPageOrderedContent content);
  
  public IPage addPage(IPage page, IPage parent) throws SdServerEception;
  public IPage update(IPage subpage);
  public IPage moveAndUpdate(IPage subpage, Long newParentId);
  public IPage move(Long pageId, Integer orderValue, Long newParentId);
  public void delete(IPage page);
  public IPage createPage(String templateClassName, Long projectId);
  public Map<String, String> loadNamedContentValues(Long pageId, List<String> namedItems);

//  /**
//   * Moves subpage under parent.
//   * @param subpage
//   * @param parent
//   * @return updated old subpage parent and subpage with new parent
//   */
//  public MoveResultDTO move(IPage subpage);
}
