package net.sevenscales.editor.diagram;

import java.util.ArrayList;
import java.util.List;

import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.event.FreehandModeChangedEvent;
import net.sevenscales.editor.api.event.FreehandModeChangedEventHandler;
import net.sevenscales.editor.api.event.ColorSelectedEventHandler;
import net.sevenscales.editor.api.event.ColorSelectedEvent;
import net.sevenscales.editor.api.impl.Theme;
import net.sevenscales.editor.content.utils.IntegerHelpers;
import net.sevenscales.editor.content.utils.ScaleHelpers;
import net.sevenscales.editor.content.utils.DiagramHelpers;
import net.sevenscales.editor.diagram.utils.GridUtils;
import net.sevenscales.editor.gfx.domain.IGroup;
import net.sevenscales.editor.gfx.domain.IPolyline;
import net.sevenscales.editor.gfx.domain.IShapeFactory;
import net.sevenscales.editor.gfx.domain.MatrixPointJS;
import net.sevenscales.editor.gfx.domain.Point;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.uicomponents.uml.GenericElement;
import net.sevenscales.editor.uicomponents.uml.FreehandElement;
import net.sevenscales.editor.uicomponents.AngleUtil2;
import net.sevenscales.editor.content.ui.UIKeyHelpers;
import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.domain.SvgDataDTO;
import net.sevenscales.domain.IPathRO;
import net.sevenscales.domain.PathDTO;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;


public class FreehandDrawerHandler implements MouseDiagramHandler {
  private static SLogger logger = SLogger.createLogger(FreehandDrawerHandler.class);

  static {
    logger.addFilter(FreehandDrawerHandler.class);
  }

  private GridUtils gridUtils;
  private int downX;
  private int downY;
  private boolean backgroundMouseDown = true;
  private boolean mouseDown = false;
  private ISurfaceHandler surface;
	private boolean freehandKeyDown;
  private List<FreehandPath> freehandPahts = new ArrayList<FreehandPath>();
  private FreehandPath currentFreehandPath;
  private Color currentColor;
  private boolean staticMovement = false;

  private static class PaperPoint extends JavaScriptObject {
    protected PaperPoint() {
    }

    public final native int getX()/*-{
      return this.x;
    }-*/;
    public final native int getY()/*-{
      return this.y;
    }-*/;
  }

  private static class PaperSegment extends JavaScriptObject {
    protected PaperSegment() {
    }

    public final native PaperPoint getPoint()/*-{
      return this.point;
    }-*/;
  }

//  private Map<String,String> params = new HashMap<String, String>();

  public FreehandDrawerHandler(ISurfaceHandler surface) {
    this.surface = surface;
    
    gridUtils = new GridUtils(1);
    currentColor = Theme.getCurrentColorScheme().getBorderColor();
    
    Event.addNativePreviewHandler(new NativePreviewHandler() {
      @Override
      public void onPreviewNativeEvent(NativePreviewEvent event) {
        NativeEvent ne = event.getNativeEvent();
        if (!freehandKeyDown && event.getTypeInt() == Event.ONKEYDOWN && UIKeyHelpers.noMetaKeys(ne) && UIKeyHelpers.isEditorClosed(FreehandDrawerHandler.this.surface.getEditorContext())) {
          if (ne.getKeyCode() == 'F' && UIKeyHelpers.allMenusAreClosed()) {
            freehandKeyDown = true;
            fireToggleFreehandMode();
          }
        }

        if (freehandKeyDown && event.getTypeInt() == Event.ONKEYUP && UIKeyHelpers.isEditorClosed(FreehandDrawerHandler.this.surface.getEditorContext())) {
          if (ne.getKeyCode() == 'F' && UIKeyHelpers.allMenusAreClosed()) {
            freehandKeyDown = false;
          }
        }

        if (!freehandMode()) {
          return;
        }

        // freehand mode handling
        evenDuringFreehandMode(event);
      }
    });

    surface.getEditorContext().getEventBus().addHandler(FreehandModeChangedEvent.TYPE, new FreehandModeChangedEventHandler() {
      @Override
      public void on(FreehandModeChangedEvent event) {
        switch (event.getModeType()) {
          case FREEHAND_FREE: {
            staticMovement = false;
            break;
          }
          case FREEHAND_LINES: {
            staticMovement = true;
            break;
          }
        }
        
      }
    });

    surface.getEditorContext().getEventBus().addHandler(ColorSelectedEvent.TYPE, new ColorSelectedEventHandler() {
      @Override
      public void onSelection(ColorSelectedEvent event) {
        if (freehandMode()) {
          currentColor = event.getElementColor().getBorderColor();
        }
      }
    });

    handleEscKey(this);
  }

