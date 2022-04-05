package net.sevenscales.editor.diagram;

import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.user.client.Timer;

import net.sevenscales.domain.utils.Debug;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.ILongPressHandler;
import net.sevenscales.editor.api.LongPressHandler;
import net.sevenscales.editor.api.LongPressHandlerV2;
import net.sevenscales.editor.api.ReactAPI;
import net.sevenscales.editor.api.event.pointer.PointerEventsSupport;
import net.sevenscales.editor.api.event.pointer.PointerMoveEvent;
import net.sevenscales.editor.api.event.pointer.PointerMoveHandler;
import net.sevenscales.editor.api.event.pointer.PointerUpEvent;
import net.sevenscales.editor.api.event.pointer.PointerUpHandler;

class DoubleTapHandler implements
  DiagramSelectionHandler,
  PointerUpHandler,
  PointerMoveHandler {
  private boolean itsDoubleTap;
  private IDoubleTapHandler handler;
  private ISurfaceHandler surface;
  private ILongPressHandler longPressHandler;
  private static final int DOUBLETAP_MILLIS = 300;
  private static final int DOUBLETAP_POSITION_MAX_DIFF = 20;

  interface IDoubleTapHandler {
    void handleDoubleTap(int x, int y, boolean shiftKey, String targetId);
  }

  DoubleTapHandler(
    boolean editable,
    ISurfaceHandler surface,
    SelectionHandler selectionHandler,
    IDoubleTapHandler handler
  ) {
    this.surface = surface;
    this.handler = handler;

    if (editable) {
      longPressHandler = createLongPressHandler();
      selectionHandler.addDiagramSelectionHandler(this);

      initDoubleTap();
    }

    _init(this);
  }

  private native void _init(DoubleTapHandler me)/*-{
    $wnd.globalStreams.navigationUpdatedStream.onValue(function(value) {
      me.@net.sevenscales.editor.diagram.DoubleTapHandler::updateNavigation()();
    })
  }-*/;

  private void updateNavigation() {
    longPressHandler = createLongPressHandler();
  }

  private ILongPressHandler createLongPressHandler() {

    if (longPressHandler != null) {
      longPressHandler.unregister();
    }

    if (ReactAPI.isNav2()) {
      return new LongPressHandlerV2(surface);
    }

    return new LongPressHandler(surface);
  }

  boolean isDoubleTap() {
    return itsDoubleTap;
  }

  void resetState() {
    itsDoubleTap = false;
    tapped = false;
    timer.cancel();
  }

	private void doubleTouchTap(
    int tapX,
    int tapY,
    int tapClientHeight,
    int doubleTapX,
    int doubleTapY,
    int doubleTapClientHeight,
    boolean shiftKey,
    String targetId
  ) {

    // height of soft keyboard
    int softKeyboard = Math.abs(doubleTapClientHeight - tapClientHeight);

    if (!checkPass(tapX - doubleTapX, tapY - (softKeyboard + doubleTapY))) {
      return;
    }

    // iOS: use tap (not double tap) position to create shape
    // when soft keyboard was visible
    doubleTap(
      tapX,
      tapY,
      shiftKey,
      targetId
    );
  }
	private void doubleTap(int tapX, int tapY, boolean shiftKey, String targetId) {
		Debug.log("doubleTap...");
    // cannot check connect mode, or will not show property editor
    itsDoubleTap = true;
    
    longPressHandler.cancel();
    handler.handleDoubleTap(tapX, tapY, shiftKey, targetId);
    
    timer.schedule(200);
	}

	/**
	* doubleClick should not prevent next mouse up! Thats' why separated.
	*/
	private void doubleClick(int x, int y, boolean shiftKey, String targetId) {
		// logger.debug("doubleClick...");
		// cannot check connect mode, or will not show property editor
		handler.handleDoubleTap(x, y, shiftKey, targetId);
  }
  
  private void initDoubleTap() {
    if (PointerEventsSupport.isSupported()) {
      disableTouch(this.surface.getElement());
      supportPointerEvents();
    } else {
      supportMouseTouchEvents();
    }
  }

  /**
   * Need to prevent Windows 10 touch back navigation.
   * Otherwise, pointermove events will stop after back navigation
   * gesture steps in.
   */
  private native void disableTouch(Element elem)/*-{
    $wnd.$(elem).on("touchstart",function(e){
      e.preventDefault()
      // console.log('tap prevent touch default')
    })
  }-*/;

  private void supportPointerEvents() {
    surface.addDomHandler(this, PointerUpEvent.getType());
    surface.addDomHandler(this, PointerMoveEvent.getType());
  }

  private void supportMouseTouchEvents() {
    initDoubleTap(surface.getElement(), this, DOUBLETAP_MILLIS);
    handleMouseDoubleClick(surface.getElement(), this);
  }

  private boolean tapped = false;

  private Timer timer = new Timer() {
    @Override
    public void run() {
      tapped = false;
      itsDoubleTap = false;
      // Debug.log("single tap...");
    }
  };
  private int tapX;
  private int tapY;

  @Override
	public void onPointerUp(PointerUpEvent event) {
    handlePointerEvent(event);
  }

	@Override
	public void onPointerMove(PointerMoveEvent event) {
		// resetState();
	}
  
  private void handlePointerEvent(MouseEvent event) {
    itsDoubleTap = false;

    if (!tapped) {
      tapped = true;

      tapX = event.getClientX();
      tapY = event.getClientY();

      timer.schedule(DOUBLETAP_MILLIS);
      // Debug.log("tap: timer scheduled...");
    } else {
      // it is a double tap

      int diffx = event.getClientX() - tapX;
      int diffy = event.getClientY() - tapY;

      if (checkPass(diffx, diffy)) {

        // Debug.log("double tap..." + event.isShiftKeyDown());

        timer.cancel(); // stop single tap
        tapped = false;
        itsDoubleTap = true;

        doubleTap(
          event.getClientX(),
          event.getClientY(),
          event.isShiftKeyDown(),
          getTargetId(event)
        );

        event.preventDefault();
      }
    }
  }

  private boolean checkPass(int diffx, int diffy) {
    return Math.abs(diffx) < DOUBLETAP_POSITION_MAX_DIFF &&
      Math.abs(diffy) < DOUBLETAP_POSITION_MAX_DIFF;
  }

  private String getTargetId(MouseEvent event) {
    if (Element.is(event.getNativeEvent().getEventTarget())) {
      return Element.as(event.getNativeEvent().getEventTarget()).getId();
    }

    return "";
  }

  /**
   * Used on iPad and Safari. Could be variated for safari and ios and not loaded at all on Chrome.
   */
  private native void initDoubleTap(Element elem, DoubleTapHandler me, int millis)/*-{
		// Hammer has performance problems on big boards
		// e.g. Macbook Air doesn't fire double tap at all
		// user reported bug

		// $wnd.Hammer(elem, {
		// 	preventDefault: true
		// }).on('doubletap', function(event) {
		// 	// console.log('handleDoubleTap', event)
		// 	if (event.gesture.center.clientX && event.gesture.center.clientY) {
		// 		event.stopPropagation()
		// 		event.preventDefault()
		// 		console.info('doubletap...')

		// 		me.@net.sevenscales.editor.diagram.DoubleTapHandler::doubleTap(IIZLjava/lang/String;)(event.gesture.center.clientX, event.gesture.center.clientY, event.gesture.srcEvent.shiftKey, event.target.id);
		// 	}
		// })

    var tapped = null
    var tapX = 0
    var tapY = 0
    var tapClientHeight = 0

		$wnd.$(elem).on("touchstart",function(e){
	    if (!tapped){ //if tap is not set, set up single tap
	      tapped = setTimeout(function(){
	        tapped = null
	        //insert things you want to do when single tapped
        }, millis)   //wait 300ms then run single click code
				var touches = e.originalEvent.touches
				if (touches && touches.length == 1) {
          tapX = touches[0].screenX
          tapY = touches[0].screenY
          tapClientHeight = $wnd.innerHeight

          // console.log('on tap: tapX', tapX, 'tapY', tapY, tapClientHeight, touches[0])
        }        
	    } else {    //tapped within 300ms of last tap. double tap
	      clearTimeout(tapped) //stop single tap callback
	      tapped = null
	      //insert things you want to do when double tapped
	      // console.log('double tap', e)

				var touches = e.originalEvent.touches
				if (touches && touches.length == 1) {
//          me.@net.sevenscales.editor.diagram.DoubleTapHandler::doubleTap(IIZLjava/lang/String;)(touches[0].clientX, touches[0].clientY, false, e.target.id);

          var doubleTapX = touches[0].screenX
          var doubleTapY = touches[0].screenY
          var doubleTapClientHeight = $wnd.innerHeight

          // console.log(
          //   'on double tap: tapX',
          //   tapX,
          //   'tapY',
          //   tapY,
          //   'tapClientHeight',
          //   tapClientHeight,
          //   'doubleTapX',
          //   doubleTapX,
          //   'doubleTapY',
          //   doubleTapY,
          //   'doubleTapClientHeight',
          //   doubleTapClientHeight
          // )

          me.@net.sevenscales.editor.diagram.DoubleTapHandler::doubleTouchTap(IIIIIIZLjava/lang/String;)(
            tapX,
            tapY,
            tapClientHeight,
            doubleTapX,
            doubleTapY,
            doubleTapClientHeight,
            false,
            e.target.id
          );
				}
	    }

      e.preventDefault()
		})
  }-*/;
  
  /**
   * Used on Safari that doesn't support pointer events.
   */
	private native void handleMouseDoubleClick(Element e, DoubleTapHandler me)/*-{
		$wnd.$(e).on('dblclick', function(e) {
			e.stopPropagation()
			e.preventDefault()

			me.@net.sevenscales.editor.diagram.DoubleTapHandler::doubleClick(IIZLjava/lang/String;)(e.clientX, e.clientY, false, e.target.id);
		})
  }-*/;
  
	@Override
	public void selected(List<Diagram> sender) {
    // cancelDoubleTap();
    longPressHandler.cancel();
	}

	@Override
	public void unselectAll() {
		
	}

	@Override
	public void unselect(Diagram sender) {
		
	}

}