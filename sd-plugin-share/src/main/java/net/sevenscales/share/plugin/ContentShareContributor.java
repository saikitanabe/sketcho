package net.sevenscales.share.plugin;

import com.google.gwt.user.client.rpc.AsyncCallback;

import net.sevenscales.domain.api.IContent;
import net.sevenscales.domain.dto.ContactDTO;
import net.sevenscales.domain.dto.ContentUpdateEventDTO;
import net.sevenscales.plugin.api.Context;
import net.sevenscales.plugin.api.IContentShareContributor;
import net.sevenscales.serverAPI.remote.ShareRemote;

// TODO: IContentShareContributor should be content edit contributor
// and contributor adds button there 
public class ContentShareContributor implements IContentShareContributor {
  
  private Context context;

  public ContentShareContributor(Context context) {
    this.context = context;
  }

  // @Override
  public void share(IContent content) {
    // select users to share content
    // - check boxes appears on right side of online users
    // - also ok and cancel buttons are needed to accept/decline users
    // - ok/cancel buttons are not needed if focus change is known
    // and correct users are selected
    
    
    // next time on save => send event that content changed for selected users
    
    // TODO: Solution 2
    // Server could be notified that this content is shared for all
    // - server holds list of contents
    // then when content is saved server creates update event to notify
    // all online users
    // - this would make below save(IContent) as obsolete
    
    // Solution 3
    // everything is shared all the time and when content
    // is saved, server generates update event
  }
  
  // @Override
  public void save(IContent result) {
    ContentUpdateEventDTO update = new ContentUpdateEventDTO();
    update.content = result;
    ContactDTO to = new ContactDTO();
    ShareRemote.Util.inst.sendEvent(to, update, new AsyncCallback() {
      // @Override
      public void onSuccess(Object result) {
        System.out.println("sendEvent success");
      }
      // @Override
      public void onFailure(Throwable caught) {
        System.out.println("sendEvent failure");
      }
    });
  }
  
  // @Override
  public <T> T cast(Class<T> clazz) {
    // TODO Auto-generated method stub
    return null;
  }
}
