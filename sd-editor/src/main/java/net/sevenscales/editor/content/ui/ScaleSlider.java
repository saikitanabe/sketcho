package net.sevenscales.editor.content.ui;

import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.domain.constants.Constants;
import net.sevenscales.editor.api.EditorContext;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.event.PinchZoomStartedEvent;
import net.sevenscales.editor.api.event.SurfaceScaleEvent;
import net.sevenscales.editor.api.impl.TouchHelpers;
import net.sevenscales.editor.content.utils.EffectHelpers;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.ui.SimplePanel;

public class ScaleSlider implements IScaleSlider {
	private static SLogger logger = SLogger.createLogger(ScaleSlider.class);

	private static final double TRESHOLD = 40;

	private EditorContext editorContext;
	private ISurfaceHandler surface;
	private int currentIndex = Constants.ZOOM_DEFAULT_INDEX;
	private double currentDistance;

	private SimplePanel innerScaleSlider;

	public ScaleSlider(ISurfaceHandler surface) {
		this.surface = surface;
		this.editorContext = surface.getEditorContext();
		
		// just pinch is used
		createVisibleSlider();
		innerScaleSlider.setVisible(!TouchHelpers.isSupportsTouch());
		
		surface.addTouchStartHandler(new TouchStartHandler() {
			@Override
			public void onTouchStart(TouchStartEvent event) {
				if (freehandMode() || 
						event.getTouches().length() != 2) {
					// handle only pinch
					return;
				}
				startPinching(event);
			}
		});
		
		surface .addTouchMoveHandler(new TouchMoveHandler() {
			@Override
			public void onTouchMove(TouchMoveEvent event) {
				if (freehandMode() || 
						event.getTouches().length() != 2) {
					// handle only pinch
					return;
				}
				
				pinch(event);
			}
		});
		
		surface.addTouchEndHandler(new TouchEndHandler() {
			@Override
			public void onTouchEnd(TouchEndEvent event) {
				endPinch();
			}
		});
		
//		new ShowHideHelpers(scaleSlider, innerScaleSlider, 6000);
		new BirdsEye(surface, editorContext, this);
	}

	private boolean freehandMode() {
		return surface.getEditorContext().isTrue(EditorProperty.FREEHAND_MODE);		
	}
	
  public void scaleToIndex(int index) {
    currentIndex = index;
    _setSliderValue(innerScaleSlider.getElement(), currentIndex);
  }

	private void createVisibleSlider() {
		final SimplePanel scaleSlider = new SimplePanel();
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
	private void startPinching(TouchStartEvent event) {
		EffectHelpers.fadeIn(innerScaleSlider.getElement());

		currentDistance = TouchHelpers.distance(event.getTouches().get(0), event.getTouches().get(1));
		firePinchStarted();
	}
	
	private void firePinchStarted() {
		surface.getEditorContext().getEventBus().fireEvent(new PinchZoomStartedEvent());
	}

	private void endPinch() {
		EffectHelpers.fadeOut(innerScaleSlider.getElement());
	}
	
	private void pinch(TouchMoveEvent event) {
		double distance = TouchHelpers.distance(event.getTouches().get(0), event.getTouches().get(1));
		boolean exceedTreshold = Math.abs(currentDistance - distance) > TRESHOLD; 
		if (exceedTreshold) {
			int index = currentIndex;
			if (distance <= currentDistance && (index -1 ) >= 0) {
				currentIndex = index - 1;
			} else if (distance > currentDistance && (index + 1) <= Constants.ZOOM_FACTORS.length) {
				currentIndex = index + 1;
			}
			currentDistance = distance;
			
			if (currentIndex != index && currentIndex <= Constants.ZOOM_FACTORS.length && currentIndex >= 0) {
				logger.debug("set slider to value {}...", currentIndex);
				_setSliderValue(innerScaleSlider.getElement(), currentIndex);
				editorContext.getEventBus().fireEvent(new SurfaceScaleEvent(currentIndex));
			}
		}
	}
	
	@Override
  public void scale(int index) {
		editorContext.getEventBus().fireEvent(new SurfaceScaleEvent(index));
  }

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
  	if (typeof $wnd.jq172 == "function") {
	  	$wnd.jq172(element).slider({ 
	  		orientation: 'vertical', 
	  		max: @net.sevenscales.domain.constants.Constants::ZOOM_FACTORS.length - 1, 
	  		value: @net.sevenscales.domain.constants.Constants::ZOOM_DEFAULT_INDEX,
	  		change: function(e, ui) {
	  				me.@net.sevenscales.editor.content.ui.ScaleSlider::scale(I)(ui.value);
	        }
	  		});
  	} else if (typeof $wnd.jQuery == "function")  {
	  	$wnd.jQuery(element).slider({ 
	  		orientation: 'vertical', 
	  		max: @net.sevenscales.domain.constants.Constants::ZOOM_FACTORS.length - 1, 
	  		value: @net.sevenscales.domain.constants.Constants::ZOOM_DEFAULT_INDEX,
	  		change: function(e, ui) {
	  				me.@net.sevenscales.editor.content.ui.ScaleSlider::scale(I)(ui.value);
	        }
	  		});
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
