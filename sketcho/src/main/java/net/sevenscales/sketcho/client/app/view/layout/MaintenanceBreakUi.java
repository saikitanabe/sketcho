package net.sevenscales.sketcho.client.app.view.layout;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class MaintenanceBreakUi extends Composite {

  private static MaintenanceBreakUiUiBinder uiBinder = GWT.create(MaintenanceBreakUiUiBinder.class);

  interface MaintenanceBreakUiUiBinder extends
      UiBinder<Widget, MaintenanceBreakUi> {
  }

  public MaintenanceBreakUi() {
    initWidget(uiBinder.createAndBindUi(this));
  }

}
