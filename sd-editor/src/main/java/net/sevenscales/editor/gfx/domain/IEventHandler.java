package net.sevenscales.editor.gfx.domain;

import net.sevenscales.editor.gfx.base.GraphicsBase;
import net.sevenscales.editor.gfx.base.GraphicsEvent;

import com.google.gwt.user.client.Event;

public interface IEventHandler {
	void onMouseEnter(IGraphics graphics, Event event);
	void onMouseLeave(IGraphics graphics, Event event);
	void onMouseMove(IGraphics graphics, Event event);
	void onDoubleClick(IGraphics graphics, Event event);
	void onMouseDown(IGraphics graphics, Event event, int keys);
	void onMouseUp(IGraphics graphics, Event event, int keys);
	void onTouchMove(GraphicsBase shape, GraphicsEvent event);
	void onTouchStart(GraphicsBase shape, GraphicsEvent event);
	void onTouchEnd(GraphicsBase shape, GraphicsEvent event);
}
