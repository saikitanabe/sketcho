package net.sevenscales.editor.api.impl;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.event.dom.client.HasTouchStartHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.js.JsDimension;
import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.EditorContext;
import net.sevenscales.editor.api.IBirdsEyeView;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.SurfaceLoadedEventListener;
import net.sevenscales.editor.api.ot.IBoardUserHandler;
import net.sevenscales.editor.api.ot.OTBuffer;
import net.sevenscales.editor.api.ot.OperationTransaction;
import net.sevenscales.editor.content.ui.IModeManager;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.DiagramDragHandler;
import net.sevenscales.editor.diagram.DiagramResizeHandler;
import net.sevenscales.editor.diagram.DiagramSearch;
import net.sevenscales.editor.diagram.DiagramSelectionHandler;
import net.sevenscales.editor.diagram.KeyEventListener;
import net.sevenscales.editor.diagram.MouseDiagramHandler;
import net.sevenscales.editor.diagram.MouseDiagramHandlerManager;
import net.sevenscales.editor.diagram.ProxyDragHandler;
import net.sevenscales.editor.diagram.SelectionHandler;
import net.sevenscales.editor.diagram.drag.Anchor;
import net.sevenscales.editor.diagram.drag.AnchorElement;
import net.sevenscales.editor.gfx.base.GraphicsEvent;
import net.sevenscales.editor.gfx.domain.IGroup;
import net.sevenscales.editor.gfx.domain.ILoadObserver;
import net.sevenscales.editor.gfx.domain.IShapeFactory;
import net.sevenscales.editor.gfx.domain.ISurface;
import net.sevenscales.editor.gfx.domain.JsSvg;
import net.sevenscales.editor.gfx.domain.JsSvgContainer;
import net.sevenscales.editor.gfx.domain.MatrixPointJS;
import net.sevenscales.editor.gfx.domain.OrgEvent;
import net.sevenscales.editor.gfx.domain.Promise;

public class UnAttachedSurface extends SimplePanel implements ISurfaceHandler {
	private static SLogger logger = SLogger.createLogger(UnAttachedSurface.class);
	static {
		SLogger.addFilter(UnAttachedSurface.class);
	}

	private EditorContext editorContext;
	private ISurface surface;
	private IGroup connectionLayer3;
	private IGroup interactionLayer4;
	private IGroup elementLayer2;
	private IGroup containerLayer1;
	private IGroup slideLayer;
	private IGroup rootLayer0;
	private List<Diagram> diagrams;
	private ILoadObserver loadObserver;
	private boolean editable;

	public UnAttachedSurface(EditorContext editorContext, ILoadObserver loadObserver) {
		this.editorContext = editorContext;
		this.loadObserver = loadObserver;
		this.editable = false;
		this.diagrams = new ArrayList<Diagram>();
		// setPixelSize(100, 100);
	}

  public void setSize(int width, int height) {
    surface.setSize(width, height, "px");
    setPixelSize(width, height);
  }

	@Override
	protected void onLoad() {
		logger.debug("onLoad {}...");
		if (surface == null) {
			surface = IShapeFactory.Util.factory(editable).createSurface();
			surface.init(this, new ILoadObserver() {
				public void loaded() {
					// loaded();
				}
			});
			loaded();
		}
	}

	private void loaded() {
		rootLayer0 = IShapeFactory.Util.factory(editable).createGroup(surface);
		slideLayer = IShapeFactory.Util.factory(editable).createGroup(rootLayer0);
		containerLayer1 = IShapeFactory.Util.factory(editable).createGroup(rootLayer0);
		elementLayer2 = IShapeFactory.Util.factory(editable).createGroup(rootLayer0);
		connectionLayer3 = IShapeFactory.Util.factory(editable).createGroup(rootLayer0);
		interactionLayer4 = IShapeFactory.Util.factory(editable).createGroup(rootLayer0);
		loadObserver.loaded();
	}

	public void init(int width, int height, boolean editable, IModeManager modeManager, boolean deleteSupported, 
			EditorContext editorContext, OTBuffer otBuffer, OperationTransaction operationTransaction, IBirdsEyeView birdsEyeView) {
	}
	public IBirdsEyeView getBirdsEyeView() {
		return null;
	}
	public void setId(String id) {
		getElement().setId(id);
	}
	public boolean isLibrary() {
		return false;
	}
	public void show() {
	}
	public void hide() {
	}
	public ISurface getSurface() {
		return surface;
	}
	public boolean isExporting() {
		return true;
	}
  public Promise<JsSvg> getSvg() {
  	JsSvgContainer svg = surface.getContainer().cast();
		return svg.getSvg();
  }
	public void setBoardUserHandler(IBoardUserHandler boardUserHandler) {
	}
	public IBoardUserHandler getBoardUserHandler() {
		return null;
	}
	public List<Diagram> getDiagrams() {
		return diagrams;
	}
	public List<Diagram> getVisualItems() {
		return null;
	}
	public JsDimension getDimension(JsArrayString shapeIds) {
		return null;		
	}

