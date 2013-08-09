package net.sevenscales.appFrame.impl.uicomponents;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HTMLTable.Cell;

public class ListUiHelper<D> extends SimplePanel {
	private Table table;
	private Map checkboxMap = new HashMap();
  private int row;
  private Map<D,Integer> rowMap;
  private LabelDropHandler labelDropHandler;
  
  public static class Table extends FlexTable {
    public class TableCell extends Cell {

      public TableCell(int rowIndex, int cellIndex) {
        super(rowIndex, cellIndex);
      }
      
    }
    public Table() {
      super();
    }
    protected Element getEventTargetCell(Event event) {
      return super.getEventTargetCell(event);
    }
    public Cell createCell(int row, int column) {
      return new TableCell(row, column);
    }
  }
  
  public interface LabelDropHandler {
    <D> void onLabelDrop(D domainObject, Object label);
  }
  
	public ListUiHelper() {
    rowMap = new HashMap<D,Integer>();
    table = new Table();
    configTable(table);
    setWidget(table);
    setStyleName("sd-app-frame-ListUiHelper");
	}
	
	public Table getTable() {
	  return table;
	}

	private void configTable(FlexTable table) {
    table.setCellSpacing(0);
    table.setCellPadding(0);
    table.setWidth("100%");
  }

  public void setHeader(List<String> header) {
		// empty for selection column title
//		table.setHTML(0, 0, "<center>*</center>");

		for (int i = 0; i < header.size(); ++i) {
			HTML html = new HTML((String) header.get(i));
      table.getCellFormatter().setWordWrap(0, i, false);
			table.setWidget(0, i, html);
		}
    table.getRowFormatter().addStyleName(0, "sd-app-frame-ListUiHelper-Header");
	}
	
  public void addRow(List<Widget> columns, D domainObject) {
    addRow(columns, domainObject, "");
  }
  
	 public void addRow(List<Widget> columns, D domainObject, String style) {
     ++this.row;
     rowMap.put(domainObject, this.row);
     table.getRowFormatter().addStyleName(row, "sd-app-frame-ListUiHelper-RowFormatter");
     
     if (style.length() > 0) {
       table.getRowFormatter().addStyleName(row, style);
     }

     int column = 0;
     for (Widget w : columns) {
       table.setWidget(this.row, column, w);
       table.getCellFormatter().setWordWrap(row, column, false);
       ++column;
     }
	 }
	 
	 public void replaceRow(List<Widget> columns, D domainObject) {
	   int selectedRow = rowMap.get(domainObject);
	   int column = 0;
//     table.getRowFormatter().setStyleName(selectedRow, "sd-app-frame-ListUiHelper-RowFormatter");

	   for (Widget w : columns) {
       table.clearCell(selectedRow, column);
       table.getCellFormatter().setWordWrap(selectedRow, column, false);
//       table.getFlexCellFormatter().setWidth(row, column, "100px");
       table.setWidget(selectedRow, column, w);
       ++column;
	   }
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
//    table.getRowFormatter().addStyleName(0, "sd-app-frame-ListUiHelper-Header");
		configTable(table);
//	  table = new Table();
//    configTable(table);
//    setWidget(table);
    this.row = 0;
		rowMap.clear();
	}

	public void addClickHandler(ClickHandler handler) {
	  table.addClickHandler(handler);
	}

  public D getDomainObject(ClickEvent event) {
    Cell cell = table.getCellForEvent(event);
    if (cell == null) {
      return null;
    }
    
    for (Entry<D, Integer> e : rowMap.entrySet()) {
      if (e.getValue().equals(cell.getRowIndex())) {
        return (D) e.getKey();
      }
    }
    return null;
  }
  
  public Cell getCellForEvent(Event event) {
    Element td = table.getEventTargetCell(event);
    if (td == null) {
      return null;
    }

    Element tr = DOM.getParent(td);
    Element body = DOM.getParent(tr);
    int row = DOM.getChildIndex(body, tr);
    int column = DOM.getChildIndex(tr, td);

    return table.createCell(row, column);
  }

  public void onLabelDrop(int targetRow, Object label) {
    D domainObject = null;
    for (Entry<D, Integer> e : rowMap.entrySet()) {
      if (e.getValue().equals(targetRow)) {
        domainObject = (D) e.getKey();
      }
    }

    labelDropHandler.onLabelDrop(domainObject, label);
  }
  
  public void setLabelDropHandler(LabelDropHandler labelDropHandler) {
    this.labelDropHandler = labelDropHandler;
  }

}
