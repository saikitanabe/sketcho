package net.sevenscales.sketcho.client.app.utils;

import net.sevenscales.domain.api.IPage;
import net.sevenscales.plugin.api.Context;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragHandler;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.google.gwt.event.dom.client.HasMouseDownHandlers;
import com.google.gwt.event.dom.client.HasMouseMoveHandlers;
import com.google.gwt.event.dom.client.HasMouseOutHandlers;
import com.google.gwt.event.dom.client.HasMouseUpHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IndexedPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.MouseListenerCollection;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class PageOrganizeHandle extends SimplePanel implements HasMouseDownHandlers,
                                                    HasMouseUpHandlers, HasMouseOutHandlers,
                                                    HasMouseMoveHandlers, DragHandler {
//  private HorizontalPanel line = new HorizontalPanel();
//  private VerticalPanel children = new VerticalPanel();
  private VerticalPanel items = new VerticalPanel();
  private IPage page;
//  private MouseListenerCollection mouseListeners;
  private Context context;
  private PageOrganizeHandle parent;
  private HTML pageName;
  
  public PageOrganizeHandle(IPage page, PickupDragController widgetDragController, Context context) {
    this.page = page;
    this.context = context;
    
//    Map<Object, Object> requests = new HashMap<Object, Object>();
//    requests.put(RequestId.CONTROLLER, RequestValue.PAGE_CONTROLLER);
//    requests.put(RequestId.PAGE_ID, page.getId());
//    requests.put(RequestId.PROJECT_ID, context.getProjectId());
//    Action name = ActionFactory.createLinkAction(page.getName(), requests);
    
    this.pageName = new HTML(page.getName());
    items.add(pageName);
    
//    items.add(children);
//    children.setStylePrimaryName("sd-app-PageOrganizeHandle");
//    children.addStyleDependentName("empty");
    
    Label spacerLabel = new Label("");
    spacerLabel.setHeight("5px");
    items.add(spacerLabel);
    
    setWidget(items);

    NoInsertAtEndIndexedDropController widgetDropController = new NoInsertAtEndIndexedDropController
      (page, this);
    widgetDragController.registerDropController(widgetDropController);

    widgetDragController.makeDraggable(this);
    widgetDragController.addDragHandler(this);
  }
  
//  @Override
//  public void onBrowserEvent(Event event) {
//    switch (event.getTypeInt()) {
//      case Event.ONMOUSEDOWN:
//      case Event.ONMOUSEUP:
//      case Event.ONMOUSEMOVE:
//      case Event.ONMOUSEOVER:
//      case Event.ONMOUSEOUT:
//        if (mouseListeners != null) {
//          mouseListeners.fireMouseEvent(this, event);
//        }
//        break;
//    }
//  }
  
  public void add(Widget widget) {
    // lets keep widget
    widget.addStyleName("sd-app-PageOrganizeHandle-Child");
//    items.add(widget);
    items.insert(widget, items.getWidgetCount() - 1);
//    children.removeStyleDependentName("empty");
  }

  public IPage getPage() {
    return page;
  }

//  public void addMouseListener(MouseListener listener) {
//    if (mouseListeners == null) {
//      mouseListeners = new MouseListenerCollection();
//      sinkEvents(Event.MOUSEEVENTS);
//    }
//    mouseListeners.add(listener);
//  }
//
//  public void removeMouseListener(MouseListener listener) {
//    mouseListeners.remove(listener);
//  }

  public void onDragEnd(DragEndEvent event) {
//    System.out.println(page.getName()+children.getWidgetCount());
//    if (children.getWidgetCount() == 1) {
//      children.addStyleDependentName("empty");
//    } else {
//      children.removeStyleDependentName("empty");
//    }
  }

  public void onDragStart(DragStartEvent event) {
  }

  public void onPreviewDragEnd(DragEndEvent event) throws VetoDragException {
  }

  public void onPreviewDragStart(DragStartEvent event) throws VetoDragException {
  }

  public Widget getContainer() {
    return items;
  }

  public IndexedPanel getDropTarget() {
    return items;
  }

  public HTML getPageName() {
    return pageName;
  }

  public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
    return addDomHandler(handler, MouseDownEvent.getType());
  }

  public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
    return addDomHandler(handler, MouseUpEvent.getType());
  }

  public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
    return addDomHandler(handler, MouseOutEvent.getType());
  }

  public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
    return addDomHandler(handler, MouseMoveEvent.getType());
  }

}