	public void addLoadEventListener(SurfaceLoadedEventListener listener) {

	}
	public EditorContext getEditorContext() {
		return editorContext;
	}
	public MouseDiagramHandlerManager getMouseDiagramManager() {
		return null;
	}
	public SelectionHandler getSelectionHandler() {
		return null;
	}
	public IModeManager getModeManager() {
		return null;
	}
	public void suspendRedraw() {

	}
	public void unsuspendRedrawAll() {

	}
	public void add(Diagram diagram, boolean ownerComponent) {
		diagrams.add(diagram);
	}
	public void add(Diagram diagram, boolean ownerComponent, boolean duplicate) {

	}
	public void add(List<Diagram> toAddDiagrams, boolean ownerComponent, boolean duplicate) {

	}
	public void addAsSelected(Diagram diagram, boolean ownerComponent) {

	}
	public void addAsSelected(Diagram diagram, boolean ownerComponent, boolean duplicate) {

	}
	public void addAsSelected(List<Diagram> diagrams, boolean ownerComponent, boolean duplicate) {

	}
	public void addAsDragging(OrgEvent event, Diagram diagram, boolean ownerComponent, MatrixPointJS point, int keys) {

	}
	public boolean isProxyDragAdding() {
		return false;
	}
	public int getCurrentClientX() {
		return 0;
	}
	public int getCurrentClientY() {
		return 0;
	}
	public int getCurrentClientMouseMoveX() {
		return 0;
	}
	public int getCurrentClientMouseMoveY() {
		return 0;
	}
	public void remove(Diagram diagram) {

	}
	public void remove(JavaScriptObject element) {

	}
	public void clear() {

	}
	public void reset() {

	}
	public AnchorElement getAttachElement(Anchor anchor, int x, int y) {
		return null;
	}
	public IGroup getElementLayer() {
		return elementLayer2;
	}
	public IGroup getConnectionLayer() {
		return connectionLayer3;
	}
	public IGroup getContainerLayer() {
		return containerLayer1;
  }
  public IGroup getSlideLayer() {
    return slideLayer;
  }
	public IGroup getRootLayer() {
		return rootLayer0;
	}
	public IGroup getInteractionLayer() {
		return interactionLayer4;
	}
	public void setTransform(double tx, double ty) {
		
	}
	public void scale(double value, boolean wheel, int middleX, int middleY) {

	}
	public double getScaleFactor() {
		return 0;
	}
	public int scaleClientX(int clientX) {
		return 0;
	}
	public int scaleClientY(int clientY) {
		return 0;
	}
	public void setBackground(String color) {

	}
	public boolean isDeleteSupported() {
		return false;
	}
	public String getName() {
		return null;
	}
	public void setName(String name) {

	}
	// public <H extends EventHandler> HandlerRegistration addDomHandler(final H handler, DomEvent.Type<H> type) {
	// 	return null;
	// }
	public void dispatchDiagram(MatrixPointJS point) {

	}
	public void setDragEnabled(boolean enableDragging) {

	}
	public boolean isDragEnabled() {
		return false;
	}
	public void setProxyOnDrag(boolean proxyOnDrag) {

	}
	public boolean isProxyOnDrag() {
		return false;
	}
	public boolean isVerticalDrag() {
		return false;
	}
	// public com.google.gwt.user.client.Element getElement() {
	// 	return null;
	// }
	public void addKeyEventHandler(KeyEventListener keyEventHandler) {

	}
	public void makeDraggable(Diagram diagram) {

	}
	public void makeBendable(Diagram diagram) {

	}
	public void addResizeHandler(DiagramResizeHandler resizeHandler) {

	}
	public void addDragHandler(DiagramDragHandler handler) {

	}
	public void addSelectionListener(DiagramSelectionHandler handler) {

	}
	public void addMouseDiagramHandler(MouseDiagramHandler mouseDiagramHandler) {

	}
	@Override public void addProxyDragHandler(ProxyDragHandler proxyDragHandler) {

	}
	public HandlerRegistration addTouchStartHandler(TouchStartHandler handler) {
		return null;
	}
	public HandlerRegistration addTouchEndHandler(TouchEndHandler handler) {
		return null;
	}
	public HandlerRegistration addTouchMoveHandler(TouchMoveHandler handler) {
		return null;
	}
	public HandlerRegistration addMouseWheelHandler(MouseWheelHandler handler) {
		return null;
	}
	public void onMouseDown(GraphicsEvent event, int keys) {

	}
	public void onMouseUp(GraphicsEvent event, int keys) {

	}
	public void setDisableOnArea(boolean value) {

	}
	public void fireLongPress(int x, int y) {

	}
	public Widget getWidget() {
		return null;
	}
	public HasTouchStartHandlers getHasTouchStartHandlers() {
		return null;
	}
	public int getAbsoluteLeft() {
		return 0;
	}
	public int getAbsoluteTop() {
		return 0;
	}
	public void setStyleName(String style) {

	}
	public void setVisible(boolean visible) {

	}
	public DiagramSearch createDiagramSearch() {
		return null;
	}
	public void moveToBack() {

	}
	public void moveSelectedToBack() {

	}
	public void moveSelectedToFront() {

	}
	public void moveSelectedToBackward() {

	}
	public void moveSelectedToForward() {

	}
	public void applyDisplayOrders(List<? extends IDiagramItemRO> items) {

	}
	public void fireMouseDown(MouseDownEvent event) {

	}
	public void fireMouseOnEnter(MouseMoveEvent event) {

	}
	public void fireMouseMove(MouseMoveEvent event, boolean toolbar) {

	}
	public void fireMouseUp(MouseUpEvent event) {

	}
	public void fireMouseOnLeave(MouseMoveEvent event) {

	}

	public OTBuffer getOTBuffer() {
		return null;
	}

	@Override
	public void beginTransaction() {
	}
	@Override
	public void commitTransaction() {
	}

	@Override
	public void setSvgClassName(String classname) {
	}

}