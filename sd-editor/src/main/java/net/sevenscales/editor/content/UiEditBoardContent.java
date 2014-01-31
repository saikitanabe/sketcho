package net.sevenscales.editor.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sevenscales.domain.api.IContent;
import net.sevenscales.domain.dto.ContentPropertyDTO;
import net.sevenscales.editor.api.EditorContext;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.event.CancelButtonClickedEvent;
import net.sevenscales.editor.api.event.CancelButtonClickedEventHandler;
import net.sevenscales.editor.api.event.SaveButtonClickedEvent;
import net.sevenscales.editor.api.event.SaveButtonClickedEventHandler;
import net.sevenscales.editor.content.ui.ConfluenceMenu;
import net.sevenscales.editor.content.ui.FreehandModeButton;
import net.sevenscales.editor.content.ui.IModeManager;
import net.sevenscales.editor.content.ui.ModeBarUi;
import net.sevenscales.editor.content.ui.SelectButtonBox;
import net.sevenscales.editor.content.ui.SimpleColorHandler;
import net.sevenscales.editor.content.ui.UndoMenu;
import net.sevenscales.editor.ui.UpDownController.ISizeCallback;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class UiEditBoardContent extends UiContent implements KeyDownHandler {
  protected boolean supportsEditMenu = false;
	private HorizontalPanel container;
//  private TextBox width;
//  private TextBox height;
	
//	private Button shareButton;

	protected List<ContentSaveListener> saveListeners;
	private Widget widget;
//  protected HorizontalPanel wp;
//  protected HorizontalPanel hp;
  private VerticalPanel background;
  private MenuBar menu;
//  private MenuItem deleteMenuItem;
//  private MenuItemSeparator deleteSeparator;
  private Map<Integer,UIObject> menuItems = new HashMap<Integer, UIObject>();
  public static final Integer SAVE_MENU_ITEM = 1;
  public static final Integer SAVE_AND_CLOSE_MENU_ITEM = 2;
  public static final Integer DELETE_MENU_ITEM = 3;
  public static final Integer PASTE_MENU_ITEM = 4;
  public static final Integer CLOSE_MENU_ITEM = 5;
  public static final Integer SAVE_SEPARATOR_MENU_ITEM = 6;
  public static final Integer DELETE_SEPARATOR_MENU_ITEM = 7;
//private MenuItem pasteMenuItem;	
	
	private static class SaveType {}	
	protected static final SaveType SAVE = new SaveType();
  protected static final SaveType SAVE_AND_CLOSE = new SaveType();
  
  private static final int DELTA = 100;
  protected VerticalPanel leftBar = new VerticalPanel();
  
  private ISizeCallback sizeCallback = new ISizeCallback() {
    @Override
    public void setSize() {
      UiEditBoardContent.this.setSize();
    }
  };
//  protected DecoratorPanel decorator;
	private ModeBarUi modeBar;
	private boolean supportsUndoMenu;
  
	public UiEditBoardContent(IContent content, Context context, boolean supportsEditMenu, boolean supportsUndoMenu, EditorContext editorContext) {
		super(content, context, editorContext);
		this.supportsEditMenu = supportsEditMenu;
		this.supportsUndoMenu = supportsUndoMenu;
//		this.decorator = new DecoratorPanel();
//		decorator.setStyleName("gwt-DecoratorPanel2");
		
		container = new HorizontalPanel();
		container.setStyleName("UiEditContent");
		container.setHeight("100%");

//		decorator.add(container);
		saveListeners = new ArrayList();
		
		getEditorContext().registerAndAddToRootPanel(createButtonsArea());
//		RootPanel.get().add(new TopButtons(getEditorContext()));
//		RootPanel.get().add(createButtonsArea());
		
//		this.wp = new HorizontalPanel();
//		wp.add(new Label("Width:"));
//    width = new TextBox();
//    width.setReadOnly(true);
//    width.getElement().getStyle().setWidth(45, Unit.PX);
//    width.addKeyDownHandler(this);
//    wp.add(width);
    
//    wp.add(new UpDownController(width, sizeCallback));
//    
//    this.hp = new HorizontalPanel();
//    hp.add(new Label("Height:"));
//    height = new TextBox();
//    height.setReadOnly(true);
//    height.getElement().getStyle().setWidth(45, Unit.PX);
//    height.addKeyDownHandler(this);
//    hp.add(height);
//    hp.add(new UpDownController(height, sizeCallback));
    this.modeBar = new ModeBarUi();
    this.menu = createMenu();

    ContentPropertyDTO cp = (ContentPropertyDTO) getContent().getProperties().get(ContentPropertyDTO.DELETABLE);
    if (cp != null && !Boolean.parseBoolean(cp.getValue())) {
      // hide if not deletable
      menuItems.get(DELETE_MENU_ITEM).setVisible(false);
      menuItems.get(DELETE_SEPARATOR_MENU_ITEM).setVisible(false);
    }

//    shareButton = new Button("Share");
//    shareButton.addClickListener(this);
	}
	
	public void init() {
//    addStyleName("sd-editor-diagram-edit-content");
	}

  public void addSaveListener(ContentSaveListener clickListener) {
		saveListeners.add(clickListener);
	}
	
	private	MenuBar createMenu() {
  	MenuBar menu = new MenuBar();
    menu.setAutoOpen(true);
    menu.setAnimationEnabled(true);
    
 // Create the edit menu
    MenuBar saveMenu = new MenuBar(true);
    
    menu.addItem(new MenuItem("File", true, saveMenu));
    saveMenu.addItem("Save", new Command() {
      public void execute() {
        onSave(SAVE);
      }
    });
    menuItems.put(SAVE_AND_CLOSE_MENU_ITEM, saveMenu.addItem("Save and Close", new Command() {
      public void execute() {
        onSave(SAVE_AND_CLOSE);
      }
    }));
    menuItems.put(DELETE_SEPARATOR_MENU_ITEM, saveMenu.addSeparator());
    menuItems.put(DELETE_MENU_ITEM, saveMenu.addItem("Delete", new Command() {
      public void execute() {
        deleteAction();
      }
    }));
    menuItems.put(SAVE_SEPARATOR_MENU_ITEM, saveMenu.addSeparator());
    
    menuItems.put(CLOSE_MENU_ITEM, saveMenu.addItem("Close Without Saving", new Command() {
        public void execute() {
          for (Iterator i = saveListeners.iterator(); i.hasNext();) {
            ContentSaveListener l = (ContentSaveListener) i.next();
            l.cancel(getContent());
          }
        }
      }));

    return menu;
	}
	
	public void setSupportsEditMenu(boolean supportsEditMenu) {
    this.supportsEditMenu = supportsEditMenu;
  }
	
	public boolean supportsEditMenu() {
    return supportsEditMenu;
  }
	
	protected IContent copy() {
	  return null;
	}
	
  protected void paste(IContent content) {
	}
  
  protected Class pasteType() {
    return null;
  }
  
  private HorizontalPanel createButtonsArea() {
  	HorizontalPanel result = new HorizontalPanel();
//  	result.getElement().setId("sketchboardme-buttonbar");
  	result.setStyleName("buttonbar");
  	new SimpleColorHandler(getEditorContext());
  	
  	if (getEditorContext().isTrue(EditorProperty.CONFLUENCE_MODE)) {
  		final ConfluenceMenu confmenu = new ConfluenceMenu(getEditorContext());
  		getEditorContext().getEventBus().addHandler(SaveButtonClickedEvent.TYPE, new SaveButtonClickedEventHandler() {
				@Override
				public void onSelection(SaveButtonClickedEvent event) {
					confmenu.clear();
					onSave(SAVE);				
				}
			});
//  		final ConfluenceMenu confmenu = new ConfluenceMenu(getEditorContext());
//  		confmenu.addSaveClickHandler(new EventListener() {
//				@Override
//				public void onBrowserEvent(Event event) {
//					confmenu.clear();
//					onSave(SAVE);				
//				}
//			});
  		
  		getEditorContext().getEventBus().addHandler(CancelButtonClickedEvent.TYPE, new CancelButtonClickedEventHandler() {
				
				@Override
				public void onSelection(CancelButtonClickedEvent event) {
					confmenu.clear();
			    externalize();
	        for (ContentSaveListener i : saveListeners) {
	          i.cancel(getContent());
	        }
				}
			});
	  	
//  		confmenu.addCancelClickHandler(new EventListener() {
//				@Override
//				public void onBrowserEvent(Event event) {
//					confmenu.clear();
//			    externalize();
//	        for (ContentSaveListener i : saveListeners) {
//	          i.cancel(getContent());
//	        }
//				}
//			});
	  	
  		getEditorContext().registerAndAddToRootPanel(confmenu);
//	  	RootPanel.get().add(confmenu);
  	}
  	
//  	Button scaleButton = new Button("Scale Board");
//  	scaleButton.addClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				scaleSlider.setVisible(true);
//			}
//		});
  	
//  	final Button deleteSelected = new Button("Delete selected");
//  	deleteSelected.setEnabled(false);
//  	final HTML deleteSelected = new HTML(SafeHtmlUtils.fromSafeConstant(""));
//  	deleteSelected.setStyleName("btn-delete");
//  	deleteSelected.addStyleName("btn-delete-disabled");
//  	deleteSelected.addClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				if (deleteSelected.getStyleName().contains("btn-delete-enabled")) {
//					getEditorContext().getEventBus().fireEvent(new DeleteSelectedEvent());
//				}
//			}
//		});
  	
  	// final SelectButtonBox relationShipType = new SelectButtonBox(getEditorContext());
//  	getEditorContext().getEventBus().addHandler(SelectionEvent.TYPE, new SelectionEventHandler() {
//			@Override
//			public void onSelection(SelectionEvent event) {
////				deleteSelected.removeStyleName("btn-delete-disabled");
////				deleteSelected.addStyleName("btn-delete-enabled");
//			}
//		});

//    getEditorContext().getEventBus().addHandler(UnselectAllEvent.TYPE, new UnselecteAllEventHandler() {
//			@Override
//			public void onUnselectAll(UnselectAllEvent event) {
////				deleteSelected.removeStyleName("btn-delete-enabled");
////				deleteSelected.addStyleName("btn-delete-disabled");
//			}
//		});
    
//    ColorButtonBox colorButton = new ColorButtonBox(getEditorContext());

    Widget freehandMode = new FreehandModeButton(getEditorContext());
  	
//  	result.add(save);
//  	result.add(cancel);

//    result.add(scaleButton);
//  	result.add(deleteSelected);
//  	result.add(modeBar);
  	// result.add(relationShipType);
//  	result.add(colorButton);
  	result.add(freehandMode);
  	if (supportsUndoMenu) {
  		result.add(new UndoMenu(getEditorContext()));
  	}

  	return result;
  }
  
	public void setWidget(Widget widget) {
		container.clear();
		this.widget = widget;

//		this.background = new VerticalPanel();
//		HorizontalPanel menuArea = new HorizontalPanel();
//		menuArea.setStyleName("menuArea");
//    menuArea.setWidth("100%");
//		
//		menuArea.setSpacing(5);
//		menuArea.add(menu);

//		menuArea.add(createButtonsArea());
		
//		HorizontalPanel buttons = new HorizontalPanel();
//		buttons.setWidth("100%");
//		menuArea.add(buttons);
////		menuArea.setCellHorizontalAlignment(buttons, HorizontalPanel.ALIGN_RIGHT);
//		menuArea.setCellWidth(buttons, "100%");
//		
//		background.add(menuArea);
//		
//		background.setStyleName("content-edit-buttons");
//		background.setWidth("100%");
//		background.setCellVerticalAlignment(buttons, VerticalPanel.ALIGN_MIDDLE);
//		
//		extendButtons(buttons);
//    extendMenuBar(menu);
//
//    buttons.add(resizeUi);
//    
//    // doesn't work on confluence 4 any longer changed to above, HTML5 supports only float: right;
//    buttons.setCellHorizontalAlignment(resizeUi, HorizontalPanel.ALIGN_RIGHT);
//
//    VerticalPanel contentWrapper = new VerticalPanel();
//    contentWrapper.setSpacing(0);
//    contentWrapper.add(background);
//    contentWrapper.add(widget);
//
//    container.add(leftBar);
//    container.add(contentWrapper);
//
//    leftBar.setHeight("100%");
//    container.setCellHeight(leftBar, "100%");
    
//		initWidget(container);
    super.setWidget(widget);
	}
  
	protected void extendButtons(HorizontalPanel buttons) {
  }

	protected void extendMenuBar(MenuBar menu) {
  }

  public void onKeyDown(KeyDownEvent event) {
    if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
      setSize();
    }
  }

  private void setSize() {
//    if (Integer.valueOf(getWidth().getText()) > 100 && Integer.valueOf(getHeight().getText()) > 100) {
//      setSize(Integer.valueOf(getWidth().getText()), Integer.valueOf(getHeight().getText()));
//    }
  }

  public abstract void setSize(int width, int height);

  public Widget getWidget() {
		return widget;
	}
	
  protected void onSave(SaveType saveType) {
    // update content
    externalize();
    
    if (!extendedSave()) {
      if (saveType == SAVE) {
        // notify save
        for (ContentSaveListener i : saveListeners) {
          i.save(getContent());
        }
      }
      if (saveType == SAVE_AND_CLOSE) {
        // notify close
        for (ContentSaveListener i : saveListeners) {
          i.close(getContent());
        }
      }
    }
  }

  protected boolean extendedSave() {
    // by default, nothing is done
    return false;
  }
  
