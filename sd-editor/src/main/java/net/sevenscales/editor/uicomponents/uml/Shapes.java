package net.sevenscales.editor.uicomponents.uml;

import java.util.Map;
import java.util.HashMap;

import net.sevenscales.domain.ElementType;


public class Shapes {
	private static final Map<ElementType,Group> shapes;

	public static class Matrix {
		public double a;
		public double b;
		public double c;
		public double d;
		public double e;
		public double f;

		public Matrix(double a, double b, double c, double d, double e, double f) {
			this.a = a;
			this.b = b;
			this.c = c;
			this.d = d;
			this.e = e;
			this.f = f;
		}
	}

	public static class Group {
		public Proto[] protos;

		// NOTE: important to keep as float or double; int will be really slow!
		public double width;
		public double height;

		public Group(Proto[] protos, double width, double height) {
			this.protos = protos;
			this.width = width;
			this.height = height;
		}
	}

	public static class Proto {
		public String path;

		private Proto(String path) {
			this.path = path;
		}
	}

 // <g transform="translate(7.7738567e-7,-308.27059)">
 //  <path opacity="0.85500004" d="m83.703402,82.745628c0,9.906024-10.913134,17.936442-24.375166,17.936442-13.462033,0-24.375166-8.030418-24.375166-17.936442s10.913133-17.936443,24.375166-17.936443c13.462032,0,24.375166,8.030419,24.375166,17.936443z" transform="matrix(0.98649777,0,0,0.92248204,-33.527173,249.43923)" stroke="#000" stroke-miterlimit="4" stroke-dasharray="none" stroke-width="2" fill="none"/>
 // </g>

	static {
		shapes = new HashMap<ElementType,Group>();
		shapes.put(ElementType.STAR5, new Group(new Proto[]{
			new Proto("m 49.000000,19.434298 -12.098453,12.212955 2.693801,17.352755 -14.767181,-8.266410 -14.898450,8.104734 2.971834,-17.321878 -11.901550,-12.343754 16.603875,-2.439099 7.542888,-15.733593 7.289926,15.814431 z")
		}, 50, 50));
		shapes.put(ElementType.ENVELOPE, new Group(new Proto[]{
			new Proto("m 1.000000,1.000000 0.000000,48.000000 98.000000,0.000000 0.000000,-47.875000 -48.375000,25.281250 -48.593750,-25.406250 -1.031250,0.000000 z"),
			new Proto("m 1.345292,1.121100 97.464868,0.000000")
		}, 100, 50));
	}

	public static Group get(String elementType) {
		return get(ElementType.getEnum(elementType));
	}

	public static Group get(ElementType type) {
		return shapes.get(type);
	}
}