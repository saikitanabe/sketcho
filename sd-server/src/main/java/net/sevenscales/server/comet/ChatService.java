package net.sevenscales.server.comet;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import net.sevenscales.server.event.ContentUpdateEvent;
import net.sevenscales.domain.api.IContent;

import org.acegisecurity.context.SecurityContextHolder;
import org.cometd.Bayeux;
import org.cometd.Channel;
import org.cometd.Client;
import org.cometd.RemoveListener;
import org.mortbay.cometd.BayeuxService;
import org.mortbay.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

public class ChatService extends BayeuxService
                         implements ApplicationListener {
  final Logger logger = LoggerFactory.getLogger(ChatService.class);
  private final ConcurrentMap<String, Map<String, String>> _members = new ConcurrentHashMap<String, Map<String, String>>();
  private String messageId;

	public ChatService(Bayeux bayeux) {
		super(bayeux, "chat");
    subscribe("/chat/**", "trackMembers");
	}
	
  public void trackMembers(final Client joiner, final String channelName, Map<String, Object> data, final String messageId)
  {
      this.messageId = messageId;
      if (Boolean.TRUE.equals(data.get("join"))) {
          Map<String, String> membersMap = _members.get(channelName);
          if (membersMap == null) {
              Map<String, String> newMembersMap = new ConcurrentHashMap<String, String>();
              membersMap = _members.putIfAbsent(channelName, newMembersMap);
              if (membersMap == null) membersMap = newMembersMap;
          }
          
          final Map<String, String> members = membersMap;
          final String userName = (String)data.get("user");
          members.put(userName, joiner.getId());
          joiner.addListener(new RemoveListener() {
              public void removed(String clientId, boolean timeout) {
                  members.values().remove(clientId);
                  Log.info("remove: " + clientId);
                  Log.info("members: " + members);
                  // Broadcast the members to all existing members
                  Channel channel = getBayeux().getChannel(channelName, false);
                  if (channel != null) {
                    Map<String, Object> data = new HashMap<String, Object>();
                    data.put("members", members.keySet());
                    channel.publish(getClient(), data, messageId);
                  }
              }
          });

          Log.info("Members: " + members);
          // Broadcast the members to all existing members
          Channel channel = getBayeux().getChannel(channelName, false);
          data.put("members", members.keySet());
          channel.publish(getClient(), data, messageId);
      }
  }

    public void onApplicationEvent(ApplicationEvent event) {
      if (event instanceof ContentUpdateEvent) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("user", SecurityContextHolder.getContext().getAuthentication().getName());
        data.put("type", "content");
        IContent c = (IContent) event.getSource();
        data.put("value", c.getId().toString());
        data.put("timeStamp", c.getModifiedTime());

        String channelName = "/chat/demo";
        logger.info("sending event to members: " + _members.get(channelName));

        Channel channel = getBayeux().getChannel(channelName, false);
        if (channel != null) {
          channel.publish(getClient(), data, messageId);
        }
      }
    }

}
