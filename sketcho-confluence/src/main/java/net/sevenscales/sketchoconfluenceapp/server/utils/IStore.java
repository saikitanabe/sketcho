package net.sevenscales.sketchoconfluenceapp.server.utils;

public interface IStore {

  String versionKey(Long pageId, String name);
  String store(Long pageId, String key, StoreEntry entry);
  byte[] load(Long pageId, String versionKey);
  String loadContent(Long pageId, String key);
  void setContextPath(String contextPath);

}
