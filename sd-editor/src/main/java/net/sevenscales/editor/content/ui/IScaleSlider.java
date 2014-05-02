package net.sevenscales.editor.content.ui;

interface IScaleSlider {
	static final int MAX_INDEX = 11;
	static final int DEFAULT_INDEX = 7;

	int getSliderValue();
	void scaleToIndex(int index);
	void scale(int index);
}