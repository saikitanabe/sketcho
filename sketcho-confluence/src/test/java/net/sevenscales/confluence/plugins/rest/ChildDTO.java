package net.sevenscales.confluence.plugins.rest;

import net.sevenscales.domain.DiagramItemDTO;

public class ChildDTO extends DiagramItemDTO {
	private String parent;
	
	public ChildDTO(String text, String type, String shape, String backgroundColor, String textColor,
			Integer version, Long id, String clientId, String customData, double crc32, String parent) {
		super(text, type, shape, backgroundColor, textColor, version, id, clientId, customData, crc32);
		this.parent = parent;
	}
	
	public String getParent() {
		return parent;
	}
	
	public void setParent(String parent) {
		this.parent = parent;
	}
}
