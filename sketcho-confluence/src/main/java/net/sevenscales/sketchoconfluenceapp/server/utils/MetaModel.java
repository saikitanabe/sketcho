package net.sevenscales.sketchoconfluenceapp.server.utils;

import java.util.List;

public class MetaModel {
  private Integer currentVersion;
  private List<Integer> versions;
  
  public Integer getCurrentVersion() {
    return currentVersion;
  }
  public void setCurrentVersion(Integer currentVersion) {
    this.currentVersion = currentVersion;
  }
  public List<Integer> getVersions() {
    return versions;
  }
  public void setVersions(List<Integer> versions) {
    this.versions = versions;
  }
  
}
