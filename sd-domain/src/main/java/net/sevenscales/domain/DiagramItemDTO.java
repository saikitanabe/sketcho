package net.sevenscales.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.IsSerializable;

import net.sevenscales.domain.api.IDiagramContent;
import net.sevenscales.domain.api.IDiagramItem;
import net.sevenscales.domain.api.IExtension;
import net.sevenscales.domain.json.JsonExtraction;
import net.sevenscales.domain.utils.DiagramItemUtils;

public class DiagramItemDTO implements IDiagramItem, Serializable, IsSerializable {
	public static final int DATA_VERSION = 4;
	private static final long serialVersionUID = 5754682876312853660L;
	private Long id;
	private String text;
	private String type;
	private String shape;
	private IExtension extension;
  private IDiagramContent diagramContent;
	private String backgroundColor;
	private String textColor;
	private Integer fontSize;
	private Integer shapeProperties;
	private Integer displayOrder;
  private Integer version = DATA_VERSION;
	private String clientId;
	private String customData;
	private Integer rotateDegrees;
	private Double crc32;
	private String group;
	private Integer annotation;
	private Integer resolved;
	private List<UrlLinkDTO> links;
	private String parentId;
	private JavaScriptObject data;
	private Long createdAt;
	private Long updatedAt;

	public DiagramItemDTO() {
  }
	
	@Override
	public String toString() {
		return "DiagramItemDTO [id=" + id + ", text=" + text + ", type=" + type
				+ ", shape=" + shape
				+ ", diagramContent=" + diagramContent
				+ ", backgroundColor=" + backgroundColor
				+ ", textColor=" + textColor
				+ ", fontSize=" + fontSize
				+ ", shapeProperties=" + shapeProperties
				+ ", displayOrder=" + displayOrder
				+ ", version=" + version + ", clientId=" + clientId + ", customData=" + customData + ", crc32=" + crc32
				+ ", annotation=" + annotation 
				+ ", resolved=" + resolved
				+ ", group=" + group
				+ ", rotateDegrees=" + rotateDegrees
				+ ", links=" + links
				+ ", parentId=" + parentId
				+ "]";
	}
  
	public DiagramItemDTO(
		String text,
		String type,
		String shape,
		IExtension extension,
		String backgroundColor,
		String textColor,
		Integer fontSize,
		Integer shapeProperties,
		Integer displayOrder,
		Integer version,
		Long id,
		String clientId,
		String customData,
		Integer rotateDegrees,
		List<UrlLinkDTO> links,
		String parentId
	) {
    this(
      text,
      type,
      shape,
      extension,
      backgroundColor,
      textColor,
      fontSize, 
      shapeProperties,
      displayOrder,
      version,
      id,
      clientId,
      customData,
      rotateDegrees,
      Double.valueOf(0),
      /*group*/ null,
      Integer.valueOf(0),
      Integer.valueOf(0),
      links,
      parentId,
      /* data */ null
    );
  }

//	public DiagramItemDTO(String text, String type, String shape, String backgroundColor, String textColor,
//			Integer version, Long id, String clientId, String customData, double crc32) {
//		this(text, type, shape, backgroundColor, textColor, version, id, clientId, customData, crc32, 0, 0, null);
//	}

	public DiagramItemDTO(
		String text,
		String type,
		String shape,
		IExtension extension,
		String backgroundColor,
		String textColor,
		Integer fontSize,
		Integer shapeProperties,
		Integer displayOrder,
		Integer version,
		Long id,
		String clientId,
		String customData,
		Integer rotateDegrees,
		Double crc32,
		String group,
		Integer annotation,
		Integer resolved,
		List<UrlLinkDTO> links,
		String parentId,
		JavaScriptObject data
	) {
		super();
		this.text = text;
		this.type = type;
		this.shape = shape;
		this.extension = extension;
		this.backgroundColor = backgroundColor;
		this.textColor = textColor;
		this.fontSize = fontSize;
		this.shapeProperties = shapeProperties;
		this.displayOrder = displayOrder;
		this.version = version;
		this.id = id;
		this.clientId = clientId;
		this.customData = customData;
		this.rotateDegrees = rotateDegrees;
		this.crc32 = crc32;
		this.group = group;
		this.annotation = annotation;
		this.resolved = resolved;
		this.links = links;
		this.parentId = parentId;
		this.data = data;
	}
	
