package net.sevenscales.sketcho.client.uicomponents;

import com.google.gwt.user.client.ui.ListBox;

import java.util.HashMap;
import java.util.Map;

public class ListBoxMap<T> extends ListBox {
  private Map<Integer, T> mappings = new HashMap<Integer, T>();

  public void clear() {
    mappings.clear();
    super.clear();
  }

  public void addItem(String value, T data, boolean focus) {
    int index = getItemCount();
    addItem(value);
    setItemSelected(index, focus);
    mappings.put(index, data);
  }

  public T getData(Integer index) {
    return (T) mappings.get(index);
  }
}
