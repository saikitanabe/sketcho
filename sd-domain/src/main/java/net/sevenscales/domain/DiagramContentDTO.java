package net.sevenscales.domain;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.json.client.*;

import net.sevenscales.domain.api.IContent;
import net.sevenscales.domain.api.IDiagramContent;
import net.sevenscales.domain.api.IDiagramItem;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.dto.ContentDTO;
import net.sevenscales.domain.utils.JsonFormat;

import com.google.gwt.user.client.rpc.IsSerializable;


public class DiagramContentDTO extends ContentDTO implements IDiagramContent, IsSerializable {
	private static final long serialVersionUID = 4975000551782219679L;
	private List<IDiagramItemRO> diagramItems = new ArrayList<IDiagramItemRO>();
	// precision will suffer, but should be enough times to update the board...
  private Integer version = -1;

  public DiagramContentDTO() {
	}
  
	public DiagramContentDTO(IContent content) {
    IDiagramContent dc = (IDiagramContent) content;
    for (IDiagramItemRO di : dc.getDiagramItems()) {
    	addItem(new DiagramItemDTO(di));
    }
	}
	
	public List<IDiagramItemRO> getDiagramItems() {
		return this.diagramItems;
	}
	public void setDiagramItems(List<IDiagramItemRO> diagramItems) {
		this.diagramItems = diagramItems;
	}

	public void addItem(IDiagramItemRO diagramItem) {
		if (!diagramItems.contains(diagramItem)) {
			diagramItems.add(diagramItem);
		}
	}

	public void reset() {
		diagramItems.clear();
	}
	
	public boolean isEmpty() {
		return diagramItems.size() == 0 ? true : false;
	}
	
	public Integer getVersion() {
    return version;
  }
	
	public void setVersion(Integer version) {
		this.version = version;
	}
	
	@Override
	public boolean equals(Object content) {
    IDiagramContent dc = (IDiagramContent) content;
    return diagramItems.equals(dc.getDiagramItems());
	}

	public JSONValue toJson(JsonFormat jsonFormat) {
		JSONObject result = new JSONObject();
		result.put("name", new JSONString(getName()));
		// do not send createAt and updatedAt times
		// slightly misleading, even though server is not saving/using those
		result.put("editorWidth", new JSONNumber(getWidth()));
		result.put("editorHeight", new JSONNumber(getHeight()));
		result.put("createdAt", new JSONNumber(getCreatedTime()));
		result.put("updatedAt", new JSONNumber(getModifiedTime()));

		// need to have some special tricks with version...
		String version = String.valueOf(getVersion());
		result.put("version", new JSONNumber(Integer.valueOf(version)));

		JSONArray items = new JSONArray();
		int index = 0;
		for (IDiagramItemRO item : getDiagramItems()) {
		 items.set(index++, item.toJson(jsonFormat));
		}
		result.put("items", items);
		return result;
	}

}