  private native void handleEscKey(FreehandDrawerHandler me)/*-{
    $wnd.cancelStream.onValue(function(v) {
      me.@net.sevenscales.editor.diagram.FreehandDrawerHandler::onEsc()();
    })
  }-*/;

  private void onEsc() {
    if (freehandMode()) {
      fireToggleFreehandMode();
    }
  }

  private void evenDuringFreehandMode(NativePreviewEvent event) {
    NativeEvent ne = event.getNativeEvent();
    if (event.getTypeInt() == Event.ONKEYDOWN && UIKeyHelpers.justShift(ne)) {
      staticMovement = true;
    } else if (staticMovement && event.getTypeInt() == Event.ONKEYUP) {
      disableStaticMovement(ne);
    }

    if (event.getTypeInt() == Event.ONMOUSEDOWN) {
      if (Element.is(ne.getEventTarget())) {
        // check is mouse down for surface or it could be some button
        if (surface.getElement().isOrHasChild(Element.as(ne.getEventTarget()))) {
          handleMouseDown(ne.getClientX(), ne.getClientY());
        }
      }
    } else if (event.getTypeInt() == Event.ONTOUCHSTART) {
      Touch touch = getTouches(ne).get(0);
      if (Element.is(ne.getEventTarget())) {
        if (surface.getElement().isOrHasChild(Element.as(ne.getEventTarget()))) {
          handleMouseDown(touch.getClientX(), touch.getClientY());
        }
      }
    } /*else if (event.getTypeInt() == Event.ONTOUCHMOVE) {
      // potential idea to start 
      // JsArray<Touch> touches = getTouches(ne);
      // if (touches.length() == 2) {
      //   staticMovement = true;
      // }

      // Touch touch = touches.get(1);
      // MatrixPointJS point = MatrixPointJS.createScaledPoint(touch.getClientX(), touch.getClientY(), surface.getScaleFactor());
      // drawFreehand(point);
    } */ else if (event.getTypeInt() == Event.ONDBLCLICK) {
      handleDoubleClick(event.getNativeEvent());
    }    
  }

  private native boolean isClassString(Element e)/*-{
    if (e.className instanceof String) {
      return true;
    }
    return false;
  }-*/;

  private boolean freehandMode() {
    return FreehandDrawerHandler.this.surface.getEditorContext().isTrue(EditorProperty.FREEHAND_MODE);
  }

  private JsArray<Touch> getTouches(NativeEvent ne) {
    return ne.getTouches();
  }


  private void handleMouseDown(int x, int y) {
    MatrixPointJS point = MatrixPointJS.createScaledPoint(x, y, surface.getScaleFactor());
    startOrContinueExistingPath(point);
  }

  private void handleDoubleClick(NativeEvent ne) {
    if (currentFreehandPath != null) {
      removeDoubleClickMouseDownAdditions();
      markClosePath();
      endDrawing();
    }
  }

  private void removeDoubleClickMouseDownAdditions() {
    int size = currentFreehandPath.points.size();
    if (size > 4) {
      // two mouse down double clicks
      currentFreehandPath.points.remove(size - 1);
      currentFreehandPath.points.remove(size - 2);
      currentFreehandPath.points.remove(size - 3);
      currentFreehandPath.points.remove(size - 4);
    }
  }

  private void markClosePath() {
    currentFreehandPath.closePath = true;
  }

  private void disableStaticMovement(NativeEvent ne) {
    staticMovement = false;
    endDrawing();
  }
  
