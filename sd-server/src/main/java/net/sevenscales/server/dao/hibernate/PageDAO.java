package net.sevenscales.server.dao.hibernate;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import net.sevenscales.domain.api.IContent;
import net.sevenscales.domain.api.ILabel;
import net.sevenscales.domain.api.IListContent;
import net.sevenscales.domain.api.IPage;
import net.sevenscales.domain.api.IPageOrderedContent;
import net.sevenscales.domain.api.IPageWithNamedContentValues;
import net.sevenscales.domain.api.IProperty;
import net.sevenscales.domain.api.ITemplate;
import net.sevenscales.domain.api.ITextContent;
import net.sevenscales.domain.api.ITextLineContent;
import net.sevenscales.domain.api.SketchesSearch;
import net.sevenscales.domain.constants.Constants;
import net.sevenscales.domain.dto.SdServerEception;
import net.sevenscales.server.dao.IPageDAO;
import net.sevenscales.server.domain.ContentProperty;
import net.sevenscales.server.domain.DiagramContent;
import net.sevenscales.server.domain.Label;
import net.sevenscales.server.domain.Page;
import net.sevenscales.server.domain.PageOrderedContent;
import net.sevenscales.server.domain.PageProperty;
import net.sevenscales.server.domain.PageWithNamedContentValues;
import net.sevenscales.server.domain.Project;

import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;
import org.hibernate.Hibernate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

public class PageDAO extends HibernateDaoSupport implements IPageDAO {
  
//  @Override
  @Transactional(propagation=Propagation.SUPPORTS)
  public List<IPage> findAll() {
    List<IPage> pages = (List<IPage>) getHibernateTemplate().findByNamedParam(
        "from page p where p.type=:type or p.type is NULL", "type", Constants.PAGE_TYPE_DOC);
    return pages;
  }
  
  @Transactional(propagation=Propagation.SUPPORTS)
  public List<IPage> findAll(Long projectId, Integer type) {
    String[] names = new String[]{"projectId", "type"};
    Object[] values = new Object[]{projectId, type}; 
    List<IPage> sketches = (List<IPage>) getHibernateTemplate().findByNamedParam(
        "from page p where p.project.id=:projectId and p.type=:type order by p.name asc", names, values);
    return sketches;
  }
  
  @Transactional(propagation=Propagation.SUPPORTS)
  public List<IPageWithNamedContentValues> findAll(Long projectId, String filterOption, String order, List<String> namedItems) {
    String[] names = new String[]{"projectId", "type"};
    Object[] values = new Object[]{projectId, Constants.PAGE_TYPE_SKETCH}; 
    List<Page> sketches = (List<Page>) getHibernateTemplate().findByNamedParam(
        "from page p where p.project.id=:projectId and p.type=:type order by p.name asc", names, values);
    return filterAndSort(sketches, namedItems, filterOption, order);
  }
  
  @Transactional(propagation=Propagation.SUPPORTS)
  public List<IPage> findAll(Long projectId) {
    return (List<IPage>) getHibernateTemplate().findByNamedParam(
        "from page p where p.project.id=:projectId", "projectId", projectId);
  }
  
  @Transactional(propagation=Propagation.SUPPORTS)
  public List<IPageWithNamedContentValues> findAllWithNamedContentValues(Long projectId, Integer type, List<String> namedItems) {
    List<IPage> pages = findAll(projectId, type);
    List<IPageWithNamedContentValues> result = new ArrayList<IPageWithNamedContentValues>();
    
    for (IPage page : pages) {
      Map<String, String> namedValues = loadNamedContentValues(page.getId(), namedItems);
      PageWithNamedContentValues pageNamedContent = new PageWithNamedContentValues();
      pageNamedContent.setPage(page);
      pageNamedContent.setNamedContentValues(namedValues);
      result.add(pageNamedContent);
    }
    return result;
  }
  
