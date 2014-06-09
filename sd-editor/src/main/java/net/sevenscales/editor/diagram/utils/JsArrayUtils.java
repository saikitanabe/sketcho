package net.sevenscales.editor.diagram.utils;

import java.util.List;

import com.google.gwt.core.client.JsArrayInteger;


public class JsArrayUtils {
  public static JsArrayInteger readOnlyJsArray(List<Integer> list) {
    JsArrayInteger dest = JsArrayInteger.createArray().cast();
    for (int i = 0; i < list.size(); ++i) {
      dest.push(list.get(i));
    }
    return dest;
  }

}