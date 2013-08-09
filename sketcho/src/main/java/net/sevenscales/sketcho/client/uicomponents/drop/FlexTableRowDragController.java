package net.sevenscales.sketcho.client.uicomponents.drop;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.google.gwt.user.client.ui.AbsolutePanel;

public final class FlexTableRowDragController extends PickupDragController {

  public FlexTableRowDragController(AbsolutePanel boundaryPanel,
      boolean allowDroppingOnBoundaryPanel) {
    super(boundaryPanel, false);
    setBehaviorDragProxy(true);
    setBehaviorMultipleSelection(false);
  }
  @Override
  protected void saveSelectedWidgetsLocationAndStyle() {
  }
  @Override
  protected void restoreSelectedWidgetsLocation() {
  }
  @Override
  protected void restoreSelectedWidgetsStyle() {
  }
  
  @Override
  public void previewDragStart() throws VetoDragException {
    System.out.println("previewDragStart");
  }
  
  @Override
  public int getBehaviorDragStartSensitivity() {
    return 10;
  }
  
}

