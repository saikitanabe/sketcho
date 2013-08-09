package net.sevenscales.sketcho.client.app.controller;

import java.util.HashMap;
import java.util.Map;

import net.sevenscales.appFrame.impl.ActivationObserver;
import net.sevenscales.appFrame.impl.ClassInfo;
import net.sevenscales.appFrame.impl.DynamicParams;
import net.sevenscales.appFrame.impl.RequestUtils;
import net.sevenscales.appFrame.impl.View;
import net.sevenscales.domain.api.IPage;
import net.sevenscales.plugin.api.Context;
import net.sevenscales.plugin.constants.ActionId;
import net.sevenscales.plugin.constants.RequestId;
import net.sevenscales.plugin.constants.RequestValue;
import net.sevenscales.serverAPI.remote.PageRemote;
import net.sevenscales.sketcho.client.app.view.SketchView;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class SketchController extends PageController {
  private SketchView view;
  private boolean newSketch;

  public static ClassInfo info() {
    return new ClassInfo() {
      public Object createInstance(Object data) {
        return new SketchController((Context) data);
      }

      public Object getId() {
        return SketchController.class;
      }
    };
  }

  protected SketchController(Context context) {
    super(context);
  }
  
  @Override
  public void activate(Map requests, ActivationObserver observer) {
    Integer actid = RequestUtils.parseInt(RequestId.ACTION_ID, requests);
    
    if (actid != null && actid.equals(ActionId.NEW_PAGE)) {
      // New Sketch
      PageRemote.Util.inst.createPage("net.sevenscales.server.domain.SketchTemplate", getContext().getProjectId(), new AsyncCallback<IPage>() {
        public void onSuccess(IPage result) {
          // need to reload page with a new request
          // to get rid of action to create new page from browser location bar (search)
          Map<Object, String> requests = new HashMap<Object, String>();
          requests.put(RequestId.CONTROLLER, RequestValue.SKETCH_CONTROLLER);
          requests.put(RequestId.PROJECT_ID, String.valueOf(result.getProject().getId()));
          requests.put(RequestId.PAGE_ID, String.valueOf(result.getId()));
          RequestUtils.activate(requests);
          newSketch = true;
        }
        public void onFailure(Throwable t) {
          System.out.println("FAILURE: createPage"+t);
        }
      });
    } else {
      // open existing page
      super.activate(requests, observer);
    }
  }
  
  @Override
  public void activate(DynamicParams params) {
    super.activate(params);
    if (newSketch) {
      view.makePageTitleAsEdit();
    }
    
    // do not edit later
    newSketch = false;
  }

  @Override
  public View getView() {
    if (view == null) {
      view = new SketchView(this, commandCallback);
    }
    return view;
  }
  
//  public Handler createHandlerById(Action action) {
//    return super.createHandlerById(action);
//  }
  
//  public T getContext();
//  public boolean isVisible();
//  public void setVisible(boolean visible);

}
