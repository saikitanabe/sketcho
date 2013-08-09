package net.sevenscales.sketcho.client.app.view;

import net.sevenscales.appFrame.api.IContributor;
import net.sevenscales.appFrame.impl.DynamicParams;
import net.sevenscales.appFrame.impl.IController;
import net.sevenscales.appFrame.impl.ITilesEngine;
import net.sevenscales.appFrame.impl.View;
import net.sevenscales.plugin.api.Context;
import net.sevenscales.plugin.api.ITopNavigationContributor;
import net.sevenscales.plugin.api.ITopNavigationContributor.ITopNaviPanel;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

public class TopNavigationView extends View<Context> {

  public TopNavigationView(IController<Context> controller) {
		super(controller);
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
	
	static class NavigationPanel extends HorizontalPanel implements ITopNaviPanel {
	  public void addItem(Widget widget) {
	    if (getWidgetCount() > 0) {
	      
	      Widget w = getWidget(getWidgetCount() -1);
	      if ( !(w instanceof RightLinkHTML) ) {
	        super.add(new RightLinkHTML("|"));
	      }
	    }
      
      widget.setStyleName("topPanel-Item-Right");
      super.add(widget);
	  }

	  public void insertItem(Widget widget, int index) {
      widget.setStyleName("topPanel-Item-Right");
	    super.insert(widget, index);
      super.insert(new RightLinkHTML("|"), index + 1);
	  }
	}

	public void activate(ITilesEngine tilesEngine, 
						 DynamicParams params, IContributor contributor) {
    Context c = (Context) controller.getContext();
    
		HorizontalPanel left = new HorizontalPanel();
		
		// add to left
//    Map requests = new HashMap();
//    requests.put(RequestId.CONTROLLER, RequestValue.PROJECTS_CONTROLLER);
//    
//    Action a = ActionFactory.createLinkAction("All projects", requests);
//    a.setStyleName("topPanel-Item-Left");
//    left.add(a);

    HTML report = new HTML("<a href='http://7issues.appspot.com' target='_blank'>Report a bug");
    report.setStyleName("topPanel-Item-Left");
    left.add(report);

    HTML tos = new HTML("<a href='http://7scales.net/7scales/tos.html' target='_blank'>Terms of Service");
    tos.setStyleName("topPanel-Item-Left");
    left.add(tos);

    
//		left.add(new LeftLinkHTML("Report a bug"));
		
		// TODO temporarily removed until server supports 
		// e.g. Trac
//		left.add(new LeftLinkHTML("<b>Shared Design</b>"));
//		
//		if (c.getProjectId() != null) { 
//      left.add(new LeftLinkHTML("|"));
//  		left.add(new LeftLinkHTML("<a href='javascript:;'>Product Backlog</a>"));
//      left.add(new LeftLinkHTML("|"));
//  		left.add(new LeftLinkHTML("<a href='javascript:;'>Sprint Backlog</a>"));
//      left.add(new LeftLinkHTML("|"));
//  		left.add(new LeftLinkHTML("<a href='javascript:;'>Bugs</a>"));
//		}
		
		tilesEngine.setTile("top-left-navigation", left);

		NavigationPanel right = new NavigationPanel();
		ITopNavigationContributor topContributor = contributor.cast
		  (ITopNavigationContributor.class);
	  topContributor.addToRight(right);

		tilesEngine.setTile("top-right-navigation", right);
	}

}
