package net.sevenscales.editor.content.ui;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.ui.SimplePanel;
import java.util.ArrayList;
import java.util.List;
import net.sevenscales.domain.constants.Constants;
import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.EditorContext;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.IBirdsEyeView;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.event.PinchZoomEvent;
import net.sevenscales.editor.api.event.SurfaceScaleEvent;
import net.sevenscales.editor.api.event.SurfaceScaleEventHandler;
import net.sevenscales.editor.api.event.pointer.PointerDownEvent;
import net.sevenscales.editor.api.event.pointer.PointerDownHandler;
import net.sevenscales.editor.api.event.pointer.PointerEvent;
import net.sevenscales.editor.api.event.pointer.PointerEventsSupport;
import net.sevenscales.editor.api.event.pointer.PointerMoveEvent;
import net.sevenscales.editor.api.event.pointer.PointerMoveHandler;
import net.sevenscales.editor.api.event.pointer.PointerUpEvent;
import net.sevenscales.editor.api.event.pointer.PointerUpHandler;
import net.sevenscales.editor.api.impl.TouchHelpers;
import net.sevenscales.editor.content.utils.EffectHelpers;
import net.sevenscales.editor.diagram.utils.UiUtils;



public class ScaleSlider implements IScaleSlider, SurfaceScaleEventHandler {
	private static SLogger logger = SLogger.createLogger(ScaleSlider.class);

	private static final double TRESHOLD = 5;

	private EditorContext editorContext;
	private ISurfaceHandler surface;
	private int currentIndex = Constants.ZOOM_DEFAULT_INDEX;
	private double currentDistance;

  private SimplePanel scaleSlider;
	private SimplePanel innerScaleSlider;
	private BirdsEye birdsEye;
	private int deltaSum = 0;
	private boolean wheel;
  private boolean fireEvent = true;
  private List<PointerSnapShot> pointerEvents;
  private int middleX;
  private int middleY;

  private static class PointerSnapShot {
    int pointerId;
    int clientX;
    int clientY;

    PointerSnapShot(
      int pointerId,
      int clientX,
      int clientY
    ) {
      this.pointerId = pointerId;
      this.clientX = clientX;
      this.clientY = clientY;
    }
  }

	public ScaleSlider(ISurfaceHandler surface) {
		this.surface = surface;
		this.editorContext = surface.getEditorContext();
		
		// just pinch is used
		createVisibleSlider();
    innerScaleSlider.setVisible(!UiUtils.isMobile());
    
    if (PointerEventsSupport.isSupported()) {
      supportPointerEvents();
    } else {
      supportTouchEvents();
    }
		
		_initMouseWheel(surface.getElement(), this);
		
//		new ShowHideHelpers(scaleSlider, innerScaleSlider, 6000);
		birdsEye = new BirdsEye(surface, editorContext, this);

		surface.getEditorContext().getEventBus().addHandler(SurfaceScaleEvent.TYPE, this);

		listen(this);
  }

  private void supportPointerEvents() {
    if (pointerEvents == null) {
      pointerEvents = new ArrayList<PointerSnapShot>();
    }

    surface.addDomHandler(new PointerDownHandler(){
      @Override
      public void onPointerDown(PointerDownEvent event) {
        addPointerEvent(event);

        if (pointerEvents.size() == 2) {
          int x1 = pointerEvents.get(0).clientX;
          int y1 = pointerEvents.get(0).clientY;
          int x2 = pointerEvents.get(1).clientX;
          int y2 = pointerEvents.get(1).clientY;

          double distance = TouchHelpers.distance(
            x1,
            y1,
            x2,
            y2
          );

          startPinching(distance);
        }
      }
    }, PointerDownEvent.getType());

    surface.addDomHandler(new PointerMoveHandler(){
      @Override
      public void onPointerMove(PointerMoveEvent event) {
        if (pointerEvents.size() == 2) {
          replacePointerEvent(event);

          int x1 = pointerEvents.get(0).clientX;
          int y1 = pointerEvents.get(0).clientY;
          int x2 = pointerEvents.get(1).clientX;
          int y2 = pointerEvents.get(1).clientY;

          double distance = TouchHelpers.distance(
            x1,
            y1,
            x2,
            y2
          );

          updateMiddlePoint(x1, y1, x2, y2);
          pinch(distance);
        }
      }
    }, PointerMoveEvent.getType());

    surface.addDomHandler(new PointerUpHandler() {
      @Override
      public void onPointerUp(PointerUpEvent event) {
        // Debug.log("pinch pointer up: ", event.getPointerId());

        if (pointerEvents.size() == 2) {
          endPinch();
        }

        // better to remove all to make sure that memory is not reserved
        pointerEvents.clear();
      }
    }, PointerUpEvent.getType());
  }

