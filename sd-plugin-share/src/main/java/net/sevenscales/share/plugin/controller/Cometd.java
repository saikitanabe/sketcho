package net.sevenscales.share.plugin.controller;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Window;

public class Cometd {
  private boolean connected = false;
  private boolean handshook = false;
  private JavaScriptObject metasubscription;
  private JavaScriptObject channelSubscription;
  private String username;
  private List<ChannelListener> channelListeners = new ArrayList<ChannelListener>();
  private List<String> users = new ArrayList<String>();
  private JavaScriptObject cometd;
  
  public interface ChannelListener {
    public void message(String user, String type, String value);
    public void onlineUsers(List<String> users);
    public void reconnected();
    public void disconnected(int status, String responseText);
  }
  
  public Cometd() {
    this.cometd = nativeCreateCometd();
  }
  
  private native JavaScriptObject nativeCreateCometd()/*-{
    return $wnd.dojox.cometd;
  }-*/;

  public void join(String user) {
    this.username = user;
    String hostName = Window.Location.getHostName();
    String port = Window.Location.getPort();
    String contextPath = "";
    String protocol = Window.Location.getProtocol();
    String host = protocol + "//" + hostName;

    if (port.length() > 0) {
      host += ":" + port;
    }
    
    host += contextPath;
    if (metasubscription == null) {
      nativeConfigure(host+"/cometd/chat/", cometd);
      this.metasubscription = nativeAddListener(cometd);
      nativeHandshake(cometd);
    }
//    metaSubscribe();
//    nativeInit(host + "/cometd/chat/");
  }

  private void _connectionSucceeded() {
    System.out.println("connected");
    if (channelSubscription != null) {
      unsubscribe(cometd, channelSubscription);
    }
    this.channelSubscription = nativeSubscribe(cometd, "/chat/demo");
    for (ChannelListener cl : channelListeners) {
      cl.reconnected();
    }
  }
  
  private native void unsubscribe(JavaScriptObject cometd, JavaScriptObject channelSubscription)/*-{
    cometd.unsubscribe(channelSubscription);
  }-*/;

  private native JavaScriptObject nativeSubscribe(JavaScriptObject cometd, String channel)/*-{
    var me = this;
  
    function handler(e) {
      if (e.data.members) {
        me.@net.sevenscales.share.plugin.controller.Cometd::notifyUsers(Lcom/google/gwt/core/client/JavaScriptObject;I)
          (e.data.members, e.data.members.length);
      } else {
        me.@net.sevenscales.share.plugin.controller.Cometd::notifyListeners(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)
          (e.data.user, e.data.type, e.data.value);
      }
    }

    cometd.startBatch();
    var result = cometd.subscribe(channel, handler);
    cometd.publish(channel, {
        user: me.@net.sevenscales.share.plugin.controller.Cometd::username,
        join: true,
    });
    cometd.endBatch();
    return result;
  }-*/;

  private void _connectionBroken(int status, String responseText) {
    responseText = responseText.replaceAll("\n", "");
    String reg = ".*<title>Error\\s"+status+"\\s.+</title>.*";
    if (responseText.matches(reg)) {
      responseText = responseText.substring(responseText.indexOf("<title>"), responseText.indexOf("</title>"));
      responseText = responseText.substring(responseText.indexOf(Integer.valueOf(status).toString())+4);
    }

    System.out.println("broken:"+status+":"+responseText);
    
    for (ChannelListener cl : channelListeners) {
      cl.disconnected(status, responseText);
    }
  }

  private native JavaScriptObject nativeAddListener(JavaScriptObject cometd)/*-{
    var me = this;
    var _connected = false;
    function _metaConnect(message) {
//      @net.sevenscales.share.plugin.controller.Cometd::debugConsole(Ljava/lang/String;)
//          ("meta/connect");
//      @net.sevenscales.share.plugin.controller.Cometd::debugConsole(Ljava/lang/String;)
//        (message.action+" "+message.successful+" "+_connected);

      var wasConnected = _connected;
      _connected = message.successful;
      if (!wasConnected && _connected) {
        me.@net.sevenscales.share.plugin.controller.Cometd::_connectionSucceeded()();
      } else if (wasConnected && !_connected) {
        me.@net.sevenscales.share.plugin.controller.Cometd::_connectionBroken(ILjava/lang/String;)
          (message.xhr.status,message.xhr.responseText);
      }
    }
    
    cometd.addListener('/meta/unsuccessful', function(e){
//      @net.sevenscales.share.plugin.controller.Cometd::debugConsole(Ljava/lang/String;)
//          ("meta/unsuccessful");
      me.@net.sevenscales.share.plugin.controller.Cometd::_connectionBroken(ILjava/lang/String;)
        (e.xhr.status,e.xhr.responseText);
    });

    return cometd.addListener('/meta/connect', _metaConnect);
  }-*/;

  private native void nativeHandshake(JavaScriptObject cometd)/*-{
    cometd.handshake();
  }-*/;

  private native void nativeConfigure(String url, JavaScriptObject cometd)/*-{
    cometd.configure({
      url: url,
      logLevel: "debug"
    });
  }-*/;

  public void addListener(ChannelListener listener) {
    if (!channelListeners.contains(listener)) {
      channelListeners.add(listener);
    }
  }
  
  protected void notifyListeners(String user, String type, String value) {
    for (ChannelListener cl : channelListeners) {
      cl.message(user, type, value);
    }
  }
  
  protected void notifyUsers(JavaScriptObject users, int size) {
    this.users.clear();
    for (int i = 0; i < size; ++i) {
      this.users.add(nativeGet(i, users));
    }
    for (ChannelListener cl : channelListeners) {
      cl.onlineUsers(this.users);
    }
  }
  
  private native String nativeGet(int i, JavaScriptObject array)/*-{
    return array[i];
  }-*/;

//  private void metaSubscribe() {
//    metaUnsubscribe();
//    nativeMetasubscribe("/chat/demo");
//  }
  
  protected static void debugConsole(String msg) {
    System.out.println(msg);
//    Debug.print(msg);
  }
  
  public void disconnect() {
    channelUnsubscribe();
//    metaUnsubscribe();
    if (metasubscription != null) {
      nativeRemoveListener(metasubscription);
    }
    metasubscription = null;
//    nativeDisconnect();
  }

  private native void nativeRemoveListener(JavaScriptObject metasubscription)/*-{
    $wnd.dojox.cometd.removeListener(metasubscription);
  }-*/;

  private void channelUnsubscribe() {
    if (channelSubscription != null) {
      nativeUnsubscribe(channelSubscription);
    }
    channelSubscription = null;
  }

  private native void nativeUnsubscribe(JavaScriptObject channelSubscription)/*-{
    $wnd.dojox.cometd.unsubscribe(channelSubscription);
  }-*/;

}