  @Transactional(propagation=Propagation.SUPPORTS)
  public List<IPageWithNamedContentValues> findAllWithNamedContentValues(
        Long projectId, Integer type, List<String> namedItems, Integer max,
        String where, String orderBy) {
    if (orderBy != null && orderBy.length() > 0) {
      orderBy = "order by p." + orderBy;
    }
    
    if (where != null && where.length() > 0) {
      where = "and p." + where;
    }
    
    if (max != null) {
      getHibernateTemplate().setMaxResults(max);
    }

    String[] names = new String[]{"projectId", "type"};
    Object[] values = new Object[]{projectId, type};
    String query = String.format("from page p where p.project.id=:projectId %s and p.type=:type %s", where, orderBy);
    List<IPage> sketches = (List<IPage>) getHibernateTemplate().findByNamedParam(query, names, values);
    
    // reset max counter
    getHibernateTemplate().setMaxResults(0);

    List<IPageWithNamedContentValues> result = new ArrayList<IPageWithNamedContentValues>();
    
    for (IPage page : sketches) {
      Map<String, String> namedValues = loadNamedContentValues(page, namedItems);
      PageWithNamedContentValues pageNamedContent = new PageWithNamedContentValues();
      pageNamedContent.setPage(page);
      pageNamedContent.setNamedContentValues(namedValues);
      result.add(pageNamedContent);
    }


//    List<IPageWithNamedContentValues> pageContent = findAllWithNamedContentValues(projectId, type, namedItems);

//    List<ITicket> result = getHibernateTemplate().findByNamedParam
//      (String.format("from Ticket t where t.project=:project %s %s", where, orderBy), keys, values);
//    getHibernateTemplate().setMaxResults(0);
    
    // TODO: there should be requested a list of fields and then some content
    // wouldn't needt be initialized => performance
//    for (ITicket t : result) {
//      Hibernate.initialize(t.getDescription());
//    }
    
    return result;
  }

  @Transactional(propagation=Propagation.SUPPORTS)
  public List<IPageWithNamedContentValues> findAllWithNamedContentValues(
        Long projectId, String filterOption, Integer type, List<String> namedItems, Integer max,
        String where, String orderBy) {
    long start = System.currentTimeMillis();
    if (orderBy != null && orderBy.length() > 0) {
      orderBy = "order by p." + orderBy;
    }
    
    if (where != null && where.length() > 0) {
      where = "and p." + where;
    }
    
    if (max != null) {
      getHibernateTemplate().setMaxResults(max);
    }

    String[] names = new String[]{"projectId", "type"};
    Object[] values = new Object[]{projectId, type};
    String query = String.format("from page p where p.project.id=:projectId %s and p.type=:type %s", where, orderBy);
    List<IPage> sketches = (List<IPage>) getHibernateTemplate().findByNamedParam(query, names, values);
    
    // reset max counter
    getHibernateTemplate().setMaxResults(0);

    List<IPageWithNamedContentValues> result = new ArrayList<IPageWithNamedContentValues>();
    
    for (IPage page : sketches) {
      Map<String, String> namedValues = loadNamedContentValues(page, namedItems);
      String status = namedValues.get(Constants.SKETCH_STATUS);
      PageWithNamedContentValues pageNamedContent = new PageWithNamedContentValues();
      pageNamedContent.setPage(page);
      pageNamedContent.setNamedContentValues(namedValues);
      if (status != null && filterOption.equals(SketchesSearch.TEXT_OPEN_SKETCHES) && openStatus(status)) {
        result.add(pageNamedContent);
      } else if (filterOption.equals(SketchesSearch.TEXT_ALL_SKETCHES)) {
        result.add(pageNamedContent);
      }
    }

    System.out.println("find: "+(System.currentTimeMillis() - start));

    return result;
  }
  
  private boolean openStatus(String status) {
    String[] closedStatuses = new String[] {"Designed","Discarded"};
    for (String s : closedStatuses) {
      if (status.equals(s)) {
        return false;
      }
    }
    return true;
  }


//  @Override
  @Transactional(propagation=Propagation.REQUIRED)
  public IPage save(IPage page) {
    Long time = System.currentTimeMillis();
    page.setCreatedTime(time);
    Authentication a = SecurityContextHolder.getContext().getAuthentication();
    page.setCreator(a.getName());
    
    page.setModifiedTime(time);
    page.setModifier(a.getName());
    
    getHibernateTemplate().save(page);
    return page;
  }
  
//  @Override
  @Transactional(propagation=Propagation.SUPPORTS)
  public IPage open(Long id) {
    Page result = (Page) getHibernateTemplate().get(Page.class, id);
    Hibernate.initialize(result.getContentItems());
    for (PageOrderedContent c : result.getContentItems()) {
      if (c.getContent() instanceof DiagramContent) {
        DiagramContent dc = (DiagramContent) c.getContent();
        Hibernate.initialize(dc.getDiagramItems());
      }
    }
    
    return result;
  }
  
