package net.sevenscales.domain.api;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;


public interface IProperty extends Serializable, IsSerializable {
  public Long getId();
  public void setId(Long id);
  public String getName();
  public void setName(String name);
  public String getType();
  public void setType(String type);
  public String getValue();
  public void setValue(String value);
}
