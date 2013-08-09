package net.sevenscales.server.service.impl;


import java.util.List;
import java.util.Map;

import net.sevenscales.domain.api.IPage;
import net.sevenscales.domain.api.IPageOrderedContent;
import net.sevenscales.domain.api.IPageWithNamedContentValues;
import net.sevenscales.domain.api.ITemplate;
import net.sevenscales.domain.dto.SdServerEception;
import net.sevenscales.server.dao.IPageDAO;
import net.sevenscales.server.domain.Page;
import net.sevenscales.server.service.IPageService;

public class PageService implements IPageService {
  private IPageDAO pageDAO;

  public void setPageDAO(IPageDAO pageDAO) {
    this.pageDAO = pageDAO;
  }
  
  public IPageDAO getPageDAO() {
    return pageDAO;
  }
  
//  @Override
  public List<IPage> findAll() {
    return pageDAO.findAll();
  }
  
  public List<IPage> findAll(Long projectId, Integer type) {
    return pageDAO.findAll(projectId, type);
  }
  public List<IPageWithNamedContentValues> findAll(Long projectId, String filterOption, String order, List<String> namedItems) {
    return pageDAO.findAll(projectId, filterOption, order, namedItems);
  }
  
  public List<IPageWithNamedContentValues> findAllWithNamedContentValues(
      Long projectId, Integer type, List<String> namedItems) {
    return pageDAO.findAllWithNamedContentValues(projectId, type, namedItems);
  }
  
  public List<IPageWithNamedContentValues> findAllWithNamedContentValues(
      Long projectId, Integer type, List<String> namedItems, Integer max,
      String where, String orderBy) {
    return pageDAO.findAllWithNamedContentValues(projectId, type, namedItems, max, where, orderBy);
  }

  public List<IPageWithNamedContentValues> findAllWithNamedContentValues(
      Long projectId, String filterOption, Integer type, List<String> namedItems, Integer max,
      String where, String orderBy) {
    return pageDAO.findAllWithNamedContentValues(projectId, filterOption, type, namedItems, max, where, orderBy);
  }

//  @Override
  public IPage save(IPage page) {
    return pageDAO.save(page);
  }
  
//  @Override
  public IPage open(Long id) {
    return pageDAO.open(id);
  }
  
  public Page openLazy(Long id) {
    return pageDAO.openLazy(id);
  }
  
//  @Override
  public IPageOrderedContent addContent(IPageOrderedContent orderedContent) {
    return pageDAO.addContent(orderedContent);
  }
  
  public IPageOrderedContent moveContent(IPageOrderedContent content,
      Integer newPos) {
    return pageDAO.moveContent(content, newPos);
  }

//  @Override
  public IPageOrderedContent updateContent(IPageOrderedContent content) {
    return pageDAO.updateContent(content);
  }
  
//  @Override
  public IPage deleteContent(IPageOrderedContent orderedContent) {
    return pageDAO.deleteContent(orderedContent);
  }
  
//  @Override
  public IPage addPage(IPage page, IPage parent) throws SdServerEception {
    return pageDAO.addPage(page, parent);
  }
  
//  @Override
  public IPage update(IPage currentSubpage) {
    return pageDAO.update(currentSubpage);
  }
  
//  @Override
  public IPage moveAndUpdate(IPage subpage, Long newParentId) {
    return pageDAO.moveAndUpdate(subpage, newParentId);
  }
  
  public IPage move(Long pageId, Integer orderValue, Long newParentId) {
    IPage result = pageDAO.move(pageId, orderValue, newParentId);
//    
//    // assert
//    List<IPage> pages = pageDAO.findAll();
//    for (IPage p : pages) {
//      if (!p.getName().equals("Dashboard")) {
//        Assert.notNull(p.getParent());
//      }
//    }

    return result;
  }
  
//  @Override
  public void delete(IPage page) {
    pageDAO.delete(page);
  }
  
  public IPage createPage(String templateClassName, Long projectId) {
    return pageDAO.createPage(templateClassName, projectId);
  }
  
  public Page createPage(ITemplate template, Long projectId) {
    return pageDAO.createPage(template, projectId);
  }
  
  public Map<String, String> loadNamedContentValues(Long pageId, List<String> namedItems) {
    return pageDAO.loadNamedContentValues(pageId, namedItems);
  }
  
//  @Override
//  public MoveResultDTO move(IPage subpage) {
//    return pageDAO.move(subpage);
//  }
}
