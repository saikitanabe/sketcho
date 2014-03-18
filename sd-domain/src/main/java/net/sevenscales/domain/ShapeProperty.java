package net.sevenscales.domain;

/**
* Each property has it's own range. 
* Those cannot overlap, so properties can be combined!!
*/
public enum ShapeProperty {
	TEXT_POSITION_CENTER							(0x00001),
	TEXT_POSITION_TOP 								(0x00002),
	TEXT_POSITION_RIGHT 							(0x00004),
	TEXT_POSITION_BOTTOM							(0x00008),
	TEXT_POSITION_LEFT  							(0x00010),
	TEXT_RESIZE_DIR_HORIZONTAL				(0x00100),
	TEXT_RESIZE_DIR_VERTICAL					(0x00200),
	DEGREES_0 												(0x01000),
	DEGREES_90 												(0x02000),
	SHAPE_AUTO_RESIZE_TRUE  					(0x10000),
	SHAPE_AUTO_RESIZE_FALSE						(0x20000);

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

}