package net.sevenscales.editor.api.impl;

import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.SurfaceHandler;
import net.sevenscales.editor.gfx.base.GraphicsEvent;
import net.sevenscales.editor.gfx.domain.MatrixPointJS;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Element;

public class SurfaceHandlerImplFirefox extends SurfaceHandler {
	private static final SLogger logger = SLogger.createLogger(SurfaceHandlerImplFirefox.class);

  private HandlerRegistration overreg;
  private HandlerRegistration movereg;
  private boolean enterEvent = false; 

  public SurfaceHandlerImplFirefox() {
    // HACK: Firefox 3.6 Silverlight doesn't send mouse events
    // when mouse has been pressed down somewhere else
    // sending always browser native events
    // and disabling silverlight mouse events

  	// no longer working on Firefox 6
  	// Well the answer is to add mousemove and mouseup event listener to document.
  	// parentin kayttaminen, mut sitten surfacen tulisi olla ehka pyytaa eventteja
  	// model panelilta...
  	// kuvakkeen tekeminen kaikista ja browser contextissa liikuttaa proxyja
  	// canvaksen kokeilue... ei valttamatta auta kun selain jo syo muiden elementtien
  	// eventit dragissa
//    this.overreg = addDomHandler(new MouseOverHandler() {
//      @Override
//      public void onMouseOver(MouseOverEvent event) {
//        Element e = SurfaceHandlerImplFirefox.this.getElement();
//        mouseDiagramManager.onMouseEnter(null, event.getRelativeX(e), event.getRelativeY(e));
//      }
//    }, MouseOverEvent.getType());
//    
//    this.movereg = addDomHandler(new MouseMoveHandler() {
//      @Override
//      public void onMouseMove(MouseMoveEvent event) {
//        Element e = SurfaceHandlerImplFirefox.this.getElement();
//        mouseDiagramManager.onMouseMove(null, event.getRelativeX(e), event.getRelativeY(e));
////		  	DOM.releaseCapture(getElement());
//      }
//    }, MouseMoveEvent.getType());
  }
  
  @Override
  public void onMouseMove(GraphicsEvent event) {
  }
  
  @Override
  public void onMouseEnter(GraphicsEvent event) {
  }
  
  @Override
  public void fireMouseDown(MouseDownEvent event) {
    Element e = SurfaceHandlerImplFirefox.this.getElement();
//    int x = event.getRelativeX(e) - getRootLayer().getTransformX();
//    int y = event.getRelativeY(e) - getRootLayer().getTransformY();
    int x = event.getNativeEvent().getClientX();
    int y = event.getNativeEvent().getClientY();

    mouseDiagramManager.onMouseDown(null, MatrixPointJS.createScaledPoint(x, y, getScaleFactor()), 0);
  }
  
  @Override
  public void fireMouseOnEnter(MouseMoveEvent event) {
    Element e = SurfaceHandlerImplFirefox.this.getElement();
//    int x = event.getRelativeX(e) - getRootLayer().getTransformX();
//    int y = event.getRelativeY(e) - getRootLayer().getTransformY();
    int x = event.getRelativeX(e);
    int y = event.getRelativeY(e);
    mouseDiagramManager.onMouseEnter(null, MatrixPointJS.createScaledPoint(x, y, getScaleFactor()));
  }
  
  @Override
  public void fireMouseOnLeave(MouseMoveEvent event) {
    Element e = SurfaceHandlerImplFirefox.this.getElement();
//    int x = event.getRelativeX(e) - getRootLayer().getTransformX();
//    int y = event.getRelativeY(e) - getRootLayer().getTransformY();
    int x = event.getRelativeX(e);
    int y = event.getRelativeY(e);
    mouseDiagramManager.onMouseLeave(null, MatrixPointJS.createScaledPoint(x, y, getScaleFactor()));
  }
  
  @Override
  public void fireMouseMove(MouseMoveEvent event, boolean toolbar) {
    Element e = SurfaceHandlerImplFirefox.this.getElement();
//    int x = event.getRelativeX(e) - getRootLayer().getTransformX();
//    int y = event.getRelativeY(e) - getRootLayer().getTransformY();
//    System.out.println("X: " + event.getNativeEvent().getClientX() + " Y: " + event.getNativeEvent().getClientY());
    
    int x = 0;
    int y = 0;

    if (!getEditorContext().isTrue(EditorProperty.SKETCHO_BOARD_MODE)) {
      // this works with confluence plugin, but not with library => HACK to fix x position
    	// not a perfect position but enough close :)
    	// on board position is drawing area surface coordinates, that's why it works...
	    x = toolbar ? getAbsoluteLeft() + event.getRelativeX(e) : event.getRelativeX(e);
	    y = toolbar ? getAbsoluteTop() + event.getRelativeY(e) : event.getRelativeY(e);
    } else {
    	// this works with board implementation
	    x = event.getNativeEvent().getClientX();
	    y = event.getNativeEvent().getClientY();
    }
    
    // hack to prevent showing surface context menu => fire click will reset this
		cancelSurfaceClickEvent = mouseDiagramManager.getBackgroundMoveHandler().backgroundMoveIsOn();
    mouseDiagramManager.onMouseMove(null, MatrixPointJS.createScaledPoint(x, y, getScaleFactor()));
  }
  
  @Override
  public void fireMouseUp(MouseUpEvent event) {
  	logger.debug("fireMouseUp...");
    int x = event.getNativeEvent().getClientX();
    int y = event.getNativeEvent().getClientY();
  
	  // hack to prevent showing surface context menu => fire click will reset this
		cancelSurfaceClickEvent = mouseDiagramManager.getBackgroundMoveHandler().backgroundMoveIsOn();
	  mouseDiagramManager.onMouseUp(null, MatrixPointJS.createScaledPoint(x, y, getScaleFactor()));
  }

}
