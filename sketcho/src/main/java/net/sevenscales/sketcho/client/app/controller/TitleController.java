package net.sevenscales.sketcho.client.app.controller;

import java.util.Map;

import net.sevenscales.appFrame.impl.Action;
import net.sevenscales.appFrame.impl.ActivationObserver;
import net.sevenscales.appFrame.impl.ClassInfo;
import net.sevenscales.appFrame.impl.ControllerBase;
import net.sevenscales.appFrame.impl.DynamicParams;
import net.sevenscales.appFrame.impl.Handler;
import net.sevenscales.appFrame.impl.View;
import net.sevenscales.plugin.api.Context;
import net.sevenscales.plugin.constants.TileId;
import net.sevenscales.sketcho.client.app.view.TitleView;

public class TitleController extends ControllerBase<Context> {
  private TitleView view = new TitleView();

  public static ClassInfo info() {
    return new ClassInfo() {
      public Object createInstance(Object data) {
        return new TitleController((Context) data);
      }

      public Object getId() {
        return TitleController.class;
      }
      
    };
  }
	
	private TitleController(Context context) {
	  super(context);
	}

	public void activate(Map requests, ActivationObserver observer) {
	  if (getContext().getProject() != null) {
	    getContext().getTilesEngine().setTile(TileId.TITLE, view);
	    view.setProjectName(getContext().getProject().getName());
	  } else {
	    view.setProjectName("");
	  }
	}

	public void activate(DynamicParams params) {
	}

	public Handler createHandlerById(Action action) {
    return null;
	}

	public View getView() {
	  return null;
	}
  
}
