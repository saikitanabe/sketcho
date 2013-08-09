package net.sevenscales.sketcho.client.app.view;

import java.util.HashMap;
import java.util.Map;

import net.sevenscales.appFrame.api.IContributor;
import net.sevenscales.appFrame.impl.Action;
import net.sevenscales.appFrame.impl.ActionFactory;
import net.sevenscales.appFrame.impl.DynamicParams;
import net.sevenscales.appFrame.impl.IController;
import net.sevenscales.appFrame.impl.ITilesEngine;
import net.sevenscales.appFrame.impl.RequestUtils;
import net.sevenscales.appFrame.impl.View;
import net.sevenscales.domain.api.IContent;
import net.sevenscales.domain.api.IPage;
import net.sevenscales.domain.api.IProject;
import net.sevenscales.domain.api.ITemplate;
import net.sevenscales.domain.dto.ContentPropertyDTO;
import net.sevenscales.domain.dto.TextLineContentDTO;
import net.sevenscales.editor.content.ContentEditListener;
import net.sevenscales.editor.content.ContentSaveListener;
import net.sevenscales.editor.content.UiContent;
import net.sevenscales.editor.content.UiEditTextLineContent;
import net.sevenscales.editor.content.UiReadContent;
import net.sevenscales.editor.content.UiReadTextLineContent;
import net.sevenscales.plugin.api.Context;
import net.sevenscales.plugin.api.IPermissionContributor;
import net.sevenscales.plugin.api.UiNotifier;
import net.sevenscales.plugin.constants.ActionId;
import net.sevenscales.plugin.constants.RequestId;
import net.sevenscales.plugin.constants.RequestValue;
import net.sevenscales.plugin.constants.Styles;
import net.sevenscales.plugin.constants.TileId;
import net.sevenscales.sketcho.client.app.view.constants.ParamId;
import net.sevenscales.sketcho.client.uicomponents.StyleButton;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class PageView extends View<Context> /*implements ChangeListener*/ {
	private VerticalPanel contentPanel;
	private IProject project;
	private IPage page;
	private VerticalPanel commandArea;
	private HorizontalPanel hierarchy;
	private HorizontalPanel buttons;
//	private LinkAction backLink;
  private VerticalPanel contentArea;
//  private HTML pageTitle;
  private HTML editButton;
  private StyleButton addModel;
  private StyleButton addText;
  private StyleButton properties;
  private Widget copyContent;
//  private HorizontalPanel spaces;
  private TextLineContentDTO pageTitleContent;
  private UiReadTextLineContent pageTitle;
  private UiEditTextLineContent pageTitleEdit;
  private ICommandCallback callback;
  
  final String editText = "Edit Mode";
  final String quitEditText = "Quit Edit Mode";
  private DeckPanel pageTitleDeck;
	private Button pasteButton;
  
  public interface ICommandCallback {
    public void editMode();
    public void quitEditMode();
    public void paste();
    public void addText();
    public void addModel();
    public void modifyProperties();
  }

	public PageView(IController<Context> controller, ICommandCallback callback) {
		super(controller);
		this.callback = callback;

		// command area
		commandArea = new VerticalPanel();
		commandArea.setStyleName(Styles.COMMAND_AREA);
		
		hierarchy =  new HorizontalPanel();
		commandArea.add(hierarchy);
		hierarchy.setStyleName(Styles.PAGE_LINK_HIERARCHY);

		buttons = new HorizontalPanel();
		buttons.addStyleName(Styles.COMMAND_AREA_BUTTONS);
		commandArea.add(buttons);

//		this.properties = ActionFactory
//      .createButtonAction("Properties", ActionId.EDIT_PROPERTIES, controller)
//      .getWidget();
		this.properties = new StyleButton(new Image("images/configuration.png"), "Properties");
		properties.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        PageView.this.callback.modifyProperties();
      }
    });
		
		this.editButton = new HTML();
		editButton.addStyleName("PageView-EditModeSelector");
		editButton.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        if (!PageView.this.controller.getContext().isEditMode()) {
          PageView.this.callback.editMode();
        } else {
          PageView.this.callback.quitEditMode();
        }
      }
    });

    this.addModel = new StyleButton(new Image("images/add_icon.png"), "Add Model");
    addModel.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        PageView.this.callback.addModel();
      }
    });

    this.addText = new StyleButton(new Image("images/add_icon.png"), "Add Text");
    addText.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        PageView.this.callback.addText();
      }
    });

    this.copyContent = ActionFactory
      .createButtonAction("Import Content", ActionId.COPY_CONTENT, controller)
      .getWidget();
    
    this.pasteButton = new Button("Paste");
    pasteButton.setEnabled(false);
    pasteButton.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        PageView.this.callback.paste();
      }
    });

    buttons.add(properties);
    buttons.add(addText);
    buttons.add(addModel);
    buttons.add(pasteButton);
    
    // TODO: should be removed, replaced with copy-paste :)
