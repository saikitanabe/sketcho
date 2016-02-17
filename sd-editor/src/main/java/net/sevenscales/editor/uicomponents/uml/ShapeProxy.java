package net.sevenscales.editor.uicomponents.uml;

import com.google.gwt.core.client.JsArray;

import net.sevenscales.domain.js.JsShape;

class ShapeProxy implements IShapeGroup {
	private String elementType;
	private int shapeType;
	private ShapeGroup shape;
	private ShapeLoaded listener;

	ShapeProxy(String elementType, int shapeType) {
		this.elementType = elementType;
	}

	void setShape(ShapeGroup shape) {
		this.shape = shape;
	}

	@Override
	public void fetch(ShapeLoaded listener) {
		this.listener = listener;
		if (isReady()) {
			listener.onSuccess();
		} else {
			_fetch(elementType, shapeType, this);
		}
	}
	@Override
	public boolean isReady() {
		return shape != null;
	}
	@Override
	public ShapeGroup getShape() {
		return shape;
	}

	private native void _fetch(String elementType, int shapeType, ShapeProxy me)/*-{
		var boardId = $wnd.currentBoard().boardId
		$wnd.libraryService.loadBoardShape({
			board_id: boardId,
			element_type: elementType,
			shape_type: shapeType
		}).then(function(response) {
			if (response.ok && response.shapes.length > 0) {
				me.@net.sevenscales.editor.uicomponents.uml.ShapeProxy::loaded(Lcom/google/gwt/core/client/JsArray;)(response.shapes)
			} else {
				console.error("_fetch failed for...", elementType, shapeType)
			}
		})
	}-*/;

	private void loaded(JsArray<JsShape> shapes) {
		ShapeCache.updateShapes(shapes);
		boolean sketch = shapeType == 0;
		shape = ShapeCache.get(elementType, sketch).getShape();
		listener.onSuccess();
	}

}