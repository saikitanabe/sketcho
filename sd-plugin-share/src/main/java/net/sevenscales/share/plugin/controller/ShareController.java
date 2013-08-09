package net.sevenscales.share.plugin.controller;

import java.util.List;
import java.util.Map;

import net.sevenscales.appFrame.api.IContributor;
import net.sevenscales.appFrame.api.IRegistryEventObserver;
import net.sevenscales.appFrame.impl.Action;
import net.sevenscales.appFrame.impl.ActivationObserver;
import net.sevenscales.appFrame.impl.ClassInfo;
import net.sevenscales.appFrame.impl.ControllerBase;
import net.sevenscales.appFrame.impl.DynamicParams;
import net.sevenscales.appFrame.impl.Handler;
import net.sevenscales.appFrame.impl.ITilesEngine;
import net.sevenscales.appFrame.impl.View;
import net.sevenscales.domain.api.IContent;
import net.sevenscales.domain.api.IEvent;
import net.sevenscales.domain.api.Member;
import net.sevenscales.domain.dto.ContentUpdateEventDTO;
import net.sevenscales.plugin.api.Context;
import net.sevenscales.plugin.constants.SdRegistryEvents;
import net.sevenscales.plugin.constants.TileId;
import net.sevenscales.serverAPI.remote.AdminRemote;
import net.sevenscales.serverAPI.remote.ContentRemote;
import net.sevenscales.share.plugin.controller.Cometd.ChannelListener;
import net.sevenscales.share.plugin.view.ShareView;
import net.sevenscales.share.plugin.view.ShareView.Listener;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;

public class ShareController extends ControllerBase<Context> implements IRegistryEventObserver, Listener, ChannelListener {

	private ShareView shareView;
	private View dummyView;
  private Cometd cometd;
	
  public static ClassInfo info() {
    return new ClassInfo() {
      public Object createInstance(Object data) {
        return new ShareController((Context) data);
      }

      public Object getId() {
        return ShareController.class;
      }
      
//      public boolean isGlobal() {
//        return true;
//      }
    };
  }
	
	private ShareController(Context context) {
	  super(context);
		dummyView = new View(this) {
		  private HTML singIn = new HTML("Sign In");
		  public void activate(ITilesEngine tilesEngine, DynamicParams params,
		      IContributor contributor) {
		    tilesEngine.clear(TileId.CONTENT_LEFT);
		    
//		    tilesEngine.setTile(TileId.CONTENT_LEFT, singIn);
		  }
		};
		
    getContext().getEventRegistry().register(SdRegistryEvents.EVENT_LOGIN, this);
    getContext().getEventRegistry().register(SdRegistryEvents.EVENT_LOGOUT, this);
    this.cometd = new Cometd();
	}

	public void activate(Map requests, ActivationObserver observer) {
	  Context context = getContext();
	  if (context.getUserId() != null) {
	    observer.activated(this, null);
      cometd.join(getContext().getUserId());
      cometd.addListener(this);
      
      if (getContext().getProjectId() != null) {
        AdminRemote.Util.inst.findAll(getContext().getProjectId(), new AsyncCallback<List<Member>>() {
          public void onSuccess(List<Member> result) {
            shareView.setUsers(result, getContext().getUserId());
          }
          public void onFailure(Throwable caught) {
            System.out.println("FAILURE: users failed");
          }
        });
      }
	  }
	}

	public void activate(DynamicParams params) {
	}

	public Handler createHandlerById(Action action) {
    switch (action.getId()) {
    }
    return null;
	}

	public View getView() {
	  if (getContext().getUserId() == null) {
	    return dummyView;
	  }
	  
	  if (shareView == null) {
	    shareView = new ShareView(this, this);
	  }

	  return shareView;
	}

