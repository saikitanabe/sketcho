package net.sevenscales.editor.uicomponents.uml;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JavaScriptObject;

import net.sevenscales.domain.ElementType;
import net.sevenscales.domain.js.JsShape;
import net.sevenscales.domain.js.JsPath;
import net.sevenscales.domain.utils.SLogger;


public class ShapeCache {
	private static final SLogger logger = SLogger.createLogger(ShapeCache.class);

	private static final Map<String,ShapeGroup> shapes;
	private static final Map<String,ShapeGroup> sketchShapes;

	// public static ShapeGroup get(String elementType, boolean sketch) {
	// 	return get(ElementType.getEnum.getValue()(elementType), sketch);
	// }

	public static JsArray<JsShape> getScaledContextShapes(double width, double height) {
		JsArray<JsShape> result = JavaScriptObject.createArray().cast();
		JsArray<JsShape> shapes = loadLibraryContext();

		for (int i = 0; i < shapes.length(); ++i) {
			JsShape shape = shapes.get(i);
			ShapeGroup sg = extractShapeGroup(shape);

			result.push(scaleShape(width, height, sg));
		}

		return result;
	}

	public static JsArray<JsShape> getScaledShapes(double width, double height) {
		JsArray<JsShape> result = JavaScriptObject.createArray().cast();
		// JavaScriptObject result = JavaScriptObject.createArray();
		for (String type : sketchShapes.keySet()) {
			ShapeGroup sg = sketchShapes.get(type);

			// double sw = width / sg.width;
			// double sh = height / sg.height;
			// double scale = sw < sh ? sw : sh;

			// JsShape shape = sg.scaleToShape(scale, scale);
			// result.push(shape);
			result.push(scaleShape(width, height, sg));
		}
		return result;
	}

	public static ShapeGroup get(String type, boolean sketch) {
		ShapeGroup result = null;
		if (sketch) {
			result = getSketch(type);
		}
		if (result == null) {
			result = shapes.get(type);
		}
		return result;
	}

	public static ShapeGroup getSketch(String type) {
		return sketchShapes.get(type);
	}

	public static void updateShapes(JsArray<JsShape> shapes) {
		for (int i = 0; i < shapes.length(); ++i) {
			JsShape shape = shapes.get(i);
			addShape(shape);
		}
	}

	public static boolean hasShape(JsShape shape) {
		if (shape.getShapeType() == 0) {
			return sketchShapes.get(shape.getElementType()) != null;
		} else if (shape.getShapeType() == 1) {
			return shapes.get(shape.getElementType()) != null;
		}
		return false;
	}

	public static void addShape(JsShape shape) {
		ShapeGroup sg = extractShapeGroup(shape);
		addShape(shape, sg);
	}

	private static native JsArray<JsShape> loadLibraryContext()/*-{
		return $wnd.loadLibraryContext()
	}-*/;

	private static native void loadLibrary()/*-{
		if ($wnd.loadLibraryCommon != 'undefined') {
			var shapes = $wnd.loadLibraryCommon()
			@net.sevenscales.editor.uicomponents.uml.ShapeCache::updateShapes(Lcom/google/gwt/core/client/JsArray;)(shapes)
		}
	}-*/;

	private static JsShape scaleShape(double width, double height, ShapeGroup sg) {
		double sw = width / sg.width;
		double sh = height / sg.height;
		double scale = sw < sh ? sw : sh;
		return sg.scaleToShape(scale, scale);
	}

	private static void addShape(JsShape shape, ShapeGroup shapeGroup) {
		if (shape.getShapeType() == 0) {
			sketchShapes.put(shape.getElementType(), shapeGroup);
		} else if (shape.getShapeType() == 1) {
			shapes.put(shape.getElementType(), shapeGroup);
		}
	}

	private static ShapeGroup extractShapeGroup(JsShape shape) {
		List<ShapeProto> protos = new ArrayList<ShapeProto>();
		for (int i = 0; i < shape.getShape().length(); ++i) {
			JsPath path = shape.getShape().get(i);
			// logger.debug("shape {}", shape.getElementType());
			protos.add(new ShapeProto(path.getPath(), path.getStyle(), path.getNoScaling()));
		}
		ShapeProto[] prots = new ShapeProto[protos.size()];
		protos.toArray(prots);
		ShapeGroup result = new ShapeGroup(shape.getElementType(), prots, shape.getWidth(), shape.getHeight(), shape.getProperties());
		return result;
	}

