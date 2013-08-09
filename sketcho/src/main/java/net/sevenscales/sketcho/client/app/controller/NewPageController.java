package net.sevenscales.sketcho.client.app.controller;

import java.util.HashMap;
import java.util.Map;

import net.sevenscales.appFrame.impl.Action;
import net.sevenscales.appFrame.impl.ActivationObserver;
import net.sevenscales.appFrame.impl.ClassInfo;
import net.sevenscales.appFrame.impl.ControllerBase;
import net.sevenscales.appFrame.impl.DynamicParams;
import net.sevenscales.appFrame.impl.Handler;
import net.sevenscales.appFrame.impl.HandlerBase;
import net.sevenscales.appFrame.impl.RequestUtils;
import net.sevenscales.appFrame.impl.View;
import net.sevenscales.domain.api.IPage;
import net.sevenscales.domain.api.IProject;
import net.sevenscales.domain.constants.Constants;
import net.sevenscales.domain.dto.PageDTO;
import net.sevenscales.plugin.api.Context;
import net.sevenscales.plugin.constants.ActionId;
import net.sevenscales.plugin.constants.RequestId;
import net.sevenscales.plugin.constants.RequestValue;
import net.sevenscales.serverAPI.remote.PageRemote;
import net.sevenscales.sketcho.client.app.view.NewPageView;
import net.sevenscales.sketcho.client.app.view.NewPageView.PagePropertiesChangeListener;
import net.sevenscales.sketcho.client.app.view.constants.ParamId;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

public class NewPageController extends ControllerBase<Context> implements PagePropertiesChangeListener {
	private NewPageView view;
	private Long projectId;
  private ActivationObserver activationObserver;
  private Long pageId;
  private IPage currentPage;
//  private IProject project;
  private PagePropertiesPresentation currentPagePresentation;

	private NewPageController(Context context) {
	  super(context);
	}
	
	public static ClassInfo info() {
		return new ClassInfo() {
			public Object createInstance(Object data) {
				return new NewPageController((Context) data);
			}

			public Object getId() {
				return NewPageController.class;
			}
		};
	}
	
	static class PagePropertiesPresentation extends PageDTO {
    public void init(IPage page) {
      if (page != null) {
        setName(new String(page.getName()));
        if (page.getOrderValue() != null) {
          setOrderValue(new Integer(page.getOrderValue()));
        }
        setParent(page.getParent());
        setId(page.getId());
        setType(page.getType());
      }
    }
	}

	public void activate(Map requests, ActivationObserver observer) {
	  this.activationObserver = observer;
	  final NewPageController self = this;
		this.projectId = RequestUtils.parseLong(RequestId.PROJECT_ID, requests);
		this.pageId = RequestUtils.parseLong(RequestId.PAGE_ID, requests);
		this.currentPage = (IPage) requests.get(RequestId.PAGE);
		
    view = new NewPageView(this, requests, this);
    activatePageEditor();
		
//		ProjectRemote.Util.inst.open(projectId, new AsyncCallback<IProject>() {
//		  public void onSuccess(IProject result) {
//		    NewPageController.this.project = result;
//		    PageIterator pi = new PageIterator(result.getDashboard(), new PageIterator.IteratorCallback() {
//		      public void iteration(IPage page, int level) {
//		        if (page.getId().equals(pageId)) {
//		          currentPage = page;
//		        }
//		      }
//		    });
//		    pi.iterate();
//		    
//		    activatePageEditor();
//		  }
//		  public void onFailure(Throwable caught) {
//		    System.out.println("new page failed");
//		  }
//		});
	}
	
	public void activate(DynamicParams params) {
//		project = (Project) params.getParam(ParamId.PROJECT_PARAM);
	}
	
