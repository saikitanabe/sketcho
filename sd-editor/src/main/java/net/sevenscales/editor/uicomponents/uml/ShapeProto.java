package net.sevenscales.editor.uicomponents.uml;

import java.util.Map;
import java.util.HashMap;

import com.google.gwt.core.client.JsArray;


public class ShapeProto {
	public JsArray<Shapes.JsPathData> pathDatas;
	public String style;
	private boolean noscaling;
	private double width;
	public Map<String, String> defaultData;

	ShapeProto(String path) {
		this(path, null, false, null);
	}

	ShapeProto(String path, String style) {
		this(path, style, false, null);
	}

	ShapeProto(String path, String style, boolean noscaling, String defaultData) {
		this.style = style;
		importDefaultData(defaultData);
		this.noscaling = noscaling;
		pathDatas = parse(path);

		width = getWidth();
	}

	private double getWidth() {
		double right = Double.MIN_VALUE;
		double left = Double.MAX_VALUE;
		double result = 0;
		double prevx = 0;
		for (int i = 0; i < pathDatas.length(); ++i) {
			Shapes.JsPathData pd = pathDatas.get(i);

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

	private native JsArray<Shapes.JsPathData> parse(String d)/*-{
		var result = $wnd.svgPathParser.parse(d)
		return result
	}-*/;

	public String toPath(double factorX, double factorY, double mainWidth) {
			// could cache if prev factors are the same!
		String result = "";
		for (int i = 0; i < pathDatas.length(); ++i) {
			Shapes.JsPathData current = pathDatas.get(i);
			if (!noscaling) {
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

	public void importDefaultData(String defaultData) {
		if (defaultData != null && defaultData.length() > 0) {
			parseDefaultData(defaultData);
		}
	}

	private void parseDefaultData(String dd) {
		if (this.defaultData == null) {
			this.defaultData = new HashMap<String, String>();
		}

		String[] pairs = dd.split(";");
		for (int i = 0; i < pairs.length; ++i) {
			String[] pair = pairs[i].split(":");
			if (pair.length == 2) {
				this.defaultData.put(pair[0], pair[1]);
			}
		}
	}

}