package net.sevenscales.editor.uicomponents;

import java.util.HashMap;
import java.util.Map;

public class TextParser {
  private HashMap<String, String> textItems = new HashMap<String, String>();

  /**
   * Parses any text in format
   * 
   * <any text>=<any text>
   * @param text
   * @return
   */
  public Map<String, String> parse(String text) {
    textItems.clear();
    String[] lines = text.split("\n");
    for (String line : lines) {
      int index = line.indexOf("=");
      if (index >= 0) {
        String key = line.substring(0, index);
        String value = line.substring(index + 1);
        textItems.put(key, value);
      }
    }
    return textItems;
  }

}
