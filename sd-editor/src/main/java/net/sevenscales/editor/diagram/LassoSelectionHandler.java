package net.sevenscales.editor.diagram;

import java.util.List;
import java.util.Set;

import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.domain.ElementType;
import net.sevenscales.domain.js.JsSlideData;
import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.LibraryShapes;
import net.sevenscales.editor.api.Tools;
import net.sevenscales.editor.api.event.SelectionMouseUpEvent;
import net.sevenscales.editor.api.event.StartSelectToolEvent;
import net.sevenscales.editor.api.event.StartSelectToolEventHandler;
import net.sevenscales.editor.content.utils.ScaleHelpers;
import net.sevenscales.editor.content.utils.ShapeParser;
import net.sevenscales.editor.diagram.shape.GenericShape;
import net.sevenscales.editor.diagram.utils.GridUtils;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.IGraphics;
import net.sevenscales.editor.gfx.domain.IGroup;
import net.sevenscales.editor.gfx.domain.IRectangle;
import net.sevenscales.editor.gfx.domain.IShapeFactory;
import net.sevenscales.editor.gfx.domain.MatrixPointJS;
import net.sevenscales.editor.uicomponents.CircleElement;



public class LassoSelectionHandler implements MouseDiagramHandler {
	private static final SLogger logger = SLogger.createLogger(LassoSelectionHandler.class);

  private GridUtils gridUtils;
  private int downX;
  private int downY;
  private Diagram currentSender;
  private boolean mouseDown = false;
  private ISurfaceHandler surface;
  private IRectangle lassoRectangle;
	private IGroup group;
	private boolean isLassoing;
	private DimensionContext dimensionContext;
	private MouseState mouseState;
	private static final Color HIGHLIGHT_COLOR = new Color(0x6B, 0x66, 0x54, 0.8);

	private class DimensionContext {
		int x;
		int y;
		int width;
		int height;
		private MatrixPointJS scaledPoint;
		private MatrixPointJS scaledDimension;
		
		int scaledX() {
			return scaledPoint.getX();
		}
		
		int scaledY() {
			return scaledPoint.getY();
		}
		
		int scaledWidth() {
			return scaledDimension.getX();
		}
		
		int scaledHeight() {
			return scaledDimension.getY();
		}

		void makeDimension(MatrixPointJS point) {
			int width = downX - point.getScreenX();
			int height = downY - point.getScreenY();
	  	
	  	int x = point.getScreenX();
	  	int y = point.getScreenY();
	  	if (width < 0) {
	  		width = point.getScreenX() - downX; 
	  		x = downX;
	  	}
	  	if (height < 0) {
	  		height = point.getScreenY() - downY; 
	  		y = downY;
	  	}
	  	
	  	dimensionContext.x = x;
	  	dimensionContext.y = y;
	  	dimensionContext.width = width;
	  	dimensionContext.height = height;
	  	
	  	// rect drawn with screen coordinates => scale coordinates to select correct diagram items
	  	dimensionContext.scaledPoint = MatrixPointJS.createScaledPoint(x, y, surface.getScaleFactor());
	  	dimensionContext.scaledDimension = MatrixPointJS.createScaledPoint(width, height, surface.getScaleFactor());
		}
	}

  public LassoSelectionHandler(ISurfaceHandler surface, MouseState mouseState) {
    this.surface = surface;
    this.mouseState = mouseState;
    
	  gridUtils = new GridUtils();
    
		surface.getEditorContext().getEventBus().addHandler(StartSelectToolEvent.TYPE, new StartSelectToolEventHandler() {
			@Override
			public void on(StartSelectToolEvent event) {
				select(false);
			}
		});

		init(this);
		
		dimensionContext = new DimensionContext();
  }

  private native void init(LassoSelectionHandler me)/*-{
  	$wnd.globalStreams.contextMenuStream.filter(function(e) {
  		return e && e.type === 'select'
  	}).onValue(function(e) {
  		me.@net.sevenscales.editor.diagram.LassoSelectionHandler::select(Z)(true)
  	})

  	$wnd.globalStreams.contextMenuStream.filter(function(e) {
  		return e && e.type === 'select-button'
  	}).onValue(function(e) {
  		me.@net.sevenscales.editor.diagram.LassoSelectionHandler::select(Z)(false)
  	})
  }-*/;

  private void select(boolean immediately) {
  	if (surface.isLibrary()) {
  		return;
  	}
  	
		surface.getEditorContext().set(EditorProperty.START_SELECTION_TOOL, true);
		if (immediately) {
			mouseDown = enableLassoMouseDown(0);
		}

		if (mouseDown) {
			highlightShapeDimensions();
		}
  }

