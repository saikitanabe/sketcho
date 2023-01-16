package net.sevenscales.editor.diagram;

import java.util.HashSet;
import java.util.Set;

import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.ActionType;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.content.ui.IModeManager;
import net.sevenscales.editor.diagram.drag.AnchorElement;
import net.sevenscales.editor.diagram.utils.GridUtils;
import net.sevenscales.editor.diagram.utils.MouseDiagramEventHelpers;
import net.sevenscales.editor.gfx.domain.IGraphics;
import net.sevenscales.editor.gfx.domain.MatrixPointJS;
import net.sevenscales.editor.gfx.domain.OrgEvent;
import net.sevenscales.editor.gfx.domain.Point;

public class MouseDiagramResizeHandler implements MouseDiagramHandler, MouseDiagramDoubleClickHandler, MouseLongPressHandler {
  private static final SLogger logger = SLogger.createLogger(MouseDiagramResizeHandler.class);
  
  static {
    SLogger.addFilter(MouseDiagramResizeHandler.class);
  }

	private Diagram sender;
	private MouseDiagramHandlerManager parent;
	boolean mouseDown = false;
	boolean resizing = false;
	private Point mouseDownPoint;
	private ResizeHandlerCollection resizeHandlerCollection;
  private GridUtils gridUtils = new GridUtils();
  private Point diffTemp = new Point();
//	private int prevX;
//	private int prevY;
	private boolean onResizeArea;
  private ISurfaceHandler surface;
  private IModeManager modeManager;
	private int prevDX;
	private int prevDY;
	private boolean keepAspectRatio;	

  private static net.sevenscales.editor.gfx.domain.IPolyline tempRect;
	
	public MouseDiagramResizeHandler(MouseDiagramHandlerManager parent, ISurfaceHandler surface, 
	    IModeManager modeManager) {
		this.parent = parent;
		this.surface = surface;
		this.modeManager = modeManager;
		mouseDownPoint = new Point();
		resizeHandlerCollection = new ResizeHandlerCollection();
	}

  @Override
	public boolean onMouseDown(OrgEvent event, Diagram sender, MatrixPointJS point, int keys) {
    if (!surface.isDragEnabled() || surface.getSelectionHandler().getOnlyOneSelected() == null) {
      return false;
    }
    
    boolean result = false;
		if (sender != null && !(modeManager.isConnectMode())) {
			// do not handle surface events
			keepAspectRatio = keys == IGraphics.SHIFT;
			int width = sender.getWidth();
			int height = sender.getHeight();

			onResizeArea = sender.onResizeArea(point.getX(), point.getY());
			if (onResizeArea) {
				this.sender = sender;
				mouseDown = true;
				mouseDownPoint.x = point.getX();
				mouseDownPoint.y = point.getY();
				
//				gridUtils.init(point.getX(), point.getY(), surface.getScaleFactor());
//				prevX = point.getX();
//				prevY = point.getY();
				
				gridUtils.init(point.getScreenX(), point.getScreenY(), surface.getScaleFactor());
				result = true;

        initRect();
        tempRect.setVisibility(true);
			}
		}
		return result;
	}

  @Override
	public void onMouseEnter(OrgEvent event, Diagram sender, MatrixPointJS point) {
//		resizeSender = sender;
//		System.out.println("enter x:"+x+" y:"+y+sender);
//		
//		if (sender != null && sender.onResizeArea(x, y) != null) {
//			// change cursor
//			System.out.println("resize");
//			SilverUtils.setCursor(SilverUtils.RESIZE);
//		} else {
//			System.out.println("no resize");
//			SilverUtils.setCursor(SilverUtils.DEFAULT);
//		}
	}

	public void onMouseLeave(Diagram sender, MatrixPointJS point) {
//		if (!mouseDown) {
//			resizeSender = null;
//			System.out.println("out of element scope" + sender);
//		}
//		if (sender != null) {
//			System.out.println("out of element scope");
//			SilverUtils.setCursor(SilverUtils.DEFAULT);
//		}
	}

