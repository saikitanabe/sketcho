package net.sevenscales.sketchoconfluenceapp.client.view;

import java.util.ArrayList;
import java.util.List;

import net.sevenscales.domain.DiagramContentDTO;
import net.sevenscales.domain.api.IContent;
import net.sevenscales.domain.api.IDiagramContent;
import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.EditorContext;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.SurfaceHandler;
import net.sevenscales.editor.api.SurfaceLoadedEventListener;
import net.sevenscales.editor.api.event.EditDiagramPropertiesEndedEvent;
import net.sevenscales.editor.api.event.EditDiagramPropertiesEndedEventHandler;
import net.sevenscales.editor.api.event.EditDiagramPropertiesStartedEvent;
import net.sevenscales.editor.api.event.EditDiagramPropertiesStartedEventHandler;
import net.sevenscales.editor.api.event.FreehandModeChangedEvent;
import net.sevenscales.editor.content.ContentSaveListener;
import net.sevenscales.editor.content.Context;
import net.sevenscales.editor.content.UiSketchoBoardEditContent;
import net.sevenscales.editor.content.utils.ContentEventUtils;
import net.sevenscales.editor.content.utils.JQuery;
import net.sevenscales.editor.diagram.utils.UiUtils;
import net.sevenscales.editor.gfx.svg.converter.SvgConverter;
import net.sevenscales.editor.gfx.svg.converter.SvgData;
import net.sevenscales.editor.ui.WarningDialog;
import net.sevenscales.sketchoconfluenceapp.client.Sketcho_confluence_app;
import net.sevenscales.sketchoconfluenceapp.client.util.DiagramContentFactory;
import net.sevenscales.sketchoconfluenceapp.client.util.ot.BoardOTConfluenceHandler;
import net.sevenscales.sketchoconfluenceapp.client.util.ot.Spinner;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class SketchoEditor extends Composite implements Spinner {
	private static SLogger logger = SLogger.createLogger(SketchoEditor.class);

	// to calculate weather Confluence shortcut keys should be enabled or not
	// editor is closed
	private static int numberOfEditorOpen = 0;

  private static SketchoEditorUiBinder uiBinder = GWT.create(SketchoEditorUiBinder.class);

  interface SketchoEditorUiBinder extends UiBinder<Widget, SketchoEditor> {
  }

  public interface MyStyle extends CssResource {
    String image();
  }

  @UiField
  MyStyle style;

  // @UiField Label errorLabel;
//  @UiField SimplePanel mainPanel;
  @UiField DeckPanel deck;
//  @UiField HTML editLink;
  @UiField SimplePanel editPanel;
  @UiField SimplePanel readPanel;
//  @UiField HTML editInDialog;
//  @UiField HorizontalPanel linkpanel;

  private Context context;
  private String name;

	private boolean inDialog;

	private DialogBox dialogBox;
	private SlideShow slideShow;

	private String modelurl;

	private DiagramContentDTO originalContent;

	private UiSketchoBoardEditContent currentEditContent;

	private HandlerRegistration keyDownRegistration;
	private HandlerRegistration keyPressRegistration;

	protected boolean editingProperties;
	private EditorContext editorContext = new EditorContext();

	private String spaceId;
	private Long pageId;

	private String restoreSplitterContentZIndex;

	private Boolean editable;
	private static PopupPanel spinnerPanel;
	
	private BoardOTConfluenceHandler boardHandler;

	private String selector;
	
	static {
		spinnerPanel = new PopupPanel();
		spinnerPanel.setStyleName("sketchboardme-spinner");
		SimplePanel panel = new SimplePanel();
		panel.setStyleName("sketchboardme-spinner-bg");
		spinnerPanel.setWidget(panel);
		spinnerPanel.setAutoHideEnabled(false);
		
		// handy for testing
//		spinnerPanel.center();

//  	RootPanel.get().add(spinnerPanel);
	}

  public SketchoEditor(Context context) {
  	this.context = context;
  	configureContext(editorContext);
    initWidget(uiBinder.createAndBindUi(this));
    deck.showWidget(0);
	}
  
	public void openSketch(String spaceId, Long pageId, String name, String selector, boolean editable) {
		this.spaceId = spaceId;
		this.pageId = pageId;
		this.name = name;
		this.selector = selector;
		this.editable = editable;
		
		logger.debug("openSketch: spaceId {} pageId {}, name {}, selector {}, editable {}", spaceId, pageId, name, selector, editable);
		resolveImgUrl();
		openEdit(null);
	}

	private void resolveImgUrl() {
	  String[] ssplit = spaceId.split(":");
//	  String[] imageIdSplit = ssplit[1].split("\\.");
	  // name:versionId
	  this.modelurl = DiagramContentFactory.diagramImageUrl(pageId, ssplit[0], ssplit[1], true);
	}

	private void applyImageUrl() {
		resolveImgUrl();
    logger.debug("applyImageUrl: " + modelurl);
    // this would be needed only for sketcho demo editor
    // because version number is not changing
//    image.setUrl(modelurl + "&t=" + System.currentTimeMillis());
    
    // fetch full image before hand; otherwise dialog cannot be
    // centered correctly
//		Image.prefetch(getModelImageUrl());
//    image.setUrl(modelurl);
    JQuery.attr(selector, "src", modelurl);
	}

  // Removed. This will be replaced with a board. 
//	@UiHandler("editInDialog")
//  public void onEditInDialog(ClickEvent event) {
//  	final DialogBox dialogBox = createDialogBox();
//    DiagramContentFactory.create(name, context,
//        new AsyncCallback<UiDiagramEditContent>() {
//          public void onSuccess(UiDiagramEditContent result) {
//            edit(result);
//            dialogBox.setText(name);
//            dialogBox.setWidget(result);
//            if (IShapeFactory.Util.isCurrentGfxSvg()) {
//              dialogBox.center();
//            } else {
//            	// IE8 hack again; probably would need to wait loading of content
//            	// before setting the center
//            	dialogBox.setPopupPosition(0, 0);
//            }
//            dialogBox.show();
//          }
//
//          public void onFailure(Throwable caught) {
//            // errorLabel.setText(caught.getMessage());
//          }
//        }, true);
//  }
  
  private DialogBox createDialogBox() {
  	inDialog = true;
    dialogBox = new DialogBox();
//    dialogBox.setStyleName("NoClass");
    dialogBox.setGlassEnabled(true);
    dialogBox.setAnimationEnabled(true);
		return dialogBox;
	}

//	@UiHandler("editLinkWrapper")
//  public void edit(ClickEvent event) {
//		openEdit(event);
//  }

	private void openEdit(ClickEvent event) {
		logger.debug("openEdit...");
		showSpinner();
//
//		spinner.getElement().getStyle().setLeft(event.getClientX(), Unit.PX);
//		spinner.getElement().getStyle().setTop(event.getClientY(), Unit.PX);

		// disable freehand mode on confluence, since same editor is loaded and remembering the previous state
		editorContext.getEventBus().fireEvent(new FreehandModeChangedEvent(false));

    DiagramContentFactory.create(pageId, name, context,
        new AsyncCallback<IDiagramContent>() {
          public void onSuccess(IDiagramContent result) {
          	logger.debug("openEdit.onSuccess...");
          	hideSpinner();

          	if (currentEditContent == null) {
    	      	currentEditContent = new UiSketchoBoardEditContent(result, context, true, editorContext, true);
    	      	boardHandler = new BoardOTConfluenceHandler(name, context, editorContext, currentEditContent, result);
    	      	//        editContent.setSupportsEditMenu(false);
//    	      	currentEditContent.setVisibilityById(UiEditContent.CLOSE_MENU_ITEM, false);
//    	      	currentEditContent.setVisibilityById(UiEditContent.SAVE_AND_CLOSE_MENU_ITEM, false);
//    	      	currentEditContent.setVisibilityById(UiEditContent.DELETE_MENU_ITEM, false);
//    	      	currentEditContent.setVisibilityById(UiEditContent.DELETE_SEPARATOR_MENU_ITEM, false);
//    	      	currentEditContent.setVisibilityById(UiEditContent.SAVE_SEPARATOR_MENU_ITEM, false);
              editPanel.setWidget(currentEditContent);
            	currentEditContent.addSaveListener(new ContentSaveListener() {
                public void share(IContent content) {
                }

                public void save(IContent content) {
                	showSpinner();
                	if (!originalContent.equals(content)) {
                		assert(content != null);
                		// do not save if content has not changed
          	        SvgConverter sc = new SvgConverter();
          	        SvgData svg = sc.convertToSvg((IDiagramContent) content,
          	        		currentEditContent.getModelingPanel().getSurface());
          	        DiagramContentFactory.store(pageId, name, (IDiagramContent) content, svg, new AsyncCallback<String>() {
          	        	@Override
          	        	public void onSuccess(String result) {
                        closeEditor(result);
          	        	}
          	        	@Override
          	        	public void onFailure(Throwable caught) {
          	        		logger.error("Save failed", caught);
          	        		Window.alert("Ups, unexpected error: " + caught.getMessage());
          	        	}
          					});
                	} else {
                		// no changes just close the editor
                		closeEditor(modelurl);
                	}
                }

                public void generateImage(IDiagramContent content,
                    SurfaceHandler surfaceHandler) {
                }

                public void delete(IContent content) {
                }

                public void close(IContent content) {
                }

                public void cancel(IContent content) {
                	if (originalContent.equals(content)) {
                		closeEditor(modelurl);
                	} else {
                		// show warning that content has been changed
                		WarningDialog warningDialog = new WarningDialog(new WarningDialog.WarningHandler() {
          						@Override
          						public void doIt() {
          							closeEditor(modelurl);
          						}
          						@Override
          						public void cancel() {
          						}
          					});
                		warningDialog.show("Diagram has been changed. Close without saving?");
                	}
                }
              });
          	} else {
//              editPanel.remove(currentEditContent);
          		boardHandler.setContent(result);
          	}

            deck.showWidget(1);
            edit();
            editorContext.showEditor();
            confluenceCustomModificationsOnOpen();
            
            // this is actually must for full screen editing or calculations will fail
            // on drag handles
//            Document.get().getBody().getStyle().setMargin(0, Unit.PX);
          }

					public void onFailure(Throwable caught) {
          	Window.alert("Ups, something unexpected. Please try again.");
            // errorLabel.setText(caught.getMessage());
          }
        }, false, editorContext);
	}
	
  private void confluenceCustomModificationsOnOpen() {
    Window.enableScrolling(false);
    
    // Confluence 3.5 has bug having z-index: 0 that breaks layering system.
    Element splitterContent = Document.get().getElementById("splitter-content");
    if (splitterContent != null && splitterContent.getStyle().getZIndex() != null) {
    	restoreSplitterContentZIndex = splitterContent.getStyle().getZIndex();
    	splitterContent.getStyle().clearZIndex();
    }
	}
  
  private void confluenceCustomModificationsOnClose() {
    Window.enableScrolling(true);
    try {
    	if (restoreSplitterContentZIndex != null) {
    		Integer restore = Integer.valueOf(restoreSplitterContentZIndex);
    		Element splitterContent = Document.get().getElementById("splitter-content");
    		if (splitterContent != null) {
    			splitterContent.getStyle().setZIndex(restore);
    		}
    	}
    } catch (Exception e) {
    	// ignore exception, some unknown value is retrieved, anyway should work without putting the
    	// value back, it is anyway removed in at least on 4.1.6 
    }
  }

	
	private void closeEditor(String url) {
	  boardHandler.closeGlobalElements();
		
    removeStyleName("SketchoBoard");
    confluenceCustomModificationsOnClose();
    
//		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
//		@Override
//		public void execute() {
//	    Window.scrollTo(0, getAbsoluteTop());
//			}
//		});
    

    editorContext.closeEditor();
    
		// don't let it go below 0
		--numberOfEditorOpen;
		numberOfEditorOpen = (numberOfEditorOpen <= 0 ? 0 : numberOfEditorOpen); 

  	enableConfluenceShortCuts(true);
  	keyDownRegistration.removeHandler();
  	keyPressRegistration.removeHandler();
  	
		modelurl = url;
		if (inDialog) {
			dialogBox.hide();
		}
		inDialog = false;
    deck.showWidget(0);
    
    boardHandler.clear();

    applyImageUrl();
    hideSpinner();
	}
	
  private void enableConfluenceShortCuts(boolean enable) {
		System.out.println("numberOfEditorOpen simultaneously: " + numberOfEditorOpen);
		
		if (numberOfEditorOpen <= 0) {
			_enableConfluenceShortCuts(true);
		} else {
			_enableConfluenceShortCuts(false);
		}
	}
	private native void _enableConfluenceShortCuts(boolean enable)/*-{
//		function setConfluenceSetting() {
//			if ($wnd.Confluence && $wnd.Confluence.KeyboardShortcuts) {
//				$wnd.Confluence.KeyboardShortcuts.enabled = enable;
//			}
//		}
		
		// TODO: needs to check keyboards shortcuts variable or might
		// enable when user doesn't want to have those enabled.  
		if (enable && $wnd.AJS) {
			if ($wnd.Confluence.KeyboardShortcuts.enabled) {
				if (typeof $wnd.console != "undefined") $wnd.console.log("Restore Confluence keyboard shortcuts");
				$wnd.AJS.trigger("add-bindings.keyboardshortcuts");
			}
		} else if ($wnd.AJS) {
			if (typeof $wnd.console != "undefined") $wnd.console.log("Remove Confluence keyboard shortcuts temporarily");
			$wnd.AJS.trigger("remove-bindings.keyboardshortcuts");
		} else {
			if (typeof $wnd.console != "undefined") $wnd.console.log("Confluence keyboard binding removal failed");
		}
		
//		setConfluenceSetting();
	}-*/;

  public void edit() {
		++numberOfEditorOpen;

    setStyleName("SketchoBoard");

  	currentEditContent.getEditorContext().getEventBus().addHandler(EditDiagramPropertiesStartedEvent.TYPE, new EditDiagramPropertiesStartedEventHandler() {
			@Override
			public void on(EditDiagramPropertiesStartedEvent event) {
				SketchoEditor.this.editingProperties = true;
			}
		});
  	
  	currentEditContent.getEditorContext().getEventBus().addHandler(EditDiagramPropertiesEndedEvent.TYPE, new EditDiagramPropertiesEndedEventHandler() {
			@Override
			public void on(EditDiagramPropertiesEndedEvent event) {
				SketchoEditor.this.editingProperties = false;
			}
		});
  	
  	enableConfluenceShortCuts(false);
  	keyDownRegistration = RootPanel.get().addDomHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (!SketchoEditor.this.editingProperties) {
					// allow e.g. backspace
					ContentEventUtils.disableNavigationAway(event);
				}
				
				if (UiUtils.isChrome() && !SketchoEditor.this.editingProperties) {
					System.out.println("chrome global: " + event.getNativeKeyCode());
					// pass editor global key events to modeling panel
//					currentEditContent.getModelingPanel().handleKeyDownEvent(event);
				}
			}
		}, KeyDownEvent.getType());

  	keyPressRegistration = RootPanel.get().addDomHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
