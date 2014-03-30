package net.sevenscales.domain;

import java.util.List;

public interface ISvgDataRO {
	List<? extends IPathRO> getPaths();
	double getWidth();
	double getHeight();
	ISvgDataRO copy();
}