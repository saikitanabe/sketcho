package net.sevenscales.editor.uicomponents.uml;

public interface IShapeGroup {
	public interface ShapeLoaded {
		void onSuccess();
		void onError();
	}
	void fetch(ShapeLoaded listener);
	boolean isReady();
	ShapeGroup getShape();
}