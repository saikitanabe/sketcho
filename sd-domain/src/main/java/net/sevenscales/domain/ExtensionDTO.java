package net.sevenscales.domain;

import net.sevenscales.domain.api.IExtension;
import net.sevenscales.domain.ISvgDataRO;
import net.sevenscales.domain.utils.DiagramItemUtils;

public class ExtensionDTO implements IExtension {
	private ISvgDataRO svgdata;
	private Integer lineWeight;

	public ExtensionDTO() {
	}

	public ExtensionDTO(ISvgDataRO svgdata, Integer lineWeight) {
		this.svgdata = svgdata;
		this.lineWeight = lineWeight;
	}

	public ISvgDataRO getSvgData() {
		return svgdata;
	}

	public void setSvgData(ISvgDataRO svgdata) {
		this.svgdata = svgdata;
	}

	public void setLineWeight(Integer lineWeight) {
		this.lineWeight = lineWeight;
	}
	public Integer getLineWeight() {
		return lineWeight;
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

			if (DiagramItemUtils.checkIfNotSame(lineWeight, e.lineWeight)) {
				result = false;
			}

		}	
		return result;
	}

}