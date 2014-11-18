package net.sevenscales.editor.uicomponents.uml;

import java.util.Map;
import java.util.HashMap;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JavaScriptObject;

import net.sevenscales.domain.ElementType;
import net.sevenscales.domain.utils.SLogger;


public class Shapes {
	private static final SLogger logger = SLogger.createLogger(Shapes.class);

	static {
		SLogger.addFilter(Shapes.class);
	}

	private static final Map<ElementType,Group> shapes;
	private static final Map<ElementType,Group> sketchShapes;

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

	public static class JsPathData extends JavaScriptObject {
		protected JsPathData() {
		}

	  public final native String getCode()/*-{
	  	return this.code;
	  }-*/;
	  public final native String getCommand()/*-{
	  	return this.command;
	  }-*/;
	  public final native double getX()/*-{
	  	return this.x;
	  }-*/;
	  public final native double getY()/*-{
	  	return this.y;
	  }-*/;
	  public final native boolean isRelative()/*-{
	  	return this.relative;
	  }-*/;
	  public final native double getX1()/*-{
	  	return this.x1;
	  }-*/;
	  public final native double getY1()/*-{
	  	return this.y1;
	  }-*/;
	  public final native double getX2()/*-{
	  	return this.x2;
	  }-*/;
	  public final native double getY2()/*-{
	  	return this.y2;
	  }-*/;

	  // A command support
	  public final native double getRX()/*-{
	  	return this.rx;
	  }-*/;
	  public final native double getRY()/*-{
	  	return this.ry;
	  }-*/;
	  public final native int getXRotation()/*-{
	  	return this.xAxisRotation;
	  }-*/;
	  public final native int getLargeArc()/*-{
	  	return this.largeArc ? 1 : 0;
	  }-*/;
	  public final native int getSweep()/*-{
	  	return this.sweep ? 1 : 0;
	  }-*/;

	  public final String toPath(double factorX, double factorY) {
	  	String result = "";
	  	String code = getCode().toLowerCase();
	  	if (isRelative()) {
	  		result = code;
	  	} else {
	  		// some bug in parser and this is uppercase...
	  		// at least chrome doesn't seem to care about that
	  		// still to lower case as in spec if some browser doesn't work like that...
	  		result = code;
	  	}
	  	
	  	if ("c".equals(code)) {
		  	result += (getX1() * factorX) + "," + (getY1() * factorY) + " ";
		  	result += (getX2() * factorX) + "," + (getY2() * factorY) + " ";
	  	}

	  	if ("a".equals(code)) {
		  	result += (getRX() * factorX) + "," + (getRY() * factorY) + " " +
		  						getXRotation() + " " + getLargeArc() + " " + getSweep() + " ";
	  	}

	  	if ("v".equals(code)) {
		  	result += (getY() * factorY);
	  	}
	  	else if (!"z".equals(code)) {
		  	result += (getX() * factorX) + "," + (getY() * factorY);
	  	}
	  	// if ("m".equals(getCode())) {
		  // 	result += getX() + "," + getY();
	  	// } else if ("l".equals(getCode())) {
	  	// 	result += getX() + "," + getY();
	  	// } else if ("c".equals(getCode())) {
	  	// 	result += getX1() + "," + getY1() + " " + getX2() + "," + getY2() + " " + getX() + "," + getY();
	  	// }
	  	return result;
	  }

	  public final String toPathMove(int moveX, int moveY) {
	  	String code = getCode().toLowerCase();
	  	String result = code;
	  	if ("c".equals(code)) {
		  	result += (getX1()) + "," + (getY1()) + " ";
		  	result += (getX2()) + "," + (getY2()) + " ";
	  	}

	  	if ("a".equals(code)) {
		  	result += (getRX()) + "," + (getRY()) + " " +
		  						getXRotation() + " " + getLargeArc() + " " + getSweep() + " ";
	  	}

	  	if ("v".equals(code)) {
		  	result += (getY());
	  	}
	  	else if (!"z".equals(code) && !"m".equals(code)) {
		  	result += (getX()) + "," + (getY());
	  	}

	  	if ("m".equals(code)) {
		  	result += (getX() + moveX) + "," + (getY() + moveY);
	  	}
	  	return result;
	  }

	}

	public static class Proto {
		public JsArray<JsPathData> pathDatas;
		public String style;
		private boolean scalable;
		private double width;

		private Proto(String path) {
			this(path, null, true);
		}

		private Proto(String path, String style) {
			this(path, style, true);
		}

		private Proto(String path, String style, boolean scalable) {
			this.style = style;
			this.scalable = scalable;
			pathDatas = parse(path);

			width = getWidth();
		}

		private double getWidth() {
	  	double right = Double.MIN_VALUE;
	  	double left = Double.MAX_VALUE;
	  	double result = 0;
	  	double prevx = 0;
			for (int i = 0; i < pathDatas.length(); ++i) {
				JsPathData pd = pathDatas.get(i);

				double x = pd.getX() + prevx;
				if (x > right) {
					right = x;
				}
				if (x < left) {
					left = x;
				}
				result = right - left;
				prevx = x;
			}
			return result;
		}

		private native JsArray<JsPathData> parse(String d)/*-{
			var result = $wnd.svgPathParser.parse(d)
			return result
		}-*/;

		public String toPath(double factorX, double factorY, double mainWidth) {
			// could cache if prev factors are the same!
			String result = "";
			for (int i = 0; i < pathDatas.length(); ++i) {
				JsPathData current = pathDatas.get(i);
				if (scalable) {
					result += current.toPath(factorX, factorY);
				} else {
					// factorX = 1;
					// factorY = 1;
					// double shouldBeWidth = width * factorX;
					// double shouldBeX = current.getX() * factorX;
					// double moveX = shouldBeX + shouldBeWidth / 2;

					result += current.toPathMove((int) ((-1 * width / 2) + (mainWidth / 2 * factorX)), 0);
				}
			}

			// logger.debug("toPath factorX {}, factorY {} {}", factorX, factorY, result);
			return result;
		}
	}


