package net.sevenscales.domain.api;

import java.util.List;

import net.sevenscales.domain.IPathRO;
import net.sevenscales.domain.ISvgDataRO;

public interface ISvgData extends ISvgDataRO {
	void setPaths(List<? extends IPathRO> paths);
	void setWidth(double width);
	void setHeight(double height);
}
