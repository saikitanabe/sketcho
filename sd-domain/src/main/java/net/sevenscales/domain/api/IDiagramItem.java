package net.sevenscales.domain.api;

import com.google.gwt.core.client.JavaScriptObject;

import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.ShapeProperty;


public interface IDiagramItem extends IDiagramItemRO {
  void setText(String text);
	void setType(String type);
	void setShape(String shape);
  void setExtension(IExtension extension);
	void setBackgroundColor(String backgroundColor);
	void setTextColor(String textColor);
  void setFontSize(Integer fontSize);
  void setTextAlign(ShapeProperty textAlign);
  void setLineWeight(Integer lineWeight);
  void setShapeProperties(Integer shapeProperties);
  void addShapeProperty(ShapeProperty shapeProperty);
  void clearShapeProperty(ShapeProperty shapeProperty);
  void setDisplayOrder(Integer displayOrder);
	void setVersion(Integer version);
  void setClientId(String clientId);
  void setParentId(String parentClientId);
  void setId(Long id);
  void setCustomData(String customData);
  void setCrc32(Double crc32);
  void setGroup(String group);
  void setData(JavaScriptObject data);
  void annotate();
  void unannotate();
  void resolve();
  void unresolve();
  void addLink(String link);
  void setLink(String link);
  void clearLinks();
  void setRotateDegrees(Integer degrees);
  void setCreatedAt(Long createdAt);
  void setUpdatedAt(Long updatedAt);
}
