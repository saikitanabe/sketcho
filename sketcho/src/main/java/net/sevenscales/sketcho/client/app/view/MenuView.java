package net.sevenscales.sketcho.client.app.view;

import net.sevenscales.appFrame.api.IContributor;
import net.sevenscales.appFrame.impl.DynamicParams;
import net.sevenscales.appFrame.impl.IController;
import net.sevenscales.appFrame.impl.ITilesEngine;
import net.sevenscales.appFrame.impl.View;
import net.sevenscales.domain.api.IProject;
import net.sevenscales.plugin.api.Context;
import net.sevenscales.plugin.api.IPermissionContributor;
import net.sevenscales.plugin.constants.TileId;

public class MenuView extends View<Context> {
  
  private IProject project;
//  private SimplePanel sizerPanel = new SimplePanel();
//  private VerticalPanel main;
  private MenuViewCompo menuViewCompo;
  
  public interface IMenuViewListener {
    void newSketch();
  }
  
	public MenuView(IController<Context> controller, IMenuViewListener listener) {
		super(controller);
		this.menuViewCompo = new MenuViewCompo(controller, listener);
//		this.main = new VerticalPanel();
		
//		HorizontalPanel newpanel = new HorizontalPanel();
//		newpanel.setWidth("100%");
//		newpanel.setBorderWidth(1);
//		this.newSketch = new HTML("New Sketch");
//		newSketch.addStyleName("MenuView-Link");
//		
//		Image img = new Image("images/add.png");
//		newpanel.add(img);
//		newpanel.setCellVerticalAlignment(img, HorizontalPanel.ALIGN_MIDDLE);
//    newpanel.setCellHorizontalAlignment(img, HorizontalPanel.ALIGN_RIGHT);
//		newpanel.add(newSketch);
//    newpanel.setCellHorizontalAlignment(newSketch, HorizontalPanel.ALIGN_LEFT);
//
//		main.add(newpanel);
		
//		newpanel.addClickHandler(new ClickHandler() {
//      public void onClick(ClickEvent event) {
//        MenuView.this.listener.newSketch();
//      }
//    });
		
//    sizerPanel.setWidget(main);
//    sizerPanel.setWidth("175px");
//    sizerPanel.setStyleName("sd-app-HierarchyView");
	}

  public void activate(ITilesEngine tilesEngine, 
						 DynamicParams params, IContributor contributor) {
    IPermissionContributor permissionContributor = controller.getContext().getContributor().cast(
        IPermissionContributor.class);
    if (controller.getContext().getProjectId() == null) {
      tilesEngine.clear(TileId.CONTENT_LEFT_MENU);
    } else {
      tilesEngine.setTile(TileId.CONTENT_LEFT_MENU, menuViewCompo);
    }
		
    menuViewCompo.checkAcl(permissionContributor);
    menuViewCompo.setFocus();

    // TODO: get event when project is loaded
//		this.project = (IProject) params.getParam(ParamId.PROJECT_PARAM);
		
//    newSketch.setVisible(project != null);
  }
}