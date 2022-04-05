package net.sevenscales.editor.gfx.dojosvg;

import net.sevenscales.editor.api.event.pointer.Events;
import net.sevenscales.editor.api.event.pointer.PointerEventsSupport;
import net.sevenscales.editor.gfx.base.GraphicsBase;

import com.google.gwt.core.client.JavaScriptObject;

abstract class Graphics extends GraphicsBase {
  
  @Override
  protected void connectMouse(String eventType) {
    if (PointerEventsSupport.isSupported()) {
      supportPointerEvents(eventType);
    } else {
      supportMouseTouchEvents(eventType);
    }
  }

  private void supportPointerEvents(String eventType) {
    if (eventType.equals(Events.PointerDown.getNativeEventName())) {
      nativeConnectOnMouseDown(rawNode, eventType);
    } else if (eventType.equals(Events.PointerUp.getNativeEventName())) {
      nativeConnectOnMouseUp(rawNode, eventType);
    } else if (eventType.equals(Events.PointerMove.getNativeEventName())) {
      nativeConnectMouseMove(rawNode, eventType);
    } else if (eventType.equals(Events.PointerLeave.getNativeEventName())) {
      nativeConnectMouseLeave(rawNode, eventType);
    } else if (eventType.equals(Events.PointerEnter.getNativeEventName())) {
      nativeConnectMouseEnter(rawNode, eventType);
    }
  }

  private void supportMouseTouchEvents(String eventType) {
    if (eventType.equals(ON_MOUSE_LEAVE)) {
      nativeConnectMouseLeave(rawNode, eventType);
    } else if (eventType.equals(ON_MOUSE_ENTER)) {
      nativeConnectMouseEnter(rawNode, eventType);
    } else if (eventType.equals(ON_MOUSE_MOVE)) {
      nativeConnectMouseMove(rawNode, eventType);
    } else if (eventType.equals(ON_DOUBLE_CLICK)) {
      // nativeConnectDoubleClick(rawNode, eventType);
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

      if (e.button == 2) {
        // allow right click to move background
        return
      }

			e.preventDefault();
			
//		for (var k in e) {
//		  @net.sevenscales.domain.utils.Debug::println(Ljava/lang/String;)("hepul:"+k);
//		}
//    @net.sevenscales.domain.utils.Debug::println(Ljava/lang/String;)("shift:"+e.shiftKey);
//		  var keys = Keyboard.Modifiers;
     var keys = e.shiftKey ? @net.sevenscales.editor.gfx.domain.IGraphics::SHIFT : 0;
     keys |= e.altKey ? @net.sevenscales.editor.gfx.domain.IGraphics::ALT : 0;
     keys |= e.ctrlKey ? @net.sevenscales.editor.gfx.domain.IGraphics::CTRL : 0;
     keys |= e.metaKey ? @net.sevenscales.editor.gfx.domain.IGraphics::META : 0;
		 self.@net.sevenscales.editor.gfx.dojosvg.Graphics::onMouseDown(Lnet/sevenscales/editor/gfx/base/GraphicsEvent;I)(e,keys);
		}
		object.connect(eventType, onMouseDown);
	}-*/;
	native void nativeConnectOnMouseUp(JavaScriptObject object, String eventType)/*-{
		var self = this;
		function onMouseUp(e) {
			e.preventDefault();
	    var keys = e.shiftKey ? @net.sevenscales.editor.gfx.domain.IGraphics::SHIFT : 0;
	    keys |= e.altKey ? @net.sevenscales.editor.gfx.domain.IGraphics::ALT : 0;
	    keys |= e.ctrlKey ? @net.sevenscales.editor.gfx.domain.IGraphics::CTRL : 0;
	    keys |= e.metaKey ? @net.sevenscales.editor.gfx.domain.IGraphics::META : 0;
			self.@net.sevenscales.editor.gfx.dojosvg.Graphics::onMouseUp(Lnet/sevenscales/editor/gfx/base/GraphicsEvent;I)(e,keys);
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
