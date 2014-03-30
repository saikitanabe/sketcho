package net.sevenscales.domain;

import net.sevenscales.domain.api.IPath;

public class PathDTO implements IPath {
	private String path;
	private String style;

	public PathDTO() {
	}

	public PathDTO(String path, String style) {
		this.path = path;
		this.style = style;
	}

	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}

	public String getStyle() {
		return style;
	}
	public void setStyle(String style) {
		this.style = style;
	}

	public PathDTO copy() {
		return new PathDTO(path, style);
	}
	
	@Override
	public boolean equals(Object obj) {
		PathDTO item = (PathDTO) obj;
		if (obj == this) {
			return true;
		}
		if (item != null && item.path != null && item.path.equals(path) &&
				item.style != null && item.style.equals(style)) {
			return true;
		}
		return false;
	}	
}