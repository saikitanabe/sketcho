package net.sevenscales.domain;

import java.util.List;
import java.util.ArrayList;

import net.sevenscales.domain.api.ISvgData;
import net.sevenscales.domain.IPathRO;
import net.sevenscales.domain.ISvgDataRO;

public class SvgDataDTO implements ISvgData {
	private List<? extends IPathRO> paths;
	private double width;
	private double height;

	public SvgDataDTO(List<? extends IPathRO> paths, double width, double height) {
		this.paths = paths;
		this.width = width;
		this.height = height;
	}

	public List<? extends IPathRO> getPaths() {
		return paths;
	}
	public void setPaths(List<? extends IPathRO> paths) {
		this.paths = paths;
	}

	public double getWidth() {
		return width;
	}
	public void setWidth(double width) {
		this.width = width;
	}

	public double getHeight() {
		return height;
	}
	public void setHeight(double height) {
		this.height = height;
	}

	public ISvgDataRO copy() {
		List<IPathRO> copypaths = new ArrayList<IPathRO>();
		for (IPathRO p : paths) {
			copypaths.add(p.copy());
		}
		return new SvgDataDTO(copypaths, width, height);
	}


	@Override
	public boolean equals(Object obj) {
		SvgDataDTO item = (SvgDataDTO) obj;
		if (obj == this) {
			return true;
		}
		if (item != null && width == item.width && item.height == height && paths.equals(item.paths)) {
			return true;
		}
		return false;
	}	

}