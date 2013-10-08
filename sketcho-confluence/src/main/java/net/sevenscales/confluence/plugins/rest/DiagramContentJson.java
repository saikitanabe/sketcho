package net.sevenscales.confluence.plugins.rest;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import net.sevenscales.domain.DiagramContentDTO;
import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.api.IDiagramContent;

@XmlRootElement(name = "diagramcontent")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({DiagramItemJson.class, DiagramChildItemJson.class})
public class DiagramContentJson {
	@XmlElement(name = "pageId")
	private Long pageId = 0L;
	@XmlElement(name = "name")
	private String name = "";
	@XmlElement(name = "svg")
	private String svg = "";
	@XmlElement(name = "editorWidth")
	private Integer editorWidth = 0;
	@XmlElement(name = "editorHeight")
	private Integer editorHeight = 0;
	// dates are as strings to keep presision, javascript doesn't have long...
	@XmlElement(name = "createdAt")
	private String createdAt = "0";
	@XmlElement(name = "updatedAt")
	private String updatedAt = "0";
	@XmlElement(name = "version")
	private Integer version = 0;
	@XmlElement(name = "items")
	private List<DiagramItemJson> items = new ArrayList<DiagramItemJson>();

	public Long getPageId() {
		return pageId;
	}

	public void setPageId(Long pageId) {
		this.pageId = pageId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSvg() {
		return svg;
	}

	public void setSvg(String svg) {
		this.svg = svg;
	}

	public Integer getEditorWidth() {
		return editorWidth;
	}

	public void setEditorWidth(Integer editorWidth) {
		this.editorWidth = editorWidth;
	}

	public Integer getEditorHeight() {
		return editorHeight;
	}

	public void setEditorHeight(Integer editorHeight) {
		this.editorHeight = editorHeight;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public List<DiagramItemJson> getItems() {
		return items;
	}

	public void setItems(List<DiagramItemJson> items) {
		this.items = items;
	}

	public final DiagramContentDTO asDTO() {
		DiagramContentDTO result = new DiagramContentDTO();
		result.setName(getName());
		result.setCreatedTime(Long.valueOf(getCreatedAt()));
		result.setModifiedTime(Long.valueOf(getUpdatedAt()));
		result.setVersion(getVersion());
		result.setWidth(getEditorWidth());
		result.setHeight(getEditorHeight());

		for (DiagramItemJson item : getItems()) {
			DiagramItemDTO dto = item.asDTO();
			result.addItem(dto);
		}

		return result;
	}

	public static DiagramContentJson fromDTO(IDiagramContent from) {
		DiagramContentJson result = new DiagramContentJson();
		result.setName(from.getName());
		if (from.getCreatedTime() != null) {
			result.setCreatedAt(from.getCreatedTime().toString());
		}
		if (from.getModifiedTime() != null) {
			result.setUpdatedAt(from.getModifiedTime().toString());
		}
		result.setVersion(from.getVersion());

		for (IDiagramItemRO item : (List<? extends IDiagramItemRO>) from.getDiagramItems()) {
			if ("child".equals(item.getType())) {
				result.getItems().add(DiagramChildItemJson.fromDTO(item));
			} else {
				result.getItems().add(DiagramItemJson.fromDTO(item));
			}
		}
		return result;
	}

}
