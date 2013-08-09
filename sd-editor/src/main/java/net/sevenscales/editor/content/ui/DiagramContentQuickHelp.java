package net.sevenscales.editor.content.ui;

import java.util.List;

import net.sevenscales.editor.api.SurfaceHandler;
import net.sevenscales.editor.api.ToolFrame;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.DiagramSelectionHandler;
import net.sevenscales.editor.uicomponents.uml.Relationship2;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

public class DiagramContentQuickHelp extends Composite {

  private static QuickHelpUiBinder uiBinder = GWT.create(QuickHelpUiBinder.class);

  interface QuickHelpUiBinder extends UiBinder<Widget, DiagramContentQuickHelp> {
  }

  @UiField HorizontalPanel helpLine;
  @UiField HTML helptext;
//  @UiField HTML hideHelp;
//  @UiField Button closeButton;
  
  private boolean firstTime = true;
  private boolean overtoolbar = true;
  private int selectionCounter = 0;
  
  public DiagramContentQuickHelp(SurfaceHandler surfaceHandler, ToolFrame toolFrame) {
    initWidget(uiBinder.createAndBindUi(this));
    
    // for some reason IE doesn't accept background-color in ui.xml
//    helpLine.getElement().getStyle().setBackgroundColor("#d0e4f6");  
    
    toolFrame.addMouseEnterHandler(new MouseOverHandler() {
      @Override
      public void onMouseOver(MouseOverEvent event) {
        helptext.setHTML(DiagramHelp.INSTANCE.addelement());
        overtoolbar = true;
      }
    });
    toolFrame.addMouseOutHandler(new MouseOutHandler() {
      @Override
      public void onMouseOut(MouseOutEvent event) {
//        helptext.setHTML(DiagramHelp.INSTANCE.empty());
        overtoolbar = false;
      }
    });
    
    helptext.setHTML(DiagramHelp.INSTANCE.addelement());
//    HorizontalPanel h;
//    h.setCellHorizontalAlignment(w, align)
    surfaceHandler.addSelectionListener(new DiagramSelectionHandler() {
      @Override
      public void unselectAll() {
        if (firstTime) {
          helptext.setHTML(DiagramHelp.INSTANCE.addelement());
        } else if (!overtoolbar) {
          helptext.setHTML(DiagramHelp.INSTANCE.backgrounddrag());
        }
        firstTime = false;
      }
      @Override
      public void unselect(Diagram sender) {
      }
      @Override
      public void selected(List<Diagram> sender) {
        ++selectionCounter;
        if (selectionCounter % 3 == 0) {
          helptext.setHTML(DiagramHelp.INSTANCE.copypaste());
          return;
        }
        
        if (sender instanceof Relationship2) {
          helptext.setHTML(DiagramHelp.INSTANCE.relationship());
        } else {
          helptext.setHTML(DiagramHelp.INSTANCE.quickrelationship());
        }
      }
    });
  }
  
//  @UiHandler("hideHelp")
//  public void hideHelp(ClickEvent event) {
//    setVisible(false);
//  }

}
