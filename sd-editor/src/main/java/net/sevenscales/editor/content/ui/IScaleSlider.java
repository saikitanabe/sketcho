package net.sevenscales.editor.content.ui;

public interface IScaleSlider {
	int getSliderValue();
	void scaleToIndex(int index, boolean wheel);
	void scale(int index);
}