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

	public static final int CORPORATE_MODE_LINE_WEIGHT = 2;

	public static final int ELLIPSE_RECT_SHAPE_VERSION = 7;

	public static int ZOOM_DEFAULT_INDEX;
	// 0.2f, 0.25f, 
	public static final double[] ZOOM_FACTORS = new double[]{
		0.1,
		0.11,
		0.12,
		0.13,
		0.14,
		0.15,
		0.16,
		0.175,
		0.2,
		0.225,
		0.25,
		0.275,
		0.3,
		0.325,
		0.35,
		0.37,
		0.4,
		0.45,
		0.5,
		0.55,
		0.6,
		0.65,
		0.7, 
		0.75,
		0.8,
		0.85,
		0.9,
		0.95,
		1.0,
		1.05,
		1.1,
		1.15,
		1.2,
		1.25,
		1.3,
		1.35,
		1.4,
		1.45,
		1.5,
		1.55
	};

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
