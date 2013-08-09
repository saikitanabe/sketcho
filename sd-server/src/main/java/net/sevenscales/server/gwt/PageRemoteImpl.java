package net.sevenscales.server.gwt;


import java.util.List;
import java.util.Map;

import net.sevenscales.domain.api.IPage;
import net.sevenscales.domain.api.IPageOrderedContent;
import net.sevenscales.domain.api.IPageWithNamedContentValues;
import net.sevenscales.domain.api.ITemplate;
import net.sevenscales.domain.dto.PageOrderedContentDTO;
import net.sevenscales.domain.dto.SdServerEception;
import net.sevenscales.server.Configuration;
import net.sevenscales.server.GwtController;
import net.sevenscales.server.domain.Page;
import net.sevenscales.server.domain.PageOrderedContent;
import net.sevenscales.server.event.ContentUpdateEvent;
import net.sevenscales.server.service.IPageService;
import net.sevenscales.serverAPI.remote.PageRemote;
import net.sf.hibernate4gwt.core.HibernateBeanManager;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

public class PageRemoteImpl extends GwtController implements PageRemote,
                            ApplicationEventPublisherAware {
  private IPageService pageService;
  private ApplicationEventPublisher eventPublisher;
  private HibernateBeanManager beanManager;
  
  public PageRemoteImpl() {
  }
  
  public HibernateBeanManager getBeanManager() {
    return beanManager;
  }
  public void setBeanManager(HibernateBeanManager beanManager) {
    this.beanManager = beanManager;
  }

  protected Configuration getConfiguration() {
    return new Configuration("applicationContext.xml");
  }
  
  public IPageService getPageService() {
    return pageService;
  }
  
  public void setPageService(IPageService pageService) {
    this.pageService = pageService;
  }
  
//  @Override
  public List<IPage> findAll() {
    return pageService.findAll();
  }
  
  public List<IPage> findAll(Long projectId, Integer type) {
    return (List<IPage>)beanManager.clone(pageService.findAll(projectId, type));
  }
  
  public List<IPageWithNamedContentValues> findAll(Long projectId, String filterOption, String order, List<String> namedItems) {
    return (List<IPageWithNamedContentValues>)beanManager.clone(pageService.findAll(projectId, filterOption, order, namedItems));
  }

  public List<IPageWithNamedContentValues> findAllWithNamedContentValues(
      Long projectId, Integer type, List<String> namedItems) {
    List<IPageWithNamedContentValues> result = pageService.findAllWithNamedContentValues
      (projectId, type, namedItems);
    
    return (List<IPageWithNamedContentValues>) beanManager.clone(result);
  }
  
  public List<IPageWithNamedContentValues> findAllWithNamedContentValues(
      Long projectId, Integer type, List<String> namedItems, Integer max,
      String where, String orderBy) {
    List<IPageWithNamedContentValues> result = pageService.findAllWithNamedContentValues
      (projectId, type, namedItems, max, where, orderBy);
    return (List<IPageWithNamedContentValues>) beanManager.clone(result);
  }

  public List<IPageWithNamedContentValues> findAllWithNamedContentValues(
      Long projectId, String filterOption, Integer type, List<String> namedItems, Integer max,
      String where, String orderBy) {
    List<IPageWithNamedContentValues> result = pageService.findAllWithNamedContentValues
      (projectId, filterOption, type, namedItems, max, where, orderBy);
    return (List<IPageWithNamedContentValues>) beanManager.clone(result);
  }

//  @Override
  public IPage save(IPage page) {
    Page merged = (Page) beanManager.merge(page);
    pageService.save(merged);
    merged = pageService.openLazy(merged.getId());
    return (IPage) beanManager.clone(merged);
  }

//  @Override
  public IPage open(Long id) {
    Page page = (Page) pageService.open(id);
    return (IPage) beanManager.clone(page);
  }
  
//  @Override
  public IPageOrderedContent addContent(IPageOrderedContent orderedContent) {
    orderedContent = (IPageOrderedContent) beanManager.merge(orderedContent);
    orderedContent = pageService.addContent(orderedContent);
    
    // need to manually clone PageOrderedContentDTO, because
    // otherwise collection replication would fail hence
    // PageOrderedContentDTO is already a one of collection items
    // and would have null order value during contentItems replication
    // TODO: better solution would be extend hibernate4gwt to accept
    // custom transformer for PageOrderedContentDTO
    PageOrderedContentDTO result = new PageOrderedContentDTO();
    result.setPage((IPage) beanManager.clone( orderedContent.getPage() ));
    
    // cannot separately clone content, because then new instance would be
    // created and hence need retrieve it from page, above TODO would
    // fix this too.
    result.setContent(result.getPage().findContent(orderedContent.getId())
        .getContent());
    
    result.setOrderValue(result
        .getPage()
        .findContent(
            orderedContent.getId())
              .getOrderValue());
    result.setId(orderedContent.getId());
    
    return result;
  }
  
//  @Override
  public IPageOrderedContent updateContent(IPageOrderedContent orderedContent) {
//    Page p = (Page) beanManager.merge(orderedContent.getPage());
    orderedContent = (IPageOrderedContent) beanManager.merge(orderedContent);
//    orderedContent.setPage(p);
    orderedContent = (IPageOrderedContent) pageService.updateContent(orderedContent);
    IPage page = orderedContent.getPage();

    // just clone enhanced parent and find content from cloned page
    // otherwise parent content items doesn't point to correct content
    // TODO: fix sorted as last method and add clonePojoEnhanced if still needed
//    page = (IPage) getBeanManager().clonePojoEnhanced(page);s
    page = (IPage) beanManager.clone(page);
    orderedContent = page.findContent(orderedContent.getId());
    
    assert(orderedContent.getContent() == page.findContent(orderedContent.getId())
        .getContent());
    
    eventPublisher.publishEvent(new ContentUpdateEvent(orderedContent.getContent()));
    return orderedContent;
  }
  
  public IPageOrderedContent moveContent(IPageOrderedContent content,
      Integer newPos) {
    IPageOrderedContent result = pageService.moveContent((IPageOrderedContent) beanManager.merge(content), newPos); 
    return (IPageOrderedContent) beanManager.clone(result);
  }
  
//  @Override
  public IPage deleteContent(IPageOrderedContent orderedContent) {
    PageOrderedContent poc = (PageOrderedContent) beanManager.merge(orderedContent);
    IPage result = (IPage) beanManager.clone(pageService.deleteContent(poc));
    // for some reason page is not uptodate always so reopen to get delayed database changes
    result = (IPage) beanManager.clone(pageService.open(result.getId()));
    return result;
  }

//  @Override
  public IPage addPage(IPage page, IPage parent) throws SdServerEception {
    page = (IPage) beanManager.merge(page);
    parent = (IPage) beanManager.merge(parent);
    IPage result = pageService.addPage(page, parent);
    return (IPage) beanManager.clone(result);
  }

//  @Override
  public IPage update(IPage page) {
    page = (IPage) beanManager.merge(page);
    return (IPage) beanManager.clone(pageService.update(page));
  }
  
//  @Override
  public IPage moveAndUpdate(IPage subpage, Long newParentId) {
    subpage = (IPage) beanManager.merge(subpage);
    return (IPage) beanManager.clone(pageService.moveAndUpdate(subpage, newParentId));
  }

  public IPage move(Long pageId, Integer orderValue, Long newParentId) {
    Page p = (Page) pageService.move(pageId, orderValue, newParentId);
    
    // reload to have changes effective
    p = (Page) pageService.openLazy(p.getId());
    IPage result = (IPage) beanManager.clone(p);
    return result;
  }

//  @Override
  public void delete(IPage page) {
    pageService.delete(page);
  }

  public void setApplicationEventPublisher(ApplicationEventPublisher eventPublisher) {
    this.eventPublisher = eventPublisher;
  }
  
  public IPage createPage(String templateClassName, Long projectId) {
    // need to add class loading here to map DTO classes to server domain classes
    Class<? extends ITemplate> templateClass;
    ITemplate template = null;
    try {
      templateClass = (Class<? extends ITemplate>) Class.forName(templateClassName);
      template = templateClass.newInstance();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
//    template.setTemplateFields((List<IContent>) beanManager.merge(template.getTemplateFields()));
//    template.setPageProperties((Map<String, IProperty>) beanManager.merge(template.getPageProperties()));
    
    // let it crash on error
    IPage plain = pageService.createPage(template, projectId);
    IPage result = (IPage) beanManager.clone(plain);
    
//    result.setProject((IProject)beanManager.clone(plain.getProject()));
    return result;
  }

  public Map<String, String> loadNamedContentValues(Long pageId, List<String> namedItems) {
    return pageService.loadNamedContentValues(pageId, namedItems);
  }

//  @Override
//  public MoveResultDTO move(IPage subpage) {
//    MoveResultDTO result = pageService.move((IPage)merge(subpage));
//    result.subpage = (IPage) clone(result.subpage);
//    result.oldParent = (IPage) clone(result.oldParent);
//    return result;
//  }
}
