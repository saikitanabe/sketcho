package net.sevenscales.domain;

/**
* Each property has it's own range. 
* Those cannot overlap, so properties can be combined!!
*/
public enum ShapeProperty {
	TEXT_POSITION_CENTER							(0x000001),
	TEXT_POSITION_TOP 								(0x000002),
	TEXT_POSITION_RIGHT 							(0x000004),
	TEXT_POSITION_BOTTOM							(0x000008),
	TEXT_POSITION_LEFT  							(0x000010),
	TEXT_RESIZE_DIR_HORIZONTAL				(0x000100),
	TEXT_RESIZE_DIR_VERTICAL					(0x000200),
	DEGREES_0 												(0x001000),
	DEGREES_90 												(0x002000),
	SHAPE_AUTO_RESIZE_TRUE  					(0x010000),
	SHAPE_AUTO_RESIZE_FALSE						(0x020000),
	CURVED_ARROW										  (0x100000),
	CLOSEST_PATH										  (0x200000),
	NO_TEXT_AUTO_ALIGN								(0x000020);

	private int value;

	private ShapeProperty(int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}

	/**
	* TextPosition
	*/
	public static boolean isTextPositionCenter(int value) {
		return (value & TEXT_POSITION_CENTER.getValue()) == TEXT_POSITION_CENTER.getValue();
	}
	public static boolean isTextPositionTop(int value) {
		return (value & TEXT_POSITION_TOP.getValue()) == TEXT_POSITION_TOP.getValue();
	}
	public static boolean isTextPositionRight(int value) {
		return (value & TEXT_POSITION_RIGHT.getValue()) == TEXT_POSITION_RIGHT.getValue();
	}
	public static boolean isTextPositionBottom(int value) {
		return (value & TEXT_POSITION_BOTTOM.getValue()) == TEXT_POSITION_BOTTOM.getValue();
	}
	public static boolean isTextPositionLeft(int value) {
		return (value & TEXT_POSITION_LEFT.getValue()) == TEXT_POSITION_LEFT.getValue();
	}

	/**
	* Resize direction.
	*/
	public static boolean isTextResizeDimHorizontalResize(int value) {
		return (value & TEXT_RESIZE_DIR_HORIZONTAL.getValue()) == TEXT_RESIZE_DIR_HORIZONTAL.getValue();
	}
	public static boolean isTextResizeDimVerticalResize(int value) {
		return (value & TEXT_RESIZE_DIR_VERTICAL.getValue()) == TEXT_RESIZE_DIR_VERTICAL.getValue();
	}

	/**
	* TextDegree
	*/
	public static boolean isDegress0(int value) {
		return (value & DEGREES_0.getValue()) == DEGREES_0.getValue();
	}
	public static boolean isDegress90(int value) {
		return (value & DEGREES_90.getValue()) == DEGREES_90.getValue();
	}

	/**
	* ShapeAutoReisze: should shape autoresize.
	*/
	public static boolean isShapeAutoResizeTrue(int value) {
		return (value & SHAPE_AUTO_RESIZE_TRUE.getValue()) == SHAPE_AUTO_RESIZE_TRUE.getValue();
	}
	public static boolean isShapeAutoResizeFalse(int value) {
		return (value & SHAPE_AUTO_RESIZE_FALSE.getValue()) == SHAPE_AUTO_RESIZE_FALSE.getValue();
	}

	public static boolean isCurvedArrow(int value) {
		return (value & CURVED_ARROW.getValue()) == CURVED_ARROW.getValue();
	}

	public static boolean isClosestPath(int value) {
		return (value & CLOSEST_PATH.getValue()) == CLOSEST_PATH.getValue();
	}

	public static boolean isNoTextAutoAlign(Integer value) {
		if (value == null) {
			// nothing is set and text auto align is on
			return false;
		}
		return (value & NO_TEXT_AUTO_ALIGN.getValue()) == NO_TEXT_AUTO_ALIGN.getValue();
	}

	public static int clear(int current, int toclear) {
    return current & ~toclear;
	}

}