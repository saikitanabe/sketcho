package net.sevenscales.plugin.api;

import net.sevenscales.appFrame.api.IContext;
import net.sevenscales.appFrame.api.IContributor;
import net.sevenscales.appFrame.impl.EventRegistry;
import net.sevenscales.appFrame.impl.ITilesEngine;
import net.sevenscales.domain.api.IProject;

public class Context implements IContext {
  private String userId;
  private String cookieValue;
  private EventRegistry eventRegistry;
  private ITilesEngine tilesEngine;
  private IContributor contributor;
  private Long projectId;
  private IProject project;
  private int memberPermissions;
  private Long pageId;
  private static boolean editMode;
  private Clipboard clipboard = new Clipboard();
  
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

  // not so nice design to share these in here...
  public ITilesEngine getTilesEngine() {
    return tilesEngine;
  }

  // not so nice design to share these in here...
  public IContributor getContributor() {
    return contributor;
  }

  public void setTilesEngine(ITilesEngine tilesEngine) {
    this.tilesEngine = tilesEngine;    
  }

  public void setContributor(IContributor contributor) {
    this.contributor = contributor;
  }

  public Long getProjectId() {
    return projectId;
  }
  public void setProjectId(Long projectId) {
    this.projectId = projectId;
  }
  
  public IProject getProject() {
    return project;
  }
  
  public void setProject(IProject project) {
    this.project = project;
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

  public void setEditMode(boolean editMode) {
    this.editMode = editMode;
  }

  public boolean isEditMode() {
    return editMode;
  }
  
  public Clipboard getClipboard() {
	return clipboard;
  }
  public void setClipboard(Clipboard clipboard) {
	this.clipboard = clipboard;
  }
}
