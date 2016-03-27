package net.sevenscales.editor.diagram;

import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.event.dom.client.MouseMoveEvent;

import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.domain.utils.Error;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.event.ShowDiagramPropertyTextEditorEvent;
import net.sevenscales.editor.api.event.FreehandModeChangedEvent;
import net.sevenscales.editor.api.impl.TouchDragAndDrop;
import net.sevenscales.editor.api.impl.TouchDragAndDrop.ITouchToMouseHandler;
import net.sevenscales.editor.api.Tools;
import net.sevenscales.editor.content.utils.ScaleHelpers;
import net.sevenscales.editor.diagram.utils.GridUtils;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.MatrixPointJS;

/**
 * Handles fully proxy dragging. MouseDiagramDragHandler is not used for dragging proxy.
 * That handles surface area element dragging.
 * @author saikitanabe
 *
 */
public class ProxyDragHandler implements MouseDiagramHandler {
	private static final SLogger logger = SLogger.createLogger(ProxyDragHandler.class);

  private GridUtils gridUtils = new GridUtils();
  private ISurfaceHandler source;
  private ISurfaceHandler target;
  private Diagram sourceproxy;
  private Diagram targetproxy;
	private int prevDX;
	private int prevDY;
//	private TouchManager touchManager;

//  private int prevX = 0;
//  private int prevY = 0;

  public ProxyDragHandler(ISurfaceHandler source, ISurfaceHandler target) {
    this.source = source;
    this.target = target;
    
//    touchManager = new TouchManager(this); 
    
    // source.addMouseDiagramHandler(this);
    source.addProxyDragHandler(this);
    target.addMouseDiagramHandler(new MouseDiagramHandler() {
      @Override
      public void onMouseUp(Diagram sender, MatrixPointJS point, int keys) {
//        System.out.println("MOUSE UP target");
    		// fire event show property text editor
      	if (sender == null && targetproxy != null) {
	        ProxyDragHandler.this.target.getEditorContext().getEventBus().fireEvent(
	        		new ShowDiagramPropertyTextEditorEvent(targetproxy).setJustCreated(true));
	      	targetproxy = null;
      	}
      }
      @Override
      public void onMouseMove(Diagram sender, MatrixPointJS point) {
      }
      @Override
      public void onMouseLeave(Diagram sender, MatrixPointJS point) {
      }
      @Override
      public void onMouseEnter(Diagram sender, MatrixPointJS point) {
//        System.out.println("ENTER target");
        // create element on target and move it by width
      	createTargetProxy(point);
      }
      @Override
      public boolean onMouseDown(Diagram sender, MatrixPointJS point, int keys) {
        return false;
      }
      
      @Override
      public void onTouchStart(Diagram sender, MatrixPointJS point) {
      }
      
      @Override
      public void onTouchMove(Diagram sender, MatrixPointJS point) {
      	createTargetProxy(point);
      }
      @Override
      public void onTouchEnd(Diagram sender, MatrixPointJS point) {
      }
      
      private void createTargetProxy(MatrixPointJS point) {
        if (sourceproxy != null) {
          Diagram owner = sourceproxy.getOwnerComponent();
          ProxyDragHandler.this.target.getEditorContext().set(EditorProperty.ON_SURFACE_LOAD, true);
          Diagram d = owner.duplicate(ProxyDragHandler.this.target, 
          		point.getX() - ScaleHelpers.scaleValue(ProxyDragHandler.this.target.getRootLayer().getTransformX(), ProxyDragHandler.this.target.getScaleFactor()), 
          		point.getY() - ScaleHelpers.scaleValue(ProxyDragHandler.this.target.getRootLayer().getTransformY(), ProxyDragHandler.this.target.getScaleFactor()));

          ProxyDragHandler.this.target.getEditorContext().set(EditorProperty.ON_SURFACE_LOAD, false);
          
          // for now color is taken from library, usability:
          // - uses the color that it looks like
          // case library: takes library element color
          // case quick insert: takes color from prev element
//      		net.sevenscales.editor.diagram.utils.Color current = (net.sevenscales.editor.diagram.utils.Color) 
//      				ProxyDragHandler.this.target.getEditorContext().get(EditorProperty.CURRENT_COLOR);
//      		d.setBackgroundColor(current.getRr(), current.getGg(), current.getBb(), current.getOpacity());
//      		d.setTextColor(current.getR(), current.getG(), current.getB());
          
          ProxyDragHandler.this.target.addAsDragging(d, true, point, 0);
          sourceproxy.setVisible(false);
          removeProxy();
          targetproxy = d;
        }
      }

    });
  }

