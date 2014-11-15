package net.sevenscales.editor.gfx.dojosvg;

import net.sevenscales.editor.gfx.base.GraphicsEvent;
import net.sevenscales.editor.gfx.domain.IContainer;
import net.sevenscales.editor.gfx.domain.IKeyEventHandler;
import net.sevenscales.editor.gfx.domain.ILoadObserver;
import net.sevenscales.editor.gfx.domain.ISurface;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.UIObject;

class Surface extends Graphics implements IContainer, ISurface {
	protected UIObject uiObject;
	protected JavaScriptObject surface;
//	private Rectangle canvas;
	private ILoadObserver loadObserver;
	private int width;
	private int height;
	private IKeyEventHandler handler;

	Surface() {
		GraphicsEvent.type = false;
	}

	public void init(UIObject uiObject, ILoadObserver loadObserver) {
		this.loadObserver = loadObserver;
		this.uiObject = uiObject;
		surface = createSurface(uiObject.getElement(), uiObject.getOffsetWidth(),
				uiObject.getOffsetHeight());
	}

	public void load() {
		// if (isLoaded()) {
		nativeConnect(surface, "onLoad");
		// } else {
		// // syncronous start
		// System.out.println("SYNCHORONOUS LOAD!!");
		// loaded();
		// }
		// nativeSetSize(surface, width, height);
	}

	protected native JavaScriptObject createSurface(Element element, int width,
			int height)/*-{
		return $wnd.dojox.gfx.createSurface(element, width, height);
	}-*/;

	public JavaScriptObject getContainer() {
		return surface;
	}

	public void loaded() {
		this.width = uiObject.getOffsetWidth();
		this.height = uiObject.getOffsetHeight();

		// set size; at least with safari these are 0 on reload
		// need to set size here, because on javascript load uiObject width and
		// height
		// are zero if using e.g. deck panel => other panel is not loaded and size
		// is 0, 0
		nativeSetSize(surface, width, height);

		// to support canvas events outside diagram items on safari
//		canvas = new Rectangle(this);
//		canvas
//				.setShape(0, 0, uiObject.getOffsetWidth(), uiObject.getOffsetHeight(), 0);
//		canvas.setFill(100, 100, 100, 0.5);
		// rawNode = canvas.getRawNode();
		// rawNode = nativeGetEventSource(surface);

//		nativeEnableEvents(surface);
		rawNode = surface;
		loadObserver.loaded();
	}

	private native JavaScriptObject nativeGetEventSource(JavaScriptObject object)/*-{
		return object.getEventSource();
	}-*/;

	public void setKeyEventHandler(IKeyEventHandler handler) {
		this.handler = handler;
	}

	private void handleKeyDown(int keyCode, int platformCode, boolean shift,
			boolean ctrl) {
		handler.handleKeyDown(keyCode, platformCode, shift, ctrl);
	}

	private void handleKeyUp(int keyCode, int platformCode, boolean shift,
			boolean ctrl) {
		handler.handleKeyUp(keyCode, shift, ctrl);
	}

//	private native JavaScriptObject nativeEnableEvents(JavaScriptObject object)/*-{
//		var self = this;
////		object.rawNode.setAttribute("tabindex", "0");
//		$wnd.console.log('enable keydown:' + self);
//		$wnd.dojo.connect(object, "onkeydown", function(s) {
//			$wnd.console.log('keydown:' + s);
//		});
//		
//		object.rawNode.addEventListener('onkeydown', function(evt) {
//							//    evt.clientX = parseInt(evt.clientX.valueOf());
//							//    evt.clientY = parseInt(evt.clientY.valueOf());
//			$wnd.console.log('keydown:' + evt);
//			//    self.@net.sevenscales.editor.gfx.svgweb.Graphics::onMouseUp(Lnet/sevenscales/editor/gfx/base/GraphicsEvent;)(evt);
//			self.@net.sevenscales.editor.gfx.svgweb.Surface::handleKeyDown(IIZZ)(0,0, false, false);
//		});
//						
//		object.connect("onkeypress", function(e) {$wnd.console.log("downer");});
//		
//		$wnd.console.log('enable keydown post:' + self);
//
//		//function keyDown(sender, e) {
//		////  $wnd.debugLog(e.key+" "+e.ctrl);
//		//	self.@net.sevenscales.editor.gfx.dojo.Surface::handleKeyDown(IIZZ)
//		//	  (e.key, e.platformKeyCode, e.shift, e.ctrl);
//		//}
//		//object.getEventSource().addEventListener("KeyDown", keyDown);
//		//
//		//function keyUp(sender, e) {
//		//	self.@net.sevenscales.editor.gfx.dojo.Surface::handleKeyUp(IIZZ)(e.key, e.platformKeyCode, e.shift, e.ctrl);
//		//}
//		//object.getEventSource().addEventListener("KeyUp", keyUp);
//
//		//var p = object.rawNode && object.rawNode.getHost();
//		//brush = p.content.createFromXaml('<SolidColorBrush />');
//		//brush.color = "transparent";
//		//object.rawNode.background = brush;
//	}-*/;

	public void setSize(int width, int height) {
		if (surface != null) {
			nativeSetSize(surface, width, height);
//			canvas.setShape(0, 0, uiObject.getOffsetWidth(),
//					uiObject.getOffsetHeight());
		}
	}

	private native void nativeSetSize(JavaScriptObject surface, int width,
			int height)/*-{
		//  	surface.setDimensions(width, height);
		surface._parent.firstChild.style.width = width + "px";
		surface._parent.firstChild.style.height = height + "px";
	}-*/;

	protected native void nativeConnect(JavaScriptObject surface, String event)/*-{
		var self = this;
		if (surface.isLoaded) {
			if (typeof $wnd.console != "undefined") $wnd.console.log('sync surface onLoad:'+self);
			self.@net.sevenscales.editor.gfx.dojosvg.Surface::loaded()();
		} else {
			if (typeof $wnd.console != "undefined") $wnd.console.log('async follow onLoad:'+this);
			if (typeof $wnd.console != "undefined") $wnd.dojo.connect(surface, "onLoad", function(s) {
			if (typeof $wnd.console != "undefined") $wnd.console.log('surface onLoad:'+self);
			self.@net.sevenscales.editor.gfx.dojosvg.Surface::loaded()();
		});
		}
	}-*/;

	public native boolean isLoaded()/*-{
		return this.@net.sevenscales.editor.gfx.dojosvg.Surface::surface.isLoaded;
	}-*/;

	public void setBackground(String color) {
//		if (canvas != null) {
//			canvas.setFill(color);
//		}
	}

	// @Override
	public void suspendRedraw() {
//		_suspendRedraw(rawNode);
	}

	// @Override
	public void unsuspendRedrawAll() {
	}
	
	@Override
	public void moveToBack() {
	}
	
  @Override
  public void setAttribute(String name, String value) {
  	setAttribute(surface, name, value);
  }
  
  private native void setAttribute(JavaScriptObject rawNode, String name, String value)/*-{
  	rawNode.rawNode.setAttribute(name, value);
  }-*/;

}