  public Page openLazy(Long id) {
    return (Page) getHibernateTemplate().get(Page.class, id);
  }

//  @Override
  @Transactional(propagation=Propagation.REQUIRED)
  public IPage addPage(IPage page, IPage parent) throws SdServerEception {
    if (parent == null || parent.getId() == null) {
      throw new SdServerEception("Parent page doesn't exist");
    }
    page.setOrderValue(Integer.MAX_VALUE);
    page.setParent(parent);
    page.setProject(parent.getProject());
    save(page);

    return page;
  }

//  @Override
  @Transactional(propagation=Propagation.REQUIRED)
  public IPageOrderedContent addContent(IPageOrderedContent orderedContent) {
    assert(orderedContent.getContent().getId() == null);
//    Content persistedContent = (Content) content;
//    Integer order = new Integer(orderedContent.getPage().getContentItems().size() + 1);

//    PageOrderedContent orderedContent = new PageOrderedContent();
//    persistedContent.setParent( (Page) page);
//    getHibernateTemplate().save(persistedContent);
//    orderedContent.setContent(persistedContent);
    
    // if there is a persistent instance with the same identifier 
    // currently associated with the session, copy the state of 
    // the given object onto the persistent instance 
    Map<String,ContentProperty> map = (Map<String, ContentProperty>) orderedContent.getContent().getProperties();
    for (Entry<String,ContentProperty> e : map.entrySet()) {
      getHibernateTemplate().save(e.getValue());
    }
    getHibernateTemplate().save(orderedContent);

    resetPageContentItemsOrderValues(orderedContent.getPage());
    orderedContent.setOrderValue(orderedContent.getPage().getContentItems().size() + 1);
    orderedContent.getPage().getContentItems().add(orderedContent);
    getHibernateTemplate().update(orderedContent);
//    getHibernateTemplate().update(orderedContent.getPage());

//    PageContentDTO result = new PageContentDTO();
//    result.setContent(persistedContent);
//    result.setPage(page);
    return orderedContent;
  }
  
  private void resetPageContentItemsOrderValues(IPage page) {
    // lets reset all order values in case there is a bug somewhere
    int i = 1;
    for (IPageOrderedContent oc : (Set<IPageOrderedContent>) page.getContentItems()) {
      oc.setOrderValue(i++);
      getHibernateTemplate().update(oc);
    }
  }

//  @Override
  @Transactional(propagation=Propagation.REQUIRED)
  public IPageOrderedContent updateContent(IPageOrderedContent orderedContent) {
    boolean updateModified = true;
    if (orderedContent.getContent() instanceof IListContent) {
      IListContent lc = (IListContent) orderedContent.getContent();
      
      // do not update modified time if sketch status is updated
      updateModified = !lc.getName().equals(Constants.SKETCH_STATUS);
    }
    
    // content and page will have same time
    Long time = System.currentTimeMillis();
    if (updateModified) {
      update(orderedContent.getPage(), time);
    } else {
      // do not update with time
      update(orderedContent.getPage(), new Long(0));
    }
        
    // content modified time and modifier is always updated
    orderedContent.getContent().setModifiedTime(time);
    Authentication a = SecurityContextHolder.getContext().getAuthentication();
    orderedContent.getContent().setModifier(a.getName());

    getHibernateTemplate().update(orderedContent);
    return orderedContent;
  }

