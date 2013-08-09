package net.sevenscales.appFrame.impl;

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Hyperlink;

import java.util.Map;

public class LinkAction extends Action { 
	private String name;
	private HyperL hyperlink;
	
	private class HyperL extends Hyperlink {
	  @Override
	  public void onBrowserEvent(Event event) {
	    if (!getTargetHistoryToken().equals(History.getToken())) {
	      super.onBrowserEvent(event);
	    } else {
	      // same request doesn't get otherwise activated
	      History.fireCurrentHistoryState();
	    }
	  }
	}

	public LinkAction() {
		hyperlink = new HyperL();
		initWidget(hyperlink);
	}

	public LinkAction(Map requests) {
		hyperlink = new HyperL();
		setRequest(requests);
		initWidget(hyperlink);
	}
	
	public void setRequest(Map requests) {
	  if (requests != null) {
  		String queries = Location.formatRequests(requests);
  		hyperlink.setTargetHistoryToken(queries);
	  }
	}

	public void setName(String name) {
		this.name = name;
		hyperlink.setHTML(this.name);
	}

	public String getName() {
		return name;
	}
	
}
