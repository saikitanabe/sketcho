package net.sevenscales.sketcho.client.app.view;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import net.sevenscales.appFrame.api.IContributor;
import net.sevenscales.appFrame.impl.Action;
import net.sevenscales.appFrame.impl.ActionFactory;
import net.sevenscales.appFrame.impl.Debug;
import net.sevenscales.appFrame.impl.DynamicParams;
import net.sevenscales.appFrame.impl.IController;
import net.sevenscales.appFrame.impl.ITilesEngine;
import net.sevenscales.appFrame.impl.View;
import net.sevenscales.domain.api.IContent;
import net.sevenscales.domain.api.IPage;
import net.sevenscales.domain.api.IPageOrderedContent;
import net.sevenscales.domain.api.IProject;
import net.sevenscales.domain.utils.PagePullIterator;
import net.sevenscales.editor.content.UiContent;
import net.sevenscales.editor.content.UiContentFactory;
import net.sevenscales.plugin.api.Context;
import net.sevenscales.plugin.api.IPermissionContributor;
import net.sevenscales.plugin.constants.ActionId;
import net.sevenscales.plugin.constants.RequestId;
import net.sevenscales.plugin.constants.RequestValue;
import net.sevenscales.plugin.constants.TileId;
import net.sevenscales.serverAPI.remote.PageRemote;
import net.sevenscales.sketcho.client.app.view.constants.ParamId;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ArchitectureView extends View<Context> {
	private VerticalPanel commandArea;
	private VerticalPanel architecture = new VerticalPanel();
  private HorizontalPanel hierarchy;
  private HorizontalPanel buttons;
  private Widget newPage;
  private Action organize;

	public ArchitectureView(IController<Context> controller) {
		super(controller);
		
		architecture.setWidth("100%");
		architecture.addStyleName("ArchitectureView");
		
		// construct body
		
//		architecture.setStyleName("pageContent");
		
		// command area
		commandArea = new VerticalPanel();
		commandArea.setStyleName("command-Area");
		
    this.hierarchy = new HorizontalPanel();
    hierarchy.setStyleName("page-link-hierarchy");

//    Map<Object, Object> requests = new HashMap<Object, Object>();
//    requests.put(RequestId.CONTROLLER, RequestValue.PROJECT_ARCHITECTURE_CONTROLLER);
//    requests.put(RequestId.PROJECT_ID, controller.getContext().getProjectId());    
//    Action a = ActionFactory.createLinkAction("Documentation", requests);
    HTML a = new HTML("Design");
    a.setStyleName("page-Hierarchy");

    hierarchy.add(a);
    this.buttons = new HorizontalPanel();
    buttons.addStyleName("command-area-buttons");
    commandArea.add(hierarchy);
    commandArea.add(buttons);

    this.newPage = ActionFactory
      .createButtonAction("New Page", ActionId.NEW_PAGE, controller)
      .getWidget();

    this.organize = (Action) ActionFactory
      .createButtonAction("Organize", ActionId.ORGANIZE, controller)
      .getWidget();

    buttons.add(newPage);
    buttons.add(organize);

		// back link
//		Map requests = new HashMap();
//		requests.put(RequestId.CONTROLLER, RequestValue.PROJECTS_CONTROLLER);
//		commandArea.add(ActionFactory
//			.createLinkAction("&laquo; back to projects", requests)
//			.getWidget());		
	}
	
	public class PageListFormatter {
    private IProject project;
    private Stack<Integer> numberings = new Stack<Integer>();
    private int prevLevel = -1;
    private PagePullIterator ppi;
    
    public PageListFormatter(IProject project) {
      this.project = project;
      ppi = new PagePullIterator(project.getDashboard());
      openPage(ppi.next()); 
    }
    
    private void openPage(IPage page) {
      if (!page.equals(project.getDashboard())) {
        PageRemote.Util.inst.open(page.getId(), new AsyncCallback<IPage>() {
          public void onSuccess(IPage result) {
//            System.out.println(result.getName());
            Map<Object, Object> requests = new HashMap<Object, Object>();
            requests.put(RequestId.CONTROLLER, RequestValue.PAGE_CONTROLLER);
            requests.put(RequestId.PAGE_ID, result.getId());
            requests.put(RequestId.PROJECT_ID, project.getId());
            
  //          if (level > prevLevel) {
  //            numberings.push(new Integer(0));
  //          } else if (level < prevLevel) {
  //            numberings.pop();
  //          }
  //          Integer n = numberings.lastElement();
  //          numberings.set(numberings.size() - 1, ++n);
  //          String fn = formatNumber();
            HorizontalPanel title = new HorizontalPanel();
            title.setWidth("100%");
            Action a = ActionFactory.createLinkAction(result.getName(), requests);
            a.addStyleName("ArchitectureView-Title");
            Action edit = ActionFactory.createLinkAction("[edit]", requests);
            edit.addStyleName("ArchitectureView-EditLink");
            title.add(a);
            title.add(edit);
            title.setCellHorizontalAlignment(edit, HorizontalPanel.ALIGN_RIGHT);
  //          prevLevel = level;
            architecture.add(title);
            for (IPageOrderedContent c : (Set<IPageOrderedContent>) result.getContentItems()) {
              UiContent uc = UiContentFactory.create(c.getContent());
//              uc.addStyleName("architecture-content");
              architecture.add(uc);
            }
  
            IPage page = ppi.next();
            if (page != null) {
              openPage(page);
            }
          }
          public void onFailure(Throwable caught) {
            System.out.println("Architecture open failed");
            Debug.print("Architecture open failed");
          }
        });
      } else {
        // skip dashboard
        IPage p = ppi.next();
        if (p != null) {
          openPage(p);
        }
      }
    }

    private String formatNumber() {
      String result = new String();
      int i = numberings.size();
      while (--i > 0) {
        String val = String.valueOf(numberings.get(i));
        result = val + "."+ result;
      }
      return result;
    }
	}

	public void activate(ITilesEngine tilesEngine,
						 DynamicParams params, IContributor contributor) {
		IProject project = (IProject) params.getParam(ParamId.PROJECT_PARAM);
		
    IPermissionContributor permissionContributor = controller.getContext().getContributor().cast(
        IPermissionContributor.class);
    newPage.setVisible(permissionContributor.hasEditPermission());
    organize.setVisible(permissionContributor.hasEditPermission());

		tilesEngine.setTile(TileId.COMMAND_AREA, commandArea);
		
		architecture.clear();
		
		new PageListFormatter(project);		
    
		tilesEngine.setTile(TileId.CONTENT, architecture);
	}
	
	public void redraw(IContent content) {
	  UiContent uiContent = find(content);
	  if (uiContent != null) {
	    uiContent.setContent(content);
	    uiContent.internalize();
	    int index = architecture.getWidgetIndex(uiContent);
	    architecture.remove(index);
	    architecture.insert(uiContent, index);
	  }
	}
	
	public UiContent find(IContent content) {
	  UiContent result = null;
    Iterator i = architecture.iterator();
    while (i.hasNext()) {
      Widget n = (Widget) i.next();
      if (n instanceof UiContent) {
        result = (UiContent) n;
        if (result.getContent().equals(content)) {
          return result;
        }
      }
    }
    return result;	  
	}

}
