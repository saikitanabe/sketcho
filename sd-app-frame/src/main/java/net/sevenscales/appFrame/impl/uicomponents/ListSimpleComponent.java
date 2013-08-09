package net.sevenscales.appFrame.impl.uicomponents;

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

public class ListSimpleComponent extends SimplePanel {
	private FlexTable table;
  private int row = 0;
  private int column = 0;
	
	public ListSimpleComponent() {
		// construct body
		table = new FlexTable();
		table.getRowFormatter().setStyleName(0, "page-ListHeader");
		setWidget(table);
	}

	public void setHeader(List<String> header) {
		// empty for selection column title
		table.setHTML(0, 0, "<center>*</center>");

		for (int i = 0; i < header.size(); ++i) {
			HTML html = new HTML(header.get(i));
			html.setStyleName("content-Area-Td");
			table.setWidget(0, i + 1, html);
		}
	}
	
	public void add(Widget widget) {
	  widget.setStyleName("content-Area-ListItem");
	  table.setWidget(row, column++, widget);
	}
	
	public void newRow() {
	  ++row;
	  column = 1;
	}
	
	public void clear() {
    int row = 0;
    int count = table.getRowCount();
    while (--count >= 0) {
      for (int column = 0; column < table.getCellCount(row); ++column) {
        table.clearCell(row, column);
      }
      table.removeRow(row);
    }
    table.clear();
	}
}
