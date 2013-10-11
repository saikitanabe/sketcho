package net.sevenscales.domain.api;

import net.sevenscales.domain.IDiagramItemRO;



public interface IDiagramItem extends IDiagramItemRO {
  void setText(String text);
	void setType(String type);
	void setShape(String shape);
	void setBackgroundColor(String backgroundColor);
	void setTextColor(String textColor);
	void setVersion(int version);
  void setClientId(String clientId);
  void setId(long id);
  void setCustomData(String customData);
  void setCrc32(double crc32);
  void setAnnotation(int annotation);
}