	public DiagramItemDTO(String clientId) {
		this(
      "",
      "",
      "",
      /* extension */ null,
      "",
      "",
      null,
      /* shapeProperties */null,
      /* displayOrder */ null,
      Integer.valueOf(0),
      Long.valueOf(0),
      clientId,
      "",
      /*rotateDegrees*/  null,
      Double.valueOf(0),
      /*group*/ null,
      Integer.valueOf(0),
      Integer.valueOf(0),
      null,
      /* parentId */ null,
      /* data */null
    );
	}

	public DiagramItemDTO(IDiagramItemRO di) {
		copyFrom(di);
	}

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}

	public String getType() {
	  return type;
	}
	public void setType(String type) {
	  this.type = type;
	}
	
	public String getShape() {
	  return shape;
	}
	public void setShape(String shape) {
	  this.shape = shape;
	}

	public IExtension getExtension() {
		return extension;
	}
	public void setExtension(IExtension extension) {
		this.extension = extension;
	}
	
	public IDiagramContent getDiagramContent() {
	  return this.diagramContent;
	}
	public void setDiagramContent(IDiagramContent diagramContent) {
	  this.diagramContent = diagramContent;
	}
	
	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	
	public String getBackgroundColor() {
		return backgroundColor;
	}
	
	// @Override
	public String getTextColor() {
		return textColor;
	}
	// @Override
	public void setTextColor(String textColor) {
		this.textColor = textColor;
	}

	// @Override
	public Integer getFontSize() {
		return fontSize;
	}
	// @Override
	public void setFontSize(Integer fontSize) {
		this.fontSize = fontSize;
	}

	// @Override
	public void setTextAlign(ShapeProperty textAlign) {
		clearShapeProperty(ShapeProperty.TXT_ALIGN_CENTER);
		clearShapeProperty(ShapeProperty.TXT_ALIGN_RIGHT);
		// clearShapeProperty(ShapeProperty.TXT_ALIGN_LEFT);
		if (textAlign == null) {
			// just clear text alignment and default legacy is left align
		} else {
			addShapeProperty(textAlign);
		}
	}

	// @Override
	public Integer getLineWeight() {
		if (extension != null) {
			return extension.getLineWeight();
		}
		return null;
	}
	// @Override
	public void setLineWeight(Integer lineWeight) {
		if (extension == null) {
			extension = new ExtensionDTO();
		}
		extension.setLineWeight(lineWeight);
	}

	// @Override
	public Integer getShapeProperties() {
    if (shapeProperties == null) {
      // getting unnecessary null pointer exceptions
      return 0;
    }
		return shapeProperties;
	}

	// @Override
	public void setShapeProperties(Integer shapeProperties) {
		this.shapeProperties = shapeProperties;
	}

	// @Override
	public void addShapeProperty(ShapeProperty shapeProperty) {
		if (shapeProperties == null) {
			shapeProperties = 0;
		}
		this.shapeProperties |= shapeProperty.getValue();
	}

	// @Override
	public void clearShapeProperty(ShapeProperty shapeProperty) {
		shapeProperties = ShapeProperty.clear(shapeProperties, shapeProperty.getValue());
	}

	// @Override
	public Integer getDisplayOrder() {
		return displayOrder;
	}

	// @Override
	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}
	
  @Override
	public Integer getVersion() {
    return version;
  }
	
  @Override
	public void setVersion(Integer version) {
		this.version = version;
	}
	
	// @Override
	public String getClientId() {
		return clientId;
	}
	
	// @Override
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	
	// @Override
	public String getCustomData() {
		return customData;
	}

	// @Override
	public void setCustomData(String customData) {
		this.customData = customData;
	}

	// @Override
	public String getParentId() {
		return parentId;
	}
	// @Override
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	// @Override
	public Double getCrc32() {
		return crc32;
	}

	// @Override
	public void setCrc32(Double crc32) {
		this.crc32 = crc32;
	}

	// @Override
	public String getGroup() {
		return this.group;
	}
	// @Override
	public void setGroup(String group) {
		this.group = group;
	}
	// @Override
	public boolean isGroup() {
		return group != null && !"".equals(group);
	}

	public Integer getRotateDegrees() {
		return this.rotateDegrees;
	}
	public void setRotateDegrees(Integer degrees) {
		this.rotateDegrees = degrees;
	}

	// @Override
	public Long getCreatedAt() {
		return createdAt;
	}
	// @Override
	public void setCreatedAt(Long cat) {
		this.createdAt = cat;
	}

	// @Override
	public Long getUpdatedAt() {
		return updatedAt;
	}
	// @Override
	public void setUpdatedAt(Long uat) {
		this.updatedAt = uat;
	}


	// @Override
	public JavaScriptObject getData() {
		return this.data;
	}
	// @Override
	public void setData(JavaScriptObject data) {
		this.data = data;
	}	

	// @Override
	public Integer getAnnotation() {
		return annotation;
	}

	// @Override
	public Integer getResolved() {
		return resolved;
	}

	// @Override
	public boolean isAnnotation() {
		return annotation != null && annotation == 1;
	}

	// @Override
	public boolean isResolved() {
		return resolved != null && resolved == 1;
	}

	// @Override
	public void annotate() {
		annotation = 1;
	}

	// @Override
	public void unannotate() {
		annotation = 0;
	}

	// @Override
	public void resolve() {
		resolved = 1;
	}

	// @Override
	public void unresolve() {
		resolved = 0;
	}

	// @Override 
	public List<? extends IUrlLinkRO> getLinks() {
		return links;
	}

	public void addLink(String url) {
		createLinks();
		links.add(new UrlLinkDTO(url));
	}

	public void setLink(String link) {
		createLinks();
		links.clear();
		links.add(new UrlLinkDTO(link));
	}

	public void clearLinks() {
		links = null;
	}

	public String getFirstLink() {
		if (links != null && links.size() > 0) {
			return links.get(0).getUrl();
		} 
		return null;
	}

	private void createLinks() {
		if (links == null) {
			links = new ArrayList<UrlLinkDTO>();
		}
	}

	// @Override
	public boolean equals(Object obj) {
		if (obj instanceof IDiagramItemRO) {
			return compare((IDiagramItemRO) obj, null);
		}
		return false;
	}
	
	// @Override
	public IDiagramItem copy() {
		return new DiagramItemDTO(this);
	}

  @Override
  public void merge(IDiagramItemRO di) {

    JSONObject from = JsonExtraction.decompose(di);
    JSONObject to = JsonExtraction.decompose(this);
    JavaScriptObject merged = _merge(to.getJavaScriptObject(), from.getJavaScriptObject());
    JSONObject mvalue = new JSONObject(merged);
    if (mvalue.isObject() != null) {
      DiagramItemDTO md = new JSONDiagramParser(mvalue.isObject(), false).isDiagram();
      copyFrom(md);
    }

  }

  private native JavaScriptObject _merge(JavaScriptObject to, JavaScriptObject from)/*-{
    return $wnd.mergeJson(to, from)
  }-*/;
	
	@Override
	public void copyFrom(IDiagramItemRO di) {
		DiagramItemDTO dit = (DiagramItemDTO) di;
		id = dit.id;
		text = dit.text;
		type = dit.type;
		shape = dit.shape;
		backgroundColor = dit.backgroundColor;
		textColor = dit.textColor;
		fontSize = dit.fontSize;
		shapeProperties = dit.shapeProperties;
		displayOrder = dit.displayOrder;
		version = dit.version;
		clientId = dit.clientId;
		customData = dit.customData;
		rotateDegrees = dit.rotateDegrees;
		crc32 = dit.crc32;
		group = dit.group;
		data = dit.data;
		annotation = dit.annotation;
		resolved = dit.resolved;
		parentId = dit.parentId;
		createdAt = dit.createdAt;
		updatedAt = dit.updatedAt;

		List<? extends IUrlLinkRO> fromLinks = di.getLinks();
		if (fromLinks != null) {
			createLinks();
			links.clear();
			for (IUrlLinkRO link : fromLinks) {
				links.add(new UrlLinkDTO(link.getUrl()));
			}
		}

		if (dit.extension != null) {
			copyExtensions(dit.extension);
		}
	}

	private void copyExtensions(IExtension ext) {
		ISvgDataRO svg = null;
		if (ext.getSvgData() != null) {
			svg = ext.getSvgData().copy();
		}
		extension = new ExtensionDTO(svg, ext.getLineWeight());
	}

	public boolean isComment() {
		return false;
	}

	private void updateDirtyFields(Map<String, Boolean> dirtyFields, DiagramItemField field) {
		if (dirtyFields != null) {
			dirtyFields.put(field.getValue(), true);
		}
	}

	public boolean compare(IDiagramItemRO diro, Map<String, Boolean> dirtyFields) {
		DiagramItemDTO item = (DiagramItemDTO) diro;
		boolean result = true;

		if (DiagramItemUtils.checkIfNotSame(id, item.getId())) {
			result = false;
			updateDirtyFields(dirtyFields, DiagramItemField.ID);
		}
		if (DiagramItemUtils.checkIfNotSame(text, item.getText())) {
			result = false;
			updateDirtyFields(dirtyFields, DiagramItemField.TEXT);
		}
		if (DiagramItemUtils.checkIfNotSame(type, item.getType())) {
			result = false;
			updateDirtyFields(dirtyFields, DiagramItemField.TYPE);
		}
		if (DiagramItemUtils.checkIfNotSame(shape, item.getShape())) {
			result = false;
			updateDirtyFields(dirtyFields, DiagramItemField.SHAPE);
		}
		if (DiagramItemUtils.checkIfNotSame(backgroundColor, item.getBackgroundColor())) {
			result = false;
			updateDirtyFields(dirtyFields, DiagramItemField.BACKGROUND_COLOR);
		}
		if (DiagramItemUtils.checkIfNotSame(textColor, item.getTextColor())) {
			result = false;
			updateDirtyFields(dirtyFields, DiagramItemField.TEXT_COLOR);
		}
		if (DiagramItemUtils.checkIfNotSame(fontSize, item.fontSize)) {
			result = false;
			updateDirtyFields(dirtyFields, DiagramItemField.FONT_SIZE);
		}
		if (DiagramItemUtils.checkIfNotSame(shapeProperties, item.shapeProperties)) {
			result = false;
			updateDirtyFields(dirtyFields, DiagramItemField.SHAPE_PROPERTIES);
		}
		if (DiagramItemUtils.checkIfNotSame(displayOrder, item.displayOrder)) {
			result = false;
			updateDirtyFields(dirtyFields, DiagramItemField.DISPLAY_ORDER);
		}
		if (DiagramItemUtils.checkIfNotSame(version, item.getVersion())) {
			result = false;
			updateDirtyFields(dirtyFields, DiagramItemField.VERSION);
		}
		if (DiagramItemUtils.checkIfNotSame(clientId, item.getClientId())) {
			result = false;
			updateDirtyFields(dirtyFields, DiagramItemField.CLIENT_ID);
		}
		if (DiagramItemUtils.checkIfNotSame(parentId, item.getParentId())) {
			result = false;
			updateDirtyFields(dirtyFields, DiagramItemField.PARENT);
		}
		if (DiagramItemUtils.checkIfNotSame(customData, item.getCustomData())) {
			result = false;
			updateDirtyFields(dirtyFields, DiagramItemField.CUSTOM_DATA);
		}
		if (DiagramItemUtils.checkIfNotSame(rotateDegrees, item.getRotateDegrees())) {
			result = false;
			updateDirtyFields(dirtyFields, DiagramItemField.ROTATE_DEGREES);
		}
		// TODO how to compare! Could use underscore to compare object
		if (DiagramItemUtils.checkIfNotSame(data, item.getData())) {
			result = false;
			updateDirtyFields(dirtyFields, DiagramItemField.DATA);
		}
		if (crc32 != item.getCrc32()) {
			result = false;
			updateDirtyFields(dirtyFields, DiagramItemField.CRC);
		}

		if (DiagramItemUtils.checkIfNotSame(createdAt, item.getCreatedAt())) {
			result = false;
			updateDirtyFields(dirtyFields, DiagramItemField.CREATED_AT);
		}
		if (DiagramItemUtils.checkIfNotSame(updatedAt, item.getUpdatedAt())) {
			result = false;
			updateDirtyFields(dirtyFields, DiagramItemField.UPDATED_AT);
		}

		if (DiagramItemUtils.checkIfNotSame(group, item.getGroup())) {
			result = false;
			updateDirtyFields(dirtyFields, DiagramItemField.GROUP);
		}

		if (annotation != item.getAnnotation()) {
			result = false;
			updateDirtyFields(dirtyFields, DiagramItemField.ANNOTATION);
		}
		if (resolved != item.getResolved()) {
			result = false;
			updateDirtyFields(dirtyFields, DiagramItemField.RESOLVED);
		}

		if (links != item.getLinks() || (links != null && !links.equals(item.getLinks()))) {
			result = false;
			updateDirtyFields(dirtyFields, DiagramItemField.LINKS);
		}

		if (DiagramItemUtils.checkIfNotSame(extension, item.getExtension())) {
			updateDirtyFields(dirtyFields, DiagramItemField.EXTENSION);
			result = false;
		}

		return result;
	}

	// @Override
	public boolean isSketchiness() {
		return type.endsWith("_s");
	}

}
