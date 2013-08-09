package net.sevenscales.server.gwt;


import java.util.List;

import net.sevenscales.domain.api.IPage;
import net.sevenscales.domain.api.IProject;
import net.sevenscales.domain.dto.ProjectPageDTO;
import net.sevenscales.server.Configuration;
import net.sevenscales.server.GwtController;
import net.sevenscales.server.domain.Project;
import net.sevenscales.server.service.IProjectService;
import net.sevenscales.serverAPI.remote.ProjectRemote;
import net.sf.hibernate4gwt.core.HibernateBeanManager;

public class ProjectRemoteImpl extends GwtController implements ProjectRemote {
  private IProjectService projectService;
  private HibernateBeanManager beanManager;
  
  public ProjectRemoteImpl() {
  }
  
  protected Configuration getConfiguration() {
    return new Configuration("applicationContext.xml");
  }
  
  public void setBeanManager(HibernateBeanManager beanManager) {
	  this.beanManager = beanManager;
  }
  
  public IProjectService getProjectService() {
    return projectService;
  }
  
  public void setProjectService(IProjectService pageService) {
    this.projectService = pageService;
  }
  
//  @Override
  public List<IProject> findAll() {
    List<IProject> result = projectService.findAll();
    result = (List<IProject>) beanManager.clone(result);
    return result;
  }
  
  public List<IProject> findAllPublicProjects() {
    List<IProject> result = projectService.findAllPublicProjects();
    result = (List<IProject>) beanManager.clone(result);
    return result;
  }

//  @Override
  public IProject save(IProject page) {
    page = (IProject) beanManager.merge(page);
    page = projectService.save(page);
    page = (IProject) beanManager.clone(page);
    return page;
  }
  
//  @Override
  public IProject update(IProject project) {
    Project merged = (Project) beanManager.merge(project);
    project = projectService.update(merged);
    return (IProject) beanManager.clone(project);
  }
  
//  @Override
  public IProject open(Long id) {
    Project page = (Project) projectService.open(id);
    return (IProject) beanManager.clone(page);
  }
  
//  @Override
//  public ProjectPageDTO addPage(IPage page, IProject project) {
//    Page mergedPage = (Page) merge(page);
//    Project mergedProject = (Project) merge(project);
//    ProjectPageDTO result = projectService.addPage(mergedPage, mergedProject);
//    return clone(result);
//  }
//  
//  @Override
//  public ProjectPageDTO addPage(IPage page, Long projectId) {
//    Page mergedPage = (Page) merge(page);
//    ProjectPageDTO result = projectService.addPage(mergedPage, projectId);
//    return clone(result);
//  }
//  
  private ProjectPageDTO clone(ProjectPageDTO projectPage) {
    projectPage.setPage((IPage) beanManager.clone(projectPage.getPage()));
    projectPage.setProject((IProject) beanManager.clone(projectPage.getProject()));
    return projectPage;
  }
}
