package net.sevenscales.sketcho.client.app.controller;

import java.util.HashMap;
import java.util.Map;

import net.sevenscales.appFrame.api.IRegistryEventObserver;
import net.sevenscales.appFrame.api.utils.IncrementalAsyncProcessor;
import net.sevenscales.appFrame.api.utils.IncrementalAsyncProcessor.IIncrementalExecutionObserver;
import net.sevenscales.appFrame.impl.Action;
import net.sevenscales.appFrame.impl.ActivationObserver;
import net.sevenscales.appFrame.impl.ClassInfo;
import net.sevenscales.appFrame.impl.ControllerBase;
import net.sevenscales.appFrame.impl.DynamicParams;
import net.sevenscales.appFrame.impl.Handler;
import net.sevenscales.appFrame.impl.HandlerBase;
import net.sevenscales.appFrame.impl.RequestUtils;
import net.sevenscales.appFrame.impl.View;
import net.sevenscales.domain.api.IContent;
import net.sevenscales.domain.api.IDiagramContent;
import net.sevenscales.domain.api.IPage;
import net.sevenscales.domain.api.IPageOrderedContent;
import net.sevenscales.domain.api.IProject;
import net.sevenscales.domain.dto.ContentUpdateEventDTO;
import net.sevenscales.domain.dto.DiagramContentDTO;
import net.sevenscales.domain.dto.PageOrderedContentDTO;
import net.sevenscales.domain.dto.TextContentDTO;
import net.sevenscales.domain.dto.TextLineContentDTO;
import net.sevenscales.editor.api.SurfaceHandler;
import net.sevenscales.editor.content.ContentEditListener;
import net.sevenscales.editor.content.ContentEditorFactory;
import net.sevenscales.editor.content.ContentSaveListener;
import net.sevenscales.editor.content.OrderedContentUtil;
import net.sevenscales.editor.content.UiEditContent;
import net.sevenscales.editor.content.UiReadContent;
import net.sevenscales.editor.content.UiDiagramOrderedEditContent.IAddLinkListener;
import net.sevenscales.editor.gfx.svg.converter.SvgConverter;
import net.sevenscales.plugin.api.Context;
import net.sevenscales.plugin.api.IContentShareContributor;
import net.sevenscales.plugin.api.IPermissionContributor;
import net.sevenscales.plugin.api.UiNotifier;
import net.sevenscales.plugin.constants.ActionId;
import net.sevenscales.plugin.constants.RequestId;
import net.sevenscales.plugin.constants.RequestValue;
import net.sevenscales.plugin.constants.SdRegistryEvents;
import net.sevenscales.serverAPI.remote.ContentRemote;
import net.sevenscales.serverAPI.remote.PageRemote;
import net.sevenscales.sketcho.client.app.controller.impl.page.CopyContentHandler;
import net.sevenscales.sketcho.client.app.utils.AbstractAsyncCallback;
import net.sevenscales.sketcho.client.app.utils.ImageUtil;
import net.sevenscales.sketcho.client.app.view.PageView;
import net.sevenscales.sketcho.client.app.view.PageView.ICommandCallback;
import net.sevenscales.sketcho.client.app.view.constants.ParamId;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

