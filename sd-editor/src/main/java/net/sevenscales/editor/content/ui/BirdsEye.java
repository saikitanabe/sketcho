package net.sevenscales.editor.content.ui;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.Window;

import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.EditorContext;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.BoardDimensions;


class BirdsEye {
		private int transformX = 0;
		private int transformY = 0;
		private int mousePosX;
		private int mousePosY;
		private double ratio = 0;
	
		private boolean birdsEyeDown = false;
		private HandlerRegistration moveRegistration;
		private ISurfaceHandler surface;
		private EditorContext editorContext;
		private IScaleSlider slider;


		BirdsEye(ISurfaceHandler surface, EditorContext editorContext, IScaleSlider slider) {
			this.surface = surface;
			this.editorContext = editorContext;
			this.slider = slider;

			// do not handle undo/redo if property editor is open
			Event.addNativePreviewHandler(new NativePreviewHandler() {
			  @Override
			  public void onPreviewNativeEvent(NativePreviewEvent event) {
          NativeEvent ne = event.getNativeEvent();
			    if (!birdsEyeDown && event.getTypeInt() == Event.ONKEYDOWN && UIKeyHelpers.noMetaKeys(ne) && !BirdsEye.this.editorContext.isTrue(EditorProperty.PROPERTY_EDITOR_IS_OPEN)) {
			      if (ne.getKeyCode() == 'Z' && UIKeyHelpers.allMenusAreClosed()) {
			      	birdsEyeViewOn();
			      }
			    }

			    if (birdsEyeDown && event.getTypeInt() == Event.ONKEYUP && !BirdsEye.this.editorContext.isTrue(EditorProperty.PROPERTY_EDITOR_IS_OPEN)) {
			      if (ne.getKeyCode() == 'Z') {
			      	birdsEyeViewOff();
			      }
			    }
			  }
			});

			Event.addNativePreviewHandler(new NativePreviewHandler() {
			  @Override
			  public void onPreviewNativeEvent(NativePreviewEvent event) {
			  	handlePreview(event);
				}
			});

			subscribeMapView(this);
		}

		private void handlePreview(NativePreviewEvent event) {
	    if (!birdsEyeDown && (event.getTypeInt() == Event.ONKEYPRESS) && !editorContext.isTrue(EditorProperty.PROPERTY_EDITOR_IS_OPEN) && UIKeyHelpers.allMenusAreClosed()) {
	      NativeEvent ne = event.getNativeEvent();
	      if (ne.getCharCode() == '+') { // compare using char code since key code is different on Firefox
	      	int val = slider.getSliderValue() + 1;
          // logger.debug("zoom ++ {}", val);
	      	if (val <= IScaleSlider.MAX_INDEX) {
	      	  slider.scaleToIndex(val);
	      	}
				}	
			}

			if (!birdsEyeDown && event.getTypeInt() == Event.ONKEYPRESS && !editorContext.isTrue(EditorProperty.PROPERTY_EDITOR_IS_OPEN) && UIKeyHelpers.allMenusAreClosed()) {
	      NativeEvent ne = event.getNativeEvent();
	      if (ne.getCharCode() == '-') { // compare using char code since key code is different on Firefox
	      	// logger.debug("zoom --");
	      	int val = slider.getSliderValue() - 1;
          // logger.debug("zoom -- {}", val);
	      	if (val >= 0) {
            slider.scaleToIndex(val);
	      	}
				}	
			}
		}

		private native void subscribeMapView(BirdsEye me)/*-{
			$wnd.mapViewStream.onValue(function(value) {
				me.@net.sevenscales.editor.content.ui.BirdsEye::mapView(Z)(value);
			})
		}-*/;

		private void mapView(boolean value) {
			if (value) {
				birdsEyeViewOn();
			} else {
				birdsEyeViewOff();
			}
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

		private void birdsEyeViewOn() {
	    // logger.debug("show birds eye view...");
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
	    // logger.debug("clientWidth {} clientHeight {} width {} height {}", clientWidth, clientHeight, width, height);
	    // logger.debug("ratio {} transformX {} transformY {} leftmost {} topmost {}", ratio, transformX, transformY, leftmost, topmost);

	    // int leftPosition = 40;
	    // surface.getRootLayer().setTransform((int)-(leftmost * ratio) + leftPosition, (int)-(topmost * ratio));
	    surface.getRootLayer().setTransform((int)-(leftmost * ratio), (int)-(topmost * ratio));

	  	birdsEyeDown = true;
	  	followMouse();
		}

		private void birdsEyeViewOff() {
			if (birdsEyeDown) {
	    	slider.scale(slider.getSliderValue());
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
	      slider.scaleToIndex(IScaleSlider.DEFAULT_INDEX);
	      // posx /= surface.getScaleFactor();
	      // posy /= surface.getScaleFactor();
	      // int posx = leftmost - mousePosX;

	    	// logger.debug("posx {} posy {} ratio {} mousePosX {} mousePosY {} leftmost {} topmost {}", posx, posy, ratio, mousePosX, mousePosY, leftmost, topmost);

	      surface.getRootLayer().setTransform(posx, posy);

	      moveRegistration.removeHandler();
	      moveRegistration = null;
			}
		}

		private void followMouse() {
			// logger.debug("followMouse...");
			moveRegistration = Event.addNativePreviewHandler(mouseMoveHandler);
		}
		
	}