	static {
		SLogger.addFilter(ShapeCache.class);

		shapes = new HashMap<String,ShapeGroup>();
		sketchShapes = new HashMap<String,ShapeGroup>();

		loadLibrary();

		// shapes.put(ElementType.STAR4.getValue(), new ShapeGroup(new ShapeProto[]{
		// 	new ShapeProto("m 25,49.004685 -8.485281,-15.514719 -15.514719,-8.485280 15.514719,-8.485281 8.485280,-15.514719 8.485281,15.514719 15.514719,8.485280 -15.514719,8.485281 z", "")
		// }, 50, 50));

		// sketchShapes.put(ElementType.STAR4.getValue(), new ShapeGroup(new ShapeProto[]{
		// 	new ShapeProto("m19.956837,46.905000 l-3.039849,-17.229300 l-16.852719,-10.780100 l18.394563,-1.133100 l7.814065,-17.064600 l4.416968,18.182700 l17.170516,8.026000 l-17.653038,3.781300  z", Shapes.STROKE_LINE_ROUND)
		// }, 47.908752, 48.002502));

		// shapes.put(ElementType.STAR5.getValue(), new ShapeGroup(new ShapeProto[]{
		// 	new ShapeProto("m 49,19.434298 -12.098453,12.212955 2.693801,17.352755 -14.767181,-8.266410 -14.898450,8.104734 2.971834,-17.321878 -11.901550,-12.343754 16.603875,-2.439099 7.542888,-15.733593 7.289926,15.814431 z")
		// }, 50, 50));
		// sketchShapes.put(ElementType.STAR5.getValue(), new ShapeGroup(new ShapeProto[]{
		// 	new ShapeProto("m45.128302,22.543240 l-15.402247,7.868080 c1.837619,4.705080 2.554999,11.195720 2.094602,19.563640l-11.729998,-15.556810 c-5.084870,4.314460 -11.550689,6.247960 -17.291526,9.406450l11.083228,-16.614590 l-13.836872,-14.243270 c5.654021,1.117090 11.504460,2.466310 17.965632,5.606630c6.577878,-9.635680 6.353243,-12.468850 9.354048,-18.527460c1.660651,6.416950 3.150024,12.833890 1.866800,19.250840 z", "")
		// }, 47.067509, 50.036259));

		// shapes.put(ElementType.ENVELOPE.getValue(), new ShapeGroup(new ShapeProto[]{
		// 	new ShapeProto("m1,1 l98,0 l0,48 l-98,0  z", ""),
		// 	new ShapeProto("m1.268353,1.485500 l48.725893,23.358900 l48.620198,-23.676000 ", Shapes.STROKE_LINE_ROUND)
		// }, 100.000000, 50.000019));
		// sketchShapes.put(ElementType.ENVELOPE.getValue(), new ShapeGroup(new ShapeProto[]{
		// 	new ShapeProto("m0.260127,-0.162700 l99.374049,0.739900 l-1.082606,48.123800 l-97.701047,2.391700  z", ""),
		// 	new ShapeProto("m2.912598,0.588600 l40.803621,27.843200 l55.197179,-25.170700 ", Shapes.STROKE_LINE_ROUND)
		// }, 100.000000, 50.000019));

		// shapes.put(ElementType.TRIANGLE.getValue(), new ShapeGroup(new ShapeProto[]{
		// 	new ShapeProto("m 0.5,46.5 49,0 -24.5,-43 z", "")
		// }, 50, 50));
		// sketchShapes.put(ElementType.TRIANGLE.getValue(), new ShapeGroup(new ShapeProto[]{
		// 	new ShapeProto("m0.042404,42.035700 l49.591908,2.218100 l-26.290949,-44.200700  z", Shapes.STROKE_LINE_ROUND)
		// }, 49.692509, 44.317509)
		// );

		// shapes.put(ElementType.CLOUD.getValue(), new ShapeGroup(new ShapeProto[]{
		// 	new ShapeProto("m21.199099,0.049700 c-1.938210,0 -3.653040,2.701700 -4.706519,6.851200c-1.971343,-2.831700 -4.604841,-4.474900 -7.159760,-4.102600c-3.801548,0.554100 -6.140021,5.381000 -5.686840,11.212400c-2.483592,2.505300 -3.874953,5.626800 -3.555494,8.715500c0.261420,2.527500 1.626471,4.623200 3.696934,6.082400c-0.603504,2.668300 -0.613678,5.453800 0.102422,7.878600c1.580652,5.352300 6.135476,6.971800 10.310444,3.769200c0.082390,0.301200 0.167326,0.598500 0.258494,0.891300c2.190984,7.035000 6.094714,8.817400 8.715592,3.980100c0.852926,-1.574200 1.461687,-3.660000 1.824081,-5.994000c1.555595,5.376000 4.802332,6.378000 7.642606,2.041100c0.775751,-1.184600 1.448235,-2.653600 1.999661,-4.299900c0.655541,1.438000 1.368079,2.692800 2.116714,3.694400c3.739349,5.002800 7.006321,1.798300 7.301200,-7.157500c0.053110,-1.612900 0.007900,-3.298800 -0.126808,-5.014200c2.643119,-1.706200 4.230775,-3.999500 4.106619,-6.429400c-0.095080,-1.861000 -1.180064,-3.522100 -2.940963,-4.817000c0.850135,-2.211900 1.446247,-4.398000 1.707028,-6.395400c0.928355,-7.110700 -2.788589,-9.149100 -8.305909,-4.551600c-0.243277,0.202700 -0.484909,0.418400 -0.726707,0.639500c-2.005789,-6.599300 -7.220956,-8.166200 -11.739469,-3.469800c-0.294922,0.306500 -0.579413,0.629900 -0.853514,0.972900c-1.038768,-2.784300 -2.439133,-4.497200 -3.979812,-4.497200 z", "")
		// }, 48.083313, 48.104042));
// 		sketchShapes.put(ElementType.CLOUD.getValue(), new ShapeGroup(new ShapeProto[]{
// new ShapeProto("m197.917480,54.899610 c11.963600,-16.563930 45.471600,-29.010080 71.980470,-5.180160c26.508880,23.829930 0.860630,68.519980 0.860630,68.519980c0,0 32.767900,1.282050 35.504500,38.507200c2.736600,37.225160 -33.861540,40.766600 -48.735190,39.708200c-74.536040,5.207230 -143.641550,2.558930 -212.458040,6.935020c-29.328630,-1.599530 -48.479170,-33.687370 -44.509580,-61.074720c-1.315650,-20.148030 29.370160,-37.960650 29.370160,-37.960650c0,0 -17.075020,-36.599410 19.172270,-75.431130c32.190190,-33.189310 99.182900,-38.998890 127.740360,-10.924900c10.662190,10.003960 14.720090,24.218980 21.074420,36.901160 z", Shapes.STROKE_LINE_ROUND)
// 		}, 306.449280, 203.440120));

// shapes.put(ElementType.FIREWALL.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m 26.499960,45.938400 -5.038943,3.561600 -20.961017,-9.043500 0.000004,-36.395200 5.038943,-3.561300 20.961081,9.043700 z", "stroke-linejoin:round;"),
// 	new ShapeProto("m 2.706990,11.427400 18.340945,7.913300 m -14.458863,-12.565500 0,6.347300 m 8.163128,21.736400 0,6.347200 m -8.639272,-9.987600 0,6.347200 m 4.092287,-10.670200 0,6.347300 m 4.774335,-10.215200 0,6.347300 m -8.866621,-10.215200 0,6.347200 m 4.774335,-10.215100 0,6.347200 m -7.957214,14.457800 18.340946,7.913200 m -18.340946,-13.913200 18.340946,7.913200 m -18.340946,-13.913300 18.340946,7.913300 m -18.340946,-13.913300 18.340946,7.913200 m -0.000002,-13.053200 -0.000068,34.043300 m -18.225058,-41.840600 18.299842,7.902400 m 0,0.111500 4.997839,-3.572300 ", "stroke-linejoin:round;")
// }, 27, 50));
// sketchShapes.put(ElementType.FIREWALL.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m27.726794,43.707800 l-6.265777,5.792200 l-21.295608,-8.151300 l-0.557648,-36.729700 l5.931186,-4.119000 l21.630263,7.036200  z", ""),
// 	new ShapeProto("m2.706990,11.985100 l18.340945,7.355600 m-14.570393,-11.896300 l0.111530,5.678100 m8.163128,21.736400 l0,6.347200 m-8.639272,-9.987600 l0,6.347200 m4.092287,-10.112500 l0.111530,5.120400 m4.662805,-9.546000 l0,6.347300 m-8.866621,-10.215200 l0,6.347200 m4.774335,-10.215100 l0,5.678000 m-8.180275,16.019200 l18.564007,7.021000 m-18.564007,-14.917000 l18.564007,8.917000 m-18.564007,-13.355600 l18.564007,7.355600 m-18.340946,-14.694000 l18.340946,8.693900 m-0.000002,-13.053200 l0.446053,33.820200 m-19.786483,-41.059800 l19.415146,7.344700 m0,0.111500 l5.220900,-5.133700 ", Shapes.STROKE_LINE_ROUND)
// }, 27.000029, 50.000000));

// shapes.put(ElementType.BUBBLE.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m 2.360450,36.388330 0,-23.086280 c 0,-7.092080 5.637940,-12.801600 12.641130,-12.801600 l 0,0.000030 71.857280,0 c 7.003190,0 12.641140,5.709520 12.641140,12.801600 l 0,23.086280 c 0,7.092090 -5.638000,12.775710 -12.641140,12.801610 l -71.944890,0.266160 c -3.127830,-0.061000 -5.924880,-1.373520 -7.997070,-3.018980 -1.164830,2.074880 -4.540840,1.898310 -6.416900,3.063300 4.322270,-5.075860 2.132430,-7.748470 1.860450,-13.112120 z", "")
// }, 100, 50));
// sketchShapes.put(ElementType.BUBBLE.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m79.276280,1.554480 c-20.934620,-0.785620 -56.474900,-1.663580 -69.560720,-1.461620c-7.359800,0.113590 -8.290880,1.470070 -8.951870,6.790740c0.699430,16.289190 -1.633190,33.227000 -0.277810,50.310810c0.223950,3.932690 -0.220540,6.845590 4.140540,6.994380c10.587440,-0.106210 5.868980,-0.896660 4.891830,6.087290c7.020590,-4.932240 8.210040,-6.026160 11.910100,-5.586680c3.483520,0.067950 48.953010,0.475440 60.944920,-0.574210c1.523360,-0.133340 2.951720,-1.868610 3.089110,-3.391610c1.599920,-17.735550 1.125270,-43.989420 -0.329900,-54.761840c-0.785860,-2.933650 -1.670410,-4.655910 -5.856200,-4.407260 z", "")
// }, 86.500000, 70.339996));

// shapes.put(ElementType.BUBBLE_R.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m 97.639550,36.388330 0,-23.086280 c 0,-7.092080 -5.637940,-12.801600 -12.641130,-12.801600 l 0,0.000030 -71.857280,0 c -7.003190,0 -12.641140,5.709520 -12.641140,12.801600 l 0,23.086280 c 0,7.092090 5.638000,12.775710 12.641140,12.801610 l 71.944890,0.266160 c 3.127830,-0.061000 5.924880,-1.373520 7.997070,-3.018980 1.164830,2.074880 4.540840,1.898310 6.416900,3.063300 -4.322270,-5.075860 -2.132430,-7.748470 -1.860450,-13.112120 z", "")
// }, 100, 50));
// sketchShapes.put(ElementType.BUBBLE_R.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m7.240490,1.554480 c20.934620,-0.785620 56.474900,-1.663580 69.560720,-1.461620c7.359800,0.113590 8.290880,1.470070 8.951870,6.790740c-0.699430,16.289190 1.633190,31.227000 0.277810,48.310810c-0.223950,3.932690 0.220540,6.845590 -4.140540,6.994380c-10.587440,-0.106210 -5.868980,1.103340 -4.891830,8.087290c-7.020590,-4.932240 -8.210040,-6.783770 -11.910100,-6.344290c-3.483520,0.067950 -48.953010,1.738130 -60.944920,0.688480c-1.523360,-0.133340 -2.951720,-1.868610 -3.089110,-3.391610c-1.599920,-17.735550 -1.125270,-44.494500 0.329900,-55.266920c0.785860,-2.933650 1.670410,-4.655910 5.856200,-4.407260 z", "")
// }, 86.500000, 70.339996));
// 		sketchShapes.put(ElementType.BUBBLE_R.getValue(), new ShapeGroup(new ShapeProto[]{
// new ShapeProto("m97.639550,36.388330 l2.272840,-23.970160 c0.669470,-7.060420 -7.910780,-12.675330 -14.913970,-12.675330c-17.404200,-0.675550 -52.949060,0.736120 -72.362360,1.515250c-6.997550,0.280840 -13.388830,4.700320 -13.272480,11.791450l0.378810,23.086280 c0.116350,7.091130 6.143920,13.068490 13.146210,13.180420l71.944890,1.150040 c3.127830,-0.061000 6.177420,-2.383670 8.249610,-4.029130c1.164830,2.074880 4.540840,1.898310 6.416900,3.063300c-4.322270,-5.075860 -2.132430,-7.748470 -1.860450,-13.112120 z", Shapes.STROKE_LINE_ROUND)
// 		}, 100.000000, 50.000454));

// shapes.put(ElementType.CIRCLE.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m49.500201,25.000118 a24.500050,24.500057 0 1 1 -49.000103,0a24.500050,24.500057 0 1 1 49.000103,0 z", "")
// }, 50, 50));

// shapes.put(ElementType.SMILEY.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m 49.500166,25.000057 a 24.500057,24.500035 0 1 1 -49.000113,0 24.500057,24.500035 0 1 1 49.000113,0 z", ""),
// 	new ShapeProto("m 8.623853,30.917500 c 15.963302,20 32.155963,0.321100 32.155963,0.321100", "fill:none;"),
// 	new ShapeProto("m 19.633027,19.678887 a 1.743119,2.522936 0 1 1 -3.486239,0 1.743119,2.522936 0 1 1 3.486239,0 z", ""),
// 	new ShapeProto("m 33.486238,19.678887 a 1.743119,2.522936 0 1 1 -3.486239,0 1.743119,2.522936 0 1 1 3.486239,0 z", ""),
// 	new ShapeProto("m 6.055046,31.009100 c 3.394495,0.917500 4.036697,-2.201700 4.036697,-2.201700 ", ""),
// 	new ShapeProto("m 43.577982,31.376100 c -3.394495,0.917500 -4.036697,-2.201700 -4.036697,-2.201700 ", "")
// }, 50, 50));
// sketchShapes.put(ElementType.SMILEY.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m7.282185,9.582980 c13.388000,-17.507950 55.611000,-10.302960 57.673000,16.473000c2.058000,26.775000 -20.597000,41.195000 -44.285000,29.865000c-23.684000,-11.322000 -25.256000,-30.815000 -13.388000,-46.338000 z", ""),
// 	new ShapeProto("m17.151658,34.881380 c15.963302,20 31.422018,-1.697200 31.422018,-1.697200", ""),
// 	new ShapeProto("m14.399365,34.973080 c3.394495,0.917500 4.036697,-2.201700 4.036697,-2.201700", ""),
// 	new ShapeProto("m51.371842,33.321680 c-3.394495,0.917500 -4.036697,-2.201700 -4.036697,-2.201700", ""),
// 	new ShapeProto("m41.002350,20.525620 c-0.074960,1.391362 -0.823274,2.752528 -1.784578,2.700738c-0.961305,-0.051790 -1.588086,-1.038210 -1.513126,-2.429572c0.074960,-1.391361 0.823275,-2.660784 1.784580,-2.608994c0.961304,0.051790 1.588085,0.946467 1.513125,2.337828 z", "fill:bordercolor;"),
// 	new ShapeProto("m26.797507,20.052568 c0.229836,1.374293 -0.078347,2.984078 -1.027858,3.142873c-0.949512,0.158795 -1.813818,-1.010046 -2.043654,-2.384339c-0.229835,-1.374292 0.261834,-2.433618 1.211346,-2.592414c0.949511,-0.158795 1.630331,0.459588 1.860166,1.833880 z", "fill:bordercolor;")
// }, 65.137627, 59.867428)
// );

// shapes.put(ElementType.POLYGON4.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m 40.132126,49.643455 -30.283670,-0.010736 -9.348456,-30.283675 24.506007,-18.705604 24.494001,18.722977 z", "")
// }, 50, 50));
// sketchShapes.put(ElementType.POLYGON4.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m39.921095,50.184520 l-31.261931,-0.554214 l-8.587586,-27.348892 l27.440790,-22.183866 l21.667913,19.809934  z", Shapes.STROKE_LINE_ROUND)
// }, 49.259506, 50.235168)
// );

// shapes.put(ElementType.POLYGON8.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m 35.146457,49.499952 -20.295937,-0.001287 -14.350511,-14.352278 0.001287,-20.295936 14.352278,-14.350511 20.295936,0.001253 14.350511,14.352278 -0.001287,20.295937 z", "")
// }, 50, 50));
// shapes.put(ElementType.POLYGON8.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m37.459106,50.215994 l-26.664498,0.462000 l-10.756685,-24.254450 l12.552835,-25.041128 l24.824671,-1.327802 l12.596513,23.496875  z", Shapes.STROKE_LINE_ROUND)
// }, 50.051842, 50.724659));

// shapes.put(ElementType.ARROW_UP.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m 12.531496,0.5 -12.031496,17.621200 5.417323,0 0,31.378800 13.165354,0 0,-31.347400 5.417322,0 -11.968503,-17.652600 z", "")
// }, 25, 50));
// sketchShapes.put(ElementType.ARROW_UP.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m12.040336,0.081000 l-11.933263,18.112400 l7.971350,-1.669900 l-1.178782,32.262800 l11.986573,0.884100 l-2.357565,-33.705000 l8.757205,0.687600  z", Shapes.STROKE_LINE_ROUND)
// }, 25.375000, 49.718750));

// shapes.put(ElementType.ARROW_DOWN.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m 12.531496,49.5 -12.031496,-17.621200 5.417323,0 0,-31.378800 13.165354,0 0,31.347400 5.417322,0 -11.968503,17.652600 z", "")
// }, 25, 50));
// sketchShapes.put(ElementType.ARROW_DOWN.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m11.310645,49.936200 l-11.245641,-18.505300 l8.167814,1.866400 l-2.259332,-33.245200 l12.674195,0.687600 l-2.357564,33.312000 l7.578422,-1.080500  z", Shapes.STROKE_LINE_ROUND)
// }, 23.911257, 49.973759));

// shapes.put(ElementType.ARROW_RIGHT.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m 49.5,12.531500 -17.621200,-12.031500 0,5.417300 -31.378800,0 0,13.165400 31.347400,0 0,5.417300 17.652600,-11.968500 z", "")
// }, 50, 25));
// sketchShapes.put(ElementType.ARROW_RIGHT.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m49.500000,11.352700 l-17.621200,-10.852700 l1.473477,6.989000 l-33.539900,-1.964600 l0.491159,12.084800 l33.508501,-1.669900 l-3.438114,8.560700  z", Shapes.STROKE_LINE_ROUND)
// }, 50.000000, 25.000000));

// shapes.put(ElementType.ARROW_LEFT.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m 0.5,12.531500 17.621200,-12.031500 0,5.417300 31.378800,0 0,13.165400 -31.347400,0 0,5.417300 -17.652600,-11.968500 z", "")
// }, 50, 25));
// sketchShapes.put(ElementType.ARROW_LEFT.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m0.107073,13.219100 l18.799982,-13.308500 l-3.536346,8.757200 l34.915146,-2.848700 l-0.785855,13.263600 l-34.392587,-2.848700 l2.554028,8.266000  z", Shapes.STROKE_LINE_ROUND)
// }, 50.000000, 25.000000));

// shapes.put(ElementType.IPHONE.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m 4.193653,0.5 15.612694,0 c 2.046284,0 3.693653,1.647400 3.693653,3.693600 l 0,41.612700 c 0,2.046300 -1.647369,3.693700 -3.693653,3.693700 l -15.612694,0 c -2.046284,0 -3.693653,-1.647400 -3.693653,-3.693700 l 0,-41.612700 c 0,-2.046200 1.647369,-3.693600 3.693653,-3.693600 z", "fill:none;"),
// 	new ShapeProto("m 12.5,2.733845 a 0.5,0.5 0 1 1 -1,0 0.5,0.5 0 1 1 1,0 z", ""),
// 	new ShapeProto("m 14,45.688237 a 2,2.000001 0 1 1 -4,0 2,2.000001 0 1 1 4,0 z", ""),
// 	new ShapeProto("m 8.351575,5.058100 7.296850,0", ""),
// 	new ShapeProto("m 1.635158,7.545500 20.729684,0 0,34.079600 -20.729684,0 z", "fill:none;")
// }, 24, 50));
// sketchShapes.put(ElementType.IPHONE.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m3.959123,-0.086300 l16.890821,0.732000 c2.044366,0.088600 2.755489,1.853500 2.767321,3.899700l0.234529,40.557300 c0.011833,2.046300 -2.000998,5.187500 -4.045447,5.100900l-16.607719,-0.703600 c-2.044449,-0.086600 -2.315292,-2.643100 -2.366953,-4.688700l-1.015144,-40.197200 c-0.051658,-2.045600 2.098228,-4.789000 4.142593,-4.700400 z", ""),
// 	new ShapeProto("m12.527691,2.999315 c-0.225329,0.272777 -0.949534,0.079598 -1.027691,-0.265471c-0.062017,-0.273806 0.370824,-0.601327 0.645107,-0.541460c0.290830,0.063479 0.572165,0.577429 0.382583,0.806930 z", ""),
// 	new ShapeProto("m8.351575,5.058100 l7.296850,-0.351800 ", ""),
// 	new ShapeProto("m14.228031,46.632800 c-0.615129,1.000900 -3.023751,1.056300 -3.524442,-0.006500c-0.486557,-1.032700 1.041014,-2.769000 2.175897,-2.644900c0.985685,0.107800 1.867724,1.806700 1.348545,2.651400 z", Shapes.FILL_BORDER + Shapes.STROKE_NONE),
// 	new ShapeProto("m2.221482,7.193700 l19.128216,0.028400 l-0.311556,34.403000 l-18.699396,-0.117300  z", Shapes.FILL_BACKGROUND)
// }, 24.000000, 50.000000));

// shapes.put(ElementType.WEB_BROWSER.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m 0.25,0.25 49.5,0 0,49.5 -49.5,0 z", "fill:none;"),
// 	new ShapeProto("m 0.25,0.25 49.5,0 0,5.945400 -49.5,0 z", "fill:none;"),
// 	new ShapeProto("m 10.490104,2.3082 37.339534,0 0,1.791900 -37.339534,0 z", "fill:none;"),
// 	new ShapeProto("m 2.549995,2.1704 -1.102701,1.0337 1.102701,0.896 ", "fill:none;"),
// 	new ShapeProto("m 1.585132,3.204142 1.654051,0 ", ""),
// 	new ShapeProto("m 4.481076,2.1704 1.102701,1.0337 -1.102701,0.896 ", "fill:none;"),
// 	new ShapeProto("m 5.445939,3.204100 -1.654051,0 ", ""),
// 	new ShapeProto("m 8.780560,3.787900 c -0.183226,0.183200 -0.436352,0.2966 -0.715946,0.2966 -0.559188,0 -1.0125,-0.453300 -1.0125,-1.0125 0,-0.559300 0.453312,-1.0125 1.0125,-1.0125 0.279594,0 0.588543,0.169100 0.771770,0.463900 ", "fill:none;"),
// 	new ShapeProto("m 8.886836,2.802148 -0.487970,-0.254333 0.464244,-0.295428 z", "")
// }, 50, 50));
// sketchShapes.put(ElementType.WEB_BROWSER.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m0.718750,0.549700 l49.792398,-0.487300 l0.097466,50.377200 l-50.572125,-0.195000  z", Shapes.STROKE_LINE_ROUND + "fill:none;"),
// 	new ShapeProto("m0.718750,0.549700 l49.694932,-0.584800 l0.194932,6.725100 l-50.084796,0  z", Shapes.STROKE_LINE_ROUND + "fill:none;"),
// 	new ShapeProto("m10.958854,2.607900 l37.144602,-0.097500 l-0.097466,2.279300 l-37.242068,-0.292400  z", Shapes.STROKE_LINE_ROUND + "fill:none;"),
// 	new ShapeProto("m2.823813,2.470100 l-0.907769,1.033700 l1.102701,0.896000 ", Shapes.STROKE_LINE_ROUND + "fill:none;"),
// 	new ShapeProto("m2.053882,3.503842 l1.702784,-0.243665 ", Shapes.STROKE_LINE_ROUND + "fill:none;"),
// 	new ShapeProto("m4.949826,2.470100 l1.102701,1.033700 l-1.102701,0.896000 ", Shapes.STROKE_LINE_ROUND + "fill:none;"),
// 	new ShapeProto("m5.914689,3.503800 l-1.702784,-0.097500 ", Shapes.STROKE_LINE_ROUND + "fill:none;"),
// 	new ShapeProto("m9.249310,4.087600 c-0.183226,0.183200 -0.436352,0.296600 -0.715946,0.296600c-0.559188,0 -1.012500,-0.453300 -1.012500,-1.012500c0,-0.559300 0.453312,-1.012500 1.012500,-1.012500c0.279594,0 0.588543,0.169100 0.771770,0.463900", Shapes.STROKE_LINE_ROUND + "fill:none;"),
// 	new ShapeProto("m9.355586,3.101848 l-0.487970,-0.254333 l0.464244,-0.295428  z", Shapes.STROKE_LINE_ROUND + "fill:none;")
// }, 50.656250, 50.500000));

// shapes.put(ElementType.RECT.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m 0.25,0.25 49.5,0 0,34.5 -49.5,0 z", "")
// }, 50, 35));
// sketchShapes.put(ElementType.RECT.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m0.804251,0.047200 l51.050095,0.944100 l-1.146435,34.055800 l-50.645471,-0.134800  z", Shapes.STROKE_LINE_ROUND)
// }, 51.908752, 35.098759));

// shapes.put(ElementType.SWITCH.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m 0.766624,10.664652 24.999980,12.857130 23.405109,-11.463840 -24.833680,-12.464710 z ", "fill:none"),
// 	new ShapeProto("m 0.662019,10.538384 -0.000029,10.310087 24.747444,13.322597 0,-10.649286 ", "fill:none"),
// 	new ShapeProto("m 49.102319,12.158574 -0.121452,10.291782 -23.571407,11.772083 0.024870,-10.942900 ", "fill:none"),
// 	new ShapeProto("m 20.858430,2.604705 6.446425,0.339468 0.339282,4.410702 -2.374997,-1.696546 -4.749995,3.053624 -3.392854,-2.035749 5.767854,-2.714156 z ", ""),
// 	new ShapeProto("m 28.961761,20.531902 -6.446425,-0.339468 -0.339285,-4.410436 2.374997,1.696016 4.749998,-3.053359 3.392853,2.035749 -5.767853,2.714156 z ", ""),
// 	new ShapeProto("m 20.293499,15.982183 -6.446424,-0.339468 -0.339283,-4.410437 2.374998,1.696016 4.749995,-3.053358 3.392853,2.035750 -5.767854,2.714155 z ", ""),
// 	new ShapeProto("m 29.013907,7.326542 6.446424,0.339203 0.339283,4.410702 -2.374998,-1.696281 -4.749995,3.053359 -3.392853,-2.035485 5.767851,-2.714420 z ", "")
// }, 50, 35));
// sketchShapes.put(ElementType.SWITCH.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m1.289217,9.410427 l24.291523,14.200704 l24.113567,-13.434526 l-27.551168,-10.896929  z", Shapes.STROKE_LINE_ROUND),
// 	new ShapeProto("m1.312305,9.373508 l-1.680332,7.680275 l25.045829,17.744397 l0.657454,-11.605124 ", Shapes.STROKE_LINE_ROUND),
// 	new ShapeProto("m49.415875,10.277237 l-1.062120,13.113788 l-23.555846,11.284285 l0.508651,-11.266973 ", Shapes.STROKE_LINE_ROUND),
// 	new ShapeProto("m19.813243,2.186630 l6.446425,0.339468 l0.339282,4.410702 l-2.374997,-1.696546 l-4.749995,3.053624 l-3.392854,-2.035749 l5.767854,-2.714156  z", Shapes.STROKE_LINE_ROUND),
// 	new ShapeProto("m27.916574,20.113827 l-6.446425,-0.339468 l-0.339285,-4.410436 l2.374997,1.696016 l4.749998,-3.053359 l3.392853,2.035749 l-5.767853,2.714156  z", Shapes.STROKE_LINE_ROUND),
// 	new ShapeProto("m19.248312,15.564108 l-6.446424,-0.339468 l-0.339283,-4.410437 l2.374998,1.696016 l4.749995,-3.053358 l3.392853,2.035749 l-5.767854,2.714155  z", Shapes.STROKE_LINE_ROUND),
// 	new ShapeProto("m27.968720,6.908467 l6.446424,0.339203 l0.339283,4.410702 l-2.374998,-1.696281 l-4.749995,3.053359 l-3.392853,-2.035485 l5.767851,-2.714420  z", Shapes.STROKE_LINE_ROUND)
// }, 50.000000, 35.000000));

// shapes.put(ElementType.ROUTER.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m 25.024070,0.407736 c -13.586462,0 -24.631789,5.049913 -24.631789,11.325102 l 0,11.504865 c 0,6.275190 11.045327,11.370043 24.631789,11.370043 13.586461,0 24.573558,-5.094853 24.573558,-11.370043 l 0,-11.504865 c 0,-6.275189 -10.987097,-11.325102 -24.573558,-11.325102 z ", "fill:none;"),
// 	new ShapeProto("m 49.607688,11.754521 a 24.600462,11.362238 0 1 1 -49.200924,0 24.600462,11.362238 0 1 1 49.200924,0 z ", "fill:none;"),
// 	new ShapeProto("m 27.063013,11.615851 0.591448,5.310802 1.979647,-1.309176 6.574362,3.806381 4.804257,-2.402835 -6.702474,-3.582272 1.784037,-1.309041 z ", ""),
// 	new ShapeProto("m 23.035014,10.073585 -0.591448,-4.405937 -2.175056,1.309176 -6.769773,-4.258813 -4.608848,2.704457 6.897885,3.582273 -1.588627,1.158228 z ", ""),
// 	new ShapeProto("m 39.988052,3.627231 -9.125742,0.045272 2.501081,1.535408 -6.958802,3.295466 4.254985,2.051231 6.887052,-3.659964 2.891575,1.384402 z ", ""),
// 	new ShapeProto("m 9.808246,18.813654 8.532502,-0.067910 -2.231254,-1.518447 7.463915,-3.445628 -4.248495,-2.184661 -7.357951,3.521837 -2.231022,-1.518289 z ", "")
// }, 50, 35));
// sketchShapes.put(ElementType.ROUTER.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m26.492215,-0.011734 c-13.586462,0 -26.519404,4.630442 -26.519404,10.905632l0.419470,12.343805 c0,6.275190 13.047446,11.221738 26.633908,11.221738c13.586461,0 22.571439,-4.946548 22.571439,-11.221738l0,-11.504865 c0,-6.275189 -9.518952,-11.744572 -23.105413,-11.744572 z", Shapes.STROKE_LINE_ROUND),
// 	new ShapeProto("m49.712557,12.698329 c0,6.275190 -16.650617,10.514799 -30.263309,10.523296c-13.665552,0.008530 -19.357086,-6.555192 -19.357086,-12.830382c0,-6.275192 14.789230,-10.208696 28.375691,-10.208696c13.586461,0 21.244704,6.240590 21.244704,12.515781 z", Shapes.STROKE_LINE_ROUND),
// 	new ShapeProto("m27.063013,11.615851 l0.591448,5.310802 l1.979647,-1.309176 l6.574362,3.806381 l4.804257,-2.402835 l-6.702474,-3.582272 l1.784037,-1.309041  z", Shapes.STROKE_LINE_ROUND),
// 	new ShapeProto("m23.035014,10.073585 l-0.591448,-4.405937 l-2.175056,1.309176 l-6.769773,-4.258813 l-4.608848,2.704457 l6.897885,3.582273 l-1.588627,1.158228  z", Shapes.STROKE_LINE_ROUND),
// 	new ShapeProto("m39.988052,3.627231 l-9.125742,0.045272 l2.501081,1.535408 l-6.958802,3.295466 l4.254985,2.051231 l6.887052,-3.659964 l2.891575,1.384402  z", Shapes.STROKE_LINE_ROUND),
// 	new ShapeProto("m9.808246,18.813654 l8.532502,-0.067910 l-2.231254,-1.518447 l7.463915,-3.445628 l-4.248495,-2.184661 l-7.357951,3.521837 l-2.231022,-1.518289  z", Shapes.STROKE_LINE_ROUND)
// }, 50.000000, 35.000027));

// shapes.put(ElementType.DESKTOP.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m 0.996463,0.996300 48.007076,0 0,31.108200 -48.007076,0 z ", "fill:none;stroke-width:5px;stroke-linecap:round;stroke-linejoin:round;stroke-miterlimit:4;"),
// 	new ShapeProto("m 30.027283,33.581400 c 0.143291,3.075900 0.144906,5.327200 2.847456,6.418500 l -15.688919,0 c 3.237678,-1.149500 2.963535,-3.455800 3.014953,-6.418500 z ", "fill-opacity:1;stroke: none;fill:bordercolor;"),
// 	new ShapeProto("m 1.650634,29.419800 46.224986,0 c 0.624870,0 1.127924,0.503000 1.127924,1.127900 l 0,0.902300 c 0,0.624900 -0.503054,1.128000 -1.127924,1.128000 l -46.224986,0 c -0.624870,0 -1.127924,-0.503100 -1.127924,-1.128000 l 0,-0.902300 c 0,-0.624900 0.503054,-1.127900 1.127924,-1.127900 z ", "fill-opacity:1;stroke: none;fill:bordercolor;")
// }, 50, 40));

// sketchShapes.put(ElementType.DESKTOP.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m0.997634,2.176900 l48.084362,-1.576800 l0.951653,30.484300 l-49.512622,0.782400  z", ""),
// 	new ShapeProto("m29.886463,33.451300 c0.273808,3.067000 0.450283,5.237000 3.196747,6.212500l-15.595521,0.270200 c1.918103,-2.870800 2.893317,-3.974800 2.581132,-6.144600 z", "")
// }, 50.000000, 40.000000));

// shapes.put(ElementType.LAPTOP.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m 7.020842,0.5711 35.958315,0 c 0.425769,0 0.768537,0.3428 0.768537,0.7686 l 0,22.4419 c 0,0.4257 -0.342768,0.7685 -0.768537,0.7685 l -35.958315,0 c -0.425769,0 -0.768537,-0.342800 -0.768537,-0.7685 l 0,-22.441900 c 0,-0.4258 0.342767,-0.7686 0.768537,-0.7686 z ", "fill:none;"),
// 	new ShapeProto("m 0.829383,26.3273 c -0.470764,0 -0.829384,0.3617 -0.829384,0.836300 0,0.474900 0.358620,0.8364 0.829384,0.8364 l 48.341233,0 c 0.470764,0 0.829384,-0.3615 0.829384,-0.8364 0,-0.474600 -0.358620,-0.8363 -0.829384,-0.8363 l -18.838863,0 c -0.03312,0.4305 -0.393679,0.7767 -0.829384,0.7767 l -8.116113,0 c -0.435705,0 -0.796272,-0.346200 -0.829384,-0.7767 l -19.727489,0 z ", ""),
// 	new ShapeProto("m -0,26.327500 0,0.597400 20.912322,0 c -0.186085,-0.1396 -0.336406,-0.3498 -0.35545,-0.5974 l -20.556872,0 z m 30.331753,0 c -0.018200,0.235900 -0.124322,0.4577 -0.296208,0.5974 l 19.964455,0 0,-0.5974 -19.668247,0 z ", "")
// }, 50, 28));
// sketchShapes.put(ElementType.LAPTOP.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m6.783124,-0.062800 l37.305384,-0.317000 c0.425753,-0.003600 0.791269,0.343500 0.768537,0.768600l-1.267830,23.709800 c-0.022727,0.425000 -0.342772,0.770300 -0.768537,0.768500l-36.671468,-0.158500 c-0.425765,-0.001900 -0.780147,-0.343000 -0.768537,-0.768500l0.633914,-23.234300 c0.011613,-0.425700 0.342783,-0.765000 0.768537,-0.768600 z", Shapes.STROKE_LINE_ROUND),
// 	new ShapeProto("m0.016939,26.281200 c-0.022502,0.311900 -0.016939,0.278300 -0.016939,0.640800c0,0.474900 0.358623,0.841000 0.829384,0.844000l48.453294,0.299800 c0.470761,0.003000 0.881950,-0.187400 0.829384,-0.659800c-0.039620,-0.356100 0.048511,-0.472500 -0.060199,-0.865700l-19.608048,-0.161300 c-0.033120,0.430200 -0.393679,0.773300 -0.829384,0.769000l-8.228174,-0.079500 c-0.435705,-0.004200 -0.796272,-0.354100 -0.829384,-0.784900 z", "")
// }, 50.000000, 28.000000));
// new ShapeGroup(new ShapeProto[]{
// new ShapeProto("m6.783124,-0.062800 l37.384623,-0.158500 c0.425764,-0.001800 0.792848,0.343500 0.768537,0.768600l-1.347069,23.551300 c-0.024305,0.424900 -0.342772,0.770300 -0.768537,0.768500l-36.671468,-0.158500 c-0.425765,-0.001900 -0.780147,-0.343000 -0.768537,-0.768500l0.633914,-23.234300 c0.011613,-0.425700 0.342771,-0.766800 0.768537,-0.768600 z", Shapes.STROKE_LINE_ROUND),
// new ShapeProto("m0.056031,27.551800 l49.923305,-0.728400 ", Shapes.STROKE_LINE_ROUND + STROKE_LINECAP_ROUND)
// 		}, 50.000000, 28.000000)
// );

// shapes.put(ElementType.SERVER2.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m 3.644492,0 17.71102,0 c 2.019047,0 3.64449,1.6254 3.64449,3.6445 l 0,42.711 c 0,2.019 -1.625443,3.6445 -3.644490,3.644500 l -17.711020,0 c -2.019047,0 -3.644490,-1.6255 -3.644490,-3.6445 l 0,-42.711 c 0,-2.0191 1.625443,-3.644500 3.644490,-3.644500 z ", "fill:bordercolor;"),
// 	new ShapeProto("m 1.976496,2.489200 21.047013,0 0,5.8982 -21.047013,0 z ", "fill:bgcolor;stroke-color:#fefefe;fill-opacity:1;"),
// 	new ShapeProto("m 1.976496,9.409100 21.047013,0 0,5.8983 -21.047013,0 z ", "fill:bgcolor;stroke-color:#fefefe;fill-opacity:1;"),
// 	new ShapeProto("m 1.976496,16.329000 21.047013,0 0,5.8983 -21.047013,0 z ", "fill:bgcolor;stroke-color:#fefefe;fill-opacity:1;"),
// 	new ShapeProto("m 14.901691,30.034619 a 2.401708,2.4329 0 1 1 -4.803417,0 2.401708,2.432900 0 1 1 4.803417,0 z ", "fill:bgcolor;stroke-color:#fefefe;fill-opacity:1;")
// }, 25, 50));
// sketchShapes.put(ElementType.SERVER2.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m3.733459,-0.089000 l18.778637,-0.978600 c2.016310,-0.105100 3.668570,1.625500 3.644490,3.644500l-0.533808,44.757200 c-0.024080,2.019000 -1.630524,3.787600 -3.644490,3.644500l-18.778636,-1.334500 c-2.013968,-0.143100 -3.669880,-1.625700 -3.644490,-3.644500l0.533807,-42.444100 c0.025391,-2.018900 1.628180,-3.539400 3.644490,-3.644500 z", ""),
// 	new ShapeProto("m2.332368,3.023000 l20.691141,-0.978600 l0,6.698900 l-21.047013,-0.355900  z", ""),
// 	new ShapeProto("m1.976496,10.298800 l21.047013,-0.355900 l0.444840,5.631400 l-21.847725,-0.089000  z", ""),
// 	new ShapeProto("m1.442689,17.129700 l22.025661,0.177900 l-0.355872,5.008700 l-21.135981,0.444800  z", ""),
// 	new ShapeProto("m14.723755,29.233908 c0,1.343653 -0.363539,2.877739 -1.689966,2.877739c-1.326427,0 -2.935515,-0.110599 -2.935515,-1.454252c0,-1.343654 0.185602,-2.966707 1.512029,-2.966707c1.326427,0 3.113452,0.199566 3.113452,1.543221 z", "")
// }, 25.000000, 50.000000));

// shapes.put(ElementType.TABLET_UP.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m 4.39569,0.6193 31.2086,0 c 2.0921,0 3.77635,1.6843 3.77635,3.7764 l 0,41.2087 c 0,2.092 -1.68425,3.7763 -3.77635,3.7763 l -31.2086,0 c -2.0921,0 -3.77635,-1.6843 -3.77635,-3.7763 l 0,-41.2087 c 0,-2.0921 1.68425,-3.7764 3.77635,-3.7764 z ", "fill:bordercolor;"),
// 	new ShapeProto("m 21,46.9324 c 0,0.5523 -0.447716,1 -1,1 -0.552285,0 -1,-0.4477 -1,-1 0,-0.5523 0.447715,-1 1,-1 0.552284,0 1,0.4477 1,1 z ", "fill:bgcolor;stroke:none;"),
// 	new ShapeProto("m 3.00791,5.4139 33.9841,0 0,38.7484 -33.9841,0 z ", "fill:bgcolor;stroke:none;")
// }, 40, 50));
// sketchShapes.put(ElementType.TABLET_UP.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m61.086533,0.366320 c4.694020,9.842320 2.755460,62.672120 -0.860160,78.989820c-16.169526,1.719400 -33.979276,2.215100 -54.184069,-1.372600c-8.465303,-27.744200 -6.863718,-67.454900 -1.918439,-77.737940c19.368484,0.436090 38.948894,-0.703370 56.962668,0.120720 z", ""),
// 	new ShapeProto("m10.006217,7.598550 c15.450427,0.943890 28.779672,-1.011080 42.778205,-0.049320c7.486691,9.318610 2.726501,46.822210 1.729831,59.511210c-5.626734,1.722900 -36.577844,1.466900 -43.622999,0.341300c-3.144375,-18.481900 -5.683128,-45.147000 -0.885037,-59.803190 z", Shapes.FILL_BACKGROUND),
// 	new ShapeProto("m34.417982,71.863240 c-4.439800,-0.925200 -1.935233,7.018000 1.681921,3.753900c1.931395,-1.182600 -0.059128,-3.631300 -1.681921,-3.753900 z", Shapes.FILL_BORDER+Shapes.STROKE_NONE)
// }, 63.903721, 80.726219));

// shapes.put(ElementType.TABLET_HORIZONTAL.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m 49.4205,31.1238 0,-27.2479 c 0,-1.8265 -1.68706,-3.2969 -3.78259,-3.2969 l -41.2762,0 c -2.09543,0 -3.78249,1.4704 -3.78249,3.2969 l 0,27.2479 c 0,1.8265 1.68706,3.297 3.78249,3.297 l 41.2762,0 c 2.09553,0 3.78259,-1.4705 3.78259,-3.297 z ", "fill:bordercolor;"),
// 	new ShapeProto("m 3.03149,16.6267 c -0.553205,0 -1.00164,0.3909 -1.00164,0.8732 0,0.4822 0.448434,0.8731 1.00164,0.8731 0.553205,0 1.00164,-0.3909 1.00164,-0.8731 0,-0.4823 -0.448434,-0.8732 -1.00164,-0.8732 z ", "fill:bgcolor;stroke:none;"),
// 	new ShapeProto("m 44.618,32.3354 0,-29.671 -38.8119,0 0,29.671 z ", "fill:bgcolor;stroke:none;")
// }, 50, 35));
// sketchShapes.put(ElementType.TABLET_HORIZONTAL.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m80.365625,61.088400 c-9.842400,4.694000 -62.672205,2.755400 -78.989855,-0.860200c-1.719400,-16.169500 -2.215100,-33.979300 1.372600,-54.184100c27.744150,-8.465320 67.454855,-6.863720 77.737955,-1.918400c1.227680,18.536600 2.783130,38.740900 -0.120700,56.962700 z", ""),
// 	new ShapeProto("m73.133325,10.008100 c2.383660,15.034500 1.427050,27.947700 0.049400,42.778200c-9.318700,7.486600 -46.822305,2.726500 -59.511305,1.729800c-1.722900,-5.626700 -1.466900,-36.577900 -0.341300,-43.623000c18.481900,-3.144400 45.147000,-5.683100 59.803205,-0.885000 z", Shapes.FILL_BACKGROUND),
// 	new ShapeProto("m8.868620,34.419800 c0.925200,-4.439800 -7.017950,-1.935200 -3.753850,1.681900c1.182600,1.931400 3.631250,-0.059100 3.753850,-1.681900 z", Shapes.FILL_BORDER + Shapes.STROKE_NONE)
// }, 82.066666, 63.903725));

// shapes.put(ElementType.OLD_PHONE.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m 3.04036,0 17.9193,0 c 1.68436,0 3.04036,1.356 3.04036,3.0403 l 0,43.9193 c 0,1.6844 -1.356,3.0404 -3.04036,3.0404 l -17.9193,0 c -1.68436,0 -3.04036,-1.356 -3.04036,-3.0404 l 0,-43.9193 c 0,-1.6843 1.356,-3.0403 3.04036,-3.0403 z ", "fill:bordercolor;"),
// 	new ShapeProto("m 2.42553,2.3136 19.1489,0 0,23.9074 -19.1489,0 z ", "fill:bgcolor;stroke:none;"),
// 	new ShapeProto("m 9.49127,28.3738 5.04255,0 0,4.82 -5.04255,0 z ", "fill:bgcolor;stroke:none;"),
// 	new ShapeProto("m 16.6208,29.5627 5.04255,0 0,2.4422 -5.04255,0 z ", "fill:bgcolor;stroke:none;"),
// 	new ShapeProto("m 2.3617,29.5627 5.04255,0 0,2.4422 -5.04255,0 z ", "fill:bgcolor;stroke:none;"),
// 	new ShapeProto("m 2.3617,35.604 5.04255,0 0,2.4421 -5.04255,0 z ", "fill:bgcolor;stroke:none;"),
// 	new ShapeProto("m 16.6208,35.604 5.04255,0 0,2.4421 -5.04255,0 z ", "fill:bgcolor;stroke:none;"),
// 	new ShapeProto("m 9.49127,40.1476 5.04255,0 0,2.4421 -5.04255,0 z ", "fill:bgcolor;stroke:none;"),
// 	new ShapeProto("m 16.6208,40.1476 5.04255,0 0,2.4421 -5.04255,0 z ", "fill:bgcolor;stroke:none;"),
// 	new ShapeProto("m 2.3617,40.1476 5.04255,0 0,2.4421 -5.04255,0 z ", "fill:bgcolor;stroke:none;"),
// 	new ShapeProto("m 9.49127,35.604 5.04255,0 0,2.4421 -5.04255,0 z ", "fill:bgcolor;stroke:none;"),
// 	new ShapeProto("m 2.3617,44.6909 5.04255,0 0,2.4422 -5.04255,0 z ", "fill:bgcolor;stroke:none;"),
// 	new ShapeProto("m 9.49127,44.6909 5.04255,0 0,2.4422 -5.04255,0 z ", "fill:bgcolor;stroke:none;"),
// 	new ShapeProto("m 16.6208,44.6909 5.04255,0 0,2.4422 -5.04255,0 z ", "fill:bgcolor;stroke:none;")
// }, 24, 50));
// sketchShapes.put(ElementType.OLD_PHONE.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m3.040357,0 l17.919287,-0.519900 c1.683648,-0.048900 3.028393,1.356000 3.040356,3.040300l0.311959,43.919300 c0.011964,1.684300 -1.356404,3.003400 -3.040356,3.040400l-18.959150,0.415900 c-1.683952,0.037000 -3.068336,-1.356200 -3.040357,-3.040400l0.727904,-43.815300 c0.027977,-1.684100 1.356708,-2.991400 3.040357,-3.040300 z", ""),
// 	new ShapeProto("m2.425531,2.313600 l18.940964,-0.519900 l0.519932,23.595400 l-19.564882,0.104000  z", ""),
// 	new ShapeProto("m9.491273,28.373800 l5.042553,-0.519900 l0.311959,5.651900 l-5.354512,-0.312000  z", ""),
// 	new ShapeProto("m16.620844,29.562700 l5.042553,0 l0,2.442200 l-5.562484,0.312000  z", ""),
// 	new ShapeProto("m2.361702,29.562700 l5.042553,0.415900 l0,2.442200 l-5.042553,-0.415900  z", ""),
// 	new ShapeProto("m2.361702,35.604000 l4.938567,-0.519900 l0.103986,2.962000 l-5.042553,0  z", ""),
// 	new ShapeProto("m16.620844,35.604000 l5.042553,0 l0,2.442100 l-5.042553,0.519900  z", ""),
// 	new ShapeProto("m9.491273,40.147600 l5.042553,0 l0.311959,2.962000 l-5.354512,-0.519900  z", ""),
// 	new ShapeProto("m16.620844,40.147600 l5.354512,-0.312000 l-0.311959,2.754100 l-5.042553,0  z", ""),
// 	new ShapeProto("m2.361702,40.147600 l5.094546,-0.312000 l-0.051993,2.754100 l-5.042553,0  z", ""),
// 	new ShapeProto("m9.491273,35.604000 l5.042553,0 l0,2.442100 l-5.146539,0.312000  z", ""),
// 	new ShapeProto("m2.361702,44.690900 l5.042553,0 l0,2.442200 l-5.250526,-0.260000  z", ""),
// 	new ShapeProto("m9.231307,44.482900 l5.302519,0.208000 l0,2.442200 l-5.042553,0  z", ""),
// 	new ShapeProto("m16.620844,44.690900 l5.302519,-0.156000 l-0.259966,2.598200 l-5.198532,0.260000  z", "")
// }, 24.000000, 50.000000));

// shapes.put(ElementType.ANDROID.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m 5.77398,0 14.452,0 c 3.19878,0 5.77398,2.5752 5.77398,5.774 l 0,38.452 c 0,3.1988 -2.57519,5.774 -5.77398,5.774 l -14.452,0 c -3.19878,0 -5.77398,-2.5752 -5.77398,-5.774 l 0,-38.452 c 0,-3.1988 2.57519,-5.774 5.77398,-5.774 z ", "fill:bordercolor;"),
// 	new ShapeProto("m 2.1572,6.576 21.6856,0 0,36.848 -21.6856,0 z ", "fill:bgcolor;stroke:none;"),
// 	new ShapeProto("m 15.1274,46.6587 c 0,0.515806 -0.952467,0.93395 -2.12739,0.93395 -1.17493,0 -2.1274,-0.418144 -2.1274,-0.93395 0,-0.515808 0.952467,-0.93395 2.1274,-0.93395 1.17493,0 2.12739,0.418142 2.12739,0.93395 z ", "fill:bgcolor;stroke:none;")
// }, 26, 50));
// sketchShapes.put(ElementType.ANDROID.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m5.254045,0.415900 l14.971978,-0.727900 c3.195009,-0.155300 6.328982,2.887400 6.293908,6.086000l-0.415945,37.932100 c-0.035074,3.198600 -2.162225,5.844000 -5.358032,5.981900l-14.452046,0.624000 c-3.195806,0.137900 -6.822632,-3.199200 -6.813839,-6.398000l0.103986,-37.828000 c0.008793,-3.198900 2.474981,-5.514700 5.669990,-5.670100 z", ""),
// 	new ShapeProto("m13.203652,47.958493 c-1.724696,-0.100990 -2.520052,-0.067408 -2.331043,-0.987869c0.275355,-1.340964 2.721701,-2.003095 4.051142,-0.985944c0.727074,0.556281 -0.806195,2.027326 -1.720099,1.973812 z", Shapes.FILL_BORDER + Shapes.STROKE_NONE),
// 	new ShapeProto("m1.637271,6.576000 l22.205527,-0.519900 l-0.311959,37.887800 l-21.581609,-0.623900  z", Shapes.FILL_BACKGROUND)
// }, 26.000000, 50.000000));

// shapes.put(ElementType.LIGHTBULB.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m 22.4676,10.5797 c -6.56901,0 -11.8905,5.1708 -11.8905,11.5437 0,2.3507 0.737594,4.538 1.98175,6.3616 2.05617,3.9241 4.36366,5.6306 4.41982,10.4001 l 11.1459,0 c 0.115967,-4.9336 2.54553,-6.7664 4.43191,-10.4001 1.39298,-1.5069 1.7799,-4.4684 1.80159,-6.3616 0,-6.3729 -5.32148,-11.5437 -11.8905,-11.5437 z ", ""),
// 	new ShapeProto("m 18.4085,40.0784 8.02938,0 c 0.581965,0 1.05048,0.4685 1.05048,1.0504 0,0.582 -0.468513,1.0505 -1.05048,1.0505 l -8.02938,0 c -0.581965,0 -1.05048,-0.4685 -1.05048,-1.0505 0,-0.5819 0.468513,-1.0504 1.05048,-1.0504 z ", "fill:none;"),
// 	new ShapeProto("m 18.4085,42.7311 8.02938,0 c 0.581965,0 1.05048,0.4685 1.05048,1.0504 0,0.582 -0.468513,1.0505 -1.05048,1.0505 l -8.02938,0 c -0.581965,0 -1.05048,-0.4685 -1.05048,-1.0505 0,-0.5819 0.468513,-1.0504 1.05048,-1.0504 z ", "fill:none;"),
// 	new ShapeProto("m 18.4411,45.2823 8.02938,0 c 0.581965,0 1.05048,0.4685 1.05048,1.0505 0,0.582 -0.468513,1.0505 -1.05048,1.0505 l -8.02938,0 c -0.581964,0 -1.05048,-0.4685 -1.05048,-1.0505 0,-0.582 0.468514,-1.0505 1.05048,-1.0505 z ", "fill:none;"),
// 	new ShapeProto("m 19.4727,48.0741 c 0.369015,0.8074 1.06096,1.3087 1.88849,1.7116 0.633291,0.3084 1.46226,0.262 2.11641,0 0.797147,-0.3192 1.41094,-0.7966 1.85593,-1.7762 z ", "fill:none;"),
// 	new ShapeProto("m 5.94834,37.4973 4.15444,-4.05476 c 0.304183,-0.296884 0.790202,-0.293115 1.08974,0.00844471 0.299534,0.30156 0.295805,0.78334 -0.00837815,1.08022 l -4.15444,4.05476 c -0.304176,0.296877 -0.790201,0.293101 -1.08974,-0.00845879 -0.299534,-0.30156 -0.295798,-0.783333 0.00837804,-1.08021 z ", "fill:bordercolor;"),
// 	new ShapeProto("m 39.0558,37.5618 -4.15444,-4.05476 c -0.304176,-0.296877 -0.790202,-0.293115 -1.08974,0.00845181 -0.299534,0.30156 -0.29579,0.78334 0.0083852,1.08022 l 4.15444,4.05476 c 0.304176,0.296877 0.790194,0.293108 1.08973,-0.0084517 0.299541,-0.301567 0.295805,-0.78334 -0.008371,-1.08022 z ", "fill:bordercolor;"),
// 	new ShapeProto("m 37.886,22.9798 5.84059,-0.0467203 c 0.423317,-0.00338622 0.761552,-0.346903 0.758373,-0.770291 -0.00317814,-0.423288 -0.34653,-0.761353 -0.769847,-0.757966 l -5.84059,0.0467203 c -0.423317,0.00338622 -0.761552,0.346903 -0.758374,0.770191 0.00317889,0.423388 0.346531,0.761453 0.769848,0.758066 z ", "fill:bordercolor;"),
// 	new ShapeProto("m 1.28836,22.9797 5.84059,-0.0467203 c 0.423317,-0.00338622 0.761552,-0.346903 0.758374,-0.770191 -0.00317814,-0.423288 -0.34653,-0.761353 -0.769848,-0.757966 l -5.84059,0.0467203 c -0.423317,0.00338622 -0.761552,0.346903 -0.758374,0.770191 0.00317814,0.423288 0.34653,0.761353 0.769848,0.757966 z ", "fill:bordercolor;"),
// 	new ShapeProto("m 34.7476,11.2245 4.08769,-4.12042 c 0.299383,-0.30178 0.295637,-0.783917 -0.00839155,-1.08102 -0.304028,-0.2971 -0.789814,-0.29334 -1.0892,0.00844033 l -4.08769,4.12042 c -0.29939,0.301787 -0.295644,0.783924 0.00838451,1.08102 0.304028,0.2971 0.789814,0.29334 1.0892,-0.00844743 z ", "fill:bordercolor;"),
// 	new ShapeProto("m 11.2494,9.98146 -4.15444,-4.05476 c -0.304176,-0.296877 -0.790202,-0.293115 -1.08974,0.00844471 -0.299534,0.30156 -0.29579,0.78334 0.0083852,1.08022 l 4.15444,4.05476 c 0.304176,0.296877 0.790202,0.293115 1.08974,-0.00844471 0.299534,-0.30156 0.29579,-0.78334 -0.0083852,-1.08022 z ", "fill:bordercolor;"),
// 	new ShapeProto("m 21.6435,1.29278 0.0469029,5.76821 c 0.00347031,0.426786 0.34985,0.767836 0.776635,0.764684 0.426786,-0.00315276 0.767578,-0.34928 0.764108,-0.776065 l -0.0469029,-5.76821 c -0.00347031,-0.426786 -0.34985,-0.767836 -0.776636,-0.764684 -0.426785,0.00315275 -0.767577,0.34928 -0.764107,0.776065 z ", "fill:bordercolor;"),
// 	new ShapeProto("m 26.1069,24.375 c -0.467679,0.033 -0.972758,0.1942 -1.33317,0.3931 -1.13684,0.6273 -2.05558,2.9226 -2.27,3.5024 -0.259948,-0.6887 -1.14118,-2.8637 -2.23397,-3.4666 -0.720833,-0.3978 -1.96329,-0.6605 -2.45016,0 -0.591598,0.8024 0.164702,2.1844 0.900794,2.8591 0.982204,0.9003 3.92746,0.8935 3.92746,0.8935 0,0 0.000525,-0.034 0,-0.036 0.539788,-0.011 2.8424,-0.097 3.71127,-0.8935 0.736092,-0.6746 1.49239,-2.0566 0.900794,-2.8591 -0.243436,-0.3302 -0.685337,-0.4259 -1.15302,-0.3931 z ", ""),
// 	new ShapeProto("m 22.4693,28.5688 0,10.6875 ", "")
// }, 45, 50));
// sketchShapes.put(ElementType.LIGHTBULB.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m12.179352,87.570100 l9.166563,-10.937300 c0.671169,-0.800800 1.859839,-0.909500 2.665191,-0.243800c0.805352,0.665600 0.913414,1.846200 0.242254,2.647100l-9.166551,10.937400 c-0.671142,0.800900 -1.859830,0.909600 -2.665182,0.243900c-0.805372,-0.665800 -0.913417,-1.846400 -0.242275,-2.647300 z", "fill:bordercolor;stroke:none;"),
// 	new ShapeProto("m95.742560,87.445300 l-10.033000,-10.148300 c-0.734580,-0.743100 -1.928180,-0.753800 -2.676220,-0.024400c-0.748030,0.729500 -0.758820,1.914900 -0.024250,2.658100l10.033010,10.148400 c0.734570,0.743200 1.928160,0.753900 2.676180,0.024400c0.748050,-0.729600 0.758860,-1.915000 0.024280,-2.658200 z", "fill:bordercolor;stroke:none;"),
// 	new ShapeProto("m91.967240,56.271600 l14.295570,-1.179200 c1.036120,-0.085500 1.801750,-0.990200 1.716620,-2.028600c-0.085080,-1.038000 -0.987740,-1.805100 -2.023870,-1.719600l-14.295550,1.179200 c-1.036140,0.085400 -1.801760,0.990100 -1.716660,2.028300c0.085120,1.038300 0.987770,1.805400 2.023890,1.719900 z", "fill:bordercolor;stroke:none;"),
// 	new ShapeProto("m2.197767,52.173200 l14.256381,1.584200 c1.033290,0.114700 1.958188,-0.626300 2.073762,-1.661500c0.115574,-1.035200 -0.623224,-1.961000 -1.656514,-2.075800l-14.256381,-1.584200 c-1.033279,-0.114800 -1.958178,0.626200 -2.073763,1.661500c-0.115573,1.035200 0.623236,1.961000 1.656515,2.075800 z", "fill:bordercolor;stroke:none;"),
// 	new ShapeProto("m83.290650,26.283600 l11.279150,-8.738630 c0.826090,-0.640000 0.972210,-1.817480 0.327640,-2.639980c-0.644580,-0.822510 -1.828560,-0.969400 -2.654650,-0.329410l-11.279150,8.738620 c-0.826121,0.640100 -0.972230,1.817400 -0.327659,2.640000c0.644579,0.822500 1.828569,0.969400 2.654669,0.329400 z", "fill:bordercolor;stroke:none;"),
// 	new ShapeProto("m25.517092,24.984700 l-7.976911,-11.832980 c-0.584043,-0.866390 -1.754927,-1.098400 -2.625267,-0.520260c-0.870341,0.578140 -1.100812,1.741080 -0.516769,2.607460l7.976885,11.833080 c0.584053,0.866300 1.754938,1.098300 2.625273,0.520200c0.870334,-0.578100 1.100841,-1.741200 0.516789,-2.607500 z", "fill:bordercolor;stroke:none;"),
// 	new ShapeProto("m53.035120,2.604720 l1.505640,14.114550 c0.111404,1.044370 1.040206,1.796140 2.082525,1.685670c1.042333,-0.110330 1.791772,-1.039990 1.680367,-2.084360l-1.505638,-14.114550 c-0.111407,-1.044370 -1.040210,-1.796130 -2.082529,-1.685670c-1.042319,0.110460 -1.791771,1.039990 -1.680365,2.084360 z", "fill:bordercolor;stroke:none;"),
// 	new ShapeProto("m57.631365,23.970700 c-0.230000,-0.016000 -0.436000,0.006000 -0.630000,0.043000c-0.161000,-0.031000 -0.332000,-0.046000 -0.514000,-0.043000c-14.992000,0.220000 -28.040000,7.262000 -30.264000,23.087000c-2.016000,14.345000 4.414000,29.319000 13.150000,40.509000c-1.341000,0.995000 -2.420000,2.315000 -2.523000,4.063000c-0.105000,1.775000 0.821000,3.183000 2.032000,4.330000c-1.105000,0.945000 -1.941000,2.148000 -2.032000,3.677000c-0.148000,2.504000 1.742000,4.286000 3.612000,5.622000c0.072000,0.052000 0.144000,0.084000 0.216000,0.124000c-0.538000,0.656000 -0.913000,1.422000 -0.967000,2.330000c-0.125000,2.105000 1.465000,3.604000 3.037000,4.727000c0.107000,0.077000 0.214000,0.133000 0.321000,0.184000c0.307000,0.317000 0.732000,0.536000 1.291000,0.559000c7.991000,0.327000 15.954000,0.501000 23.756000,-1.511000c0.505000,-0.131000 0.852000,-0.403000 1.073000,-0.738000c0.184000,-0.200000 0.335000,-0.451000 0.431000,-0.766000c0.579000,-1.902000 0.353000,-3.824000 -0.628000,-5.427000c0.548000,-0.123000 1.095000,-0.254000 1.642000,-0.395000c0.602000,-0.155000 1.013000,-0.479000 1.276000,-0.878000c0.219000,-0.238000 0.398000,-0.536000 0.512000,-0.911000c0.715000,-2.351000 0.387000,-4.723000 -0.899000,-6.674000c0.151000,-0.128000 0.284000,-0.267000 0.387000,-0.423000c0.219000,-0.238000 0.398000,-0.536000 0.512000,-0.911000c0.779000,-2.560000 0.332000,-5.149000 -1.256000,-7.187000c17.170005,-20.083000 20.916005,-60.956000 -13.535000,-63.391000 zm-14.532000,77.624000 c-0.099000,-0.100000 -0.204000,-0.198000 -0.328000,-0.287000c-0.441000,-0.315000 -0.819000,-0.668000 -1.186000,-1.068000c-0.103000,-0.113000 -0.129000,-0.318000 -0.150000,-0.363000c0.149000,-0.407000 0.712000,-0.777000 1.261000,-1.066000c0.196000,0.052000 0.403000,0.088000 0.635000,0.088000c8.118000,-0.014000 16.229000,-0.201000 24.339000,-0.547000c0.396000,0.570000 0.549000,1.179000 0.523000,1.854000c-8.260000,1.913000 -16.659000,1.727000 -25.094000,1.389000 zm0,-8.007000 c-0.099000,-0.100000 -0.204000,-0.198000 -0.328000,-0.287000c-0.441000,-0.315000 -0.819000,-0.668000 -1.186000,-1.068000c-0.103000,-0.113000 -0.129000,-0.318000 -0.150000,-0.363000c0.149000,-0.407000 0.712000,-0.777000 1.261000,-1.066000c0.196000,0.052000 0.403000,0.088000 0.635000,0.088000c8.118000,-0.014000 16.229000,-0.201000 24.339000,-0.547000c0.396000,0.570000 0.549000,1.179000 0.523000,1.854000c-8.260000,1.912000 -16.659000,1.727000 -25.094000,1.389000 zm1.865000,15.771000 c-0.083000,-0.084000 -0.171000,-0.167000 -0.276000,-0.241000c-0.371000,-0.265000 -0.689000,-0.562000 -0.997000,-0.898000c-0.087000,-0.095000 -0.108000,-0.267000 -0.126000,-0.305000c0.126000,-0.342000 0.599000,-0.653000 1.061000,-0.897000c0.165000,0.044000 0.339000,0.074000 0.535000,0.074000c6.826000,-0.012000 13.645000,-0.169000 20.464000,-0.460000c0.333000,0.479000 0.461000,0.991000 0.440000,1.559000c-6.946000,1.608000 -14.008000,1.452000 -21.101000,1.168000 zm22.489000,-24.662000 c-0.307000,0.347000 -0.473000,0.720000 -0.548000,1.093000c-3.063000,0.127000 -6.126000,0.216000 -9.190000,0.297000c0.412000,-6.071000 0.479000,-12.148000 0.485000,-18.231000c4.200000,-3.744000 7.033000,-8.615000 8.310000,-14.107000c0.667000,-2.871000 -3.744000,-4.090000 -4.412000,-1.216000c-1.044000,4.489000 -3.180000,8.303000 -6.432000,11.447000c-3.446000,-2.742000 -5.912000,-6.373000 -7.078000,-10.684000c-0.770000,-2.845000 -5.184000,-1.636000 -4.412000,1.216000c1.515000,5.602000 4.869000,10.184000 9.447000,13.622000c-0.009000,6.025000 -0.079000,12.044000 -0.491000,18.056000c-3.007000,0.057000 -6.014000,0.110000 -9.021000,0.119000c-0.082000,-0.159000 -0.180000,-0.316000 -0.306000,-0.468000c-8.012000,-9.719000 -13.513000,-22.274000 -13.326000,-35.012000c0.221000,-15.106000 12.078000,-22.079000 26.008000,-22.283000c0.225000,-0.003000 0.427000,-0.040000 0.617000,-0.093000c0.165000,0.047000 0.339000,0.079000 0.527000,0.093000c30.464995,2.154000 24.553005,39.501000 9.822000,56.151000 z", "fill:bordercolor;stroke:none;")
// }, 108.491540, 113.383400));

// shapes.put(ElementType.CLASS.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m2.135216,0 l45.729568,0 c1.182910,0 2.135216,0.952300 2.135216,2.135200l0,30.729600 c0,1.182900 -0.952306,2.135200 -2.135216,2.135200l-45.729568,0 c-1.182910,0 -2.135216,-0.952300 -2.135216,-2.135200l0,-30.729600 c0,-1.182900 0.952306,-2.135200 2.135216,-2.135200z", "")
// }, 50, 35));

		// new official shapes where conversion from generic element to legacy official doesn't work.
// shapes.put(ElementType.ACTIVITY_START2.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m43.990518,21.926754 a21.981054,21.891428 0 1 1 -43.962108,0a21.981054,21.891428 0 1 1 43.962108,0 z", "")
// }, 44.033047, 43.847889));
// shapes.put(ElementType.ACTIVITY_END2.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m56.888216,28.549570 a28.420302,28.498391 0 1 1 -56.840603,0a28.420302,28.498391 0 1 1 56.840603,0 z", ""),
// 	new ShapeProto("m45.496831,28.549559 a17.028917,17.008535 0 1 1 -34.057833,0a17.028917,17.008535 0 1 1 34.057833,0 z", "fill:bordercolor;stroke:none;")
// }, 56.928741, 57.098091));

// shapes.put(ElementType.USE_CASE.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m49.999996,14.999995 c0,8.008126 -11.469022,14.999997 -24.999997,14.999997c-13.530976,0 -24.999999,-6.991864 -24.999999,-14.999990c0,-8.008128 11.469023,-15.000004 24.999999,-15.000004c13.530975,0 24.999997,6.991869 24.999997,14.999997 z", "")
// }, 50, 30));

// shapes.put(ElementType.FORK_HORIZONTAL.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m198.300420,10.023030 c-0.076290,4.631870 -3.895140,8.325730 -8.527020,8.249440l-181.534383,0.061540 c-4.633122,-0.076310 -8.326979,-3.895160 -8.250686,-8.527040c0.076293,-4.631870 3.893896,-8.325750 8.527018,-8.249440l181.534381,-0.061540 c4.631870,0.076290 8.327000,3.893910 8.250690,8.527040", "fill:bordercolor;stroke:none")
// }, 198.288760, 19.830000));
// shapes.put(ElementType.FORK_VERTICAL.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m9.871870,198.302970 c-4.635670,-0.073320 -8.329490,-3.892220 -8.256270,-8.521640l-0.179080,-181.538030 c0.073200,-4.628170 3.885940,-8.328340 8.521610,-8.255020c4.628170,0.073200 8.328240,3.892200 8.255040,8.520370l0.180330,181.538050 c-0.073220,4.629420 -3.892210,8.329490 -8.521630,8.256270", "fill:bordercolor;stroke:none")
// }, 19.830000, 198.291240));


// 		sketchShapes.put(ElementType.CLASS.getValue(), new ShapeGroup(new ShapeProto[]{
// new ShapeProto("m215.912990,72.141160 c-0.061600,5.055870 -4.096250,9.152500 -9.152500,9.152500l-194.869150,-1.517870 c-5.055000,0 -9.152500,-4.096250 -9.152500,-9.151250l-2.667900,-61.407980 c0,-5.056250 4.096250,-9.152500 9.152500,-9.152500l195.252500,0 c5.056250,0 12.265350,4.096630 12.203750,9.152500 z", "")
// 		}, 216.757810, 80.976562));
// sketchShapes.put(ElementType.CLASS.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m0.704653,-0.381500 l47.636985,-0.023100 c1.182910,0 1.849104,0.666300 1.849104,1.849200c-0.143650,9.341200 0.446692,20.290000 0.381483,32.183100c0.040976,1.182200 -0.475452,2.326000 -1.658362,2.326000l-47.508888,-0.858300 c-1.182910,0 -1.181508,-0.284700 -1.181508,-1.467600l-0.604951,-32.637100 c0,-1.182900 -0.096773,-1.372200 1.086137,-1.372200 z", "")
// }, 50.000000, 35.000000)

// );
// sketchShapes.put(ElementType.SEQUENCE.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m49.489226,-0.381500 l-47.636985,-0.023100 c-1.182910,0 -1.849104,0.666300 -1.849104,1.849200c0.143650,9.341200 -0.446692,20.290000 -0.381483,32.183100c-0.040976,1.182200 0.475452,2.326000 1.658362,2.326000l47.508888,-0.858300 c1.182909,0 1.181507,-0.284700 1.181507,-1.467600l0.604952,-32.637100 c0,-1.182900 0.096773,-1.372200 -1.086137,-1.372200 z", "")
// }, 50.000000, 35.000000));
// sketchShapes.put(ElementType.HORIZONTAL_PARTITION.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m0.389020,0.151450 l51.514450,-0.112050 c0.649420,0.044460 0.782120,0.399620 0.826580,0.960110c-0.143650,9.341200 -0.398010,23.001920 -0.463220,34.895020c-0.047940,0.871000 -0.253170,0.903330 -0.902580,0.858870l-51.075150,0.075360 c-0.693880,0 -0.648020,0.070960 -0.648020,-0.711820l0.195300,-35.260160 c0,-0.738320 0.036590,-0.749790 0.552640,-0.705330 z", "")
// }, 52.750027, 36.843750));
		// sketchShapes.put(ElementType.CLASS.getValue(), new ShapeGroup(new ShapeProto[]{
		// 	new ShapeProto("m2.135216,0 c0,0 13.550818,0.429500 22.864784,0c9.313966,-0.429500 22.864784,0 22.864784,0c1.182910,0 2.135216,0.952300 2.135216,2.135200c0,0 -0.367897,1.465300 -0.095000,8.879600c0.202533,5.502500 0.095000,21.850000 0.095000,21.850000c0.040976,1.182200 -0.952306,2.135200 -2.135216,2.135200c0,0 -15.529302,0.190700 -22.864784,0c-7.621595,0.667600 -22.864784,0 -22.864784,0c-1.182910,0 -2.135216,-0.952300 -2.135216,-2.135200c0,0 0.286113,-7.668200 0,-15.364800c-0.286113,-7.696600 0,-15.364800 0,-15.364800c0,-1.182900 0.952306,-2.135200 2.135216,-2.135200z", "")
		// }, 50, 35));

// sketchShapes.put(ElementType.CIRCLE.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m7.282185,9.583000 c13.388000,-17.507990 55.611000,-10.303000 57.673000,16.473000c2.058000,26.775000 -20.597000,41.195000 -44.285000,29.865000c-23.684000,-11.322000 -25.256000,-30.815000 -13.388000,-46.338000 z", "")
// }, 65.137627, 59.867428));

// sketchShapes.put(ElementType.USE_CASE.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m10.620043,2.079800 c10.179862,-3.892000 34.138710,-2.242100 38.375122,5.731900c4.236163,7.974100 -6.479323,16.695200 -18.689248,17.692700c-12.210426,0.996600 -25.209891,-1.026200 -29.155329,-9.220400c-3.239036,-6.728400 0.997378,-10.964900 9.469455,-14.204200 z", "")
// }, 50, 25));

// sketchShapes.put(ElementType.ACTOR.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m26.121513,7.430600 c9.727500,-12.098750 26.956250,-7.811200 30.736250,2.266200c3.777500,10.073800 -2.267500,26.452600 -18.138750,24.941300c-15.871250,-1.511200 -21.917500,-15.621300 -12.597500,-27.207500 z", ""),
// 	new ShapeProto("m18.059388,94.591200 l-17.885000,-0.756200 c0,0 -0.250000,-33.755000 0,-39.045000c0.251250,-5.290000 5.291250,-7.557600 10.581250,-7.557600c5.290000,0 53.906245,-2.521200 57.940005,-1.766200c4.032490,0.757500 9.571240,3.531300 9.824990,10.833800c0.253750,7.305000 0.503750,38.543700 0.503750,38.543700l-16.626250,1.011300 ", ""),
// 	new ShapeProto("m18.564138,68.138600 l0.250000,33.255000 l44.088745,0.755000 l-2.267490,-35.767500 ", Shapes.STROKE_LINE_ROUND)
// }, 79.078140, 102.204260)
// );

// sketchShapes.put(ElementType.SERVER.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m0.025649,8.590800 l0.611944,37.958200 l13.469328,8.571300 l-0.612354,-36.734100 ", ""),
// 	new ShapeProto("m31.249553,0.019500 c0,0 -29.387662,5.509900 -29.999606,7.347000c-0.612354,1.836700 12.244620,11.020300 12.244620,11.020300l33.060146,-10.407900 l-15.305160,-7.959400  z", ""),
// 	new ShapeProto("m46.554713,7.978400 l-0.611943,37.958200 l-31.835849,9.183700 l-0.671921,-36.734100 ", ""),
// 	new ShapeProto("m4.923248,22.671800 l4.285660,3.061200 ", "")
// }, 46.579338, 55.141220));

// sketchShapes.put(ElementType.STORAGE.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m0.139629,8.109600 v25.554700 c-0.085370,0 10.417202,8.051700 25.819637,7.351300c15.402434,-0.700300 23.453844,-6.300900 23.453844,-6.300900v-25.554700 ", ""),
// 	new ShapeProto("m26.043228,0.408300 c29.996845,2.647000 33.606048,14.002900 0,14.702900c-33.605696,0.700700 -35.705436,-17.853000 0,-14.702900 z", "")
// }, 49.963425, 41.105717));

// sketchShapes.put(ElementType.NOTE.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m0,0.033600 l49.976084,-0.191900 l0.441281,12.445600 l-50.172731,-0.038300  z", "stroke-linejoin:round;"),
// 	new ShapeProto("m3.248271,-6.202400 l38.009314,-2.407400 l-0.603969,2.407400 l3.762540,0.531500 l-2.130631,2.445800 l-1.313180,1.852100 l3.613041,0 l-2.874526,2.848300 l2.216143,2.706900 l-40.979521,2.250000 l0.300789,-2.406600 l-2.715465,-0.902500 l3.318235,-1.503800 l-3.851042,-3.491200 l3.364879,-1.282200 l-2.052295,-1.423800 l1.935688,-1.624500  z", "fill:bordercolor;stroke:none;", false)
// }, 49.924129, 12.190000));

// sketchShapes.put(ElementType.COMPONENT.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m1.557000,94.088004 l194.052000,-1.493000 l0,-86.156000 l-23.882000,0 l-1.495000,-18.391000 l-29.853000,0 l-2.986000,16.898000 l-70.157000,1.493000 l-5.970000,-13.913000 l-25.376000,0 l-2.986000,13.913000 l-32.840000,0  z", "stroke-linejoin:round")
// }, 195.658750, 94.130005));

// sketchShapes.put(ElementType.ACTIVITY.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m221.250000,71.757810 c0,11.795000 -9.560000,21.355000 -21.355000,21.355000l-178.473750,0 c-11.795000,0 -21.355000,-9.560000 -21.355000,-21.355000l0,-44.237500 c0,-11.795000 9.560000,-21.355000 21.355000,-21.355000l181.523750,-6.101250 c11.796250,0 21.356250,12.611250 21.356250,24.406250l-3.051250,47.287500  z", "stroke-linecap:round")
// }, 224.378130, 93.164062)
// );

// sketchShapes.put(ElementType.CHOICE.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m40.225004,0.062250 l-40.183000,43.953000 l42.697000,38.928000 l36.417000,-41.441000  z", "stroke-linecap:round;stroke-linejoin:round;stroke-miterlimit:10")
// }, 79.192505, 83.002502));

// sketchShapes.put(ElementType.ACTIVITY_START2.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m7.288095,9.589460 c13.388000,-17.508000 55.611000,-10.303000 57.673000,16.473000c2.058000,26.775000 -20.597000,41.195000 -44.285000,29.865000c-23.684000,-11.322000 -25.256000,-30.815000 -13.388000,-46.338000 z", "stroke-linecap:round")
// }, 65.137627, 59.867428));

// sketchShapes.put(ElementType.ACTIVITY_END2.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m64.538635,32.678480 c-1.709000,21.971000 -41.058000,38.896000 -57.355000,17.545000c-16.294000,-21.342000 -5.128000,-45.768000 20.913000,-49.148000c26.034000,-3.383000 37.958000,12.121000 36.442000,31.603000 z", ""),
// 	new ShapeProto("m14.174635,23.956480 c3.073000,-13.988000 28.566000,-12.258000 35.105000,-0.340000c6.539000,11.914000 -0.021000,19.932000 -14.457000,22.129000c-15.147000,2.308000 -24.091000,-6.123000 -20.648000,-21.789000 z", "fill:bordercolor;stroke:none;")
// }, 65.137627, 59.867428));


// sketchShapes.put(ElementType.FORK_HORIZONTAL.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m198.288750,8.390000 c0,4.632500 -3.757500,8.388750 -8.390000,8.388750l-181.508750,3.051250 c-4.633750,0 -8.390000,-3.757500 -8.390000,-8.390000c0,-4.632500 3.756250,-8.388750 8.390000,-8.388750l181.508750,-3.051250 c4.632500,0 8.390000,3.756250 8.390000,8.390000", "fill:bordercolor;stroke:none;")
// }, 198.288760, 19.830000));
// sketchShapes.put(ElementType.FORK_VERTICAL.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m11.440000,198.291250 c-4.636250,0 -8.390000,-3.760000 -8.390000,-8.390000l-3.050000,-181.512500 c0,-4.628750 3.753750,-8.388750 8.390000,-8.388750c4.628750,0 8.388750,3.760000 8.388750,8.388750l3.051250,181.512500 c0,4.630000 -3.760000,8.390000 -8.390000,8.390000", "fill:bordercolor;stroke:none;")
// }, 19.830000, 198.291240));

// sketchShapes.put(ElementType.MIND_CENTRAL.getValue(), new ShapeGroup(new ShapeProto[]{
// 	new ShapeProto("m14.490390,4.038670 l240.820140,-4 c19.334290,-0.321140 22.283720,7.635180 22.666670,19.111120l2.888880,86.571940 c0.496100,14.866670 -3.335140,23.935290 -19.111100,23.555550l-249.264590,-6 c-6.892230,-0.165900 -12.590560,-5.551760 -12.444450,-12.444440l2,-94.349720 c0.146110,-6.892680 5.551170,-12.329950 12.444450,-12.444450 z", "")
// }, 280.473790, 129.317460));

}


}