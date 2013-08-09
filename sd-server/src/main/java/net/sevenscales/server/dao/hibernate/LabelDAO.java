package net.sevenscales.server.dao.hibernate;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sevenscales.domain.api.ILabel;
import net.sevenscales.domain.api.IPage;
import net.sevenscales.domain.api.IPageWithNamedContentValues;
import net.sevenscales.domain.constants.Constants;
import net.sevenscales.server.dao.ILabelDAO;
import net.sevenscales.server.dao.IPageDAO;
import net.sevenscales.server.domain.Label;
import net.sevenscales.server.domain.Page;
import net.sevenscales.server.domain.PageWithNamedContentValues;
import net.sevenscales.server.domain.Project;

import org.hibernate.Hibernate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class LabelDAO extends HibernateDaoSupport implements ILabelDAO {
  private IPageDAO pageDAO;
  
  public void setPageDAO(IPageDAO pageDAO) {
    this.pageDAO = pageDAO;
  }
  
//  @Override
  @Transactional(propagation=Propagation.REQUIRED)
  public ILabel save(ILabel label) {
    Project project = (Project) getHibernateTemplate().get(Project.class, label.getProject().getId());
    List<Label> labels = (List<Label>) getHibernateTemplate().findByNamedParam
      ("from label l where l.project=:project order by l.value asc", "project", project);

    label.setOrderValue( (labels.size()+1)*5);
    getHibernateTemplate().save(label);
    return label;
  }
  
//  @Override
  @Transactional(propagation=Propagation.REQUIRED)
  public ILabel update(ILabel label) {
    getHibernateTemplate().update(label);
    return label;
  }
  
//  @Override
  @Transactional(propagation=Propagation.SUPPORTS)
  public ILabel open(Long id) {
    Label result = (Label) getHibernateTemplate().get(Label.class, id);
    Hibernate.initialize(result);
    return result;
  }

  @SuppressWarnings("unchecked")
  @Transactional(propagation=Propagation.SUPPORTS)
  public List<ILabel> findAll(Long projectId) {
    Project project = (Project) getHibernateTemplate().get(Project.class, projectId);
    List<ILabel> result = getHibernateTemplate().findByNamedParam
      ("from label l where l.project=:project order by l.orderValue asc", "project", project);
    return result;
  }
  
  @Transactional(propagation=Propagation.SUPPORTS)
  public List<IPageWithNamedContentValues> findAllPages(Long labelId,
        List<String> namedItems, String filter, String sort) {
    return null;
  }

  @Override
  @Transactional(propagation=Propagation.SUPPORTS)
  public List<IPageWithNamedContentValues> findAllPages2(Label label,
      List<String> namedItems, String filter, String sort) {
    return pageDAO.filterAndSort(label.getPages(), namedItems, filter, sort);
//    List<IPageWithNamedContentValues> result = new ArrayList<IPageWithNamedContentValues>();
//
//    for (Page page : label.getPages()) {
//      Map<String, String> namedValues = pageDAO.loadNamedContentValues(page, namedItems);
//      String status = namedValues.get(Constants.SKETCH_STATUS);
//      PageWithNamedContentValues pageNamedContent = new PageWithNamedContentValues();
//      pageNamedContent.setPage(page);
//      pageNamedContent.setNamedContentValues(namedValues);
//      result.add(pageNamedContent);
//    }
//
//    return result;
  }
  
  @Transactional(propagation=Propagation.REQUIRED)
  public ILabel addPageToLabel(ILabel label, IPage page) {
    Label l = (Label) getHibernateTemplate().get(Label.class, label.getId());
    List<Page> pages = l.getPages();
    if (!pages.contains(page)) {
      pages.add((Page) page);  
    }
    return l;
  }
  
  @Transactional(propagation=Propagation.REQUIRED)
  public void removeFromPage(ILabel label, Long pageId) {
    label = (ILabel) getHibernateTemplate().get(Label.class, label.getId());
    Page page = (Page) getHibernateTemplate().get(Page.class, pageId);
    label.getPages().remove(page);
  }

  @Transactional(propagation=Propagation.REQUIRED)
  public void remove(ILabel label) {
    getHibernateTemplate().delete(label);
  }

  
}
