package net.sevenscales.server.domain;

import java.util.Map;

import net.sevenscales.domain.api.IPage;
import net.sevenscales.domain.api.IPageWithNamedContentValues;

public class PageWithNamedContentValues implements IPageWithNamedContentValues {
  private IPage page;
  private Map<String, String> namedContentValues;
  
  public Map<String, String> getNamedContentValues() {
    return namedContentValues;
  }
  public void setNamedContentValues(Map<String, String> namedContentValues) {
    this.namedContentValues = namedContentValues;
  }
  
  public IPage getPage() {
    return page;
  }
  public void setPage(IPage page) {
    this.page = page;
  }

}
