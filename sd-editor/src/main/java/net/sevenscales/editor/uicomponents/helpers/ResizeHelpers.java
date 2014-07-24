package net.sevenscales.editor.uicomponents.helpers;

import java.util.HashMap;
import java.util.Map;

import net.sevenscales.domain.utils.SLogger;
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
	}
	
	private void resizeDragHandling() {
		surface.getMouseDiagramManager().addDragHandler(new DiagramDragHandler() {
			@Override
			public void onDrag(Diagram sender, int dx, int dy) {
				if (sender != null && sender.equals(parent)) {
					setShape(parent.getLeft(), parent.getTop(), parent.getWidth(), parent.getHeight(),
							parent.getResizeIndentX(), parent.getResizeIndentY());
				}
			}
			
			@Override
			public boolean isSelected() {
				return false;
			}
			
			@Override
			public void dragStart(Diagram sender) {
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
		setShape(parent.getLeft(), parent.getTop(), parent.getWidth(), parent.getHeight(), parent.getResizeIndentX(), parent.getResizeIndentY());
	}

	private void setShape(int left, int top, int width, int height, int indentX, int indentY) {
//    resizeElement.setShape(left + width - THE_SIZE, top + height - THE_SIZE, THE_SIZE, THE_SIZE, 0);
		resizeElement.setShape(left + width - (cornerwidth - 9) - indentX + (0), top + height - indentY + (0), THE_SIZE);
		
    line1.setShape(left + width - cornerwidth - indentX, top + height - indentY, left + width - indentX, top + height - cornerwidth - indentY);
    line2.setShape(left + width - (cornerwidth - 4) - indentX, top + height - indentY, left + width - indentX, top + height - (cornerwidth - 4) - indentY);
    line3.setShape(left + width - (cornerwidth - 8) - indentX, top + height - indentY, left + width - indentX, top + height - (cornerwidth - 8) - indentY);

    line4.setShape(left + width - (cornerwidth - 1) - indentX, top + height - indentY, left + width - indentX, top + height - (cornerwidth - 1) - indentY);
    line5.setShape(left + width - (cornerwidth - 5) - indentX, top + height - indentY, left + width - indentX, top + height - (cornerwidth - 5) - indentY);
    line6.setShape(left + width - (cornerwidth - 9) - indentX, top + height - indentY, left + width - indentX, top + height - (cornerwidth - 9) - indentY);
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
	public void show(AbstractDiagramItem parent) {
		this.parent = parent;
		setShape(parent);
		setVisible(true);
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
