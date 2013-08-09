package net.sevenscales.plugin.api;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;

import net.sevenscales.appFrame.impl.ITilesEngine;
import net.sevenscales.plugin.constants.TileId;

public class UiNotifier {
  private static UiNotifier singleton;
  private ITilesEngine tilesEngine;
  private HTML html;
  
  private UiNotifier() {
    this.html = new HTML();
    html.addStyleName("plugin-api-UiNotifier");
  }
  
  public static UiNotifier instance() {
    if (singleton == null) {
      singleton = new UiNotifier();
    }
    return singleton;
  }
  
  public void setTilesEngine(ITilesEngine tilesEngine) {
    this.tilesEngine = tilesEngine;
  }
  
  public void showError(String html) {
    this.html.setHTML(html);
    tilesEngine.setTile(TileId.NOTIFY_AREA, this.html);
    Window.scrollTo(0, 0);
  }
  
  public void showInfo(String html) {
    this.html.setHTML(html);
    tilesEngine.setTile(TileId.NOTIFY_AREA, this.html);
    Window.scrollTo(0, 0);
  }

  public void clear() {
    this.html.setHTML("");
  }
  
}
