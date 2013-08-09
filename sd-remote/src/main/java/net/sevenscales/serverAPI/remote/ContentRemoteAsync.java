package net.sevenscales.serverAPI.remote;

import java.util.List;

import net.sevenscales.domain.api.IContent;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ContentRemoteAsync {
  public void save(IContent content, AsyncCallback<IContent> async);
  public void update(IContent content, AsyncCallback<IContent> async);
  public void open(Long id, AsyncCallback<IContent> async);
  public void findAll(AsyncCallback< List<IContent> > async);  
  public void remove(IContent content, AsyncCallback async);
  public void removeAll(List<IContent> contents, AsyncCallback async);
  public void downloadImage(String svg, Long contentId, AsyncCallback<String> async);
}
