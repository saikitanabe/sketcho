package net.sevenscales.editor.content;

import net.sevenscales.domain.DiagramContentDTO;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.api.IContent;
import net.sevenscales.domain.api.IDiagramContent;
import net.sevenscales.domain.api.IDiagramItem;
import net.sevenscales.editor.api.EditorContext;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.IModelingPanel;
import net.sevenscales.editor.api.ISurfaceHandler;

import net.sevenscales.editor.api.dojo.FactoryDoJo;

import net.sevenscales.editor.content.UiModelContentHandler.IUiDiagramContent;
import net.sevenscales.editor.content.utils.DiagramItemFactory;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.KeyEventListener;
import net.sevenscales.editor.diagram.SelectionHandler;
import net.sevenscales.editor.uicomponents.impl.BrowserStyleImpl;

import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.VerticalPanel;

public class UiSketchoBoardEditContent extends UiEditBoardContent implements KeyEventListener, IUiDiagramContent {
  private UiModelContentHandler modelHandler;
  private IModelingPanel modelingPanel;
//  private HorizontalPanel main = new HorizontalPanel();
  protected VerticalPanel vpanel = new VerticalPanel();
//  private DiagramContentQuickHelp help;
//  public UiDiagramEditContent(IContent content, Context context, boolean inDialog) {
//    this(content, context, true, inDialog);
//  }
  private static String alphabets = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
  private static boolean isReadOnlyUrl(String url) {
  	if (url.startsWith("sb")) {
  		// legacy
  		return false;
  	}
    return (alphabets.indexOf(url.charAt(0)) % 2) == 0;
  }

	
	public static boolean isEditableBoard() {
		String[] path = Window.Location.getPath().split("/");
		String boardname = path[path.length - 1];
		return !isReadOnlyUrl(boardname);
	}
  
	// TODO: is editable as parameter of as callback
  public UiSketchoBoardEditContent(IContent content, Context context, boolean editable, EditorContext editorContext) {
  	this(content, context, editable, editorContext, true);
  }
  
  public UiSketchoBoardEditContent(IContent content, Context context, boolean editable, EditorContext editorContext, boolean supportsUndoMenu) {
    super(content, context, false, supportsUndoMenu, editorContext);
    
//    getWidth().setText(getContent().getWidth().toString());
//    getHeight().setText(getContent().getHeight().toString());
//    leftBar.setHeight("100%");
//    leftBar.setBorderWidth(1);
//    leftBar.add(modeBar);
//    leftBar.setCellHeight(modeBar, "100%");
    getEditorContext().set(EditorProperty.SKETCHO_BOARD_MODE, true);
    getEditorContext().setEditable(editable);

    modelingPanel = FactoryDoJo.createModelingPanel(this, getContent().getWidth(), getContent().getHeight(), editable, getModeManager(), getEditorContext());
    modelingPanel.addKeyEventHandler(this);
    modelHandler = new UiModelContentHandler(this, editable, getEditorContext(), getModeManager());
    
    // default note, not added for now
//    modelingPanel.getSurface().addLoadEventListener(new SurfaceLoadedEventListener() {
//			@Override
//			public void onLoaded() {
//				IDiagramContent d = (IDiagramContent) getContent();
//				
//				// add guide text on a new board
//				// created time should be ~ about the same as updated at; give it a half a second boundary
//				if (d.getDiagramItems().size() == 0 && 
//						d.getCreatedTime() >= (d.getModifiedTime() - 500)) {
////				if (true) {
//					getEditorContext().set(EditorProperty.AUTO_RESIZE_ENABLED, true);
//					
//			  	getEditorContext().set(EditorProperty.ON_SURFACE_LOAD, true);
//					NoteElement ne = new NoteElement(modelingPanel.getSurface(),
//			        new NoteShape(0, 0, 250, 250),
//			        DiagramHelp.INSTANCE.boardGuideText(),
//			        AbstractDiagramItem.createDefaultBackgroundColor(), Color.createDefaultTextColor(), true);
//					getEditorContext().set(EditorProperty.ON_SURFACE_LOAD, false);
//
//					int middle = Window.getClientWidth() / 2;
//					int x = middle - ne.getWidth() / 2;
//					int y = Window.getClientHeight() / 2 - ne.getHeight() / 2 - 45;
//
//					ne.setShape(x, y, ne.getWidth(), ne.getHeight());
//					getEditorContext().set(EditorProperty.AUTO_RESIZE_ENABLED, false);
//					
//					// enable inserts temporarily
//					getEditorContext().set(EditorProperty.ON_CHANGE_ENABLED, true);
//					modelingPanel.getSurface().add(ne, true, false);
//					getEditorContext().set(EditorProperty.ON_CHANGE_ENABLED, false);
//				}
//			}
//		});

//    help = new DiagramContentQuickHelp(modelingPanel.getSurface(), modelingPanel.getToolFrame());
//    vpanel.setBorderWidth(1);
//    vpanel.add(help);
    vpanel.add(modelingPanel.getWidget());
    
//    decorator.getElement().getStyle().setBackgroundColor("#d0e4f6");

//    main.setCellHeight(modeBar, "100%");
//    main.add(vpanel);
    
    Window.enableScrolling(false);
    Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				setSize(Window.getClientWidth(), Window.getClientHeight());
//				System.out.println(Window.getClientWidth());
//				System.out.println(Window.getClientHeight());
			}
		});
  }
  
  @Override
  protected int getDefaultWidth() {
    return Window.getClientWidth();
  }
  
  @Override
  protected int getDefaultHeight() {
    return Window.getClientHeight();
  }

  public void externalize() {
    modelHandler.externalize();
  }

  public void internalize() {
    super.internalize();
    modelHandler.internalize();
  }
  
  public void reload() {
    modelHandler.onLoaded();
  }
  
  @Override
  public void setSize(int width, int height) {
    width = width > getDefaultWidth() ? width : getDefaultWidth();
    getContent().setWidth(width);
    height = height > getDefaultHeight() ? height : getDefaultHeight();
    getContent().setHeight(height);
    modelingPanel.setSize(width, height);
  }

  // @Override
  public boolean onKeyEventDown(int keyCode, boolean shift, boolean ctrl) {
    if (keyCode == BrowserStyleImpl.KEY_ENTER && ctrl) {
      onSave(SAVE);
      return true;
    }
    return false;
  }
  