  private void updateMiddlePoint(
    int x1, 
    int y1,
    int x2,
    int y2
  ) {
    middleX = (x1 + x2) / 2;
    middleY = (y1 + y2) / 2;
  }

  private void addPointerEvent(PointerEvent event) {
    pointerEvents.add(new PointerSnapShot(
      event.getPointerId(),
      event.getClientX(),
      event.getClientY()
    ));
  }

  private void replacePointerEvent(PointerEvent event) {
    for (int i = 0; i < pointerEvents.size(); ++i) {
      if (pointerEvents.get(i).pointerId == event.getPointerId()) {
        pointerEvents.set(i, new PointerSnapShot(
          event.getPointerId(),
          event.getClientX(),
          event.getClientY()
        ));
        return;
      }
    }
  }

  private void supportTouchEvents() {
		surface.addTouchStartHandler(new TouchStartHandler() {
			@Override
			public void onTouchStart(TouchStartEvent event) {
				if (freehandMode() || 
						event.getTouches().length() != 2) {
					// handle only pinch
					return;
        }
        
        double distance = TouchHelpers.distance(event.getTouches().get(0), event.getTouches().get(1));

				startPinching(distance);
			}
		});
		
		surface.addTouchMoveHandler(new TouchMoveHandler() {
			@Override
			public void onTouchMove(TouchMoveEvent event) {
				if (freehandMode() || 
						event.getTouches().length() != 2) {
					// handle only pinch
					return;
        }
        
        event.preventDefault();
        
        Touch t1 = event.getTouches().get(0);
        Touch t2 = event.getTouches().get(1);

        double distance = TouchHelpers.distance(t1, t2);

        updateMiddlePoint(
          t1.getClientX(),
          t1.getClientY(), 
          t2.getClientX(),
          t2.getClientY()
        );

        pinch(distance);
			}
		});
		
		surface.addTouchEndHandler(new TouchEndHandler() {
			@Override
			public void onTouchEnd(TouchEndEvent event) {
				endPinch();
			}
		});
  }

	@Override
	public void on(SurfaceScaleEvent event) {
    if (event.isResetScale()) {
      // reset scale only sets default zoom index
      updateSliderState(Constants.ZOOM_DEFAULT_INDEX);
    } else {
      updateSliderState(event.getScaleFactor());
    }
	}

	private void updateSliderState(int index) {
		// this is just to set slider to correct zoom value
		fireEvent = false;
		scaleToIndex(index);
	}

	private void handlMouseWheel(int delta) {

		boolean up = delta < 0;
		boolean down = delta > 0;
		boolean change = false;

		deltaSum += delta;
		boolean update = deltaSum % 2 == 0;

    // ST 11.2.2022: performance improvements
    // after Chrome 97, skip some (3) steps to have
    // faster zoom experience though it jumps a bit
    // rolled back, because now it is too fast on Mac OS
    // on Windows it probably should be 3.
		int index = currentIndex;
		if (update && up && (index - 1 ) >= 0) {
			currentIndex = index - 1;
		} else if (update && down && (index + 1) < Constants.ZOOM_FACTORS.length) {
			currentIndex = index + 1;
		}

		scaleAndSlide(currentIndex, index, true);
	}

