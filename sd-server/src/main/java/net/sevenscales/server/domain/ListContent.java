package net.sevenscales.server.domain;

import java.util.List;

import javax.persistence.Entity;

import net.sevenscales.domain.api.IListContent;

@Entity(name="list_content")
public class ListContent extends Content implements IListContent {
  private String value;
  private String items;
  private ListValue selected;
  private List<ListValue> values;
  
  public String getItems() {
    return this.items;
  }
  public void setItems(String items) {
    this.items = items;
  }
  
  public String getValue() {
    return this.value;
  }
  public void setValue(String value) {
    this.value = value;
  }
}
