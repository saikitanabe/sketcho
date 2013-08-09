package net.sevenscales.server.dao.hibernate;


import java.util.List;

import net.sevenscales.domain.api.IPage;
import net.sevenscales.domain.api.IProject;
import net.sevenscales.server.dao.IProjectDAO;
import net.sevenscales.server.domain.Page;
import net.sevenscales.server.domain.PageOrderedContent;
import net.sevenscales.server.domain.Project;
import net.sevenscales.server.domain.TextContent;

import org.hibernate.Hibernate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class ProjectDAO extends HibernateDaoSupport implements IProjectDAO {
  
  @SuppressWarnings("unchecked")
//  @Override
  @Transactional(propagation=Propagation.SUPPORTS)
  public List<IProject> findAll() {
    List<IProject> result = getHibernateTemplate().find("from project p order by p.name asc");

    return result;
  }
  
  public List<IProject> findAllPublicProjects() {
    List<IProject> result = getHibernateTemplate().find("from project p where p.publicProject=true order by p.name asc");
    return result;
  }
  
//  @Override
  @Transactional(propagation=Propagation.REQUIRED)
  public IProject save(IProject project) {
//    Page result = (Page) getHibernateTemplate().get(Page.class, new Long(1));
//    if (result == null) {
//      // currently like this, probably this should be saved
//      // to project
//      result = new Page();
//      getHibernateTemplate().save(result);
//    }
    Page page = new Page();
    page.setName("Dashboard");
    page.setProject(project);
    
    getHibernateTemplate().save(project);
    project.setDashboard(page);
    getHibernateTemplate().save(page);
    
    TextContent dashTemplate = new TextContent();
    String text = "This is a project dashboard. Sketches macro shows latest sketches. " +
    		          "Enable edit mode from the toolbar by clicking \"edit\". " +
    		          "To edit content in edit mode double click highlighted content. " +
    		          "Add new content to the page from the toolbar.<p>" +
    		          "<b>Read more</b> <a href='http://7scales.net/7scales/sketcho_introduction.html'>Sketcho Introduction</a>" +
    		          "<p>";
    text += "[[SKETCHES(Title,Status,modifier,modifiedTime,max:5,order:modifiedTime desc)]]";
    dashTemplate.setText(text);
    PageOrderedContent poc = new PageOrderedContent();
    poc.setContent(dashTemplate);
    poc.setOrderValue(1);
    poc.setPage(page);
    getHibernateTemplate().save(dashTemplate);
    getHibernateTemplate().save(poc);
    page.getContentItems().add(poc);
    
    return project;
  }
  
//  @Override
  @Transactional(propagation=Propagation.REQUIRED)
  public IProject update(IProject project) {
    getHibernateTemplate().update(project);
    getHibernateTemplate().flush();
    return project;
  }
  
//  @Override
  @Transactional(propagation=Propagation.SUPPORTS)
  public IProject open(Long id) {
    Project p = (Project) getHibernateTemplate().get(Project.class, id);
    Hibernate.initialize(p.getDashboard().getSubpages());
    
    for (IPage sp : p.getDashboard().getSubpages()) {
      Hibernate.initialize(sp);
    }

    return p;
  }
  
//  @Override
//  @Transactional(propagation=Propagation.REQUIRED)
//  public ProjectPageDTO addPage(IPage page, IPage parent) {
//    getHibernateTemplate().save(page);
//    Project persistedProject = (Project) project;
//    persistedProject.getPages().add( (Page) page);
    
    // if there is a persistent instance with the same identifier 
    // currently associated with the session, copy the state of 
    // the given object onto the persistent instance 
//    getHibernateTemplate().merge(project);
//
//    ProjectPageDTO result = new ProjectPageDTO();
//    result.setPage(page);
//    result.setProject(persistedProject);
//    return result;
//  }
  
//  @Override
//  @Transactional(propagation=Propagation.REQUIRED)
//  public ProjectPageDTO addPage(IPage page, Long projectId) {
//    Project persistedProject = (Project) getHibernateTemplate()
//      .get(Project.class, projectId);
//    return addPage(page, persistedProject);
//  }
}