  @Override
	public void onMouseMove(OrgEvent event, Diagram sender, MatrixPointJS point) {
    if (!surface.isDragEnabled()) {
      return;
    }

    if (onResizeArea && !gridUtils.passTreshold(point, 5)) {
      return;
    }

		if (mouseDown && onResizeArea) { 
//      if (!gridUtils.pass(x, y)) {
//        return;
//      }
		  
			if (!resizing) {
				// not yet resized => start resizing
		    prevDX = 0;
		    prevDY = 0;

				this.sender.resizeStart();
				resizeHandlerCollection.fireResizeStart(this.sender);
        notifyResizeStart();
      }
      
      logger.debug("Resizing ON");
      resizing = true;
      
  
			// resize component
//			System.out.println("resize component:" + resizeInfo.area);
			
			MatrixPointJS dp = MatrixPointJS.createScaledTransform(gridUtils.dx(point.getScreenX()), gridUtils.dy(point.getScreenY()), surface.getScaleFactor());
			int dx = dp.getDX() - prevDX;
			int dy = dp.getDY() - prevDY;
      prevDX = dp.getDX();
      prevDY = dp.getDY();


//      int dx = gridUtils.diffX(point.getX(), mouseDownPoint.x);
//      int dy = gridUtils.diffY(point.getY(), mouseDownPoint.y);
      
//      prevX = x;
//      prevY = y;

      if (keepAspectRatio) {
				int width = this.sender.getWidth();
				int height = this.sender.getHeight();
				if (height < width) {
					double aspectRatio = width / (double) height;
					double newHeight = height + dy;
					double newWidth = aspectRatio * newHeight;

					diffTemp.x = (int) (newWidth - width);
					diffTemp.y = dy;
				} else {
					double aspectRatio = height / (double) width;
					double newWidth = width + dx;
					double newHeight = aspectRatio * newWidth;

					diffTemp.x = dx;
					diffTemp.y = (int) (newHeight - height);
				}
      } else {
				diffTemp.x = dx;
				diffTemp.y = dy;
      }
			
			// System.out.println("resizing: diffTemp.x" + dx + " diffTemp.y" + dy);

      // NOTE should not use this if calculating using polyline rect
      // then should set resize(left, top, width, height)
      if (this.sender.resize(diffTemp)) {
				// returns true if resized; there is minimum resize level for 
				// components
				resizeHandlerCollection.fireOnResize(this.sender, diffTemp);
			}

      // >>>>> ALTERNATIVE
      // Alternative way to calculate resize
      // then should set resize(left, top, width, height)
      // double left = this.sender.getLeft();
      // double top = this.sender.getTop();
      // double width = this.sender.getWidth();
      // double height = this.sender.getHeight();
      // Integer rotateDegree = this.sender.getDiagramItem().getRotateDegrees();

      // double cx = left + width / 2;
      // double cy = top + height / 2;


      // com.google.gwt.touch.client.Point move = net.sevenscales.editor.uicomponents.AnchorUtils.rotatePoint(
      //   point.getX(),
      //   point.getY(),
      //   cx,
      //   cy,
      //   rotateDegree
      // );

      // double diffX = point.getX() - mouseDownPoint.x;
      // double diffY = point.getY() - mouseDownPoint.y;

      // width = diffX + width;
      // height = diffY + height;

      // double right = left + width;
      // double bottom = top + height;

      // com.google.gwt.touch.client.Point leftTop = net.sevenscales.editor.uicomponents.AnchorUtils.rotatePoint(
      //   left,
      //   top,
      //   cx,
      //   cy,
      //   rotateDegree
      // );

      // com.google.gwt.touch.client.Point rightTop = net.sevenscales.editor.uicomponents.AnchorUtils.rotatePoint(
      //   right,
      //   top,
      //   cx,
      //   cy,
      //   rotateDegree
      // );

      // com.google.gwt.touch.client.Point rightBottom = net.sevenscales.editor.uicomponents.AnchorUtils.rotatePoint(
      //   right,
      //   bottom,
      //   cx,
      //   cy,
      //   rotateDegree
      // );

      // com.google.gwt.touch.client.Point leftBottom = net.sevenscales.editor.uicomponents.AnchorUtils.rotatePoint(
      //   left,
      //   bottom,
      //   cx,
      //   cy,
      //   rotateDegree
      // );

      // debugArea(leftTop, rightTop, rightBottom, leftBottom);
      // <<<<<< ALTERNATIVE
		}
	}
  private native void notifyResizeStart()/*-{
    $wnd.globalStreams.contextMenuStream.push({type:'resize-start'})
  }-*/;

