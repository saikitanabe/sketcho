package net.sevenscales.sketcho.client.app.view;

import java.util.HashMap;
import java.util.Map;

import net.sevenscales.appFrame.impl.IController;
import net.sevenscales.appFrame.impl.Location;
import net.sevenscales.appFrame.impl.RequestUtils;
import net.sevenscales.plugin.api.Context;
import net.sevenscales.plugin.api.IPermissionContributor;
import net.sevenscales.plugin.constants.RequestId;
import net.sevenscales.plugin.constants.RequestValue;
import net.sevenscales.sketcho.client.app.view.MenuView.IMenuViewListener;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class MenuViewCompo extends SimplePanel {
  interface MyUiBinder extends UiBinder<Widget, MenuViewCompo> {}
  private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
  
  public interface MyStyle extends CssResource {
    String focused();
    String disabled();
    String enabled();
  }
  
  private IMenuViewListener listener;
  
  @UiField HorizontalPanel newsketch;
  @UiField Label sketches;
  @UiField Label dashboard;
  @UiField Label design;
  @UiField Label myprojects;
  @UiField MyStyle style;
  
  private IController<Context> controller;

  private boolean editPermission;
  
  public MenuViewCompo(IController<Context> controller, IMenuViewListener listener) {
    this.controller = controller;
    this.listener = listener;
    setWidget(uiBinder.createAndBindUi(this));
  }
  
  public void setFocus() {
    String historyToken = History.getToken();
    Location location = new Location(historyToken);
    Map<String,String> requests = location.getRequests();
    
    String controllerId = requests.get(RequestId.CONTROLLER);
    Long pageId = controller.getContext().getPageId();
    boolean dashEnabled = false;
    if (controller.getContext().getProject() != null &&
        controller.getContext().getProject().getDashboard().getId().equals(pageId)) {
      dashboard.addStyleName(style.focused());
      dashEnabled = true;
    } else {
      dashboard.removeStyleName(style.focused());
    }
    
    boolean labeled = requests.get(RequestId.LABEL_ID) != null ? true : false;
    if ((controllerId != null && controllerId.equals(RequestValue.SKETCHES_CONTROLLER) || 
        controllerId != null && controllerId.equals(RequestValue.SKETCH_CONTROLLER)) &&
        !labeled) {
      sketches.addStyleName(style.focused());
    } else {
      sketches.removeStyleName(style.focused());
    }

    boolean paged = requests.get(RequestId.PAGE_ID) != null ? true : false;
    if ((controllerId != null && controllerId.equals(RequestValue.PROJECT_ARCHITECTURE_CONTROLLER) ||
        (controllerId != null && controllerId.equals(RequestValue.PAGE_CONTROLLER) && !dashEnabled)) &&
        !paged && !dashEnabled) {
      design.addStyleName(style.focused());
    } else {
      design.removeStyleName(style.focused());
    }
  }
  
  @UiHandler("newsketch")
  public void newSketch(ClickEvent event) {
    if (editPermission) {
      listener.newSketch();
    }
  }
  @UiHandler("sketches")
  public void sketches(ClickEvent event) {
    Map<String,String> requests = new HashMap<String, String>();
    requests.put(RequestId.CONTROLLER, RequestValue.SKETCHES_CONTROLLER);
    requests.put(RequestId.PROJECT_ID, controller.getContext().getProjectId().toString());
    RequestUtils.activate(requests);
  }
  @UiHandler("dashboard")
  public void dashboard(ClickEvent event) {
    Map<String,String> requests = new HashMap<String, String>();
    requests.put(RequestId.CONTROLLER, RequestValue.PAGE_CONTROLLER);
    Long dashboardId = controller.getContext().getProject().getDashboard().getId();
    requests.put(RequestId.PAGE_ID, dashboardId.toString());
    requests.put(RequestId.PROJECT_ID, controller.getContext().getProjectId().toString());
    RequestUtils.activate(requests);
  }
  @UiHandler("design")
  public void design(ClickEvent event) {
    Map<String,String> requests = new HashMap<String, String>();
    requests.put(RequestId.CONTROLLER, RequestValue.PROJECT_ARCHITECTURE_CONTROLLER);
    requests.put(RequestId.PROJECT_ID, controller.getContext().getProjectId().toString());
    RequestUtils.activate(requests);
  }
  @UiHandler("myprojects")
  public void myprojects(ClickEvent event) {
    Map<String,String> requests = new HashMap<String, String>();
    requests.put(RequestId.CONTROLLER, RequestValue.PROJECTS_CONTROLLER);
    RequestUtils.activate(requests);
  }

  public void checkAcl(IPermissionContributor permissionContributor) {
//    newsketch.set
    editPermission = permissionContributor.hasEditPermission();
    newsketch.setVisible(editPermission);
    if (!editPermission) {
//      newsketch.addStyleName(style.disabled());
    } else {
//      newsketch.removeStyleName(style.disabled());
    }
  }

}
