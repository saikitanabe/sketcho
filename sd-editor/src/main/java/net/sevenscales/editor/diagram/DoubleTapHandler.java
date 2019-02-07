package net.sevenscales.editor.diagram;

import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.RootPanel;

import net.sevenscales.domain.utils.Debug;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.LongPressHandler;
import net.sevenscales.editor.api.event.pointer.PointerUpEvent;
import net.sevenscales.editor.api.event.pointer.PointerUpHandler;
import net.sevenscales.editor.api.event.pointer.PointerEventsSupport;
import net.sevenscales.editor.api.event.pointer.PointerMoveEvent;
import net.sevenscales.editor.api.event.pointer.PointerMoveHandler;

class DoubleTapHandler implements
  DiagramSelectionHandler,
  PointerUpHandler,
  PointerMoveHandler {
  private boolean itsDoubleTap;
  private boolean canceled;
  private IDoubleTapHandler handler;
  private ISurfaceHandler surface;
  private LongPressHandler longPressHandler;
  private static final int DOUBLETAP_MILLIS = 300;
  private static final int DOUBLETAP_POSITION_MAX_DIFF = 10;

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
      longPressHandler = new LongPressHandler(surface);
      selectionHandler.addDiagramSelectionHandler(this);

      handleDoubleTap();
      // handleDoubleTap(surface.getElement(), this, DOUBLETAP_MILLIS);
      // handleMouseDoubleClick(surface.getElement(), this);
    }

  }

  boolean isDoubleTap() {
    return itsDoubleTap;
  }

  void resetState() {
    Debug.log("double tap reset state...");

    itsDoubleTap = false;
    tapped = false;
    timer.cancel();
  }

	private void doubleTap(int x, int y, boolean shiftKey, String targetId) {
		// logger.debug("doubleTap...");
    // cannot check connect mode, or will not show property editor
    
    longPressHandler.cancel();
		handler.handleDoubleTap(x, y, shiftKey, targetId);
	}

	/**
	* doubleClick should not prevent next mouse up! Thats' why separated.
	*/
	private void doubleClick(int x, int y, boolean shiftKey, String targetId) {
		// logger.debug("doubleClick...");
		// cannot check connect mode, or will not show property editor
		handler.handleDoubleTap(x, y, shiftKey, targetId);
  }
  
  private void handleDoubleTap() {
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
      console.log('tap prevent touch default')
    })
  }-*/;

  private void supportPointerEvents() {
    surface.addDomHandler(this, PointerUpEvent.getType());
    surface.addDomHandler(this, PointerMoveEvent.getType());
  }

  private void supportMouseTouchEvents() {

  }

  private boolean tapped = false;

  private Timer timer = new Timer() {
    @Override
    public void run() {
      tapped = false;
      itsDoubleTap = false;
      Debug.log("single tap...");
    }
  };
private int tapX;
private int tapY;

  @Override
	public void onPointerUp(PointerUpEvent event) {
    mouseDown(event);
  }

	@Override
	public void onPointerMove(PointerMoveEvent event) {
		// resetState();
	}
  
  private void mouseDown(MouseEvent event) {
    itsDoubleTap = false;

    if (!tapped) {
      tapped = true;

      tapX = event.getClientX();
      tapY = event.getClientY();

      timer.schedule(DOUBLETAP_MILLIS);
      Debug.log("tap: timer scheduled...");
    } else {
      // it is a double tap

      int diffx = event.getClientX() - tapX;
      int diffy = event.getClientY() - tapY;

      if (Math.abs(diffx) < DOUBLETAP_POSITION_MAX_DIFF &&
          Math.abs(diffy) < DOUBLETAP_POSITION_MAX_DIFF) {
        Debug.log("double tap...");
        timer.cancel(); // stop single tap
        tapped = false;
        itsDoubleTap = true;

        doubleTap(
          event.getClientX(),
          event.getClientY(),
          false,
          event.getRelativeElement().getId()
        );

        event.preventDefault();
      }
    }
  }

  private native void handleDoubleTap(Element elem, DoubleTapHandler me, int millis)/*-{
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

		$wnd.$(elem).on("touchstart",function(e){
	    if (!tapped){ //if tap is not set, set up single tap
	      tapped = setTimeout(function(){
	        tapped = null
	        //insert things you want to do when single tapped
	      }, millis)   //wait 300ms then run single click code
	    } else {    //tapped within 300ms of last tap. double tap
	      clearTimeout(tapped) //stop single tap callback
	      tapped = null
	      //insert things you want to do when double tapped
	      // console.log('double tap', e)

				var touches = e.originalEvent.touches
				if (touches && touches.length == 1) {
		      me.@net.sevenscales.editor.diagram.DoubleTapHandler::doubleTap(IIZLjava/lang/String;)(touches[0].clientX, touches[0].clientY, false, e.target.id);
				}
	    }

	    e.preventDefault()
		})
  }-*/;
  
	private native void handleMouseDoubleClick(Element e, DoubleTapHandler me)/*-{
		$wnd.$(e).on('dblclick', function(e) {
			e.stopPropagation()
			e.preventDefault()

			me.@net.sevenscales.editor.diagram.DoubleTapHandler::doubleClick(IIZLjava/lang/String;)(e.clientX, e.clientY, false, e.target.id);
		})
  }-*/;
  
  private void cancelDoubleTap() {
    this.canceled = true;
  }

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