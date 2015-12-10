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
	public static final float[] ZOOM_FACTORS = new float[]{0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f, 1.1f, 1.2f, 1.3f, 1.4f};

	static {
		int i = 0;
		for (float f : ZOOM_FACTORS) {
			if (f == 1.0f) {
				ZOOM_DEFAULT_INDEX = i;
				break;
			}
			++i;
		}
	}

}
