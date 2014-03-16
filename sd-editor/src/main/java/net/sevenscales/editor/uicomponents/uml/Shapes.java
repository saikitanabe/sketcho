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
		public String style;

		private Proto(String path) {
			this.path = path;
		}

		private Proto(String path, String style) {
			this.path = path;
			this.style = style;
		}
	}

 // <g transform="translate(7.7738567e-7,-308.27059)">
 //  <path opacity="0.85500004" d="m83.703402,82.745628c0,9.906024-10.913134,17.936442-24.375166,17.936442-13.462033,0-24.375166-8.030418-24.375166-17.936442s10.913133-17.936443,24.375166-17.936443c13.462032,0,24.375166,8.030419,24.375166,17.936443z" transform="matrix(0.98649777,0,0,0.92248204,-33.527173,249.43923)" stroke="#000" stroke-miterlimit="4" stroke-dasharray="none" stroke-width="2" fill="none"/>
 // </g>

	static {
		shapes = new HashMap<ElementType,Group>();

		shapes.put(ElementType.STAR4, new Group(new Proto[]{
			new Proto("m 25,49.004685 -8.485281,-15.514719 -15.514719,-8.485280 15.514719,-8.485281 8.485280,-15.514719 8.485281,15.514719 15.514719,8.485280 -15.514719,8.485281 z ", "")
		}, 50, 50));

		shapes.put(ElementType.STAR5, new Group(new Proto[]{
			new Proto("m 49,19.434298 -12.098453,12.212955 2.693801,17.352755 -14.767181,-8.266410 -14.898450,8.104734 2.971834,-17.321878 -11.901550,-12.343754 16.603875,-2.439099 7.542888,-15.733593 7.289926,15.814431 z")
		}, 50, 50));

		shapes.put(ElementType.ENVELOPE, new Group(new Proto[]{
			new Proto("m 1,1 98,0 0,48 -98,0 z ", "stroke-linejoin:round;"),
			new Proto("m 1.268353,1.485500 48.725893,23.358900 48.620198,-23.676000 ", "stroke-linejoin:round;")
		}, 100, 50));

		shapes.put(ElementType.TRIANGLE, new Group(new Proto[]{
			new Proto("m 0.5,46.5 49,0 -24.5,-43 z ", "")
		}, 50, 50));

		shapes.put(ElementType.CLOUD, new Group(new Proto[]{
			new Proto("m 22.153869,1.000000 c -1.938210,0.000000 -3.653040,2.701700 -4.706519,6.851200 -1.971343,-2.831700 -4.604841,-4.474900 -7.159760,-4.102600 -3.801548,0.554100 -6.140021,5.381000 -5.686840,11.212400 -2.483592,2.505300 -3.874953,5.626800 -3.555494,8.715500 0.261420,2.527500 1.626471,4.623200 3.696934,6.082400 -0.603504,2.668300 -0.613678,5.453800 0.102422,7.878600 1.580652,5.352300 6.135476,6.971800 10.310444,3.769200 0.082390,0.301200 0.167326,0.598500 0.258494,0.891300 2.190984,7.035000 6.094714,8.817400 8.715592,3.980100 0.852926,-1.574200 1.461687,-3.660000 1.824081,-5.994000 1.555595,5.376000 4.802332,6.378000 7.642606,2.041100 0.775751,-1.184600 1.448235,-2.653600 1.999661,-4.299900 0.655541,1.438000 1.368079,2.692800 2.116714,3.694400 3.739349,5.002800 7.006321,1.798300 7.301200,-7.157500 0.053110,-1.612900 0.007900,-3.298800 -0.126808,-5.014200 2.643119,-1.706200 4.230775,-3.999500 4.106619,-6.429400 -0.095080,-1.861000 -1.180064,-3.522100 -2.940963,-4.817000 0.850135,-2.211900 1.446247,-4.398000 1.707028,-6.395400 0.928355,-7.110700 -2.788589,-9.149100 -8.305909,-4.551600 -0.243277,0.202700 -0.484909,0.418400 -0.726707,0.639500 -2.005789,-6.599300 -7.220956,-8.166200 -11.739469,-3.469800 -0.294922,0.306500 -0.579413,0.629900 -0.853514,0.972900 -1.038768,-2.784300 -2.439133,-4.497200 -3.979812,-4.497200 z ", "stroke-linejoin:round;")
		}, 50, 50));

		// shapes.put(ElementType.ENVELOPE, new Group(new Proto[]{
		// 	new Proto("m 1,1 0,48 98,0 0,-47.875 -48.375000,25.281250 z"),
		// 	new Proto("m 1.345292,1.121100 97.464868,0")
		// }, 100, 50));
	}

	public static Group get(String elementType) {
		return get(ElementType.getEnum(elementType));
	}

	public static Group get(ElementType type) {
		return shapes.get(type);
	}
}