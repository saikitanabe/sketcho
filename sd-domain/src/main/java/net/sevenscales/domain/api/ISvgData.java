package net.sevenscales.domain.api;

import net.sevenscales.domain.ISvgDataRO;

public interface ISvgData extends ISvgDataRO {
	void setSvg(String svg);
	void setWidth(double width);
	void setHeight(double height);
}