	private native void _initMouseWheel(com.google.gwt.user.client.Element el, ScaleSlider me)/*-{

		function mouseWheelHandler(e) {
			// old IE support
			var e = window.event || e;
			var delta = Math.max(-1, Math.min(1, (e.wheelDelta || -e.detail || -e.deltaY)))

			// ST 11.1.2017: win10 firefox has smaller than 1 or bigger than -1 values
			// make sure those are exact 1 or -1
			if (delta < 0) {
				delta = -1
			} else if (delta > 0) {
				delta = 1
      }
      
      // ST 27.11.2019: Prevent surface element area two finger browser zoom
      // on MacOS
      e.preventDefault()

			me.@net.sevenscales.editor.content.ui.ScaleSlider::handlMouseWheel(I)(delta)
		}

		if (el.addEventListener) {
			// IE9, Chrome, Safari, Opera, Firefox
			// Standard event
			el.addEventListener("wheel", mouseWheelHandler, false)
		}	else {
			// IE 6/7/8
			el.attachEvent("onmousewheel", mouseWheelHandler)
		}
	}-*/;


	public IBirdsEyeView getBirdsEyeView() {
		return birdsEye;
	}

	private native void listen(ScaleSlider me)/*-{
		$wnd.globalStreams.scaleResetStream.onValue(function() {
			me.@net.sevenscales.editor.content.ui.ScaleSlider::scaleToIndex(I)(@net.sevenscales.domain.constants.Constants::ZOOM_DEFAULT_INDEX)
		})

		$wnd.globalStreams.scaleRestoreStream.onValue(function(value) {
			me.@net.sevenscales.editor.content.ui.ScaleSlider::scaleToFactor(D)(value)
    })
    
    $wnd.globalStreams.userMessagePropsStream.onValue(function(value) {
      me.@net.sevenscales.editor.content.ui.ScaleSlider::userMessagePropsStream(I)(value)
    })
  }-*/;
  
  private void userMessagePropsStream(int extraHeight) {
    this.scaleSlider.getElement().getStyle().setTop(58 + extraHeight, Unit.PX);
  }

	private boolean freehandMode() {
		return surface.getEditorContext().isTrue(EditorProperty.FREEHAND_MODE);		
	}
	
  public void scaleToIndex(int index) {
    currentIndex = index;
    _setSliderValue(innerScaleSlider.getElement(), currentIndex);
  }

  public void scaleToFactor(double value) {
  	scaleToIndex(findFactorIndex(value));
  }

  private int findFactorIndex(double value) {
		int i = 0;

		for (double f : Constants.ZOOM_FACTORS) {
			if (f == value) {
				return i;
			}
			++i;
		}

		return Constants.ZOOM_DEFAULT_INDEX;
  }

	private void createVisibleSlider() {
		this.scaleSlider = new SimplePanel();
		scaleSlider.setStyleName("scaleSlider");
		scaleSlider.getElement().setTitle("Zoom Out - | Zoom In +");
    EffectHelpers.tooltip(scaleSlider.getElement(), "right");

		this.innerScaleSlider = new SimplePanel();
		innerScaleSlider.getElement().setId("innerScaleSlider");
		scaleSlider.setWidget(innerScaleSlider);
		
		editorContext.registerAndAddToRootPanel(scaleSlider);
		logger.debug("innerScaleSlider.getElement() {}", innerScaleSlider.getElement());
		_slideElement(innerScaleSlider.getElement(), this);
	}

	/**
	 * Halts surface touch events, otherwise e.g. background move will continue
	 * using the same pinch and surface jumps randomly.
	 */
	private void startPinching(double distance) {
    EffectHelpers.fadeOut(innerScaleSlider.getElement());
    currentDistance = distance;
		firePinchStarted();
	}
	
	private void firePinchStarted() {
		surface.getEditorContext().getEventBus().fireEvent(new PinchZoomEvent(true));
	}

	private void endPinch() {
		EffectHelpers.fadeIn(innerScaleSlider.getElement());
    surface.getEditorContext().getEventBus().fireEvent(new PinchZoomEvent(false));
	}
	
	private void pinch(double distance) {
		boolean exceedTreshold = Math.abs(currentDistance - distance) > TRESHOLD; 
		if (exceedTreshold) {
			int index = currentIndex;
			if (distance <= currentDistance && (index -1 ) >= 0) {
				currentIndex = index - 1;
			} else if (distance > currentDistance && (index + 1) < Constants.ZOOM_FACTORS.length) {
				currentIndex = index + 1;
			}
			currentDistance = distance;

			scaleAndSlide(currentIndex, index, false);
		}
	}

