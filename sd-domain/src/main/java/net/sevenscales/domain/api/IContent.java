package net.sevenscales.domain.api;

import java.io.Serializable;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

public interface IContent extends Serializable, IsSerializable {
	public Long getId();
	public String getName();
	public void setName(String name);
	public Integer getWidth();
	public void setWidth(Integer width);
	public Integer getHeight();
	public void setHeight(Integer height);
	
  public Map getProperties();
  public void setProperties(Map properties);
  
  public Long getCreatedTime();
  public void setCreatedTime(Long createdTime);
  
  public String getCreator();
  public void setCreator(String creator);
  
  public Long getModifiedTime();
  public void setModifiedTime(Long modifiedTime);
  
  public String getModifier();
  public void setModifier(String modifier);

}
