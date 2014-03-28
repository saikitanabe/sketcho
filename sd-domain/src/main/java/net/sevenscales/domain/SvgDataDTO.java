package net.sevenscales.domain;

import net.sevenscales.domain.api.ISvgData;

public class SvgDataDTO implements ISvgData {
	private String svg;
	private double width;
	private double height;

	public SvgDataDTO(String svg, double width, double height) {
		this.svg = svg;
		this.width = width;
		this.height = height;
	}

	public String getSvg() {
		return svg;
	}
	public void setSvg(String svg) {
		this.svg = svg;
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
}