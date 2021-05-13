package net.sevenscales.editor.uicomponents.helpers;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.DiagramDragHandler;
import net.sevenscales.editor.gfx.base.GraphicsEvent;
import net.sevenscales.editor.gfx.base.GraphicsMouseDownHandler;
import net.sevenscales.editor.gfx.base.GraphicsMouseEnterHandler;
import net.sevenscales.editor.gfx.base.GraphicsMouseLeaveHandler;
import net.sevenscales.editor.gfx.base.GraphicsMouseUpHandler;
import net.sevenscales.editor.gfx.base.GraphicsTouchEndHandler;
import net.sevenscales.editor.gfx.base.GraphicsTouchStartHandler;
import net.sevenscales.editor.gfx.domain.ICircle;
import net.sevenscales.editor.gfx.domain.ILine;
import net.sevenscales.editor.gfx.domain.IShapeFactory;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.uicomponents.AbstractDiagramItem;
import net.sevenscales.editor.api.event.UndoEvent;
import net.sevenscales.editor.api.event.UndoEventHandler;


public class ResizeHelpers implements GraphicsMouseDownHandler, GraphicsMouseUpHandler, IGlobalElement {
	private static final SLogger logger = SLogger.createLogger(ResizeHelpers.class);
	private static final int THE_SIZE = 18;
	
	private boolean onResizeArea;
	private ICircle resizeElement;
	private ILine line1;
	private ILine line2;
	private ILine line3;
	private ILine line4;
	private ILine line5;
	private ILine line6;
	private AbstractDiagramItem parent;

	private ISurfaceHandler surface;
	
	private static Map<ISurfaceHandler, ResizeHelpers> instances;
	
	static {
		instances = new HashMap<ISurfaceHandler, ResizeHelpers>();
	}
	
	private static final int cornerwidth = 10;
	
	private static final Color DARK_LINE = new Color(0x22, 0x22, 0x22, 1);
	private static final Color WHITE_LINE = new Color(0xdd, 0xdd, 0xdd, 1);
	
	public static ResizeHelpers createResizeHelpers(ISurfaceHandler surface) {
		if (!surface.getEditorContext().isEditable()) {
			return null;
		}

		ResizeHelpers result = instances.get(surface);
		if (result == null) {
//			if (ISurfaceHandler.DRAWING_AREA.equals(surface.getName())) {
				result = new ResizeHelpers(surface);
//			} else {
//				// dummy implementation is the default for library
//				result = createEmptyConnectionHelpers(); 
//			}
			instances.put(surface, result);
		}
		return result;
	}
	
  public static ResizeHelpers getIfAny(ISurfaceHandler surface) {
    return instances.get(surface);
  }


//	private ResizeHelpers(AbstractDiagramItem parent, List<IShape> shapes) {
//		this(parent, shapes, 0, 0);
//	}
//	private ResizeHelpers(AbstractDiagramItem parent, List<IShape> shapes, int indent) {
//		this(parent, shapes, indent, indent);
//	}
	private ResizeHelpers(ISurfaceHandler surface) {
		this.surface = surface;
		line4 = createLine(WHITE_LINE, 2);
		line5 = createLine(WHITE_LINE, 2);
		line6 = createLine(WHITE_LINE, 2);

		line1 = createLine(DARK_LINE, 1);
		line2 = createLine(DARK_LINE, 1);
		line3 = createLine(DARK_LINE, 1);

		resizeElement = IShapeFactory.Util.factory(true).createCircle(surface.getInteractionLayer());
		resizeElement.setFill(200, 200, 200, 0);
    // >>>>>> DEBUGGING
		// resizeElement.setStroke(200, 200, 200, 1);
    // <<<<<< DEBUGGING
		
		// resize support
		resizeElement.addGraphicsMouseEnterHandler(new GraphicsMouseEnterHandler() {
      public void onMouseEnter(GraphicsEvent event) {
        onResizeArea = true;
      }
    });
		resizeElement.addGraphicsMouseLeaveHandler(new GraphicsMouseLeaveHandler() {
      public void onMouseLeave(GraphicsEvent event) {
        onResizeArea = false;
      }
    });
		
		resizeDragHandling();
		resizeTouchSupport();
		
		resizeElement.addGraphicsMouseDownHandler(this);
    resizeElement.addGraphicsMouseUpHandler(this);

    surface.getEditorContext().getEventBus().addHandler(UndoEvent.TYPE, new UndoEventHandler() {
      public void on(UndoEvent event) {
        hide();
      }
    });

    hide();
    
//    shapes.add(resizeElement);
//    shapes.add(line1);
//    shapes.add(line2);
//    shapes.add(line3);
//    
//    shapes.add(line4);
//    shapes.add(line5);
//    shapes.add(line6);
    
//    setShape(parent.getLeft(), parent.getTop(), parent.getWidth(), parent.getHeight());
		listen(this);
	}

