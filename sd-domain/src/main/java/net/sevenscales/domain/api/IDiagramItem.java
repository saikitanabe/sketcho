package net.sevenscales.domain.api;

import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.ISvgDataRO;
import net.sevenscales.domain.ShapeProperty;


public interface IDiagramItem extends IDiagramItemRO {
  void setText(String text);
	void setType(String type);
	void setShape(String shape);
  void setExtension(IExtension extension);
	void setBackgroundColor(String backgroundColor);
	void setTextColor(String textColor);
  void setFontSize(Integer fontSize);
  void setLineWidth(Integer lineWidth);
  void setShapeProperties(Integer shapeProperties);
  void addShapeProperty(ShapeProperty shapeProperty);
  void clearShapeProperty(ShapeProperty shapeProperty);
  void setDisplayOrder(Integer displayOrder);
	void setVersion(int version);
  void setClientId(String clientId);
  void setParentId(String parentClientId);
  void setId(long id);
  void setCustomData(String customData);
  void setCrc32(double crc32);
  void annotate();
  void unannotate();
  void resolve();
  void unresolve();
  void addLink(String link);
  void setLink(String link);
}