  private void fireToggleFreehandMode() {
    boolean toggleValue = !surface.getEditorContext().isTrue(EditorProperty.FREEHAND_MODE);
    // logger.debug("toggleValue {}", toggleValue);
    // surface.getEditorContext().set(EditorProperty.FREEHAND_MODE, toggleValue);
    surface.getEditorContext().getEventBus().fireEvent(new FreehandModeChangedEvent(toggleValue));
  }

  private void createNewPath() {
    currentFreehandPath = new FreehandPath(surface);
    currentFreehandPath.changeColor(currentColor);
    freehandPahts.add(currentFreehandPath);
  }

  public boolean onMouseDown(Diagram sender, MatrixPointJS point, int keys) {
    // not handled through here!!! handling custom mouse down preview event
    // since when drawing on top of existing element coordinates are wrong
    // and free hand needs to have surface coordinates
    return false;
  }

  @Override
  public void onMouseUp(Diagram sender, MatrixPointJS point, int keys) {
    if (!staticMovement) {
      endDrawing();
    }
  }

  private boolean startOrContinueExistingPath(MatrixPointJS point) {
    if (!surface.getEditorContext().isTrue(EditorProperty.FREEHAND_MODE) || !surface.getName().equals(ISurfaceHandler.DRAWING_AREA)) {
      return false;
    }

    if (staticMovement && currentFreehandPath != null && currentFreehandPath.points.size() > 0) {
      // continue existing freehand path
      drawStatic(point.getScreenX(), point.getScreenY());
      int size = currentFreehandPath.points.size();
      currentFreehandPath.points.add(currentFreehandPath.points.get(size - 2));
      currentFreehandPath.points.add(currentFreehandPath.points.get(size - 1));
    } else {
      // start new freehand drawing
      createNewPath();
      setMouseDown();
      downX = point.getScreenX();
      downY = point.getScreenY();
      currentFreehandPath.points.add(downX);
      currentFreehandPath.points.add(downY);
      gridUtils.init(point.getX(), point.getY(), surface.getScaleFactor());
    }
    return true;
  }  

  private void endDrawing() {
    plotLater();
    backgroundMouseDown = true;
    setMouseUp();
  }

  private void setMouseDown() {
    mouseDown = true;
    surface.getEditorContext().set(EditorProperty.FREEHAND_MOUSE_DOWN, mouseDown);
  }

  private void setMouseUp() {
    mouseDown = false;
    clearPoints();
    surface.getEditorContext().set(EditorProperty.FREEHAND_MOUSE_DOWN, mouseDown);
  }

  public void onMouseEnter(Diagram sender, MatrixPointJS point) {
  }

  public void onMouseLeave(Diagram sender, MatrixPointJS point) {
    clearPoints();
  }

  public void onMouseMove(Diagram sender, MatrixPointJS point) {
    if (!surface.getEditorContext().isTrue(EditorProperty.FREEHAND_MODE) || !surface.getName().equals(ISurfaceHandler.DRAWING_AREA)) {
      return;
    }
    
    if (mouseDown && backgroundMouseDown && freehandIsOn()) {
      drawFreehand(point);
    }
  }

  private void clearPoints() {
    if (currentFreehandPath != null) {
      currentFreehandPath.points.clear();    
    }
  }

  private boolean freehandIsOn() {
    return surface.getEditorContext().isTrue(EditorProperty.FREEHAND_MODE);
  }

  private void drawFreehand(MatrixPointJS point) {
  	int x = point.getScreenX();
  	int y = point.getScreenY();
  	
    int size = currentFreehandPath.points.size();
    if (staticMovement && size >= 4) {
      drawStatic(x, y);
    } else {
      currentFreehandPath.points.add(x);
      currentFreehandPath.points.add(y);
    }
  	
  	currentFreehandPath.polyline.setShape(currentFreehandPath.points);
	}

