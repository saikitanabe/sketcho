package net.sevenscales.editor.uicomponents.uml;

import java.util.Map;
import java.util.HashMap;

import net.sevenscales.domain.ElementType;


public class Shapes {
	private static final Map<ElementType,Proto> shapes;

	public static class Proto {
		public String path;
		public float translateX;
		public float translateY;

		private Proto(String path, float translateX, float translateY) {
			this.path = path;
			this.translateX = translateX;
			this.translateY = translateY;
		}
	}

	static {
		shapes = new HashMap<ElementType,Proto>();
		shapes.put(ElementType.STAR5, new Proto("m83.703402,82.745628c0,9.906024-10.913134,17.936442-24.375166,17.936442-13.462033,0-24.375166-8.030418-24.375166-17.936442s10.913133-17.936443,24.375166-17.936443c13.462032,0,24.375166,8.030419,24.375166,17.936443z", -33.95307f + 3.1311035f,244.45851f + -308.2677f));
	}

	public static Proto get(String elementType) {
		return get(ElementType.getEnum(elementType));
	}

	public static Proto get(ElementType type) {
		return shapes.get(type);
	}
}