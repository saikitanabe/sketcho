package net.sevenscales.editor.gfx.domain;

import net.sevenscales.editor.gfx.base.GraphicsDoubleClickHandler;
import net.sevenscales.editor.gfx.base.GraphicsEventBase;
import net.sevenscales.editor.gfx.base.GraphicsEventHandler;
import net.sevenscales.editor.gfx.base.GraphicsMouseDownHandler;
import net.sevenscales.editor.gfx.base.GraphicsMouseEnterHandler;
import net.sevenscales.editor.gfx.base.GraphicsMouseLeaveHandler;
import net.sevenscales.editor.gfx.base.GraphicsMouseMoveHandler;
import net.sevenscales.editor.gfx.base.GraphicsMouseUpHandler;
import net.sevenscales.editor.gfx.base.GraphicsTouchEndHandler;
import net.sevenscales.editor.gfx.base.GraphicsTouchMoveHandler;
import net.sevenscales.editor.gfx.base.GraphicsTouchStartHandler;

public interface IGraphics {
  public static final String ON_MOUSE_LEAVE = "onmouseout";
  public static final String ON_MOUSE_ENTER = "onmouseover";
  public static final String ON_MOUSE_MOVE = "onmousemove";
  public static final String ON_DOUBLE_CLICK = "dblclick";
  public static final String ON_MOUSE_DOWN = "mousedown";
  public static final String ON_MOUSE_UP = "onmouseup";
  public static final String ON_TOUCH_START = "ontouchstart";
  public static final String ON_TOUCH_MOVE = "ontouchmove";
  public static final String ON_TOUCH_END = "ontouchend";
  public static final int SHIFT = 0x00000001;

  public <H extends GraphicsEventHandler> void addGraphicsHandler(H handler, GraphicsEventBase.Type<H> type);
  void addGraphicsDoubleClickHandler(GraphicsDoubleClickHandler handler);
  void addGraphicsMouseDownHandler(GraphicsMouseDownHandler handler);
  void addGraphicsMouseUpHandler(GraphicsMouseUpHandler handler);
  void addGraphicsMouseEnterHandler(GraphicsMouseEnterHandler handler);
  void addGraphicsMouseLeaveHandler(GraphicsMouseLeaveHandler handler);
  void addGraphicsMouseMoveHandler(GraphicsMouseMoveHandler handler);
  
	void addGraphicsTouchStartHandler(GraphicsTouchStartHandler handler);
  void addGraphicsTouchMoveHandler(GraphicsTouchMoveHandler handler);
	void addGraphicsTouchEndHandler(GraphicsTouchEndHandler handler);

//  public void addGraphicsKeyDownhandler(GraphicsKeyDownHandler handler);
}