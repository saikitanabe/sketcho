package net.sevenscales.sketcho.client.app.view;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sevenscales.appFrame.api.IContributor;
import net.sevenscales.appFrame.impl.Action;
import net.sevenscales.appFrame.impl.ActionFactory;
import net.sevenscales.appFrame.impl.DynamicParams;
import net.sevenscales.appFrame.impl.IController;
import net.sevenscales.appFrame.impl.ITilesEngine;
import net.sevenscales.appFrame.impl.View;
import net.sevenscales.domain.api.IPage;
import net.sevenscales.domain.api.IProject;
import net.sevenscales.domain.utils.PageIterator;
import net.sevenscales.plugin.api.Context;
import net.sevenscales.plugin.api.IPermissionContributor;
import net.sevenscales.plugin.constants.RequestId;
import net.sevenscales.plugin.constants.RequestValue;
import net.sevenscales.plugin.constants.TileId;
import net.sevenscales.sketcho.client.app.utils.PageListFormatter;
import net.sevenscales.sketcho.client.app.utils.PageListFormatter.INameFormatter;
import net.sevenscales.sketcho.client.app.view.constants.ParamId;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class HierarchyView extends View<Context> {
  private DisclosurePanel main;
  private Tree pages;
  private IProject project;
  private HTML organize;
  private DecoratorPanel decorator = new DecoratorPanel();
  private SimplePanel sizerPanel = new SimplePanel();
  private DecoratedPopupPanel simplePopup = new DecoratedPopupPanel(true);
  private HTML newPage;
  private IHierarchyViewListener listener;
  
  public interface IHierarchyViewListener {
    public void newPage();

    public void organize();
  }
  
	public HierarchyView(IController<Context> controller, IHierarchyViewListener listener) {
		super(controller);
		this.listener = listener;
//		decorator.addStyleName("sd-app-HierarchyView");
		main = new DisclosurePanel("Design Map");
		main.setOpen(false);
    main.setWidth("100%");
//		main.addStyleName("sd-app-HierarchyView");

    pages = new Tree();
    pages.addStyleName("sd-app-HierarchyView-Tree");
    
//    this.organize = ActionFactory
//      .createButtonAction("Organize", ActionId.ORGANIZE, controller);
    this.organize = new HTML("Organize");
    organize.addStyleName("HierarchyView-Button");
    organize.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        HierarchyView.this.listener.organize(); 
      }
    });
    
    this.newPage = new HTML("New Page");
    newPage.addStyleName("HierarchyView-Button");
    newPage.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        HierarchyView.this.listener.newPage(); 
      }
    });

    VerticalPanel panel = new VerticalPanel();
    panel.setSpacing(0);
    
    HorizontalPanel buttons = new HorizontalPanel();
//    buttons.setWidth("135px");
    buttons.setSpacing(5);
    buttons.add(newPage);
    buttons.add(organize);
    panel.add(buttons);
    panel.add(pages);
    
    main.setContent(panel);
    pages.setWidth("145px");
    sizerPanel.setWidget(main);
    sizerPanel.setWidth("175px");
    sizerPanel.setStyleName("sd-app-HierarchyView");
    decorator.add(sizerPanel);
	}

  public void activate(ITilesEngine tilesEngine, 
						 DynamicParams params, IContributor contributor) {
		tilesEngine.setTile(TileId.CONTENT_LEFT2, decorator);
		
    IPermissionContributor permissionContributor = controller.getContext().getContributor().cast(
        IPermissionContributor.class);
    newPage.setVisible(permissionContributor.hasEditPermission());
    organize.setVisible(permissionContributor.hasEditPermission());

		this.project = (IProject) params.getParam(ParamId.PROJECT_PARAM);
		
    pages.clear();
    INameFormatter formatter = new INameFormatter() {
      public Widget format(IPage page, int level) {
        if (level > 0) {
          // Skip dashboard
          Map<Object, Object> requests = new HashMap<Object, Object>();
          requests.put(RequestId.CONTROLLER, RequestValue.PAGE_CONTROLLER);
          requests.put(RequestId.PAGE_ID, page.getId());
          requests.put(RequestId.PROJECT_ID, project.getId());
          
          final IPage finalPage = page;
          final Action a = ActionFactory.createLinkAction(page.getName(), requests);
//          a.addKeyDownHandler(new KeyDownHandler() {
//            public void onKeyDown(KeyDownEvent event) {
//            }
//          });
//          simplePopup.ad
          
          a.addMouseOverHandler(new MouseOverHandler() {
            public void onMouseOver(MouseOverEvent event) {
              simplePopup.hide();
              a.setHover(true);
//            simplePopup.ensureDebugId("cwBasicPopup-simplePopup");
//            simplePopup.setWidth("150px");
//              
              final Widget source = (Widget) event.getSource();
              int left = source.getAbsoluteLeft() + 25;
              int top = source.getAbsoluteTop() + 15;
              simplePopup.setPopupPosition(left, top);
              simplePopup.setWidget(new HTML("[[wiki("+finalPage.getId()+","+finalPage.getName()+")]]"));
              
              Timer timer = new Timer() {
                public void run() {
                  if (a.isHover()) {
  //                  Widget source = (Widget) event.getSource();
                    // Show the popup
                    if (controller.getContext().isEditMode()) {
                      simplePopup.show();
                    }
                  }
                }
              };
              timer.schedule(1500);
            }
          });
          
          a.addMouseOutHandler(new MouseOutHandler() {
            public void onMouseOut(MouseOutEvent event) {
              a.setHover(false);
            }
          });
          
          
//          a.setTitle("[[wiki("+page.getId()+","+page.getName()+")]]");
          return a;
        }
        return null;
      }
    };
    
    decorator.setVisible(project != null);
    if (project != null) {
      PageIterator pi = new PageIterator(project.getDashboard(), new PageListFormatter(project, pages, formatter));
      pi.iterate();
  
      // open tree
      Iterator<TreeItem> i = pages.treeItemIterator();
      while (i.hasNext()) {
        i.next().setState(true);
      }
    }

  }
}