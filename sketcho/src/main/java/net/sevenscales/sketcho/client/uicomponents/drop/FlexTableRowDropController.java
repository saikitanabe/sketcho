package net.sevenscales.sketcho.client.uicomponents.drop;

import net.sevenscales.appFrame.impl.Action;
import net.sevenscales.appFrame.impl.uicomponents.ListUiHelper;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.AbstractPositioningDropController;
import com.allen_sauer.gwt.dnd.client.util.CoordinateLocation;
import com.allen_sauer.gwt.dnd.client.util.DOMUtil;
import com.allen_sauer.gwt.dnd.client.util.LocationWidgetComparator;
import com.google.gwt.user.client.ui.IndexedPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public final class FlexTableRowDropController extends AbstractPositioningDropController {
  private int targetRow = -1;
  private ListUiHelper dropTable;
  private IndexedPanel indexedPanel = new IndexedPanel() {
    public Widget getWidget(int index) {
      return dropTable.getTable().getWidget(index, 0);
    }

    public int getWidgetCount() {
      return dropTable.getTable().getRowCount();
    }

    public int getWidgetIndex(Widget child) {
      throw new UnsupportedOperationException();
    }

    public boolean remove(int index) {
      throw new UnsupportedOperationException();
    }
  };
  public FlexTableRowDropController(Panel dropPanel) {
    super(dropPanel);
    this.dropTable = (ListUiHelper) dropPanel;
  }
  @Override
  public void onEnter(DragContext context) {
  }
  @Override
  public void onMove(DragContext context) {
    int prevTargetRow = targetRow;
    targetRow = DOMUtil.findIntersect(indexedPanel, new CoordinateLocation(
        context.mouseX, context.mouseY), LocationWidgetComparator.BOTTOM_HALF_COMPARATOR) - 1;

    if (prevTargetRow >= 1 && prevTargetRow != targetRow) {
      dropTable.getTable().getRowFormatter().removeStyleName(prevTargetRow, "LabelView-HighlightRow");
    }

    if (targetRow >= 1 && dropTable.getTable().getRowCount() > 0) {
      dropTable.getTable().getRowFormatter().addStyleName(targetRow, "LabelView-HighlightRow");
    }
//    super.onMove(context);
  }
  @Override
  public void onLeave(DragContext context) {
    if (targetRow >= 1) {
      dropTable.getTable().getRowFormatter().removeStyleName(targetRow, "LabelView-HighlightRow");
    }
    targetRow = -1;
    super.onLeave(context);
  }
  @Override
  public void onDrop(DragContext context) {
    targetRow = DOMUtil.findIntersect(indexedPanel, new CoordinateLocation(
        context.mouseX, context.mouseY), LocationWidgetComparator.BOTTOM_HALF_COMPARATOR) - 1;
    Action from = (Action) context.selectedWidgets.get(0);
    dropTable.onLabelDrop(targetRow, from.getData());
    super.onDrop(context);
  }
}
