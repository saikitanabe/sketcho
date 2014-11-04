package net.sevenscales.editor.content.utils;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class TokenParser {
	public static class StringToken {
		public String text;
		public boolean fontWeight;
		
		public StringToken(String text, boolean fontWeight) {
			super();
			this.text = text;
			this.fontWeight = fontWeight;
		}
	}

	public native static JavaScriptObject parse2(String text)/*-{
		return $wnd.markedToken.parse(text);
	}-*/;

	public static class JsInlineToken extends JavaScriptObject {
		protected JsInlineToken() {}

		public final native String getText()/*-{
			return this.text;
		}-*/;
	  public final native String isBold() /*-{
	  	return this.bold;
	  }-*/;

	}

	public native static JsArray<JsInlineToken> parseInline(String text)/*-{
		return $wnd.markedToken.parseInline(text)
	}-*/;

	public native static String formatHtml(JavaScriptObject tokens)/*-{
		return $wnd.markedToken.parseHtml(tokens);
	}-*/;
	
	public static List<StringToken> parseEntities(String line) {
    // split by entities *<text>*, _<text>_
		String[] entities = line.split("\\s");
		
		List<StringToken> result = new ArrayList<StringToken>(entities.length);
		int boldFrom = -1;
		int boldTo = -1;
		int index = 0;
		for (String e : entities) {
			if (!"".equals(e)) {
				result.add(new StringToken(e, false));
				
				boolean startsWithToken = e.startsWith("*");
				if (startsWithToken) {
					boldFrom = index;
				}
				
				if (e.endsWith("*")) {
					boldTo = index;
					markBolded(result, boldFrom, boldTo);
					boldFrom = -1;
					boldTo = -1;
				}
				
				++index;
			}
		}
		
		return result;
	}
	
	private static void markBolded(List<StringToken> tokens, int from, int to) {
		if (from >= 0 && to >= 0) {
			for (int i = from; i <= to; ++i) {
				StringToken token = tokens.get(i);
				// remove first and last *
				if (i == from) {
					token.text = token.text.substring(1, token.text.length());
				}
				if (i == to) {
					token.text = token.text.substring(0, token.text.length() - 1);
				}
				token.fontWeight = true;
			}
		}
	}


	public static List<StringToken> parse(String content) {
    content += " "; // in case last line is just new line 
    String[] lineTokens = content.split("\\n");
    // split by lines
    // split by entities *<text>*, _<text>_ => \*[^\s].*\*\s
    // set state according to entities, (mita jos molemmat), *_<text line>_* _*<text line>*_
    // split by words
    
    List<StringToken> result = new ArrayList<StringToken>();
    for (String line : lineTokens) {
    	List<StringToken> entities = parseEntities(line);
    	// add also line break that has parsed away
    	result.addAll(entities);
    	result.add(new StringToken("\n", false));
    }
    // remove last extra line break \n
    result.remove(result.size() - 1);
    return result;
	}
}
