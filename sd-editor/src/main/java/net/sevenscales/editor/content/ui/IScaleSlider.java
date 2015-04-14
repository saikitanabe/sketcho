package net.sevenscales.editor.content.ui;

public interface IScaleSlider {
	int getSliderValue();
	void scaleToIndex(int index);
	void scale(int index);
}