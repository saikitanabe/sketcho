package net.sevenscales.editor.content.ui;

import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.BoardDimensions;
import net.sevenscales.editor.api.EditorContext;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.event.PinchZoomStartedEvent;
import net.sevenscales.editor.api.event.SurfaceScaleEvent;
import net.sevenscales.editor.api.impl.TouchHelpers;
import net.sevenscales.editor.content.utils.EffectHelpers;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.SimplePanel;

public class ScaleSlider {
	private static SLogger logger = SLogger.createLogger(ScaleSlider.class);

	private static final int MAX_INDEX = 11;
	private static final int DEFAULT_INDEX = 7;
	private static final double TRESHOLD = 40;

	private EditorContext editorContext;
	private ISurfaceHandler surface;
	private int currentIndex = DEFAULT_INDEX;
	private double currentDistance;

	private SimplePanel innerScaleSlider;

	private class BirdsEye {
		private int transformX = 0;
		private int transformY = 0;
		private int mousePosX;
		private int mousePosY;
		private double ratio = 0;
	
		private boolean birdsEyeDown = false;
		private HandlerRegistration moveRegistration;
		BirdsEye() {
			// do not handle undo/redo if property editor is open
			Event.addNativePreviewHandler(new NativePreviewHandler() {
			  @Override
			  public void onPreviewNativeEvent(NativePreviewEvent event) {
          NativeEvent ne = event.getNativeEvent();
			    if (!birdsEyeDown && event.getTypeInt() == Event.ONKEYDOWN && UIKeyHelpers.noMetaKeys(ne) && !ScaleSlider.this.editorContext.isTrue(EditorProperty.PROPERTY_EDITOR_IS_OPEN)) {
			      if (ne.getKeyCode() == 'Z' && UIKeyHelpers.allMenusAreClosed()) {
				      logger.debug("show birds eye view...");
				      BoardDimensions.resolveDimensions(surface.getDiagrams());
				      
				      transformX = surface.getRootLayer().getTransformX();
				      transformY = surface.getRootLayer().getTransformY();
				      int leftmost = BoardDimensions.getLeftmost();
				      int topmost = BoardDimensions.getTopmost();
				      int width = BoardDimensions.getWidth();
				      int height = BoardDimensions.getHeight();
				      
				      // int clientLeftMargin = 100;
				      // double clientWidth = Window.getClientWidth() - clientLeftMargin;
				      double clientWidth = Window.getClientWidth();
				      double clientHeight = Window.getClientHeight();
				      double ratioW = clientWidth / width;
				      double ratioH = clientHeight / height;
				      ratio = (ratioW < ratioH) ? ratioW : ratioH;
				      surface.scale((float)ratio);
				      logger.debug("clientWidth {} clientHeight {} width {} height {}", clientWidth, clientHeight, width, height);
				      logger.debug("ratio {} transformX {} transformY {} leftmost {} topmost {}", ratio, transformX, transformY, leftmost, topmost);

				      // int leftPosition = 40;
				      // surface.getRootLayer().setTransform((int)-(leftmost * ratio) + leftPosition, (int)-(topmost * ratio));
				      surface.getRootLayer().setTransform((int)-(leftmost * ratio), (int)-(topmost * ratio));

			      	birdsEyeDown = true;
			      	followMouse();
			      }
			    }

			    if (birdsEyeDown && event.getTypeInt() == Event.ONKEYUP && !ScaleSlider.this.editorContext.isTrue(EditorProperty.PROPERTY_EDITOR_IS_OPEN)) {
			      if (ne.getKeyCode() == 'Z') {
			      	scale(getSliderValue());
			      	birdsEyeDown = false;
				      // surface.getRootLayer().setTransform(mousePosX - BoardDimensions.getLeftmost(), mousePosY - BoardDimensions.getTopmost());
				      double clientWidth = Window.getClientWidth();
				      double clientHeight = Window.getClientHeight();
				      int leftmost = BoardDimensions.getLeftmost();
				      int topmost = BoardDimensions.getTopmost();
				      int width = BoardDimensions.getWidth();
				      int height = BoardDimensions.getHeight();

				      // int sign = mousePosX > 0 ? 1 : -1;
				      // int posx = (int) (Math.abs(mousePosX) + clientWidth/2) * sign;
				      int posx = -(int) (mousePosX / ratio - clientWidth / 2) - leftmost;
				      int posy = -(int) (mousePosY / ratio - clientHeight / 2) - topmost;
				      scaleToIndex(DEFAULT_INDEX);
				      // posx /= surface.getScaleFactor();
				      // posy /= surface.getScaleFactor();
				      // int posx = leftmost - mousePosX;

			      	logger.debug("posx {} posy {} ratio {} mousePosX {} mousePosY {} leftmost {} topmost {}", posx, posy, ratio, mousePosX, mousePosY, leftmost, topmost);

				      surface.getRootLayer().setTransform(posx, posy);

				      moveRegistration.removeHandler();
				      moveRegistration = null;
			      }
			    }
			  }
			});

			Event.addNativePreviewHandler(new NativePreviewHandler() {
			  @Override
			  public void onPreviewNativeEvent(NativePreviewEvent event) {
			    if (!birdsEyeDown && (event.getTypeInt() == Event.ONKEYPRESS) && !ScaleSlider.this.editorContext.isTrue(EditorProperty.PROPERTY_EDITOR_IS_OPEN) && UIKeyHelpers.allMenusAreClosed()) {
			      NativeEvent ne = event.getNativeEvent();
			      if (ne.getCharCode() == '+') { // compare using char code since key code is different on Firefox
			      	int val = getSliderValue() + 1;
	            logger.debug("zoom ++ {}", val);
			      	if (val <= MAX_INDEX) {
			      	  scaleToIndex(val);
			      	}
						}	
					}

					if (!birdsEyeDown && event.getTypeInt() == Event.ONKEYPRESS && !ScaleSlider.this.editorContext.isTrue(EditorProperty.PROPERTY_EDITOR_IS_OPEN) && UIKeyHelpers.allMenusAreClosed()) {
			      NativeEvent ne = event.getNativeEvent();
			      if (ne.getCharCode() == '-') { // compare using char code since key code is different on Firefox
			      	logger.debug("zoom --");
			      	int val = getSliderValue() - 1;
              logger.debug("zoom -- {}", val);
			      	if (val >= 0) {
                scaleToIndex(val);
			      	}
						}	
					}

				}
			});
		}
		