  public boolean onMouseDown(Diagram sender, MatrixPointJS point, int keys) {
  	if (surface.isLibrary()) {
  		return false;
  	}

    if (!surface.isDragEnabled() || mouseState.isResizing()) {
      return false;
    }
    this.currentSender = sender;
    
    // if shift if pressed then background moving is disabled
    // shift is reserved for lassoing multiple elements
    mouseDown = enableLassoMouseDown(keys);

    downX = point.getScreenX();
    downY = point.getScreenY();
//    System.out.println("onMouseDown: x("+x+") y("+y+") prevX("+prevX+") prevY("+prevY+")");
    gridUtils.init(point.getX(), point.getY(), surface.getScaleFactor());
    return false;
  }

  private boolean enableLassoMouseDown(int keys) {
  	if (surface.isLibrary()) {
  		return false;
  	}

  	if (surface.getEditorContext().isTrue(EditorProperty.START_SELECTION_TOOL)) {
  		return true;
  	}
  	if (GlobalState.isAddSlideMode()) {
  		return true;
  	}
  	return keys == IGraphics.SHIFT ? true : false;
	}

	public void onMouseEnter(Diagram sender, MatrixPointJS point) {
//    mouseDown = false;
//    backgroundMouseDown = true;
  }

  public void onMouseLeave(Diagram sender, MatrixPointJS point) {
//    mouseDown = false;
//    backgroundMouseDown = true;
  }

  public void onMouseMove(Diagram sender, MatrixPointJS point) {
    if (!surface.isDragEnabled()) {
      return;
    }
    
    
    if (lassoIsOn(point)) {
	    if (!isLassoing) {
	  		// start lassoing
	  		highlightShapeDimensions();
	    }

    	// start lassoing
    	isLassoing = true;
    }
    
    if (isLassoing) {
      // background mouse move
//      int dx = gridUtils.diffX(x, prevX);
//      int dy = gridUtils.diffY(y, prevY);
//      System.out.println("dx("+dx+") dy("+dy+") x("+x+") y("+y+") prevX("+prevX+") prevY("+prevY+")");
//      prevX = x;
//      prevY = y;

      startToDrawLasso(point);
//      moveAll(dx, dy);
    }
  }

  private void highlightShapeDimensions() {
		for (Diagram d : surface.getDiagrams()) {
			d.setHighlightBackgroundBorder(HIGHLIGHT_COLOR);
		}
		// Scheduler.get().scheduleFixedDelay(repeating, 300);
  }

  public boolean isLassoOn() {
  	return mouseDown;
  }

  private boolean lassoIsOn(MatrixPointJS point) {
  	return mouseDown && gridUtils.passTreshold(point);
  }

	private void startToDrawLasso(MatrixPointJS point) {
  	if (lassoRectangle == null) {
  		// lazy intialization
      group = IShapeFactory.Util.factory(true).createGroup(surface.getSurface());
      lassoRectangle = IShapeFactory.Util.factory(true).createRectangle(group);
      lassoRectangle.setStroke(new Color(0xf0, 0xf0, 0xf0, 1));
      lassoRectangle.setFill(0, 0, 0, 0.1);
      lassoRectangle.setVisibility(false);
  	}
//		lassoRectangle.moveToFront();
		
  	dimensionContext.makeDimension(point);
  	
		syncdraw(dimensionContext.x, dimensionContext.y, dimensionContext.width, dimensionContext.height, 
						 dimensionContext.scaledX(), dimensionContext.scaledY(), 
						 dimensionContext.scaledWidth(), dimensionContext.scaledHeight());
	}

	private void syncdraw(int screenX, int screenY, int screenWidth, int screenHeight, int x, int y, int width, int height) {
		lassoRectangle.setVisibility(true);
    lassoRectangle.setShape(screenX, screenY, screenWidth, screenHeight, 0);
//    selectItems(x, y, width, height);
	}

	private void selectItems(MatrixPointJS point) {
  	dimensionContext.makeDimension(point);
		selectItems(dimensionContext.scaledX(), dimensionContext.scaledY(), 
				 				dimensionContext.scaledWidth(), dimensionContext.scaledHeight());
	}