	// @Override
	public void handleEvent(Integer eventId, Object data) {
    if (eventId.equals(SdRegistryEvents.EVENT_LOGIN)) {
      getView(); // init view if not initialized
      shareView.activate(getContext().getTilesEngine(), null, getContext().getContributor());
      shareView.setText("Project Members");
      cometd.join(getContext().getUserId());
      cometd.addListener(this);
      
      if (getContext().getProjectId() != null) { 
        AdminRemote.Util.inst.findAll(getContext().getProjectId(), new AsyncCallback<List<Member>>() {
          public void onSuccess(List<Member> result) {
            shareView.setUsers(result, getContext().getUserId());
          }
          public void onFailure(Throwable caught) {
            System.out.println("FAILURE: users failed");
          }
        });
      }
      
      System.out.println("Logged in");

//      ShareRemote.Util.inst.getEvents(new GetEventsCallback());
    } else if (eventId.equals(SdRegistryEvents.EVENT_LOGOUT)) {
      dummyView.activate(getContext().getTilesEngine(), null, getContext().getContributor());
      shareView.reset();
      cometd.disconnect();
      shareView.resetOnlineUsers();
      System.out.println("Logged out");
    }
	}

  private void handleShareEvent(IEvent event) {
    if (event instanceof ContentUpdateEventDTO) {
      getContext().getEventRegistry().handleEvent(SdRegistryEvents.EVENT_CONTENT_UPDATE, event);
    }
  }
  
  public void message(String user, String type, String value) {
    System.out.println(user+": "+type+" "+value);
    
    if (type != null && type.equals("content")) { 
      Long id = Long.valueOf(value);
      ContentRemote.Util.inst.open(id, new AsyncCallback<IContent>() {
        public void onSuccess(IContent result) {
          ContentUpdateEventDTO event = new ContentUpdateEventDTO();
          event.content = result;
          handleShareEvent(event);
        };
        public void onFailure(Throwable caught) {
          System.out.println("content update failed");
        };
      });
    }
  }
  
  public void onlineUsers(List<String> users) {
    System.out.println("onlineUsers:"+users);
    
//    users.remove(getContext().getUserId());
    shareView.resetOnlineUsers();
    
    // add me
//    shareView.addOnlineUser(getContext().getUserId());
    for (int i = 0; i < users.size(); ++i) {
      shareView.addOnlineUser(users.get(i));
    }
  }
  
  public void disconnected(int status, String responseText) {
    // TODO: set to share view to offline mode!!
//    getContext().getEventRegistry().handleEvent(SdRegistryEvents.EVENT_LOGOUT, null);
//    dummyView.activate(getContext().getTilesEngine(), null, getContext().getContributor());
//    shareView.reset();
    if (status == 500 /*&& responseText.equals("Access is denied")*/) {
//      cometd.disconnect();
//      Map<String,String> requests = new HashMap<String, String>();
//      requests.put(RequestId.CONTROLLER, RequestValue.LOGIN_CONTROLLER);
//      requests.put(RequestId.ACTION_ID, String.valueOf(ActionId.LOGOUT));
//
//      RequestUtils.activate(requests);
      Window.Location.reload();
    } else {
      shareView.disconnected();
    }
    shareView.resetOnlineUsers();
  }
  
  public void reconnected() {
    getContext().getEventRegistry().handleEvent(SdRegistryEvents.EVENT_LOGIN, null);
  }

  protected void joined(String user) {
    System.out.println(user + " joined");
  }
  
  // @Override
  public void signOut() {
//    LoginRemote.Util.inst.logout(new SignOutCallback());
  }
  
//    _cometd.init(url);

//    $wnd.dojox.cometd.init(url);
//
//    $wnd.dojox.cometd.startBatch();
//    $wnd.dojox.cometd.subscribe(channel, room, "handler");
//    $wnd.dojox.cometd.publish(channel, {user:room.user, type: "join", join: true});
//    $wnd.dojox.cometd.endBatch();
//
//    if (room.meta) {
//      $wnd.dojo.unsubscribe(room.meta, null, null);
//    }
//    
//    room.meta = $wnd.dojo.subscribe("/cometd/meta", function(e) {
//      @net.st.shareddesign.share.plugin.controller.ShareController::debugConsole(Ljava/lang/String;)(e.action+" "+e.reestablish+" "+e.successful);
//      if (e.action == "handshake") {
//        if (e.reestablish) {
//          if (e.successful) {
//            @net.st.shareddesign.share.plugin.controller.ShareController::debugConsole(Ljava/lang/String;)("reestablish");
//            $wnd.dojox.cometd.subscribe(room.channel, room, "handler");
//          }
//        }
//      }
//    });
//    
//    $wnd.dojo.addOnUnload(room, "leave");
}
