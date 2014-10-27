package net.sevenscales.domain.api;

import net.sevenscales.domain.IExtensionRO;
import net.sevenscales.domain.ISvgDataRO;

public interface IExtension extends IExtensionRO {
	void setSvgData(ISvgDataRO svgdata);
	void setLineWidth(Integer lineWidth);
}
