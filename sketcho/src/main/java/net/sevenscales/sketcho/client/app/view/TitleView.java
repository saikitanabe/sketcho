package net.sevenscales.sketcho.client.app.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class TitleView extends SimplePanel {
  interface MyUiBinder extends UiBinder<Widget, TitleView> {}
  private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
  
  @UiField Label title;

  public TitleView() {
    setWidget(uiBinder.createAndBindUi(this));
  }
  
  public void setProjectName(String name) {
    title.setText(name);
  }
}
