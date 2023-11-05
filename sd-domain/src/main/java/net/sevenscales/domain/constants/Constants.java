package net.sevenscales.domain.constants;

import net.sevenscales.domain.utils.Debug;

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
	public static final int CORPORATE_SEQUENCE_LINE_WEIGHT = 1;

	public static final int ELLIPSE_RECT_SHAPE_VERSION = 7;

	public static int ZOOM_DEFAULT_INDEX;
  private static double zoomSmallest = 0.1;
  // private static double zoomStep = 0.0225;

  public static final double[] ZOOM_FACTORS;

	// 0.2f, 0.25f, 
	// public static final double[] ZOOM_FACTORS = new double[]{
	// 	0.1,
	// 	0.11,
	// 	0.12,
	// 	0.13,
	// 	0.14,
	// 	0.15,
	// 	0.16,
	// 	0.175,
	// 	0.2,
	// 	0.225,
	// 	0.25,
	// 	0.275,
	// 	0.3,
	// 	0.325,
	// 	0.35,
	// 	0.37,
	// 	0.4,
	// 	0.45,
	// 	0.5,
	// 	0.55,
	// 	0.6,
	// 	0.65,
	// 	0.7, 
	// 	0.75,
	// 	0.8,
	// 	0.85,
	// 	0.9,
	// 	0.95,
	// 	1.0,
	// 	1.05,
	// 	1.1,
	// 	1.15,
	// 	1.2,
	// 	1.25,
	// 	1.3,
	// 	1.35,
	// 	1.4,
	// 	1.45,
	// 	1.5,
	// 	1.55,
	// 	1.6,
	// 	1.7,
	// 	1.75,
	// 	1.8,
	// 	1.85,
	// 	1.9,
	// 	1.95,
	// 	2.00,
	// 	2.05,
	// 	2.1,
	// 	2.15,
	// 	2.2,
	// 	2.25,
	// 	2.3,
	// 	2.35,
	// 	2.4,
	// 	2.45,
	// 	2.5,
	// 	2.55,
	// 	2.6,
	// 	2.7,
	// 	2.75,
	// 	2.8,
	// 	2.85,
	// 	2.9,
	// 	2.95,
	// 	3,
	// 	3.05,
	// 	3.10,
	// 	3.15,
	// 	3.2,
	// 	3.25
	// };

	static {
    java.util.List<Double> values = new java.util.ArrayList<Double>();
    
    for (double value = zoomSmallest; value <= 3.6;) {
      // ZOOM_FACTORS[i] = value;
      values.add(value);

      if (value <= 0.26) {
        value += 0.005;
      } else if (value <= 1) {
        value += 0.03;
      } else if (value <= 2) {
        value += 0.04;
      } else {
        value += 0.05;
      }
    }

    ZOOM_FACTORS = new double[values.size()];
    for (int i = 0; i < ZOOM_FACTORS.length; ++i) {
      ZOOM_FACTORS[i] = values.get(i);
    }

		int i = 0;
		for (double f : ZOOM_FACTORS) {
			if (f >= 1.0f) {
				ZOOM_DEFAULT_INDEX = i;
				break;
			}
			++i;
		}

    Debug.log("ZOOM_FACTORS", ZOOM_FACTORS, "ZOOM_DEFAULT_INDEX", ZOOM_DEFAULT_INDEX);
	}

}
