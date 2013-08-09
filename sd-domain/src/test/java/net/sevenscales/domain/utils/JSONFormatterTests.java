package net.sevenscales.domain.utils;

import junit.framework.TestCase;

public class JSONFormatterTests extends TestCase {
  public void testStringEscape() {
//    JSONFormatterString formatter = new JSONFormatterString("quote", "\"");
//    assertEquals("\"quote\":\"\\\"\"", formatter.toString());
    
    String v = "\"".replaceAll("\\\\", "\\\\\\\\")
                    .replaceAll("\\n", "\\\\n")
                    .replaceAll("\"", "\\\\\"");
    System.out.println(v);
    assertEquals("\\\"", v);
  }
}
