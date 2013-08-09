package net.sevenscales.domain.api;

import net.sevenscales.domain.IDiagramItemRO;



public interface IDiagramItem extends IDiagramItemRO {
  void setText(String text);
	void setType(String type);
	void setShape(String shape);
	void setBackgroundColor(String backgroundColor);
	void setTextColor(String textColor);
	void setVersion(Integer version);
  void setClientId(String clientId);
  void setId(Long id);
  void setCustomData(String customData);
}
