package net.sevenscales.editor.content.ui;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Window;

import net.sevenscales.domain.constants.Constants;
import net.sevenscales.editor.api.BoardDimensions;
import net.sevenscales.editor.api.EditorContext;
import net.sevenscales.editor.api.IBirdsEyeView;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.event.SurfaceScaleEvent;
import net.sevenscales.editor.api.event.SurfaceScaleEventHandler;


class BirdsEye implements IBirdsEyeView, SurfaceScaleEventHandler {
		private int mousePosX;
		private int mousePosY;
		private double ratio = 0;
	
		private boolean birdsEyeDown = false;
		private boolean zDown = false;
		private HandlerRegistration moveRegistration;
		private ISurfaceHandler surface;
		private EditorContext editorContext;
		private IScaleSlider slider;

	// >>>>>>>>> Debugging
		// private net.sevenscales.editor.gfx.domain.ICircle tempCircle;
	// <<<<<<<<< Debugging

		BirdsEye(ISurfaceHandler surface, EditorContext editorContext, IScaleSlider slider) {
			this.surface = surface;
			this.editorContext = editorContext;
			this.slider = slider;

			surface.getEditorContext().getEventBus().addHandler(SurfaceScaleEvent.TYPE, this);

			subscribeMapView(this);
		}

		@Override
		public void on(SurfaceScaleEvent event) {
			disable();
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

    	int val = slider.getSliderValue() + 3;
      // logger.debug("zoom ++ {}", val);
    	if (val < Constants.ZOOM_FACTORS.length) {
    	  slider.scaleToIndex(val, true);
    	}
		}

		private void zoomOutShortcut() {
			if (birdsEyeDown) {
				return;
			}

    	int val = slider.getSliderValue() - 3;
      // logger.debug("zoom -- {}", val);
    	if (val >= 0) {
        slider.scaleToIndex(val, true);
    	}
    }

    private void zoomResetShortcut() {
			if (birdsEyeDown) {
				return;
			}

  	  slider.scaleToIndex(Constants.ZOOM_DEFAULT_INDEX, true);
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


      $wnd.gwtIsBirdsEyeOn = function() {
        return me.@net.sevenscales.editor.content.ui.BirdsEye::isBirdsEyeViewOn()();
      }

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
  		      	setMousePosition(
  		      		touches.get(0).getClientX(),
  		      		touches.get(0).getClientY()
  		      	);
		    		}
		    		break;
		    	}
		      case Event.ONMOUSEDOWN:
		      case Event.ONMOUSEMOVE:
		      	setMousePosition(
		      		event.getNativeEvent().getClientX(),
		      		event.getNativeEvent().getClientY()
		      	);
		        break;
		      default:
		        // not interested in other events
		    }
		  }
		};

		private void setMousePosition(int x, int y) {
	    // >>>>>>>> Debug 
	   //  if (tempCircle == null) {
		  //   tempCircle = net.sevenscales.editor.gfx.domain.IShapeFactory.Util.factory(true).createCircle(
		  //   	surface.getRootLayer()
		  //   );
		  //   tempCircle.setShape(0, 0, 10);
		  //   tempCircle.setStroke(218, 57, 57, 1);
		  //   tempCircle.setFill(218, 57, 57, 1);
		  // }
			// <<<<<<<< Debug

    	int tx = surface.getRootLayer().getTransformX();
    	int ty = surface.getRootLayer().getTransformY();

      mousePosX = (int) (x / ratio - tx / ratio);
      mousePosY = (int) (y / ratio - ty / ratio);

      // >>>>>>>>> DEBUGGING
			// tempCircle.setShape(mousePosX - 5, mousePosY - 5, 10);
			// <<<<<<<<< DEBUGGING
		}

		private void birdsEyeViewOn() {
	    // logger.debug("show birds eye view...");
	    BoardDimensions.resolveDimensions(surface.getDiagrams());

	    if (surface.getDiagrams().size() == 0) {
	    	// protect that if no diagrams, map view is not enabled
	    	return;
	    }

	    // first reset scale slider to zoom where it would return when
	    // coming back from birds eye view
	    // fixes problem when in birds eye view and then starting directly
	    // to zoom, then starts from scale(1)
      slider.scaleToIndex(Constants.ZOOM_DEFAULT_INDEX, false);

	    int leftmost = BoardDimensions.getLeftmost();
	    int topmost = BoardDimensions.getTopmost();
	    int width = BoardDimensions.getWidth();
	    int height = BoardDimensions.getHeight();

	    // if (width <= Window.getClientWidth() && height <= Window.getClientHeight()) {
	    // 	// protect that if fits in screen, map view is not enabled
	    // 	return;
	    // }

	    // int clientLeftMargin = 100;
	    // double clientWidth = Window.getClientWidth() - clientLeftMargin;
	    double clientWidth = Window.getClientWidth() - 20;
	    double clientHeight = Window.getClientHeight() - 100;

	    double ratioW = clientWidth / width;
	    double ratioH = clientHeight / height;
	    ratio = (ratioW < ratioH) ? ratioW : ratioH;
	    surface.scale(ratio, false, 0, 0);

	    // how much space (width) board takes when zoomed, visible for eye
	    double boardWidthSameUnitWithClientWindow = width * ratio;
	    double boardHeightSameUnitWithClientWindow = height * ratio;
	    // move board to left by half of its width => board center is in the middle
	    // move board right to half of cliend window width => move whole thing towards center
	    double moveToCenterX = (clientWidth - boardWidthSameUnitWithClientWindow) / 2;
	    double moveToCenterY = (clientHeight - boardHeightSameUnitWithClientWindow) / 2;

      int x = (int)((-leftmost * ratio) + 5 * ratio + moveToCenterX);
      int y = (int)((-topmost * ratio) + 10 * ratio + moveToCenterY);
	    surface.setTransform(x, y);

	  	birdsEyeDown = true;
	  	followMouse();
	  	notifyMapView(true);
		}

		public void disable() {
			if (birdsEyeDown) {
	    	birdsEyeDown = false;
	    	if (moveRegistration != null) {
		      moveRegistration.removeHandler();
	    	}
	      moveRegistration = null;
		  	notifyMapView(false);
			}
		}

		private void birdsEyeViewOff() {
			if (birdsEyeDown) {
	      disable();

	    	// slider.scale(slider.getSliderValue());
	      // 1. zero transform to make calculations easy
	      surface.setTransform(0, 0);

	      // 2. scale at 0,0
	      slider.scaleToIndex(Constants.ZOOM_DEFAULT_INDEX, false);

	      // 3. move mouse point to 0,0 then move half the screen size to right
	      // 		to center the mouse point
	      double clientWidth = Window.getClientWidth();
	      double clientHeight = Window.getClientHeight();

	      int posx = (int) (-mousePosX + clientWidth / 2);
	      int posy = (int) (-mousePosY + clientHeight / 2);
	      surface.setTransform(posx, posy);
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
