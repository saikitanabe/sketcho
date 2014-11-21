package net.sevenscales.domain;

/**
* Each property has it's own range. 
* Those cannot overlap, so properties can be combined!!
*/
public enum ShapeProperty {
	TEXT_TITLE_CENTER									(0x000001),
	TEXT_POSITION_TOP 								(0x000002),
	TEXT_POSITION_RIGHT 							(0x000004),
	TEXT_POSITION_BOTTOM							(0x000008),
	TEXT_POSITION_LEFT  							(0x000010),
	TEXT_POSITION_MIDDLE							(0x000400),
	TEXT_RESIZE_DIR_HORIZONTAL				(0x000100),
	TEXT_RESIZE_DIR_VERTICAL					(0x000200),
	DEGREES_0 												(0x001000),
	DEGREES_90 												(0x002000),
	DISABLE_SHAPE_AUTO_RESIZE					(0x010000),
	CURVED_ARROW										  (0x100000),
	CLOSEST_PATH										  (0x200000),
	CENTERED_PATH											(0x400000),
	NO_TEXT_AUTO_ALIGN								(0x000020),
	BOLD_TITLE												(0x000040),
	CENTERED_TEXT											(0x000080);

	private int value;

	private ShapeProperty(int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}

	/**
	* Title centered like in class diagram.
	*/
	public static boolean isTextTitleCenter(Integer value) {
		return value != null && (value & TEXT_TITLE_CENTER.getValue()) == TEXT_TITLE_CENTER.getValue();
	}
	public static boolean isTextPositionTop(Integer value) {
		return value != null && (value & TEXT_POSITION_TOP.getValue()) == TEXT_POSITION_TOP.getValue();
	}
	public static boolean isTextPositionRight(Integer value) {
		return value != null && (value & TEXT_POSITION_RIGHT.getValue()) == TEXT_POSITION_RIGHT.getValue();
	}
	public static boolean isTextPositionBottom(Integer value) {
		return value != null && (value & TEXT_POSITION_BOTTOM.getValue()) == TEXT_POSITION_BOTTOM.getValue();
	}
	public static boolean isTextPositionLeft(Integer value) {
		return value != null && (value & TEXT_POSITION_LEFT.getValue()) == TEXT_POSITION_LEFT.getValue();
	}
	public static boolean isTextPositionMiddle(Integer value) {
		return value != null && (value & TEXT_POSITION_MIDDLE.getValue()) == TEXT_POSITION_MIDDLE.getValue();
	}

	/**
	* Resize direction.
	*/
	public static boolean isTextResizeDimHorizontalResize(Integer value) {
		return value != null && (value & TEXT_RESIZE_DIR_HORIZONTAL.getValue()) == TEXT_RESIZE_DIR_HORIZONTAL.getValue();
	}
	public static boolean isTextResizeDimVerticalResize(Integer value) {
		return value != null && (value & TEXT_RESIZE_DIR_VERTICAL.getValue()) == TEXT_RESIZE_DIR_VERTICAL.getValue();
	}

	/**
	* TextDegree
	*/
	public static boolean isDegress0(Integer value) {
		return value != null && (value & DEGREES_0.getValue()) == DEGREES_0.getValue();
	}
	public static boolean isDegress90(Integer value) {
		return value != null && (value & DEGREES_90.getValue()) == DEGREES_90.getValue();
	}

	public static boolean isShapeAutoResizeFalse(Integer value) {
		return value != null && (value & DISABLE_SHAPE_AUTO_RESIZE.getValue()) == DISABLE_SHAPE_AUTO_RESIZE.getValue();
	}

	public static boolean isCurvedArrow(Integer value) {
		return value != null && (value & CURVED_ARROW.getValue()) == CURVED_ARROW.getValue();
	}

	public static boolean isClosestPath(Integer value) {
		return value != null && (value & CLOSEST_PATH.getValue()) == CLOSEST_PATH.getValue();
	}

	public static boolean isCenterPath(Integer value) {
		return value != null && (value & CENTERED_PATH.getValue()) == CENTERED_PATH.getValue();
	}

	public static boolean isNoTextAutoAlign(Integer value) {
		if (value == null) {
			// nothing is set and text auto align is on
			return false;
		}
		return value != null && (value & NO_TEXT_AUTO_ALIGN.getValue()) == NO_TEXT_AUTO_ALIGN.getValue();
	}

	public static boolean boldTitle(Integer value) {
		return value != null && (value & BOLD_TITLE.getValue()) == BOLD_TITLE.getValue();	
	}

	public static boolean isCenteredText(Integer value) {
		return value != null && (value & CENTERED_TEXT.getValue()) == CENTERED_TEXT.getValue();	
	}

	public static Integer clear(Integer current, Integer toclear) {
		if (current != null) {
	    return current & ~toclear;
		}
		return current;
	}

}