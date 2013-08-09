package net.sevenscales.share.plugin.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sevenscales.appFrame.api.IContributor;
import net.sevenscales.appFrame.impl.DynamicParams;
import net.sevenscales.appFrame.impl.IController;
import net.sevenscales.appFrame.impl.ITilesEngine;
import net.sevenscales.appFrame.impl.View;
import net.sevenscales.domain.api.Member;
import net.sevenscales.domain.dto.ContactDTO;
import net.sevenscales.plugin.api.Context;
import net.sevenscales.plugin.api.UiNotifier;
import net.sevenscales.plugin.constants.TileId;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowCloseListener;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;

public class ShareView extends View<Context> implements WindowCloseListener {
  private DisclosurePanel main;
  private FlexTable listTable = new FlexTable();
  private Listener listener;
  private DecoratorPanel decorator = new DecoratorPanel();
  private SimplePanel sizerPanel = new SimplePanel();
  private Map<String,String> onlineUsers = new HashMap<String, String>();
  private Map<String,Integer> users = new HashMap<String,Integer>();
  
  public interface Listener {
    public void signOut();
  }

	public ShareView(IController<Context> controller, Listener listener) {
		super(controller);
		main = new DisclosurePanel("Project Members", true);
		main.setWidth("100%");
    sizerPanel.addStyleName("sd-plugin-share-ShareView");

		listTable.setCellPadding(0);
		listTable.setCellSpacing(0);
//		listTable.setWidth("100%");
		Context context = controller.getContext();

		main.add(listTable);
		sizerPanel.add(main);
		decorator.add(sizerPanel);

		Window.addWindowCloseListener(this);
		observBrowserCloseEvent();
		this.listener = listener;
	}
	
  public void activate(ITilesEngine tilesEngine, 
						 DynamicParams params, IContributor contributor) {
    if (controller.getContext().getProjectId() != null) {
      decorator.setVisible(true);
      tilesEngine.setTile(TileId.CONTENT_LEFT, decorator);
    } else {
      decorator.setVisible(false);
//      tilesEngine.setTile(TileId.CONTENT_LEFT, null);
    }
	}

  public void setUsers(List<Member> users, String me) {
    reset();
    System.out.println("setUsers:"+users+" "+onlineUsers);
    addUser(me);
    for (Member m : users) {
      if (!m.getUsername().equals(me)) {
        addUser(m.getUsername());
      }
    }
  }
  
  public void addUser(String user) {
    int row = listTable.getRowCount();
    users.put(user, row);
//    System.out.println("row: "+row);
    if (row == 0) {
      listTable.setText(row, 1, user);
      listTable.getRowFormatter().addStyleName(0, "meUser");
      listTable.getColumnFormatter().setWidth(1, "100%");
    } else {
      HTML userItem = new HTML(user);
      listTable.setWidget(row, 1, userItem);
    }
    
    String iconPath = onlineUsers.get(user) != null ? 
        "images/useronline.png" : "images/useroffline.png";
    final Image icon = new Image(iconPath);
    listTable.setWidget(row, 0, icon);
  }
  
  public void addOnlineUser(String user) {
    onlineUsers.put(user, user);
    Integer row = users.get(user);
    if (row != null) {
      final Image icon = new Image("images/useronline.png");
      listTable.setWidget(row, 0, icon);
    }
  }

  public void removeOnlineUser(ContactDTO contact) {
    onlineUsers.remove(contact.getUserId());
    Integer row = users.get(contact.getUserId());
    if (row != null) {
      final Image icon = new Image("images/useroffline.png");
      listTable.setWidget(row, 0, icon);
    }

//    for (int row = 1; row < listTable.getRowCount(); ++row) {
//      if (listTable.getText(row, 1).equals(contact.getUserId())) {
//        listTable.removeRow(row);
//      }
//    }
  }

  public void removeUser(ContactDTO contact) {
    users.remove(contact.getUserId());
    for (int row = 1; row < listTable.getRowCount(); ++row) {
      if (listTable.getText(row, 1).equals(contact.getUserId())) {
        listTable.removeRow(row);
      }
    }
  }
  
  // @Override
  public void onWindowClosed() {
//    listener.signOut();
  }

  // @Override
  public String onWindowClosing() {
    return null;
  }
  
  protected void handleBrowserClose() {
//    System.out.println("Browser closed");
//    listener.signOut();
  }

  private native void observBrowserCloseEvent()/*-{
    var globalThis = this;
    function browserCloseHandler() {
      globalThis.@net.sevenscales.share.plugin.view.ShareView::handleBrowserClose()();
    } 
    $wnd.onbeforeunload = browserCloseHandler;
  }-*/;

  public void reset() {
    for (int r = 0; r < listTable.getRowCount(); ++r) {
      listTable.removeRow(r);
    }
    listTable.clear();
    main.remove(listTable);
    listTable = new FlexTable();
    main.add(listTable);
    
    users.clear();
//    onlineUsers.clear();
  }
  
  public void resetOnlineUsers() {
    for (int r = 0; r < listTable.getRowCount(); ++r) {
//      listTable.clearCell(r, 0);
      final Image icon = new Image("images/useroffline.png");
      listTable.setWidget(r, 0, icon);
    }
    onlineUsers.clear();
  }

  public void disconnected() {
      // this is not always correct so show only in header
    main.getHeaderTextAccessor().setText("Reconnecting...");
//    UiNotifier.instance().showError("Connection broken. Reconnecting...");
  }

  public void setText(String string) {
    main.getHeaderTextAccessor().setText("Project Members");
    UiNotifier.instance().clear();
  }
  
}
