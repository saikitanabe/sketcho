package net.sevenscales.editor.content;

import com.google.gwt.event.shared.HandlerManager;
import java.util.List;
import net.sevenscales.domain.api.IDiagramItem;
import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.event.ThemeChangedEvent;
import net.sevenscales.editor.api.event.ThemeChangedEventHandler;
import net.sevenscales.editor.api.impl.Theme;
import net.sevenscales.editor.api.impl.Theme.ElementColorScheme;
import net.sevenscales.editor.api.impl.Theme.ThemeName;
import net.sevenscales.editor.api.ot.BoardDocument;
import net.sevenscales.editor.api.ot.BoardDocumentHelpers;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.uicomponents.CircleElement;
import net.sevenscales.editor.uicomponents.uml.ChildTextElement;



public class BoardColorHelper {
  private static final SLogger logger = SLogger.createLogger(BoardColorHelper.class);
  
  private ISurfaceHandler surface;
  private BoardDocument boardDocument;
  private ISurfaceHandler toolbar;
  private BoardDocument toolbarDocument;
  private HandlerManager evenBus;

  public BoardColorHelper(HandlerManager eventBus) {
    this.evenBus = eventBus;

		evenBus.addHandler(ThemeChangedEvent.TYPE, new ThemeChangedEventHandler() {
      @Override
      public void on(ThemeChangedEvent event) {
        applyThemeChanged();
      }
    });

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
    // $wnd.$($doc).trigger('theme-changed', colorName)
    $wnd.themeChangedStream.push(colorName)
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
      this.toolbarDocument = new BoardDocument(toolbarItems, "toolbar-no-id", "ToolbarDocument");
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

    // >>>>>>>>>>>> COMMENTED 20.11.2014
    // child text didn't apply correct background color when loading the board
    // if (currentColorScheme.equals(newColorScheme)) {
    //   // don't do anything except apply annotations if needed
    //   d.applyAnnotationColors();
    //   return;
    // }
    // <<<<<<<<<<<< COMMENTED 20.11.2014

    boolean notCircleElement = !(d instanceof CircleElement);
    if (notCircleElement) {
      if (d.usesSchemeDefaultTextColor(currentColorScheme)) {
        d.setTextColor(d.getDefaultTextColor(newColorScheme));
      }
      if (d.usesSchemeDefaultBorderColor(currentColorScheme)) {
        d.setBorderColor(d.getDefaultBorderColor(newColorScheme));
      }
      if (d.usesSchemeDefaultBackgroundColor(currentColorScheme)) {
        if (d.getSurfaceHandler().isExporting() && d instanceof ChildTextElement) {
          // Fix 18.9.2019 ST: when exporting background color should be white
          d.setBackgroundColor(ThemeName.WHITE.getBoardBackgroundColor());
        } else {
          d.setBackgroundColor(d.getDefaultBackgroundColor(newColorScheme));
        }
      }
    }
    // else if (notCircleElement && d.isTextElementBackgroundTransparent()) {
    //   // need to switch text color since it might not be visible, e.g. white on white, see actor
    //   d.setTextColor(d.getDefaultTextColor(newColorScheme));
    // }

    d.applyThemeBorderColor();

    // checks if diagram is annotation or not and applies colors accordingly
    d.applyAnnotationColors();
  }

  /**
   * Normal call through applyThemeToDiagram doesn't work
   * when dynamically changing background color.
   * Waiting event after global theme has been changed.
   */
  private void applyThemeChanged() {
    for (Diagram d : surface.getDiagrams()) {
      d.applyThemeBorderColor();
    }
  }

}
