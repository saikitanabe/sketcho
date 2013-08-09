package net.sevenscales.login.client;

import java.util.HashMap;
import java.util.Map;

import net.sevenscales.appFrame.impl.ActionFactory;
import net.sevenscales.appFrame.impl.LinkAction;
import net.sevenscales.plugin.api.Context;
import net.sevenscales.plugin.api.ITopNavigationContributor;
import net.sevenscales.plugin.constants.ActionId;
import net.sevenscales.plugin.constants.RequestId;
import net.sevenscales.plugin.constants.RequestValue;

import com.google.gwt.user.client.ui.HTML;

public class LoginTopNavigationContributor implements ITopNavigationContributor {
  
  private Context context;
  private HTML userName;
  private LinkAction linkAction;

  public LoginTopNavigationContributor(Context context) {
    this.context = context;
  }
  
  public void addToRight(ITopNaviPanel right) {
    Map<Object, Object> requests = new HashMap<Object, Object>();
    requests.put(RequestId.CONTROLLER, RequestValue.LOGIN_CONTROLLER);
    String text = "Sign In";
    
    if (context.getUserId() != null) {
      text = "Sign Out";
      
      // if clicked => logs out
      requests.put(RequestId.ACTION_ID, String.valueOf(ActionId.LOGOUT));
      
      // add user id
      this.userName = new HTML("<b>" + context.getUserId() + "</b>");
      right.insertItem(userName, 0);    
    }
    
    this.linkAction = (LinkAction) ActionFactory.createLinkAction(text, requests);
    right.addItem(linkAction);
  }

  public <T> T cast(Class<T> clazz) {
    return null;
  }

}
