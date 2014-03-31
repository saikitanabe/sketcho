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
import net.sevenscales.editor.content.utils.ScaleHelpers.ScaledAndTranslatedPoint;
import net.sevenscales.editor.content.utils.DiagramHelpers;
import net.sevenscales.editor.diagram.shape.FreehandShape;
import net.sevenscales.editor.diagram.shape.GenericShape;
import net.sevenscales.editor.diagram.utils.GridUtils;
import net.sevenscales.editor.gfx.domain.IGroup;
import net.sevenscales.editor.gfx.domain.IPolyline;
import net.sevenscales.editor.gfx.domain.IShapeFactory;
import net.sevenscales.editor.gfx.domain.MatrixPointJS;
import net.sevenscales.editor.gfx.domain.Point;
import net.sevenscales.editor.uicomponents.uml.GenericElement;
import net.sevenscales.editor.uicomponents.uml.FreehandElement;
import net.sevenscales.editor.uicomponents.AngleUtil2;
import net.sevenscales.editor.content.ui.UIKeyHelpers;
import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.domain.ElementType;
import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.domain.SvgDataDTO;
import net.sevenscales.domain.IPathRO;
import net.sevenscales.domain.PathDTO;

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
  private boolean mouseDown = false;
  private ISurfaceHandler surface;
	private boolean freehandKeyDown;
  private List<FreehandPath> freehandPahts = new ArrayList<FreehandPath>();
  private FreehandPath currentFreehandPath;
  private boolean staticMovement = false;
  private boolean closePath = false;

  private class FreehandPath {
    // private static SLogger logger = SLogger.createLogger(FreehandPath.class);
    IGroup group;
    IPolyline polyline;
    List<Integer> points = new ArrayList<Integer>();
    ISurfaceHandler surface;
    boolean curve = true;

    FreehandPath(ISurfaceHandler surface) {
      this.surface = surface;
      group = IShapeFactory.Util.factory(true).createGroup(surface.getSurface());
      //  path = IShapeFactory.Util.factory(true).createPath(group);
      polyline = IShapeFactory.Util.factory(true).createPolyline(group);

      polyline.setVisibility(true);
      polyline.setStroke(Theme.getCurrentColorScheme().getBorderColor().toHexString());
      polyline.setStrokeWidth(FreehandElement.FREEHAND_STROKE_WIDTH);
    }

    GenericElement plot() {
      if (polyline != null && points.size() > 2) {
        // logger.debug("PLOTTING...");

        int absleft = DiagramHelpers.getLeftCoordinate(points);
        int abstop = DiagramHelpers.getTopCoordinate(points);
        int width = DiagramHelpers.getWidth(points);
        int height = DiagramHelpers.getHeight(points);

        String svg = null;
        if (curve) {
          svg = fitPointsToSvg(absleft, abstop);
        } else {
          // if staticMovement, do not simplify path! Format path with lines
          svg = formatAllPointsAsLines(absleft, abstop);
        }

        // use absolute values to calculate relative points
        ScaledAndTranslatedPoint pos = ScaleHelpers.scaleAndTranslateScreenpoint(absleft, abstop, surface);
        // need to scale width and height to correspond screen coordinates
        // doe not translate, wince only left, top is scaled and translated!
        int scaledWidth = ScaleHelpers.scaleValue(width, surface.getScaleFactor());
        int scaledHeight = ScaleHelpers.scaleValue(height, surface.getScaleFactor());
        List<PathDTO> paths = new ArrayList<PathDTO>();
        paths.add(new PathDTO(svg, ""));
        SvgDataDTO svgdata = new SvgDataDTO(paths, scaledWidth, scaledHeight);
        GenericElement diagram = new GenericElement(surface, 
          new GenericShape(ElementType.FREEHAND2.getValue(), 
                           pos.scaledAndTranslatedPoint.x, pos.scaledAndTranslatedPoint.y, scaledWidth, scaledHeight, 0, svgdata),
          "", 
          Theme.createDefaultBackgroundColor(), 
          Theme.createDefaultBorderColor(), 
          Theme.createDefaultTextColor(),
          surface.getEditorContext().isEditable(), 
          DiagramItemDTO.createGenericItem(ElementType.FREEHAND2));
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

    private String formatAllPointsAsLines(int absx, int absy) {
      String result = "";
      List<Integer> relative = relativePoints(absx, absy);
      for (int i = 0; i < relative.size(); i += 2) {
        if (i == 0) {
          result += "m";
        } else {
          result += " ";
        }
        result += relative.get(i) + "," + relative.get(i + 1);
      }
      if (closePath) {
        result += "z";
      }
      return result;
    }

    private String fitPointsToSvg(int absx, int absy) {
      int modeType = surface.getEditorContext().<FreehandModeType>getAs(EditorProperty.FREEHAND_MODE_TYPE).value();
      logger.debug("fitPointsToSvg modeType {}", modeType);
      List<Integer> relative = relativePoints(absx, absy);
      return fit(relative);
    }

    private List<Integer> relativePoints(int absx, int absy) {
      List<Integer> result = new ArrayList<Integer>();
      int prevx = absx;
      int prevy = absy;

      for (int i = 0; i < points.size(); i += 2) {
        // - calculation from absolute values to relative (use unscaled and translated values)
        // - since this is now relative
        // - do not take into account root layer tranlation and only scale point
        // - left, top will take into account root layer scaling and translation
        int ax = points.get(i);
        int ay = points.get(i + 1); 
        int x = 0;
        int y = 0;

        if (curve) {
          // for some reason need minux first abs from all coordinates...
          x = ax - absx;
          y = ay - absy;
        } else {
          x = ax - prevx;
          y = ay - prevy;
        }
        result.add(ScaleHelpers.scaleValue(x, surface.getScaleFactor()));
        result.add(ScaleHelpers.scaleValue(y, surface.getScaleFactor()));
        prevx = ax;
        prevy = ay;
      }

      trimLastExtreaZeroMove(result);
      return result;
    }

    private void trimLastExtreaZeroMove(List<Integer> relativePoints) {
      int size = relativePoints.size();
      if (size > 2 && relativePoints.get(size - 2) == 0 && relativePoints.get(size - 1) == 0) {
        // remove last zero move x, y
        relativePoints.remove(relativePoints.size() - 1);
        relativePoints.remove(relativePoints.size() - 1);
      }
    }
  
    private String fit(List<Integer> points) {
      JsArrayInteger dest = JsArrayInteger.createArray().cast();
      for (Integer i : points) {
        dest.push(i.intValue());
      }
      // JsArray<PaperSegment> segments = fit(dest);
      // for (int i = 0; i < segments.length(); ++i) {
      //   PaperPoint point = segments.get(i).getPoint();
      //   logger.debug("seg {}, {}", point.getX(), point.getY());
      // }
      return fit(dest);
    }

    private native String /*JsArray<PaperSegment>*/ fit(JsArrayInteger points)/*-{
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
              parts.push('l' + f.point(point2, precision));
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
        parts.push('m' + f.point(segments[0]._point));
        for (var i = 0, l = segments.length  - 1; i < l; i++)
          addCurve(segments[i], segments[i + 1], false);
        if (this._closed) {
          addCurve(segments[segments.length - 1], segments[0], true);
          parts.push('z');
        }
        return parts.join('');
      }

      console.log(getPathData(fitter.fit()));
      // return fitter.fit();
      return getPathData(fitter.fit());
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

        if (event.getTypeInt() == Event.ONMOUSEDOWN) {
          handleMouseDown(event.getNativeEvent());
        } else if (event.getTypeInt() == Event.ONDBLCLICK) {
          handleDoubleClick(event.getNativeEvent());
        }
      }
    });
  }

  private void handleMouseDown(NativeEvent ne) {
    int x = ne.getClientX();
    int y = ne.getClientY();

    MatrixPointJS point = MatrixPointJS.createScaledPoint(x, y, surface.getScaleFactor());
    startOrContinueExistingPath(point);
  }

  private void handleDoubleClick(NativeEvent ne) {
    removeDoubleClickMouseDownAdditions();
    markClosePath();
    endDrawing();
    markUnclosePath();
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
    closePath = true;
  }

  private void markUnclosePath() {
    closePath = false;
  }

  private void disableStaticMovement(NativeEvent ne) {
    staticMovement = false;
    endDrawing();
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
    // return startOrContinueExistingPath(point);
    return false;
  }

  public void onMouseUp(Diagram sender, MatrixPointJS point) {
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
      int size = currentFreehandPath.points.size();
      drawStatic(point.getScreenX(), point.getScreenY(), size);
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
    currentFreehandPath = null;
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
    
    if (mouseDown && backgroundMouseDown && freehandIsOn()) {
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
    if (staticMovement && size >= 4) {
      drawStatic(x, y, size);
    } else {
      currentFreehandPath.points.add(x);
      currentFreehandPath.points.add(y);
    }
  	
  	currentFreehandPath.polyline.setShape(currentFreehandPath.points);
	}

  private void drawStatic(int x, int y, int size) {
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
      GenericElement fe = fp.plot();
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
