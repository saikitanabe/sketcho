package net.sevenscales.sketcho.client.app.controller;

import java.util.ArrayList;
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
import net.sevenscales.appFrame.impl.HandlerBase;
import net.sevenscales.appFrame.impl.Location;
import net.sevenscales.appFrame.impl.RequestUtils;
import net.sevenscales.appFrame.impl.View;
import net.sevenscales.domain.api.ILabel;
import net.sevenscales.domain.api.IPage;
import net.sevenscales.domain.api.IPageWithNamedContentValues;
import net.sevenscales.domain.api.IProject;
import net.sevenscales.domain.api.SketchesSearch;
import net.sevenscales.domain.constants.Constants;
import net.sevenscales.plugin.api.Context;
import net.sevenscales.plugin.api.UiNotifier;
import net.sevenscales.plugin.constants.ActionId;
import net.sevenscales.plugin.constants.RequestId;
import net.sevenscales.plugin.constants.RequestValue;
import net.sevenscales.plugin.constants.SdRegistryEvents;
import net.sevenscales.serverAPI.remote.IPageRemote;
import net.sevenscales.serverAPI.remote.LabelRemote;
import net.sevenscales.serverAPI.remote.PageRemote;
import net.sevenscales.serverAPI.remote.ProjectRemote;
import net.sevenscales.sketcho.client.app.view.SketchesView;
import net.sevenscales.sketcho.client.app.view.SketchesView.ICommandCallback;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SketchesController extends ControllerBase<Context> implements IRegistryEventObserver {
  private SketchesView view;
//  private List<IPage> sketches;
  private ActivationObserver activationObserver;
  private Long projectId;
  private Long labelId;

  private static List<String> namedItems = new ArrayList<String>();
  static {
//    namedItems.add("Link");
    namedItems.add(Constants.SKETCH_STATUS);
  }

  
  private SketchesController(Context context) {
    super(context);
    view = new SketchesView(this, callback);

    getContext().getEventRegistry().register(SdRegistryEvents.EVENT_CONTENT_UPDATE, this);
    getContext().getEventRegistry().register(SdRegistryEvents.LABEL_CHANGED, this);
  }

  public static ClassInfo info() {
    return new ClassInfo() {
      public Object createInstance(Object data) {
        return new SketchesController((Context) data);
      }

      public Object getId() {
        return SketchesController.class;
      }
    };
  }
  
  private ICommandCallback callback = new ICommandCallback() {
    public void search(String filterOption, String sort) {
      if (labelId != null) {
        // started with label search
        searchByLabel(filterOption, sort);
      } else if (sort.equals(SketchesSearch.TEXT_SORT_BY_MODIFIED)) {
        PageRemote.Util.inst.findAllWithNamedContentValues(projectId, filterOption, Constants.PAGE_TYPE_SKETCH, 
            namedItems, null, "", "modifiedTime desc", 
            new AsyncCallback< List<IPageWithNamedContentValues> >() {
        public void onSuccess(List<IPageWithNamedContentValues> result) {
          activationObserver.activated(SketchesController.this, null);
          view.setSketches(result);
        }
        public void onFailure(Throwable caught) {
          UiNotifier.instance().showError("Sketches failed");
        }
        });
      } else if (sort.equals(SketchesSearch.TEXT_SORT_BY_LABELS)) {
        sortByLabels();
      }
    }
    
    private void searchByLabel(String filter, String sort) {
      LabelRemote.Util.inst.findAllPages(labelId, namedItems, filter, sort, new AsyncCallback<List<IPageWithNamedContentValues>>() {
        public void onSuccess(List<IPageWithNamedContentValues> result) {
          activationObserver.activated(SketchesController.this, null);
          view.setSketches(result);
        }
        public void onFailure(Throwable caught) {
          UiNotifier.instance().showError("Ups, sketchs cannot be loaded");
        }
      });
    }
    
    public void sortByLabels() {
      PageRemote.Util.inst.findAll(getContext().getProjectId(), view.getFilterOption(), SketchesSearch.TEXT_SORT_BY_LABELS, namedItems, 
          new AsyncCallback<List<IPageWithNamedContentValues>>() {
        public void onSuccess(List<IPageWithNamedContentValues> result) {
          view.clear();
          activationObserver.activated(SketchesController.this, null);
    
          for (IPageWithNamedContentValues r : result) {
            view.addSketch(r.getPage(), r.getNamedContentValues());
          }
        }
        
        public void onFailure(Throwable caught) {
        }
      });
    }
    
    public <D> void onLabelDrop(D domainObject, Object label) {
      ILabel l = (ILabel) label;
      LabelRemote.Util.inst.addPageToLabel(l, (IPage) domainObject, new AsyncCallback<ILabel>() {
        public void onSuccess(ILabel result) {
          callback.search(view.getFilterOption(), view.getSortOption());
        }
        public void onFailure(Throwable caught) {
          UiNotifier.instance().showError("Label apply failed");
        }
      });
    }
    
    public void onClick(ILabel label, IPage page) {
      LabelRemote.Util.inst.removeFromPage(label, page.getId(), new AsyncCallback<Void>() {
        public void onSuccess(Void result) {
          callback.search(view.getFilterOption(), view.getSortOption());
        }
        
        public void onFailure(Throwable caught) {
          UiNotifier.instance().showError("Label delete failed");
        }
      });
    }
  };

  public void activate(Map requests, ActivationObserver observer) {
    this.activationObserver = observer;
    final SketchesController self = this;
    this.projectId = Long.valueOf((String) requests.get(RequestId.PROJECT_ID));
    this.labelId = RequestUtils.parseLong(RequestId.LABEL_ID, requests);

//    view.refresh(projectId);
    
    ProjectRemote.Util.inst.open(projectId, new AsyncCallback<IProject>() {
      public void onSuccess(IProject result) {
        view.setProject(result);
        view.clear();
        activationObserver.activated(self, null);
        
//        // async
//        PageRemote.Util.inst.findAll(result.getId(), Constants.PAGE_TYPE_SKETCH, new AsyncCallback< List<IPage> >() {
//          public void onSuccess(List<IPage> result) {
//            List<String> namedItems = new ArrayList<String>();
//            namedItems.add("Title");
//            namedItems.add("Link");
//            namedItems.add("State");
//            namedItems.add("Description");
//            Map<String, String> emptyNamedItems = new HashMap<String, String>();
//            
//            for (final IPage sketch : result) {
//              view.addSketch(sketch, emptyNamedItems);
//              PageRemote.Util.inst.loadNamedContentValues(sketch.getId(), namedItems, new AsyncCallback< Map<String, String>> () {
//                public void onSuccess(Map<String, String> result) {
//                  view.fillSketch(sketch, result);
//                }
//                public void onFailure(Throwable t) {
//                  System.out.println("FAILURE loadNamedContentValues:"+t);
//                }
//              });
//            }
//          }
//          public void onFailure(Throwable caught) {
//            System.out.println("FAILURE: Sketches failed");
//          }
//        });
        
        // synchronous
//        namedItems.add("Description");
        callback.search(view.getFilterOption(), view.getSortOption());
        // reset search by label if any
//        labelId = null;
//        PageRemote.Util.inst.findAllWithNamedContentValues(result.getId(), SketchesSearch.TEXT_ALL_SKETCHES, Constants.PAGE_TYPE_SKETCH, 
//                namedItems, null, "", "modifiedTime desc", 
//                new AsyncCallback< List<IPageWithNamedContentValues> >() {
//          public void onSuccess(List<IPageWithNamedContentValues> result) {
//            view.clear();
//            activationObserver.activated(self, null);
//
//            for (IPageWithNamedContentValues r : result) {
//              view.addSketch(r.getPage(), r.getNamedContentValues());
//            }
//          }
//          public void onFailure(Throwable caught) {
//            System.out.println("Sketches failed");
//          }
//        });
      }
      public void onFailure(Throwable caught) {
        // TODO Auto-generated method stub
        
      }
    });
  }

  public void activate(DynamicParams params) {
//    view.setPageTitle(page.getName());
//    for (Object o : page.getContentItems()) {
//      IPageOrderedContent c = (IPageOrderedContent) o;
//      UiReadContent uiContent = getUiContent(c).readContent;
//      view.addContent(uiContent);
//      System.out.println(c + " " + c.getPage().findContent(c.getId()) + " "
//          + uiContent.getContent());
//    }
  }

  public Handler createHandlerById(Action action) {
    switch (action.getId()) {
      case ActionId.NEW_TICKET: {
        return new HandlerBase() {
          public void execute() {
            Map<Object, String> requests = new HashMap<Object, String>();
            requests.put(RequestId.CONTROLLER, RequestValue.SKETCH_CONTROLLER);
            requests.put(RequestId.PROJECT_ID, String.valueOf(projectId));
//            requests.put(RequestId.PAGE_ID, String.valueOf(result.getId()));
            requests.put(RequestId.ACTION_ID, String.valueOf(ActionId.NEW_PAGE));
//            Map<Integer, Integer> dynamicParams = new HashMap<Integer, Integer>();
            RequestUtils.activate(requests);

            // create new page with name page id
            // fill page with template fields
            // - title
            // - link
            // - state
            // - description
            // - model
            // let's do this in server
            // PageRemote.createPage(SketchTemplate.class);
            // SketchTemplate has name generation logic 
            // - sketchTemplate.generateName(Page) in here uses sketch+page id
            // - this class could read from database template fields
            // but for now let's just return predefined fields
          }
        };
      }
    }
    return null;
  }

  public View getView() {
    return view;
  }

  // @Override
  public void handleEvent(Integer eventId, Object data) {
    if (eventId.equals(SdRegistryEvents.LABEL_CHANGED)) {
      String historyToken = History.getToken();
      Location location = new Location(historyToken);
      Map<String,String> requests = location.getRequests();
      
      String controllerId = requests.get(RequestId.CONTROLLER);
      if (controllerId.equals(RequestValue.SKETCHES_CONTROLLER)) { 
        // this view currently focused?
        callback.search(view.getFilterOption(), view.getSortOption());
      }
    }
  }

}
