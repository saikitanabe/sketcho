package net.sevenscales.sketcho.client.app.view;

import java.util.HashMap;
import java.util.Map;

import net.sevenscales.appFrame.api.IContributor;
import net.sevenscales.appFrame.impl.Action;
import net.sevenscales.appFrame.impl.DynamicParams;
import net.sevenscales.appFrame.impl.IController;
import net.sevenscales.appFrame.impl.ITilesEngine;
import net.sevenscales.appFrame.impl.RequestUtils;
import net.sevenscales.appFrame.impl.View;
import net.sevenscales.plugin.api.Context;
import net.sevenscales.plugin.api.ITopNavigationContributor.ITopNaviPanel;
import net.sevenscales.plugin.constants.RequestId;
import net.sevenscales.plugin.constants.RequestValue;
import net.sevenscales.plugin.constants.TileId;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class PrimaryNavigationView extends View<Context> implements ClickHandler {

  private NavigationPanelHorizontal panel;
  private HTML dashboard;
  private HTML sketches;
  private HTML documentation;
  private Map request;
  private HTML allProjects;
  private DecoratorPanel dp = new DecoratorPanel();
  private HorizontalPanel projectsPanel;

  public PrimaryNavigationView(IController<Context> controller) {
		super(controller);
		
		dp.setWidth("100%");
		dp.setHeight("100%");
		
    Context c = (Context) controller.getContext();
    
//  HorizontalPanel left = new HorizontalPanel();   
//  tilesEngine.setTile("top-left-navigation", left);

    Map<Object, Object> requests = new HashMap<Object, Object>();
    
    Action a = null;
  
  // project selected and user has logged in => possibility to modify project
//    IPermissionContributor permissionContributor = controller.getContext().getContributor().cast(
//        IPermissionContributor.class);
    
    this.panel = new NavigationPanelHorizontal();
  //  panel.setStyleName("primary-nav");
  
    if (c.getProjectId() != null) {
      // View Pages
//      requests = new HashMap<Object, Object>();
//      requests.put(RequestId.CONTROLLER, RequestValue.PAGE_CONTROLLER);
//      requests.put(RequestId.PROJECT_ID, c.getProjectId());
//      requests.put(RequestId.ACTION_ID, ActionId.OPEN_PROJECT_DASHBOARD);
//      a = ActionFactory.createLinkAction("Dashboard", requests);
    }

    this.dashboard = new HTML("<a href='javascript:;'>Dashboard</a>");
    this.sketches = new HTML("<a href='javascript:;'>Sketches</a>");
    this.documentation = new HTML("<a href='javascript:;'>Design</a>");
    this.allProjects = new HTML("<a href='javascript:;'>All Projects</a>");
    allProjects.setWordWrap(false);

    dashboard.addClickHandler(this);
    sketches.addClickHandler(this);
    documentation.addClickHandler(this);
    allProjects.addClickHandler(this);

    panel.addItem(dashboard);
    panel.addItem(sketches);
    panel.addItem(documentation);
    panel.addItem(allProjects);
    
    panel.setWidth("100%");
    
    projectsPanel = new HorizontalPanel();
    projectsPanel.setVisible(false);

  //  if (c.getProjectId() != null && permissionContributor.hasEditPermission()) {
  //    // New Sketch
  //    requests = new HashMap<Object, Object>();
  //    requests.put(RequestId.CONTROLLER, RequestValue.TICKET_CONTROLLER);
  //    requests.put(RequestId.PROJECT_ID, String.valueOf(c.getProjectId()));
  //    requests.put(RequestId.ACTION_ID, String.valueOf(ActionId.NEW_TICKET));
  //    a = ActionFactory.createLinkAction("New Sketch", requests);
  //    panel.addItem(a);
  //
  //    // New Page
  //    requests = new HashMap<Object, Object>();
  //    requests.put(RequestId.CONTROLLER, RequestValue.NEW_PAGE_CONTROLLER);
  //    requests.put(RequestId.PROJECT_ID, String.valueOf(c.getProjectId()));
  //    requests.put(RequestId.ACTION_ID, String.valueOf(ActionId.NEW_PAGE));      
  //    a = ActionFactory.createLinkAction("New Page", requests);
  //    panel.addItem(a);
  //    // Settings
  ////    right.addItem(new RightLinkHTML("<a href='javascript:;'>Settings</a>"));
  //  }
    
//    if (c.getProjectId() != null) {
//      // View Sketches
//      requests = new HashMap<Object, Object>();
//      requests.put(RequestId.CONTROLLER, RequestValue.SKETCHES_CONTROLLER);
//      requests.put(RequestId.PROJECT_ID, String.valueOf(c.getProjectId()));
//      a = ActionFactory.createLinkAction("Sketches", requests);
//      a.addClickHandler(this);
//      panel.addItem(a);
  
  //    requests = new HashMap<Object, Object>();
  //    requests.put(RequestId.CONTROLLER, RequestValue.PROJECT_CONTROLLER);
  //    requests.put(RequestId.PROJECT_ID, c.getProjectId());
    
//      requests = new HashMap<Object, Object>();
//      requests.put(RequestId.CONTROLLER, RequestValue.PROJECT_ARCHITECTURE_CONTROLLER);
//      requests.put(RequestId.PROJECT_ID, c.getProjectId());    
//      a = ActionFactory.createLinkAction("Documentation", requests);
//      a.addClickHandler(this);
//      panel.addItem(a);
//    }

//  ITopNavigationContributor topContributor = contributor.cast
//    (ITopNavigationContributor.class);
//  topContributor.addToRight(left);
	}

	static class LeftLinkHTML extends HTML {
		public LeftLinkHTML(String text) {
			super(text);
			setStyleName("topPanel-Item-Left");
		}
	}

	static class RightLinkHTML extends HTML {
		public RightLinkHTML(String text) {
			super(text);
			setStyleName("topPanel-Item-Right");
		}
	}
	
	static class NavigationPanelHorizontal extends HorizontalPanel implements ITopNaviPanel {
	  public NavigationPanelHorizontal() {
	    setStyleName("sd-app-PrimaryNavigationView-NavigationPanelHorizontal");
    }
	  
	  public void addItem(Widget widget) {
	    if (getWidgetCount() > 0) {
	      
	      Widget w = getWidget(getWidgetCount() -1);
	      if ( !(w instanceof LeftLinkHTML) ) {
	        super.add(new LeftLinkHTML("|"));
	      }
	    }
      
      widget.setStyleName("topPanel-Item-Left");
      super.add(widget);
	  }

	  public void insertItem(Widget widget, int index) {
      widget.setStyleName("topPanel-Item-Right");
	    super.insert(widget, index);
      super.insert(new RightLinkHTML("|"), index + 1);
	  }
	}
	
	 static class NavigationPanelVertical extends VerticalPanel implements ITopNaviPanel {
	   public NavigationPanelVertical() {
	     setStyleName("sd-app-PrimaryNavigationView-NavigationPanelVertical");
     }
	    public void addItem(Widget widget) {
//	      widget.setStyleName("sd-app-PrimaryNavigationView-NavigationPanelVertical");
	      super.add(widget);
	    }

	    public void insertItem(Widget widget, int index) {
//	      widget.setStyleName("sd-app-PrimaryNavigationView-NavigationPanelVertical");
	      super.insert(widget, index);
	    }
	  }

	public void activate(ITilesEngine tilesEngine, 
						 DynamicParams params, IContributor contributor) {
	  boolean visibility = controller.getContext().getProject() != null;
	  
	  dashboard.setVisible(visibility);
    sketches.setVisible(visibility);
    documentation.setVisible(visibility);
	  tilesEngine.setTile(TileId.PRIMARY_NAVIGATION, panel);

    Object controllerId = request.get(RequestId.CONTROLLER);
    Object pageId = controller.getContext().getPageId();
    boolean dashEnabled = false;
    if (controller.getContext().getProject() != null &&
        controller.getContext().getProject().getDashboard().getId().equals(pageId)) {
      dashboard.addStyleName("sd-app-PrimaryNavigationView-focus");
      dashEnabled = true;
    } else {
      dashboard.removeStyleName("sd-app-PrimaryNavigationView-focus");
    }
    
    if (controllerId != null && controllerId.equals(RequestValue.SKETCHES_CONTROLLER) || 
        controllerId != null && controllerId.equals(RequestValue.SKETCH_CONTROLLER)) {
	    sketches.addStyleName("sd-app-PrimaryNavigationView-focus");
	  } else {
	    sketches.removeStyleName("sd-app-PrimaryNavigationView-focus");
	  }

    if (controllerId != null && controllerId.equals(RequestValue.PROJECT_ARCHITECTURE_CONTROLLER) ||
        (controllerId != null && controllerId.equals(RequestValue.PAGE_CONTROLLER) && !dashEnabled)) {
      documentation.addStyleName("sd-app-PrimaryNavigationView-focus");
    } else {
      documentation.removeStyleName("sd-app-PrimaryNavigationView-focus");
    }
    
    if (controller.getContext().getProjectId() == null) {
      allProjects.addStyleName("sd-app-PrimaryNavigationView-focus");
      tilesEngine.setTile(TileId.PRIMARY_NAVIGATION, projectsPanel);
      projectsPanel.setVisible(true);
      projectsPanel.add(allProjects);
    } else {
      allProjects.removeStyleName("sd-app-PrimaryNavigationView-focus");
      projectsPanel.setVisible(false);
      projectsPanel.remove(allProjects);
      panel.add(allProjects);
    }
	}

  public void onClick(ClickEvent event) {
//    dp.clear();
    for (int i = 0; i < panel.getWidgetCount(); ++i) {
      Widget w = panel.getWidget(i);
      if (w == event.getSource()) {
        w.addStyleName("sd-app-PrimaryNavigationView-focus");
//        dp.add(w);
      } else {
        w.removeStyleName("sd-app-PrimaryNavigationView-focus");
      }
    }

    Map<Object,Object>requests = new HashMap<Object, Object>();
    if (event.getSource() == dashboard) {
      requests.put(RequestId.CONTROLLER, RequestValue.PAGE_CONTROLLER);
      Long dashboardId = controller.getContext().getProject().getDashboard().getId();
      requests.put(RequestId.PAGE_ID, dashboardId);
      requests.put(RequestId.PROJECT_ID, controller.getContext().getProjectId());
    } else if (event.getSource() == sketches) {
      requests.put(RequestId.CONTROLLER, RequestValue.SKETCHES_CONTROLLER);
      requests.put(RequestId.PROJECT_ID, controller.getContext().getProjectId());
    } else if (event.getSource() == documentation) {
      requests.put(RequestId.CONTROLLER, RequestValue.PROJECT_ARCHITECTURE_CONTROLLER);
      requests.put(RequestId.PROJECT_ID, controller.getContext().getProjectId());
    } else if (event.getSource() == allProjects) {
      requests.put(RequestId.CONTROLLER, RequestValue.PROJECTS_CONTROLLER);
    }
    
    RequestUtils.activate(requests);
  }

  public void setRequest(Map requests) {
    this.request = requests;
  }

}
