package net.sevenscales.editor.uicomponents.uml;

import net.sevenscales.domain.js.JsShapeConfig;

public interface IShapeGroup {
	public interface ShapeLoaded {
		void onSuccess();
		void onError();
	}
	void fetch(ShapeLoaded listener);
	boolean isReady();
	ShapeGroup getShape();
}