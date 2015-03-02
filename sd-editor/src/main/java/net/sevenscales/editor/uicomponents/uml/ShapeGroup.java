package net.sevenscales.editor.uicomponents.uml;

public class ShapeGroup {
	public String elementType;
	public ShapeProto[] protos;

		// NOTE: important to keep as float or double; int will be really slow!
	public double width;
	public double height;
	public Integer properties;
	public Integer fontSize;

	public ShapeGroup(String elementType, ShapeProto[] protos, double width, double height, Integer properties) {
		this.elementType = elementType;
		this.protos = protos;
		this.width = width;
		this.height = height;
		this.properties = properties;
	}

}