  private void drawStatic(int x, int y) {
    int size = currentFreehandPath.points.size();
    if (size >= 4) {
      int posx = size - 2;
      int posy = size - 1;
      int prevxpos = size - 4;
      int prevypos = size - 3;
      int x1 = currentFreehandPath.points.get(prevxpos);
      int y1 = currentFreehandPath.points.get(prevypos);
      Point ep = endPoint(x1, y1, x, y);
      currentFreehandPath.points.set(posx, ep.x);
      currentFreehandPath.points.set(posy, ep.y);
      currentFreehandPath.curve = false;
    }
  }

  private Point endPoint(int x1, int y1, int x2, int y2) {
    final double angle0 = 0;
    final double angle45 = Math.PI / 4;
    final double angle90 = Math.PI / 2;
    final double angle135 = 3 * Math.PI / 4;
    final double angle180 = Math.PI;
    final double angle225 = 5 * Math.PI / 4 - 2 * Math.PI;
    final double angle270 = 3 * Math.PI / 2 - 2 * Math.PI;
    final double angle315 = 7 * Math.PI / 4 - 2 * Math.PI;
    final double[] angles = new double[]{angle0, angle45, angle90, angle135, angle180, angle225, angle270, angle315};

    int dy = y2 - y1;
    int dx = x2 - x1;
    double theta = Math.atan2(dy, dx);

    double smallest = Double.MAX_VALUE;
    double theangle = angle0;
    for (double a : angles) {
      double diff = Math.abs(theta - a);
      if (diff < smallest) {
        theangle = a;
        smallest = diff;
      }
    }

    double length = Math.sqrt(Math.pow(y2 - y1, 2) + Math.pow(x2 - x1, 2));

    // (x2,y2)=(x1+l⋅cos(a),y1+l⋅sin(a))
    double rx2 = x1 + length * Math.cos(theangle);
    double ry2 = y1 + length * Math.sin(theangle);
    return new Point((int)rx2, (int)ry2);
  }

  private class PlotTimer extends Timer {
    public PlotTimer() {
    }

    @Override
    public void run() {
      if (mouseDown) {
        return;
      }

      // Scheduler.get().scheduleDeferred(new ScheduledCommand() {
      //   @Override
      //   public void execute() {
          freeSending();
          // plot();
      //   }
      // });

      cancel();
    }
  };

  private PlotTimer timer;

  private void plotLater() {
    try {
      // plot later blocks sending
      // blocking is dangerous, if any code fails
      // nothing gets sent to server until user refreh the board
      // but all changes have been lost
      plot();
    } catch (Exception e) {
      freeSending();
      Window.alert("Sorry, something unexpected happened! Please try to reload the board");
    }
  }

  private void freeLater() {
    if (timer != null) {
      timer.cancel();
    } else {
      timer = new PlotTimer();
    }
    timer.scheduleRepeating(2000);
  }

  private void blockSending() {
    logger.debug("blockSending...");
    // surface.getEditorContext().getEventBus().fireEvent(new OperationQueueRequestEvent(OperationQueueRequestEvent.QueueRequest.BLOCK_SENDING));
  }

  private void freeSending() {
    logger.debug("freeSending...");
    // surface.getEditorContext().getEventBus().fireEvent(new OperationQueueRequestEvent(OperationQueueRequestEvent.QueueRequest.FREE_SENDING));
  }

  private void plot() {
    // logger.debug("PLOTTING size {}...", freehandPahts.size());
    List<Diagram> drawing = new ArrayList<Diagram>();
    for (FreehandPath fp : freehandPahts) {
      Diagram fe = fp.plot();
      if (fe != null) {
        drawing.add(fe);
      }
    }
    if (drawing.size() > 0) {
      surface.add(drawing, true, false);
      // start blocking and also timer to free sending
      // blockAndStartFreeTimer();
    }
    freehandPahts.clear();
    // logger.debug("PLOTTING... done");
  }

  private void blockAndStartFreeTimer() {
    blockSending();
    freeLater();
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

	public boolean handling() {
		return surface.getEditorContext().isTrue(EditorProperty.FREEHAND_MODE) && surface.getName().equals(ISurfaceHandler.DRAWING_AREA);
	}
  
}