	private void scaleAndSlide(int currentIndex, int index, boolean wheel) {
		if (currentIndex != index && currentIndex < Constants.ZOOM_FACTORS.length && currentIndex >= 0) {
			logger.debug("set slider to value {}...", currentIndex);
			this.wheel = wheel;
			_setSliderValue(innerScaleSlider.getElement(), currentIndex);
			this.wheel = false;

			// editorContext.getEventBus().fireEvent(new SurfaceScaleEvent(currentIndex, wheel));
		}
	}
	
	@Override
  public void scale(int index) {
  	if (fireEvent) {

      if (wheel) {
        editorContext.getEventBus().fireEvent(new SurfaceScaleEvent(index, wheel));
      } else {
        editorContext.getEventBus().fireEvent(
          new SurfaceScaleEvent(index,
          false,
          middleX,
          middleY
        ));
      }
			// unfocus everything, or otherwise shortcuts are not working
			// that use key press events, focus steals press events and should
			// handle keydown, but e.g. +, - signs have different keycodes on 
			// different browsers
			_unfocusEverything();
  	}
  	fireEvent = true;
  }

  private native void _unfocusEverything()/*-{
		if ("activeElement" in $wnd.document) {
    	$wnd.document.activeElement.blur()
    	$wnd.globalStreams.closeEditorStream.push()
		}
  }-*/;

	@Override
  public int getSliderValue() {
	  return _sliderValue(innerScaleSlider.getElement());
	}
  
  private native int _sliderValue(JavaScriptObject element)/*-{
    var jq = null;
    if (typeof $wnd.jq172 == "function") {
      jq = $wnd.jq172;
    } else if (typeof $wnd.jQuery == "function") {
      jq = $wnd.jQuery;
    }
  	return jq(element).slider("value");
  }-*/;
  
  private native void _setSliderValue(JavaScriptObject element, int index)/*-{
    var jq = null;
    if (typeof $wnd.jq172 == "function") {
      jq = $wnd.jq172;
    } else if (typeof $wnd.jQuery == "function") {
      jq = $wnd.jQuery;
    }
    
		jq(element).slider('value', index);
  }-*/;

  private native void _slideElement(JavaScriptObject element, ScaleSlider me)/*-{
  	var _jquery = null
  	if (typeof $wnd.jq172 == "function") {
  		_jquery = $wnd.jq172

	  	$wnd.jq172(element).slider({ 
	  		orientation: 'vertical', 
	  		max: @net.sevenscales.domain.constants.Constants::ZOOM_FACTORS.length - 1, 
	  		value: @net.sevenscales.domain.constants.Constants::ZOOM_DEFAULT_INDEX,
	  		change: function(e, ui) {
	  				me.@net.sevenscales.editor.content.ui.ScaleSlider::scale(I)(ui.value);
	        }
	  		});
  	} else if (typeof $wnd.jQuery == "function")  {
  		_jquery = $wnd.jQuery
	  	$wnd.jQuery(element).slider({ 
	  		orientation: 'vertical', 
	  		max: @net.sevenscales.domain.constants.Constants::ZOOM_FACTORS.length - 1, 
	  		value: @net.sevenscales.domain.constants.Constants::ZOOM_DEFAULT_INDEX,
	  		change: function(e, ui) {
	  				me.@net.sevenscales.editor.content.ui.ScaleSlider::scale(I)(ui.value);
	        }
	  		});
  	}

		var $e = _jquery(element)
  	var $a = $e.find('a')
  	if ($a) {
  		$e.attr('tabIndex', '-1')
  		$a.attr('tabIndex', '-1')
  		$a.attr('href', '')
  	}
  	
  // 	if (typeof $wnd.jq172 == "function") {
	 //  	$wnd.jq172(element).on( "slidechange", function(event, ui) {
	 //  		me.@net.sevenscales.editor.content.ui.ScaleSlider::scale(I)
		// 	    ($wnd.jQuery(element).slider("value"));
		// 	});
  // 	} else if (typeof $wnd.jQuery == "function") $wnd.jQuery(element).on( "slidechange", function(event, ui) {
  // 		me.@net.sevenscales.editor.content.ui.ScaleSlider::scale(I)
		//     ($wnd.jQuery(element).slider("value"));
		// });
	}-*/;

  public void reset() {
    scaleToIndex(Constants.ZOOM_DEFAULT_INDEX);
  }

}
