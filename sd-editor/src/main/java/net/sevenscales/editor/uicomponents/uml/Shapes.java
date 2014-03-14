package net.sevenscales.editor.uicomponents.uml;

import java.util.Map;
import java.util.HashMap;

import net.sevenscales.domain.ElementType;


public class Shapes {
	private static final Map<ElementType,Proto> shapes;

	public static class Matrix {
		public double xx;
		public double xy;
		public double yx;
		public double yy;
		public double dx;
		public double dy;

		public Matrix(double xx, double xy, double yx, double yy, double dx, double dy) {
			this.xx = xx;
			this.xy = xy;
			this.yx = yx;
			this.yy = yy;
			this.dx = dx;
			this.dy = dy;
		}
	}

	public static class Proto {
		public String path;
		public float width;
		public float height;

		private Proto(String path, float width, float height) {
			this.path = path;
			this.width = width;
			this.height = height;
		}
	}

 // <g transform="translate(7.7738567e-7,-308.27059)">
 //  <path opacity="0.85500004" d="m83.703402,82.745628c0,9.906024-10.913134,17.936442-24.375166,17.936442-13.462033,0-24.375166-8.030418-24.375166-17.936442s10.913133-17.936443,24.375166-17.936443c13.462032,0,24.375166,8.030419,24.375166,17.936443z" transform="matrix(0.98649777,0,0,0.92248204,-33.527173,249.43923)" stroke="#000" stroke-miterlimit="4" stroke-dasharray="none" stroke-width="2" fill="none"/>
 // </g>

	static {
		shapes = new HashMap<ElementType,Proto>();
		shapes.put(ElementType.STAR5, new Proto("m 49.000000,19.434298 -12.098453,12.212955 2.693801,17.352755 -14.767181,-8.266410 -14.898450,8.104734 2.971834,-17.321878 -11.901550,-12.343754 16.603875,-2.439099 7.542888,-15.733593 7.289926,15.814431 z", 50, 50));
	}

	public static Proto get(String elementType) {
		return get(ElementType.getEnum(elementType));
	}

	public static Proto get(ElementType type) {
		return shapes.get(type);
	}
}