package net.sevenscales.server.gwt;


import java.util.List;

import net.sevenscales.domain.api.ILabel;
import net.sevenscales.domain.api.IPage;
import net.sevenscales.domain.api.IPageWithNamedContentValues;
import net.sevenscales.server.GwtController;
import net.sevenscales.server.dao.ILabelDAO;
import net.sevenscales.server.domain.Label;
import net.sevenscales.serverAPI.remote.LabelRemote;
import net.sf.hibernate4gwt.core.HibernateBeanManager;

import org.springframework.beans.factory.annotation.Required;

public class LabelRemoteImpl extends GwtController implements LabelRemote {
  private ILabelDAO labelDAO;
  private HibernateBeanManager beanManager;
  
  public LabelRemoteImpl() {
//    ApplicationContext.getInstance().loadConfiguration(getConfiguration());    
  }

  public void setLabelDAO(ILabelDAO labelDAO) {
    this.labelDAO = labelDAO;
  }
  
  @Required
  public void setBeanManager(HibernateBeanManager beanManager) {
    this.beanManager = beanManager;
  }

//  @Override
  public ILabel save(ILabel label) {
    label = (ILabel) beanManager.merge(label);
    ILabel result = labelDAO.save(label);
    return (ILabel) beanManager.clone(result);
  }

  public List<ILabel> findAll(Long projectId) {
    return (List<ILabel>)beanManager.clone(labelDAO.findAll(projectId));
  }
  
  public List<IPageWithNamedContentValues> findAllPages(Long labelId,
      List<String> namedItems, String filter, String sort) {
//    Label label = (Label) labelDAO.open(labelId);
    try {
      beanManager.getPersistenceUtil().openSession();
      Label label = (Label) beanManager.getPersistenceUtil().load(labelId, Label.class);
      return (List<IPageWithNamedContentValues>)beanManager.clone(
          labelDAO.findAllPages2(label, namedItems, filter, sort));
    } finally {
      beanManager.getPersistenceUtil().closeCurrentSession();
    }
  }
  
//  public SuggestOracle.Response findAll(SuggestOracle.Request request) {
//    return adminService.findAll(request);
//  }
  
  public void removeFromPage(ILabel label, Long pageId) {
    labelDAO.removeFromPage(label, pageId);
  }

//  @Override
  public void remove(ILabel label) {
    labelDAO.remove((ILabel)beanManager.merge(label));
  }

  public ILabel addPageToLabel(ILabel label, IPage page) {
    ILabel result = (ILabel) beanManager.clone(labelDAO.addPageToLabel(
        label, (IPage) beanManager.merge(page)));
    return result;
  }

  public ILabel open(Long id) {
    return (ILabel) beanManager.clone(labelDAO.open(id));
  }

  public ILabel update(ILabel label) {
    return (ILabel) beanManager.clone(labelDAO.update((ILabel)beanManager.merge(label)));
  }
  
}
