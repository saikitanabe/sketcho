package net.sevenscales.sketcho.client.app.view;

import net.sevenscales.appFrame.api.IContributor;
import net.sevenscales.appFrame.impl.Action;
import net.sevenscales.appFrame.impl.DynamicParams;
import net.sevenscales.appFrame.impl.IController;
import net.sevenscales.appFrame.impl.ITilesEngine;
import net.sevenscales.appFrame.impl.View;
import net.sevenscales.plugin.constants.TileId;

import com.google.gwt.user.client.ui.VerticalPanel;

public class MenuNavigationView extends View {
	private VerticalPanel menuNavigation;
	private Action openProjects;
	private Action adminPage;

	public MenuNavigationView(IController controller) {
		super(controller);
		// construct footer
		menuNavigation = new VerticalPanel();

//		openProjects = ActionFactory
//			.createLinkAction("Projects", ActionId.PROJECTS, controller);
//		menuNavigation.add(openProjects);
//
//		adminPage = ActionFactory
//			.createLinkAction("Admin", ActionId.ADMIN_PAGE, controller);
//		menuNavigation.add(adminPage);

		menuNavigation.addStyleName("menuNavigationView-Style");
	}

	public void activate(ITilesEngine tilesEngine, 
						 DynamicParams params, IContributor contributor) {
//		if (globalState.isActive(openProjects)) {
//			openProjects.addStyleName("menuNavigationView-Active-Projects");
//			adminPage.removeStyleName("menuNavigationView-Active-Admin");
//		} else if (globalState.isActive(adminPage)) {
//			openProjects.removeStyleName("menuNavigationView-Active-Projects");
//			adminPage.addStyleName("menuNavigationView-Active-Admin");
//		}

		tilesEngine.setTile(TileId.MENU_NAVIGATION, menuNavigation);
	}
}
