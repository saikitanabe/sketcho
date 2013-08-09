package net.sevenscales.server.dao;

import java.util.List;

import net.sevenscales.domain.api.IPageWithNamedContentValues;
import net.sevenscales.server.domain.Label;
import net.sevenscales.serverAPI.remote.ILabelRemote;


public interface ILabelDAO extends ILabelRemote {
  // secured method
  public List<IPageWithNamedContentValues> findAllPages2(
      Label label, List<String> namedItems, String filter, String sort);
}
