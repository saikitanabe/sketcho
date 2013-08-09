package net.sevenscales.editor.content.utils;


public class MacroUtils {

  public static class ContentParser {
    private int nextStart;
    private String buf;
    private MacroParser mp;
    private String macro;

    public ContentParser(String buf) {
      this.buf = buf;
      this.mp = new MacroParser(buf);
    }
    
    private int findEnd(String buf, int start) {
      if (buf == null) {
        return -1;
      }
//      int end = 0;
      int index = start;
      int state = 0;
      boolean accepted = false;
      int macroBegin = 0;
      while (!accepted && index < buf.length()) {
        char ch = buf.charAt(index); 
        switch (state) {
          case 0:
            // begin state
            if (ch == '[') {
//              end = index;
              state = 1;
            }
            break;
          case 1:
            // state 1
            macroBegin = index - 1;
            if (ch == '[') {
              state = 2;
              // [[ => state 2
            } else {
              // => begin
              state = 0;
            }
            break;
          case 2:
            // state 2
            if (ch == ']') {
              state = 3;
            }
            if (ch == '[') {
              // => state 1
//              end = index;
              state = 1;
            }
            break;
          case 3:
            // state 3
            if (ch == ']') {
              // accepted
//              end = index;
              accepted = true;
            }
            break;
        }
        ++index;
      }
      
      int result = accepted ? macroBegin : index; 
      if (accepted && macroBegin == start) {        
        result = index;
      }
      return result;
    }
    
    public String next() {
      int begin = nextStart;
      int end = findEnd(buf, nextStart);
      nextStart = end;
      if (begin == end || end == -1) {
        return null;
      }
      return buf.substring(begin, end);
    }
  }
  
  public static class MacroParser {
    private int nextStart;
    private String buf;
    
    public MacroParser(String buf) {
      this.buf = buf;
    }
    public String next() {
      int start = buf.indexOf("[[", nextStart);
      int end = buf.indexOf("]]", nextStart);
      nextStart = end + 2;
      if (start == -1 || end == -1) {
        return null;
      }

      if (start >= end) {
        // skip until next correct is found
        return next();
      }
      return buf.substring(start + 2, end);
    }
  }
  
  public static class MacroParamParser {
    private String buf;
    private String macroName;
    private int pos;
    private String[] params;

    public MacroParamParser(String macroName, String buf) {
      this.macroName = macroName;
      this.buf = buf;
    }
    
    public boolean isMacro() {
      int start = buf.indexOf("(");
      int end = buf.indexOf(")");
      if (start == -1 || end == -1) {
        return false;
      }
      params = buf.substring(start + 1, end).split(",");
      return buf.substring(0, start).equals(macroName);
    }
    
    public String next() {
      if (pos < params.length)
        return params[pos++];
      return null;
    }
  }

  public static class Pair {
    private Pair(String left, String right) {
      this.left = left;
      this.right = right;
    }
    public String left;
    public String right;
  }

  public static class ValueParser {    
    public static Pair parse(String argument) {
      String[] pair = argument.split(":");
      if (pair.length == 1) {
        return new Pair(pair[0], null);
      }
      return new Pair(pair[0], pair[1]);
    }
  }
  
  public static class WhereValueParser {
    public static class Result {
      public Pair pair;
      public String operator;
    }
    final static String greater = "&gt;";
    final static String less = "&lt;";
      
    static public Result parse(String value) {
      // when content is edited with rich text editor
      // html entities have bean replaced so replace back to chars
      value = value.replaceFirst(greater, ">");
      value = value.replaceFirst(less, "<");
      Result result = new Result();
      result.operator = "";
      if (value.indexOf('>') > 0) {
        result.operator = ">";
      } else if (value.indexOf('<') > 0) {
        result.operator = "<";
      }

      String[] pair = value.split(result.operator);
      
      if (pair[1].indexOf("%NOW%") != -1) {
        Long timeMinutes = System.currentTimeMillis() / 1000 / 60;
        pair[0] = pair[0] + "/ 1000 / 60";
        pair[1] = pair[1].replaceAll("%NOW%", Long.toString(timeMinutes));
      }
      result.pair = new Pair(pair[0], pair[1]); 
      return result;
    }    
  }

}
