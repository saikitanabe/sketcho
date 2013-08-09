package net.sevenscales.sketcho.client.app.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sevenscales.appFrame.api.IRegistryEventObserver;
import net.sevenscales.appFrame.impl.Action;
import net.sevenscales.appFrame.impl.ActivationObserver;
import net.sevenscales.appFrame.impl.ClassInfo;
import net.sevenscales.appFrame.impl.ControllerBase;
import net.sevenscales.appFrame.impl.DynamicParams;
import net.sevenscales.appFrame.impl.Handler;
import net.sevenscales.appFrame.impl.RequestUtils;
import net.sevenscales.appFrame.impl.View;
import net.sevenscales.domain.api.ILabel;
import net.sevenscales.domain.api.IProject;
import net.sevenscales.domain.dto.LabelDTO;
import net.sevenscales.plugin.api.Context;
import net.sevenscales.plugin.api.UiNotifier;
import net.sevenscales.plugin.constants.RequestId;
import net.sevenscales.plugin.constants.RequestValue;
import net.sevenscales.plugin.constants.SdRegistryEvents;
import net.sevenscales.serverAPI.remote.LabelRemote;
import net.sevenscales.sketcho.client.app.view.LabelView;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class LabelController extends ControllerBase<Context> {

  private LabelView view;
  private IProject project;

  public static ClassInfo info() {
    return new ClassInfo() {
      public Object createInstance(Object data) {
        return new LabelController((Context) data);
      }

      public Object getId() {
        return LabelController.class;
      }

      // public boolean isGlobal() {
      // return true;
      // }
    };
  }

  private LabelController(Context context) {
    super(context);
    getContext().getEventRegistry().register(SdRegistryEvents.LABEL_CHANGED, new IRegistryEventObserver() {
      public void handleEvent(Integer eventId, Object data) {
        reloadLabels();
      }
    });

    view = new LabelView(this, new LabelView.ILabelViewListener() {
      public void newLabel() {
        view.newLabel();
        // Map requests = new HashMap<Object, Object>();
        // requests.put(RequestId.CONTROLLER, RequestValue.PROJECT_CONTROLLER);
        // requests.put(RequestId.PROJECT_ID, getContext().getProjectId());
        // String queries = Location.formatRequests(requests);
        // History.newItem(queries);
      }

      public void createLabel(String text) {
        ILabel l = new LabelDTO();
        l.setProject(getContext().getProject());
        l.setValue(text);
        LabelRemote.Util.inst.save(l, new AsyncCallback<ILabel>() {
          public void onSuccess(ILabel result) {
            reloadLabels();
          }

          public void onFailure(Throwable caught) {
            UiNotifier.instance().showError("Label creation failed");
          }
        });
      }
      
      public void manageLabels() {
        Map<Object, Object> requests = new HashMap<Object, Object>();
        requests.put(RequestId.CONTROLLER, RequestValue.MANAGE_LABELS_CONTROLLER);
        requests.put(RequestId.PROJECT_ID, getContext().getProjectId());
        RequestUtils.activate(requests);
      }

      public void changeLabelColor(ILabel label, String bc, String color) {
        label.setBackgroundColor(bc);
        label.setTextColor(color);
        LabelRemote.Util.inst.update(label, new AsyncCallback<ILabel>() {
          public void onSuccess(ILabel result) {
            // -notify sketches list to be reloaded
            // -- label color change event
            getContext().getEventRegistry().handleEvent(SdRegistryEvents.LABEL_CHANGED, null);
          }
          public void onFailure(Throwable caught) {
            UiNotifier.instance().showError("Label color save failed");
          }
        });
      }
      
      public void open(ILabel label) {
        getContext().getEventRegistry().handleEvent(SdRegistryEvents.OPEN_SKETCHES_BY_LABELS, label);
      }
    });
  }

  public void activate(Map requests, ActivationObserver observer) {
    view.setProject(null);
    Context context = getContext();
    // observer.activated(this, null);
    final ActivationObserver activationObserver = observer;

    if (context.getProjectId() != null) {
      reloadLabels();
    } else {
      // DynamicParams params = new DynamicParams();
      // params.addParam(ParamId.PROJECT_PARAM, null);
      activationObserver.activated(LabelController.this, null);
    }
  }

  private void reloadLabels() {
    LabelRemote.Util.inst.findAll(getContext().getProjectId(),
        new AsyncCallback<List<ILabel>>() {
          public void onSuccess(List<ILabel> result) {
            view.setProject(getContext().getProject());
            view.setLabels(result);
            view.activate(getContext().getTilesEngine(), null, null);
          }

          public void onFailure(Throwable caught) {
            UiNotifier.instance().showError("Labels open failed: " + caught);
          }
        });
  }

  public void activate(DynamicParams params) {
  }

  public Handler createHandlerById(Action action) {
    return null;
  }

  public View getView() {
    return view;
  }

}