//  public TextBox getWidth() {
//    return width;
//  }
//  
//  public TextBox getHeight() {
//    return height;
//  }
  
//  public int getWidth() {
//    return width;
//  }
//  public int getHeight() {
//    return height;
//  }
  
  @Override
  public void internalize() {
    checkDefaults();
    setSize(getContent().getWidth(), getContent().getHeight());
//    getWidth().setText(getContent().getWidth().toString());
//    getHeight().setText(getContent().getHeight().toString());
  }
  
  public void setFocus(boolean focus) {
    
  }
  
  private void deleteAction() {
    final DialogBox dialogBox = new DialogBox();
    dialogBox.setText("Delete Content?");
    dialogBox.setAnimationEnabled(true);
    Button okButton = new Button("OK");
    final Button cancelButton = new Button("Cancel");

    VerticalPanel dialogVPanel = new VerticalPanel();
    dialogVPanel.addStyleName("dialogVPanel");
    dialogVPanel.add(new HTML("<b>Whole content is deleted and cannot be restored!</b>"));
    dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
    HorizontalPanel buttons = new HorizontalPanel();
//    buttons.setSpacing(10);
    buttons.add(okButton);
    buttons.add(cancelButton);
    dialogVPanel.add(buttons);
    dialogBox.setWidget(dialogVPanel);
    
    // HACK:
    DeferredCommand.addCommand(new Command() {
      public void execute() {
        cancelButton.setEnabled(true);
        cancelButton.setFocus(true);
      }
    });

    // Add a handler to close the DialogBox
    okButton.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        for (Iterator i = saveListeners.iterator(); i.hasNext();) {
          ContentSaveListener l = (ContentSaveListener) i.next();
          l.delete(getContent());
        }
        dialogBox.hide();
      }
    });
    
    cancelButton.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        dialogBox.hide();
      }
    });

    dialogBox.center();
  }
  
  public void setVisibilityById(int id, boolean visible) {
    UIObject item = menuItems.get(id);
    if (item != null) {
      item.setVisible(visible);
    }
  }
  
  public IModeManager getModeManager() {
  	return modeBar;
  }
  
}
