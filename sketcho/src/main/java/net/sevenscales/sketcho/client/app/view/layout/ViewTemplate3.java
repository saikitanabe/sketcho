package net.sevenscales.sketcho.client.app.view.layout;

import java.util.ArrayList;
import java.util.List;

import net.sevenscales.appFrame.impl.ITilesEngine;
import net.sevenscales.plugin.constants.TileId;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ViewTemplate3 extends Composite {
  private static List<String> dynamicAreas;
  static {
    dynamicAreas = new ArrayList<String>();
//    dynamicAreas.add(TileId.TITLE);
//    dynamicAreas.add(TileId.COMMAND_AREA);
//    dynamicAreas.add(TileId.CONTENT);
//    dynamicAreas.add(TileId.CONTENT_LEFT);
  }

	public ViewTemplate3(ITilesEngine tilesEngine) {

		VerticalPanel root = new VerticalPanel();
		root.setStyleName("sd-app-ViewTemplate3-Root");
		tilesEngine.setContainer(root);

		root.add(createTopNavigation(tilesEngine));
//		root.add(createTitleArea(tilesEngine));
		root.add(createNotifyArea(tilesEngine));
		SimplePanel separator = new SimplePanel();
		separator.setStyleName("separator");
		root.add(separator);
		root.add(createBelly(tilesEngine));
		root.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
		root.add(createFooter(tilesEngine));
		
		tilesEngine.setDynamicAreas(dynamicAreas);

		root.setWidth("100%");
		
		initWidget(root);
	}
		
	private Widget createFooter(ITilesEngine tilesEngine) {
		// footer
		SimplePanel footer = (SimplePanel) tilesEngine.createTile
			(TileId.FOOTER, new SimplePanel());
		footer.setStyleName("footer-Area");
		
		// need to set cell width in here, because otherwise content will not 
		// reserve whole width
		return footer;
	}
	
	private Widget createCommandArea(ITilesEngine tilesEngine) {
    // command area
    SimplePanel commandArea = (SimplePanel) tilesEngine.createTile
      (TileId.COMMAND_AREA, new SimplePanel());
    commandArea.setWidth("100%");
    commandArea.setStyleName("command-Area");
    return commandArea;
	}
	
  private Widget createNotifyArea(ITilesEngine tilesEngine) {
    // command area
    SimplePanel commandArea = (SimplePanel) tilesEngine.createTile
      (TileId.NOTIFY_AREA, new SimplePanel());
    commandArea.setStyleName("notify-Area");
    return commandArea;
	}

	private Widget createBelly(ITilesEngine tilesEngine) {
		// left + content
	  HorizontalPanel mainBelly = new HorizontalPanel();
	  
	  mainBelly.setWidth("100%");
	  mainBelly.setStyleName("belly-Area");
	  
	  VerticalPanel leftPanel = new VerticalPanel();
	  SimplePanel left1 = (SimplePanel) tilesEngine.createTile
      (TileId.CONTENT_LEFT, new SimplePanel());
    left1.setStyleName("sd-app-ViewTemplate3-ContentLeft");
    SimplePanel left2 = (SimplePanel) tilesEngine.createTile
      (TileId.CONTENT_LEFT2, new SimplePanel());
    left2.setStyleName("sd-app-ViewTemplate3-ContentLeft");
    
    VerticalPanel logoWrapper = new VerticalPanel();
    final Image icon = new Image("images/sketcho11.png");
    logoWrapper.add(icon);
    logoWrapper.setStyleName("sd-app-ViewTemplate3-Icon");
    logoWrapper.setWidth("100%");
    leftPanel.add(logoWrapper);

//    leftPanel.add(createPrimaryNavigation(tilesEngine));
	  leftPanel.add(left1);
    leftPanel.add(left2);
	  
	  mainBelly.add(leftPanel);

	  VerticalPanel mainContent = new VerticalPanel();
	  mainContent.setWidth("100%");
	  
    Widget title = tilesEngine.createTile(TileId.TITLE, new SimplePanel());
    title.setWidth("100%");
    title.setStyleName("sd-app-ViewTemplate3-Title");
    mainContent.add(title);
	  
    Widget primary = createPrimaryNavigation(tilesEngine);
    mainContent.add(primary);
    mainContent.setCellWidth(primary, "100%");
    
    VerticalPanel sub = new VerticalPanel();
    sub.setStyleName("sd-app-ViewTemplate3-MainContent");
    sub.setWidth("100%");
    
//    SimplePanel space = new SimplePanel();
//    mainBelly.add(space);
//    space.setWidth("7px");
//    mainBelly.setCellWidth(space, "7px");
    
    DecoratorPanel d = new DecoratorPanel();
    d.setWidth("100%");
    d.setHeight("100%");

    d.add(sub);
    mainContent.add(d);
    mainContent.setCellWidth(d, "100%");
    
	  mainBelly.add(mainContent);
	  mainBelly.setCellWidth(mainContent, "100%");
	  
	  sub.add(createCommandArea(tilesEngine));

		{ // content
			SimplePanel content = (SimplePanel) tilesEngine.createTile(TileId.CONTENT, new SimplePanel());
			sub.add(content);
			content.setStyleName("content-Area");
			sub.setCellWidth(content, "100%");
			sub.setCellHorizontalAlignment(content, HasHorizontalAlignment.ALIGN_LEFT);
		}
		
		return mainBelly;
	}

	private Widget createTitleArea(ITilesEngine tilesEngine) {
	  HorizontalPanel panel = new HorizontalPanel();
	  panel.setWidth("100%");
//	  panel.setStylePrimaryName("sd-app-ViewTemplate3-title");
	  panel.setSpacing(0);

    final Image icon = new Image("images/sketcho10.png");
    icon.setStyleName("sd-app-ViewTemplate3-Icon");
    panel.add(icon);

    Widget title = tilesEngine.createTile(TileId.TITLE, new SimplePanel());
    title.setWidth("100%");
    title.setStyleName("sd-app-ViewTemplate3-Title");
    panel.add(title);

//		title.setStyleName("title-Area");
		
    
//    Widget primary = createPrimaryNavigation(tilesEngine);
//    primary.setWidth("100%");
//    primary.setStyleName("primary-navigation");
//
//    panel.add(primary);
//    panel.setCellVerticalAlignment(primary, VerticalPanel.ALIGN_BOTTOM);
    
		return panel;
	}
	
	private Widget createTopNavigation(ITilesEngine tilesEngine) {
		// top navigation: left + right navigation
//    final Image icon = new Image("images/sketcho10.png");
//    icon.setStyleName("sd-app-ViewTemplate3-Icon");
//    main.add(icon);

		HorizontalPanel topNavigation = new HorizontalPanel();
		
		Widget left = tilesEngine.createTile(TileId.TOP_LEFT_NAVIGATION, new SimplePanel());
		topNavigation.add(left);

		Widget right = tilesEngine.createTile(TileId.TOP_RIGHT_NAVIGATION, new SimplePanel());
		topNavigation.add(right);

		topNavigation.setCellHorizontalAlignment(left, HorizontalPanel.ALIGN_LEFT);
		topNavigation.setCellHorizontalAlignment(right, HorizontalPanel.ALIGN_RIGHT);

		topNavigation.setStyleName("sd-app-ViewTemplate3-top-Navigation");
		
		return topNavigation;
	}
	
	private Widget createPrimaryNavigation(ITilesEngine tilesEngine) {
    // top navigation: left + right navigation
    HorizontalPanel topNavigation = new HorizontalPanel();
//    topNavigation.setWidth("100%");
    
    Widget primary = tilesEngine.createTile(TileId.PRIMARY_NAVIGATION, new SimplePanel());
    primary.setStyleName("sd-app-ViewTemplate3-PrimaryNavigation");
    primary.setWidth("100%");

    topNavigation.add(primary);

    topNavigation.setCellHorizontalAlignment(primary, HorizontalPanel.ALIGN_LEFT);

    return topNavigation;
  }

}
