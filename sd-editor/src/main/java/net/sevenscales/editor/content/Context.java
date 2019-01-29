package net.sevenscales.editor.content;

import net.sevenscales.editor.appframe.IContext;
import net.sevenscales.editor.appframe.EventRegistry;

public class Context implements IContext {
  private String userId;
  private String cookieValue;
  private EventRegistry eventRegistry;
  private Long projectId;
  private int memberPermissions;
  private Long pageId;
  private static boolean editMode;
  
  public String getUserId() {
    return userId;
  }
  
  public void setUserId(String userId) {
    this.userId = userId;
  }
  
  public void setCookieValue(String value) {
    this.cookieValue = value;
  }
  public String getCookieValue() {
    return cookieValue;
  }

  // @Override
  public EventRegistry getEventRegistry() {
    return eventRegistry;
  }
  // @Override
  public void setEventRegistry(EventRegistry eventRegistry) {
    this.eventRegistry = eventRegistry;
  }

  public Long getProjectId() {
    return projectId;
  }
  public void setProjectId(Long projectId) {
    this.projectId = projectId;
  }
  
  public void setMemberPermissions(int memberPermissions) {
    this.memberPermissions = memberPermissions;
  }
  
  public int getMemberPermissions() {
    return memberPermissions;
  }

  public void setPageId(Long pageId) {
    this.pageId = pageId;
  }

  public Long getPageId() {
    return pageId;
  }

}
