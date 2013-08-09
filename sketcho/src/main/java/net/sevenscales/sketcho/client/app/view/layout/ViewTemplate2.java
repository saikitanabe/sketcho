package net.sevenscales.sketcho.client.app.view.layout;

import net.sevenscales.appFrame.impl.TilesEngineComponent;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ViewTemplate2 extends Composite {
	private DockPanel mainPanel;
	private Widget header;
	private Widget body;
	private Widget menu;
	private Widget footer;

	public ViewTemplate2(TilesEngineComponent tilesEngine) {
		VerticalPanel root = new VerticalPanel();
		tilesEngine.setContainer(root);

		// top navigation: left + right navigation
		HorizontalPanel topNavigation = new HorizontalPanel();
		Widget left = tilesEngine.createTile("top-left-navigation", new SimplePanel());
		topNavigation.add(left);
		
		Widget right = tilesEngine.createTile("top-right-navigation", new SimplePanel());
		topNavigation.add(right);
		
		topNavigation.setCellHorizontalAlignment(left, HorizontalPanel.ALIGN_LEFT);
		topNavigation.setCellHorizontalAlignment(right, HorizontalPanel.ALIGN_RIGHT);
		topNavigation.setWidth("100%");
		
		root.add(topNavigation);
		
		// title area
		root.add(tilesEngine.createTile("title-area", new SimplePanel()));

		// menu + body
		HorizontalPanel belly = new HorizontalPanel();
		belly.add(tilesEngine.createTile("menu", new SimplePanel()));
		belly.add(tilesEngine.createTile("body", new SimplePanel()));
		root.add(belly);

		// footer
		Widget footer = tilesEngine.createTile("footer", new SimplePanel());
		topNavigation.setCellHorizontalAlignment(footer, HorizontalPanel.ALIGN_CENTER);
		footer.setWidth("100%");
		root.add(footer);
		
		initWidget(root);
	}
}