//    buttons.add(copyContent);
    buttons.add(editButton);

		// Content
		contentArea = new VerticalPanel();
		contentArea.setWidth("100%");
		
		contentArea.setStyleName("PageView-PageContent");

		contentPanel = new VerticalPanel();
		contentPanel.setWidth("100%");
		
    pageTitleContent = new TextLineContentDTO();
    pageTitleContent.setName("Title");
    ContentPropertyDTO titleProp = new ContentPropertyDTO();
    titleProp.setType(ITemplate.BOOLEAN);
    titleProp.setValue(Boolean.TRUE.toString());
    pageTitleContent.getProperties().put(ContentPropertyDTO.NAME_AS_TITLE, titleProp);
    
    ContentPropertyDTO delProp = new ContentPropertyDTO();
    delProp.setType(ITemplate.BOOLEAN);
    delProp.setValue(Boolean.FALSE.toString());
    pageTitleContent.getProperties().put(ContentPropertyDTO.DELETABLE, delProp);

    pageTitleContent.setText(" "); // doesn't appear on first reload if no text when initialized
    
    this.pageTitleDeck = new DeckPanel();
    pageTitleEdit = new UiEditTextLineContent(pageTitleContent, controller.getContext());
    pageTitleEdit.init();
    pageTitle = new UiReadTextLineContent(pageTitleContent);
    pageTitle.addEditClickListener(new ContentEditListener() {
      public void edit(IContent content) {
        // HACK: duplicate code. Also permissions needs to be refactored at some point
        IPermissionContributor permissionContributor = PageView.this.controller.getContext().getContributor().cast(
            IPermissionContributor.class);
        if (permissionContributor.hasEditPermission()) {
          makePageTitleAsEdit();
        } else if (PageView.this.controller.getContext().getUserId() != null) {
          // no edit permission
          UiNotifier.instance().showError("You don't have edit rights");
        } else {
          // not logged in
          Map<Object, String> requests = new HashMap<Object, String>();
          requests.put(RequestId.CONTROLLER, RequestValue.LOGIN_CONTROLLER);
          RequestUtils.activate(requests);
        }

      }
    });
    contentArea.add(pageTitleDeck);
    contentArea.setCellWidth(pageTitleDeck, "100%");
    pageTitle.internalize();
    pageTitleDeck.add(pageTitle);
    pageTitleDeck.add(pageTitleEdit);

//		pageTitle = new HTML();
//		pageTitle.addStyleName("generic-title");
    contentArea.add(contentPanel);