  public boolean onMouseDown(Diagram sender, MatrixPointJS point, int keys) {
    if (sender != null && source.isProxyOnDrag() && sourceproxy == null) {
      try {
        createProxy(sender, point);
      } catch (Exception e) {
        Error.reload("createProxy: " + e);
      } finally {
        // restore server saving state
        target.getEditorContext().set(EditorProperty.ON_CHANGE_ENABLED, true);
      }
    }
    return false;
  }

  private void createProxy(Diagram sender, MatrixPointJS point) {
  	logger.debug("createProxy...");
	//  prevX = point.getX();
	//  prevY = point.getY();
	  prevDX = 0;
	  prevDY = 0;
	
//	  int screenX = point.getScreenX();
//	  int screenY = point.getScreenY();
	//  System.out.println("point.getScreenX(), point.getScreenY(): " + screenX + " " + screenY);
		gridUtils.init(point.getScreenX(), point.getScreenY(), source.getScaleFactor());
	
	//  gridUtils.init(point.getX(), point.getY(), source.getScaleFactor());
	
	  // create proxy
	  Diagram owner = sender.getOwnerComponent();
	  sourceproxy = owner.duplicate(source, point.getX(), point.getY());
	  
	  target.getEditorContext().set(EditorProperty.ON_CHANGE_ENABLED, false);
	  source.add(sourceproxy, true);
	  target.getEditorContext().set(EditorProperty.ON_CHANGE_ENABLED, true);

    target.getEditorContext().getEventBus().fireEvent(new FreehandModeChangedEvent(false));

    // hand tool is always disabled when starting to drag shapes from the library
    Tools.setHandTool(false);
	}

	public void onMouseMove(Diagram sender, MatrixPointJS point) {
    if (sourceproxy != null && sender == null) {
//    	logger.debug("onMouseMove point x({}), y({}), screenX({}), screenY({})", 
//    								point.getX(), point.getY(), point.getScreenX(), point.getScreenY());
//    	System.out.println("sender: " + sender);
			MatrixPointJS dp = MatrixPointJS.createScaledTransform(
					gridUtils.dx(point.getScreenX() - source.getAbsoluteLeft() - source.getRootLayer().getTransformX()), 
					gridUtils.dy(point.getScreenY() - source.getAbsoluteTop() - source.getRootLayer().getTransformY()), 
					source.getScaleFactor());
			int dx = dp.getDX() - prevDX;
			int dy = dp.getDY() - prevDY;
      prevDX = dp.getDX();
      prevDY = dp.getDY();

//      int dx = gridUtils.diffX(point.getX(), prevX);
//      int dy = gridUtils.diffY(point.getY(), prevY);

//      System.out.println("move... " + point.getScreenX() + ", " + point.getScreenY() + " " + dx + ", " + dy);
//      MatrixPointJS dp = MatrixPointJS.createScaledPoint(dx, dy, source.getScaleFactor());
      sourceproxy.applyTransform(dx, dy);
    }
  }

  public void onMouseUp(Diagram sender, MatrixPointJS point, int keys) {
    // delete proxy
    removeProxy();
  }

  @Override
  public void onTouchStart(Diagram sender, MatrixPointJS point) {
  	onMouseDown(sender, point, 0);
  }
  @Override
  public void onTouchMove(Diagram sender, MatrixPointJS point) {
  	onMouseMove(sender, point);
  }
  @Override
  public void onTouchEnd(Diagram sender, MatrixPointJS point) {
  	onMouseUp(sender, point, 0);
  }

  private void removeProxy() {
  	logger.debug("removeProxy...");
    if (sourceproxy != null) {
      sourceproxy.removeFromParent();
    }
    sourceproxy = null;
  }

  @Override
  public void onMouseEnter(Diagram sender, MatrixPointJS point) {
    // Goes quite complex
    // better to try then to change whole design and proxy implementation
    // is easier...
//    if (targetproxy != null) {
//      Diagram owner = targetproxy.getOwnerComponent();
//      Diagram d = null;
//      if (owner != null) {
//        // duplicate always from owner if exists
//        d = owner.duplicate(ProxyDragHandler.this.source, x, y);
//      } else {
//        d = targetproxy.duplicate(ProxyDragHandler.this.source, x, y);
//      }
//      
////      System.out.println("ENTER:"+d);
//      ProxyDragHandler.this.source.addAsDragging(d, true, x, y, 0);
////      removeProxy();
//      sourceproxy = d;
//      targetproxy.removeFromParent();
//      targetproxy = null;
//    }
  }

  @Override
  public void onMouseLeave(Diagram sender, MatrixPointJS point) {
//    System.out.println("LEAVE");
//    if (proxy != null) {
//      removeProxy();
//    }
  }

//	@Override
//	public void onTouchToMouseMove(MouseMoveEvent event, EventTarget et) {
//		onMouseMove(null, MatrixPointJS.createScaledPoint(event.getScreenX(), event.getScreenY(), source.getScaleFactor()));
//	}

}
