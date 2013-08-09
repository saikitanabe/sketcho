package net.sevenscales.editor.gfx.dojosvg;

import net.sevenscales.editor.gfx.base.GraphicsBase;

import com.google.gwt.core.client.JavaScriptObject;

abstract class Graphics extends GraphicsBase {
  
  @Override
  protected void connectMouse(String eventType) {
    if (eventType.equals(ON_MOUSE_LEAVE)) {
      nativeConnectMouseLeave(rawNode, eventType);
    } else if (eventType.equals(ON_MOUSE_ENTER)) {
      nativeConnectMouseEnter(rawNode, eventType);
    } else if (eventType.equals(ON_MOUSE_MOVE)) {
      nativeConnectMouseMove(rawNode, eventType);
    } else if (eventType.equals(ON_DOUBLE_CLICK)) {
      nativeConnectDoubleClick(rawNode, eventType);
    } else if (eventType.equals(ON_MOUSE_DOWN)) {
      nativeConnectOnMouseDown(rawNode, eventType);
    } else if (eventType.equals(ON_MOUSE_UP)) {
      nativeConnectOnMouseUp(rawNode, eventType);
    } else if (eventType.equals(ON_TOUCH_MOVE)) {
      nativeConnectTouchMove(rawNode, eventType);
    } else if (eventType.equals(ON_TOUCH_START)) {
      nativeConnectTouchStart(rawNode, eventType);
    } else if (eventType.equals(ON_TOUCH_END)) {
      nativeConnectTouchEnd(rawNode, eventType);
    }
  }
	
	native void nativeConnectMouseLeave(JavaScriptObject object, String eventType)/*-{
		var self = this;
		function onMouseLeave(e) {
			self.@net.sevenscales.editor.gfx.dojosvg.Graphics::onMouseLeave(Lnet/sevenscales/editor/gfx/base/GraphicsEvent;)(e);
		}
//		object.rawNode.addEventListener(onMouseLeave);
		object.connect(eventType, onMouseLeave);
// 		$wnd.dojo.connect(object, eventType, obj, "kala");
	}-*/;
	native void nativeConnectMouseEnter(JavaScriptObject object, String eventType)/*-{
		var self = this;
		function onMouseEnter(e) {
//			e.preventDefault();
			self.@net.sevenscales.editor.gfx.dojosvg.Graphics::onMouseEnter(Lnet/sevenscales/editor/gfx/base/GraphicsEvent;)(e);
		}
		object.connect(eventType, onMouseEnter);
	}-*/;
	native void nativeConnectMouseMove(JavaScriptObject object, String eventType)/*-{
		var self = this;
		function onMouseMove(e) {
			e.preventDefault();
			self.@net.sevenscales.editor.gfx.dojosvg.Graphics::onMouseMove(Lnet/sevenscales/editor/gfx/base/GraphicsEvent;)(e);
		}
		object.connect(eventType, onMouseMove);
	}-*/;
	native void nativeConnectDoubleClick(JavaScriptObject object, String eventType)/*-{
		var self = this;
		function onDoubleClick(e) {
//			e.preventDefault();
			self.@net.sevenscales.editor.gfx.dojosvg.Graphics::onDoubleClick(Lnet/sevenscales/editor/gfx/base/GraphicsEvent;)(e);
		}
		object.connect(eventType, onDoubleClick);
	}-*/;
	native void nativeConnectOnMouseDown(JavaScriptObject object, String eventType)/*-{
		var self = this;
		function onMouseDown(e,a) {
			e.preventDefault();
			
//		for (var k in e) {
//		  @net.sevenscales.domain.utils.Debug::println(Ljava/lang/String;)("hepul:"+k);
//		}
//    @net.sevenscales.domain.utils.Debug::println(Ljava/lang/String;)("shift:"+e.shiftKey);
//		  var keys = Keyboard.Modifiers;
     var keys = e.shiftKey ? @net.sevenscales.editor.gfx.domain.IGraphics::SHIFT : 0;
		 self.@net.sevenscales.editor.gfx.dojosvg.Graphics::onMouseDown(Lnet/sevenscales/editor/gfx/base/GraphicsEvent;I)(e,keys);
		}
		object.connect("onmousedown", onMouseDown);
	}-*/;
	native void nativeConnectOnMouseUp(JavaScriptObject object, String eventType)/*-{
		var self = this;
		function onMouseUp(e) {
//			e.preventDefault();
			self.@net.sevenscales.editor.gfx.dojosvg.Graphics::onMouseUp(Lnet/sevenscales/editor/gfx/base/GraphicsEvent;)(e);
		}
		object.connect(eventType, onMouseUp);
	}-*/;
	
	native void nativeConnectTouchMove(JavaScriptObject object, String eventType)/*-{
		var self = this;
		function onTouchMove(e) {
			e.preventDefault();
			self.@net.sevenscales.editor.gfx.dojosvg.Graphics::onTouchMove(Lnet/sevenscales/editor/gfx/base/GraphicsEvent;)(e);
		}
		object.connect(eventType, onTouchMove);
	}-*/;

	native void nativeConnectTouchStart(JavaScriptObject object, String eventType)/*-{
		var self = this;
		function onTouchStart(e) {
			e.preventDefault();
			self.@net.sevenscales.editor.gfx.dojosvg.Graphics::onTouchStart(Lnet/sevenscales/editor/gfx/base/GraphicsEvent;)(e);
		}
		object.connect(eventType, onTouchStart);
	}-*/;
	
	native void nativeConnectTouchEnd(JavaScriptObject object, String eventType)/*-{
		var self = this;
		function onTouchEnd(e) {
			e.preventDefault();
			self.@net.sevenscales.editor.gfx.dojosvg.Graphics::onTouchEnd(Lnet/sevenscales/editor/gfx/base/GraphicsEvent;)(e);
		}
		object.connect(eventType, onTouchEnd);
}-*/;



}