//    addContent(pageTitle);
	}

	public void addContent(UiContent content) {
		contentPanel.add(content);
		contentPanel.setCellWidth(content, "100%");
	}

	public void activate(ITilesEngine tilesEngine,
						 DynamicParams params, IContributor contributor) {
		project = (IProject) params.getParam(ParamId.PROJECT_PARAM);
		page = (IPage) params.getParam(ParamId.PAGE_PARAM);
		
		// HACK! because pageTitle is not currently used for sketches
		// and uses own text line content which has own margin
		pageTitle.setVisible(pageTitle.getText().length() > 0);
		
    IPermissionContributor permissionContributor = controller.getContext().getContributor().cast(
        IPermissionContributor.class);
    properties.setVisible(permissionContributor.hasEditPermission());
    // dashboard properties cannot be changed
    properties.setVisible(!page.equals(controller.getContext().getProject().getDashboard()));
    addText.setVisible(permissionContributor.hasEditPermission());
    addModel.setVisible(permissionContributor.hasEditPermission());
    copyContent.setVisible(permissionContributor.hasEditPermission());
    buttons.setVisible(permissionContributor.hasEditPermission());
    
    hierarchy.clear();
    Map<Object, Object> requests = new HashMap<Object, Object>();
//    requests.put(RequestId.CONTROLLER, RequestValue.PAGE_CONTROLLER);
//    requests.put(RequestId.PAGE_ID, page.getId());
//    requests.put(RequestId.PROJECT_ID, project.getId());
//    Action a1 = ActionFactory.createLinkAction(page.getName(), requests);
    HTML a1 = new HTML(page.getName());
    a1.setStyleName("page-Hierarchy");
    hierarchy.add(a1);
    IPage parent = page;
    while ( (parent = parent.getParent()) != null) {
      requests.clear();
      
      Action a = null;
      if (parent.getParent() != null) {
        requests.put(RequestId.CONTROLLER, RequestValue.PAGE_CONTROLLER);
        requests.put(RequestId.PAGE_ID, parent.getId());
        requests.put(RequestId.PROJECT_ID, project.getId());
        a = ActionFactory.createLinkAction(parent.getName()+" &laquo;", requests);
      } else {
        // Root is documentation page
        requests.put(RequestId.CONTROLLER, RequestValue.PROJECT_ARCHITECTURE_CONTROLLER);
        requests.put(RequestId.PROJECT_ID, controller.getContext().getProjectId());
        a = ActionFactory.createLinkAction("Design"+" &laquo;", requests);
      }
      a.setStyleName("page-Hierarchy");
      hierarchy.insert(a, 0);
    }

//		title.setHTML("<h1>" + project.getName() + "-" + page.getName() + "</h1>");
		// TODO: global context of project?? or how to show that now we are under some project
		// perhaps hierarchical history is showed of depth of links!!
		tilesEngine.setTile(TileId.COMMAND_AREA, commandArea);
//		tilesEngine.setTile(TileId.MENU, null);
		tilesEngine.setTile(TileId.CONTENT, contentArea);
		setEditMode(controller.getContext().isEditMode());
	}
	
	public void clear() {
		contentPanel.clear();
	}

	public void replace(Long id, UiContent replacableContent) {
		int index = find(replacableContent.getContent().getId());
		if (index >= 0) {
			contentPanel.remove(index);
			contentPanel.insert(replacableContent, index);
		}
	}
	
	public UiContent findUiContent(Long contentId) {
    for (int i = 0; i < contentPanel.getWidgetCount(); ++i) {
      UiContent uiContent = (UiContent) contentPanel.getWidget(i);
      if (uiContent.getContent().getId().equals(contentId)) {
        return uiContent;
      }
    }
    return null;
	}

	private int find(Long contentId) {		
		for (int i = 0; i < contentPanel.getWidgetCount(); ++i) {
			UiContent uiContent = (UiContent) contentPanel.getWidget(i);
			if (uiContent.getContent().getId().equals(contentId)) {				
				return i;
			}
		}
		return -1;
	}
	
	public void remove(IContent content) {
		int index = find(content.getId());
		if (index >= -1) {
			contentPanel.remove(index);
		}
	}

	public void refresh(Long projectId) {
		clear();
		Map backRequest = new HashMap();
		backRequest.put(RequestId.CONTROLLER, RequestValue.PROJECTS_CONTROLLER);
//		backLink.setRequest(backRequest);
	}

  public void setPageTitle(String name) {
//    pageTitle.setHTML(name);
    pageTitleContent.setText(name);
    makePageTitleAsRead();
  }
  
  protected HorizontalPanel getHierarchy() {
    return hierarchy;
  }

  public void addPageTitleSaveListener(ContentSaveListener contentSaveListener) {
    this.pageTitleEdit.addSaveListener(contentSaveListener);
  }

  public void makePageTitleAsRead() {
//    if (contentArea.getWidgetIndex(pageTitleEdit) > 0) {
//      contentArea.remove(pageTitleEdit);
      pageTitle.internalize();
      pageTitleDeck.showWidget(0);
//      contentArea.insert(pageTitle, 0);
//    }
  }
  
  public void makePageTitleAsEdit() {
//    contentArea.remove(pageTitle);
    pageTitleEdit.internalize();
    pageTitleDeck.showWidget(1);
//    contentArea.insert(pageTitleEdit, 0);
    DeferredCommand.addCommand(new Command() {
      public void execute() {
        pageTitleEdit.setFocus(true);
      }
    });
  }

  public void setEditMode(boolean editable) {
    String text = editable ? quitEditText : editText;
    editButton.setText(text);

    pageTitle.setEditable(editable);
    for (int i = 0; i < contentPanel.getWidgetCount(); ++i) {
      Widget w = contentPanel.getWidget(i);
      if (w instanceof UiReadContent) {
        UiReadContent r = (UiReadContent) w;
        r.setEditable(editable);
      }
    }
  }
  
  public VerticalPanel getContentPanel() {
    return contentPanel;
  }

  public void enablePaste(boolean enabled) {
    pasteButton.setEnabled(enabled);
    int size = controller.getContext().getClipboard().size();
    String count = "";
    
    if (size > 1) {
      count = " ("+size+")";
    }
    pasteButton.setText("Paste"+count);
  }

}