public class PageController extends ControllerBase<Context> implements
    ContentEditListener, ContentSaveListener, IRegistryEventObserver,
    OrderedContentUtil.OrderChangeListener {
  private PageView view;
  private IProject project;
  protected IPage page;
  private ActivationObserver activationObserver;
  private Long projectId;
  private Long pageId;
//  private Long makeAsRead = new Long(0);
  private Long contentModifiedTime = new Long(0);
  
  private IAddLinkListener addLinkListener = new IAddLinkListener() {
    
  };
  
  private static class ReadEditContent {
    public UiReadContent readContent;
    public UiEditContent editContent;
  }
  private Map<Long, ReadEditContent> readEditContents = new HashMap<Long, ReadEditContent>();
  private Map currentActivationRequests;
  private Handler currentHandler;
  protected ICommandCallback commandCallback;

  protected PageController(Context context) {
    super(context);
    this.commandCallback = new ICommandCallback() {
      public void quitEditMode() {
        getContext().setEditMode(false);
        view.setEditMode(false);
      }
      
      public void editMode() {
        getContext().setEditMode(true);
        view.setEditMode(true);
      }
      
      public void paste() {
        IContent content = getContext().getClipboard().pop();
        if (content != null) {
          addPageContent(content);
        }
      }

      public void addText() {
        addPageContent(new TextContentDTO());
      }

      public void addModel() {
        addPageContent(new DiagramContentDTO());
      }

      public void modifyProperties() {
        Map<String, Object> requests = new HashMap<String, Object>();
        requests.put(RequestId.PROJECT_ID, String.valueOf(projectId));
        requests.put(RequestId.PAGE, page);
        requests.put(RequestId.CONTROLLER, RequestValue.NEW_PAGE_CONTROLLER);
        requests.put(RequestId.ACTION_ID, String.valueOf(ActionId.EDIT_PROPERTIES));
        RequestUtils.activateDynamic(requests);
      }
    };
    
    view = (PageView) getView();
    view.addPageTitleSaveListener(new ContentSaveListener() {
      public void cancel(IContent content) {
        view.makePageTitleAsRead();
      }
      public void close(IContent content) {
        save(content);
        view.makePageTitleAsRead();
      }
      public void delete(IContent content) {
        // this needs to be disabled
      }
      public void save(IContent content) {
        // update page title
        TextLineContentDTO tlc = (TextLineContentDTO) content;
        page.setName(tlc.getText());
        PageRemote.Util.inst.update(page, new AsyncCallback<IPage>() {
          public void onSuccess(IPage result) {
            page = result;
            view.setPageTitle(page.getName());
          }
          public void onFailure(Throwable caught) {
            System.out.println("FAILURE: page title update: "+caught);
          }
        });
      }
      public void share(IContent content) {
      }
      
      public void generateImage(IDiagramContent content, SurfaceHandler surfaceHandler) {
        // TODO Auto-generated method stub
        
      }
    });
    getContext().getEventRegistry().register(
        SdRegistryEvents.EVENT_CONTENT_UPDATE, this);
    getContext().getEventRegistry().register(
        SdRegistryEvents.CLIPBOARD_UPDATE, new IRegistryEventObserver() {
          public void handleEvent(Integer eventId, Object data) {
            view.enablePaste(getContext().getClipboard().size() > 0);
          }
        });
  }

  public static ClassInfo info() {
    return new ClassInfo() {
      public Object createInstance(Object data) {
        return new PageController((Context) data);
      }

      public Object getId() {
        return PageController.class;
      }
    };
  }

  public void activate(Map requests, ActivationObserver observer) {
    this.currentActivationRequests = requests;
    this.activationObserver = observer;
    final PageController self = this;
    this.projectId = Long.valueOf((String) requests.get(RequestId.PROJECT_ID));

    project = getContext().getProject();

    view.refresh(projectId);

//    ProjectRemote.Util.inst.open(this.projectId, new AsyncCallback<IProject>() {
//      public void onSuccess(IProject result) {
//        project = result;
        pageId = Long.valueOf((String) currentActivationRequests.get(RequestId.PAGE_ID));

        PageRemote.Util.inst.open(pageId, new AsyncCallback<IPage>() {
          public void onSuccess(IPage result) {
            page = result;
            activatePage();
          }

          public void onFailure(Throwable caught) {
            UiNotifier.instance().showError("FAILURE: Project open failed:"+caught);
            System.out.println("FAILURE: Project open failed:"+caught);
          }
        });
        
//      }
//      public void onFailure(Throwable caught) {
//        System.out.println("project open failed during page opening");
//      }
//    });
  }

  public void activate(DynamicParams params) {
//    IProperty p = (IProperty) page.getProperties().get(PagePropertyDTO.NAME_VISIBLE);
//    boolean nameVisible = true;
//    if (p != null) {
//      nameVisible = Boolean.parseBoolean(p.getValue());
//    }
    
//    if (nameVisible) {
    view.setPageTitle(page.getName());
    
//    }
    for (Object o : page.getContentItems()) {
      IPageOrderedContent c = (IPageOrderedContent) o;
      UiReadContent uiContent = createUiReadContent(c);
//      UiReadContent uiContent = getUiContent(c).readContent;
      view.addContent(uiContent);
    }
  }

  private void activatePage() {
    DynamicParams params = new DynamicParams();
    params.addParam(ParamId.PAGE_PARAM, page);
    params.addParam(ParamId.PROJECT_PARAM, project);
    activationObserver.activated(this, params);
    activate(params);
  }
  
//  private ReadEditContent getUiContent(IPageOrderedContent c) {
//    ReadEditContent rec = readEditContents.get(c.getContent().getId());
//    if (rec == null) {
//      // create new mapping
//      rec = new ReadEditContent();
//      rec.readContent = ContentEditorFactory.createReadContent(c);
//      rec.editContent = ContentEditorFactory.createEditContent(c, this);
////      rec.editContent.init();
//      rec.readContent.addEditClickListener(this);
//      rec.editContent.addSaveListener(this);
//      readEditContents.put(c.getContent().getId(), rec);
//    }
//    
//    rec.readContent.setContent(c.getContent());
//    rec.editContent.setContent(c.getContent());
//    
//    rec.readContent.internalize();
//    rec.editContent.internalize();
//
//    return rec;
//  }

  // HACK! currently cannot utilize same instances again => possible memory leaks!!
  // and need to recreate ui contents again and again to function properly especially
  // with key board events
  private UiReadContent createUiReadContent(IPageOrderedContent c) {
    UiReadContent result = ContentEditorFactory.createReadContent(c, getContext().isEditMode()); 
    result.addEditClickListener(this);
    result.internalize();
    return result;
  }
  
  private UiEditContent createUiEditContent(IPageOrderedContent c, Context context) {
    UiEditContent result = ContentEditorFactory.createEditContent(c, context, this, addLinkListener);
    result.addSaveListener(this);
    result.internalize();
    return result;
  }

  public Handler createHandlerById(Action action) {
    switch (action.getId()) {
      case ActionId.COPY_CONTENT: {
        currentHandler = new CopyContentHandler(project, this);
        return currentHandler;
      }
    }
    return null;
  }

  public void addPageContent(final IContent newContent) {
    // save page title if on edit state
    // save all on edit state content editors
    IncrementalAsyncProcessor<IPageOrderedContent> processor = new IncrementalAsyncProcessor<IPageOrderedContent>();
    
    for (final Widget w : view.getContentPanel()) {
      if (w instanceof UiEditContent) {
        processor.addCommand(processor.new IncrementalAsyncCommand<IPageOrderedContent>() {
          @Override
          public void onStepSuccess(IPageOrderedContent result) {
          }
          @Override
          public void onStepFailure(Throwable caught) {
            UiNotifier.instance().showError("Ups, cannot save now. Check network connection: "+caught);
            System.out.println("FAILURE: IContent save and close failed: "+caught);
          }
          @Override
          public boolean execute() {
            UiEditContent e = (UiEditContent) w;
            e.externalize();
            IPageOrderedContent oc = replaceOrderedContent(e.getContent());
            PageRemote.Util.inst.updateContent(oc, this);
            return false;
          }
        });
      }
    }
    // after that add new content through server
    processor.addExecutionObserver(new IIncrementalExecutionObserver() {
      public void finished() {
        GWT.log("addPageContent: " + newContent, null);
        IPageOrderedContent oc = new PageOrderedContentDTO();
        oc.setContent(newContent);
        oc.setPage(page);
        PageRemote.Util.inst.addContent(oc, new AsyncCallback<IPageOrderedContent>() {
          public void onSuccess(IPageOrderedContent result) {
            page = result.getPage();
            // need to refresh whole page, because otherwise content instance
            // are not in
            // sync with db instances
            view.refresh(projectId);
            activate(null);
            Widget w = makeContentAsEdit(result);
            
            // move scroll focus to newly added item
            int x = DOM.getAbsoluteLeft(w.getElement());
            int y = DOM.getAbsoluteTop(w.getElement());
            Window.scrollTo(x, y);
          }

          public void onFailure(Throwable caught) {
            UiNotifier.instance().showError("Ups, cannot add content: "+ caught);
            System.out.println("addPageContent failure");
          }
        });
      }
    });
    
    processor.execute();
  }

  public View getView() {
    if (view == null) {
      view = new PageView(this, commandCallback); 
    }
    return view;
  }

  public void edit(IContent content) {
    IPermissionContributor permissionContributor = getContext().getContributor().cast(
        IPermissionContributor.class);
    if (permissionContributor.hasEditPermission()) {
      UiEditContent uiContent = createUiEditContent(findOrderedContent(content), getContext());
//      UiEditContent uiContent = getUiContent(findOrderedContent(content)).editContent;
      view.replace(content.getId(), uiContent);
      
      // move scroll focus to edit item (actually too confusing. There should be
      // some logic that when element doesnt fit, then focus would change...
//      int x = DOM.getAbsoluteLeft(uiContent.getElement());
//      int y = DOM.getAbsoluteTop(uiContent.getElement());
//      Window.scrollTo(x, y);
    } else if (getContext().getUserId() != null) {
      // no edit permission
      UiNotifier.instance().showError("You don't have edit rights");
    } else {
      // not logged in
      Map<Object, String> requests = new HashMap<Object, String>();
      requests.put(RequestId.CONTROLLER, RequestValue.LOGIN_CONTROLLER);
      RequestUtils.activate(requests);
    }
  }

  public void save(IContent content) {
    // just save content to db
    IPageOrderedContent oc = replaceOrderedContent(content);
    PageRemote.Util.inst.updateContent(oc,
        new AsyncCallback<IPageOrderedContent>() {
          public void onSuccess(IPageOrderedContent result) {
//            System.out.println("IContent saved: " + result);
//            page = result.getPage();
//            
//            makeContentAsEdit(result);
//
//            // TODO: this can removed on new content update event implementation
//            IContentShareContributor shareContributor = getContext().getContributor().cast(
//                IContentShareContributor.class);
//            shareContributor.save(result.getContent());
          }

          public void onFailure(Throwable caught) {
            UiNotifier.instance().showError("Ups, cannot save now. Check network connection: "+caught);
            System.out.println("FAILURE: IContent save and close failed: "+caught);
          }
        });
  }

  // @Override
  public void close(IContent content) {
    IPageOrderedContent oc = replaceOrderedContent(content);
    // save content and close
    PageRemote.Util.inst.updateContent(oc,
      new AsyncCallback<IPageOrderedContent>() {
        public void onSuccess(IPageOrderedContent result) {
          /*makeAsRead = */result.getContent().getId();
          
          // with this page will be loaded twice, but this is much 
          // responsive for user in this way
          page = result.getPage();
          makeContentAsRead(result);
          
          // to avoid second loading time store modified time
          // and compare that to event time stamp
          contentModifiedTime = result.getContent().getModifiedTime();
        }

        public void onFailure(Throwable caught) {
          UiNotifier.instance().showError("Ups, cannot save now. Check network connection: "+caught);
          System.out.println("IContent save and close failed: " + caught);
        }
      });
  }

  public void cancel(IContent content) {
    makeContentAsRead(findOrderedContent(content));
  }

  public void delete(IContent content) {
    PageRemote.Util.inst.deleteContent(findOrderedContent(content),
        new AbstractAsyncCallback<IPage>(content) {
          public void onSuccess(IPage result) {
            page = result;
            view.remove((IContent) data);
            
//            System.out.println("Delete content success");
          }

          public void onFailure(Throwable caught) {
            UiNotifier.instance().showError("You don't have access rights to delete content");
          }
        });
  }

  // @Override
  public void share(IContent content) {
    IContentShareContributor shareContributor = getContext().getContributor().cast(
        IContentShareContributor.class);
    shareContributor.share(content);
  }
  
  public void generateImage(IDiagramContent content, SurfaceHandler surfaceHandler) {
    SvgConverter sc = new SvgConverter();
    String svg = sc.convertToSvg(content, surfaceHandler);
    
    //    RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(url));
//    builder.setHeader("content-svg", svg);
//    
//    try {
//      builder.sendRequest(null, new RequestCallback() {
//        public void onResponseReceived(Request request, Response response) {
//          GWT.log("Download success", null);
//        }
//        public void onError(Request request, Throwable exception) {
//          GWT.log("Download failed: " + exception, null);
//        }
//      });
//    } catch (RequestException e) {
//      GWT.log("Request Failed: " + e, null);
//    }
    final IContent remember = content;
    ContentRemote.Util.inst.downloadImage(svg, content.getId(), new AsyncCallback<String>() {
      public void onSuccess(String result) {
        GWT.log("Download success:"+result, null);
        String url = GWT.getHostPageBaseURL()+"resources/contentImage/"+result;
        ImageUtil.show(url, remember.getWidth(), remember.getHeight());
//        Window.Location.open(url, "_blank", "");
      }
      
      public void onFailure(Throwable caught) {
        GWT.log("Download failed: " + caught, null);
      }
    });
    GWT.log(svg, null);
  }

  // @Override
  public void orderChanged(IPageOrderedContent orderedContent, int order) {
//    OrderUtils.order(orderedContent, prevOrder);
    PageRemote.Util.inst.moveContent(orderedContent, order, new AsyncCallback<IPageOrderedContent>() {
      public void onSuccess(IPageOrderedContent result) {
        page = result.getPage();
        readEditContents.clear();
//        System.out.println("Order change success");
        view.refresh(projectId);
        activate(null);
      }

      public void onFailure(Throwable caught) {
        UiNotifier.instance().showError("Ups, cannot save now. Check network connection: " + caught);
        System.out.println("FAILURE: Order change failed:" + caught);
      }
    });
  }

  protected void makeContentAsRead(IPageOrderedContent oc) {
    UiReadContent uiContent = createUiReadContent(oc);
//    UiReadContent uiContent = getUiContent(oc).readContent;
    view.replace(oc.getId(), uiContent);
  }

  protected Widget makeContentAsEdit(IPageOrderedContent content) {
    UiEditContent uiContent = createUiEditContent(content, getContext());
//    UiEditContent uiContent = getUiContent(content).editContent;
    view.replace(content.getId(), uiContent);
    return uiContent;
  }

  // @Override
  public void handleEvent(Integer event) {

  }

  // @Override
  public void handleEvent(Integer eventId, Object data) {
    if (eventId.equals(SdRegistryEvents.EVENT_CONTENT_UPDATE)) {
      ContentUpdateEventDTO update = (ContentUpdateEventDTO) data;
      
      if (update.content.getModifiedTime() > contentModifiedTime && 
          view.findUiContent(update.content.getId()) != null) {
        if (/*!makeAsRead.equals(update.content.getId()) &&*/ 
            view.findUiContent(update.content.getId()) instanceof UiEditContent) {
          makeContentAsEdit(replaceOrderedContent(update.content));
        } else {
          makeContentAsRead(replaceOrderedContent(update.content));
        }
        
        // reset cached value
/*        makeAsRead = new Long(0); */
      }
    }
  }

  private IPageOrderedContent findOrderedContent(IContent content) {
    IPageOrderedContent result = null;
    for (Object o : page.getContentItems()) {
      result = (IPageOrderedContent) o;
      if (result.getContent().equals(content)) {
        break;
      }
    }
    return result;
  }

  /**
   * For updating content inside ordered item.
   * 
   * @param content
   * @return
   */
  private IPageOrderedContent replaceOrderedContent(IContent content) {
    IPageOrderedContent oc = findOrderedContent(content);
    oc.setContent(content);
    return oc;
  }

}
