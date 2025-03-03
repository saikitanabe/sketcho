package net.sevenscales.editor.api;

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import net.sevenscales.editor.api.impl.Theme;
import net.sevenscales.editor.api.ot.OTBuffer;
import net.sevenscales.editor.api.ot.OperationTransaction;
import net.sevenscales.editor.content.ui.IModeManager;
import net.sevenscales.editor.content.utils.ShowHideHelpers;
import net.sevenscales.editor.diagram.DiagramSelectionHandler;
import net.sevenscales.editor.api.event.ThemeChangedEvent;
import net.sevenscales.editor.api.event.ThemeChangedEventHandler;

public class ToolFrame extends SimplePanel {
	private Properties properties;
  private Libarary toolbar;
	private EditorContext editorContext;
	// private VerticalPanel panel;
	private SimplePanel justBacgkround;
	private ShowHideHelpers showHideHelpers;

  public ToolFrame(ISurfaceHandler surface, int height, IModeManager modeManager, EditorContext editorContext, boolean autohide, OTBuffer otBuffer, OperationTransaction operationTransaction) {
  	this.editorContext = editorContext;
  	
		// panel = new VerticalPanel();
		// panel.setStyleName("ToolFrame-panel");
		// panel.setHeight("100%");
		// panel.setSpacing(0);
		this.toolbar = new Libarary(surface, height, modeManager, editorContext, otBuffer, operationTransaction);
		// DEBUGGING START handy way to disable library, just comment next line
		// panel.add(toolbar);
		// DEBUGGING END
		// panel.setCellHeight(toolbar, height+"px");
//		h = (int) (height * 0.2);
		properties = new Properties(90, surface, surface.getSelectionHandler(), editorContext);
		properties.addSurface(surface, true); 
    properties.addSurface(toolbar.getSurfaceHandler(), false); 
//		panel.add(properties);
		
		justBacgkround = new SimplePanel();
//		justBacgkround.getElement().setId("sketchboardme-toolframe");
		if (Theme.isBlackTheme()) {
			justBacgkround.setWidget(new HTML("<img src='/static/images/menu_open_black_01.svg' class='library-show-ide-button'>"));
		} else {
			justBacgkround.setWidget(new HTML("<img src='/static/images/menu_open_white.svg' class='library-show-ide-button'>"));
		}

    editorContext.getEventBus().addHandler(ThemeChangedEvent.TYPE, new ThemeChangedEventHandler() {
      @Override
      public void on(ThemeChangedEvent event) {
        if (Theme.isBlackTheme()) {
          justBacgkround.setWidget(new HTML("<img src='/static/images/menu_open_black_01.svg' class='library-show-ide-button'>"));
        } else {
          justBacgkround.setWidget(new HTML("<img src='/static/images/menu_open_white.svg' class='library-show-ide-button'>"));
        }
      }
    });

		justBacgkround.setStyleName("library-showhide-area");
		
		// justBacgkround.setWidget(panel);
    RootPanel.get().add(toolbar);

		if (editorContext.isTrue(EditorProperty.SKETCHO_BOARD_MODE)) {
			// currently enabled only on Sketchboard.Me
			showHideHelpers = new ShowHideHelpers(justBacgkround, toolbar, editorContext.isEditable(), editorContext);
		}

    if (!editorContext.isEditable()) {
      return;
    }

		if (editorContext.isTrue(EditorProperty.SKETCHO_BOARD_MODE)) {
			editorContext.registerAndAddToRootPanel(justBacgkround);
//			RootPanel.get().add(justBacgkround);
		} else {
			setWidget(justBacgkround);
		}
		
		addStyleName("ToolFrame");
	}
  
  @Override
  public void setVisible(boolean visible) {
    super.setVisible(visible);
    toolbar.setVisible(visible);
  }
  
  IToolSelection getToolSelection() {
    return toolbar;
  }
  
  public void addSelectionHandler(DiagramSelectionHandler handler) {
    toolbar.addSelectionHandler(handler);
  }

  public void addMouseEnterHandler(MouseOverHandler handler) {
    addDomHandler(handler, MouseOverEvent.getType());
  }

  public void addMouseOutHandler(MouseOutHandler handler) {
    addDomHandler(handler, MouseOutEvent.getType());
  }
  
  public Properties getProperties() {
		return properties;
	}
  
  public ISurfaceHandler getToolbar() {
		return toolbar.getToolPool();
	}

	public void hideToolbar() {
		if (editorContext.isTrue(EditorProperty.SKETCHO_BOARD_MODE)) {
			// for now enabled only on board mode, need to check this out
			// later for sketcho confluence
			showHideHelpers.forceFadeOut();
		}
	}

}
