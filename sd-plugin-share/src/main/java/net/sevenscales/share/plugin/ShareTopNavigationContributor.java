package net.sevenscales.share.plugin;

import net.sevenscales.plugin.api.Context;
import net.sevenscales.plugin.api.ITopNavigationContributor;

public class ShareTopNavigationContributor implements ITopNavigationContributor {
  
  private Context context;

  public ShareTopNavigationContributor(Context context) {
    this.context = context;
  }

  public void addToRight(ITopNaviPanel right) {
//    Map<String, String> requests = new HashMap<String, String>();
//    requests.put(RequestId.CONTROLLER, RequestValue.LOGIN_CONTROLLER);
//    String text = "Sign In";
//    if (context.getUser() != null) {
//      text = "Sign Out";
//      
//      // add user id
//      HTML userName = new HTML("<b>" + context.getUser().getUserId() + "</b>");
//      right.insertItem(userName, 0);    
//    }
//    Action a = ActionFactory.createLinkAction(text, requests);
//    right.addItem(a);
  }

  public <T> T cast(Class<T> clazz) {
    return null;
  }

}
