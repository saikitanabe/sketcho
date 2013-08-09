package net.sevenscales.sketcho.client.app.view.components;

import java.util.List;

import net.sevenscales.appFrame.impl.Action;
import net.sevenscales.appFrame.impl.IController;

import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MenuComponent extends SimplePanel {
	private VerticalPanel menuItems;
	private DisclosurePanel menu;
	
	public MenuComponent
			(String titleText,
			 IController controller,
			 List actions) {

		// construct menu
		menu = new DisclosurePanel(titleText, true);

		menuItems = new VerticalPanel();
		for (int i = 0; i < actions.size(); ++i) {
			Action a = (Action) actions.get(i);
			menuItems.add(a.getWidget());
		}
		menu.setContent(menuItems);
		
		menuItems.addStyleName("projects-Menu-Content");
		menu.getHeader().addStyleName("projects-Menu-Header");
		menu.addStyleName("projects-Menu");
		
		setWidget(menu);
	}
}
