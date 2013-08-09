package net.sevenscales.serverAPI.remote;

import java.util.List;

import net.sevenscales.domain.api.IProject;

public interface IProjectRemote {
  public IProject save(IProject project);
  public IProject update(IProject project);
  public IProject open(Long id);
  public List<IProject> findAll();
  public List<IProject> findAllPublicProjects();
}
