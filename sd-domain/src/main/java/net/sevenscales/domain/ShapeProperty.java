package net.sevenscales.domain;

/**
* Each property has it's own range. 
* Those cannot overlap, so properties can be combined!!
*/
public enum ShapeProperty {
	TEXT_TITLE_CENTER									(0x0000001),
	TEXT_POSITION_TOP 								(0x0000002),
	TEXT_POSITION_RIGHT 							(0x0000004),
	TEXT_POSITION_BOTTOM							(0x0000008),
	TEXT_POSITION_LEFT  							(0x0000010),
	TEXT_POSITION_MIDDLE							(0x0000400),
	TEXT_RESIZE_DIR_HORIZONTAL				(0x0000100),
	TEXT_RESIZE_DIR_VERTICAL					(0x0000200),
	DEGREES_0 												(0x0001000),
	DEGREES_90 												(0x0002000),
	DISABLE_SHAPE_AUTO_RESIZE					(0x0010000),
	CURVED_ARROW										  (0x0100000),
	CLOSEST_PATH										  (0x0200000),
	CENTERED_PATH											(0x0400000),
	NO_TEXT_AUTO_ALIGN								(0x0000020),
	BOLD_TITLE												(0x0000040),
	TXT_ALIGN_CENTER									(0x0000080),
	TXT_ALIGN_LEFT										(0x1000000),
	TXT_ALIGN_RIGHT										(0x2000000);

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

	public static boolean isShapeAutoResizeDisabled(Integer value) {
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

	public static boolean isTextAlignLeft(Integer value) {
		// text alignment is not right or center
		return !(isTextAlignCenter(value) || isTextAlignRight(value));
	}
	public static boolean isTextAlignCenter(Integer value) {
		return value != null && (value & TXT_ALIGN_CENTER.getValue()) == TXT_ALIGN_CENTER.getValue();	
	}
	public static boolean isTextAlignRight(Integer value) {
		return value != null && (value & TXT_ALIGN_RIGHT.getValue()) == TXT_ALIGN_RIGHT.getValue();	
	}

	public static Integer clear(Integer current, Integer toclear) {
		if (current != null) {
	    return current & ~toclear;
		}
		return current;
	}

}