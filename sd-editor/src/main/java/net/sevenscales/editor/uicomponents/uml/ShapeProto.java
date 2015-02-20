package net.sevenscales.editor.uicomponents.uml;

import com.google.gwt.core.client.JsArray;


public class ShapeProto {
	public JsArray<Shapes.JsPathData> pathDatas;
	public String style;
	private boolean scalable;
	private double width;

	ShapeProto(String path) {
		this(path, null, true);
	}

	ShapeProto(String path, String style) {
		this(path, style, true);
	}

	ShapeProto(String path, String style, boolean scalable) {
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