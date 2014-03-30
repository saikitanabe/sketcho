package net.sevenscales.domain;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import net.sevenscales.domain.api.IDiagramContent;
import net.sevenscales.domain.api.IDiagramItem;
import net.sevenscales.domain.api.IExtension;
import net.sevenscales.domain.utils.JsonFormat;
import net.sf.hibernate4gwt.pojo.java5.LazyPojo;

import com.google.gwt.user.client.rpc.IsSerializable;

public class DiagramItemDTO extends LazyPojo implements IDiagramItem, Serializable, IsSerializable {
	public static final int DATA_VERSION = 4;
	private static final long serialVersionUID = 5754682876312853660L;
	private long id;
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
  private int version = DATA_VERSION;
	private String clientId;
	private String customData;
	private double crc32;
	private int annotation;
	private int resolved;
	private List<UrlLinkDTO> links;

	public static DiagramItemDTO createGenericItem(ElementType type) {
		DiagramItemDTO result = new DiagramItemDTO();
		result.setType(type.getValue());
		return result;
	}

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
				+ ", links=" + links
				+ "]";
	}
  
	public DiagramItemDTO(String text, String type, String shape, IExtension extension, String backgroundColor, String textColor, Integer fontSize, Integer shapeProperties, Integer displayOrder, int version, Long id, String clientId, String customData, List<UrlLinkDTO> links) {
    this(text, type, shape, extension, backgroundColor, textColor, fontSize, shapeProperties, displayOrder, version, id, clientId, customData, 0, 0, 0, links);
  }

