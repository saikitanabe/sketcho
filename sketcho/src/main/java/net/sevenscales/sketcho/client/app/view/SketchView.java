package net.sevenscales.sketcho.client.app.view;

import java.util.HashMap;
import java.util.Map;

import net.sevenscales.appFrame.api.IContributor;
import net.sevenscales.appFrame.impl.Action;
import net.sevenscales.appFrame.impl.ActionFactory;
import net.sevenscales.appFrame.impl.DynamicParams;
import net.sevenscales.appFrame.impl.IController;
import net.sevenscales.appFrame.impl.ITilesEngine;
import net.sevenscales.plugin.api.Context;
import net.sevenscales.plugin.constants.RequestId;
import net.sevenscales.plugin.constants.RequestValue;

public class SketchView extends PageView {

  public SketchView(IController<Context> controller, ICommandCallback callback) {
    super(controller, callback);
  }

  @Override
  public void activate(ITilesEngine tilesEngine, DynamicParams params,
      IContributor contributor) {
    super.activate(tilesEngine, params, contributor);
    

    Map requests = new HashMap();
    requests.put(RequestId.CONTROLLER, RequestValue.SKETCHES_CONTROLLER);
    requests.put(RequestId.PROJECT_ID, controller.getContext().getProjectId());
    Action sketches = ActionFactory.createLinkAction("Sketches"+" &laquo;", requests);
    sketches.setStyleName("page-Hierarchy");

    getHierarchy().insert(sketches, 0);
  }
}
