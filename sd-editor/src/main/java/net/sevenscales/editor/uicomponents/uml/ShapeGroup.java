package net.sevenscales.editor.uicomponents.uml;

public class ShapeGroup {
	public ShapeProto[] protos;

		// NOTE: important to keep as float or double; int will be really slow!
	public double width;
	public double height;

	public ShapeGroup(ShapeProto[] protos, double width, double height) {
		this.protos = protos;
		this.width = width;
		this.height = height;
	}
}