//	public DiagramItemDTO(String text, String type, String shape, String backgroundColor, String textColor,
//			Integer version, Long id, String clientId, String customData, double crc32) {
//		this(text, type, shape, backgroundColor, textColor, version, id, clientId, customData, crc32, 0, 0, null);
//	}

	public DiagramItemDTO(String text, String type, String shape, IExtension extension, String backgroundColor, String textColor, Integer fontSize, Integer shapeProperties, Integer displayOrder, Integer version, Long id, String clientId, String customData, double crc32, int annotation, int resolved, List<UrlLinkDTO> links
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
		this.crc32 = crc32;
		this.annotation = annotation;
		this.resolved = resolved;
		this.links = links;
	}
	
	public DiagramItemDTO(String clientId) {
		this("", "", "", /* extension */ null, "", "", null, /* shapeProperties */null, /* displayOrder */ null, 0, 0L, clientId, "", 0, 0, 0, null);
	}

	public DiagramItemDTO(IDiagramItemRO di) {
		copyFrom(di);
	}

	public long getId() {
		return id;
	}
	
	public void setId(long id) {
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
	
	@Override
	public String getTextColor() {
		return textColor;
	}
	@Override
	public void setTextColor(String textColor) {
		this.textColor = textColor;
	}

	@Override
	public Integer getFontSize() {
		return fontSize;
	}
	@Override
	public void setFontSize(Integer fontSize) {
		this.fontSize = fontSize;
	}

	@Override
	public Integer getShapeProperties() {
		return shapeProperties;
	}

	@Override
	public void setShapeProperties(Integer shapeProperties) {
		this.shapeProperties = shapeProperties;
	}

	@Override
	public Integer getDisplayOrder() {
		return displayOrder;
	}

	@Override
	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}
	
	public int getVersion() {
    return version;
  }
	
	public void setVersion(int version) {
		this.version = version;
	}
	
	@Override
	public String getClientId() {
		return clientId;
	}
	
	@Override
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	
	@Override
	public String getCustomData() {
		return customData;
	}

	@Override
	public void setCustomData(String customData) {
		this.customData = customData;
	}

	@Override
	public double getCrc32() {
		return crc32;
	}

	@Override
	public void setCrc32(double crc32) {
		this.crc32 = crc32;
	}

	@Override
	public int getAnnotation() {
		return annotation;
	}

	@Override
	public int getResolved() {
		return resolved;
	}

	@Override
	public boolean isAnnotation() {
		return annotation == 1;
	}

	@Override
	public boolean isResolved() {
		return resolved == 1;
	}

	@Override
	public void annotate() {
		annotation = 1;
	}

	@Override
	public void unannotate() {
		annotation = 0;
	}

	@Override
	public void resolve() {
		resolved = 1;
	}

	@Override
	public void unresolve() {
		resolved = 0;
	}

	@Override 
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

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IDiagramItemRO) {
			return compare((IDiagramItemRO) obj, null);
		}
		return false;
	}
	
	private <T> boolean checkIfNotSame(T me, T other) {
		if ((me != null && other == null) || (me == null && other != null) || me != null && !me.equals(other)) {
			return true;
		}
		return false;
	}

	@Override
	public IDiagramItem copy() {
		return new DiagramItemDTO(this);
	}
	
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
		crc32 = dit.crc32;
		annotation = dit.annotation;
		resolved = dit.resolved;

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
		if (ext.getSvgData() != null) {
			List<PathDTO> paths = new ArrayList<PathDTO>();
			ISvgDataRO svgdata = ext.getSvgData();
			for (IPathRO p : svgdata.getPaths()) {
				paths.add(p.copy());
			}
			SvgDataDTO copysvgdata = new SvgDataDTO(paths, svgdata.getWidth(), svgdata.getHeight());
			extension = new ExtensionDTO(copysvgdata);
		}
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

		if (checkIfNotSame(id, item.getId())) {
			result = false;
			updateDirtyFields(dirtyFields, DiagramItemField.ID);
		}
		if (checkIfNotSame(text, item.getText())) {
			result = false;
			updateDirtyFields(dirtyFields, DiagramItemField.TEXT);
		}
		if (checkIfNotSame(type, item.getType())) {
			result = false;
			updateDirtyFields(dirtyFields, DiagramItemField.TYPE);
		}
		if (checkIfNotSame(shape, item.getShape())) {
			result = false;
			updateDirtyFields(dirtyFields, DiagramItemField.SHAPE);
		}
		if (checkIfNotSame(backgroundColor, item.getBackgroundColor())) {
			result = false;
			updateDirtyFields(dirtyFields, DiagramItemField.BACKGROUND_COLOR);
		}
		if (checkIfNotSame(textColor, item.getTextColor())) {
			result = false;
			updateDirtyFields(dirtyFields, DiagramItemField.TEXT_COLOR);
		}
		if (checkIfNotSame(fontSize, item.fontSize)) {
			result = false;
			updateDirtyFields(dirtyFields, DiagramItemField.FONT_SIZE);
		}
		if (checkIfNotSame(shapeProperties, item.shapeProperties)) {
			result = false;
			updateDirtyFields(dirtyFields, DiagramItemField.SHAPE_PROPERTIES);
		}
		if (checkIfNotSame(displayOrder, item.displayOrder)) {
			result = false;
			updateDirtyFields(dirtyFields, DiagramItemField.DISPLAY_ORDER);
		}
		if (checkIfNotSame(version, item.getVersion())) {
			result = false;
			updateDirtyFields(dirtyFields, DiagramItemField.VERSION);
		}
		if (checkIfNotSame(clientId, item.getClientId())) {
			result = false;
			updateDirtyFields(dirtyFields, DiagramItemField.CLIENT_ID);
		}
		if (checkIfNotSame(customData, item.getCustomData())) {
			result = false;
			updateDirtyFields(dirtyFields, DiagramItemField.CUSTOM_DATA);
		}
		if (crc32 != item.getCrc32()) {
			result = false;
			updateDirtyFields(dirtyFields, DiagramItemField.CRC);
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
			return false;
		}

		if (checkIfNotSame(extension, item.getExtension())) {
			updateDirtyFields(dirtyFields, DiagramItemField.EXTENSION);
			result = false;
		}

		return result;
	}

}