	private native void listen(ResizeHelpers me)/*-{
		$wnd.globalStreams.dataItemDeleteStream.onValue(function(dataItem) {
			me.@net.sevenscales.editor.uicomponents.helpers.ResizeHelpers::onItemRealTimeDelete(Lnet/sevenscales/domain/IDiagramItemRO;)(dataItem)
		})
	}-*/;

	private void onItemRealTimeDelete(IDiagramItemRO item) {
		if (parent != null && parent.getDiagramItem().getClientId().equals(item.getClientId())) {
			parent = null;
			hide();
		}
	}

	private void resizeDragHandling() {
		surface.getMouseDiagramManager().addDragHandler(new DiagramDragHandler() {
			@Override
			public void onDrag(Diagram sender, int dx, int dy) {
				if (sender != null && sender.equals(parent)) {
					setShape(
						parent.getLeft(),
						parent.getTop(),
						parent.getWidth(),
						parent.getHeight(),
						parent.getResizeIndentX(),
						parent.getResizeIndentY(),
						parent.getDiagramItem().getRotateDegrees()
					);
				}
			}
			
			@Override
			public boolean isSelected() {
				return false;
			}
			
			@Override
			public void dragStart(Diagram sender) {
        ResizeHelpers.this.hide();
			}
			
			@Override
			public void dragEnd(Diagram sender) {
			}
		});
	}

	private void resizeTouchSupport() {
		resizeElement.addGraphicsTouchStartHandler(new GraphicsTouchStartHandler() {
			@Override
			public void onTouchStart(GraphicsEvent event) {
        onResizeArea = true;
				if (parent != null) {
					parent.onTouchStart(event);
				}
			}
		});
		
		resizeElement.addGraphicsTouchEndHandler(new GraphicsTouchEndHandler() {
			@Override
			public void onTouchEnd(GraphicsEvent event) {
				onResizeArea = false;
				if (parent != null) {
					parent.onTouchEnd(event);
				}
			}
		});
	}
	
	private ILine createLine(Color strokeColor, double strokeWidth) {
		ILine line = IShapeFactory.Util.factory(true).createLine(surface.getInteractionLayer());
		line.setStyle(ILine.SOLID);
		line.setStrokeWidth(strokeWidth);
//		String strokeColor = ColorHelpers.createOppositeColor(parent.getBackgroundColor());
		line.setStroke(strokeColor);
		line.setVisibility(false);
		return line;
	}
	
	public boolean isOnResizeArea() {
		return onResizeArea;
	}

	public void setShape() {
		if (parent != null) {
			setShape(parent);
		}
	}

	private void setShape(AbstractDiagramItem parent) {
		setShape(parent.getLeft(), parent.getTop(), parent.getWidth(), parent.getHeight(), parent.getResizeIndentX(), parent.getResizeIndentY(), parent.getDiagramItem().getRotateDegrees());
	}

