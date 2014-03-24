package net.sevenscales.editor.api;

import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.core.client.JavaScriptObject;

import com.google.gwt.event.dom.client.HasTouchStartHandlers;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchEndHandler;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;

import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.MouseDiagramHandlerManager;
import net.sevenscales.editor.diagram.SelectionHandler;
import net.sevenscales.editor.diagram.KeyEventListener;
import net.sevenscales.editor.diagram.DiagramResizeHandler;
import net.sevenscales.editor.diagram.DiagramDragHandler;
import net.sevenscales.editor.diagram.DiagramSelectionHandler;
import net.sevenscales.editor.diagram.MouseDiagramHandler;
import net.sevenscales.editor.diagram.DiagramSearch;

import net.sevenscales.editor.gfx.domain.IGroup;
import net.sevenscales.editor.gfx.base.GraphicsEvent;
import net.sevenscales.editor.gfx.domain.MatrixPointJS;
import net.sevenscales.editor.gfx.domain.ISurface;
import net.sevenscales.editor.diagram.drag.AnchorElement;
import net.sevenscales.editor.diagram.drag.Anchor;
import net.sevenscales.editor.content.ui.IModeManager;


public interface ISurfaceHandler {
	public static final String DRAWING_AREA = "drawingarea";
	public static final String LIBRARY_AREA = "libraryarea";

	void init(int width, int height, boolean editable, IModeManager modeManager, boolean deleteSupported, 
			EditorContext editorContext);

	ISurface getSurface();
	List<Diagram> getDiagrams();
	List<Diagram> getVisualItems();
	void addLoadEventListener(SurfaceLoadedEventListener listener);
	EditorContext getEditorContext();
	MouseDiagramHandlerManager getMouseDiagramManager();
	SelectionHandler getSelectionHandler();
	IModeManager getModeManager();
	void suspendRedraw();
	void unsuspendRedrawAll();
	void add(Diagram diagram, boolean ownerComponent);
	void add(Diagram diagram, boolean ownerComponent, boolean duplicate);
	void add(List<Diagram> toAddDiagrams, boolean ownerComponent, boolean duplicate);
	void addAsSelected(Diagram diagram, boolean ownerComponent);
	void addAsSelected(Diagram diagram, boolean ownerComponent, boolean duplicate);
	void addAsSelected(List<Diagram> diagrams, boolean ownerComponent, boolean duplicate);
	void addAsDragging(Diagram diagram, boolean ownerComponent, MatrixPointJS point, int keys);
	int getCurrentClientX();
	int getCurrentClientY();
	int getCurrentClientMouseMoveX();
	int getCurrentClientMouseMoveY();
	void remove(Diagram diagram);
	void remove(JavaScriptObject element);
	void clear();
	void reset();
	AnchorElement getAttachElement(Anchor anchor, int x, int y);
	IGroup getElementLayer();
	IGroup getConnectionLayer();
	IGroup getContainerLayer();
	IGroup getRootLayer();
	IGroup getInteractionLayer();
	void scale(float value);
	float getScaleFactor();
	int scaleClientX(int clientX);
	int scaleClientY(int clientY);
	void setBackground(String color);
	boolean isDeleteSupported();
	String getName();
	void setName(String name);
	<H extends EventHandler> HandlerRegistration addDomHandler(
      final H handler, DomEvent.Type<H> type);
	void dispatchDiagram(MatrixPointJS point);
	void setDragEnabled(boolean enableDragging);
	boolean isDragEnabled();
	void setProxyOnDrag(boolean proxyOnDrag);
	boolean isProxyOnDrag();
	boolean isVerticalDrag();
	com.google.gwt.user.client.Element getElement();
	void addKeyEventHandler(KeyEventListener keyEventHandler);
	void makeDraggable(Diagram diagram);
	void makeBendable(Diagram diagram);
	void addResizeHandler(DiagramResizeHandler resizeHandler);
	void addDragHandler(DiagramDragHandler handler);
	void addSelectionListener(DiagramSelectionHandler handler);
	void addMouseDiagramHandler(MouseDiagramHandler mouseDiagramHandler);

	HandlerRegistration addTouchStartHandler(TouchStartHandler handler);
	HandlerRegistration addTouchEndHandler(TouchEndHandler handler);
	HandlerRegistration addTouchMoveHandler(TouchMoveHandler handler);

	void onMouseDown(GraphicsEvent event, int keys);
	void onMouseUp(GraphicsEvent event);

	void setDisableOnArea(boolean value);
	void fireLongPress(int x, int y);

	Widget getWidget();
	HasTouchStartHandlers getHasTouchStartHandlers();

	int getAbsoluteLeft();
	int getAbsoluteTop();
	void setStyleName(String style);
	void setVisible(boolean visible);

	DiagramSearch createDiagramSearch();
	void moveToBack();
	void moveSelectedToBack();
	void moveSelectedToFront();
	void moveSelectedToBackward();
	void moveSelectedToForward();
	void applyDisplayOrder(Diagram diagram, int displayOrder);

	// can be removed candidates...

	void fireMouseDown(MouseDownEvent event);
	void fireMouseOnEnter(MouseMoveEvent event);
	void fireMouseMove(MouseMoveEvent event, boolean toolbar);
	void fireMouseUp(MouseUpEvent event);
	void fireMouseOnLeave(MouseMoveEvent event);

}