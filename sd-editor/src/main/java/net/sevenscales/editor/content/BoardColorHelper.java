package net.sevenscales.editor.content;

import java.util.List;

import com.google.gwt.event.shared.HandlerManager;

import net.sevenscales.domain.api.IDiagramItem;
import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.event.ThemeChangedEvent;
import net.sevenscales.editor.api.impl.Theme;
import net.sevenscales.editor.api.impl.Theme.ElementColorScheme;
import net.sevenscales.editor.api.impl.Theme.ThemeName;
import net.sevenscales.editor.api.ot.BoardDocument;
import net.sevenscales.editor.api.ot.BoardDocumentHelpers;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.uicomponents.CircleElement;

public class BoardColorHelper {
  private static final SLogger logger = SLogger.createLogger(BoardColorHelper.class);
  
  private ISurfaceHandler surface;
  private BoardDocument boardDocument;
  private ISurfaceHandler toolbar;
  private BoardDocument toolbarDocument;
  private HandlerManager evenBus;

  public BoardColorHelper(HandlerManager eventBus) {
    this.evenBus = eventBus;
  }

  public void setBoardBackgroundroundColor(String colorName) {
    if (surface != null && toolbar != null) {
      applyBackgroundColor(surface, boardDocument, colorName);
      applyBackgroundColor(toolbar, toolbarDocument, colorName);
      Theme.setColorScheme(colorName);
      trigger(colorName);
      evenBus.fireEvent(new ThemeChangedEvent());
    }
  }

  private native void trigger(String colorName)/*-{
    $wnd.$($doc).trigger('theme-changed', colorName)
  }-*/;

  public void setSurface(ISurfaceHandler surface, ISurfaceHandler toolbar, BoardDocument boardDocument) {
    this.surface = surface;
    this.toolbar = toolbar;
    this.boardDocument = boardDocument;
    resetToolbarDocument();
  }

  private void resetToolbarDocument() {
    List<IDiagramItem> toolbarItems = BoardDocumentHelpers.getDiagramsAsDTO(toolbar.getDiagrams(), true);
    generateTestIds(toolbarItems);
    if (toolbarDocument == null) {
      this.toolbarDocument = new BoardDocument(toolbarItems, "ToolbarDocument");
    } else {
      toolbarDocument.reset(toolbarItems);
    }
  }

  private void generateTestIds(List<IDiagramItem> toolbarItems) {
    int i = 1;
    for (IDiagramItem item : toolbarItems) {
      item.setClientId(Integer.valueOf(i++).toString());
    }
  }

  private void applyBackgroundColor(ISurfaceHandler surface, BoardDocument document, String newThemeName) {
    ElementColorScheme currentColorScheme = Theme.getCurrentColorScheme();
    ElementColorScheme newColorScheme = Theme.getColorScheme(ThemeName.getEnum(newThemeName));
    for (Diagram d : surface.getDiagrams()) {
      applyThemeToDiagram(d, currentColorScheme, newColorScheme);
    }
  }

  public static void applyThemeToDiagram(Diagram d, ElementColorScheme currentColorScheme, ElementColorScheme newColorScheme) {
    if (currentColorScheme.equals(newColorScheme)) {
      // don't do anything except apply annotations if needed
      d.applyAnnotationColors();
      return;
    }

    boolean notCircleElement = !(d instanceof CircleElement);
    if (notCircleElement) {
      if (d.usesSchemeDefaultTextColor(currentColorScheme)) {
        d.setTextColor(d.getDefaultTextColor(newColorScheme));
      }
      if (d.usesSchemeDefaultBorderColor(currentColorScheme)) {
        d.setBorderColor(d.getDefaultBorderColor(newColorScheme));
      }
      if (d.usesSchemeDefaultBackgroundColor(currentColorScheme)) {
        d.setBackgroundColor(d.getDefaultBackgroundColor(newColorScheme));
      }
    } 
    // else if (notCircleElement && d.isTextElementBackgroundTransparent()) {
    //   // need to switch text color since it might not be visible, e.g. white on white, see actor
    //   d.setTextColor(d.getDefaultTextColor(newColorScheme));
    // }

    // checks if diagram is annotation or not and applies colors accordingly
    d.applyAnnotationColors();
  }

}
