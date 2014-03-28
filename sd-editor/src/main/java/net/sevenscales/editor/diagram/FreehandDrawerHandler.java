package net.sevenscales.editor.diagram;

import java.util.ArrayList;
import java.util.List;

import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.event.FreehandModeChangedEvent;
import net.sevenscales.editor.api.event.FreehandModeChangedEvent.FreehandModeType;
import net.sevenscales.editor.api.event.OperationQueueRequestEvent;
import net.sevenscales.editor.api.impl.Theme;
import net.sevenscales.editor.content.utils.IntegerHelpers;
import net.sevenscales.editor.content.utils.ScaleHelpers;
import net.sevenscales.editor.diagram.shape.FreehandShape;
import net.sevenscales.editor.diagram.utils.GridUtils;
import net.sevenscales.editor.gfx.domain.IGroup;
import net.sevenscales.editor.gfx.domain.IPolyline;
import net.sevenscales.editor.gfx.domain.IShapeFactory;
import net.sevenscales.editor.gfx.domain.MatrixPointJS;
import net.sevenscales.editor.uicomponents.uml.FreehandElement;
import net.sevenscales.editor.content.ui.UIKeyHelpers;
import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.domain.utils.SLogger;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayInteger;
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
  private Diagram currentSender;
  private boolean mouseDown = false;
  private ISurfaceHandler surface;
	private boolean freehandKeyDown;
  private List<FreehandPath> freehandPahts = new ArrayList<FreehandPath>();
  private FreehandPath currentFreehandPath;
  private boolean staticMovement = false;

  private class FreehandPath {
    // private static SLogger logger = SLogger.createLogger(FreehandPath.class);
    IGroup group;
    IPolyline polyline;
    List<Integer> points = new ArrayList<Integer>();
    ISurfaceHandler surface;

    FreehandPath(ISurfaceHandler surface) {
      this.surface = surface;
      group = IShapeFactory.Util.factory(true).createGroup(surface.getSurface());
      //  path = IShapeFactory.Util.factory(true).createPath(group);
      polyline = IShapeFactory.Util.factory(true).createPolyline(group);

      polyline.setVisibility(true);
      polyline.setStroke(Theme.getCurrentColorScheme().getBorderColor().toHexString());
      polyline.setStrokeWidth(FreehandElement.FREEHAND_STROKE_WIDTH);
    }

    FreehandElement plot() {
      if (polyline != null && points.size() > 2) {
        // logger.debug("PLOTTING...");
        List<Integer> filteredPoints = null;
        if (staticMovement) {
          filteredPoints = allPoints();
        } else {
          filteredPoints = filterPoints();
        }
        
        FreehandElement diagram = new FreehandElement(surface, new FreehandShape(IntegerHelpers.toIntArray(filteredPoints)),
            Theme.createDefaultBackgroundColor(), Theme.createDefaultBorderColor(), Theme.createDefaultTextColor(),
            surface.getEditorContext().isEditable(), new DiagramItemDTO());
        // surface.add(diagram, true);
        polyline.setVisibility(false);
        points.clear();
        polyline.setShape(points);
        polyline.moveToBack();

        // logger.debug("PLOTTING... done");
        return diagram;
        // disturbs usabilility if context menu is shown right after drawing is ended
  //      surface.getEditorContext().getEventBus().fireEvent(new SelectionMouseUpEvent(diagram));
      }
      return null;
    }

    private List<Integer> allPoints() {
      List<Integer> result = new ArrayList<Integer>();
      for (int i = 0; i < points.size(); i += 2) {
        addTranslatedPoint(points.get(i), points.get(i + 1), result);
      }
      return result;
    }

    private List<Integer> filterPoints() {
      int modeType = surface.getEditorContext().<FreehandModeType>getAs(EditorProperty.FREEHAND_MODE_TYPE).value();
      logger.debug("filterPoints modeType {}", modeType);
      List<Integer> result = new ArrayList<Integer>();
      for (int i = 0; i < points.size(); i += 2) {
        // if (i % modeType == 0) {
        addTranslatedPoint(points.get(i), points.get(i + 1), result);
        // }
      }
      
      // add last point just in case if it has been filtered out above
      addTranslatedPoint(points.get(points.size()-2), points.get(points.size()-1), result);

      fit(result);

      return result;
    }
  
    private void addTranslatedPoint(int x, int y, List<Integer> points) {
      MatrixPointJS point = MatrixPointJS.createScaledPoint(x, y, surface.getScaleFactor());
      points.add(point.getX() - ScaleHelpers.scaleValue(surface.getRootLayer().getTransformX(), surface.getScaleFactor()));
      points.add(point.getY() - ScaleHelpers.scaleValue(surface.getRootLayer().getTransformY(), surface.getScaleFactor()));
    }

    private void fit(List<Integer> points) {
      JsArrayInteger dest = JsArrayInteger.createArray().cast();
      for (Integer i : points) {
        dest.push(i.intValue());
      }
      JsArray<PaperSegment> segments = fit(dest);
      for (int i = 0; i < segments.length(); ++i) {
        PaperPoint point = segments.get(i).getPoint();
        logger.debug("seg {}, {}", point.getX(), point.getY());
      }
    }

    private native JsArray<PaperSegment> fit(JsArrayInteger points)/*-{
      var segs = []
      for (var i = 0; i < points.length; i += 2) {
        segs.push(new $wnd.paper.Segment(points[i], points[i + 1]))
      }
      // var myPath = {_segments: [new $wnd.paper.Segment(1,2), new $wnd.paper.Segment(3,4), new $wnd.paper.Segment(5,6)]};
      var myPath = {_segments: segs}
      // console.log(myPath)
      var fitter = new $wnd.paper.PathFitter(myPath, 2.5);

      var getPathData = function(_segments, precision) {
        var segments = _segments,
          f = $wnd.paper.Formatter.instance,
          parts = [];

        // TODO: Add support for H/V and/or relative commands, where appropriate
        // and resulting in shorter strings
        function addCurve(seg1, seg2, skipLine) {
          var point1 = seg1._point,
            point2 = seg2._point,
            handle1 = seg1._handleOut,
            handle2 = seg2._handleIn;
          if (handle1.isZero() && handle2.isZero()) {
            if (!skipLine) {
              // L = absolute lineto: moving to a point with drawing
              parts.push('L' + f.point(point2, precision));
            }
          } else {
            // c = relative curveto: handle1, handle2 + end - start,
            // end - start
            var end = point2.subtract(point1);
            parts.push('c' + f.point(handle1, precision)
                + ' ' + f.point(end.add(handle2), precision)
                + ' ' + f.point(end, precision));
          }
        }

        if (segments.length === 0)
          return '';
        parts.push('M' + f.point(segments[0]._point));
        for (var i = 0, l = segments.length  - 1; i < l; i++)
          addCurve(segments[i], segments[i + 1], false);
        if (this._closed) {
          addCurve(segments[segments.length - 1], segments[0], true);
          parts.push('z');
        }
        return parts.join('');
      }

      console.log(getPathData(fitter.fit()));
      return fitter.fit();
    }-*/;

  }

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

        if (event.getTypeInt() == Event.ONKEYDOWN && UIKeyHelpers.justShift(ne) && FreehandDrawerHandler.this.surface.getEditorContext().isTrue(EditorProperty.FREEHAND_MODE)) {
          staticMovement = true;
        } else if (staticMovement && event.getTypeInt() == Event.ONKEYUP) {
          disableStaticMovement(ne);
        }
      }
    });
  }

  private void disableStaticMovement(NativeEvent ne) {
    staticMovement = false;
  }
  
  private void fireToggleFreehandMode() {
    boolean toggleValue = !surface.getEditorContext().isTrue(EditorProperty.FREEHAND_MODE);
    logger.debug("toggleValue {}", toggleValue);
    surface.getEditorContext().set(EditorProperty.FREEHAND_MODE, toggleValue);
    surface.getEditorContext().getEventBus().fireEvent(new FreehandModeChangedEvent(toggleValue));
  }

  private void createNewPath() {
    currentFreehandPath = new FreehandPath(surface);
    freehandPahts.add(currentFreehandPath);
  }
    
  public boolean onMouseDown(Diagram sender, MatrixPointJS point, int keys) {
  	if (!surface.getEditorContext().isTrue(EditorProperty.FREEHAND_MODE) || !surface.getName().equals(ISurfaceHandler.DRAWING_AREA)) {
      return false;
    }
  	
    createNewPath();

    this.currentSender = sender;
    setMouseDown();
    downX = point.getScreenX();
    downY = point.getScreenY();
    currentFreehandPath.points.add(downX);
    currentFreehandPath.points.add(downY);
    gridUtils.init(point.getX(), point.getY(), surface.getScaleFactor());
		return false;
  }

  private void setMouseDown() {
    mouseDown = true;
    surface.getEditorContext().set(EditorProperty.FREEHAND_MOUSE_DOWN, mouseDown);
  }

  private void setMouseUp() {
    mouseDown = false;
    surface.getEditorContext().set(EditorProperty.FREEHAND_MOUSE_DOWN, mouseDown);
  }

  public void onMouseEnter(Diagram sender, MatrixPointJS point) {
  }

  public void onMouseLeave(Diagram sender, MatrixPointJS point) {
    currentFreehandPath.points.clear();
  }

  public void onMouseMove(Diagram sender, MatrixPointJS point) {
    if (!surface.getEditorContext().isTrue(EditorProperty.FREEHAND_MODE) || !surface.getName().equals(ISurfaceHandler.DRAWING_AREA)) {
      return;
    }
    
    if (currentSender == null && mouseDown && backgroundMouseDown && freehandIsOn()) {
      drawFreehand(point);
    }
  }

  private boolean freehandIsOn() {
    return surface.getEditorContext().isTrue(EditorProperty.FREEHAND_MODE);
  }

  private void drawFreehand(MatrixPointJS point) {
  	int x = point.getScreenX();
  	int y = point.getScreenY();
  	
    int size = currentFreehandPath.points.size();
    if (staticMovement && size > 2) {
      int posx = size - 2;
      int posy = size - 1;
      currentFreehandPath.points.set(posx, x);
      currentFreehandPath.points.set(posy, y);
    } else {
      currentFreehandPath.points.add(x);
      currentFreehandPath.points.add(y);
    }
  	
  	currentFreehandPath.polyline.setShape(currentFreehandPath.points);
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
    surface.getEditorContext().getEventBus().fireEvent(new OperationQueueRequestEvent(OperationQueueRequestEvent.QueueRequest.BLOCK_SENDING));
  }

  private void freeSending() {
    logger.debug("freeSending...");
    surface.getEditorContext().getEventBus().fireEvent(new OperationQueueRequestEvent(OperationQueueRequestEvent.QueueRequest.FREE_SENDING));
  }

  private void plot() {
    // logger.debug("PLOTTING size {}...", freehandPahts.size());
    List<Diagram> drawing = new ArrayList<Diagram>();
    for (FreehandPath fp : freehandPahts) {
      FreehandElement fe = fp.plot();
      if (fe != null) {
        drawing.add(fe);
      }
    }
    if (drawing.size() > 0) {
      surface.add(drawing, true, false);
      // start blocking and also timer to free sending
      blockSending();
      freeLater();
    }
    freehandPahts.clear();
    // logger.debug("PLOTTING... done");
  }

	public void onMouseUp(Diagram sender, MatrixPointJS point) {
    plotLater();
    currentSender = null;
    backgroundMouseDown = true;
    setMouseUp();
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
