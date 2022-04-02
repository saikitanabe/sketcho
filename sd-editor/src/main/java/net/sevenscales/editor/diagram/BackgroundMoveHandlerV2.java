package net.sevenscales.editor.diagram;

import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;

import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.Tools;
import net.sevenscales.editor.api.event.BackgroundMoveStartedEvent;
import net.sevenscales.editor.api.event.PinchZoomEvent;
import net.sevenscales.editor.api.event.PinchZoomEventHandler;
import net.sevenscales.editor.diagram.utils.GridUtils;
import net.sevenscales.editor.gfx.domain.IGraphics;
import net.sevenscales.editor.gfx.domain.IGroup;
import net.sevenscales.editor.gfx.domain.MatrixPointJS;
import net.sevenscales.editor.gfx.domain.OrgEvent;


public class BackgroundMoveHandlerV2 implements MouseDiagramHandler, IBackgroundMoveHandler {
	private static SLogger logger = SLogger.createLogger(BackgroundMoveHandlerV2.class);

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

  public BackgroundMoveHandlerV2(List<Diagram> diagrams, ISurfaceHandler surface) {
    this.diagrams = diagrams;
    this.surface = surface;
    
    listenPinchZoom();
    if (!surface.isLibrary()) {
      // do not move library background
      init(surface.getElement(), this);
    }

  }

  private void handleBackgroundMove(
    int deltaX,
    int deltaY
  ) {
    move(deltaX, deltaY);
  }

  private native void init(
    com.google.gwt.user.client.Element el,
    BackgroundMoveHandlerV2 me
  )/*-{

    function handler(data) {
      me.@net.sevenscales.editor.diagram.BackgroundMoveHandlerV2::handleBackgroundMove(II)(data.deltaX, data.deltaY)
    }

    $wnd.ReactEventStream.register(
      "bgmove",
      handler
    )

  }-*/;

  private void listenPinchZoom() {
  	surface.getEditorContext().getEventBus().addHandler(PinchZoomEvent.TYPE, new PinchZoomEventHandler() {
			@Override
			public void on(PinchZoomEvent event) {
		  	// cancel background move to prevent side effects, e.g. pinch zoom
		  	// 1. one finger on board => background move started
		  	// 2. two finer on board => background move should be canceled
        // 3. if not canceled will move background based on first finger if released appropriately
        cancelBackgroundMove();
			}
		});
  }
  
  public void cancelBackgroundMove() {
    mouseDown = false;
    backgroundMouseDown = false;
    backgroundMoving = false;
  }

  @Override
	public boolean onMouseDown(OrgEvent event, Diagram sender, MatrixPointJS point, int keys) {
    return false;
  }

  @Override
  public void onMouseEnter(OrgEvent event, Diagram sender, MatrixPointJS point) {
  }

  @Override
  public void onMouseLeave(Diagram sender, MatrixPointJS point) {
  }

  @Override
  public void onMouseMove(OrgEvent event, Diagram sender, MatrixPointJS point) {
  }

  private void move(int deltaX, int deltaY) {
    IGroup layer = surface.getRootLayer();

    int dx = layer.getTransformX() - deltaX;
    int dy = layer.getTransformY() - deltaY;

    if (surface.isVerticalDrag()) {
      dx = 0;
    }

    surface.setTransform(dx, dy);
    if (cachedEditor == null) {
      cachedEditor = getEditor();
    }
    moveBgImage(cachedEditor, dx, dy);
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
    surface.getElement().addClassName("grabbing");
  }

  public boolean backgroundMoveInitialContitionOk() {
    return currentSender == null && mouseDown && backgroundMouseDown;
  }

  public boolean backgroundMoveIsOn() {
		return backgroundMoving;
	}

	public void onMouseUp(Diagram sender, MatrixPointJS point, int keys) {
//  	complexElementHandler.showComplexElements(diagrams);
    if (backgroundMoving) {
      notifyBackgroundMoveEnd(surface.getRootLayer().getContainer());
    }

    clear();
  }

  private native void notifyBackgroundMoveEnd(com.google.gwt.core.client.JavaScriptObject group)/*-{
    $wnd.globalStreams.backgroundMoveStream.push({
      type:'move-end',
      matrix: group.getTransform()
    })
  }-*/;

  private void clear() {
    currentSender = null;
    backgroundMouseDown = true;
    mouseDown = false;
    backgroundMoving = false;
    surface.getElement().removeClassName("grabbing");
  }
  
  @Override
  public void onTouchStart(OrgEvent event, Diagram sender, MatrixPointJS point) {
  }
  
  @Override
  public void onTouchMove(OrgEvent event, Diagram sender, MatrixPointJS point) {
  }
  
  @Override
  public void onTouchEnd(Diagram sender, MatrixPointJS point) {
  	// TODO Auto-generated method stub
  }
}
