package net.sevenscales.domain.constants;

public class Constants {
  public static final int PAGE_TYPE_DOC = 1;
  public static final int PAGE_TYPE_SKETCH = 2;
  
  public static final String SKETCH_STATUS = "Status";
  
	public static final String ID_SEPARATOR = "-";

	public static final int SKETCH_MODE_LINE_WEIGHT_LIBRARY = 4;
	public static final int SKETCH_MODE_LINE_WEIGHT = 3;
	public static final int SKETCH_MODE_REL_LINE_WEIGHT = 2;
	public static final int SKETCH_SEPARATOR_WEIGHT = 2;

	public static final int ELLIPSE_RECT_SHAPE_VERSION = 7;

	public static int ZOOM_DEFAULT_INDEX;
	// 0.2f, 0.25f, 
	public static final double[] ZOOM_FACTORS = new double[]{0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0, 1.1, 1.2, 1.3, 1.4};

	static {
		int i = 0;
		for (double f : ZOOM_FACTORS) {
			if (f == 1.0f) {
				ZOOM_DEFAULT_INDEX = i;
				break;
			}
			++i;
		}
	}

}
