package net.sevenscales.serverAPI.remote;

import net.sevenscales.domain.api.IContent;

import java.util.List;

public interface IContentRemote {
  public IContent save(IContent content);
  public IContent update(IContent content);
  public IContent open(Long id);
  public List<IContent> findAll();
  public void remove(IContent content);
  public void removeAll(List<IContent> contents);
  public String downloadImage(String svg, Long contentId);
}