  @Transactional(propagation=Propagation.REQUIRED)
  public IPageOrderedContent moveContent(IPageOrderedContent content,
        Integer newPos) {
    IPage parent = content.getPage();
    List<IPageOrderedContent> contents = new ArrayList<IPageOrderedContent>(parent.getContentItems());
    
    if (newPos-1 >= 0) {
      contents.remove(content);
      contents.add(newPos-1, content);
    }
    
    int i = 1;
    for (IPageOrderedContent poc : contents) {
      poc.setOrderValue(i++);
      getHibernateTemplate().update(poc);
    }
    getHibernateTemplate().update(parent);
    return content;
  }

//  @Override
  @Transactional(propagation=Propagation.REQUIRED)
  public IPage deleteContent(IPageOrderedContent orderedContent) {
//    PageOrderedContent poc = (PageOrderedContent) getHibernateTemplate()
//      .get(PageOrderedContent.class, orderedContentId);
    IPage page = orderedContent.getPage();
    page.getContentItems().remove(orderedContent);
    getHibernateTemplate().delete(orderedContent);
//    getHibernateTemplate().evict(poc);
    getHibernateTemplate().update(page);
    
    int i = 1;
    for (IPageOrderedContent oc : (Set<IPageOrderedContent>) page.getContentItems()) {
      if (!orderedContent.equals(oc)) {
        oc.setOrderValue(i++);
        getHibernateTemplate().update(oc);
      }
    }

//    resetPageContentItemsOrderValues(page);

    return page;
  }
  
//  @Override
  @Transactional(propagation=Propagation.REQUIRED)
  public IPage update(IPage page) {
    return update(page, System.currentTimeMillis());
  }
  
  private IPage update(IPage page, Long time) {
    if (time > 0) {
      Authentication a = SecurityContextHolder.getContext().getAuthentication();
      
      page.setModifiedTime(time);
      page.setModifier(a.getName());
    }

    getHibernateTemplate().update(page);
    for (IPageOrderedContent oc : (Set<IPageOrderedContent>) page.getContentItems()) {
      getHibernateTemplate().update(oc);
    }
    return page;
  }

//  @Override
  @Transactional(propagation=Propagation.REQUIRED)
  public IPage moveAndUpdate(IPage subpage, Long newParentId) {
    getHibernateTemplate().update(subpage);

    Page newParent = (Page) getHibernateTemplate().get(Page.class, newParentId);
    subpage.setParent(newParent);

    updatePageOrders(subpage, newParent);
    getHibernateTemplate().update(newParent);
    
    // assert
    List<Page> pages = getHibernateTemplate().loadAll(Page.class);
    for (Page p : pages) {
      if (!p.getProject().getDashboard().equals(p)) {
        Assert.notNull(p.getParent());
      }
    }
    
    return subpage;
  }
  
  @Transactional(propagation=Propagation.REQUIRED)
  public IPage move(Long pageId, Integer orderValue, Long newParentId) {
      Page move = (Page) getHibernateTemplate().get(Page.class, pageId);

      Page parent = move.getParent();
      move.setOrderValue(orderValue);
      
      if (!parent.getId().equals(newParentId)) {
        move.setParent(null);
//        parent.getSubpages().remove(move);
//        getHibernateTemplate().update(parent);
//        getHibernateTemplate().flush();
//        getHibernateTemplate().clear();
        parent = (Page) getHibernateTemplate().get(Page.class, newParentId);
      }

      move.setParent(parent);
      updatePageOrders(move, parent);
      getHibernateTemplate().update(move);
//      getHibernateTemplate().update(parent);
      
      return move;
  }

  private void updatePageOrders(IPage subpage, IPage newParent) {
    // reorder always if parent exists just in case order has changed
    List<IPage> subpages = new ArrayList<IPage>(newParent.getSubpages());

    subpages.remove(subpage);
    int index = subpage.getOrderValue() - 1;
    if (index >= 0 && index < subpages.size()) {
      subpages.add(index, subpage);
    } else {
      subpages.add(subpage);
    }

    int i = 0;
    for (IPage s : subpages) {
      s.setOrderValue(++i);
      getHibernateTemplate().update(s);
    }
    
//    Hibernate.initialize(newParent.getSubpages());
//    if (newParent.getId().equals(subpage.getParent().getId())) {
//      newParent.getSubpages().add( (Page) subpage);
//    }
    subpage.setParent(newParent);
  }

//  @Override
  @Transactional(propagation=Propagation.REQUIRED)
  public void delete(IPage page) {
//    // delete all sub pages
//    for (Page p : (Set<Page>) page.getSubpages()) {
//      getHibernateTemplate().delete(p);
//    }
    // delete page itself
    Page pp = (Page) getHibernateTemplate().get(Page.class, page.getId());
    for (Label l : (List<Label>)pp.getLabels()) {
      l.getPages().remove(pp);
    }
    
//    label = (ILabel) getHibernateTemplate().get(Label.class, label.getId());
//    Page page = (Page) getHibernateTemplate().get(Page.class, pageId);
//    label.getPages().remove(page);

    getHibernateTemplate().delete(pp);
  }
  