	private void setShape(int left, int top, int width, int height, int indentX, int indentY, Integer rotateDegrees) {

		rotateDegrees = rotateDegrees != null ? rotateDegrees : 0;

		double cx = left + width / 2;
		double cy = top + height / 2;

		double circleX = left + width - (cornerwidth - 9) - indentX + (0);
		double circleY = top + height - indentY + (0);

		com.google.gwt.touch.client.Point leftTop = net.sevenscales.editor.uicomponents.AnchorUtils.rotatePoint(
			circleX, 
			circleY,
			cx, 
			cy,
			rotateDegrees
		);

		circleX = ((int) leftTop.getX());
		circleY = ((int) leftTop.getY());

		Line lineRot1 = rotateLine(
			left + width - cornerwidth - indentX,
			top + height - indentY,
			left + width - indentX,
			top + height - cornerwidth - indentY,
			cx, 
			cy,
			rotateDegrees
		);

		Line lineRot2 = rotateLine(
			left + width - (cornerwidth - 4) - indentX,
			top + height - indentY,
			left + width - indentX,
			top + height - (cornerwidth - 4) - indentY,
			cx, 
			cy,
			rotateDegrees
		);

		Line lineRot3 = rotateLine(
			left + width - (cornerwidth - 8) - indentX,
			top + height - indentY,
			left + width - indentX,
			top + height - (cornerwidth - 8) - indentY,
			cx, 
			cy,
			rotateDegrees
		);

		Line lineRot4 = rotateLine(
			left + width - (cornerwidth - 1) - indentX,
			top + height - indentY,
			left + width - indentX,
			top + height - (cornerwidth - 1) - indentY,
			cx, 
			cy,
			rotateDegrees
		);

		Line lineRot5 = rotateLine(
			left + width - (cornerwidth - 5) - indentX,
			top + height - indentY,
			left + width - indentX,
			top + height - (cornerwidth - 5) - indentY,
			cx, 
			cy,
			rotateDegrees
		);

		Line lineRot6 = rotateLine(
			left + width - (cornerwidth - 9) - indentX,
			top + height - indentY,
			left + width - indentX,
			top + height - (cornerwidth - 9) - indentY,
			cx, 
			cy,
			rotateDegrees
		);

//    resizeElement.setShape(left + width - THE_SIZE, top + height - THE_SIZE, THE_SIZE, THE_SIZE, 0);
		resizeElement.setShape(circleX, circleY, THE_SIZE);
		
    line1.setShape(lineRot1.startX, lineRot1.startY, lineRot1.endX, lineRot1.endY);
    line2.setShape(lineRot2.startX, lineRot2.startY, lineRot2.endX, lineRot2.endY);
    line3.setShape(lineRot3.startX, lineRot3.startY, lineRot3.endX, lineRot3.endY);

    line4.setShape(lineRot4.startX, lineRot4.startY, lineRot4.endX, lineRot4.endY);
    line5.setShape(lineRot5.startX, lineRot5.startY, lineRot5.endX, lineRot5.endY);
    line6.setShape(lineRot6.startX, lineRot6.startY, lineRot6.endX, lineRot6.endY);
	}

	class Line {
		int startX;
		int startY;
		int endX;
		int endY;

		Line(
			int startX,
			int startY,
			int endX,
			int endY
		) {
			this.startX = startX;
			this.startY = startY;
			this.endX = endX;
			this.endY = endY;
		}
	}

	private Line rotateLine(
		int startX,
		int startY,
		int endX,
		int endY,
		double cx,
		double cy,
		Integer rotateDegrees
	) {
		com.google.gwt.touch.client.Point line1Start = net.sevenscales.editor.uicomponents.AnchorUtils.rotatePoint(
			startX, 
			startY,
			cx, 
			cy,
			rotateDegrees
		);

		com.google.gwt.touch.client.Point line1End = net.sevenscales.editor.uicomponents.AnchorUtils.rotatePoint(
			endX, 
			endY,
			cx, 
			cy,
			rotateDegrees
		);

		startX = ((int) line1Start.getX());
		startY = ((int) line1Start.getY());
		endX = ((int) line1End.getX());
		endY = ((int) line1End.getY());

		return new Line(
			startX,
			startY,
			endX,
			endY
		);
	}

	private void setVisible(boolean value) {
		resizeElement.setVisibility(value);	
		line1.setVisibility(value);
		line2.setVisibility(value);
		line3.setVisibility(value);
		
		line4.setVisibility(value);
		line5.setVisibility(value);
		line6.setVisibility(value);
	}
	public void show(final AbstractDiagramItem parent) {
    Scheduler.get().scheduleDeferred(new ScheduledCommand() {

      @Override
      public void execute() {
        if (surface.getSelectionHandler().getSelectedItems().size() != 1) {
          return;
        }
    
        ResizeHelpers.this.parent = parent;
        setShape(parent);
        setVisible(true);
      }

    });
	}
	
	public void hide(AbstractDiagramItem candidate) {
		ElementHelpers.hide(this, candidate);
	}

	private void hide() {
		setVisible(false);
		parent = null;
	}

	@Override
	public void onMouseUp(GraphicsEvent event, int keys) {
		if (parent != null) {
			parent.onMouseUp(event, keys);
		}
	}

	@Override
	public void onMouseDown(GraphicsEvent event, int keys) {
		if (parent != null) {
			parent.onMouseDown(event, keys);
		}
	}

	@Override
	public AbstractDiagramItem getParent() {
		return parent;
	}

	@Override
	public void hideGlobalElement() {
		hide();
	}

  @Override
  public void release() {
    resizeElement.remove(); 
    line1.remove();
    line2.remove();
    line3.remove();
    
    line4.remove();
    line5.remove();
    line6.remove();
    
    resizeElement = null; 
    line1 = null;
    line2 = null;
    line3 = null;
    
    line4 = null;
    line5 = null;
    line6 = null;
    
    instances.clear();
  }

}
