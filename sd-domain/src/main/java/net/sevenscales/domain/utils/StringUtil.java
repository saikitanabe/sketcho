package net.sevenscales.domain.utils;

import java.util.Map;
import java.util.Map.Entry;

public class StringUtil {
  
  public static String parse(String template, Map<String,String> params) {
    for (Entry<String, String> entry : params.entrySet()) {
      template = template.replaceFirst(entry.getKey(), entry.getValue());
    }
    return template;
  }
  
  public static String dropRight(String text, int n) {
  	return text.substring(0, text.length()-n);
  }
  
  public static String stringOrEmpty(String value) {
  	return value == null ? "" : value;
  }
  
  public static String trimSpaces(String value) {
    String result = "";
  	int skip = 0;
  	char prevch = '?';
  	char prevprevch = '?';
    for (int i = 0; i < value.length(); ++i) {
    	char ch = value.charAt(i);
    	if ('"' == ch) { // maybe quotation started
    	  // check that " is not escaped and part of the string \"
    	  // check also that \ is not escaped \\; if it is is then this is real json "
    	  if (prevch != '\\' || (prevch == '\\' && prevprevch == '\\') ) {
    	    skip = ++skip % 2;
    	  }
    	}
    	
    	if ( !(skip == 0 && ch == ' ') ) {
    		result += ch;
    	}
    	prevprevch = prevch;
    	prevch = ch;
    }
    return result;
  }

}
