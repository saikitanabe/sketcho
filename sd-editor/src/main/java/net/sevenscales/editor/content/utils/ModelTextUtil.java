package net.sevenscales.editor.content.utils;

import net.sevenscales.editor.content.utils.MacroUtils.ContentParser;
import net.sevenscales.editor.content.utils.MacroUtils.MacroParamParser;
import net.sevenscales.editor.content.utils.MacroUtils.ValueParser;

public class ModelTextUtil {
  
  private String text;
  private String link;
  private String parsedText = "";

  public ModelTextUtil(String text) {
    this.text = text;
    parse();
  }

  private void parse() {
    ContentParser cp = new ContentParser(text);
    String content;
    while ( (content = cp.next()) != null ) {
      MacroParamParser cmpp = new MacroParamParser("[[wiki", content);
      if (cmpp.isMacro()) {
        String param = cmpp.next();
        link = ValueParser.parse(param).left;
        param = cmpp.next();
        String name = ValueParser.parse(param).left;
        parsedText += name;
      } else {
        parsedText += content;
      }
    }
  }

  public String parseText() {
    return parsedText;
  }
  
  public String parseLink() {
    return link;
  }

}
