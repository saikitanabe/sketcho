package net.sevenscales.sketchoconfluenceapp.server.utils;

import java.util.ArrayList;

import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.spring.container.ContainerManager;

public class BandanaStore implements IStore {

  private ConfluenceBandanaContext context;
  private BandanaManager bandanaManager;
  private static final String PRE = "net.sevenscales.sketcho.confluence.plugins.";

  public BandanaStore() {
    this.context = new ConfluenceBandanaContext();
    setBandanaManager();
  }
  
  public void setBandanaManager() {
    if (bandanaManager == null) {
      bandanaManager = (BandanaManager) ContainerManager.getComponent("bandanaManager");
    }
  }

  public String versionKey(Long pageId, String name) {
    String key = PRE+name;
    MetaModel metaModel = (MetaModel) bandanaManager.getValue(context, key);
    if (metaModel == null) {
      // no version stored
      return name+":"+0;
    }
    return name+":"+ metaModel.getCurrentVersion();
  }
  
  public MetaModel metaModel(String name) {
    String key = PRE+name;
    return (MetaModel) bandanaManager.getValue(context, key);
  }
  
  public byte[] load(Long pageId, String versionKey) {
//    String versionKey = key+"_version";
//    Integer currentVersio = (Integer) bandanaManager.getValue(context, versionKey);
//    String versionKey = versionKey(name);
//    MetaModel meta = metaModel(name);
//    if (meta == null) {
//      return null;
//    }
//    String versionKey = versionKey(name);
    StoreEntry entry = (StoreEntry) bandanaManager.getValue(context, versionKey);
    if (entry == null) {
      return null;
    }
    return entry.getImage();
  }
  
  public String loadContent(Long pageId, String name) {
//    Integer currentVersio = (Integer) bandanaManager.getValue(context, versionKey);
//    if (currentVersio == null) {
//      return null;
//    }
//    bandanaManager.getValue(context, versionKey);
//    key = key+":"+ currentVersio;
    
    if (metaModel(name) == null) {
      return null;
    }
    String versionKey = versionKey(pageId, name);
    StoreEntry entry = (StoreEntry) bandanaManager.getValue(context, versionKey);
    return entry.getDiagramContent();
  }

  public String store(Long pageId, String name, StoreEntry entry) {
//    String versionKey = key+"_version";
//    Integer currentVersio = (Integer) bandanaManager.getValue(context, versionKey);
//    if (currentVersio == null) {
//      currentVersio = new Integer(0);
//    }
    MetaModel meta = metaModel(name);
    if (meta == null) {
      meta = new MetaModel();
      meta.setCurrentVersion(new Integer(0));
      meta.setVersions(new ArrayList<Integer>());
    }
    meta.setCurrentVersion(meta.getCurrentVersion()+1);
    meta.getVersions().add(meta.getCurrentVersion());
    bandanaManager.setValue(context, PRE+name, meta);
    String versionKey = versionKey(pageId, name);
    bandanaManager.setValue(context, versionKey, entry);
    return versionKey;
  }

  public void setPageId(long id) {
    
  }
  public void setContextPath(String contextPath) {
    
  }
}
