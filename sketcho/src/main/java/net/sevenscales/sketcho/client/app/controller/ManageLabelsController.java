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
import net.sevenscales.sketcho.client.app.view.ManageLabelsView;
import net.sevenscales.sketcho.client.app.view.ManageLabelsView.IManageViewCallback;

public class ManageLabelsController extends ControllerBase<Context> {

    private ManageLabelsView view;
    
    private ManageLabelsController(Context context) {
      super(context);
      view = new ManageLabelsView(this, new IManageViewCallback() {
      });
    }
    
    public static ClassInfo info() {
      return new ClassInfo() {
        public Object createInstance(Object data) {
          return new ManageLabelsController((Context) data);
        }

        public Object getId() {
          return ManageLabelsController.class;
        }
      };
    }

    public void activate(Map requests, ActivationObserver observer) {
      view.activate(getContext().getTilesEngine(), null, null);
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