	private void selectItems(int x, int y, int width, int height) {
		x = x - ScaleHelpers.scaleValue(surface.getRootLayer().getTransformX(), surface.getScaleFactor());
		y = y - ScaleHelpers.scaleValue(surface.getRootLayer().getTransformY(), surface.getScaleFactor());

		for (Diagram d : surface.getDiagrams()) {
			if (d.onArea(x, y, x + width, y + height)) {
//				d.select();
				if (!d.isSelected() && Tools.filterDiagramByCurrentTool(d)) {
					// guarantee to keep last selected item as correct
					surface.getSelectionHandler().selectInMultimode(d);
				} else {
					d.unselect();
				}
			} else {
//				d.unselect();

				// ST 30.10.2018: comment out
				// relationship handle helpers hidden circles gets selected on lasso 
				// selection if parent has been selected before
			  // if ((d instanceof CircleElement) /*&& d.isSelected()*/) {
			  //   // do not select circle elements that are just handles
			  //   surface.getSelectionHandler().unselect(d);
			  // }
			}
		}
	}

	@Override
	public void onMouseUp(Diagram sender, MatrixPointJS point, int keys) {
		// logger.debug("onMouseUp isLassoing={}...", isLassoing);
		if (isLassoing && !GlobalState.isAddSlideMode()) {
			selectItems(point);
			
			surface.getEditorContext().set(EditorProperty.START_SELECTION_TOOL, false);
			
			if (surface.getSelectionHandler().getLastMultimodeSelectedDiagram() != null) {
				Set<Diagram> selected = surface.getSelectionHandler().getSelectedItems();
				Diagram[] diagrams = new Diagram[1];
				diagrams = selected.toArray(diagrams);
				surface.getEditorContext().getEventBus().fireEvent(
						new SelectionMouseUpEvent(diagrams, surface.getSelectionHandler().getLastMultimodeSelectedDiagram()));
				surface.getSelectionHandler().setLastMultimodeSelectedDiagram(null);
			}
			
	    if (!surface.isDragEnabled()) {
	      return;
	    }
		}
    
//    if (currentSender == null && mouseDown && backgroundMouseDown && gridUtils.pass(point)) {
//      for (Diagram d : diagrams) {
//        d.saveLastTransform(point.getDX(), point.getDY());
//      }
//    }

		if (GlobalState.isAddSlideMode()) {
			createSlide();
		}

		if (isLassoing) {
			hide();
		}

		clearHighlight();

		GlobalState.disableAddSlideMode();
		isLassoing = false;
	  currentSender = null;
	  mouseDown = false;
//    System.out.println("onMouseUp:" + backgroundMouseDown);
  }

  private void clearHighlight() {
		for (Diagram d : surface.getDiagrams()) {
			if (!d.isSelected()) {
				d.clearHighlightBackgroundBorder();
			}
		}
	}

  private void createSlide() {
  	// _createSlide();
  	if (GlobalState.isAddSlideMode() && lassoRectangle != null && lassoRectangle.getWidth() /surface.getScaleFactor() > 100 && lassoRectangle.getHeight() / surface.getScaleFactor()> 100 ) {
	    DiagramItemDTO item = LibraryShapes.createByType(ElementType.SLIDE.getValue());
	    item.setText("");
	    List<Diagram> found = surface.createDiagramSearch().findAllByType(ElementType.SLIDE.getValue());
	    item.setData(JsSlideData.newSlideData(found.size()+1));
	    Integer properties = null;

			ScaleHelpers.ScaledAndTranslatedPoint stp = ScaleHelpers.scaleAndTranslateScreenpoint
					(lassoRectangle.getX(), lassoRectangle.getY(), surface);

			String shape = new GenericShape(ElementType.SLIDE.getValue(), stp.scaledAndTranslatedPoint.x, stp.scaledAndTranslatedPoint.y, (int) (lassoRectangle.getWidth() / surface.getScaleFactor()), (int) (lassoRectangle.getHeight() / surface.getScaleFactor()), properties, null).toString();

			item.setShape(shape);
			Diagram d = ShapeParser.createDiagramElement(item, surface);
			surface.addAsSelected(d, true);
			surface.moveSelectedToBack();
  	}
  }

  // private native void _createSlide()/*-{
  // 	$wnd.saveBoardShape({
  // 		et: 'o_slide'
  // 		st: 0
  // 	})
  // }-*/;

	private void hide() {
		if (lassoRectangle != null) {
			logger.debug("hide...");
			lassoRectangle.setVisibility(false);
			lassoRectangle.setShape(0, 0, 1, 1, 0);
			lassoRectangle.moveToBack();
		}
	}

	@Override
	public void onTouchStart(Diagram sender, MatrixPointJS point) {
	}
	
  @Override
  public void onTouchMove(Diagram sender, MatrixPointJS point) {
  }
  @Override
  public void onTouchEnd(Diagram sender, MatrixPointJS point) {
  	// TODO Auto-generated method stub
  }
  
  public boolean isLassoing() {
		return isLassoing;
	}
  
}