//				System.out.println("event press");
				if (UiUtils.isChrome() && !SketchoEditor.this.editingProperties) {
					// chrome duplicates events from text area as well
					System.out.println("chrome global press: " + event.getCharCode());
					// pass editor global key events to modeling panel
//					currentEditContent.getModelingPanel().handleKeyPressEvent(event);
				}
			}
		}, KeyPressEvent.getType());

  	currentEditContent.init();
  	currentEditContent.internalize();
  	currentEditContent.getModelingPanel().getSurface().addLoadEventListener(new SurfaceLoadedEventListener() {
			@Override
			public void onLoaded() {
				// externalize content from surface
				currentEditContent.externalize();
		    originalContent = new DiagramContentDTO(currentEditContent.getContent());
			}
		});
  }
  
  private void configureContext(EditorContext editorContext) {
  	editorContext.setEditable(true);
  	editorContext.set(EditorProperty.CONFLUENCE_MODE, true);
  	editorContext.set(EditorProperty.RESOURCES_PATH, Sketcho_confluence_app.resourcesPath);
	}

//	@UiHandler("image")
//  public void onImage(ClickEvent event) {
//		if (editable) {
//			openEdit(event);
//		}
//		// disabled for now to work with touch devices as well
////		startSlideShow();
//  }
	
	private void startSlideShow() {
  	if (slideShow == null) {
  		slideShow = new SlideShow();
  	}
  	
  	// Provide slide show pictures
  	List<String> urls = new ArrayList<String>();
		for (SketchoEditor se : Sketcho_confluence_app.getInstance().getEditors()) {
			urls.add(se.getModelImageUrl());
		}
		
		slideShow.setSlideUrls(urls);
  	slideShow.show(getModelImageUrl());
	}

	private String getModelImageUrl() {
		return this.modelurl.replace("thumb/", "img/");
	}

	@Override
	public void showSpinner() {
		spinnerPanel.center();
	}

	@Override
	public void hideSpinner() {
		spinnerPanel.hide();
	}
}
