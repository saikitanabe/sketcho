package net.sevenscales.server.gwt;


import net.sevenscales.domain.api.IEvent;
import net.sevenscales.domain.dto.ContactDTO;
import net.sevenscales.domain.dto.MessageDTO;
import net.sevenscales.server.Configuration;
import net.sevenscales.server.GwtController;
import net.sevenscales.server.service.IShareService;
import net.sevenscales.serverAPI.remote.ShareRemote;
import net.sf.hibernate4gwt.core.HibernateBeanManager;

public class ShareRemoteImpl extends GwtController implements ShareRemote {
  private IShareService shareService;
  private HibernateBeanManager beanManager;
  
  public ShareRemoteImpl() {
  }
  
  public void setBeanManager(HibernateBeanManager beanManager) {
	  this.beanManager = beanManager;
  }
  
  protected Configuration getConfiguration() {
    return new Configuration("applicationContext.xml");
  }

  public IShareService getShareService() {
    return shareService;
  }  
  public void setShareService(IShareService shareService) {
    this.shareService = shareService;
  }
  
  public void sendEvent(ContactDTO to, IEvent event) {
    // TODO Auto-generated method stub
    
  }
  
//  @Override
  public void sendMessage(ContactDTO to, MessageDTO message) {
    shareService.sendMessage(getThreadLocalRequest().getSession().getId(), to, message);
  }

}
