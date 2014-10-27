package net.sevenscales.domain;

import net.sevenscales.domain.api.IExtension;
import net.sevenscales.domain.ISvgDataRO;
import net.sevenscales.domain.utils.DiagramItemUtils;

public class ExtensionDTO implements IExtension {
	private ISvgDataRO svgdata;
	private Integer lineWidth;

	public ExtensionDTO() {
	}

	public ExtensionDTO(ISvgDataRO svgdata, Integer lineWidth) {
		this.svgdata = svgdata;
		this.lineWidth = lineWidth;
	}

	public ISvgDataRO getSvgData() {
		return svgdata;
	}

	public void setSvgData(ISvgDataRO svgdata) {
		this.svgdata = svgdata;
	}

	public void setLineWidth(Integer lineWidth) {
		this.lineWidth = lineWidth;
	}
	public Integer getLineWidth() {
		return lineWidth;
	}

	@Override
	public boolean equals(Object obj) {
		boolean result = false;
		if (obj instanceof ExtensionDTO) {
			result = true;
			ExtensionDTO e = (ExtensionDTO) obj;

			if (DiagramItemUtils.checkIfNotSame(svgdata, e.svgdata)) {
				result = false;
			}

			if (DiagramItemUtils.checkIfNotSame(lineWidth, e.lineWidth)) {
				result = false;
			}

		}	
		return result;
	}

}