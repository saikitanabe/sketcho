package net.sevenscales.sketchoconfluenceapp.server.utils;

import com.atlassian.confluence.pages.Attachment;

public interface IStore {

  String versionKey(Long pageId, String name);
  String store(Long pageId, String key, StoreEntry entry);
  byte[] load(Long pageId, String versionKey);
  String loadContent(Long pageId, String key, String ext);
  void setContextPath(String contextPath);
  String loadAttachment(Attachment a);
}
