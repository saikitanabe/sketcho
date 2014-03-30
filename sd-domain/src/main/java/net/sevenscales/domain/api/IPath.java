package net.sevenscales.domain.api;

import net.sevenscales.domain.IPathRO;

public interface IPath extends IPathRO {
	void setPath(String path);
	void setStyle(String style);
}