 // <g transform="translate(7.7738567e-7,-308.27059)">
 //  <path opacity="0.85500004" d="m83.703402,82.745628c0,9.906024-10.913134,17.936442-24.375166,17.936442-13.462033,0-24.375166-8.030418-24.375166-17.936442s10.913133-17.936443,24.375166-17.936443c13.462032,0,24.375166,8.030419,24.375166,17.936443z" transform="matrix(0.98649777,0,0,0.92248204,-33.527173,249.43923)" stroke="#000" stroke-miterlimit="4" stroke-dasharray="none" stroke-width="2" fill="none"/>
 // </g>

	static {
		shapes = new HashMap<ElementType,Group>();
		sketchShapes = new HashMap<ElementType,Group>();

		shapes.put(ElementType.STAR4, new Group(new Proto[]{
			new Proto("m 25,49.004685 -8.485281,-15.514719 -15.514719,-8.485280 15.514719,-8.485281 8.485280,-15.514719 8.485281,15.514719 15.514719,8.485280 -15.514719,8.485281 z", "")
		}, 50, 50));

		shapes.put(ElementType.STAR5, new Group(new Proto[]{
			new Proto("m 49,19.434298 -12.098453,12.212955 2.693801,17.352755 -14.767181,-8.266410 -14.898450,8.104734 2.971834,-17.321878 -11.901550,-12.343754 16.603875,-2.439099 7.542888,-15.733593 7.289926,15.814431 z")
		}, 50, 50));

		shapes.put(ElementType.ENVELOPE, new Group(new Proto[]{
			new Proto("m 1,1 98,0 0,48 -98,0 z", "stroke-linejoin:round;"),
			new Proto("m 1.268353,1.485500 48.725893,23.358900 48.620198,-23.676000 ", "stroke-linejoin:round;")
		}, 100, 50));

		shapes.put(ElementType.TRIANGLE, new Group(new Proto[]{
			new Proto("m 0.5,46.5 49,0 -24.5,-43 z", "")
		}, 50, 50));

		shapes.put(ElementType.CLOUD, new Group(new Proto[]{
			new Proto("m 22.153869,1 c -1.938210,0 -3.653040,2.701700 -4.706519,6.851200 -1.971343,-2.831700 -4.604841,-4.474900 -7.159760,-4.102600 -3.801548,0.554100 -6.140021,5.381000 -5.686840,11.212400 -2.483592,2.505300 -3.874953,5.626800 -3.555494,8.715500 0.261420,2.527500 1.626471,4.623200 3.696934,6.082400 -0.603504,2.668300 -0.613678,5.453800 0.102422,7.878600 1.580652,5.352300 6.135476,6.971800 10.310444,3.769200 0.082390,0.301200 0.167326,0.598500 0.258494,0.891300 2.190984,7.035000 6.094714,8.817400 8.715592,3.980100 0.852926,-1.574200 1.461687,-3.660000 1.824081,-5.994000 1.555595,5.376000 4.802332,6.378000 7.642606,2.041100 0.775751,-1.184600 1.448235,-2.653600 1.999661,-4.299900 0.655541,1.438000 1.368079,2.692800 2.116714,3.694400 3.739349,5.002800 7.006321,1.798300 7.301200,-7.157500 0.053110,-1.612900 0.007900,-3.298800 -0.126808,-5.014200 2.643119,-1.706200 4.230775,-3.999500 4.106619,-6.429400 -0.095080,-1.861000 -1.180064,-3.522100 -2.940963,-4.817000 0.850135,-2.211900 1.446247,-4.398000 1.707028,-6.395400 0.928355,-7.110700 -2.788589,-9.149100 -8.305909,-4.551600 -0.243277,0.202700 -0.484909,0.418400 -0.726707,0.639500 -2.005789,-6.599300 -7.220956,-8.166200 -11.739469,-3.469800 -0.294922,0.306500 -0.579413,0.629900 -0.853514,0.972900 -1.038768,-2.784300 -2.439133,-4.497200 -3.979812,-4.497200 z", "stroke-linejoin:round;")
		}, 50, 50));

		shapes.put(ElementType.FIREWALL, new Group(new Proto[]{
			new Proto("m 26.499960,45.938400 -5.038943,3.561600 -20.961017,-9.043500 0.000004,-36.395200 5.038943,-3.561300 20.961081,9.043700 z", "stroke-linejoin:round;"),
			new Proto("m 2.706990,11.427400 18.340945,7.913300 m -14.458863,-12.565500 0,6.347300 m 8.163128,21.736400 0,6.347200 m -8.639272,-9.987600 0,6.347200 m 4.092287,-10.670200 0,6.347300 m 4.774335,-10.215200 0,6.347300 m -8.866621,-10.215200 0,6.347200 m 4.774335,-10.215100 0,6.347200 m -7.957214,14.457800 18.340946,7.913200 m -18.340946,-13.913200 18.340946,7.913200 m -18.340946,-13.913300 18.340946,7.913300 m -18.340946,-13.913300 18.340946,7.913200 m -0.000002,-13.053200 -0.000068,34.043300 m -18.225058,-41.840600 18.299842,7.902400 m 0,0.111500 4.997839,-3.572300 ", "stroke-linejoin:round;")
		}, 27, 50));

		shapes.put(ElementType.BUBBLE, new Group(new Proto[]{
			new Proto("m 2.360450,36.388330 0,-23.086280 c 0,-7.092080 5.637940,-12.801600 12.641130,-12.801600 l 0,0.000030 71.857280,0 c 7.003190,0 12.641140,5.709520 12.641140,12.801600 l 0,23.086280 c 0,7.092090 -5.638000,12.775710 -12.641140,12.801610 l -71.944890,0.266160 c -3.127830,-0.061000 -5.924880,-1.373520 -7.997070,-3.018980 -1.164830,2.074880 -4.540840,1.898310 -6.416900,3.063300 4.322270,-5.075860 2.132430,-7.748470 1.860450,-13.112120 z", "")
		}, 100, 50));

		shapes.put(ElementType.BUBBLE_R, new Group(new Proto[]{
			new Proto("m 97.639550,36.388330 0,-23.086280 c 0,-7.092080 -5.637940,-12.801600 -12.641130,-12.801600 l 0,0.000030 -71.857280,0 c -7.003190,0 -12.641140,5.709520 -12.641140,12.801600 l 0,23.086280 c 0,7.092090 5.638000,12.775710 12.641140,12.801610 l 71.944890,0.266160 c 3.127830,-0.061000 5.924880,-1.373520 7.997070,-3.018980 1.164830,2.074880 4.540840,1.898310 6.416900,3.063300 -4.322270,-5.075860 -2.132430,-7.748470 -1.860450,-13.112120 z", "")
		}, 100, 50));

		shapes.put(ElementType.CIRCLE, new Group(new Proto[]{
			new Proto("m49.500201,25.000118 a24.500050,24.500057 0 1 1 -49.000103,0a24.500050,24.500057 0 1 1 49.000103,0 z", "")
		}, 50, 50));

		shapes.put(ElementType.SMILEY, new Group(new Proto[]{
			new Proto("m 49.500166,25.000057 a 24.500057,24.500035 0 1 1 -49.000113,0 24.500057,24.500035 0 1 1 49.000113,0 z", ""),
			new Proto("m 8.623853,30.917500 c 15.963302,20 32.155963,0.321100 32.155963,0.321100", "fill:none;"),
			new Proto("m 19.633027,19.678887 a 1.743119,2.522936 0 1 1 -3.486239,0 1.743119,2.522936 0 1 1 3.486239,0 z", ""),
			new Proto("m 33.486238,19.678887 a 1.743119,2.522936 0 1 1 -3.486239,0 1.743119,2.522936 0 1 1 3.486239,0 z", ""),
			new Proto("m 6.055046,31.009100 c 3.394495,0.917500 4.036697,-2.201700 4.036697,-2.201700 ", ""),
			new Proto("m 43.577982,31.376100 c -3.394495,0.917500 -4.036697,-2.201700 -4.036697,-2.201700 ", "")
		}, 50, 50));

		shapes.put(ElementType.POLYGON4, new Group(new Proto[]{
			new Proto("m 40.132126,49.643455 -30.283670,-0.010736 -9.348456,-30.283675 24.506007,-18.705604 24.494001,18.722977 z", "")
		}, 50, 50));

		shapes.put(ElementType.POLYGON8, new Group(new Proto[]{
			new Proto("m 35.146457,49.499952 -20.295937,-0.001287 -14.350511,-14.352278 0.001287,-20.295936 14.352278,-14.350511 20.295936,0.001253 14.350511,14.352278 -0.001287,20.295937 z", "")
		}, 50, 50));

		shapes.put(ElementType.ARROW_UP, new Group(new Proto[]{
			new Proto("m 12.531496,0.5 -12.031496,17.621200 5.417323,0 0,31.378800 13.165354,0 0,-31.347400 5.417322,0 -11.968503,-17.652600 z", "")
		}, 25, 50));

		shapes.put(ElementType.ARROW_DOWN, new Group(new Proto[]{
			new Proto("m 12.531496,49.5 -12.031496,-17.621200 5.417323,0 0,-31.378800 13.165354,0 0,31.347400 5.417322,0 -11.968503,17.652600 z", "")
		}, 25, 50));

		shapes.put(ElementType.ARROW_RIGHT, new Group(new Proto[]{
			new Proto("m 49.5,12.531500 -17.621200,-12.031500 0,5.417300 -31.378800,0 0,13.165400 31.347400,0 0,5.417300 17.652600,-11.968500 z", "")
		}, 50, 25));

		shapes.put(ElementType.ARROW_LEFT, new Group(new Proto[]{
			new Proto("m 0.5,12.531500 17.621200,-12.031500 0,5.417300 31.378800,0 0,13.165400 -31.347400,0 0,5.417300 -17.652600,-11.968500 z", "")
		}, 50, 25));

		shapes.put(ElementType.IPHONE, new Group(new Proto[]{
			new Proto("m 4.193653,0.5 15.612694,0 c 2.046284,0 3.693653,1.647400 3.693653,3.693600 l 0,41.612700 c 0,2.046300 -1.647369,3.693700 -3.693653,3.693700 l -15.612694,0 c -2.046284,0 -3.693653,-1.647400 -3.693653,-3.693700 l 0,-41.612700 c 0,-2.046200 1.647369,-3.693600 3.693653,-3.693600 z", "fill:none;"),
			new Proto("m 12.5,2.733845 a 0.5,0.5 0 1 1 -1,0 0.5,0.5 0 1 1 1,0 z", ""),
			new Proto("m 14,45.688237 a 2,2.000001 0 1 1 -4,0 2,2.000001 0 1 1 4,0 z", ""),
			new Proto("m 8.351575,5.058100 7.296850,0", ""),
			new Proto("m 1.635158,7.545500 20.729684,0 0,34.079600 -20.729684,0 z", "fill:none;")
		}, 24, 50));

		shapes.put(ElementType.WEB_BROWSER, new Group(new Proto[]{
			new Proto("m 0.25,0.25 49.5,0 0,49.5 -49.5,0 z", "fill:none;"),
			new Proto("m 0.25,0.25 49.5,0 0,5.945400 -49.5,0 z", "fill:none;"),
			new Proto("m 10.490104,2.3082 37.339534,0 0,1.791900 -37.339534,0 z", "fill:none;"),
			new Proto("m 2.549995,2.1704 -1.102701,1.0337 1.102701,0.896 ", "fill:none;"),
			new Proto("m 1.585132,3.204142 1.654051,0 ", ""),
			new Proto("m 4.481076,2.1704 1.102701,1.0337 -1.102701,0.896 ", "fill:none;"),
			new Proto("m 5.445939,3.204100 -1.654051,0 ", ""),
			new Proto("m 8.780560,3.787900 c -0.183226,0.183200 -0.436352,0.2966 -0.715946,0.2966 -0.559188,0 -1.0125,-0.453300 -1.0125,-1.0125 0,-0.559300 0.453312,-1.0125 1.0125,-1.0125 0.279594,0 0.588543,0.169100 0.771770,0.463900 ", "fill:none;"),
			new Proto("m 8.886836,2.802148 -0.487970,-0.254333 0.464244,-0.295428 z", "")
		}, 50, 50));

		shapes.put(ElementType.RECT, new Group(new Proto[]{
			new Proto("m 0.25,0.25 49.5,0 0,34.5 -49.5,0 z", "")
		}, 50, 35));

		shapes.put(ElementType.SWITCH, new Group(new Proto[]{
			new Proto("m 0.766624,10.664652 24.999980,12.857130 23.405109,-11.463840 -24.833680,-12.464710 z ", "fill:none"),
			new Proto("m 0.662019,10.538384 -0.000029,10.310087 24.747444,13.322597 0,-10.649286 ", "fill:none"),
			new Proto("m 49.102319,12.158574 -0.121452,10.291782 -23.571407,11.772083 0.024870,-10.942900 ", "fill:none"),
			new Proto("m 20.858430,2.604705 6.446425,0.339468 0.339282,4.410702 -2.374997,-1.696546 -4.749995,3.053624 -3.392854,-2.035749 5.767854,-2.714156 z ", ""),
			new Proto("m 28.961761,20.531902 -6.446425,-0.339468 -0.339285,-4.410436 2.374997,1.696016 4.749998,-3.053359 3.392853,2.035749 -5.767853,2.714156 z ", ""),
			new Proto("m 20.293499,15.982183 -6.446424,-0.339468 -0.339283,-4.410437 2.374998,1.696016 4.749995,-3.053358 3.392853,2.035750 -5.767854,2.714155 z ", ""),
			new Proto("m 29.013907,7.326542 6.446424,0.339203 0.339283,4.410702 -2.374998,-1.696281 -4.749995,3.053359 -3.392853,-2.035485 5.767851,-2.714420 z ", "")
		}, 50, 35));

		shapes.put(ElementType.ROUTER, new Group(new Proto[]{
			new Proto("m 25.024070,0.407736 c -13.586462,0 -24.631789,5.049913 -24.631789,11.325102 l 0,11.504865 c 0,6.275190 11.045327,11.370043 24.631789,11.370043 13.586461,0 24.573558,-5.094853 24.573558,-11.370043 l 0,-11.504865 c 0,-6.275189 -10.987097,-11.325102 -24.573558,-11.325102 z ", "fill:none;"),
			new Proto("m 49.607688,11.754521 a 24.600462,11.362238 0 1 1 -49.200924,0 24.600462,11.362238 0 1 1 49.200924,0 z ", "fill:none;"),
			new Proto("m 27.063013,11.615851 0.591448,5.310802 1.979647,-1.309176 6.574362,3.806381 4.804257,-2.402835 -6.702474,-3.582272 1.784037,-1.309041 z ", ""),
			new Proto("m 23.035014,10.073585 -0.591448,-4.405937 -2.175056,1.309176 -6.769773,-4.258813 -4.608848,2.704457 6.897885,3.582273 -1.588627,1.158228 z ", ""),
			new Proto("m 39.988052,3.627231 -9.125742,0.045272 2.501081,1.535408 -6.958802,3.295466 4.254985,2.051231 6.887052,-3.659964 2.891575,1.384402 z ", ""),
			new Proto("m 9.808246,18.813654 8.532502,-0.067910 -2.231254,-1.518447 7.463915,-3.445628 -4.248495,-2.184661 -7.357951,3.521837 -2.231022,-1.518289 z ", "")
					}, 50, 35));

		shapes.put(ElementType.DESKTOP, new Group(new Proto[]{
			new Proto("m 0.996463,0.996300 48.007076,0 0,31.108200 -48.007076,0 z ", "fill:none;stroke-width:5px;stroke-linecap:round;stroke-linejoin:round;stroke-miterlimit:4;"),
			new Proto("m 30.027283,33.581400 c 0.143291,3.075900 0.144906,5.327200 2.847456,6.418500 l -15.688919,0 c 3.237678,-1.149500 2.963535,-3.455800 3.014953,-6.418500 z ", "fill-opacity:1;stroke: none;fill:bordercolor;"),
			new Proto("m 1.650634,29.419800 46.224986,0 c 0.624870,0 1.127924,0.503000 1.127924,1.127900 l 0,0.902300 c 0,0.624900 -0.503054,1.128000 -1.127924,1.128000 l -46.224986,0 c -0.624870,0 -1.127924,-0.503100 -1.127924,-1.128000 l 0,-0.902300 c 0,-0.624900 0.503054,-1.127900 1.127924,-1.127900 z ", "fill-opacity:1;stroke: none;fill:bordercolor;")
					}, 50, 40));

		shapes.put(ElementType.LAPTOP, new Group(new Proto[]{
			new Proto("m 7.020842,0.5711 35.958315,0 c 0.425769,0 0.768537,0.3428 0.768537,0.7686 l 0,22.4419 c 0,0.4257 -0.342768,0.7685 -0.768537,0.7685 l -35.958315,0 c -0.425769,0 -0.768537,-0.342800 -0.768537,-0.7685 l 0,-22.441900 c 0,-0.4258 0.342767,-0.7686 0.768537,-0.7686 z ", "fill:none;"),
			new Proto("m 0.829383,26.3273 c -0.470764,0 -0.829384,0.3617 -0.829384,0.836300 0,0.474900 0.358620,0.8364 0.829384,0.8364 l 48.341233,0 c 0.470764,0 0.829384,-0.3615 0.829384,-0.8364 0,-0.474600 -0.358620,-0.8363 -0.829384,-0.8363 l -18.838863,0 c -0.03312,0.4305 -0.393679,0.7767 -0.829384,0.7767 l -8.116113,0 c -0.435705,0 -0.796272,-0.346200 -0.829384,-0.7767 l -19.727489,0 z ", ""),
			new Proto("m -0,26.327500 0,0.597400 20.912322,0 c -0.186085,-0.1396 -0.336406,-0.3498 -0.35545,-0.5974 l -20.556872,0 z m 30.331753,0 c -0.018200,0.235900 -0.124322,0.4577 -0.296208,0.5974 l 19.964455,0 0,-0.5974 -19.668247,0 z ", "")
		}, 50, 28));

		shapes.put(ElementType.SERVER2, new Group(new Proto[]{
			new Proto("m 3.644492,0 17.71102,0 c 2.019047,0 3.64449,1.6254 3.64449,3.6445 l 0,42.711 c 0,2.019 -1.625443,3.6445 -3.644490,3.644500 l -17.711020,0 c -2.019047,0 -3.644490,-1.6255 -3.644490,-3.6445 l 0,-42.711 c 0,-2.0191 1.625443,-3.644500 3.644490,-3.644500 z ", "fill:bordercolor;"),
			new Proto("m 1.976496,2.489200 21.047013,0 0,5.8982 -21.047013,0 z ", "fill:bgcolor;stroke-color:#fefefe;fill-opacity:1;"),
			new Proto("m 1.976496,9.409100 21.047013,0 0,5.8983 -21.047013,0 z ", "fill:bgcolor;stroke-color:#fefefe;fill-opacity:1;"),
			new Proto("m 1.976496,16.329000 21.047013,0 0,5.8983 -21.047013,0 z ", "fill:bgcolor;stroke-color:#fefefe;fill-opacity:1;"),
			new Proto("m 14.901691,30.034619 a 2.401708,2.4329 0 1 1 -4.803417,0 2.401708,2.432900 0 1 1 4.803417,0 z ", "fill:bgcolor;stroke-color:#fefefe;fill-opacity:1;")
		}, 25, 50));

		shapes.put(ElementType.TABLET_UP, new Group(new Proto[]{
			new Proto("m 4.39569,0.6193 31.2086,0 c 2.0921,0 3.77635,1.6843 3.77635,3.7764 l 0,41.2087 c 0,2.092 -1.68425,3.7763 -3.77635,3.7763 l -31.2086,0 c -2.0921,0 -3.77635,-1.6843 -3.77635,-3.7763 l 0,-41.2087 c 0,-2.0921 1.68425,-3.7764 3.77635,-3.7764 z ", "fill:bordercolor;"),
			new Proto("m 21,46.9324 c 0,0.5523 -0.447716,1 -1,1 -0.552285,0 -1,-0.4477 -1,-1 0,-0.5523 0.447715,-1 1,-1 0.552284,0 1,0.4477 1,1 z ", "fill:bgcolor;stroke:none;"),
			new Proto("m 3.00791,5.4139 33.9841,0 0,38.7484 -33.9841,0 z ", "fill:bgcolor;stroke:none;")
		}, 40, 50));

		shapes.put(ElementType.TABLET_HORIZONTAL, new Group(new Proto[]{
			new Proto("m 49.4205,31.1238 0,-27.2479 c 0,-1.8265 -1.68706,-3.2969 -3.78259,-3.2969 l -41.2762,0 c -2.09543,0 -3.78249,1.4704 -3.78249,3.2969 l 0,27.2479 c 0,1.8265 1.68706,3.297 3.78249,3.297 l 41.2762,0 c 2.09553,0 3.78259,-1.4705 3.78259,-3.297 z ", "fill:bordercolor;"),
			new Proto("m 3.03149,16.6267 c -0.553205,0 -1.00164,0.3909 -1.00164,0.8732 0,0.4822 0.448434,0.8731 1.00164,0.8731 0.553205,0 1.00164,-0.3909 1.00164,-0.8731 0,-0.4823 -0.448434,-0.8732 -1.00164,-0.8732 z ", "fill:bgcolor;stroke:none;"),
			new Proto("m 44.618,32.3354 0,-29.671 -38.8119,0 0,29.671 z ", "fill:bgcolor;stroke:none;")
		}, 50, 35));

		shapes.put(ElementType.OLD_PHONE, new Group(new Proto[]{
			new Proto("m 3.04036,0 17.9193,0 c 1.68436,0 3.04036,1.356 3.04036,3.0403 l 0,43.9193 c 0,1.6844 -1.356,3.0404 -3.04036,3.0404 l -17.9193,0 c -1.68436,0 -3.04036,-1.356 -3.04036,-3.0404 l 0,-43.9193 c 0,-1.6843 1.356,-3.0403 3.04036,-3.0403 z ", "fill:bordercolor;"),
			new Proto("m 2.42553,2.3136 19.1489,0 0,23.9074 -19.1489,0 z ", "fill:bgcolor;stroke:none;"),
			new Proto("m 9.49127,28.3738 5.04255,0 0,4.82 -5.04255,0 z ", "fill:bgcolor;stroke:none;"),
			new Proto("m 16.6208,29.5627 5.04255,0 0,2.4422 -5.04255,0 z ", "fill:bgcolor;stroke:none;"),
			new Proto("m 2.3617,29.5627 5.04255,0 0,2.4422 -5.04255,0 z ", "fill:bgcolor;stroke:none;"),
			new Proto("m 2.3617,35.604 5.04255,0 0,2.4421 -5.04255,0 z ", "fill:bgcolor;stroke:none;"),
			new Proto("m 16.6208,35.604 5.04255,0 0,2.4421 -5.04255,0 z ", "fill:bgcolor;stroke:none;"),
			new Proto("m 9.49127,40.1476 5.04255,0 0,2.4421 -5.04255,0 z ", "fill:bgcolor;stroke:none;"),
			new Proto("m 16.6208,40.1476 5.04255,0 0,2.4421 -5.04255,0 z ", "fill:bgcolor;stroke:none;"),
			new Proto("m 2.3617,40.1476 5.04255,0 0,2.4421 -5.04255,0 z ", "fill:bgcolor;stroke:none;"),
			new Proto("m 9.49127,35.604 5.04255,0 0,2.4421 -5.04255,0 z ", "fill:bgcolor;stroke:none;"),
			new Proto("m 2.3617,44.6909 5.04255,0 0,2.4422 -5.04255,0 z ", "fill:bgcolor;stroke:none;"),
			new Proto("m 9.49127,44.6909 5.04255,0 0,2.4422 -5.04255,0 z ", "fill:bgcolor;stroke:none;"),
			new Proto("m 16.6208,44.6909 5.04255,0 0,2.4422 -5.04255,0 z ", "fill:bgcolor;stroke:none;")
		}, 24, 50));

		shapes.put(ElementType.ANDROID, new Group(new Proto[]{
			new Proto("m 5.77398,0 14.452,0 c 3.19878,0 5.77398,2.5752 5.77398,5.774 l 0,38.452 c 0,3.1988 -2.57519,5.774 -5.77398,5.774 l -14.452,0 c -3.19878,0 -5.77398,-2.5752 -5.77398,-5.774 l 0,-38.452 c 0,-3.1988 2.57519,-5.774 5.77398,-5.774 z ", "fill:bordercolor;"),
			new Proto("m 2.1572,6.576 21.6856,0 0,36.848 -21.6856,0 z ", "fill:bgcolor;stroke:none;"),
			new Proto("m 15.1274,46.6587 c 0,0.515806 -0.952467,0.93395 -2.12739,0.93395 -1.17493,0 -2.1274,-0.418144 -2.1274,-0.93395 0,-0.515808 0.952467,-0.93395 2.1274,-0.93395 1.17493,0 2.12739,0.418142 2.12739,0.93395 z ", "fill:bgcolor;stroke:none;")
		}, 26, 50));

		shapes.put(ElementType.LIGHTBULB, new Group(new Proto[]{
			new Proto("m 22.4676,10.5797 c -6.56901,0 -11.8905,5.1708 -11.8905,11.5437 0,2.3507 0.737594,4.538 1.98175,6.3616 2.05617,3.9241 4.36366,5.6306 4.41982,10.4001 l 11.1459,0 c 0.115967,-4.9336 2.54553,-6.7664 4.43191,-10.4001 1.39298,-1.5069 1.7799,-4.4684 1.80159,-6.3616 0,-6.3729 -5.32148,-11.5437 -11.8905,-11.5437 z ", ""),
			new Proto("m 18.4085,40.0784 8.02938,0 c 0.581965,0 1.05048,0.4685 1.05048,1.0504 0,0.582 -0.468513,1.0505 -1.05048,1.0505 l -8.02938,0 c -0.581965,0 -1.05048,-0.4685 -1.05048,-1.0505 0,-0.5819 0.468513,-1.0504 1.05048,-1.0504 z ", "fill:none;"),
			new Proto("m 18.4085,42.7311 8.02938,0 c 0.581965,0 1.05048,0.4685 1.05048,1.0504 0,0.582 -0.468513,1.0505 -1.05048,1.0505 l -8.02938,0 c -0.581965,0 -1.05048,-0.4685 -1.05048,-1.0505 0,-0.5819 0.468513,-1.0504 1.05048,-1.0504 z ", "fill:none;"),
			new Proto("m 18.4411,45.2823 8.02938,0 c 0.581965,0 1.05048,0.4685 1.05048,1.0505 0,0.582 -0.468513,1.0505 -1.05048,1.0505 l -8.02938,0 c -0.581964,0 -1.05048,-0.4685 -1.05048,-1.0505 0,-0.582 0.468514,-1.0505 1.05048,-1.0505 z ", "fill:none;"),
			new Proto("m 19.4727,48.0741 c 0.369015,0.8074 1.06096,1.3087 1.88849,1.7116 0.633291,0.3084 1.46226,0.262 2.11641,0 0.797147,-0.3192 1.41094,-0.7966 1.85593,-1.7762 z ", "fill:none;"),
			new Proto("m 5.94834,37.4973 4.15444,-4.05476 c 0.304183,-0.296884 0.790202,-0.293115 1.08974,0.00844471 0.299534,0.30156 0.295805,0.78334 -0.00837815,1.08022 l -4.15444,4.05476 c -0.304176,0.296877 -0.790201,0.293101 -1.08974,-0.00845879 -0.299534,-0.30156 -0.295798,-0.783333 0.00837804,-1.08021 z ", "fill:bordercolor;"),
			new Proto("m 39.0558,37.5618 -4.15444,-4.05476 c -0.304176,-0.296877 -0.790202,-0.293115 -1.08974,0.00845181 -0.299534,0.30156 -0.29579,0.78334 0.0083852,1.08022 l 4.15444,4.05476 c 0.304176,0.296877 0.790194,0.293108 1.08973,-0.0084517 0.299541,-0.301567 0.295805,-0.78334 -0.008371,-1.08022 z ", "fill:bordercolor;"),
			new Proto("m 37.886,22.9798 5.84059,-0.0467203 c 0.423317,-0.00338622 0.761552,-0.346903 0.758373,-0.770291 -0.00317814,-0.423288 -0.34653,-0.761353 -0.769847,-0.757966 l -5.84059,0.0467203 c -0.423317,0.00338622 -0.761552,0.346903 -0.758374,0.770191 0.00317889,0.423388 0.346531,0.761453 0.769848,0.758066 z ", "fill:bordercolor;"),
			new Proto("m 1.28836,22.9797 5.84059,-0.0467203 c 0.423317,-0.00338622 0.761552,-0.346903 0.758374,-0.770191 -0.00317814,-0.423288 -0.34653,-0.761353 -0.769848,-0.757966 l -5.84059,0.0467203 c -0.423317,0.00338622 -0.761552,0.346903 -0.758374,0.770191 0.00317814,0.423288 0.34653,0.761353 0.769848,0.757966 z ", "fill:bordercolor;"),
			new Proto("m 34.7476,11.2245 4.08769,-4.12042 c 0.299383,-0.30178 0.295637,-0.783917 -0.00839155,-1.08102 -0.304028,-0.2971 -0.789814,-0.29334 -1.0892,0.00844033 l -4.08769,4.12042 c -0.29939,0.301787 -0.295644,0.783924 0.00838451,1.08102 0.304028,0.2971 0.789814,0.29334 1.0892,-0.00844743 z ", "fill:bordercolor;"),
			new Proto("m 11.2494,9.98146 -4.15444,-4.05476 c -0.304176,-0.296877 -0.790202,-0.293115 -1.08974,0.00844471 -0.299534,0.30156 -0.29579,0.78334 0.0083852,1.08022 l 4.15444,4.05476 c 0.304176,0.296877 0.790202,0.293115 1.08974,-0.00844471 0.299534,-0.30156 0.29579,-0.78334 -0.0083852,-1.08022 z ", "fill:bordercolor;"),
			new Proto("m 21.6435,1.29278 0.0469029,5.76821 c 0.00347031,0.426786 0.34985,0.767836 0.776635,0.764684 0.426786,-0.00315276 0.767578,-0.34928 0.764108,-0.776065 l -0.0469029,-5.76821 c -0.00347031,-0.426786 -0.34985,-0.767836 -0.776636,-0.764684 -0.426785,0.00315275 -0.767577,0.34928 -0.764107,0.776065 z ", "fill:bordercolor;"),
			new Proto("m 26.1069,24.375 c -0.467679,0.033 -0.972758,0.1942 -1.33317,0.3931 -1.13684,0.6273 -2.05558,2.9226 -2.27,3.5024 -0.259948,-0.6887 -1.14118,-2.8637 -2.23397,-3.4666 -0.720833,-0.3978 -1.96329,-0.6605 -2.45016,0 -0.591598,0.8024 0.164702,2.1844 0.900794,2.8591 0.982204,0.9003 3.92746,0.8935 3.92746,0.8935 0,0 0.000525,-0.034 0,-0.036 0.539788,-0.011 2.8424,-0.097 3.71127,-0.8935 0.736092,-0.6746 1.49239,-2.0566 0.900794,-2.8591 -0.243436,-0.3302 -0.685337,-0.4259 -1.15302,-0.3931 z ", ""),
			new Proto("m 22.4693,28.5688 0,10.6875 ", "")
		}, 45, 50));

		shapes.put(ElementType.CLASS, new Group(new Proto[]{
			new Proto("m2.135216,0 l45.729568,0 c1.182910,0 2.135216,0.952300 2.135216,2.135200l0,30.729600 c0,1.182900 -0.952306,2.135200 -2.135216,2.135200l-45.729568,0 c-1.182910,0 -2.135216,-0.952300 -2.135216,-2.135200l0,-30.729600 c0,-1.182900 0.952306,-2.135200 2.135216,-2.135200z", "")
		}, 50, 35));

		// new official shapes where conversion from generic element to legacy official doesn't work.
		shapes.put(ElementType.ACTIVITY_START2, new Group(new Proto[]{
new Proto("m43.990518,21.926754 a21.981054,21.891428 0 1 1 -43.962108,0a21.981054,21.891428 0 1 1 43.962108,0 z", "")
		}, 44.033047, 43.847889));
		shapes.put(ElementType.USE_CASE, new Group(new Proto[]{
			new Proto("m49.999996,14.999995 c0,8.008126 -11.469022,14.999997 -24.999997,14.999997c-13.530976,0 -24.999999,-6.991864 -24.999999,-14.999990c0,-8.008128 11.469023,-15.000004 24.999999,-15.000004c13.530975,0 24.999997,6.991869 24.999997,14.999997 z", "")
		}, 50, 30));

		// sketchShapes
		sketchShapes.put(ElementType.RECT, new Group(new Proto[]{
			new Proto("m 49.1459,1.3847 c 0.104761,6.9675 0.03506,9.4865 0.26563,16.6366 0.0014,2.9517 0.309709,14.36 -0.09515,16.501 -2.42503,0.039 -16.1332,-0.2513 -19.0888,-0.068 -17.2572,-0.6772 -13.5226,0.3262 -29.4849,-0.195 0.082078,-2.3524 -0.00872,-17.0765 -0.043292,-19.4423 0.120578,-5.8357 0.25667,-8.5167 -0.0317819,-14.2045 8.64905,0.2209 16.6681,0.5994 27.3095,0.3107 l 21.8322,-0.326 ", "")
		}, 50, 35));

		sketchShapes.put(ElementType.CLASS, new Group(new Proto[]{
			new Proto("m2.135216,0 c0,0 13.550818,0.429500 22.864784,0c9.313966,-0.429500 22.864784,0 22.864784,0c1.182910,0 2.135216,0.952300 2.135216,2.135200c0,0 -0.367897,1.465300 -0.095000,8.879600c0.202533,5.502500 0.095000,21.850000 0.095000,21.850000c0.040976,1.182200 -0.952306,2.135200 -2.135216,2.135200c0,0 -15.529302,0.190700 -22.864784,0c-7.621595,0.667600 -22.864784,0 -22.864784,0c-1.182910,0 -2.135216,-0.952300 -2.135216,-2.135200c0,0 0.286113,-7.668200 0,-15.364800c-0.286113,-7.696600 0,-15.364800 0,-15.364800c0,-1.182900 0.952306,-2.135200 2.135216,-2.135200z", "")
		}, 50, 35));

		sketchShapes.put(ElementType.CIRCLE, new Group(new Proto[]{
new Proto("m50.111734,26.338493 c0,6.765504 -3.258699,12.695418 -8.764277,17.239573c-6.458227,4.385965 -8.471617,6.504974 -15.008932,6.422130c-9.107611,-0.115416 -14.533824,-3.521210 -19.358653,-8.541225c-3.456223,-4.969577 -6.756712,-11.366319 -6.756712,-18.131823c0,-6.765504 2.949945,-11.629570 7.202837,-15.901178c4.643107,-5.989404 13.708602,-7.382574 20.473967,-7.425891c6.765501,-0.043317 9.943628,3.039058 14.741992,7.572412c4.553034,4.085444 7.469778,12.000498 7.469778,18.766003 z", "")
		}, 50, 50));

		sketchShapes.put(ElementType.USE_CASE, new Group(new Proto[]{
new Proto("m10.620043,2.079800 c10.179862,-3.892000 34.138710,-2.242100 38.375122,5.731900c4.236163,7.974100 -6.479323,16.695200 -18.689248,17.692700c-12.210426,0.996600 -25.209891,-1.026200 -29.155329,-9.220400c-3.239036,-6.728400 0.997378,-10.964900 9.469455,-14.204200 z", "")
		}, 50, 25));

		sketchShapes.put(ElementType.ACTOR, new Group(new Proto[]{
new Proto("m16.748162,3.397600 c-2.525763,2.264400 -4.332541,6.573700 -3.557929,9.986300c1.198431,4.056900 6.755735,7.568500 10.843317,7.030000c4.986139,-0.438200 9.645852,-3.468400 10.621135,-8.338800c0.582426,-3.752600 -1.284851,-7.921500 -4.982030,-10.490000c-1.253076,-0.473300 -3.896463,-1.708500 -4.791201,-1.503000c-3.285653,0.351200 -6.030954,1.035800 -8.133292,3.315500 z", ""),
new Proto("m37.061041,59.030800 c0.038406,1.046100 -5.715838,-0.344000 -8.378814,-0.053800c-4.396158,-0.173500 -7.022591,0.397400 -11.664645,0.446200c-1.393262,0.125200 -5.890469,-0.037800 -5.925190,-0.823900c-0.104215,-2.359600 -0.100291,-10.917400 0.147548,-19.988200c-0.044333,-1.047900 0.165388,10.800000 -0.174384,15.350600c-2.697249,0.510200 -6.338981,0.505400 -9.576678,0.650600c-2.697020,-0.883900 -0.869439,-19.022000 -0.252941,-24.479200c0.318936,-2.823200 5.780661,-3.664300 8.935270,-3.761400c4.010204,-0.123400 8.015158,-0.445800 12.027198,-0.468200c6.957122,-0.038900 13.927515,-0.009100 20.863671,0.578600c3.669393,0.902800 4.457717,5.855700 4.241453,8.723000l0.465381,9.832500 c0.155185,5.704800 0.624067,7.485000 -0.424298,10.166900c-0.192715,0.493000 -10.236172,1.569400 -10.437779,-2.078000c0.261570,-5.645100 -0.257949,-17.986100 -0.004741,-14.308700c0.218509,9.003200 0.120544,19.166800 0.158949,20.213000 z", "")
		}, 48.056293, 59.500584));

		sketchShapes.put(ElementType.SERVER, new Group(new Proto[]{
new Proto("m0.025649,8.590800 l0.611944,37.958200 l13.469328,8.571300 l-0.612354,-36.734100 ", ""),
new Proto("m31.249553,0.019500 c0,0 -29.387662,5.509900 -29.999606,7.347000c-0.612354,1.836700 12.244620,11.020300 12.244620,11.020300l33.060146,-10.407900 l-15.305160,-7.959400  z", ""),
new Proto("m46.554713,7.978400 l-0.611943,37.958200 l-31.835849,9.183700 l-0.671921,-36.734100 ", ""),
new Proto("m4.923248,22.671800 l4.285660,3.061200 ", "")
		}, 46.579338, 55.141220));

		sketchShapes.put(ElementType.STORAGE, new Group(new Proto[]{
new Proto("m0.139629,8.109600 v25.554700 c-0.085370,0 10.417202,8.051700 25.819637,7.351300c15.402434,-0.700300 23.453844,-6.300900 23.453844,-6.300900v-25.554700 ", ""),
new Proto("m26.043228,0.408300 c29.996845,2.647000 33.606048,14.002900 0,14.702900c-33.605696,0.700700 -35.705436,-17.853000 0,-14.702900 z", "")
		}, 49.963425, 41.105717));

		sketchShapes.put(ElementType.NOTE, new Group(new Proto[]{
new Proto("m0,0.033600 l49.976084,-0.191900 l0.441281,12.445600 l-50.172731,-0.038300  z", "stroke-linejoin:round;"),
new Proto("m3.248271,-6.202400 l38.009314,-2.407400 l-0.603969,2.407400 l3.762540,0.531500 l-2.130631,2.445800 l-1.313180,1.852100 l3.613041,0 l-2.874526,2.848300 l2.216143,2.706900 l-40.979521,2.250000 l0.300789,-2.406600 l-2.715465,-0.902500 l3.318235,-1.503800 l-3.851042,-3.491200 l3.364879,-1.282200 l-2.052295,-1.423800 l1.935688,-1.624500  z", "fill:bordercolor;stroke:none;", false)
		}, 49.924129, 12.190000));

	sketchShapes.put(ElementType.COMPONENT, new Group(new Proto[]{
new Proto("m1.557000,94.088004 l194.052000,-1.493000 l0,-70.156000 l-23.882000,0 l-1.495000,-22.391000 l-29.853000,0 l-2.986000,20.898000 l-70.157000,1.493000 l-5.970000,-17.913000 l-25.376000,0 l-2.986000,17.913000 l-32.840000,0  z", "stroke-linecap:round;stroke-linejoin:round;stroke-miterlimit:10")
		}, 195.658750, 94.130005));

	sketchShapes.put(ElementType.ACTIVITY, new Group(new Proto[]{
new Proto("m221.250000,71.757810 c0,11.795000 -9.560000,21.355000 -21.355000,21.355000l-178.473750,0 c-11.795000,0 -21.355000,-9.560000 -21.355000,-21.355000l0,-44.237500 c0,-11.795000 9.560000,-21.355000 21.355000,-21.355000l181.523750,-6.101250 c11.796250,0 21.356250,12.611250 21.356250,24.406250l-3.051250,47.287500  z", "stroke-linecap:round")
		}, 224.378130, 93.164062)
);

	sketchShapes.put(ElementType.CHOICE, new Group(new Proto[]{
new Proto("m40.225004,0.062250 l-40.183000,43.953000 l42.697000,38.928000 l36.417000,-41.441000  z", "stroke-linecap:round;stroke-linejoin:round;stroke-miterlimit:10")
		}, 79.192505, 83.002502));

	sketchShapes.put(ElementType.ACTIVITY_START2, new Group(new Proto[]{
new Proto("m7.288095,9.589460 c13.388000,-17.508000 55.611000,-10.303000 57.673000,16.473000c2.058000,26.775000 -20.597000,41.195000 -44.285000,29.865000c-23.684000,-11.322000 -25.256000,-30.815000 -13.388000,-46.338000 z", "stroke-linecap:round")
		}, 65.137627, 59.867428));

	}


	public static Group get(String elementType, boolean sketch) {
		return get(ElementType.getEnum(elementType), sketch);
	}

	public static Group get(ElementType type, boolean sketch) {
		Group result = null;
		if (sketch) {
			result = getSketch(type);
		}
		if (result == null) {
			result = shapes.get(type);
		}
		return result;
	}

	public static Group getSketch(ElementType type) {
		return sketchShapes.get(type);
	}
}