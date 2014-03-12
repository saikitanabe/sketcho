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
		public Matrix matrix;
		public float width;
		public float height;

		private Proto(String path, Matrix matrix, float width, float height) {
			this.path = path;
			this.matrix = matrix;
			this.width = width;
			this.height = height;
		}
	}

 // <g transform="translate(7.7738567e-7,-308.27059)">
 //  <path opacity="0.85500004" d="m83.703402,82.745628c0,9.906024-10.913134,17.936442-24.375166,17.936442-13.462033,0-24.375166-8.030418-24.375166-17.936442s10.913133-17.936443,24.375166-17.936443c13.462032,0,24.375166,8.030419,24.375166,17.936443z" transform="matrix(0.98649777,0,0,0.92248204,-33.527173,249.43923)" stroke="#000" stroke-miterlimit="4" stroke-dasharray="none" stroke-width="2" fill="none"/>
 // </g>

	static {
		shapes = new HashMap<ElementType,Proto>();
		shapes.put(ElementType.STAR5, new Proto("m83.703402,82.745628a24.375166,17.936443,0,1,1,-48.750332,0,24.375166,17.936443,0,1,1,48.750332,0z", new Matrix(0.98377617,0,0,1.0581645,-83.365705 + 25.000001,275.7121 + -323.27059), 50, 40));
	}

	public static Proto get(String elementType) {
		return get(ElementType.getEnum(elementType));
	}

	public static Proto get(ElementType type) {
		return shapes.get(type);
	}
}