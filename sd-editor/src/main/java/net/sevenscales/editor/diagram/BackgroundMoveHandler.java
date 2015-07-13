package net.sevenscales.editor.diagram;

import java.util.List;

import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.event.PinchZoomStartedEvent;
import net.sevenscales.editor.api.event.PinchZoomStartedEventHandler;
import net.sevenscales.editor.api.event.BackgroundMoveStartedEvent;
import net.sevenscales.editor.diagram.utils.GridUtils;
import net.sevenscales.editor.gfx.domain.IGraphics;
import net.sevenscales.editor.gfx.domain.MatrixPointJS;
import com.google.gwt.core.client.JavaScriptObject;


public class BackgroundMoveHandler implements MouseDiagramHandler {
	private static SLogger logger = SLogger.createLogger(BackgroundMoveHandler.class);

  private List<Diagram> diagrams;
  private GridUtils gridUtils = new GridUtils();
  private int prevX;
  private int prevY;
  private boolean backgroundMouseDown = true;
  private Diagram currentSender;
  private boolean mouseDown = false;
  private ISurfaceHandler surface;
	private int prevTransformDX;
	private int prevTransformDY;
  private boolean backgroundMoving;
  private JavaScriptObject cachedEditor;
	
//	private DiagramHelpers.ComplexElementHandler complexElementHandler = new DiagramHelpers.ComplexElementHandler();

  public BackgroundMoveHandler(List<Diagram> diagrams, ISurfaceHandler surface) {
    this.diagrams = diagrams;
    this.surface = surface;
    
    listenPinchZoom();
    init(this);
  }

  private native void init(BackgroundMoveHandler me)/*-{
    $wnd.globalStreams.contextMenuStream.filter(function(v) {
      return v && v.type==='context-menu-open'
    }).onValue(function() {
      me.@net.sevenscales.editor.diagram.BackgroundMoveHandler::clear()()
    })
  }-*/;

  private void listenPinchZoom() {
  	surface.getEditorContext().getEventBus().addHandler(PinchZoomStartedEvent.TYPE, new PinchZoomStartedEventHandler() {
			@Override
			public void on(PinchZoomStartedEvent event) {
		  	// cancel background move to prevent side effects, e.g. pinch zoom
		  	// 1. one finger on board => background move started
		  	// 2. two finer on board => background move should be canceled
		  	// 3. if not canceled will move background based on first finger if released appropriately
		  	mouseDown = false;
		  	backgroundMouseDown = false;
			}
		});
	}

	public boolean onMouseDown(Diagram sender, MatrixPointJS point, int keys) {
  	if (!surface.isDragEnabled()) {
  		return false;
  	}
  	
  	if (surface.getEditorContext().isTrue(EditorProperty.START_SELECTION_TOOL)) {
  		return false;
  	}
  	
//    if (!surface.isDragEnabled()) {
//      return;
//    }
    this.currentSender = sender;
    if (backgroundMouseDown) {
      // by default it is assumed to be true and changed only if sender is real diagram element
      this.backgroundMouseDown = sender != null ? false : true;
//      System.out.println("backgroundMouseDown:" + backgroundMouseDown);
    }
    
    // if shift if pressed then background moving is disabled
    // shift is reserved for lassoing multiple elements
    mouseDown = keys == IGraphics.SHIFT ? false : true;
    if (mouseDown) {
      // disable background move if slide mode
      mouseDown = !GlobalState.isAddSlideMode();
    }
    if (!surface.isVerticalDrag()) {
    	prevX = point.getScreenX();
    }
    prevY = point.getScreenY();
//    System.out.println("onMouseDown: x("+x+") y("+y+") prevX("+prevX+") prevY("+prevY+")");
    gridUtils.init(point.getScreenX(), point.getScreenY(), surface.getScaleFactor());
    
    prevTransformDX = surface.getRootLayer().getTransformX();
    prevTransformDY = surface.getRootLayer().getTransformY();
//    complexElementHandler.reset();
    return false;
  }

  public void onMouseEnter(Diagram sender, MatrixPointJS point) {
//	    mouseDown = false;
//	    backgroundMouseDown = true;
  }

  public void onMouseLeave(Diagram sender, MatrixPointJS point) {
  		// reset only when going outside of drawing area or elements
//	    mouseDown = false;
//	    backgroundMouseDown = true;
  }

  public void onMouseMove(Diagram sender, MatrixPointJS point) {
    move(point);
  }

  private void move(MatrixPointJS point) {
    if (!gridUtils.passTreshold(point)) {
      return;
    }
    
    if (backgroundMoving || backgroundMoveInitialContitionOk()) {
      if (!backgroundMoving) {
        startBackgroundMove();
      }
      backgroundMoving = true;
      int dx = gridUtils.dx(point.getScreenX()) + prevTransformDX;
      int dy = gridUtils.dy(point.getScreenY()) + prevTransformDY;

      if (surface.isVerticalDrag()) {
        dx = 0;
      }
      
      surface.getRootLayer().setTransform(dx, dy);
      _notifyBackgroundMoveStream("move");
      if (cachedEditor == null) {
        cachedEditor = getEditor();
      }
      moveBgImage(cachedEditor, dx, dy);
    }
  }

  private native JavaScriptObject getEditor()/*-{
    return $wnd.$('#sketchboard-editor')
  }-*/;

  private native void moveBgImage(JavaScriptObject editor, int dx, int dy)/*-{
    var pos = dx + "px " + dy + "px"
    editor.css("background-position", pos)
  }-*/;

  private void startBackgroundMove() {
    surface.getEditorContext().getEventBus().fireEvent(new BackgroundMoveStartedEvent());
    _notifyBackgroundMoveStream("start");
  }
  private native void _notifyBackgroundMoveStream(String type)/*-{
    $wnd.globalStreams.backgroundMoveStream.push({
      type:type
    })
  }-*/;

  public boolean backgroundMoveInitialContitionOk() {
    return currentSender == null && mouseDown && backgroundMouseDown;
  }

  public boolean backgroundMoveIsOn() {
		return backgroundMoving;
	}

	public void onMouseUp(Diagram sender, MatrixPointJS point, int keys) {
//  	complexElementHandler.showComplexElements(diagrams);
    if (backgroundMoving) {
      _notifyBackgroundMoveStream("end");
    }
    clear();
  }

  private void clear() {
    currentSender = null;
    backgroundMouseDown = true;
    mouseDown = false;
    backgroundMoving = false;
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
  
//  private void moveAll(final int dx, final int dy) {
//    // synchronous uses lot's of processing power
//    // currently disabled
//  	MatrixPointJS dp = MatrixPointJS.createScaledTransform(dx, dy, surface.getScaleFactor());
//    for (Diagram d : diagrams) {
//      d.applyTransform(dp.getDX(), dp.getDY());
//    }
//    
//    // asynchronous drawing, slightly funny but works :)
////    DeferredCommand.addCommand(new IncrementalCommand() {
////      private int index = 0;
//      
////      public boolean execute() {
////        final int size = diagrams.size();
////        boolean result = false;
////        // take few at a time for drawing
////        for (int i = 0; i < 2 && index < size; ++i) {
////          Diagram d = (Diagram) diagrams.get(index++);
////          d.applyTransform(dx, dy);
////          result = true;
////        }
////        return result;
////      }
////    });
//  }

}
