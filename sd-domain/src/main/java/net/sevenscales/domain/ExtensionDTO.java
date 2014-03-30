package net.sevenscales.domain;

import net.sevenscales.domain.api.IExtension;
import net.sevenscales.domain.ISvgDataRO;

public class ExtensionDTO implements IExtension {
	private ISvgDataRO svgdata;

	public ExtensionDTO(ISvgDataRO svgdata) {
		this.svgdata = svgdata;
	}

	public ISvgDataRO getSvgData() {
		return svgdata;
	}

	public void setSvgData(ISvgDataRO svgdata) {
		this.svgdata = svgdata;
	}

	@Override
	public boolean equals(Object obj) {
		boolean result = false;
		if (obj instanceof ExtensionDTO) {
			ExtensionDTO e = (ExtensionDTO) obj;
			if (svgdata != null && svgdata.equals(e.svgdata)) {
				result = true;
			}
		}	
		return result;
	}

}