	private void activatePageEditor() {
    currentPagePresentation = new PagePropertiesPresentation();
    currentPagePresentation.init(currentPage);

    DynamicParams p = new DynamicParams();
    p.addParam(ParamId.PROJECT_PARAM, getContext().getProject());
    p.addParam(ParamId.SUBPAGE_PARAM, currentPagePresentation);
    activationObserver.activated(this, p);
	}

	public View getView() {
    return view;
	}

	public Handler createHandlerById(Action action) {
		switch (action.getId()) {
		case ActionId.CREATE_PAGE: {
			return new HandlerBase() {
				public void execute() {
					currentPage = new PageDTO();
					currentPage.setName(view.getPageName());
					PageRemote.Util.inst.addPage(currentPage, view.getParentPage(), new AsyncCallback<IPage>() {
					  public void onSuccess(IPage result) {
					    // back
					    Map requests = new HashMap();
					    requests.put(RequestId.CONTROLLER, RequestValue.PROJECT_CONTROLLER);
					    requests.put(RequestId.PROJECT_ID, getContext().getProjectId());
					    RequestUtils.activate(requests);
					  }
					  public void onFailure(Throwable caught) {
					    System.out.println("FAILURE: page create failed: "+caught);
					  }
					});
				}
			};
		}
		case ActionId.SUBMIT: {
		  return new HandlerBase() {
		    public void execute() {
		      currentPage.setName(view.getPageName());
//		      currentPage.setParent(view.getParentPage());
		      currentPage.setOrderValue(view.getOrderValue());
		      PageRemote.Util.inst.moveAndUpdate
		          (currentPage, currentPagePresentation.getParent().getId(), new AsyncCallback<IPage>() {
		        public void onSuccess(IPage result) {
//              System.out.println("subpage saved");
		          currentPage = result;
              Map requests = new HashMap();
              requests.put(RequestId.CONTROLLER, RequestValue.PROJECT_CONTROLLER);
              requests.put(RequestId.PROJECT_ID, projectId);
              RequestUtils.activate(requests);
		        }
		        public void onFailure(Throwable caught) {
		          System.out.println("subpage save failed");
		        }
		      });
		    }
		  };
		}
    case ActionId.DELETE_PAGE: {
      return new HandlerBase() {
        private void showDialog() {
          final DialogBox d = new DialogBox();
          String text = "Delete "+currentPage.getName()+"?";
          d.setText(text);
          HorizontalPanel buttons = new HorizontalPanel();
          Button ok = new Button("Ok");
          ok.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
              delete();
              d.hide();
            }
          });
          
          Button cancel = new Button("Cancel");
          cancel.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
              d.hide();
            }
          });
          
          buttons.add(ok);
          buttons.add(cancel);
          d.setWidget(buttons);
          d.center();
          d.show();
        }

        public void execute() {
          showDialog();
        }
        
        private void delete() {
          PageRemote.Util.inst.delete(currentPage, new AsyncCallback() {
            public void onSuccess(Object result) {
              Map requests = new HashMap();
              if (currentPage.getType() != null && currentPage.getType().equals(Constants.PAGE_TYPE_SKETCH)) {
                requests.put(RequestId.CONTROLLER, RequestValue.SKETCHES_CONTROLLER);
              } else {
                requests.put(RequestId.CONTROLLER, RequestValue.PROJECT_CONTROLLER);
              }
              requests.put(RequestId.PROJECT_ID, projectId);
              RequestUtils.activate(requests);
              currentPage = null;
            }
            public void onFailure(Throwable caught) {
              System.out.println("FAILURE: Page delete failed: "+caught);
            }
          });
        }
      };
    }
		}
		return null;
	}
	
//	@Override
	public void parentPageChanged() {
	  // refresh order values
	  if (currentPagePresentation != null) {
	    currentPagePresentation.setParent(view.getParentPage());
//	    currentPagePresentation.getParent().getSubpages().add(currentPage);
	    currentPagePresentation.setOrderValue(currentPagePresentation.getParent().getSubpages().size() + 1);
	    view.refreshOrderValues();
	  }
	}
}
