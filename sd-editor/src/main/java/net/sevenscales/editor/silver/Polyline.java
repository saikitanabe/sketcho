package net.sevenscales.editor.silver;

import com.google.gwt.user.client.Element;

import net.sevenscales.editor.diagram.shape.Info;
import net.sevenscales.editor.uicomponents.Point;

import java.util.ArrayList;

public class Polyline extends Info {
	private Element polylineNode;
	private String nodeType = "Polyline";
	
	public Polyline() {
	}

	public void setShape(ArrayList points) {
		String rawShape = "";
		for (int i = 0; i < points.size(); ++i) {
			Point p = (Point) points.get(i);
			rawShape += p.x + ',' + p.y + ' ';
		}
		
		setShape(rawShape);
	}
	
	private native void setShape(String rawShape)/*-{
		polylineNode.points = rawShape;
	}-*/;

}