//  @Override
  public boolean onKeyEventUp(int keyCode, boolean shift, boolean ctrl) {
    return false;
  }
  
  public void onKeyDown(Event event) {
  int keyCode = DOM.eventGetKeyCode(event);
  boolean ctrl = DOM.eventGetCtrlKey(event);
    if (keyCode == KeyboardListener.KEY_ENTER && ctrl) {
      onSave(SAVE);
    }
  }
  public void onKeyUp(Event event) {
  // TODO Auto-generated method stub
  
  }
  public void onKeyPress(Event event) {
  // TODO Auto-generated method stub  
  }
  
  @Override
  public void onKeyDown(KeyDownEvent event) {
    // TODO Auto-generated method stub
  }
  public void onKeyPress(KeyPressEvent event) {
    // TODO Auto-generated method stub
    
  }
  public void onKeyUp(KeyUpEvent event) {
    // TODO Auto-generated method stub
    
  }

  public void init() {
      setWidget(vpanel);
      super.init();
  }

  @Override
  public void refresh() {
    modelingPanel.getSurface().clear();
    modelHandler.addContentItems((IDiagramContent) getContent(), modelingPanel.getSurface());
//    modelHandler.onLoaded();
//    internalize();
  }
  
  @Override
  public void setVisible(boolean visible) {
    super.setVisible(visible);
//    modelingPanel.setVisible(visible);
  }

  public IModelingPanel getModelingPanel() {
    return modelingPanel;
  }
  
  @Override
  public IContent getContent() {
    return super.getContent();
  }
  
  @Override
  protected IContent copy() {
    SelectionHandler selectionHandler = modelingPanel.getSurface().getSelectionHandler();
    int selectedItems = selectionHandler.getSelectedItems().size();
    IDiagramContent dc = (IDiagramContent) getContent();
    IDiagramContent result = new DiagramContentDTO();
    if (selectedItems == 0) { // copy all
      for (Diagram d : modelingPanel.getSurface().getDiagrams()) {
        IDiagramItem di = DiagramItemFactory.createOrUpdate(d, 10, 10);
        if (di != null) {
          // focus circle is not any supported type even though it is in surface
          result.addItem(di);
        }
      }

    } else { // copy selected
      for (Diagram d : selectionHandler.getSelectedItems()) {
        IDiagramItem di = DiagramItemFactory.createOrUpdate(d, 10, 10);
        if (di != null) {
          // focus circle is not any supported type even though it is in surface
          result.addItem(di);
        }
      }
    }
    result.setWidth(dc.getWidth());
    result.setHeight(dc.getHeight());
    return result;
  }
  
  private boolean find(IDiagramItemRO item, SelectionHandler selectionHandler) {
    for (Diagram d : selectionHandler.getSelectedItems()) {
      IDiagramItemRO di = d.getDiagramItem();
      if (item == di || item.equals(di)) {
        return true;
      }
    }
    return false;
  }

  @Override
  protected void paste(IContent content) {
    if (content instanceof IDiagramContent) {
      modelHandler.addContentItems((IDiagramContent) content, modelingPanel.getSurface(), true);
    }
  }
  
  @Override
  protected Class pasteType() {
    return DiagramContentDTO.class;
  }

	public ISurfaceHandler getSurface() {
		return modelingPanel.getSurface();
	}

}
