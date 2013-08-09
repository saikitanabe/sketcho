package net.sevenscales.webAdmin.client.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sevenscales.appFrame.impl.Action;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class UsersSelectionComponent extends SimplePanel {
	private FlexTable table;
	private Map checkboxMap = new HashMap();
	
	public UsersSelectionComponent() {
		// construct body
		table = new FlexTable();
//		table.addStyleName("page-Table");
		table.getRowFormatter().setStyleName(0, "page-ListHeader");
		setWidget(table);
	}

	public void setHeader(List header) {
		// empty for selection column title
		table.setHTML(0, 0, "<center>*</center>");

		for (int i = 0; i < header.size(); ++i) {
			HTML html = new HTML((String) header.get(i));
			html.setStyleName("content-Area-Td");
			table.setWidget(0, i + 1, html);
		}
	}
	
	public static class ListItemData {
	  public Action action;
	  public List<Widget> others;
	}

	public void setListItems(List<ListItemData> listItems) {
	  table.clear();
	  addListItems(listItems);
	}
	
	 public void addListItems(List<ListItemData> listItems) {
	    for (int i = 0; i < listItems.size(); ++i) {
	      CheckBox c = new CheckBox();
//	      table.setWidget(i + 1, 0, c);
	      
	      ListItemData lid = listItems.get(i);
	      
	      Action a = lid.action;
	      checkboxMap.put(c, a.getData());
	      a.setStyleName("content-Area-ListItem");
	      table.setWidget(i + 1, 1, a.getWidget());
	        
	      for (int x = 0; lid.others != null && x < lid.others.size(); ++x ) {
	        table.setWidget(i + 1, x + 2, lid.others.get(x));
	      }
	    }
	  }
	
	public ArrayList getSelectedItems() {
		ArrayList result = new ArrayList();
		Iterator i = checkboxMap.keySet().iterator();
		while (i.hasNext()) {
			CheckBox c = (CheckBox)i.next();
			if (c.isChecked()) {
				result.add(checkboxMap.get(c));
			}
		}
		return result;
	}

	public void clear() {
		for (int row = 0; row < table.getRowCount(); ++row) {
			for (int column = 0; column < table.getCellCount(row); ++column) {
				table.clearCell(row, column);
			}
		}
	}
}