  public IPage createPage(String templateClassName, Long projectId) {
    return null;
  }
  
  public Page createPage(ITemplate template, Long projectId) {
    Page page = new Page();
    page.setType(template.getPageType());
    Project project = (Project) getHibernateTemplate().get(Project.class, projectId);
    page.setProject(project);
    save(page);
    String pageName = template.generateName(page);
    page.setName(pageName);
    
    List<IContent> templateFields = template.getTemplateFields();
    for (IContent c : templateFields) {
      PageOrderedContent poc = new PageOrderedContent();
      poc.setContent(c);
      poc.setPage(page);
      addContent(poc);
    }
    
    for (Entry<String, IProperty> e : template.getPageProperties().entrySet()) {
      PageProperty p = (PageProperty) e.getValue();
      getHibernateTemplate().save(p);
      page.getProperties().put(e.getKey(), p);
    }
    
    getHibernateTemplate().update(page);
    return page;
  }
  
  @Transactional(propagation=Propagation.SUPPORTS)
  public Map<String, String> loadNamedContentValues(Long pageId, List<String> namedItems) {
    IPage page = (IPage) getHibernateTemplate().get(Page.class, pageId);
    return loadNamedContentValues(page, namedItems);
  }
  
  public Map<String, String> loadNamedContentValues(IPage page, List<String> namedItems) {
    Map<String, String> result = new HashMap<String, String>();
    Hibernate.initialize(page.getContentItems());
    for (IPageOrderedContent c : (Set<IPageOrderedContent>) page.getContentItems()) {
      for (String name : namedItems) {
        if (name.equals(c.getContent().getName())) {
          if (c.getContent() instanceof ITextContent) {
            ITextContent tc = (ITextContent)c.getContent();
            result.put(name, tc.getText());
          } else if (c.getContent() instanceof ITextLineContent) {
            ITextLineContent tc = (ITextLineContent)c.getContent();
            result.put(name, tc.getText());
          } else if (c.getContent() instanceof IListContent) {
            IListContent tc = (IListContent)c.getContent();
            result.put(name, tc.getValue());
          }
          break;
        }
      }
    }
    return result;
  }
  
  public List<IPageWithNamedContentValues> filterAndSort(
      List<Page> sketches, List<String> namedItems, String filter, String sort) { 
    List<IPageWithNamedContentValues> result = new ArrayList<IPageWithNamedContentValues>();
    for (IPage page : sketches) {
      Map<String, String> namedValues = loadNamedContentValues(page, namedItems);
      String status = namedValues.get(Constants.SKETCH_STATUS);
      PageWithNamedContentValues pageNamedContent = new PageWithNamedContentValues();
      pageNamedContent.setPage(page);
      pageNamedContent.setNamedContentValues(namedValues);
      if (status != null && filter.equals(SketchesSearch.TEXT_OPEN_SKETCHES) && openStatus(status)) {
        result.add(pageNamedContent);
      } else if (filter.equals(SketchesSearch.TEXT_ALL_SKETCHES)) {
        result.add(pageNamedContent);
      }
    }
    
    if (sort.equals(SketchesSearch.TEXT_SORT_BY_LABELS)) {
      Collections.sort(result, new Comparator<IPageWithNamedContentValues>() {
        public int compare(IPageWithNamedContentValues o1, IPageWithNamedContentValues o2) {
          int comparison = 0;
          int i = 0;
          while (comparison == 0) {
            if (i >= o1.getPage().getLabels().size()) {
              return 1;
            }
            ILabel l1 = (ILabel) o1.getPage().getLabels().get(i);
            
            if (i >= o2.getPage().getLabels().size()) {
              return -1;
            }
            ILabel l2 = (ILabel) o2.getPage().getLabels().get(i);
            comparison = l1.getOrderValue().compareTo(l2.getOrderValue());
            ++i;
          }
          return comparison;
        }
      });
    }
    return result;
  }

}
