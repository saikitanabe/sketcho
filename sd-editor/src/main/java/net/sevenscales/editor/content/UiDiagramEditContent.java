package net.sevenscales.editor.content;

import net.sevenscales.domain.DiagramContentDTO;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.api.IContent;
import net.sevenscales.domain.api.IDiagramContent;
import net.sevenscales.domain.api.IDiagramItem;
import net.sevenscales.editor.api.EditorContext;
import net.sevenscales.editor.api.ModelingPanel;
import net.sevenscales.editor.content.UiModelContentHandler.IUiDiagramContent;
import net.sevenscales.editor.content.ui.DiagramContentQuickHelp;
import net.sevenscales.editor.content.utils.DiagramItemFactory;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.KeyEventListener;
import net.sevenscales.editor.diagram.SelectionHandler;
import net.sevenscales.editor.uicomponents.impl.BrowserStyleImpl;

import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.VerticalPanel;

public class UiDiagramEditContent extends UiEditContent implements KeyEventListener, IUiDiagramContent {
  private UiModelContentHandler modelHandler;
  private ModelingPanel modelingPanel;
//  private HorizontalPanel main = new HorizontalPanel();
  protected VerticalPanel vpanel = new VerticalPanel();
  private DiagramContentQuickHelp help;

//  public UiDiagramEditContent(IContent content, Context context, boolean inDialog) {
//    this(content, context, true, inDialog);
//  }
  
  public UiDiagramEditContent(IContent content, Context context, boolean supportsEditMenu, boolean inDialog, EditorContext editorContext) {
    super(content, context, supportsEditMenu, editorContext);
    boolean editable = true;
    
//    getWidth().setText(getContent().getWidth().toString());
//    getHeight().setText(getContent().getHeight().toString());
//    leftBar.setHeight("100%");
//    leftBar.setBorderWidth(1);
//    leftBar.add(modeBar);
//    leftBar.setCellHeight(modeBar, "100%");

    modelingPanel = new ModelingPanel(this, getContent().getWidth(), getContent().getHeight(), editable, getModeManager(), getEditorContext(), inDialog, false);
    modelingPanel.addKeyEventHandler(this);
    modelHandler = new UiModelContentHandler(this, editable, getEditorContext(), getModeManager());
    help = new DiagramContentQuickHelp(modelingPanel.getSurface(), modelingPanel.getToolFrame());
//    vpanel.setBorderWidth(1);
    vpanel.add(help);
    vpanel.add(modelingPanel);
    
//    decorator.getElement().getStyle().setBackgroundColor("#d0e4f6");

//    main.setCellHeight(modeBar, "100%");
//    main.add(vpanel);
  }
  
  @Override
  protected int getDefaultWidth() {
    return 800;
  }
  
  @Override
  protected int getDefaultHeight() {
    return 600;
  }

  public void externalize() {
    modelHandler.externalize();
  }

  public void internalize() {
    super.internalize();
    modelHandler.internalize();
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
    internalize();
  }
  
  @Override
  public void setVisible(boolean visible) {
    super.setVisible(visible);
//    modelingPanel.setVisible(visible);
  }

  public ModelingPanel getModelingPanel() {
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
        IDiagramItem di = DiagramItemFactory.createOrUpdate(d, true, 10, 10);
        if (di != null) {
          // focus circle is not any supported type even though it is in surface
          result.addItem(di);
        }
      }

    } else { // copy selected
      for (Diagram d : selectionHandler.getSelectedItems()) {
        IDiagramItem di = DiagramItemFactory.createOrUpdate(d, true, 10, 10);
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
}