		NativePreviewHandler mouseMoveHandler = new NativePreviewHandler() {
		  public void onPreviewNativeEvent(final NativePreviewEvent event) {
		    final int eventType = event.getTypeInt();
		    switch (eventType) {
		      case Event.ONMOUSEMOVE:
		        mousePosX = event.getNativeEvent().getClientX();
		        mousePosY = event.getNativeEvent().getClientY();
		        break;
		      default:
		        // not interested in other events
		    }
		  }
		};

		private void followMouse() {
			logger.debug("followMouse...");
			moveRegistration = Event.addNativePreviewHandler(mouseMoveHandler);
		}
		
	}

	public ScaleSlider(ISurfaceHandler surface) {
		this.surface = surface;
		this.editorContext = surface.getEditorContext();
		
		// just pinch is used
		createVisibleSlider();
		innerScaleSlider.setVisible(!TouchHelpers.isSupportsTouch());
		
		surface.addTouchStartHandler(new TouchStartHandler() {
			@Override
			public void onTouchStart(TouchStartEvent event) {
				if (event.getTouches().length() != 2) {
					// handle only pinch
					return;
				}
				startPinching(event);
			}
		});
		
		surface .addTouchMoveHandler(new TouchMoveHandler() {
			@Override
			public void onTouchMove(TouchMoveEvent event) {
				if (event.getTouches().length() != 2) {
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
		new BirdsEye();
	}
	
  public void scaleToIndex(int index) {
    currentIndex = index;
    _setSliderValue(innerScaleSlider.getElement(), currentIndex);
  }

	private void createVisibleSlider() {
		final SimplePanel scaleSlider = new SimplePanel();
		scaleSlider.setStyleName("scaleSlider");
		scaleSlider.getElement().setTitle("Zoom Out - | Zoom In +");
    EffectHelpers.tooltip(scaleSlider.getElement(), "bottom");

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
			} else if (distance > currentDistance && (index + 1) <= MAX_INDEX) {
				currentIndex = index + 1;
			}
			currentDistance = distance;
			
			if (currentIndex != index && currentIndex <= MAX_INDEX && currentIndex >= 0) {
				logger.debug("set slider to value {}...", currentIndex);
				_setSliderValue(innerScaleSlider.getElement(), currentIndex);
				editorContext.getEventBus().fireEvent(new SurfaceScaleEvent(currentIndex));
			}
		}
	}
	
  private void scale(int index) {
		editorContext.getEventBus().fireEvent(new SurfaceScaleEvent(index));
  }

  private int getSliderValue() {
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
	  		max: @net.sevenscales.editor.content.ui.ScaleSlider::MAX_INDEX, 
	  		value: @net.sevenscales.editor.content.ui.ScaleSlider::DEFAULT_INDEX,
	  		change: function(e, ui) {
	  				me.@net.sevenscales.editor.content.ui.ScaleSlider::scale(I)(ui.value);
	        }
	  		});
  	} else if (typeof $wnd.jQuery == "function")  {
	  	$wnd.jQuery(element).slider({ 
	  		orientation: 'vertical', 
	  		max: @net.sevenscales.editor.content.ui.ScaleSlider::MAX_INDEX, 
	  		value: @net.sevenscales.editor.content.ui.ScaleSlider::DEFAULT_INDEX,
	  		change: function(e, ui) {
	  				me.@net.sevenscales.editor.content.ui.ScaleSlider::scale(I)(ui.value);
	        }
	  		});
  	}
  	
  	if (typeof $wnd.jq172 == "function") {
	  	$wnd.jq172(element).on( "slidechange", function(event, ui) {
	  		me.@net.sevenscales.editor.content.ui.ScaleSlider::scale(I)
			    ($wnd.jQuery(element).slider("value"));
			});
  	} else if (typeof $wnd.jQuery == "function") $wnd.jQuery(element).on( "slidechange", function(event, ui) {
  		me.@net.sevenscales.editor.content.ui.ScaleSlider::scale(I)
		    ($wnd.jQuery(element).slider("value"));
		});
	}-*/;

  public void reset() {
    scaleToIndex(DEFAULT_INDEX);
  }

}