	@Override
	public void onMouseUp(Diagram sender, MatrixPointJS point, int keys) {
    if (!surface.isDragEnabled()) {
      return;
    }
    keepAspectRatio = false;
    
		if (this.sender != null) {
			this.sender.resizeEnd();
			AnchorElement.dragEndAnchors(this.sender);
			resizeHandlerCollection.fireResizeEnd(this.sender);
			_notifyResizeEnd();
			
			// TODO collect all dependant relationships!!!
			// to common code with mouse drag manager
			Set<Diagram> resizedElements = new HashSet<Diagram>();
			resizedElements.add(this.sender);
			MouseDiagramEventHelpers.fireDiagramsChangedEvenet(resizedElements, surface, ActionType.NONE);
			// surface.getEditorContext().getEventBus().fireEvent(new PotentialOnChangedEvent(this.sender));

//			SilverUtils.setCursor(SilverUtils.DEFAULT);
		}

		clearResize();

    initRect();
    tempRect.setVisibility(false);
	}

  private void initRect() {
    if (tempRect == null) {
      tempRect = net.sevenscales.editor.gfx.domain.IShapeFactory.Util.factory(true).createPolyline(surface.getInteractionLayer());
      // tempRect.setShape(0);
      tempRect.setStroke(218, 57, 57, 1);
      // tempRect.setFill(218, 57, 57, 1);
      tempRect.setStrokeWidth(2);
    }

    tempRect.setShape(new int[]{0, 0});
  }

  private void debugArea(
    com.google.gwt.touch.client.Point leftTop,
    com.google.gwt.touch.client.Point rightTop,
    com.google.gwt.touch.client.Point rightBottom,
    com.google.gwt.touch.client.Point leftBottom
    // int rotate
  ) {

    if (tempRect != null) {
      tempRect.setShape(new int[]{
        ((int)leftTop.getX()), ((int)leftTop.getY()),
        ((int)rightTop.getX()), ((int)rightTop.getY()),
        ((int)rightBottom.getX()), ((int)rightBottom.getY()),
        ((int)leftBottom.getX()), ((int)leftBottom.getY()),
        ((int)leftTop.getX()), ((int)leftTop.getY())
      });
    }
  }

	private native void _notifyResizeEnd()/*-{
		$wnd.globalStreams.shapeResizeStream.push()
	}-*/;

	private void clearResize() {
		this.sender = null;
		mouseDown = false;
		resizing = false;
    onResizeArea = false;
    logger.debug("Resizing OFF");
	}

	/**
	* Reserves resize are same as starting to resize.
	*/
	public boolean isResizing() {
		return onResizeArea;
	}

	@Override
	public void onDoubleClick(Diagram sender, MatrixPointJS point) {
		clearResize();
	}

	@Override
	public void onLongPress(int x, int y) {
		clearResize();
	}
	
	@Override
	public void onTouchStart(OrgEvent event, Diagram sender, MatrixPointJS point) {
	}
  @Override
  public void onTouchMove(OrgEvent event, Diagram sender, MatrixPointJS point) {
  }
  @Override
  public void onTouchEnd(Diagram sender, MatrixPointJS point) {
  }

	public void addResizeHandler(DiagramResizeHandler handler) {
		resizeHandlerCollection.add(handler);
	}

  public void reset() {
    // resizeHandlerCollection.clear();
    resizeHandlerCollection = new ResizeHandlerCollection();
  }

}
