package net.sevenscales.editor.content.ui;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Touch;

import net.sevenscales.editor.api.IBirdsEyeView;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.EditorContext;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.BoardDimensions;
import net.sevenscales.domain.constants.Constants;


class BirdsEye implements IBirdsEyeView {
		private int transformX = 0;
		private int transformY = 0;
		private int mousePosX;
		private int mousePosY;
		private double ratio = 0;
	
		private boolean birdsEyeDown = false;
		private boolean zDown = false;
		private HandlerRegistration moveRegistration;
		private ISurfaceHandler surface;
		private EditorContext editorContext;
		private IScaleSlider slider;
		private int transformInitX;
		private int transformInitY;


		BirdsEye(ISurfaceHandler surface, EditorContext editorContext, IScaleSlider slider) {
			this.surface = surface;
			this.editorContext = editorContext;
			this.slider = slider;

			subscribeMapView(this);
		}

		private void mapViewShortcutDown() {
			if (!zDown) {
	    	// keeping z key down would cause fast on/off toggling
	    	zDown = true;

	    	if (birdsEyeDown) {
					birdsEyeViewOff();
	    	} else {
	      	birdsEyeViewOn();
	    	}
			}
		}

		private void mapViewShortcutUp() {
    	zDown = false;
		}

		public boolean isBirdsEyeViewOn() {
			return birdsEyeDown;
		}

		public void off() {
			birdsEyeViewOff();
		}

		private void zoomInShortcut() {
			if (birdsEyeDown) {
				return;
			}

    	int val = slider.getSliderValue() + 1;
      // logger.debug("zoom ++ {}", val);
    	if (val <= Constants.ZOOM_FACTORS.length) {
    	  slider.scaleToIndex(val);
    	}
		}

		private void zoomOutShortcut() {
			if (birdsEyeDown) {
				return;
			}

    	int val = slider.getSliderValue() - 1;
      // logger.debug("zoom -- {}", val);
    	if (val >= 0) {
        slider.scaleToIndex(val);
    	}
    }

    private void zoomResetShortcut() {
			if (birdsEyeDown) {
				return;
			}

  	  slider.scaleToIndex(Constants.ZOOM_DEFAULT_INDEX);
    }

		private native void subscribeMapView(BirdsEye me)/*-{
			$wnd.mapViewStream.onValue(function(value) {
				me.@net.sevenscales.editor.content.ui.BirdsEye::mapView(Z)(value);
			})

			$wnd.globalStreams.birdsEyeShortcutDownStream.onValue(function() {
				me.@net.sevenscales.editor.content.ui.BirdsEye::mapViewShortcutDown()();
			})
			$wnd.globalStreams.birdsEyeShortcutUpStream.onValue(function() {
				me.@net.sevenscales.editor.content.ui.BirdsEye::mapViewShortcutUp()();
			})

			$wnd.globalStreams.zoomInShortcutStream.onValue(function() {
				me.@net.sevenscales.editor.content.ui.BirdsEye::zoomInShortcut()();
			})
			$wnd.globalStreams.zoomOutShortcutStream.onValue(function() {
				me.@net.sevenscales.editor.content.ui.BirdsEye::zoomOutShortcut()();
			})
			$wnd.globalStreams.zoomResetShortcutStream.onValue(function() {
				me.@net.sevenscales.editor.content.ui.BirdsEye::zoomResetShortcut()();
			})

		}-*/;

		private void mapView(boolean value) {
			if (birdsEyeDown) {
				birdsEyeViewOff();
			} else {
				birdsEyeViewOn();
			}
		}

		NativePreviewHandler mouseMoveHandler = new NativePreviewHandler() {
		  public void onPreviewNativeEvent(final NativePreviewEvent event) {
		    final int eventType = event.getTypeInt();
		    switch (eventType) {
		    	case Event.ONTOUCHSTART: {
		    		JsArray<Touch> touches = event.getNativeEvent().getTouches();
		    		if (touches != null && touches.length() > 0) {
			        int diffx = transformInitX - surface.getRootLayer().getTransformX();
			        int diffy = transformInitY - surface.getRootLayer().getTransformY();
			        mousePosX = touches.get(0).getClientX() + diffx;
			        mousePosY = touches.get(0).getClientY() + diffy;
		    		}
		    		break;
		    	}
		      case Event.ONMOUSEDOWN:
		      case Event.ONMOUSEMOVE:
		      	// take into account background move as well
		      	// some magic numbers to be more centered
		        int diffx = transformInitX - surface.getRootLayer().getTransformX();
		        int diffy = transformInitY - surface.getRootLayer().getTransformY();
		        mousePosX = event.getNativeEvent().getClientX() + diffx;
		        mousePosY = event.getNativeEvent().getClientY() + diffy;
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
	    double clientWidth = Window.getClientWidth() - 20;
	    double clientHeight = Window.getClientHeight() - 100;

	    double ratioW = clientWidth / width;
	    double ratioH = clientHeight / height;
	    ratio = (ratioW < ratioH) ? ratioW : ratioH;
	    surface.scale((float)ratio);
	    // logger.debug("clientWidth {} clientHeight {} width {} height {}", clientWidth, clientHeight, width, height);
	    // logger.debug("ratio {} transformX {} transformY {} leftmost {} topmost {}", ratio, transformX, transformY, leftmost, topmost);

	    // int leftPosition = 40;
	    // surface.getRootLayer().setTransform((int)-(leftmost * ratio) + leftPosition, (int)-(topmost * ratio));
	    surface.setTransform((int)((-leftmost * ratio) + 10 * ratio), 
	    																		(int)((-topmost * ratio) + 10 * ratio));

	    transformInitX = surface.getRootLayer().getTransformX();
	    transformInitY = surface.getRootLayer().getTransformY();

	  	birdsEyeDown = true;
	  	followMouse();
	  	notifyMapView(true);
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
	      slider.scaleToIndex(Constants.ZOOM_DEFAULT_INDEX);
	      // posx /= surface.getScaleFactor();
	      // posy /= surface.getScaleFactor();
	      // int posx = leftmost - mousePosX;

	    	// logger.debug("posx {} posy {} ratio {} mousePosX {} mousePosY {} leftmost {} topmost {}", posx, posy, ratio, mousePosX, mousePosY, leftmost, topmost);

	      surface.setTransform(posx, posy);

	      moveRegistration.removeHandler();
	      moveRegistration = null;
	      notifyMapView(false);
			}
		}

		private native void notifyMapView(boolean on)/*-{
			$wnd.globalStreams.mapViewStateStream.push(on)
		}-*/;

		private void followMouse() {
			// logger.debug("followMouse...");
			moveRegistration = Event.addNativePreviewHandler(mouseMoveHandler);
		}
		
	}
